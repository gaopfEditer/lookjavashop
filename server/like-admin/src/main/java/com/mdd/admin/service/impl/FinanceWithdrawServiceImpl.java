package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.binarywang.wxpay.bean.merchanttransfer.DetailsQueryResult;
import com.github.binarywang.wxpay.bean.merchanttransfer.TransferCreateResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IFinanceWithdrawService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawAuditValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawSearchValidate;
import com.mdd.admin.validate.finance.FinanceWithdrawTransferValidate;
import com.mdd.admin.vo.finance.FinanceWithdrawDetailVo;
import com.mdd.admin.vo.finance.FinanceWithdrawListVo;
import com.mdd.admin.vo.finance.FinanceWithdrawQueryVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.entity.withdraw.WithdrawApply;
import com.mdd.common.enums.LogEarningsEnum;
import com.mdd.common.enums.LogMoneyEnum;
import com.mdd.common.enums.WithdrawEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.withdraw.WithdrawApplyMapper;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.TransferQueryRequest;
import com.mdd.common.plugin.wechat.request.TransferRequest;
import com.mdd.common.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 提现记录服务实现类
 */
@Service
public class FinanceWithdrawServiceImpl implements IFinanceWithdrawService {

    @Resource
    UserMapper userMapper;

    @Resource
    UserAuthMapper userAuthMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Resource
    WithdrawApplyMapper withdrawApplyMapper;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;

    /**
     * 提现列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate FinanceWithdrawSearchValidate
     * @return PageResult<FinanceWithdrawListVo>
     */
    @Override
    public PageResult<FinanceWithdrawListVo> list(PageValidate pageValidate, FinanceWithdrawSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<WithdrawApply> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper
                .leftJoin("?_user U ON U.id=t.user_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.is_delete", 0)
                .orderByDesc("t.id");

        withdrawApplyMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "=:type@type:int",
                "datetime:startTime-endTime@t.create_time:long",
        });

        if (StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            mpjQueryWrapper.nested(wq -> wq
                    .like("U.nickname", keyword).or()
                    .like("U.sn", keyword).or()
                    .like("U.mobile", keyword));
        }

        Map<String, Object> extend = new LinkedHashMap<>();
        MPJQueryWrapper<WithdrawApply> allMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<WithdrawApply> waitMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<WithdrawApply> ingMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<WithdrawApply> successMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<WithdrawApply> failMpj = mpjQueryWrapper.clone();
        extend.put("all", withdrawApplyMapper.selectCount(allMpj.select("t.id")));
        extend.put("wait", withdrawApplyMapper.selectCount(waitMpj.select("t.id").eq("t.status", WithdrawEnum.STATUS_WAIT.getCode())));
        extend.put("ing", withdrawApplyMapper.selectCount(ingMpj.select("t.id").eq("t.status", WithdrawEnum.STATUS_ING.getCode())));
        extend.put("success", withdrawApplyMapper.selectCount(successMpj.select("t.id").eq("t.status", WithdrawEnum.STATUS_SUCCESS.getCode())));
        extend.put("fail", withdrawApplyMapper.selectCount(failMpj.select("t.id").eq("t.status", WithdrawEnum.STATUS_FAIL.getCode())));

        mpjQueryWrapper
                .selectAll(WithdrawApply.class)
                .select("U.id as user_id,U.sn as user_sn,U.nickname,U.avatar,U.username");

        // 提现状态
        if (StringUtils.isNotNull(searchValidate.getStatus()) && searchValidate.getStatus() > 0) {
            mpjQueryWrapper.eq("t.status", searchValidate.getStatus());
        }

        IPage<FinanceWithdrawListVo> iPage = withdrawApplyMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                FinanceWithdrawListVo.class,
                mpjQueryWrapper);

        for (FinanceWithdrawListVo vo : iPage.getRecords()) {
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setTypeMsg(WithdrawEnum.getTypeMsg(vo.getType()));
            vo.setStatusMsg(WithdrawEnum.getStatusMsg(vo.getStatus()));
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 提现详情
     *
     * @author mjf
     * @param id Integer
     * @return FinanceWithdrawDetailVo
     */
    @Override
    public FinanceWithdrawDetailVo detail(Integer id) {
        WithdrawApply apply = withdrawApplyMapper.selectById(id);
        Assert.notNull(apply, "提现信息不存在");

        User user = userMapper.selectById(apply.getUserId());

        FinanceWithdrawDetailVo vo = new FinanceWithdrawDetailVo();
        BeanUtils.copyProperties(apply, vo);
        vo.setNickname(user.getNickname());
        vo.setUsername(user.getUsername());
        vo.setUserSn(user.getSn());
        vo.setTypeMsg(WithdrawEnum.getTypeMsg(apply.getType()));
        vo.setStatusMsg(WithdrawEnum.getStatusMsg(apply.getStatus()));
        vo.setTransferVoucher(UrlUtils.toAbsoluteUrl(apply.getTransferVoucher()));
        vo.setCreateTime(TimeUtils.timestampToDate(apply.getCreateTime()));
        vo.setMoneyQrCode(UrlUtils.toAbsoluteUrl(vo.getMoneyQrCode()));
        return vo;
    }

    /**
     * 审核
     *
     * @author mjf
     * @param auditValidate FinanceWithdrawAuditValidate
     */
    @Override
    public void audit(FinanceWithdrawAuditValidate auditValidate) {
        WithdrawApply apply = withdrawApplyMapper.selectById(auditValidate.getId());
        Assert.notNull(apply, "提现信息不存在");

        // 待提现状态才可审核
        if (!apply.getStatus().equals(WithdrawEnum.STATUS_WAIT.getCode())) {
            throw new OperateException("不是待提现状态,不允许审核");
        }

        // 审核备注
        String auditRemark = StringUtils.isNotBlank(auditValidate.getAuditRemark()) ? auditValidate.getAuditRemark() : "";

        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            // 审核通过
            if (auditValidate.getStatus().equals(1)) {
                switch (apply.getType()) {
                    case 1:
                        // 提现到账户余额
                        this.__withdrawToBalance(apply.getId(), auditRemark);
                        break;
                    case 2:
                        // 提现到微信零钱
                        this.__withdrawToWechatChange(apply.getId(), auditRemark);
                        break;
                    case 3:
                    case 4:
                    case 5:
                        // 3=银行卡 4=微信收款码 5=支付宝收款码
                        this.__withdrawToCommon(apply.getId(), auditRemark);
                        break;
                }
            }

            // 审核拒绝
            if (auditValidate.getStatus().equals(0)) {
                // 更新提现状态
                apply.setStatus(WithdrawEnum.STATUS_FAIL.getCode());
                apply.setAuditRemark(auditRemark);
                apply.setUpdateTime(System.currentTimeMillis() / 1000);
                withdrawApplyMapper.updateById(apply);

                // 退回提现佣金, 增加佣金流水
                this.__fallbackMoney(apply, LogEarningsEnum.UE_INC_REFUSE_WITHDRAWAL.getCode());
            }

            // 提交事务
            transactionManager.commit(transactionStatus);

        } catch (OperateException e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMsg());
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 转账
     *
     * @author mjf
     * @param transferValidate FinanceWithdrawTransferValidate
     */
    @Override
    public void transfer(FinanceWithdrawTransferValidate transferValidate) {
        WithdrawApply apply = withdrawApplyMapper.selectById(transferValidate.getId());
        Assert.notNull(apply, "提现信息不存在");

        String transferVoucher = StringUtils.isNotBlank(transferValidate.getTransferVoucher()) ? transferValidate.getTransferVoucher() : "";
        String transferRemark = StringUtils.isNotBlank(transferValidate.getTransferRemark()) ? transferValidate.getTransferRemark() : "";

        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            // 转账成功
            if (transferValidate.getStatus().equals(1)) {
                if (StringUtils.isEmpty(transferVoucher)) {
                    throw new OperateException("请上传转账凭证");
                }
                if (StringUtils.isEmpty(transferRemark)) {
                    throw new OperateException("请填写转账说明");
                }
                // 更新状态
                apply.setStatus(WithdrawEnum.STATUS_SUCCESS.getCode());
                apply.setTransferVoucher(UrlUtils.toRelativeUrl(transferVoucher));
                apply.setTransferRemark(transferRemark);
                apply.setTransferTime(System.currentTimeMillis() / 1000);
                apply.setUpdateTime(System.currentTimeMillis() / 1000);
                withdrawApplyMapper.updateById(apply);
            }

            // 转账失败
            if (transferValidate.getStatus().equals(0)) {
                // 更新状态
                apply.setStatus(WithdrawEnum.STATUS_FAIL.getCode());
                apply.setTransferVoucher(UrlUtils.toRelativeUrl(transferVoucher));
                apply.setTransferRemark(transferRemark);
                apply.setUpdateTime(System.currentTimeMillis() / 1000);
                withdrawApplyMapper.updateById(apply);

                // 回退余额
                this.__fallbackMoney(apply, LogEarningsEnum.UE_INC_TRANSFER_FAIL.getCode());
            }

            // 提交事务
            transactionManager.commit(transactionStatus);
        }  catch (OperateException e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMsg());
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 查询零钱到账结果
     *
     * @author mjf
     * @param id Integer
     * @return FinanceWithdrawQueryVo
     */
    @Override
    public FinanceWithdrawQueryVo getWithdrawResult(Integer id) {
        WithdrawApply apply = withdrawApplyMapper.selectById(id);
        Assert.notNull(apply, "提现信息不存在");

        if (!apply.getStatus().equals(WithdrawEnum.STATUS_ING.getCode())) {
            throw new OperateException("非提现中状态无法查询结果");
        }

        if (!apply.getType().equals(WithdrawEnum.TYPE_WECHAT_CHANGE.getCode())) {
            throw new OperateException("非微信零钱提现方式无法查询结果");
        }

        // 用户授权信息
        UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .eq("user_id", apply.getUserId())
                .orderByAsc("terminal")
                .last("limit 1"));
        Assert.notNull(userAuth, "获取不到用户的openid");

        // 配置的零钱转账方式(当前仅支持商家转账到零钱)
        Map<String, String> config = ConfigUtils.get("withdraw");
        Integer transferWayConfig = Integer.parseInt(config.getOrDefault("transferWay", "1"));
        if (!transferWayConfig.equals(1)) {
            throw new OperateException("商家转账到零钱方式已关闭");
        }

        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            // 商家转账到零钱查询
            TransferQueryRequest transferQueryRequest = new TransferQueryRequest();
            transferQueryRequest.setTerminal(userAuth.getTerminal());
            transferQueryRequest.setOutBatchNo(apply.getBatchNo());
            transferQueryRequest.setOutDetailNo(apply.getSn());

            FinanceWithdrawQueryVo queryVo = new FinanceWithdrawQueryVo();
            queryVo.setResMsg("未知");

            // 提现转账到微信零钱查询
            DetailsQueryResult queryResult = WxPayDriver.transferQuery(transferQueryRequest);

            // 没有查询结果状态
            if (queryResult.getDetailStatus().isEmpty()) {
                String failReason = StringUtils.isNotBlank(queryResult.getFailReason()) ? queryResult.getFailReason() : "商家转账到零钱查询失败";
                throw new OperateException(failReason);
            }

            // 提现转账成功，更新提现申请
            if (queryResult.getDetailStatus().equals("SUCCESS")) {
                apply.setStatus(WithdrawEnum.STATUS_SUCCESS.getCode());
                apply.setPaySearchResult(queryResult.toString());
                apply.setPaymentNo(queryResult.getDetailId());
                apply.setPaymentTime(TimeUtils.dateToTimestamp(queryResult.getUpdateTime()));
                apply.setUpdateTime(System.currentTimeMillis() / 1000);
                withdrawApplyMapper.updateById(apply);

                queryVo.setResMsg("提现成功");
            }

            // 提现转账失败，更新提现申请
            if (queryResult.getDetailStatus().equals("FAIL")) {
                apply.setStatus(WithdrawEnum.STATUS_FAIL.getCode());
                apply.setPaySearchResult(queryResult.toString());
                apply.setUpdateTime(System.currentTimeMillis() / 1000);
                withdrawApplyMapper.updateById(apply);
                // 退回提现金额
                this.__fallbackMoney(apply, LogEarningsEnum.UE_INC_PAYMENT_FAIL.getCode());

                queryVo.setResMsg("提现失败");
            }

            // 提现转账处理中
            if (queryResult.getDetailStatus().equals("PROCESSING")) {
                queryVo.setResMsg("正在处理中");
            }

            // 提交事务
            transactionManager.commit(transactionStatus);
            return queryVo;
        } catch (OperateException e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMsg());
        } catch (Exception e) {
            // 事务回滚
            transactionManager.rollback(transactionStatus);
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 商家转账到微信零钱
     *
     * @author mjf
     * @param applyId Integer
     * @param auditRemark String
     */
    private void  __withdrawToWechatChange(Integer applyId, String auditRemark) throws WxPayException {
        WithdrawApply apply = withdrawApplyMapper.selectById(applyId);

        // 校验提现条件
        if (apply.getLeftMoney().compareTo(new BigDecimal("0.01")) < 0) {
            throw new OperateException("扣除手续费后提现金额不能小于0.01元");
        }

        Long count = withdrawApplyMapper.selectCount(new QueryWrapper<WithdrawApply>()
                .eq("user_id", apply.getUserId())
                .eq("type", WithdrawEnum.TYPE_WECHAT_CHANGE.getCode())
                .ne("status", WithdrawEnum.STATUS_WAIT.getCode())
                .ge("update_time", TimeUtils.today().get(0))
                .le("update_time", TimeUtils.today().get(1)));

        if (count >= 10) {
            throw new OperateException("同一天向同一个用户最多付款10次");
        }

        // 用户授权信息
        UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .eq("user_id", apply.getUserId())
                .orderByAsc("terminal")
                .last("limit 1"));
        Assert.notNull(userAuth, "获取不到用户的openid");

        // 配置的零钱转账方式(当前仅支持商家转账到零钱)
        Map<String, String> config = ConfigUtils.get("withdraw");
        Integer transferWayConfig = Integer.parseInt(config.getOrDefault("transferWay", "1"));
        if (!transferWayConfig.equals(1)) {
            throw new OperateException("商家转账到零钱方式已关闭");
        }

        String realName = null;
        // 商家转账到零钱
        if (apply.getLeftMoney().compareTo(new BigDecimal(2000)) >= 0) {
            if (StringUtils.isBlank(apply.getRealName())) {
                throw new OperateException("转账金额 >= 2000元，收款用户真实姓名必填");
            }
            realName = apply.getRealName();
        }

        List<TransferRequest.detailList> detailList = new LinkedList<>();
        TransferRequest.detailList detail = new TransferRequest.detailList();
        detail.setOpenid(userAuth.getOpenid());
        detail.setOutDetailNo(apply.getSn());
        detail.setTransferAmount(AmountUtil.yuan2Fen(apply.getLeftMoney().toPlainString()));
        detail.setTransferRemark("提现至微信零钱");
        detail.setUserName(realName);
        detailList.add(detail);

        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setTerminal(userAuth.getTerminal());
        transferRequest.setOutBatchNo(apply.getBatchNo());
        transferRequest.setTotalNum(1);
        transferRequest.setTotalAmount(AmountUtil.yuan2Fen(apply.getLeftMoney().toPlainString()));
        transferRequest.setDetailList(detailList);

        // 发起商家转账到零钱
        TransferCreateResult createResult = WxPayDriver.createTransfer(transferRequest);
        if (createResult.getCreateTime().isEmpty()) {
            throw new OperateException("提现批次受理失败");
        }

        // 批次受理成功，更新提现申请为提现中状态
        apply.setStatus(WithdrawEnum.STATUS_ING.getCode());
        apply.setAuditRemark(auditRemark);
        apply.setUpdateTime(System.currentTimeMillis() / 1000);
        apply.setPayDesc(createResult.toString());
        withdrawApplyMapper.updateById(apply);
    }

    /**
     * 提现失败-退回佣金
     *
     * @author mjf
     * @param apply WithdrawApply
     */
    private void __fallbackMoney(WithdrawApply apply, Integer changeType) {
        // 退回佣金
        User user = userMapper.selectById(apply.getUserId());
        user.setEarnings(user.getEarnings().add(apply.getMoney()));
        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(user);

        // 流水记录
        logEarningsMapper.add(user.getId(),
                changeType,
                apply.getMoney(),
                apply.getId(),
                apply.getSn(),
                LogEarningsEnum.getMsgByCode(changeType),
                null);
    }

    /**
     * 提现到余额
     *
     * @author mjf
     * @param applyId Integer
     * @param auditRemark String
     */
    private void __withdrawToBalance(Integer applyId, String auditRemark) {

        WithdrawApply apply = withdrawApplyMapper.selectById(applyId);

        // 增加用户余额
        User user = userMapper.selectById(apply.getUserId());
        user.setMoney(user.getMoney().add(apply.getMoney()));
        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(user);

        // 增加账户流水
        logMoneyMapper.add(
                user.getId(),
                LogMoneyEnum.BNW_INC_WITHDRAW.getCode(),
                apply.getMoney(),
                apply.getId(),
                apply.getSn(),
                "售后退还余额",
                null
        );

        // 更新提现状态
        apply.setStatus(WithdrawEnum.STATUS_SUCCESS.getCode());
        apply.setAuditRemark(auditRemark);
        apply.setUpdateTime(System.currentTimeMillis() / 1000);
        withdrawApplyMapper.updateById(apply);
    }

    /**
     * 提现至银行卡/微信收款码/支付宝收款
     *
     * @author mjf
     * @param applyId Integer
     * @param auditRemark String
     */
    private void __withdrawToCommon(Integer applyId, String auditRemark) {
        // 更新提现状态
        WithdrawApply model = withdrawApplyMapper.selectById(applyId);
        model.setStatus(WithdrawEnum.STATUS_ING.getCode());
        model.setAuditRemark(auditRemark);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        withdrawApplyMapper.updateById(model);
    }

}
