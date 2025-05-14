package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.*;
import com.mdd.admin.vo.distribution.*;
import com.mdd.common.core.PageResult;

/**
 * 分销订单服务接口类
 */
public interface IDistributionOrderService {

    /**
     * 分销订单列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionOrderSearchValidate
     * @return PageResult<DistributionOrderListedVo>
     */
    PageResult<DistributionOrderListedVo> list(PageValidate pageValidate, DistributionOrderSearchValidate searchValidate);

}
