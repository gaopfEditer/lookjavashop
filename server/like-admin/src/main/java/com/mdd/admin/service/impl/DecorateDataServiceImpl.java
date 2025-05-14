package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IDecorateDataService;
import com.mdd.admin.vo.decorate.DecorateDataArticleVo;
import com.mdd.admin.vo.decorate.DecorateDataCouponVo;
import com.mdd.admin.vo.decorate.DecorateDataGoodsVo;
import com.mdd.admin.vo.decorate.DecorateDataSeckillVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.article.Article;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.seckill.SeckillGoods;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.mapper.article.ArticleMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.seckill.SeckillGoodsMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 装修数据服务实现类
 */
@Service
public class DecorateDataServiceImpl implements IDecorateDataService {

    @Resource
    ArticleMapper articleMapper;

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    CouponMapper couponMapper;

    @Resource
    SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 获取文章数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateArticleDataVo>
     */
    @Override
    public List<DecorateDataArticleVo> article(Integer limit) {
        List<Article> articles = articleMapper.selectList(new QueryWrapper<Article>()
                .eq("is_show", 1)
                .eq("is_delete", 0)
                .orderByDesc("id")
                .last("limit " + limit));

        List<DecorateDataArticleVo> list = new LinkedList<>();
        for (Article article : articles) {
            DecorateDataArticleVo vo = new DecorateDataArticleVo();
            BeanUtils.copyProperties(article, vo);
            vo.setImage(UrlUtils.toAbsoluteUrl(article.getImage()));
            vo.setCreateTime(TimeUtils.timestampToDate(article.getCreateTime()));
            list.add(vo);
        }

        return list;
    }

    /**
     * 获取商品数据
     *
     * @param limit 条数
     * @param type 类型: hot=热门,news=最新
     * @return List<DecorateDataGoodsVo>
     */
    @Override
    public List<DecorateDataGoodsVo> goods(Integer limit, String type) {
        QueryWrapper<Goods> queryWrapper =new QueryWrapper<Goods>()
                .select("id,name,image,sales_num,min_price,max_price")
                .eq("status", 1)
                .eq("is_delete", 0)
                .last("limit " + limit);

        if (type.equals("hot")) {
            queryWrapper.orderByDesc(Arrays.asList("sales_num", "click_num", "id"));
        } else {
            queryWrapper.orderByDesc("id");
        }

        List<Goods> goodsList = goodsMapper.selectList(queryWrapper);

        List<DecorateDataGoodsVo> list = new LinkedList<>();
        for (Goods goods : goodsList) {
            DecorateDataGoodsVo vo = new DecorateDataGoodsVo();
            BeanUtils.copyProperties(goods, vo);
            vo.setImage(UrlUtils.toAbsoluteUrl(goods.getImage()));
            list.add(vo);
        }

        return list;
    }

    /**
     * 优惠券数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateDataCouponVo>
     */
    @Override
    public List<DecorateDataCouponVo> coupon(Integer limit) {
        // 条件构造
        QueryWrapper<Coupon> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("is_delete", 0)
                .in("status", Arrays.asList(1, 2, 3))
                .last("limit " + limit)
                .orderByDesc("id");

        // 发起查询
        List<Coupon> couponList = couponMapper.selectList(queryWrapper);
        List<DecorateDataCouponVo> list = new LinkedList<>();
        for (Coupon coupon: couponList) {
            DecorateDataCouponVo vo = new DecorateDataCouponVo();
            BeanUtils.copyProperties(coupon, vo);

            switch (coupon.getUseTimeType()) {
                case 1: // 固定时间
                    String useTimeEnd = TimeUtils.timestampToDate(coupon.getUseTimeEnd());
                    String useTimeStart = TimeUtils.timestampToDate(coupon.getUseTimeStart());
                    vo.setUseTimeMsg(useTimeStart + " ~ " + useTimeEnd);
                    break;
                case 2: // 当日起多少天内
                    vo.setUseTimeMsg("领取当日起" + coupon.getUseTime() + "天内可用");
                    break;
                case 3: // 次日起多少天内
                    vo.setUseTimeMsg("领取次日起" + coupon.getUseTime() + "天内可用");
                    break;
            }

            if (coupon.getConditionType().equals(CouponEnum.CONDITION_TYPE_NOT.getCode())) {
                vo.setDiscountContent("无门槛, " + "减" + coupon.getMoney() + "元");
            } else {
                vo.setDiscountContent("订单满" + coupon.getConditionMoney() + "元, " + "减" + coupon.getMoney() + "元");
            }

            vo.setUseGoodsTypeMsg(CouponEnum.getUseGoodsTypeMsg(coupon.getUseGoodsType()));
            vo.setCreateTime(TimeUtils.timestampToDate(coupon.getCreateTime()));
            list.add(vo);
        }

        return list;
    }

    /**
     * 获取秒杀数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateDataSeckillVo>
     */
    @Override
    public List<DecorateDataSeckillVo> seckill(Integer limit) {
        MPJQueryWrapper<SeckillGoods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(SeckillGoods.class)
                .select("G.name,G.image,G.max_price,SA.status")
                .innerJoin("?_goods G ON t.goods_id=G.id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_seckill_activity SA ON t.seckill_id=SA.id".replace("?_", GlobalConfig.tablePrefix))
                .eq("G.is_delete", 0)
                .eq("SA.is_delete", 0)
                .in("SA.status", Arrays.asList(1, 2, 3))
                .last("limit " + limit);

        List<DecorateDataSeckillVo> list = seckillGoodsMapper.selectJoinList(DecorateDataSeckillVo.class, mpjQueryWrapper);
        for (DecorateDataSeckillVo vo : list) {
            vo.setImage(UrlUtils.toAbsoluteUrl(vo.getImage()));
        }

        return list;
    }

}
