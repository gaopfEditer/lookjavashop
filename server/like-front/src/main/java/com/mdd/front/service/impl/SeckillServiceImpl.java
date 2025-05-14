package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.front.service.ISeckillService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.seckill.SeckillDetailVo;
import com.mdd.front.vo.seckill.SeckillListVo;
import com.mdd.front.vo.seckill.SeckillSkuVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.seckill.SeckillActivity;
import com.mdd.common.entity.seckill.SeckillGoods;
import com.mdd.common.entity.seckill.SeckillGoodsSku;
import com.mdd.common.enums.SeckillEnum;
import com.mdd.common.mapper.seckill.SeckillActivityMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsSkuMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 秒杀活动实现类
 */
@Service
public class SeckillServiceImpl implements ISeckillService {

    @Resource
    SeckillActivityMapper seckillActivityMapper;

    @Resource
    SeckillGoodsMapper seckillGoodsMapper;

    @Resource
    SeckillGoodsSkuMapper seckillGoodsSkuMapper;
    
    /**
     * 秒杀活动列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return PageResult<SeckillListedVo>
     */
    @Override
    public PageResult<SeckillListVo> list(PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        // 查询出正在进行的活动ID
        List<SeckillActivity> seckillActivity = seckillActivityMapper.selectList(new QueryWrapper<SeckillActivity>()
                .eq("status", SeckillEnum.STATUS_CONDUCT.getCode())
                .eq("is_delete", 0)
                .le("start_time", TimeUtils.timestamp())
                .ge("end_time", TimeUtils.timestamp()));

        // 活动id
        List<Integer> seckillIds = new LinkedList<>();
        for (SeckillActivity seckillItem : seckillActivity) {
            seckillIds.add(seckillItem.getId());
        }
        if (seckillIds.isEmpty()) {
            seckillIds.add(0);
        }

        // 查询出活动中的商品数据
        MPJQueryWrapper<SeckillGoods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(SeckillGoods.class)
                .select("g.name as goods_name,g.image as goods_image,t.min_seckill_price as seckill_price,t.id as seckill_gid")
                .innerJoin("?_seckill_activity a ON a.id=t.seckill_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_goods g ON g.id=t.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .in("t.seckill_id", seckillIds)
                .orderByAsc("min_seckill_price");

        IPage<SeckillListVo> iPage = seckillGoodsMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                SeckillListVo.class,
                mpjQueryWrapper);

        for (SeckillListVo vo : iPage.getRecords()) {
            vo.setGoodsImage(UrlUtils.toAbsoluteUrl(vo.getGoodsImage()));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 秒杀活动详情
     *
     * @author mjf
     * @param id Integer
     * @return SeckillDetailVo
     */
    @Override
    public SeckillDetailVo detail(Integer id) {
        // 秒杀活动
        MPJQueryWrapper<SeckillGoods> mpjQueryWrapper = new MPJQueryWrapper<SeckillGoods>()
                .selectAll(SeckillGoods.class)
                .select("a.name as seckill_name, a.remarks as seckill_remarks, a.start_time, a.end_time, t.id as seckill_gid," +
                        "t.browse_volume + t.actual_browse_volume as browse_volume, t.sales_volume + t.actual_sales_volume as sales_volume")
                .innerJoin("?_seckill_activity a ON a.id=t.seckill_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.id", id)
                .eq("a.status", SeckillEnum.STATUS_CONDUCT.getCode())
                .le("a.start_time", TimeUtils.timestamp())
                .ge("a.end_time", TimeUtils.timestamp())
                .last("limit 1");

        SeckillDetailVo detailVo = seckillGoodsMapper.selectJoinOne(SeckillDetailVo.class, mpjQueryWrapper);

        Assert.notNull(detailVo, "当前秒杀活动已结束");

        // 秒杀商品规格
        List<SeckillGoodsSku> goodsSkuList = seckillGoodsSkuMapper.selectList(new QueryWrapper<SeckillGoodsSku>()
                .eq("seckill_gid", detailVo.getSeckillGid())
                .orderByDesc("id"));

        List<SeckillSkuVo> skuList = new LinkedList<>();
        for (SeckillGoodsSku goodsSku : goodsSkuList) {
            SeckillSkuVo skuVo = new SeckillSkuVo();
            BeanUtils.copyProperties(goodsSku, skuVo);
            skuList.add(skuVo);
        }
        detailVo.setGoodsSku(skuList);

        // 更新秒杀商品浏览量
        SeckillGoods seckillGoods = seckillGoodsMapper.selectOne(new QueryWrapper<SeckillGoods>()
                .eq("id", detailVo.getSeckillGid())
                .last("limit 1"));
        seckillGoods.setActualBrowseVolume(seckillGoods.getActualBrowseVolume() + 1);
        seckillGoodsMapper.updateById(seckillGoods);

        return detailVo;
    }

    /**
     * 更新秒杀商品销量
     *
     * @author mjf
     * @param skuId Integer
     * @param order Order
     */
    @Override
    public void updateSeckillGoodsSales(Order order, Integer skuId) {
        Integer seckillId = order.getSeckillId();
        if (seckillId != null  && seckillId > 0) {
            SeckillGoodsSku seckillGoodsSku = seckillGoodsSkuMapper.selectOne(new QueryWrapper<SeckillGoodsSku>()
                    .eq("seckill_id", seckillId)
                    .eq("sku_id", skuId)
                    .last("limit 1"));

            if (seckillGoodsSku != null) {
                seckillGoodsSku.setSalesAmount(seckillGoodsSku.getSalesAmount().add(order.getGoodsMoney()));
                seckillGoodsSku.setSalesVolume(seckillGoodsSku.getSalesVolume() + order.getGoodsNum());
                seckillGoodsSku.setClosingOrder(seckillGoodsSku.getClosingOrder() + 1);
                seckillGoodsSkuMapper.updateById(seckillGoodsSku);

                SeckillGoods seckillGoods = seckillGoodsMapper.selectOne(new QueryWrapper<SeckillGoods>()
                        .eq("id", seckillGoodsSku.getSeckillGid())
                        .last("limit 1"));

                seckillGoods.setActualSalesVolume(seckillGoods.getActualSalesVolume() + order.getGoodsNum());
                seckillGoodsMapper.updateById(seckillGoods);
            }
        }
    }
}
