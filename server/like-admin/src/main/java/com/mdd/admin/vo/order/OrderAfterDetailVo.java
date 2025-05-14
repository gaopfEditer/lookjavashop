package com.mdd.admin.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("订单售后详情Vo")
public class OrderAfterDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("售后ID")
    private Integer id;

    private Integer userId;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("售后单号")
    private String afterSn;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("售后状态: [1=售后中 2=售后成功 3=售后关闭]")
    private Integer afterStatus;

    @ApiModelProperty("售后子状态: [11-买家申请售后,待商家同意 12-商家同意售后申请,待买家退货 13-买家已退货,待商家处理 14-商家确认收货,等待退款 15-商家同意售后申请,等待退款 21-商家已确认退款,售后成功  31-买家撤销申请32-商家已拒绝售后申请 33-商家拒绝收货]")
    private Integer subStatus;

    @ApiModelProperty("售后状态描述")
    private String afterStatusMsg;

    @ApiModelProperty("售后子状态描述")
    private String subStatusMsg;

    @ApiModelProperty("退款方式描述")
    private String refundTypeMsg;

    @ApiModelProperty("退款类型描述")
    private String afterTypeMsg;

    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney;

    @ApiModelProperty("退款原因")
    private String refundReason;

    @ApiModelProperty("申请时间")
    private String createTime;

    @ApiModelProperty("退款说明")
    private String refundRemark;

    @ApiModelProperty("处理原因")
    private String handleRemark;

    @ApiModelProperty("退款凭证")
    private List<String> refundImage;

    @ApiModelProperty("退款按钮")
    private Map<String, Boolean> afterBtn;

    @ApiModelProperty("退款商品")
    private List<Map<String, Object>> refundGoodsList;

    @ApiModelProperty("订单日志")
    private List<Map<String, Object>> logOrderList;

}
