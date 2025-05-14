package com.mdd.admin.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("订单售后搜索")
public class OrderAfterSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("筛选类型: 0=全部, 1=售后中, 2=售后成功, 3=售后关闭")
    private Integer type = 0;

    @ApiModelProperty("售后单号")
    private String afterSn;

    @ApiModelProperty("订单单号")
    private String orderSn;

    @ApiModelProperty("买家昵称")
    private String nickname;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("退款类型 [1=仅退款 2=退货退款]")
    private Integer refundType;

    @ApiModelProperty("售后类型 [1=整单退款 2=商品售后]")
    private Integer afterType;

    @ApiModelProperty("售后子状态: [11-买家申请售后,待商家同意 12-商家同意售后申请,待买家退货 13-买家已退货,待商家处理 14-商家确认收货,等待退款 15-商家同意售后申请,等待退款 21-商家已确认退款,售后成功  31-买家撤销申请32-商家已拒绝售后申请 33-商家拒绝收货]")
    private Integer subStatus;

    @ApiModelProperty("订单状态: [1=待付款, 2=待发货, 3=待收货, 4=已完成, 5=已取消]")
    private Integer orderStatus;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

}
