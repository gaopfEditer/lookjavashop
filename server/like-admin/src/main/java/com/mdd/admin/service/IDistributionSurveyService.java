package com.mdd.admin.service;


import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.distribution.DistributionSurveyDataVo;
import com.mdd.admin.vo.distribution.DistributionSurveyEarningsVo;
import com.mdd.admin.vo.distribution.DistributionSurveyFansVo;
import com.mdd.common.core.PageResult;


/**
 * 分销数据概括服务接口类
 */
public interface IDistributionSurveyService {

    /**
     * 分销数据概括
     *
     * @author mjf
     * @return DistributionSurveyDataVo
     */
    DistributionSurveyDataVo data();

    /**
     * 分销商收入排行
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return List<DistributionSurveyEarningsVo>
     */
    PageResult<DistributionSurveyEarningsVo> topEarnings(PageValidate pageValidate);

    /**
     * 下级人数排行
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return List<DistributionSurveyFansVo>
     */
    PageResult<DistributionSurveyFansVo> topFans(PageValidate pageValidate);
}
