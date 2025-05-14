package com.mdd.admin.vo.order;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("订单导出列表Vo")
public class OrderManageExportVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号")
    @ExcelProperty(value = "订单编号",index = 0)
    private String orderSn;

    @ApiModelProperty("提货人")
    @ExcelProperty(value = "提货人",index = 1)
    private String contact;

    @ApiModelProperty("联系号码")
    @ExcelProperty(value = "联系号码",index = 2)
    private String mobile;

    @ApiModelProperty("商品信息")
    @ExcelProperty(value = "商品信息",index = 3)
    private String goodsInfo;

    @ApiModelProperty("核销状态")
    @ExcelProperty(value = "核销状态",index = 4)
    private String verificationStatusStr;

    @ApiModelProperty("订单状态")
    @ExcelProperty(value = "订单状态",index = 5)
    private String orderStatusStr;


    @ApiModelProperty("自提门店")
    @ExcelProperty(value = "自提门店",index = 6)
    private String selffetchShop;

    @ApiModelProperty("自提地址")
    @ExcelProperty(value = "自提地址",index = 7)
    private String selffetchShopAddress;
    @ApiModelProperty("下单时间")
    @ExcelProperty(value = "下单时间",index = 8)
    private String verificationTime;




//
//    @ApiModelProperty(value = "用户头像")
//    private String avatar;
//
//    @ApiModelProperty(value = "下单用户")
//    private String nickname;
//
//    @ApiModelProperty(value = "收货人")
//    private String addressContact;
//
//    @ApiModelProperty(value = "收货人电话")
//    private String addressMobile;
//
//    @ApiModelProperty(value = "收货人地址")
//    private String addressContent;
//
//    @ApiModelProperty(value = "实付金额")
//    private String payMoney;
//
//    @ApiModelProperty(value = "是否支付: [0=未支付, 1=已支付]")
//    private Integer payIs;
//
//    @ApiModelProperty(value = "订单状态: [0=待付款, 1=待发货, 2=待收货, 3=已完成, 4=已取消]")
//    private Integer orderStatus;
//
//    @ApiModelProperty(value = "支付状态描述")
//    private String payIsMsg;
//
//    @ApiModelProperty(value = "订单状态描述")
//    private String orderStatusMsg;
//
//    @ApiModelProperty(value = "取消按钮")
//    private Integer cancelBtn;
//
//    @ApiModelProperty(value = "确认按钮")
//    private Integer confirmBtn;
//
//    @ApiModelProperty(value = "删除按钮")
//    private Integer deleteBtn;
//
//    @ApiModelProperty(value = "发货按钮")
//    private Integer deliverBtn;
//
//    @ApiModelProperty(value = "物流按钮")
//    private Integer logisticsBtn;
//
//    @ApiModelProperty(value = "售后按钮")
//    private Integer refundBtn;
//
//    @ApiModelProperty("配送方式")
//    private Integer deliveryType;
//    @ApiModelProperty("核销状态:0-待核销;1-已核销;")
//    private Integer verificationStatus;
//    @ApiModelProperty("核销状态字符串")
//    private String verificationStatusStr;
//    @ApiModelProperty("核销时间")
//    private Long verificationTime;
//    @ApiModelProperty("核销时间字符串")
//    private String verificationTimeStr;
//    @ApiModelProperty("自提门店ID")
//    private Integer selffetchShopId;
//    @ApiModelProperty("自提门店名称")
//    private String selffetchShopName;
//    @ApiModelProperty("订单类型")
//    private Integer orderType;
//    @ApiModelProperty("订单类型id")
//    private String orderTypeStr;
//    @ApiModelProperty(value = "订单商品")
//    private List<OrderGoodsListVo> orderGoodsList;

}
