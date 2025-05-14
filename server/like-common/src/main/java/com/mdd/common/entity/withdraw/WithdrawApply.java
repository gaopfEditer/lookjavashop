package com.mdd.common.entity.withdraw;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("提现申请实体")
public class WithdrawApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("提现单号")
    private String sn;

    @ApiModelProperty("商家批次单号")
    private String batchNo;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("类型：1-钱包余额；2-微信零钱；3-银行卡;4-微信收款码;5-支付宝收款码")
    private Integer type;

    @ApiModelProperty("提现金额")
    private BigDecimal money;

    @ApiModelProperty("用户可得的金额(扣除手续费后)")
    private BigDecimal leftMoney;

    @ApiModelProperty("收款二维码")
    private String moneyQrCode;

    @ApiModelProperty("手续费")
    private BigDecimal handlingFee;

    @ApiModelProperty("申请备注")
    private String applyRemark;

    @ApiModelProperty("状态：1-待提现;2-提现中;3-提现成功;4-提现失败;")
    private Integer status;

    @ApiModelProperty("微信零钱支付信息")
    private String payDesc;

    @ApiModelProperty("微信零钱支付查询结果")
    private String paySearchResult;

    @ApiModelProperty("支付单号")
    private String paymentNo;

    @ApiModelProperty("支付时间")
    private Long paymentTime;

    @ApiModelProperty("审核备注")
    private String auditRemark;

    @ApiModelProperty("转账凭证")
    private String transferVoucher;

    @ApiModelProperty("转账时间")
    private Long transferTime;

    @ApiModelProperty("转账备注")
    private String transferRemark;

    @ApiModelProperty("提现银行")
    private String bank;

    @ApiModelProperty("提现银行支行")
    private String subBank;

    @ApiModelProperty("是否删除: 0=否, 1=是")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
