package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IMarketingCouponService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.marketing.MarketingCouponCreateValidate;
import com.mdd.admin.validate.marketing.MarketingCouponRecordValidate;
import com.mdd.admin.validate.marketing.MarketingCouponSearchValidate;
import com.mdd.admin.validate.marketing.MarketingCouponUpdateValidate;
import com.mdd.admin.vo.marketing.MarketingCouponDetailVo;
import com.mdd.admin.vo.marketing.MarketingCouponListedVo;
import com.mdd.admin.vo.marketing.MarketingCouponRecordVo;
import com.mdd.admin.vo.marketing.MarketingCouponSelectVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.coupon.CouponList;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.system.SystemAuthAdmin;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.coupon.CouponListMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.system.SystemAuthAdminMapper;
import com.mdd.common.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 营销优惠券服务实现类
 */
@Service
public class MarketingCouponServiceImpl implements IMarketingCouponService {

    @Resource
    CouponMapper couponMapper;

    @Resource
    CouponListMapper couponListMapper;

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    SystemAuthAdminMapper systemAuthAdminMapper;

    @Override
    public PageResult<MarketingCouponSelectVo> select(PageValidate pageValidate, MarketingCouponSearchValidate searchValidate) {
        Integer pageNo   = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        // 条件构造
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("get_type", CouponEnum.GET_TYPE_STORE.getCode())
                .eq("status", CouponEnum.COUPON_STATUS_CONDUCT.getCode())
                .eq("is_delete", 0)
                .orderByDesc("id");

        // 基础搜索
        couponMapper.setSearch(queryWrapper, searchValidate, new String[]{
                "like:keyword@name:str"
        });
        System.out.println(searchValidate);
        // 发起查询
        IPage<Coupon> iPage = couponMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        // 处理数据
        List<MarketingCouponSelectVo> list = new LinkedList<>();
        for (Coupon coupon: iPage.getRecords()) {
            MarketingCouponSelectVo vo = new MarketingCouponSelectVo();
            BeanUtils.copyProperties(coupon, vo);
            vo.setSendTypeMsg("系统发放");

            if (coupon.getConditionType().equals(CouponEnum.CONDITION_TYPE_NOT.getCode())) {
                vo.setDiscountContent("无门槛, " + "减" + coupon.getMoney() + "元");
            } else {
                vo.setDiscountContent("订单满" + coupon.getConditionMoney() + "元, " + "减" + coupon.getMoney() + "元");
            }
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 优惠券列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingCouponListedVo>
     */
    @Override
    public PageResult<MarketingCouponListedVo> list(PageValidate pageValidate, MarketingCouponSearchValidate searchValidate) {
        Integer pageNo   = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        // 条件构造
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("is_delete", 0)
                .orderByDesc("id");

        // 基础搜索
        couponMapper.setSearch(queryWrapper, searchValidate, new String[]{
                "=:getType@get_type:long",
                "=:useGoodsType@use_goods_type:long",
        });

        // 关键词搜
        if (StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            queryWrapper.nested(wq->wq
                    .like("name", searchValidate.getKeyword())
                    .or()
                    .like("sn", searchValidate.getKeyword())
            );
        }

        // 数据统计
        QueryWrapper<Coupon> allQuery = queryWrapper.clone();
        QueryWrapper<Coupon> notQuery = queryWrapper.clone();
        QueryWrapper<Coupon> endQuery = queryWrapper.clone();
        QueryWrapper<Coupon> conductQuery = queryWrapper.clone();
        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("all", couponMapper.selectCount(allQuery));
        extend.put("not", couponMapper.selectCount(notQuery.eq("status", CouponEnum.COUPON_STATUS_NOT.getCode())));
        extend.put("end", couponMapper.selectCount(endQuery.eq("status", CouponEnum.COUPON_STATUS_END.getCode())));
        extend.put("conduct", couponMapper.selectCount(conductQuery.eq("status", CouponEnum.COUPON_STATUS_CONDUCT.getCode())));

        if (StringUtils.isNotNull(searchValidate.getType())) {
            queryWrapper.eq("status", searchValidate.getType());
        }

        // 发起查询
        IPage<Coupon> iPage = couponMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        // 处理数据
        List<MarketingCouponListedVo> list = new LinkedList<>();
        for (Coupon coupon: iPage.getRecords()) {
            MarketingCouponListedVo vo = new MarketingCouponListedVo();
            BeanUtils.copyProperties(coupon, vo);

            String getStartTime = TimeUtils.timestampToDate(coupon.getGetTimeStart());
            String getEntTime = TimeUtils.timestampToDate(coupon.getGetTimeEnd());
            vo.setGetTimeMsg(getStartTime + " ~ " + getEntTime);

            if (coupon.getConditionType().equals(CouponEnum.CONDITION_TYPE_NOT.getCode())) {
                vo.setDiscountContent("无门槛, " + "减" + coupon.getMoney() + "元");
            } else {
                vo.setDiscountContent("订单满" + coupon.getConditionMoney() + "元, " + "减" + coupon.getMoney() + "元");
            }

            switch (coupon.getUseTimeType()) {
                case 1: // 固定时间
                    String useTimeStart = TimeUtils.timestampToDate(coupon.getUseTimeStart());
                    String useTimeEnd = TimeUtils.timestampToDate(coupon.getUseTimeEnd());
                    vo.setUseTimeMsg(useTimeStart + " ~ " + useTimeEnd);
                    break;
                case 2: // 当日起多少天内
                    vo.setUseTimeMsg("领取当日起" + coupon.getUseTime() + "天内可用");
                    break;
                case 3: // 次日起多少天内
                    vo.setUseTimeMsg("领取次日起" + coupon.getUseTime() + "天内可用");
                    break;
            }

            Long receiveNumber = couponListMapper.selectCount(new QueryWrapper<CouponList>().eq("coupon_id", coupon.getId()));
            Long useNumber = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                    .eq("coupon_id", coupon.getId())
                    .eq("status", CouponEnum.USE_STATUS_OK.getCode()));

            double usageRate = 0;
            if (receiveNumber > 0) {
                usageRate = ArithUtils.div(useNumber.doubleValue() / receiveNumber.doubleValue(), 1);
                usageRate = ArithUtils.mul(usageRate, 100);
                usageRate = ArithUtils.round(usageRate, 2);
            }

            vo.setGetTypeMsg(CouponEnum.getGetTypeMsg(coupon.getGetType()));
            vo.setStatusMsg(CouponEnum.getCouponStatusMsg(coupon.getStatus()));
            vo.setUseNumber(useNumber);
            vo.setReceiveNumber(receiveNumber);
            vo.setUsageRate(usageRate + "%");
            vo.setCreateTime(TimeUtils.timestampToDate(coupon.getCreateTime()));

            vo.setSendTotalMsg(coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_NOT.getCode())
                    ? "不限量" : coupon.getSendTotal().toString());

            vo.setSurplusNumber(coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_NOT.getCode())
                    ? "不限制" : coupon.getSendTotal() - vo.getReceiveNumber() + "");

            list.add(vo);
        }


        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list, extend);
    }

    @Override
    public MarketingCouponListedVo info(Integer id) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券不存在了!");

        MarketingCouponListedVo vo = new MarketingCouponListedVo();
        BeanUtils.copyProperties(coupon, vo);

        if (coupon.getGetNumType().equals(CouponEnum.GET_NUM_TYPE_NOT.getCode())) {
            vo.setGetNumTypeMsg("不限");
        } else if (coupon.getGetNumType().equals(CouponEnum.GET_NUM_TYPE_LIMIT.getCode())) {
            vo.setGetNumTypeMsg("限制"+coupon.getGetNum()+"张");
        } else if (coupon.getGetNumType().equals(CouponEnum.GET_NUM_TYPE_DAY.getCode())) {
            vo.setGetTimeMsg("每天限制"+coupon.getGetNum()+"张");
        }

        if (coupon.getUseGoodsType().equals(CouponEnum.USE_GOODS_TYPE_NOT.getCode())) {
            vo.setUseGoodsTypeMsg("全场通用");
        } else if (coupon.getUseGoodsType().equals(CouponEnum.USE_GOODS_TYPE_ALLOW.getCode())) {
            vo.setUseGoodsTypeMsg("部分商品可用");
        } else if (coupon.getUseGoodsType().equals(CouponEnum.USE_GOODS_TYPE_BAN.getCode())) {
            vo.setUseGoodsTypeMsg("部分商品不可用");
        }

        if (coupon.getConditionType().equals(CouponEnum.CONDITION_TYPE_NOT.getCode())) {
            vo.setDiscountContent("无门槛, " + "减" + coupon.getMoney() + "元");
        } else {
            vo.setDiscountContent("订单满" + coupon.getConditionMoney() + "元, " + "减" + coupon.getMoney() + "元");
        }

        String getStartTime = TimeUtils.timestampToDate(coupon.getGetTimeStart());
        String getEntTime = TimeUtils.timestampToDate(coupon.getGetTimeEnd());
        vo.setGetTimeMsg(getStartTime + " ~ " + getEntTime);

        switch (coupon.getUseTimeType()) {
            case 3: // 次日起多少天内
                vo.setUseTimeMsg("领取次日起" + coupon.getUseTime() + "天内可用");
                break;
            case 2: // 当日起多少天内
                vo.setUseTimeMsg("领取当日起" + coupon.getUseTime() + "天内可用");
                break;
            case 1: // 固定时间
                String useTimeEnd = TimeUtils.timestampToDate(coupon.getUseTimeEnd());
                String useTimeStart = TimeUtils.timestampToDate(coupon.getUseTimeStart());
                vo.setUseTimeMsg(useTimeStart + " ~ " + useTimeEnd);
                break;
        }

        Long receiveNumber = couponListMapper.selectCount(new QueryWrapper<CouponList>().eq("coupon_id", id));
        Long useNumber = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                .eq("coupon_id", id)
                .eq("status", CouponEnum.USE_STATUS_OK.getCode()));

        double usageRate = 0;
        if (receiveNumber > 0) {
            usageRate = ArithUtils.div(useNumber.doubleValue() / receiveNumber.doubleValue(), 1);
            usageRate = ArithUtils.round(ArithUtils.mul(usageRate, 100), 2);
        }

        vo.setGetTypeMsg(CouponEnum.getGetTypeMsg(coupon.getGetType()));
        vo.setStatusMsg(CouponEnum.getUseCouponMsg(coupon.getStatus()));
        vo.setUsageRate(usageRate + "%");
        vo.setReceiveNumber(receiveNumber);
        vo.setUseNumber(useNumber);
        vo.setCreateTime(TimeUtils.timestampToDate(coupon.getCreateTime()));

        vo.setSurplusNumber(coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_NOT.getCode())
                ? "不限制" : coupon.getSendTotal() - vo.getReceiveNumber() + "");

        vo.setSendTotalMsg(coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_NOT.getCode())
                ? "不限量" : coupon.getSendTotal().toString());

        return vo;
    }

    /**
     * 优惠券详情
     *
     * @author fzr
     * @param id 主键
     * @return Object
     */
    @Override
    public MarketingCouponDetailVo detail(Integer id) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券不存在了!");

        List<MarketingCouponDetailVo.CouponGoodsItemVo> useGoodsList = new LinkedList<>();
        if (!coupon.getUseGoodsType().equals(CouponEnum.USE_GOODS_TYPE_NOT.getCode()) && StringUtils.isNotEmpty(coupon.getUseGoodsIds())) {
            List<Integer> ids = ListUtils.stringToListAsInt(coupon.getUseGoodsIds(), ",");
            List<Goods> goodsList = goodsMapper.selectList(
                    new QueryWrapper<Goods>()
                            .select("id,code,name,image,min_price,max_price")
                            .in("id", ids)
                            .eq("is_delete", 0));

            for (Goods goods : goodsList) {
                MarketingCouponDetailVo.CouponGoodsItemVo gItemVo = new MarketingCouponDetailVo.CouponGoodsItemVo();
                BeanUtils.copyProperties(goods, gItemVo);
                gItemVo.setImage(UrlUtils.toAbsoluteUrl(goods.getImage()));
                useGoodsList.add(gItemVo);
            }
        }

        MarketingCouponDetailVo vo = new MarketingCouponDetailVo();
        BeanUtils.copyProperties(coupon, vo);
        vo.setUseTimeStart(StringUtils.isNotNull(coupon.getUseTimeStart()) ? TimeUtils.timestampToDate(coupon.getUseTimeStart()) : "");
        vo.setUseTimeEnd(StringUtils.isNotNull(coupon.getUseTimeEnd()) ? TimeUtils.timestampToDate(coupon.getUseTimeEnd()) : "");
        vo.setGetTimeStart(StringUtils.isNotNull(coupon.getGetTimeStart()) ? TimeUtils.timestampToDate(coupon.getGetTimeStart()) : "");
        vo.setGetTimeEnd(StringUtils.isNotNull(coupon.getGetTimeEnd()) ? TimeUtils.timestampToDate(coupon.getGetTimeEnd()) : "");
        vo.setUseGoodsList(useGoodsList);
        return vo;
    }

    /**
     * 优惠券新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    @Override
    public void add(MarketingCouponCreateValidate createValidate) {
        // 验证名称重复
        Coupon couponName = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("name", createValidate.getName())
                        .eq("is_delete", 0)
                        .last("limit 1"));

        if (StringUtils.isNotNull(couponName)) {
            throw new OperateException("优惠券名称已被占用!");
        }

        // 领取时间范围
        Long getTimeStart = TimeUtils.dateToTimestamp(createValidate.getGetTimeStart());
        Long getTimeEnd = TimeUtils.dateToTimestamp(createValidate.getGetTimeEnd());
//        if (getTimeEnd <= getTimeStart) {
//            throw new OperateException("领取时间的结束时间不能小于开始时间");
//        }

        // 用券时间范围
        Long useTimeStart = 0L;
        Long useTimeEnd = 0L;
        if (createValidate.getUseTimeType().equals(CouponEnum.USE_TIME_TYPE_FIXED.getCode())) {
            Assert.notNull(createValidate.getUseTimeStart(), "useTimeStart参数缺失");
            Assert.notNull(createValidate.getUseTimeEnd(), "useTimeEnd参数缺失");
            try {
                useTimeStart = TimeUtils.dateToTimestamp(createValidate.getUseTimeStart());
                useTimeEnd = TimeUtils.dateToTimestamp(createValidate.getUseTimeEnd());
            } catch (Exception e) {
                throw new OperateException("使用时间范围格式有误");
            }

            if (useTimeEnd <= useTimeStart) {
                throw new OperateException("用券时间的结束时间不能小于开始时间");
            }

            if (useTimeStart < getTimeStart) {
                throw new OperateException("用券开始时间不能少于领取的开始时间");
            }
        }

        // 验证发放限制
        if (createValidate.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_FIXED.getCode())
            && createValidate.getSendTotal() <= 0) {
            throw new OperateException("发放数量不能少于0");
        }

        Coupon coupon = new Coupon();
        coupon.setSn(couponMapper.randMakeOrderSn("sn"));
        coupon.setName(createValidate.getName());
        coupon.setMoney(createValidate.getMoney());
        coupon.setSendTotalType(createValidate.getSendTotalType());
        coupon.setSendTotal(createValidate.getSendTotal());
        coupon.setConditionType(createValidate.getConditionType());
        coupon.setConditionMoney(createValidate.getConditionMoney());
        coupon.setUseTimeType(createValidate.getUseTimeType());
        coupon.setUseTimeStart(useTimeStart);
        coupon.setUseTimeEnd(useTimeEnd);
        coupon.setUseTime(createValidate.getUseTime());
        coupon.setGetType(createValidate.getGetType());
        coupon.setGetNumType(createValidate.getGetNumType());
        coupon.setGetNum(createValidate.getGetNum());
        coupon.setGetTimeStart(getTimeStart);
        coupon.setGetTimeEnd(getTimeEnd);
        coupon.setUseGoodsType(createValidate.getUseGoodsType());
        coupon.setUseGoodsIds(createValidate.getUseGoodsIds());
        coupon.setCreateTime(System.currentTimeMillis() / 1000);
        coupon.setUpdateTime(System.currentTimeMillis() / 1000);
        couponMapper.insert(coupon);
    }

    /**
     * 优惠券编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    @Override
    public void edit(MarketingCouponUpdateValidate updateValidate) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", updateValidate.getId())
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券不存在了!");
        if (!coupon.getStatus().equals(CouponEnum.COUPON_STATUS_NOT.getCode())) {
            throw new OperateException("不是未开始状态,不再允许编辑信息!");
        }

        // 验证名称重复
        Coupon couponName = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("name", updateValidate.getName())
                        .ne("id", updateValidate.getId())
                        .eq("is_delete", 0)
                        .last("limit 1"));

        if (StringUtils.isNotNull(couponName)) {
            throw new OperateException("优惠券名称已被占用!");
        }

        // 领取时间范围
        Long getTimeStart = TimeUtils.dateToTimestamp(updateValidate.getGetTimeStart());
        Long getTimeEnd = TimeUtils.dateToTimestamp(updateValidate.getGetTimeEnd());
//        if (getTimeEnd <= getTimeStart) {
//            throw new OperateException("领取时间的结束时间不能小开始时间!");
//        }

        // 用券时间范围
        Long useTimeStart = 0L;
        Long useTimeEnd = 0L;
        if (updateValidate.getUseTimeType().equals(CouponEnum.USE_TIME_TYPE_FIXED.getCode())) {
            Assert.notNull(updateValidate.getUseTimeStart(), "useTimeStart参数缺失!");
            Assert.notNull(updateValidate.getUseTimeEnd(), "useTimeEnd参数缺失!");
            try {
                useTimeStart = TimeUtils.dateToTimestamp(updateValidate.getUseTimeStart());
                useTimeEnd = TimeUtils.dateToTimestamp(updateValidate.getUseTimeEnd());
            } catch (Exception e) {
                throw new OperateException("使用时间范围格式有误!");
            }

            if (useTimeEnd <= useTimeStart) {
                throw new OperateException("用券时间的结束时间不能小于开始时间!");
            }

            if (useTimeStart < getTimeStart) {
                throw new OperateException("用券开始时间不能少于领取的开始时间");
            }
        }

        // 验证发放限制
        if (updateValidate.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_FIXED.getCode())
            && updateValidate.getSendTotal() <= 0) {
            throw new OperateException("发放数量不允许少于0");
        }

        coupon.setName(updateValidate.getName());
        coupon.setMoney(updateValidate.getMoney());
        coupon.setConditionType(updateValidate.getConditionType());
        coupon.setConditionMoney(updateValidate.getConditionMoney());
        coupon.setSendTotalType(updateValidate.getSendTotalType());
        coupon.setSendTotal(updateValidate.getSendTotal());
        coupon.setGetType(updateValidate.getGetType());
        coupon.setGetNumType(updateValidate.getGetNumType());
        coupon.setGetNum(updateValidate.getGetNum());
        coupon.setGetTimeStart(getTimeStart);
        coupon.setGetTimeEnd(getTimeEnd);
        coupon.setUseTimeType(updateValidate.getUseTimeType());
        coupon.setUseTimeStart(useTimeStart);
        coupon.setUseTimeEnd(useTimeEnd);
        coupon.setUseTime(updateValidate.getUseTime());
        coupon.setUseGoodsType(updateValidate.getUseGoodsType());
        coupon.setUseGoodsIds(updateValidate.getUseGoodsIds());
        coupon.setUpdateTime(System.currentTimeMillis() / 1000);
        couponMapper.updateById(coupon);
    }

    /**
     * 优惠券删除
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(Integer id) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券已不存在!");

        if (coupon.getStatus().equals(CouponEnum.COUPON_STATUS_CONDUCT.getCode())) {
            throw new OperateException("优惠券正在进行中,不允许删除!");
        }

        CouponList couponList = new CouponList();
        couponList.setIsDelete(1);
        couponList.setDeleteTime(System.currentTimeMillis() / 1000);
        couponListMapper.update(couponList, new QueryWrapper<CouponList>().eq("coupon_id", id));

        coupon.setIsDelete(1);
        coupon.setStatus(CouponEnum.COUPON_STATUS_END.getCode());
        coupon.setDeleteTime(System.currentTimeMillis() / 1000);
        couponMapper.updateById(coupon);
    }

    /**
     * 优惠券命名
     *
     * @author fzr
     * @param id 主键
     * @param name 命名
     * @param sendTotal 数量
     */
    @Override
    public void rename(Integer id, String name, Integer sendTotal) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券已不存在!");

        // 验证状态
        if (!coupon.getStatus().equals(CouponEnum.COUPON_STATUS_NOT.getCode())
            && !coupon.getStatus().equals(CouponEnum.COUPON_STATUS_CONDUCT.getCode())) {
            throw new OperateException("不是“未开始/进行中”状态,操作失败");
        }

        // 数量调整
        if (coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_FIXED.getCode()) && StringUtils.isNotNull(sendTotal)) {
                boolean isT = sendTotal >= coupon.getSendTotal();
                if (coupon.getStatus().equals(CouponEnum.COUPON_STATUS_CONDUCT.getCode()) && !isT) {
                    throw new OperateException("已开始优惠券数量只允许增加,不允许减少");
                }
                coupon.setSendTotal(sendTotal);
        }

        // 验证名称重复
        Coupon couponName = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("name", name)
                        .ne("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        if (StringUtils.isNotNull(couponName)) {
            throw new OperateException("优惠券名称已被占用!");
        }

        coupon.setName(name);
        coupon.setUpdateTime(System.currentTimeMillis() / 1000);
        couponMapper.updateById(coupon);
    }

//    /**
//     * 优惠券发布
//     *
//     * @author fzr
//     * @param id 主键
//     */
//    @Override
//    public void release(Integer id) {
//        Coupon coupon = couponMapper.selectOne(
//                new QueryWrapper<Coupon>()
//                        .eq("id", id)
//                        .eq("is_delete", 0)
//                        .last("limit 1"));
//
//        Assert.notNull(coupon, "优惠券已不存在!");
//
//        if (!coupon.getStatus().equals(CouponEnum.COUPON_STATUS_CONDUCT.getCode())) {
//            throw new OperateException("优惠券不是未开始状态,操作失败!");
//        }
//
//        coupon.setStatus(CouponEnum.COUPON_STATUS_NOT.getCode());
//        coupon.setUpdateTime(System.currentTimeMillis() / 1000);
//        couponMapper.updateById(coupon);
//    }

    /**
     * 优惠券开始
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void start(Integer id) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券已不存在!");

        if (!coupon.getStatus().equals(CouponEnum.COUPON_STATUS_NOT.getCode())) {
            throw new OperateException("优惠券不是待开始状态,操作失败!");
        }

        long currTime = System.currentTimeMillis() / 1000;
        if (coupon.getGetTimeStart() > currTime) {
            throw new OperateException("未到领取开始时间,开始失败!");
        }

        coupon.setStatus(CouponEnum.COUPON_STATUS_CONDUCT.getCode());
        coupon.setUpdateTime(System.currentTimeMillis() / 1000);
        couponMapper.updateById(coupon);
    }

    /**
     * 优惠券结束
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void end(Integer id) {
        Coupon coupon = couponMapper.selectOne(
                new QueryWrapper<Coupon>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(coupon, "优惠券已不存在!");

        if (coupon.getStatus().equals(CouponEnum.COUPON_STATUS_END.getCode())) {
            throw new OperateException("此优惠券已结束,请勿重复操作!");
        }

        coupon.setStatus(CouponEnum.COUPON_STATUS_END.getCode());
        coupon.setUpdateTime(System.currentTimeMillis() / 1000);
        couponMapper.updateById(coupon);
    }

    /**
     * 优惠券记录
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingCouponRecordVo>
     */
    @Override
    public PageResult<MarketingCouponRecordVo> record(PageValidate pageValidate, MarketingCouponRecordValidate searchValidate) {
        Integer pageNo   = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        // 条件构造
        MPJQueryWrapper<CouponList> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper
                .orderByDesc("t.id")
                .eq("t.is_delete", 0)
                .leftJoin("?_coupon C ON t.coupon_id=C.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_user U ON t.user_id=U.id".replace("?_", GlobalConfig.tablePrefix));

        // 基础搜索
        searchValidate.setIssuer(StringUtils.isNull(searchValidate.getIssuer()) || searchValidate.getIssuer() == -1 ? null : searchValidate.getIssuer());
        couponListMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "=:couponName@C.name:str",
                "=:channel@t.channel:int",
                "=:issuer@t.issuer_id:int",
                "datetime:startTime-endTime@t.create_time:long"
        });

        // 用户搜索
        if (StringUtils.isNotNull(searchValidate.getKeyword()) && StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            mpjQueryWrapper.nested(wq->wq
                    .like("U.sn", keyword).or()
                    .like("U.nickname", keyword).or()
                    .like("U.mobile", keyword));
        }

        // 数据统计
        Map<String, Object> extend = new LinkedHashMap<>();
        MPJQueryWrapper<CouponList> allMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<CouponList> notMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<CouponList> useMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<CouponList> expireMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<CouponList> repealMpj = mpjQueryWrapper.clone();
        extend.put("all", couponListMapper.selectCount(allMpj.select("t.id")));
        extend.put("not", couponListMapper.selectCount(notMpj.select("t.id").eq("t.status", CouponEnum.USE_STATUS_NOT.getCode())));
        extend.put("use", couponListMapper.selectCount(useMpj.select("t.id").eq("t.status", CouponEnum.USE_STATUS_OK.getCode())));
        extend.put("expire", couponListMapper.selectCount(expireMpj.select("t.id").eq("t.status", CouponEnum.USE_STATUS_EXPIRE.getCode())));
        extend.put("repeal", couponListMapper.selectCount(repealMpj.select("t.id").eq("t.status", CouponEnum.USE_STATUS_VOID.getCode())));

        mpjQueryWrapper
                .selectAll(CouponList.class)
                .select("U.sn as userSn,U.nickname,U.avatar,C.name as couponName,C.get_type");

        searchValidate.setType(StringUtils.isNull(searchValidate.getType()) || searchValidate.getType() == -1 ? null : searchValidate.getType());
        if (StringUtils.isNotNull(searchValidate.getType())) {
            mpjQueryWrapper.eq("t.status", searchValidate.getType());
        }

        // 发起查询
        IPage<MarketingCouponRecordVo> iPage = couponListMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                MarketingCouponRecordVo.class,
                mpjQueryWrapper);

        for (MarketingCouponRecordVo vo : iPage.getRecords()) {
            vo.setChannelMsg(CouponEnum.getGetTypeMsg(vo.getChannel()));
            vo.setStatusMsg(CouponEnum.getUseCouponMsg(vo.getStatus()));
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setUseTime(vo.getUseTime().equals("0") ? "-" : TimeUtils.timestampToDate(vo.getUseTime()));
            vo.setCreateTime(vo.getCreateTime().equals("0") ? "-" : TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setInvalidTime(vo.getInvalidTime().equals("0") ? "-" : TimeUtils.timestampToDate(vo.getInvalidTime()));
            vo.setIssuerMsg("系统");
            if (vo.getIssuerId() > 0) {
                SystemAuthAdmin systemAuthAdmin = systemAuthAdminMapper.selectById(vo.getId());
                if (StringUtils.isNotNull(systemAuthAdmin)) {
                    vo.setIssuerMsg(systemAuthAdmin.getNickname());
                }
            }
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 优惠券作废
     *
     * @author fzr
     * @param ids 记录ID
     */
    @Override
    public void repeal(List<Integer> ids) {
        CouponList couponList = new CouponList();
        couponList.setStatus(CouponEnum.USE_STATUS_VOID.getCode());
        couponList.setUpdateTime(System.currentTimeMillis() / 1000);
        couponListMapper.update(couponList, new QueryWrapper<CouponList>()
                .in("id", ids)
                .ne("status", CouponEnum.USE_STATUS_OK.getCode()));
    }

}
