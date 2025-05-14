package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IMarketingSeckillService;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.seckill.SeckillActivity;
import com.mdd.common.entity.seckill.SeckillGoods;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.enums.SeckillEnum;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.seckill.SeckillActivityMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component("goodsCloseIsIsActivityJob")
public class GoodsCloseIsIsActivityJob {

    @Resource
    SeckillActivityMapper seckillActivityMapper;
    @Resource
    SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    IMarketingSeckillService iMarketingSeckillService;

    @Resource
    GoodsMapper goodsMapper;

    public void handle() {
        List<Integer> statusArr = new ArrayList<>();
        statusArr.add(SeckillEnum.STATUS_WAIT.getCode());
        statusArr.add(SeckillEnum.STATUS_CONDUCT.getCode());
        statusArr.add(SeckillEnum.STATUS_NOT.getCode());
        List<SeckillActivity> seckillActivityLists = seckillActivityMapper.selectList(
                new QueryWrapper<SeckillActivity>().eq("is_delete", 0).lt("end_time", System.currentTimeMillis() / 1000).in("status", statusArr).select("id"));
        if (seckillActivityLists.size() > 0) {
            for (SeckillActivity activity : seckillActivityLists) {
                iMarketingSeckillService.end(activity.getId());
            }
        }

        //处理旧数据
        List<Integer> statusIngArr = new ArrayList<>();
        statusIngArr.add(SeckillEnum.STATUS_CONDUCT.getCode());
        statusIngArr.add(SeckillEnum.STATUS_NOT.getCode());
        statusIngArr.add(SeckillEnum.STATUS_WAIT.getCode());
        List<SeckillActivity> seckillActivityIngLists = seckillActivityMapper.selectList(
                new QueryWrapper<SeckillActivity>().eq("is_delete", 0).in("status", statusIngArr).select("id"));
        if (seckillActivityIngLists.size() > 0) {
            List<Integer> activityIdArr = new ArrayList<>();
            for (SeckillActivity activityIng : seckillActivityIngLists) {
                activityIdArr.add(activityIng.getId());
            }
            List<SeckillGoods> seckillGoodsIngLists = seckillGoodsMapper.selectList(new QueryWrapper<SeckillGoods>().in("seckill_id", activityIdArr).select("goods_id"));

            List<Integer> goodsIdArr = new ArrayList<>();
            if (seckillGoodsIngLists.size() > 0) {
                for (SeckillGoods seckillGoods : seckillGoodsIngLists) {
                    goodsIdArr.add(seckillGoods.getGoodsId());
                }

                //不在活动范围内的状态都清除
                Goods goods = new Goods();
                goods.setIsActivity(0);
                goodsMapper.update(goods, new QueryWrapper<Goods>().notIn("id", goodsIdArr));
            }
        } else {
            Goods goods = new Goods();
            goods.setIsActivity(0);
            goodsMapper.update(goods, new QueryWrapper<Goods>().eq("is_delete", 0));
        }
    }
}
