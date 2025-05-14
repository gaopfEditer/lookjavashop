package com.mdd.admin.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

@Data
@ApiModel("订单售后列表Vo")
public class OrderAfterListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("售后单号")
    private String afterSn;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("用户编号")
    private String userSn;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("买家昵称")
    private String nickname;

    @ApiModelProperty("退款数量")
    private Integer refundNum;

    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney;

    @ApiModelProperty("实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty("售后类型描述")
    private String afterTypeMsg;

    @ApiModelProperty("退款类型描述")
    private String refundTypeMsg;

    @ApiModelProperty("售后状态描述")
    private String afterStatusMsg;

    @ApiModelProperty("子级状态描述")
    private String subStatusMsg;

    @ApiModelProperty("售后类型: [1=整单退款 2=商品售后]")
    private Integer afterType;

    @ApiModelProperty("退款类型: [1=仅退款 2=退货退款]")
    private Integer refundType;

    @ApiModelProperty("售后状态: [1=售后中 2=售后成功 3=售后关闭]")
    private Integer afterStatus;

    @ApiModelProperty("售后子状态: [11-买家申请售后,待商家同意 12-商家同意售后申请,待买家退货 13-买家已退货,待商家处理 14-商家确认收货,等待退款 15-商家同意售后申请,等待退款 21-商家已确认退款,售后成功  31-买家撤销申请32-商家已拒绝售后申请 33-商家拒绝收货]")
    private Integer subStatus;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("退款按钮")
    private Map<String, Boolean> afterBtn;

    @ApiModelProperty("商品信息")
    private Object goodsInfo;

}
