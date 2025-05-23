package com.mdd.front.service;

import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.distribution.DistributionEarningsRecordVo;
import com.mdd.front.vo.recharge.RechargeRecordVo;

/**
 * 日志接口服务类
 */
public interface ILogsService {

    PageResult<RechargeRecordVo> userMoney(PageValidate pageValidate, Integer userId, Integer type);

    PageResult<DistributionEarningsRecordVo> userEarnings(PageValidate pageValidate, Integer userId, Integer type);
}
