package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.withdraw.WithdrawApply;
import com.mdd.common.enums.ClientEnum;
import com.mdd.common.enums.LogEarningsEnum;
import com.mdd.common.enums.WithdrawEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.withdraw.WithdrawApplyMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IWithdrawService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.withdraw.WithdrawApplyValidate;
import com.mdd.front.vo.withdraw.WithdrawConfigVo;
import com.mdd.front.vo.withdraw.WithdrawDetailVo;
import com.mdd.front.vo.withdraw.WithdrawListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 提现服务实现类
 */
@Service
public class WithdrawServiceImpl implements IWithdrawService {

    @Resource
    UserMapper userMapper;

    @Resource
    WithdrawApplyMapper withdrawApplyMapper;

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;

    /**
     * 提现配置
     *
     * @author mjf
     * @param userId Integer
     * @param terminal Integer
     * @return WithdrawConfigVo
     */
    @Override
    public WithdrawConfigVo config(Integer userId, Integer terminal) {
        User user = userMapper.selectById(userId);

        Map<String, String> config = ConfigUtils.get("withdraw");

        WithdrawConfigVo vo = new WithdrawConfigVo();
        vo.setAbleWithdraw(user.getEarnings());
        vo.setMinMoney(new BigDecimal(config.getOrDefault("minMoney", "10")));
        vo.setMaxMoney(new BigDecimal(config.getOrDefault("maxMoney", "100")));
        vo.setServiceCharge(Double.valueOf(config.getOrDefault("serviceCharge", "1")));

        List<WithdrawConfigVo.type> typeVoList = new ArrayList<>();
        List<Integer> typeList = ListUtils.stringToListAsInt(config.getOrDefault("withdrawWay", "1"), ",");
        for (Integer type : typeList) {
            if (type.equals(WithdrawEnum.TYPE_WECHAT_CHANGE.getCode()) && terminal.equals(ClientEnum.H5.getCode())) {
                continue;
            }
            WithdrawConfigVo.type typeVo = new WithdrawConfigVo.type();
            typeVo.setName(WithdrawEnum.getTypeMsg(type));
            typeVo.setValue(type);
            typeVoList.add(typeVo);
        }
        vo.setType(typeVoList);

        return vo;
    }

    /**
     * 提现申请
     *
     * @author mjf
     * @param userId Integer
     * @param applyValidate WithdrawApplyValidate
     */
    @Override
    public void apply(Integer userId, WithdrawApplyValidate applyValidate) {
        // 校验能否申请
        this.__checkAbleApply(userId, applyValidate);

        // 提现配置
        Map<String, String> config = ConfigUtils.get("withdraw");

        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            // 手续费
            BigDecimal handlingFee = BigDecimal.ZERO;
            if (!applyValidate.getType().equals(WithdrawEnum.TYPE_BALANCE.getCode())) {
                double percentage = Double.parseDouble(config.getOrDefault("serviceCharge", "1"));
                handlingFee = BigDecimal.valueOf(percentage).multiply(applyValidate.getMoney())
                        .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            }

            String sn = withdrawApplyMapper.randMakeOrderSn("sn");
            String batchNo = withdrawApplyMapper.randMakeOrderSn("sn", "SJZZ");

            WithdrawApply model = new WithdrawApply();
            model.setSn(sn);
            model.setBatchNo(batchNo);
            model.setUserId(userId);
            model.setRealName(StringUtils.isNotNull(applyValidate.getRealName()) ? applyValidate.getRealName() : "");
            model.setAccount(StringUtils.isNotNull(applyValidate.getAccount()) ? applyValidate.getAccount() : "");
            model.setType(applyValidate.getType());
            model.setMoney(applyValidate.getMoney());
            model.setLeftMoney(applyValidate.getMoney().subtract(handlingFee));
            model.setMoneyQrCode(StringUtils.isNotNull(applyValidate.getMoneyQrCode()) ? UrlUtils.toRelativeUrl(applyValidate.getMoneyQrCode()) : "");
            model.setHandlingFee(handlingFee);
            model.setApplyRemark(StringUtils.isNotNull(applyValidate.getApplyRemark()) ? applyValidate.getApplyRemark() : "");
            model.setStatus(WithdrawEnum.STATUS_WAIT.getCode());
            model.setBank(StringUtils.isNotNull(applyValidate.getBank()) ? applyValidate.getBank() : "");
            model.setSubBank(StringUtils.isNotNull(applyValidate.getSubBank()) ? applyValidate.getSubBank() : "");
            model.setPayDesc("");
            model.setPaySearchResult("");
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            withdrawApplyMapper.insert(model);

            // 扣减用户可提现金额
            User user = userMapper.selectById(userId);
            user.setEarnings(user.getEarnings().subtract(applyValidate.getMoney()));
            user.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(user);

            // 增加流水记录
            logEarningsMapper.dec(user.getId(),
                    LogEarningsEnum.UE_DEC_WITHDRAW.getCode(),
                    applyValidate.getMoney(),
                    model.getId(),
                    model.getSn(),
                    LogEarningsEnum.UE_DEC_WITHDRAW.getMsg(),
                    null);

            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 提现列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param userId Integer
     * @return PageResult<WithdrawListVo>
     */
    @Override
    public PageResult<WithdrawListVo> list(PageValidate pageValidate, Integer userId) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        IPage<WithdrawApply> iPage = withdrawApplyMapper.selectPage(new Page<>(pageNo, pageSize),
                new QueryWrapper<WithdrawApply>()
                        .eq("user_id", userId)
                        .eq("is_delete", 0)
                        .orderByDesc("id"));

        List<WithdrawListVo> list = new ArrayList<>();
        for (WithdrawApply apply : iPage.getRecords()) {
            WithdrawListVo vo = new WithdrawListVo();
            BeanUtils.copyProperties(apply, vo);
            vo.setTypeMsg(WithdrawEnum.getTypeMsg(apply.getType()));
            vo.setStatusMsg(WithdrawEnum.getStatusMsg(apply.getStatus()));
            vo.setCreateTime(TimeUtils.timestampToDate(apply.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 提现详情
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     * @return WithdrawDetailVo
     */
    public WithdrawDetailVo detail(Integer userId, Integer id) {
        WithdrawApply apply = withdrawApplyMapper.selectOne(new QueryWrapper<WithdrawApply>()
                .eq("user_id", userId)
                .eq("id", id)
                .last("limit 1"));

        Assert.notNull(apply, "提现信息不存在");

        WithdrawDetailVo vo = new WithdrawDetailVo();
        BeanUtils.copyProperties(apply, vo);
        vo.setMoneyQrCode(UrlUtils.toAbsoluteUrl(vo.getMoneyQrCode()));
        vo.setTransferVoucher(UrlUtils.toAbsoluteUrl(vo.getTransferVoucher()));
        vo.setTypeMsg(WithdrawEnum.getTypeMsg(vo.getType()));
        vo.setStatusMsg(WithdrawEnum.getStatusMsg(vo.getStatus()));
        vo.setCreateTime(TimeUtils.timestampToDate(apply.getCreateTime()));
        return vo;
    }

    /**
     * 提现申请校验
     *
     * @author mjf
     * @param userId Integer
     * @param applyValidate WithdrawApplyValidate
     */
    private void  __checkAbleApply(Integer userId, WithdrawApplyValidate applyValidate) {
        // 校验提现金额
        User user = userMapper.selectById(userId);
        if (user.getEarnings().compareTo(applyValidate.getMoney()) < 0) {
            throw new OperateException("可提现金额不足");
        }

        // 最低提现金额
        Map<String, String> config = ConfigUtils.get("withdraw");
        BigDecimal minMoney = new BigDecimal(config.getOrDefault("minMoney", "10"));
        BigDecimal maxMoney = new BigDecimal(config.getOrDefault("maxMoney", "100"));
        if (applyValidate.getMoney().compareTo(minMoney) < 0) {
            throw new OperateException("最低提现" + minMoney + "元");
        }
        if (applyValidate.getMoney().compareTo(maxMoney) > 0) {
            throw new OperateException("最高提现" + maxMoney + "元");
        }

        if (applyValidate.getType().equals(WithdrawEnum.TYPE_BANK.getCode())
                || applyValidate.getType().equals(WithdrawEnum.TYPE_WECHAT_CODE.getCode())
                || applyValidate.getType().equals(WithdrawEnum.TYPE_ALI_CODE.getCode())) {
            Assert.notNull(applyValidate.getAccount(), "请输入提现账号");
            Assert.notNull(applyValidate.getRealName(), "请输入真实姓名");
        }

        if (applyValidate.getType().equals(WithdrawEnum.TYPE_WECHAT_CODE.getCode())
                || applyValidate.getType().equals(WithdrawEnum.TYPE_ALI_CODE.getCode())) {
            Assert.notNull(applyValidate.getMoneyQrCode(), "请上传收款码");
        }

        if (applyValidate.getType().equals(WithdrawEnum.TYPE_BANK.getCode())) {
            Assert.notNull(applyValidate.getBank(), "请输入提现银行");
            Assert.notNull(applyValidate.getSubBank(), "请输入银行支行");
        }
    }

}
