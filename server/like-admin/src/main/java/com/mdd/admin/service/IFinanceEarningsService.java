package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceEarningsSearchValidate;
import com.mdd.admin.vo.finance.FinanceEarningsListVo;
import com.mdd.common.core.PageResult;

/**
 * 用户佣金记录服务接口类
 */
public interface IFinanceEarningsService {

    PageResult<FinanceEarningsListVo> list(PageValidate pageValidate, FinanceEarningsSearchValidate searchValidate);

}
