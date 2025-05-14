package com.mdd.front.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "售后详情Vo")
public class OrderAfterDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "售后ID")
    private Integer id;

    @ApiModelProperty("售后编码")
    private String afterSn;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单商品ID")
    private Integer orderGoodsId;

    @ApiModelProperty("售后商品")
    private List<OrderAfterGoodsDetailVo> orderAfterGoods;

    @ApiModelProperty("退款类型: [1-整单退款 2-商品售后]")
    private Integer afterType;

    @ApiModelProperty("退款原因")
    private String refundReason;

    @ApiModelProperty("退款说明")
    private String refundRemark;

    @ApiModelProperty("退款图片")
    private List<String> refundImage;

    @ApiModelProperty("退款类型: [1=仅退款 2=退货退款]")
    private Integer refundType;

    @ApiModelProperty("退款类型描述")
    private String refundTypeMsg;

    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney;

    @ApiModelProperty("实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty("快递公司名称")
    private String expressName;

    @ApiModelProperty("快递单号")
    private String invoiceNo;

    @ApiModelProperty("物流备注说明")
    private String expressRemark;

    @ApiModelProperty("买家退货时间")
    private Long expressTime;

    @ApiModelProperty("确认收货时间")
    private Long confirmTime;

    @ApiModelProperty(value = "收货人姓名")
    private String retreatConsignee;

    @ApiModelProperty(value = "收货人电话")
    private String retreatMobile;

    @ApiModelProperty(value = "收货人地区")
    private String retreatRegion;

    @ApiModelProperty(value = "收货人地址")
    private String retreatAddress;

    @ApiModelProperty(value = "退款编号")
    private String refundSn;

    @ApiModelProperty(value = "退款方式描述")
    private String refundPayWayMsg;

    @ApiModelProperty(value = "退款成功状态: [0=未退款 1=已退款]")
    private Integer refundSuccess;

    @ApiModelProperty("售后状态: [1=申请中, 2=售后成功, 3=售后关闭]")
    private Integer afterStatus;

    @ApiModelProperty("售后子状态: [11-买家申请售后,待商家同意 12-商家同意售后申请,待买家退货 13-买家已退货,待商家处理 14-商家确认收货,等待退款 15-商家同意售后申请,等待退款" +
            "21-商家已确认退款,售后成功" +
            "31-买家撤销申请；32-商家已拒绝售后申请 33-商家拒绝收货]")
    private Integer subStatus;

    @ApiModelProperty("售后状态描述")
    private String afterStatusMsg;

    @ApiModelProperty("售后状态描述")
    private String subStatusMsg;

    @ApiModelProperty("处理备注")
    private String handleRemark;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty(value = "撤销申请按钮: [0=不显示 1=显示]")
    private Integer cancelBtn;

    @ApiModelProperty(value = "重新申请按钮: [0=不显示 1=显示]")
    private Integer reapplyBtn;

    @ApiModelProperty(value = "填写单号按钮: [0=不显示 1=显示]")
    private Integer deliveryBtn;



}
