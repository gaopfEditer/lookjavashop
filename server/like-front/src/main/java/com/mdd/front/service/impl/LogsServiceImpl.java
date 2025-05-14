package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.log.LogEarnings;
import com.mdd.common.entity.log.LogMoney;
import com.mdd.common.enums.LogEarningsEnum;
import com.mdd.common.enums.LogMoneyEnum;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.front.service.ILogsService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.distribution.DistributionEarningsRecordVo;
import com.mdd.front.vo.recharge.RechargeRecordVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class LogsServiceImpl implements ILogsService {

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Override
    public PageResult<RechargeRecordVo> userMoney(PageValidate pageValidate, Integer userId, Integer type) {
        Integer pageNo   = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        QueryWrapper<LogMoney> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("id");
        if (type > 0) {
            queryWrapper.eq("action", type);
        }

        IPage<LogMoney> iPage = logMoneyMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<RechargeRecordVo> list = new LinkedList<>();
        for (LogMoney logMoney : iPage.getRecords()) {
            RechargeRecordVo vo = new RechargeRecordVo();

            vo.setId(logMoney.getId());
            vo.setAction(logMoney.getAction());
            vo.setOrderAmount(logMoney.getChangeAmount());
            vo.setTips(LogMoneyEnum.getMsgByCode(logMoney.getChangeType()));
            vo.setCreateTime(TimeUtils.timestampToDate(logMoney.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    @Override
    public PageResult<DistributionEarningsRecordVo> userEarnings(PageValidate pageValidate, Integer userId, Integer type) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        QueryWrapper<LogEarnings> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("id");
        if (type > 0) {
            queryWrapper.eq("action", type);
        }

        IPage<LogEarnings> iPage = logEarningsMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<DistributionEarningsRecordVo> list = new LinkedList<>();
        for (LogEarnings logEarnings : iPage.getRecords()) {
            DistributionEarningsRecordVo vo = new DistributionEarningsRecordVo();
            vo.setId(logEarnings.getId());
            vo.setAction(logEarnings.getAction());
            vo.setOrderAmount(logEarnings.getChangeAmount());
            vo.setTips(LogEarningsEnum.getMsgByCode(logEarnings.getChangeType()));
            vo.setCreateTime(TimeUtils.timestampToDate(logEarnings.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

}
