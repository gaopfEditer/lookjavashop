package com.mdd.front.service;

import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.seckill.SeckillDetailVo;
import com.mdd.front.vo.seckill.SeckillListVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.order.Order;


public interface ISeckillService {

    /**
     * 秒杀活动列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return PageResult<SeckillListedVo>
     */
    PageResult<SeckillListVo> list(PageValidate pageValidate);


    /**
     * 秒杀活动详情
     *
     * @author mjf
     * @param id Integer
     * @return SeckillDetailVo
     */
    SeckillDetailVo detail(Integer id);


    /**
     * 更新秒杀商品销量
     *
     * @author mjf
     * @param order Order
     * @param skuId Integer
     */
    void updateSeckillGoodsSales(Order order, Integer skuId);

}
