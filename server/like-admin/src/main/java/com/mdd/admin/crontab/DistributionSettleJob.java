package com.mdd.admin.crontab;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IDistributionStoreService;
import com.mdd.admin.vo.distribution.DistributionConfigVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.distribution.DistributionConfig;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.*;
import com.mdd.common.mapper.distribution.DistributionConfigMapper;
import com.mdd.common.mapper.distribution.DistributionOrderMapper;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.mapper.order.OrderAfterMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("distributionSettleJob")
public class DistributionSettleJob {

    @Resource
    UserMapper userMapper;

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;

    @Resource
    DistributionConfigMapper distributionConfigMapper;

    @Resource
    IDistributionStoreService iDistributionStoreService;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;

    public void handle() {
        // 分销关闭，不处理
        DistributionConfigVo config = this.__distributionConfig();
        if (config.getOpen().equals(0)) {
            return;
        }

        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            // 待结算的佣金订单
            MPJQueryWrapper<DistributionOrder> mpjQueryWrapper = new MPJQueryWrapper<>();
            mpjQueryWrapper.selectAll(DistributionOrder.class);
            mpjQueryWrapper.eq("t.is_delete", 0);
            mpjQueryWrapper.eq("t.status", DistributionEnum.ORDER_UN_RETURNED.getCode());
            mpjQueryWrapper.eq("u.is_delete", 0);
            mpjQueryWrapper.eq("u.is_close", 0);
            mpjQueryWrapper.innerJoin("?_user u ON u.id = t.user_id".replace("?_", GlobalConfig.tablePrefix));
            mpjQueryWrapper.orderByDesc("t.id");
            mpjQueryWrapper.last("limit 100");
            List<DistributionOrder> distributionOrderList = distributionOrderMapper.selectList(mpjQueryWrapper);

            for (DistributionOrder distributionOrder : distributionOrderList) {
                // 校验是否符合结算条件
                if (!this.__checkAbleSettle(distributionOrder, config)) {
                    continue;
                }

                // 增加用户佣金
                User user = userMapper.selectById(distributionOrder.getUserId());
                user.setEarnings(user.getEarnings().add(distributionOrder.getEarnings()));
                user.setUpdateTime(System.currentTimeMillis() / 1000);
                userMapper.updateById(user);

                // 记录佣金日志
                logEarningsMapper.add(user.getId(),
                        LogEarningsEnum.UE_INC_DISTRIBUTION_SETTLE.getCode(),
                        distributionOrder.getEarnings(),
                        distributionOrder.getId(),
                        distributionOrder.getSn(),
                        LogEarningsEnum.UE_INC_DISTRIBUTION_SETTLE.getMsg(),
                        null);

                // 更新分销订单状态
                this.__updateDistributionOrder(distributionOrder.getId(), DistributionEnum.ORDER_RETURNED.getCode());

                // 更新分销商等级
                iDistributionStoreService.updateDistributionLevel(distributionOrder.getUserId());
            }

            // 事务提交
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
        }
    }

    /**
     * 校验是否满足结算条件
     *
     * @author mjf
     * @param distributionOrder DistributionOrder
     * @param config DistributionConfigVo
     * @return boolean
     */
    private boolean __checkAbleSettle(DistributionOrder distributionOrder, DistributionConfigVo config) {
        // 订单
        Order order = orderMapper.selectById(distributionOrder.getOrderId());

        // 订单结算时机 -  订单完成后多少天结算
        if (config.getSettlementTiming().equals(1)) {
            if (!order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_COMPLETED.getCode())
                    && !order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_CANCEL.getCode())) {
                return false;
            }

            // 订单是否在售后中或已完成售后
            List<OrderAfter> orderAfterList = orderAfterMapper.selectList(new QueryWrapper<OrderAfter>()
                    .eq("order_id", order.getId())
                    .eq("is_delete", 0));

            for (OrderAfter orderAfter : orderAfterList) {
                // 订单处于售后中, 不可结算
                if (orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
                    return false;
                }
                // 订单已售后成功,分销单置为失效状态,不可结算
                if (orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode())) {
                    this.__updateDistributionOrder(distributionOrder.getId(), DistributionEnum.ORDER_EXPIRED.getCode());
                    return false;
                }
            }
            // 是否在佣金结算时间内
            float settleTimeConfig = Float.parseFloat(config.getSettlementTime());
            Long settleTime = (long)settleTimeConfig * 24 * 60 * 60;
            return TimeUtils.timestamp() >= order.getConfirmTime() + settleTime;
        }

        return false;
    }

    /**
     * 更新分销订单
     *
     * @author mjf
     * @param id Integer
     * @param status Integer
     */
    private void __updateDistributionOrder(Integer id, Integer status) {
        DistributionOrder updateDistribution = new DistributionOrder();
        updateDistribution.setStatus(status);
        updateDistribution.setSettleTime(System.currentTimeMillis() / 1000);
        updateDistribution.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionOrderMapper.update(updateDistribution, new QueryWrapper<DistributionOrder>()
                .eq("id", id));
    }

    /**
     * 分销配置
     *
     * @author mjf
     * @return DistributionConfigVo
     */
    private DistributionConfigVo __distributionConfig() {
        List<DistributionConfig> list = distributionConfigMapper.selectList(new QueryWrapper<DistributionConfig>()
                .eq("is_delete", 0));

        Map<String, String> map = new LinkedHashMap<>();
        for (DistributionConfig config : list) {
            map.put(config.getName(), StringUtils.isNotNull(config.getValue()) ? config.getValue().trim() : null);
        }

        DistributionConfigVo vo = new DistributionConfigVo();
        vo.setOpen(Integer.parseInt(map.getOrDefault("open", "0")));
        vo.setLevel(Integer.parseInt(map.getOrDefault("level", "1")));
        vo.setIsSelfRebate(Integer.parseInt(map.getOrDefault("isSelfRebate", "0")));
        vo.setOpenCondition(Integer.parseInt(map.getOrDefault("openCondition", "0")));
        vo.setProtocolShow(Integer.parseInt(map.getOrDefault("protocolShow", "0")));
        vo.setProtocolContent(map.getOrDefault("protocolContent", ""));
        vo.setEarningsCalMethod(Integer.parseInt(map.getOrDefault("earningsCalMethod", "1")));
        vo.setSettlementTiming(Integer.parseInt(map.getOrDefault("settlementTiming", "1")));
        vo.setSettlementTime(map.getOrDefault("settlementTime", "0"));
        return vo;
    }

}
