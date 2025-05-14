package com.mdd.front.service;


import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.withdraw.WithdrawApplyValidate;
import com.mdd.front.vo.withdraw.WithdrawConfigVo;
import com.mdd.front.vo.withdraw.WithdrawDetailVo;
import com.mdd.front.vo.withdraw.WithdrawListVo;

/**
 * 提现服务接口类
 */
public interface IWithdrawService {

    /**
     * 提现配置
     *
     * @author mjf
     * @param userId Integer
     * @param terminal Integer
     * @return WithdrawConfigVo
     */
    WithdrawConfigVo config(Integer userId, Integer terminal);

    /**
     * 提现申请
     *
     * @author mjf
     * @param userId Integer
     * @param applyValidate WithdrawApplyValidate
     */
    void apply(Integer userId, WithdrawApplyValidate applyValidate);

    /**
     * 提现详情
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     * @return WithdrawDetailVo
     */
    WithdrawDetailVo detail(Integer userId, Integer id);

    /**
     * 提现列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param userId Integer
     * @return PageResult<WithdrawListVo>
     */
    PageResult<WithdrawListVo> list(PageValidate pageValidate, Integer userId);
}
