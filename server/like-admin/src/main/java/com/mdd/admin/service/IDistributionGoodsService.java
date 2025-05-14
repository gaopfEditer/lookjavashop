package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsJoinValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsSearchValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsSetValidate;
import com.mdd.admin.vo.distribution.DistributionGoodsDetailVo;
import com.mdd.admin.vo.distribution.DistributionGoodsListedVo;
import com.mdd.common.core.PageResult;

/**
 * 分销商品服务接口类
 */
public interface IDistributionGoodsService {

    /**
     * 分销商品列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<DistributionGoodsListedVo>
     */
    PageResult<DistributionGoodsListedVo> list(PageValidate pageValidate, DistributionGoodsSearchValidate searchValidate);

    /**
     * 分销商品详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionGoodsDetailVo
     */
    DistributionGoodsDetailVo detail(Integer id);

    /**
     * 设置佣金
     *
     * @author mjf
     * @param setValidate DistributionGoodsSetValidate
     */
    void set(DistributionGoodsSetValidate setValidate);

    /**
     * 参与分销/不参与分销
     *
     * @author mjf
     * @param joinValidate DistributionGoodsJoinValidate
     */
    void join(DistributionGoodsJoinValidate joinValidate);

}
