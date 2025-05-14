package com.mdd.common.mapper.log;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.core.basics.IBaseMapper;
import com.mdd.common.entity.log.LogEarnings;
import com.mdd.common.entity.user.User;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.SpringUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
 * 佣金变动Mapper
 */
@Mapper
public interface LogEarningsMapper extends IBaseMapper<LogEarnings> {

    /**
     * 增加(用户佣金增加后调用)
     *
     * @param userId 用户ID
     * @param changeType 变动类型
     * @param changeAmount 变动金额
     * @param sourceId 来源ID
     * @param sourceSn 来源订单
     * @param remark 备注信息
     * @param extra 预留字段
     */
    default void add(Integer userId, Integer changeType, BigDecimal changeAmount,
                     Integer sourceId, String sourceSn, String remark, String extra) {

        for (int i=0; i<=0; i++) {
            UserMapper userMapper = SpringUtils.getBean(UserMapper.class);
            User user = userMapper.selectById(userId);
            if (StringUtils.isNull(user)) {
                break;
            }

            LogEarnings logEarnings = new LogEarnings();
            logEarnings.setSn(this.randMakeOrderSn());
            logEarnings.setUserId(userId);
            logEarnings.setSourceId(sourceId);
            logEarnings.setSourceSn(sourceSn);
            logEarnings.setChangeType(changeType);
            logEarnings.setChangeAmount(changeAmount);
            logEarnings.setLeftAmount(user.getEarnings());
            logEarnings.setAction(1);
            logEarnings.setRemark(remark);
            logEarnings.setExtra(extra);
            logEarnings.setCreateTime(System.currentTimeMillis() / 1000);
            logEarnings.setUpdateTime(System.currentTimeMillis() / 1000);
            this.insert(logEarnings);
        }
    }

    /**
     * 减少(用户佣金扣减后调用)
     *
     * @param userId 用户ID
     * @param changeType 变动类型
     * @param changeAmount 变动金额
     * @param sourceId 来源ID
     * @param sourceSn 来源订单
     * @param remark 备注信息
     * @param extra 预留字段
     */
    default void dec(Integer userId, Integer changeType, BigDecimal changeAmount,
                     Integer sourceId, String sourceSn, String remark, String extra) {

        for (int i=0; i<=0; i++) {
            UserMapper userMapper = SpringUtils.getBean(UserMapper.class);
            User user = userMapper.selectById(userId);
            if (StringUtils.isNull(user)) {
                break;
            };

            LogEarnings logEarnings = new LogEarnings();
            logEarnings.setSn(this.randMakeOrderSn());
            logEarnings.setSourceId(sourceId);
            logEarnings.setUserId(userId);
            logEarnings.setSourceSn(sourceSn);
            logEarnings.setChangeType(changeType);
            logEarnings.setChangeAmount(changeAmount);
            logEarnings.setLeftAmount(user.getEarnings());
            logEarnings.setAction(2);
            logEarnings.setRemark(remark);
            logEarnings.setExtra(extra);
            logEarnings.setCreateTime(System.currentTimeMillis() / 1000);
            logEarnings.setUpdateTime(System.currentTimeMillis() / 1000);
            this.insert(logEarnings);
        }
    }

    /**
     * 生成唯一流水号
     *
     * @author mjf
     * @return String
     */
    default String randMakeOrderSn() {
        String date = TimeUtils.timestampToDate(System.currentTimeMillis()/1000, "yyyyMMddHHmmss");
        String sn;
        while (true) {
            sn = date + ToolUtils.randomInt(12);
            LogEarnings snModel = this.selectOne(
                    new QueryWrapper<LogEarnings>()
                            .select("id")
                            .eq("sn", sn)
                            .last("limit 1"));
            if (snModel == null) {
                break;
            }
        }
        return sn;
    }

}
