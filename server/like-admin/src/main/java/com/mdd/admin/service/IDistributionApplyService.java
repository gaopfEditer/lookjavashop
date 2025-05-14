package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionApplyAuditValidate;
import com.mdd.admin.validate.distribution.DistributionApplySearchValidate;
import com.mdd.admin.vo.distribution.DistributionApplyDetailVo;
import com.mdd.admin.vo.distribution.DistributionApplyListedVo;
import com.mdd.common.core.PageResult;

/**
 * 分销申请服务接口类
 */
public interface IDistributionApplyService {

    /**
     * 申请列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionApplySearchValidate
     * @return PageResult<DistributionApplyListedVo>
     */
    PageResult<DistributionApplyListedVo> list(PageValidate pageValidate, DistributionApplySearchValidate searchValidate);

    /**
     * 申请详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionApplyDetailVo
     */
    DistributionApplyDetailVo detail(Integer id);

    /**
     * 审核申请
     *
     * @author mjf
     * @param AuditValidate DistributionApplyAuditValidate
     */
    void audit(DistributionApplyAuditValidate AuditValidate);

    /**
     * 返回省市区字符串
     */
    String getRegion(Integer provinceId, Integer cityId, Integer districtId);
}
