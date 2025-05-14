package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.binarywang.wxpay.bean.merchanttransfer.DetailsQueryResult;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.withdraw.WithdrawApply;
import com.mdd.common.enums.*;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.withdraw.WithdrawApplyMapper;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.TransferQueryRequest;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.TimeUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component("wechatTransferJob")
public class WechatTransferJob {

    @Resource
    UserMapper userMapper;

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Resource
    WithdrawApplyMapper withdrawApplyMapper;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;

    public void handle() {
        // 选择了商家转账到零钱再进行查询
        Map<String, String> config = ConfigUtils.get("withdraw");
        Integer transferWayConfig = Integer.parseInt(config.getOrDefault("transferWay", "1"));
        if (!transferWayConfig.equals(1)) {
            return;
        }

        // 提现申请列表
        List<WithdrawApply> applyList = withdrawApplyMapper.selectList(new QueryWrapper<WithdrawApply>()
                .eq("type", WithdrawEnum.TYPE_WECHAT_CHANGE.getCode())
                .eq("status", WithdrawEnum.STATUS_ING.getCode())
                .eq("is_delete", 0));

        if (applyList.size() <= 0) {
            return;
        }

        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            for (WithdrawApply apply : applyList) {
                // 商家转账到零钱查询
                TransferQueryRequest transferQueryRequest = new TransferQueryRequest();
                transferQueryRequest.setTerminal(ClientEnum.MNP.getCode());
                transferQueryRequest.setOutBatchNo(apply.getBatchNo());
                transferQueryRequest.setOutDetailNo(apply.getSn());
                // 提现转账到微信零钱查询
                DetailsQueryResult queryResult = WxPayDriver.transferQuery(transferQueryRequest);

                // 提现转账成功，更新提现申请
                if (queryResult.getDetailStatus().equals("SUCCESS")) {
                    apply.setStatus(WithdrawEnum.STATUS_SUCCESS.getCode());
                    apply.setPaySearchResult(queryResult.toString());
                    apply.setPaymentNo(queryResult.getDetailId());
                    apply.setPaymentTime(TimeUtils.dateToTimestamp(queryResult.getUpdateTime()));
                    apply.setUpdateTime(System.currentTimeMillis() / 1000);
                    withdrawApplyMapper.updateById(apply);
                }

                // 提现转账失败，更新提现申请
                if (queryResult.getDetailStatus().equals("FAIL")) {
                    apply.setStatus(WithdrawEnum.STATUS_FAIL.getCode());
                    apply.setPaySearchResult(queryResult.toString());
                    apply.setUpdateTime(System.currentTimeMillis() / 1000);
                    withdrawApplyMapper.updateById(apply);

                    // 退回提现佣金
                    User user = userMapper.selectById(apply.getUserId());
                    user.setEarnings(user.getEarnings().add(apply.getMoney()));
                    user.setUpdateTime(System.currentTimeMillis() / 1000);
                    userMapper.updateById(user);

                    // 流水记录
                    logEarningsMapper.add(user.getId(),
                            LogEarningsEnum.UE_INC_PAYMENT_FAIL.getCode(),
                            apply.getMoney(),
                            apply.getId(),
                            apply.getSn(),
                            LogEarningsEnum.UE_INC_PAYMENT_FAIL.getMsg(),
                            null);
                }
            }
            // 事务提交
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
        }
    }
}
