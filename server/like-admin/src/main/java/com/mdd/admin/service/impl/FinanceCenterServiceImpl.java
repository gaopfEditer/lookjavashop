package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IFinanceCenterService;
import com.mdd.admin.vo.finance.FinanceCenterDataVo;
import com.mdd.common.entity.RechargeOrder;
import com.mdd.common.entity.RefundRecord;
import com.mdd.common.entity.log.LogMoney;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.LogMoneyEnum;
import com.mdd.common.enums.OrderAfterEnum;
import com.mdd.common.enums.OrderEnum;
import com.mdd.common.enums.RefundEnum;
import com.mdd.common.mapper.RechargeOrderMapper;
import com.mdd.common.mapper.RefundRecordMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.order.OrderAfterMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 财务中心服务实现类
 */
@Service
public class FinanceCenterServiceImpl implements IFinanceCenterService {

    @Resource
    OrderMapper orderMapper;

    @Resource
    RefundRecordMapper refundRecordMapper;

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    /**
     * 数据统计
     *
     * @author cjh
     * @return FinanceCenterDataVo
     */
    @Override
    public FinanceCenterDataVo data() {
        // 累计营业额(元)
        BigDecimal totalOrderAmount = orderMapper.sum("pay_money", new QueryWrapper<Order>().eq("pay_is", 1));

        // 累计成交订单(笔)
        long totalOrderNum = orderMapper.selectCount(new QueryWrapper<Order>().eq("pay_is", 1));

        // 已退款订单金额(元)
        BigDecimal totalRefundAmount = refundRecordMapper.sum("refund_amount", new QueryWrapper<RefundRecord>()
                .eq("refund_status", RefundEnum.REFUND_SUCCESS.getCode()));

        // 待退款订单金额(元)
        BigDecimal waitRefundAmount = refundRecordMapper.sum("refund_amount", new QueryWrapper<RefundRecord>()
                .in("refund_status", Arrays.asList(
                        RefundEnum.REFUND_ING.getCode(),
                        RefundEnum.REFUND_ERROR.getCode()
                )));

        FinanceCenterDataVo vo = new FinanceCenterDataVo();
        vo.setTotalOrderAmount(totalOrderAmount);
        vo.setTotalRefundAmount(totalRefundAmount);
        vo.setWaitRefundAmount(waitRefundAmount);
        vo.setTotalOrderNum(totalOrderNum);

        // 用户充值金额(元)
        BigDecimal userRechargeAmount = rechargeOrderMapper.sum("order_amount", new QueryWrapper<RechargeOrder>().eq("pay_status", 1));
        BigDecimal userUsableMoney = userMapper.sum("money", new QueryWrapper<User>().eq("is_delete", 0));
        // 用户已用余额
        BigDecimal userUsedMoney = logMoneyMapper.sum("change_amount", new QueryWrapper<LogMoney>()
                .in("change_type", LogMoneyEnum.getChangeTypeUserDec())
                .eq("action", 2));

        // 累计充值人数
        List<RechargeOrder> rechargeUser = rechargeOrderMapper.selectList(new QueryWrapper<RechargeOrder>()
                .select("DISTINCT user_id")
                .eq("pay_status", 1));

        vo.setUserRechargeAmount(userRechargeAmount);
        vo.setUserUsableMoney(userUsableMoney);
        vo.setUserUsedMoney(userUsedMoney);
        vo.setTotalRechargeNum(rechargeUser.size());
        return vo;
    }
}
