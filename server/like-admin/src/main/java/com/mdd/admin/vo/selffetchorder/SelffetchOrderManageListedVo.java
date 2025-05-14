package com.mdd.admin.vo.selffetchorder;

import com.mdd.admin.vo.order.OrderGoodsListVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("订单列表Vo")
public class SelffetchOrderManageListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty("用户编号")
    private String userSn;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "下单用户")
    private String nickname;

    @ApiModelProperty(value = "收货人")
    private String addressContact;

    @ApiModelProperty(value = "收货人电话")
    private String addressMobile;

    @ApiModelProperty(value = "收货人地址")
    private String addressContent;

    @ApiModelProperty(value = "实付金额")
    private String payMoney;

    @ApiModelProperty(value = "是否支付: [0=未支付, 1=已支付]")
    private Integer payIs;

    @ApiModelProperty(value = "订单状态: [0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消]")
    private Integer orderStatus;

    @ApiModelProperty(value = "支付状态描述")
    private String payIsMsg;

    @ApiModelProperty(value = "订单状态描述")
    private String orderStatusMsg;

    @ApiModelProperty(value = "取消按钮")
    private Integer cancelBtn;

    @ApiModelProperty(value = "确认按钮")
    private Integer confirmBtn;

    @ApiModelProperty(value = "删除按钮")
    private Integer deleteBtn;

    @ApiModelProperty(value = "发货按钮")
    private Integer deliverBtn;

    @ApiModelProperty(value = "物流按钮")
    private Integer logisticsBtn;

    @ApiModelProperty(value = "售后按钮")
    private Integer refundBtn;


    @ApiModelProperty("核销状态:0-待核销;1-已核销;")
    private Integer verificationStatus;
    @ApiModelProperty("核销时间")
    private Long verificationTime;
    @ApiModelProperty("自提门店ID")
    private Integer selffetchShopId;
    @ApiModelProperty("自提门店名称")
    private String selffetchShopName;

    @ApiModelProperty(value = "订单商品")
    private List<OrderGoodsListVo> orderGoodsList;

}
