package com.mdd.admin.validate.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

@Data
@ApiModel("订单搜索参数")
public class OrderSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单搜索类型")
    private Integer searchType;

    @ApiModelProperty(value = "订单搜素关键词")
    private String searchKeyword;

    @ApiModelProperty("搜索类型: [0=全部,1=待付款, 2=待发货, 3=待收货, 4=已完成, 5=已取消]")
    private Integer type;

    @ApiModelProperty(value = "用户搜索类型")
    private Integer userSearchType;

    @ApiModelProperty(value = "用户搜索关键词")
    private String searchUserKeyword;

    @ApiModelProperty(value = "订单来源")
    private Integer orderSource;

    @ApiModelProperty(value = "支付方式")
    private Integer payWay;

    @ApiModelProperty(value = "支付状态")
    private Integer payStatus;

    @ApiModelProperty(value = "搜索时间类型")
    private String searchTimeType;

    @ApiModelProperty(value = "开始时间")
    private Integer startTime;

    @ApiModelProperty(value = "结束时间")
    private Integer endTime;
    @ApiModelProperty(value = "配送类型")
    private Integer deliveryType;
    @ApiModelProperty(value = "核销码")
    private String pickupCode;

    @ApiModelProperty(value = "提货门店")
    private Integer selffetchShopId;
    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "核销状态")
    private Integer verificationStatus;
}
