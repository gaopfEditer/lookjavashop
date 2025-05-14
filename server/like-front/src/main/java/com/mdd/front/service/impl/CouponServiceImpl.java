package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.front.service.ICouponService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.coupon.CouponOrderSettleValidate;
import com.mdd.front.validate.order.BuyGoodsValidate;
import com.mdd.front.vo.coupon.*;
import com.mdd.front.vo.order.OrderGoodsInfoVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.coupon.CouponList;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.coupon.CouponListMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.util.ListUtils;
import com.mdd.common.util.TimeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现类
 */
@Service
public class CouponServiceImpl implements ICouponService {

    @Resource
    CouponMapper couponMapper;

    @Resource
    CouponListMapper couponListMapper;

    @Resource
    GoodsMapper goodsMapper;

    /**
     * 优惠券列表(领券中心)
     *
     * @author mjf
     * @param userId Integer
     * @param pageValidate PageValidate
     * @return PageResult<CouponListVo>
     */
    @Override
    public PageResult<CouponListVo> list(Integer userId, PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", CouponEnum.COUPON_STATUS_CONDUCT.getCode());
        queryWrapper.eq("get_type", CouponEnum.GET_TYPE_USER.getCode());
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc("id");
        IPage<Coupon> iPage = couponMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        // 已领取优惠券ID
        List<Integer> myCouponIds = __myCouponIds(userId);

        List<CouponListVo> list = new LinkedList<>();
        for (Coupon coupon : iPage.getRecords()) {
            CouponListVo vo = new CouponListVo();
            BeanUtils.copyProperties(coupon, vo);
            // 使用场景
            vo.setUseScene(CouponEnum.getUseGoodsTypeMsg(coupon.getUseGoodsType()));
            // 使用条件
            vo.setCondition(getConditionMsg(coupon.getConditionType(), coupon.getConditionMoney()));
            // 有效时间
            switch (coupon.getUseTimeType()) {
                case 2:
                    // 领券当天起
                    vo.setEffectiveTime("领券当日起" + coupon.getUseTime() + "天内可用");
                    break;
                case 3:
                    // 领券次日起
                    vo.setEffectiveTime("领券次日起" + coupon.getUseTime() + "天内可用");
                    break;
                default:
                    // 固定时间
                    String startTime = TimeUtils.timestampToDate(coupon.getUseTimeStart());
                    String endTime = TimeUtils.timestampToDate(coupon.getUseTimeEnd());
                    vo.setEffectiveTime(startTime + " ~ " + endTime);
            }
            // 能否领取
            vo.setIsAble(__isAbleReceive(userId, coupon));
            // 库存是否为空
            vo.setIsEmpty(0);
            if (coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_FIXED.getCode())) {
                if (coupon.getSendTotal() <= 0) {
                    vo.setIsEmpty(1);
                }
                Long receiveCount = couponListMapper.selectCount(new QueryWrapper<CouponList>());
                if (receiveCount >= coupon.getSendTotal()) {
                    vo.setIsEmpty(1);
                }
            }
            // 是否已领取
            vo.setIsReceive(0);
            if (myCouponIds.contains(coupon.getId())) {
                vo.setIsReceive(1);
            }

            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 领取优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     */
    @Override
    public void receive(Integer userId, Integer id) {
        Coupon coupon = couponMapper.selectById(id);
        Assert.notNull(coupon, "优惠券不存在");

        if (coupon.getStatus().equals(CouponEnum.COUPON_STATUS_NOT.getCode())) {
            throw new OperateException("优惠券尚未开放领取，敬请期待");
        }

        if (coupon.getStatus().equals(CouponEnum.COUPON_STATUS_END.getCode())) {
            throw new OperateException("领取失败，优惠券活动已结束");
        }

        if (coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_FIXED.getCode())) {
            if (coupon.getSendTotal() <= 0) {
                throw new OperateException("已抢光~");
            }
            long receiveTotal = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                    .eq("coupon_id", coupon.getId()));
            if (receiveTotal >= coupon.getSendTotal()) {
                throw new OperateException("已抢光~");
            }
        }

        if (coupon.getGetNumType().equals(CouponEnum.GET_NUM_TYPE_LIMIT.getCode())) {
            Long limitCount = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                    .eq("user_id", userId)
                    .eq("coupon_id", coupon.getId()));
            if (limitCount >= coupon.getGetNum()) {
                throw new OperateException("领取失败，超过领取限制");
            }
        }

        if (coupon.getGetNumType().equals(CouponEnum.GET_NUM_TYPE_DAY.getCode())) {
            long startDay = TimeUtils.today().get(0);
            long endDay = TimeUtils.today().get(1);
            Long dayCount = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                    .eq("user_id", userId)
                    .eq("coupon_id", coupon.getId())
                    .ge("create_time", startDay)
                    .le("create_time", endDay));
           if (dayCount >= coupon.getGetNum()) {
               throw new OperateException("领取失败，超过今天领取限制");
           }
        }

        long invalidTime = 0;
        switch (coupon.getUseTimeType()) {
            case 1:
                invalidTime = coupon.getUseTimeEnd();
                break;
            case 2:
                invalidTime = TimeUtils.timestamp() + (coupon.getUseTime() * 86400);
                break;
            case 3:
                invalidTime = TimeUtils.daysAfter(1) + (coupon.getUseTime() * 86400);
                break;
        }

        String couponCode = couponListMapper.randMakeOrderSn("coupon_code");

        CouponList couponList = new CouponList();
        couponList.setChannel(CouponEnum.GET_TYPE_USER.getCode());
        couponList.setCouponCode(couponCode);
        couponList.setIssuerId(0);
        couponList.setCouponId(coupon.getId());
        couponList.setOrderId(0);
        couponList.setUserId(userId);
        couponList.setStatus(CouponEnum.USE_STATUS_NOT.getCode());
        couponList.setInvalidTime(invalidTime);
        couponList.setCreateTime(System.currentTimeMillis() / 1000);
        couponList.setUpdateTime(System.currentTimeMillis() / 1000);
        couponListMapper.insert(couponList);
    }

    /**
     * 我的优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param pageValidate PageValidate
     * @param status Integer
     * @return PageResult<CouponMyListVo>
     */
    @Override
    public PageResult<CouponMyListVo> myCoupon(Integer userId, PageValidate pageValidate, Integer status) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<CouponList> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.select("t.id as coupon_list_id, t.status,t.create_time,t.invalid_time," +
                "c.id,c.name,c.money,c.condition_money,c.condition_type,c.use_time_type,c.use_time," +
                "c.use_time_start,c.use_time_end,c.use_goods_type");
        mpjQueryWrapper.innerJoin("?_coupon c ON c.id=t.coupon_id".replace("?_", GlobalConfig.tablePrefix));
        mpjQueryWrapper.eq("t.user_id", userId);
        mpjQueryWrapper.eq("t.is_delete", 0);
        mpjQueryWrapper.eq("c.is_delete", 0);
        mpjQueryWrapper.orderByDesc("t.id");

        switch (status) {
            case 0:
                mpjQueryWrapper.eq("t.status", CouponEnum.USE_STATUS_NOT.getCode());
                mpjQueryWrapper.ge("t.invalid_time", TimeUtils.timestamp());
                break;
            case 1:
                mpjQueryWrapper.eq("t.status", CouponEnum.USE_STATUS_OK.getCode());
                break;
            case 2:
            case 3:
                mpjQueryWrapper.eq("t.status", CouponEnum.USE_STATUS_EXPIRE.getCode());
                mpjQueryWrapper.or().eq("t.user_id", userId)
                        .lt("t.invalid_time",TimeUtils.timestamp())
                        .eq("t.is_delete", 0)
                        .eq("c.is_delete", 0)
                        .orderByDesc("t.id");
                break;
        }

        IPage<CouponMyListVo> iPage = couponListMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                CouponMyListVo.class,
                mpjQueryWrapper);

        for (CouponMyListVo vo : iPage.getRecords()) {
            vo.setCondition(getConditionMsg(vo.getConditionType(), vo.getConditionMoney()));
            vo.setUseScene(CouponEnum.getUseGoodsTypeMsg(vo.getUseGoodsType()));
            String endTime;
            switch (vo.getUseTimeType()) {
                case 1:
                    // 固定时间
                    if (vo.getUseTimeStart() > TimeUtils.timestamp()) {
                        vo.setEffectiveTime(TimeUtils.timestampToDate(vo.getUseTimeStart(), "yyyy-MM-dd HH:mm") + " 生效");
                    } else {
                        endTime = TimeUtils.timestampToDate(vo.getInvalidTime(), "yyyy-MM-dd HH:mm");
                        vo.setEffectiveTime("有效期至:" + endTime);
                    }
                    break;
                case 2:
                    // 领券当天起
                    endTime = TimeUtils.timestampToDate(vo.getInvalidTime(), "yyyy-MM-dd HH:mm");
                    vo.setEffectiveTime("有效期至:" + endTime);
                    break;
                case 3:
                    // 领券次日起
                    long startTimeTamp = vo.getCreateTime() + 86400;
                    if (startTimeTamp > TimeUtils.timestamp()) {
                        vo.setEffectiveTime(TimeUtils.timestampToDate(startTimeTamp, "yyyy-MM-dd HH:mm") + " 生效");
                    } else {
                        endTime = TimeUtils.timestampToDate(vo.getInvalidTime(), "yyyy-MM-dd HH:mm");
                        vo.setEffectiveTime("有效期至:" + endTime);
                    }
                    break;
            }
        }

        // 统计
        Map<String, Object> extend = new LinkedHashMap<>();

        Integer normalCount = couponListMapper.selectJoinCount(new MPJQueryWrapper<CouponList>()
                .innerJoin("?_coupon c ON c.id=t.coupon_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("c.is_delete", 0)
                .eq("t.status", CouponEnum.USE_STATUS_NOT.getCode())
                .ge("t.invalid_time", TimeUtils.timestamp())
                .eq("t.user_id", userId));

        Integer usedCount = couponListMapper.selectJoinCount(new MPJQueryWrapper<CouponList>()
                .innerJoin("?_coupon c ON c.id=t.coupon_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("c.is_delete", 0)
                .eq("t.status", CouponEnum.USE_STATUS_OK.getCode())
                .eq("t.user_id", userId));

        Integer invalidCount = couponListMapper.selectJoinCount(new MPJQueryWrapper<CouponList>()
                .innerJoin("?_coupon c ON c.id=t.coupon_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("c.is_delete", 0)
                .eq("t.user_id", userId)
                .eq("t.status", CouponEnum.USE_STATUS_EXPIRE.getCode())
                .or()
                .eq("c.is_delete", 0)
                .eq("t.user_id", userId)
                .lt("t.invalid_time",TimeUtils.timestamp()));

        extend.put("normal", normalCount);
        extend.put("used", usedCount);
        extend.put("invalid", invalidCount);

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 商品页优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param goodsId Integer
     * @return List<CouponGoodsListVo>
     */
    @Override
    public List<CouponGoodsListVo> goodsCoupon(Integer userId, Integer goodsId) {
        // 优惠券列表
        List<Coupon> couponList = couponMapper.selectList(new QueryWrapper<Coupon>()
                .eq("get_type", CouponEnum.GET_TYPE_USER.getCode())
                .eq("status", CouponEnum.COUPON_STATUS_CONDUCT.getCode())
                .eq("is_delete", 0)
                .orderByAsc("condition_money")
                .orderByDesc("money")
                .orderByAsc("use_time_end"));

        // 已领取优惠券ID
        List<Integer> myCouponIds = __myCouponIds(userId);

        List<CouponGoodsListVo> list = new LinkedList<>();
        for (Coupon coupon : couponList) {
            // 当前商品能否使用
            boolean goodsAbleUse = false;
            switch (coupon.getUseGoodsType()) {
                case 1:
                    goodsAbleUse = true;
                    break;
                case 2:
                    List<Integer> allowGoodsIds = ListUtils.stringToListAsInt(coupon.getUseGoodsIds(), ",");
                    if (allowGoodsIds.contains(goodsId)) {
                        goodsAbleUse = true;
                    }
                    break;
                case 3:
                    List<Integer> banGoodsIds = ListUtils.stringToListAsInt(coupon.getUseGoodsIds(), ",");
                    if (!banGoodsIds.contains(goodsId)) {
                        goodsAbleUse = true;
                    }
                    break;
            }

            if (coupon.getUseTimeType().equals(CouponEnum.USE_TIME_TYPE_FIXED.getCode()) && coupon.getUseTimeEnd() < TimeUtils.timestamp()) {
                goodsAbleUse = false;
            }

            if (!goodsAbleUse) {
                continue;
            }

            CouponGoodsListVo vo = new CouponGoodsListVo();
            // 使用条件
            vo.setCondition(getConditionMsg(coupon.getConditionType(), coupon.getConditionMoney()));
            // 使用条件
            vo.setUseScene(CouponEnum.getUseGoodsTypeMsg(coupon.getUseGoodsType()));
            // 使用时间
            switch (coupon.getUseTimeType()) {
                case 2:
                    // 领券当天起
                    vo.setEffectiveTime("领券当日起" + coupon.getUseTime() + "天内可用");
                    break;
                case 3:
                    // 领券次日起
                    vo.setEffectiveTime("领取次日起" + coupon.getUseTime() + "天内可用");
                    break;
                default:
                    // 固定时间
                    String startTime = TimeUtils.timestampToDate(coupon.getUseTimeStart());
                    String endTime = TimeUtils.timestampToDate(coupon.getUseTimeEnd());
                    vo.setEffectiveTime(startTime + " ~ " + endTime);
            }
            // 是否已领取
            vo.setIsReceive(0);
            if (myCouponIds.contains(coupon.getId())) {
                vo.setIsReceive(1);
            }
            // 是否可领取
            vo.setIsAble(__isAbleReceive(userId, coupon));

            vo.setId(coupon.getId());
            vo.setName(coupon.getName());
            vo.setMoney(coupon.getMoney());
            list.add(vo);
        }

        return list;
    }

    /**
     * 订单结算优惠券
     *
     * @author mjf
     * @param userId Integer
     * @param couponSettleValidate CouponOrderSettleValidate
     * @return CouponOrderResultVo
     */
    @Override
    public CouponOrderResultVo orderCoupon(Integer userId, CouponOrderSettleValidate couponSettleValidate) {
        // 订单金额
        BigDecimal orderAmount = BigDecimal.ZERO;
        // 商品id
        List<Integer> goodsIds = new ArrayList<>();

        if (couponSettleValidate.getBuyType().equals("buyNow")) {
            BuyGoodsValidate buyNowGoods = couponSettleValidate.getBuyGoods().get(0);
            OrderGoodsInfoVo oneOrderGoods = goodsMapper.selectJoinOne(OrderGoodsInfoVo.class, new MPJQueryWrapper<Goods>()
                    .eq("t.is_delete", 0)
                    .eq("t.status", 1)
                    .eq("GS.id", buyNowGoods.getSkuId())
                    .innerJoin("?_goods_sku GS ON GS.goods_id=t.id".replace("?_", GlobalConfig.tablePrefix))
                    .select("t.code as goods_code,t.express_type,t.express_template_id,t.image,t.name as goods_name,t.status," +
                            "GS.goods_id,GS.id as goods_sku_id,GS.image as sku_image,GS.sku_value_arr," +
                            "GS.stock,GS.price,GS.weight,GS.volume")
                    .last("limit 1"));

            if (oneOrderGoods != null) {
                orderAmount = oneOrderGoods.getPrice().multiply(BigDecimal.valueOf(buyNowGoods.getNum()));
                goodsIds.add(oneOrderGoods.getGoodsId());
            }
        } else {
            // 购物车Ids
            List<Integer> cartIds = couponSettleValidate.getBuyGoods().stream().map(BuyGoodsValidate::getCartId).collect(Collectors.toList());
            // 商品信息
            List<OrderGoodsInfoVo> orderGoodsList = goodsMapper.selectJoinList(OrderGoodsInfoVo.class, new MPJQueryWrapper<Goods>()
                    .eq("t.is_delete", 0)
                    .eq("t.status", 1)
                    .eq("c.is_delete", 0)
                    .eq("user_id", userId)
                    .in("c.id", cartIds)
                    .innerJoin("?_cart c ON c.goods_id = t.id".replace("?_", GlobalConfig.tablePrefix))
                    .innerJoin("?_goods_sku GS ON GS.id = c.goods_sku_id".replace("?_", GlobalConfig.tablePrefix))
                    .select("t.code as goods_code,t.express_type,t.express_template_id,t.image,t.name as goods_name,t.status," +
                            "GS.goods_id,GS.id as goods_sku_id,GS.image as sku_image,GS.sku_value_arr," +
                            "GS.stock,GS.price,GS.weight,GS.volume,c.num"));

            if (!orderGoodsList.isEmpty()) {
                for (OrderGoodsInfoVo goodsInfoVo : orderGoodsList) {
                    BigDecimal itemGoodsAmount = goodsInfoVo.getPrice().multiply(BigDecimal.valueOf(goodsInfoVo.getNum()));
                    orderAmount = orderAmount.add(itemGoodsAmount);
                    goodsIds.add(goodsInfoVo.getGoodsId());
                }
            }
        }

        // 用户未使用优惠券,校验是否可使用
        List<CouponOrderSettleVo> list = couponListMapper.selectJoinList(CouponOrderSettleVo.class,
                new MPJQueryWrapper<CouponList>()
                        .select("t.id as coupon_list_id, t.coupon_id, t.status,t.create_time,t.invalid_time," +
                                "c.id,c.name,c.money,c.condition_money,c.condition_type,c.use_time_type,c.use_time," +
                                "c.use_time_start,c.use_time_end,c.use_goods_type,c.use_goods_ids")
                        .innerJoin("?_coupon c ON c.id=t.coupon_id".replace("?_", GlobalConfig.tablePrefix))
                        .eq("c.is_delete", 0)
                        .eq("t.is_delete", 0)
                        .eq("t.user_id", userId)
                        .eq("t.status", CouponEnum.USE_STATUS_NOT.getCode())
                        .orderByDesc("c.money"));

        // 可用优惠券
        List<CouponOrderSettleVo> ableUse = new LinkedList<>();
        List<CouponOrderSettleVo> unableUse = new LinkedList<>();

        for (CouponOrderSettleVo vo : list) {
            boolean flagUseTime = checkUseTime(vo);
            boolean flagCondition = checkCondition(vo, orderAmount);
            boolean flagGoods = checkGoods(vo, goodsIds);
            if (flagUseTime && flagCondition && flagGoods) {
                ableUse.add(vo);
            } else {
                unableUse.add(vo);
            }
        }

        CouponOrderResultVo resultVo = new CouponOrderResultVo();
        resultVo.setAbleUse(ableUse);
        resultVo.setAbleUseCount(ableUse.size());
        resultVo.setUnableUse(unableUse);
        resultVo.setUnableUseCount(unableUse.size());
        return resultVo;
    }

    /**
     * 校验能否领取
     *
     * @author mjf
     * @param couponSettleVo CouponOrderSettleVo
     * @return Boolean
     */
    public Boolean checkUseTime(CouponOrderSettleVo couponSettleVo) {
        Long nowTimeTamp = TimeUtils.timestamp();
        switch (couponSettleVo.getUseTimeType()) {
            case 1:
                // 固定时间
                String fixedStartTime = TimeUtils.timestampToDate(couponSettleVo.getUseTimeStart());
                String fixedEndTime = TimeUtils.timestampToDate(couponSettleVo.getUseTimeEnd());
                couponSettleVo.setEffectiveTime(fixedStartTime + " ~ " + fixedEndTime);
                if (nowTimeTamp >= couponSettleVo.getUseTimeStart() && nowTimeTamp <= couponSettleVo.getUseTimeEnd()) {
                    return true;
                }
                couponSettleVo.setFailUseMsg("不在使用时间范围内");
                return false;
            case 2:
                // 领券当天起
                String todayInvalidTime = TimeUtils.timestampToDate(couponSettleVo.getInvalidTime(), "yyyy-MM-dd HH:mm");
                couponSettleVo.setEffectiveTime("有效期至:" + todayInvalidTime);
                if (nowTimeTamp < couponSettleVo.getInvalidTime()) {
                    return true;
                }
                couponSettleVo.setFailUseMsg("不在使用时间范围内");
                return false;
            case 3:
                // 领券次日起
                long tomorrowStartTime = couponSettleVo.getCreateTime() + 86400;
                if (tomorrowStartTime > TimeUtils.timestamp()) {
                    couponSettleVo.setEffectiveTime(TimeUtils.timestampToDate(tomorrowStartTime, "yyyy-MM-dd HH:mm") + " 生效");
                } else {
                    String tomorrowInvalidTime = TimeUtils.timestampToDate(couponSettleVo.getInvalidTime(), "yyyy-MM-dd HH:mm");
                    couponSettleVo.setEffectiveTime("有效期至:" + tomorrowInvalidTime);
                }

                if (nowTimeTamp >= tomorrowStartTime && nowTimeTamp <= couponSettleVo.getInvalidTime()) {
                    return true;
                }
                couponSettleVo.setFailUseMsg("不在使用时间范围内");
                return false;
            default:
                couponSettleVo.setFailUseMsg("不在使用时间范围内");
                return false;
        }
    }

    /**
     * 校验优惠条件
     *
     * @author mjf
     * @param couponSettleVo CouponOrderSettleVo
     * @param orderAmount BigDecimal
     * @return Boolean
     */
    public Boolean checkCondition(CouponOrderSettleVo couponSettleVo, BigDecimal orderAmount) {
        if (couponSettleVo.getConditionType() == CouponEnum.CONDITION_TYPE_NOT.getCode()) {
            couponSettleVo.setCondition("无门槛");
            return true;
        }
        couponSettleVo.setCondition("满" + couponSettleVo.getConditionMoney() + "可用");
        if (orderAmount.compareTo(couponSettleVo.getConditionMoney()) >= 0) {
            return true;
        }
        couponSettleVo.setFailUseMsg("未满足规定的订单金额");
        return false;
    }

    /**
     * 校验商品是否可用
     *
     * @author mjf
     * @param couponSettleVo CouponOrderSettleVo
     * @param goodsIds List<Integer>
     * @return Boolean
     */
    public Boolean checkGoods(CouponOrderSettleVo couponSettleVo, List<Integer> goodsIds) {
        // 全部商品可用
        if (couponSettleVo.getUseGoodsType() == CouponEnum.USE_GOODS_TYPE_NOT.getCode()) {
            couponSettleVo.setUseScene("全店通用");
            return true;
        }

        if (goodsIds.isEmpty()) {
            couponSettleVo.setUseScene("商品不适用");
            return false;
        }

        // 指定商品可用
        if (couponSettleVo.getUseGoodsType() == CouponEnum.USE_GOODS_TYPE_ALLOW.getCode()) {
            couponSettleVo.setUseScene("指定商品可用");
            List<Integer> allowGoodsIds = ListUtils.stringToListAsInt(couponSettleVo.getUseGoodsIds(), ",");
            for (Integer goodsId : goodsIds) {
                if (!allowGoodsIds.contains(goodsId)) {
                    couponSettleVo.setFailUseMsg("部分商品不适用");
                    return false;
                }
            }
            return true;
        }

        // 指定商品不可用
        if (couponSettleVo.getUseGoodsType() == CouponEnum.USE_GOODS_TYPE_BAN.getCode()) {
            couponSettleVo.setUseScene("指定商品不可用");
            List<Integer> banGoodsIds = ListUtils.stringToListAsInt(couponSettleVo.getUseGoodsIds(), ",");
            for (Integer goodsId : goodsIds) {
                if (banGoodsIds.contains(goodsId)) {
                    couponSettleVo.setFailUseMsg("部分商品不适用");
                    return false;
                }
            }
            return true;
        }

        couponSettleVo.setFailUseMsg("部分商品不适用");
        return false;
    }

    /**
     * 已领优惠券ID
     *
     * @author mjf
     * @param userId Integer
     * @return List<Integer>
     */
    private List<Integer> __myCouponIds (Integer userId) {
        List<Integer> myCouponIds = new ArrayList<>();
        if (userId != null && userId > 0) {
            List<CouponList> myCouponList = couponListMapper.selectList(new QueryWrapper<CouponList>()
                    .eq("user_id", userId)
                    .eq("is_delete", 0));
            for (CouponList couponListItem : myCouponList) {
                myCouponIds.add(couponListItem.getCouponId());
            }
        }
        return myCouponIds;
    }

    /**
     * 是否可领取
     *
     * @author mjf
     * @param userId Integer
     * @param coupon Coupon
     * @return Integer
     */
    private Integer __isAbleReceive(Integer userId, Coupon coupon) {
        int isAble = 1;
        switch (coupon.getGetNumType()) {
            case 2:
                // 限制张数
                Long limitCount = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                        .eq("user_id", userId)
                        .eq("coupon_id", coupon.getId()));
                isAble = coupon.getGetNum() > limitCount ? 1 : 0;
                break;
            case 3:
                // 每天限制张数
                long startDay = TimeUtils.today().get(0);
                long endDay = TimeUtils.today().get(1);
                Long dayCount = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                        .eq("user_id", userId)
                        .eq("coupon_id", coupon.getId())
                        .ge("create_time", startDay)
                        .le("create_time", endDay));
                isAble = coupon.getGetNum() > dayCount ? 1 : 0;
                break;
        }
        return isAble;
    }

    /**
     * 条件描述
     *
     * @author mjf
     * @param conditionType Integer
     * @param conditionMoney BigDecimal
     * @return String
     */
    @Override
    public String getConditionMsg(Integer conditionType, BigDecimal conditionMoney) {
        String conditionMsg = "无门槛";
        if (conditionType.equals(CouponEnum.CONDITION_TYPE_FULL.getCode())) {
            conditionMsg = "满" + conditionMoney + "可用";
        }
        return conditionMsg;
    }
}
