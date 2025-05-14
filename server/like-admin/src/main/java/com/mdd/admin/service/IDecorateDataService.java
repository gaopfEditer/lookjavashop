package com.mdd.admin.service;

import com.mdd.admin.vo.decorate.DecorateDataArticleVo;
import com.mdd.admin.vo.decorate.DecorateDataCouponVo;
import com.mdd.admin.vo.decorate.DecorateDataGoodsVo;
import com.mdd.admin.vo.decorate.DecorateDataSeckillVo;

import java.util.List;

/**
 * 装修数据服务接口类
 */
public interface IDecorateDataService {

    /**
     * 获取文章数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateArticleDataVo>
     */
    List<DecorateDataArticleVo> article(Integer limit);

    /**
     * 获取商品数据
     *
     * @param limit 条数
     * @param type 类型: hot=热门,news=最新
     * @return List<DecorateDataGoodsVo>
     */
    List<DecorateDataGoodsVo> goods(Integer limit, String type);

    /**
     * 获取优惠券数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateDataCouponVo>
     */
    List<DecorateDataCouponVo> coupon(Integer limit);

    /**
     * 获取秒杀数据
     *
     * @author fzr
     * @param limit 条数
     * @return List<DecorateDataSeckillVo>
     */
    List<DecorateDataSeckillVo> seckill(Integer limit);

}
