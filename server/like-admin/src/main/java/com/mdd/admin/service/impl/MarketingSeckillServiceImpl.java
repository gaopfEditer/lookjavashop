package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IMarketingSeckillService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillCreateValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillSearchValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillUpdateValidate;
import com.mdd.admin.vo.marketing.MarketingSeckillDetailVo;
import com.mdd.admin.vo.marketing.MarketingSeckillListedVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.entity.seckill.SeckillActivity;
import com.mdd.common.entity.seckill.SeckillGoods;
import com.mdd.common.entity.seckill.SeckillGoodsSku;
import com.mdd.common.enums.SeckillEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.mapper.seckill.SeckillActivityMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsSkuMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class MarketingSeckillServiceImpl implements IMarketingSeckillService {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;

    @Resource
    SeckillActivityMapper seckillActivityMapper;

    @Resource
    SeckillGoodsMapper seckillGoodsMapper;

    @Resource
    SeckillGoodsSkuMapper seckillGoodsSkuMapper;

    /**
     * 秒杀活动列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<MarketingSeckillListedVo>
     */
    @Override
    public PageResult<MarketingSeckillListedVo> list(PageValidate pageValidate, MarketingSeckillSearchValidate searchValidate) {
        Integer pageNo   = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        // 条件构造
        QueryWrapper<SeckillActivity> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,name,start_time,end_time,status,create_time")
                .eq("is_delete", 0)
                .orderByDesc("id");

        // 基础搜索
        seckillActivityMapper.setSearch(queryWrapper, searchValidate, new String[]{
                "=:type@status:int",
                "like:name:str",
                ">=:startTime@start_time:long",
                "<=:endTime@end_time:long",
        });

        // 商品搜素
        if (StringUtils.isNotEmpty(searchValidate.getGoodsName())) {
            List<Goods> goodsList = goodsMapper.selectList(new QueryWrapper<Goods>()
                    .like("name", searchValidate.getGoodsName())
                    .eq("is_delete", 0));

            for (int i=0; i<=0; i++) {
                List<Integer> goodsIds = new LinkedList<>();
                for (Goods goods : goodsList) {
                    goodsIds.add(goods.getId());
                }

                if (goodsIds.isEmpty()) {
                    break;
                }

                List<Integer> seckillGoodsIds = new LinkedList<>();
                List<SeckillGoods> seckillGoodsList = seckillGoodsMapper.selectList(new QueryWrapper<SeckillGoods>().in("goods_id", goodsIds));
                for (SeckillGoods seckillGoods : seckillGoodsList) {
                    seckillGoodsIds.add(seckillGoods.getSeckillId());
                }

                if (seckillGoodsIds.isEmpty()) {
                    break;
                }

                queryWrapper.in("id", seckillGoodsIds);
            }
        }

        // 发起查询
        IPage<SeckillActivity> iPage = seckillActivityMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        // 处理数据
        List<MarketingSeckillListedVo> list = new LinkedList<>();
        for (SeckillActivity seckillActivity: iPage.getRecords()) {
            MarketingSeckillListedVo vo = new MarketingSeckillListedVo();
            BeanUtils.copyProperties(seckillActivity, vo);
            vo.setStartTime(TimeUtils.timestampToDate(seckillActivity.getStartTime()));
            vo.setEndTime(TimeUtils.timestampToDate(seckillActivity.getEndTime()));
            vo.setCreateTime(TimeUtils.timestampToDate(seckillActivity.getCreateTime()));

            Long goodsNum = seckillGoodsMapper.selectCount(new QueryWrapper<SeckillGoods>().eq("seckill_id", seckillActivity.getId()));
            Long closingOrder = seckillGoodsSkuMapper.sumByLong("closing_order", new QueryWrapper<SeckillGoodsSku>().eq("seckill_id", seckillActivity.getId()));
            Long salesVolume = seckillGoodsSkuMapper.sumByLong("sales_volume", new QueryWrapper<SeckillGoodsSku>().eq("seckill_id", seckillActivity.getId()));
            BigDecimal salesAmount = seckillGoodsSkuMapper.sum("sales_amount", new QueryWrapper<SeckillGoodsSku>().eq("seckill_id", seckillActivity.getId()));

            vo.setGoodsNum(goodsNum);
            vo.setClosingOrder(closingOrder);
            vo.setSalesVolume(salesVolume);
            vo.setSalesAmount(salesAmount);

            list.add(vo);
        }

        // 数据统计
        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("all", seckillActivityMapper.selectCount(new QueryWrapper<SeckillActivity>().eq("is_delete", 0)));
        extend.put("wait", seckillActivityMapper.selectCount(new QueryWrapper<SeckillActivity>().eq("status", SeckillEnum.STATUS_WAIT.getCode()).eq("is_delete", 0)));
        extend.put("not", seckillActivityMapper.selectCount(new QueryWrapper<SeckillActivity>().eq("status", SeckillEnum.STATUS_NOT.getCode()).eq("is_delete", 0)));
        extend.put("end", seckillActivityMapper.selectCount(new QueryWrapper<SeckillActivity>().eq("status", SeckillEnum.STATUS_END.getCode()).eq("is_delete", 0)));
        extend.put("conduct", seckillActivityMapper.selectCount(new QueryWrapper<SeckillActivity>().eq("status", SeckillEnum.STATUS_CONDUCT.getCode()).eq("is_delete", 0)));

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list, extend);
    }

    /**
     * 秒杀活动详情
     *
     * @author fzr
     * @param id 主键
     * @return MarketingSeckillVo
     */
    @Override
    public MarketingSeckillDetailVo detail(Integer id) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                    .eq("id", id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动不存在!");

        List<MarketingSeckillDetailVo.SeckillGoodsItemVo> goods = seckillGoodsMapper.selectJoinList(
                MarketingSeckillDetailVo.SeckillGoodsItemVo.class,
                new MPJQueryWrapper<SeckillGoods>()
                        .select("t.id as seckillGid,t.sales_volume,t.browse_volume,G.id,G.name,G.image,G.min_price,G.max_price,G.spec_type")
                        .eq("t.seckill_id", id)
                        .leftJoin("?_goods G ON t.goods_id=G.id".replace("?_", GlobalConfig.tablePrefix)));

        for (MarketingSeckillDetailVo.SeckillGoodsItemVo item : goods) {
            item.setImage(UrlUtils.toAbsoluteUrl(item.getImage()));
            List<MarketingSeckillDetailVo.SeckillGoodsSkuVo> skuVos = seckillGoodsSkuMapper.selectJoinList(
                    MarketingSeckillDetailVo.SeckillGoodsSkuVo.class,
                    new MPJQueryWrapper<SeckillGoodsSku>()
                            .select("GS.id,GS.sku_value_arr,GS.price,GS.stock,t.seckill_price")
                            .eq("t.seckill_gid", item.getSeckillGid())
                            .leftJoin("?_goods_sku GS ON t.sku_id=GS.id".replace("?_", GlobalConfig.tablePrefix)));

            item.setPrices(skuVos);
        }

        MarketingSeckillDetailVo vo = new MarketingSeckillDetailVo();
        BeanUtils.copyProperties(seckillActivity, vo);
        vo.setStartTime(TimeUtils.timestampToDate(seckillActivity.getStartTime()));
        vo.setEndTime(TimeUtils.timestampToDate(seckillActivity.getEndTime()));
        vo.setGoods(goods);
        return vo;
    }

    /**
     * 秒杀活动新增
     *
     * @author fzr
     * @param createValidate 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(MarketingSeckillCreateValidate createValidate) {
        // 活动时间处理
        long currTime = System.currentTimeMillis() / 1000;
        Long startTime = TimeUtils.dateToTimestamp(createValidate.getStartTime());
        Long endTime = TimeUtils.dateToTimestamp(createValidate.getEndTime());

        if (createValidate.getGoods().size() > 25) {
            throw new OperateException("活动商品不能超出25个");
        }

//        if (startTime <= currTime) {
//            throw new OperateException("活动开始时间不能少于当前时间");
//        }

        if (endTime <= currTime) {
            throw new OperateException("活动结束时间不能少于当前时间!");
        }

        if (endTime <= startTime) {
            throw new OperateException("活动结束时间不能少于开始时间");
        }


        // 创建秒杀活动
        SeckillActivity seckillActivity = new SeckillActivity();
        seckillActivity.setLimitStatus(createValidate.getLimitStatus());
        seckillActivity.setName(createValidate.getName());
        seckillActivity.setRemarks(createValidate.getRemarks());
        seckillActivity.setStartTime(startTime);
        seckillActivity.setEndTime(endTime);
        seckillActivity.setMaxBuy(createValidate.getMaxBuy());
        seckillActivity.setCreateTime(System.currentTimeMillis() / 1000);
        seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.insert(seckillActivity);

        // 处理活动商品
        for (MarketingSeckillCreateValidate.GoodsItemValidate goodsItem : createValidate.getGoods()) {
            Goods goods = goodsMapper.selectById(goodsItem.getId());
            if (StringUtils.isNull(goods) || goods.getIsDelete().equals(1) || goods.getStatus().equals(0)) {
                String goodsName = StringUtils.isNotNull(goods) ? goods.getName() : "";
                throw new OperateException(goodsName+"商品可能已被下架,操作失败!");
            }

            if (!goods.getSpecType().equals(goodsItem.getSpecType())) {
                throw new OperateException(goods.getName()+"商品规格疑似被修改,操作失败!");
            }

            // 验证是否在活动
            long isSeckill = seckillGoodsMapper.selectJoinCount(new MPJQueryWrapper<SeckillGoods>()
                    .eq("t.goods_id", goods.getId())
                    .ne("SA.status", SeckillEnum.STATUS_END.getCode())
                    .ge("SA.end_time", currTime)
                    .innerJoin("?_seckill_activity SA ON t.seckill_id=SA.id".replace("?_", GlobalConfig.tablePrefix)));

            if (isSeckill > 0) {
                throw new OperateException("《"+goods.getName()+"》已参与秒杀活动,请务重复添加");
            }

            // 创建活动商品
            SeckillGoods seckillGoods = new SeckillGoods();
            seckillGoods.setSeckillId(seckillActivity.getId());
            seckillGoods.setGoodsId(goods.getId());
            seckillGoods.setMinSeckillPrice(null);
            seckillGoods.setMaxSeckillPrice(null);
            seckillGoods.setBrowseVolume(goodsItem.getBrowseVolume() <= 0 ? 0 : goodsItem.getBrowseVolume());
            seckillGoods.setSalesVolume(goodsItem.getSalesVolume() <= 0 ? 0 : goodsItem.getSalesVolume());
            seckillGoodsMapper.insert(seckillGoods);

            // 创建商品规格
            List<BigDecimal> priceArray = new LinkedList<>();
            for (MarketingSeckillCreateValidate.SkuPriceValidate skuItem : goodsItem.getPrices()) {
                if (StringUtils.isNull(skuItem.getSeckillPrice()) || skuItem.getSeckillPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new OperateException("秒杀价不能为空且不能少于等于0");
                }

                GoodsSku goodsSku = goodsSkuMapper.selectById(skuItem.getSkuId());
                if (StringUtils.isNotNull(goodsSku)) {
                    if (skuItem.getSeckillPrice().compareTo(goodsSku.getPrice()) > 0) {
                        throw new OperateException("秒杀价格不能大于原始价格");
                    }

                    SeckillGoodsSku seckillGoodsSku = new SeckillGoodsSku();
                    seckillGoodsSku.setSeckillGid(seckillGoods.getId());
                    seckillGoodsSku.setSeckillId(seckillActivity.getId());
                    seckillGoodsSku.setSkuId(skuItem.getSkuId());
                    seckillGoodsSku.setSkuValueStr(goodsSku.getSkuValueArr());
                    seckillGoodsSku.setSellPrice(goodsSku.getPrice());
                    seckillGoodsSku.setSeckillPrice(skuItem.getSeckillPrice());
                    seckillGoodsSku.setSalesAmount(new BigDecimal(0));
                    seckillGoodsSku.setSalesVolume(0);
                    seckillGoodsSku.setClosingOrder(0);
                    seckillGoodsSkuMapper.insert(seckillGoodsSku);
                    priceArray.add(skuItem.getSeckillPrice());
                }
            }

            // 最大最小价格
            seckillGoods.setMinSeckillPrice(Collections.min(priceArray));
            seckillGoods.setMaxSeckillPrice(Collections.max(priceArray));
            seckillGoodsMapper.updateById(seckillGoods);

            // 给商品打活动标识
            goods.setIsActivity(1);
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 秒杀活动编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(MarketingSeckillUpdateValidate updateValidate) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                    .eq("id", updateValidate.getId())
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已不存在!");

        // 活动时间处理
        long currTime = System.currentTimeMillis() / 1000;
        Long startTime = TimeUtils.dateToTimestamp(updateValidate.getStartTime());
        Long endTime = TimeUtils.dateToTimestamp(updateValidate.getEndTime());

        if (endTime <= currTime) {
            throw new OperateException("活动结束时间不能少于当前时间!");
        }

        if (endTime <= startTime) {
            throw new OperateException("活动结束时间不能少于开始时间!");
        }

        if (!seckillActivity.getStatus().equals(SeckillEnum.STATUS_WAIT.getCode())) {
            // 非待发布的状态仅可编辑部分信息
            seckillActivity.setName(updateValidate.getName());
            seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
            seckillActivity.setStartTime(startTime);
            seckillActivity.setEndTime(endTime);
            seckillActivity.setRemarks(updateValidate.getRemarks());
            seckillActivityMapper.updateById(seckillActivity);

            // 更新商品数据虚拟销量及虚拟浏览量
            for (MarketingSeckillUpdateValidate.GoodsItemValidate goodsItemValidate : updateValidate.getGoods()) {
                SeckillGoods seckillGoods = new SeckillGoods();
                seckillGoods.setSalesVolume(goodsItemValidate.getSalesVolume() <= 0 ? 0 : goodsItemValidate.getSalesVolume());
                seckillGoods.setBrowseVolume(goodsItemValidate.getBrowseVolume() <= 0 ? 0 : goodsItemValidate.getBrowseVolume());
                seckillGoodsMapper.update(seckillGoods, new QueryWrapper<SeckillGoods>()
                        .eq("goods_id", goodsItemValidate.getId())
                        .eq("seckill_id", updateValidate.getId()));
            }
            return;
        }


        if (updateValidate.getGoods().size() > 25) {
            throw new OperateException("活动商品不能超出25个!");
        }

//        if (startTime <= currTime) {
//            throw new OperateException("活动开始时间不能少于当前时间!");
//        }

        // 更新活动信息
        seckillActivity.setLimitStatus(updateValidate.getLimitStatus());
        seckillActivity.setName(updateValidate.getName());
        seckillActivity.setRemarks(updateValidate.getRemarks());
        seckillActivity.setStartTime(startTime);
        seckillActivity.setEndTime(endTime);
        seckillActivity.setMaxBuy(updateValidate.getMaxBuy());
        seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.updateById(seckillActivity);

        // 移除旧的商品
        seckillGoodsMapper.delete(new QueryWrapper<SeckillGoods>().eq("seckill_id", updateValidate.getId()));
        seckillGoodsSkuMapper.delete(new QueryWrapper<SeckillGoodsSku>().eq("seckill_id", updateValidate.getId()));

        // 删除旧商品活动状态
        for (MarketingSeckillUpdateValidate.GoodsItemValidate goodsItemValidate : updateValidate.getGoods()) {
            Goods goods = new Goods();
            goods.setIsActivity(0);
            goodsMapper.update(goods, new QueryWrapper<Goods>()
                    .eq("id", goodsItemValidate.getId())
                    .eq("is_delete",0));
        }
        // 处理活动商品
        for (MarketingSeckillUpdateValidate.GoodsItemValidate goodsItem : updateValidate.getGoods()) {
            Goods goods = goodsMapper.selectById(goodsItem.getId());
            if (StringUtils.isNull(goods) || goods.getIsDelete().equals(1) || goods.getStatus().equals(0)) {
                String goodsName = StringUtils.isNotNull(goods) ? goods.getName() : "";
                throw new OperateException(goodsName+"商品可能已被下架了,操作失败!");
            }

            if (!goods.getSpecType().equals(goodsItem.getSpecType())) {
                throw new OperateException(goods.getName()+"商品规格疑似被修改了,操作失败!");
            }

            // 验证是否在活动
            long isSeckill = seckillGoodsMapper.selectJoinCount(new MPJQueryWrapper<SeckillGoods>()
                    .eq("t.goods_id", goods.getId())
                    .ge("SA.end_time", currTime)
                    .ne("SA.id", updateValidate.getId())
                    .ne("SA.status", SeckillEnum.STATUS_END.getCode())
                    .eq("SA.is_delete", 0)
                    .innerJoin("?_seckill_activity SA ON t.seckill_id=SA.id".replace("?_", GlobalConfig.tablePrefix)));

            if (isSeckill > 0) {
                throw new OperateException("《"+goods.getName()+"》已参与秒杀活动,请务重复添加");
            }

            // 创建活动商品
            SeckillGoods seckillGoods = new SeckillGoods();
            seckillGoods.setSeckillId(updateValidate.getId());
            seckillGoods.setGoodsId(goods.getId());
            seckillGoods.setMaxSeckillPrice(null);
            seckillGoods.setMinSeckillPrice(null);
            seckillGoods.setSalesVolume(goodsItem.getSalesVolume() <= 0 ? 0 : goodsItem.getSalesVolume());
            seckillGoods.setBrowseVolume(goodsItem.getBrowseVolume() <= 0 ? 0 : goodsItem.getBrowseVolume());
            seckillGoodsMapper.insert(seckillGoods);

            // 创建商品规格
            List<BigDecimal> priceArray = new LinkedList<>();
            for (MarketingSeckillUpdateValidate.SkuPriceValidate skuItem : goodsItem.getPrices()) {
                if (StringUtils.isNull(skuItem.getSeckillPrice()) || skuItem.getSeckillPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new OperateException("秒杀价不能为空且不能少于等于0");
                }

                GoodsSku goodsSku = goodsSkuMapper.selectById(skuItem.getSkuId());

                if (StringUtils.isNotNull(goodsSku)) {
                    if (skuItem.getSeckillPrice().compareTo(goodsSku.getPrice()) > 0) {
                        throw new OperateException("秒杀价格不能大于原始价格");
                    }

                    SeckillGoodsSku seckillGoodsSku = new SeckillGoodsSku();
                    seckillGoodsSku.setSeckillId(seckillActivity.getId());
                    seckillGoodsSku.setSeckillGid(seckillGoods.getId());
                    seckillGoodsSku.setSkuId(skuItem.getSkuId());
                    seckillGoodsSku.setSkuValueStr(goodsSku.getSkuValueArr());
                    seckillGoodsSku.setSellPrice(goodsSku.getPrice());
                    seckillGoodsSku.setSeckillPrice(skuItem.getSeckillPrice());
                    seckillGoodsSku.setSalesAmount(new BigDecimal(0));
                    seckillGoodsSku.setClosingOrder(0);
                    seckillGoodsSku.setSalesVolume(0);
                    seckillGoodsSkuMapper.insert(seckillGoodsSku);

                    priceArray.add(skuItem.getSeckillPrice());
                }
            }

            // 最大最小价格
            seckillGoods.setMinSeckillPrice(Collections.min(priceArray));
            seckillGoods.setMaxSeckillPrice(Collections.max(priceArray));
            seckillGoodsMapper.updateById(seckillGoods);

            // 给商品打活动标识
            goods.setIsActivity(1);
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 秒杀活动删除
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void del(Integer id) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已不存在!");

        if (seckillActivity.getStatus().equals(SeckillEnum.STATUS_CONDUCT.getCode())) {
            throw new OperateException("秒杀活动正在进行中,不允许删除!");
        }

        seckillActivity.setIsDelete(1);
        seckillActivity.setStatus(SeckillEnum.STATUS_END.getCode());
        seckillActivity.setDeleteTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.updateById(seckillActivity);

        // 取消活动标识
        MPJQueryWrapper<Goods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(Goods.class);
        mpjQueryWrapper.eq("t.is_delete",0);
        mpjQueryWrapper.eq("sg.seckill_id", seckillActivity.getId());
        mpjQueryWrapper.innerJoin("?_seckill_goods sg ON sg.goods_id = t.id".replace("?_", GlobalConfig.tablePrefix));
        List<Goods> goods = goodsMapper.selectList(mpjQueryWrapper);
        if(null != goods && goods.size() > 0){
            for (Goods good : goods) {
                good.setIsActivity(0);
                goodsMapper.updateById(good);
            }
        }
    }

    /**
     * 秒杀活动命名
     *
     * @author fzr
     * @param id 主键
     * @param name 活动名称
     */
    @Override
    public void rename(Integer id, String name) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已不存在!");

        if (seckillActivity.getStatus().equals(SeckillEnum.STATUS_END.getCode())) {
            throw new OperateException("秒杀活动已结束不能编辑,操作失败!");
        }

        seckillActivity.setName(name);
        seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.updateById(seckillActivity);
    }

    /**
     * 秒杀活动发布
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void release(Integer id) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已不存在!");

        if (!seckillActivity.getStatus().equals(SeckillEnum.STATUS_WAIT.getCode())) {
            throw new OperateException("秒杀活动不是待发布状态,操作失败!");
        }

        seckillActivity.setStatus(SeckillEnum.STATUS_NOT.getCode());
        seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.updateById(seckillActivity);
    }

    /**
     * 秒杀活动开始
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void start(Integer id) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已不存在!");

        if (!seckillActivity.getStatus().equals(SeckillEnum.STATUS_NOT.getCode())) {
            throw new OperateException("秒杀活动不是待开始状态,操作失败!");
        }

        long currTime = System.currentTimeMillis() / 1000;
//        if (seckillActivity.getStartTime() > currTime) {
//            throw new OperateException("未到活动开始时间,开始失败!");
//        }

        seckillActivity.setStatus(SeckillEnum.STATUS_CONDUCT.getCode());
        seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.updateById(seckillActivity);
    }

    /**
     * 秒杀活动结束
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void end(Integer id) {
        SeckillActivity seckillActivity = seckillActivityMapper.selectOne(
                new QueryWrapper<SeckillActivity>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(seckillActivity, "秒杀活动已不存在!");

        if (seckillActivity.getStatus().equals(SeckillEnum.STATUS_END.getCode())) {
            throw new OperateException("秒杀活动已结束,请勿重复操作!");
        }

        seckillActivity.setStatus(SeckillEnum.STATUS_END.getCode());
        seckillActivity.setUpdateTime(System.currentTimeMillis() / 1000);
        seckillActivityMapper.updateById(seckillActivity);

        // 给活动商品取消标识
        MPJQueryWrapper<Goods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(Goods.class);
        mpjQueryWrapper.eq("t.is_delete",0);
        mpjQueryWrapper.eq("sg.seckill_id", seckillActivity.getId());
        mpjQueryWrapper.innerJoin("?_seckill_goods sg ON sg.goods_id = t.id".replace("?_", GlobalConfig.tablePrefix));
        List<Goods> goods = goodsMapper.selectList(mpjQueryWrapper);
        for (Goods good : goods) {
            good.setIsActivity(0);
            goodsMapper.updateById(good);
        }
    }

}
