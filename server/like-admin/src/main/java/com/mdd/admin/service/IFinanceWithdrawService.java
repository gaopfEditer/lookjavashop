package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawAuditValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawSearchValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawTransferValidate;
import com.mdd.admin.vo.finance.FinanceWithdrawDetailVo;
import com.mdd.admin.vo.finance.FinanceWithdrawListVo;
import com.mdd.admin.vo.finance.FinanceWithdrawQueryVo;
import com.mdd.common.core.PageResult;

/**
 * 提现记录服务接口类
 */
public interface IFinanceWithdrawService {

    /**
     * 提现列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate FinanceWithdrawSearchValidate
     * @return PageResult<FinanceWithdrawListVo>
     */
    PageResult<FinanceWithdrawListVo> list(PageValidate pageValidate, FinanceWithdrawSearchValidate searchValidate);

    /**
     * 提现详情
     *
     * @author mjf
     * @param id Integer
     * @return FinanceWithdrawDetailVo
     */
    FinanceWithdrawDetailVo detail(Integer id);

    /**
     * 提现审核
     *
     * @author mjf
     * @param auditValidate FinanceWithdrawAuditValidate
     */
    void audit(FinanceWithdrawAuditValidate auditValidate);

    /**
     * 转账
     *
     * @author mjf
     * @param transferValidate FinanceWithdrawTransferValidate
     */
    void transfer(FinanceWithdrawTransferValidate transferValidate);

    /**
     * 提现查询(微信零钱)
     *
     * @author mjf
     * @param id Integer
     * @return FinanceWithdrawQueryVo
     */
    FinanceWithdrawQueryVo getWithdrawResult(Integer id);

}
