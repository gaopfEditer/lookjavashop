package com.mdd.front.vo.order;

import com.mdd.common.plugin.delivery.vo.KdTrackResultVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("订单物流Vo")
public class OrderLogisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "快递公司")
    private String expressName;

    @ApiModelProperty(value = "下单时间")
    private String createTime;

    @ApiModelProperty(value = "确认收货时间")
    private String confirmTime;

    @ApiModelProperty(value = "支付时间")
    private String payTime;

    @ApiModelProperty(value = "发货时间")
    private String expressTime;

    @ApiModelProperty(value = "物流单号")
    private String expressNo;

    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单状态描述")
    private String  orderStatusMsg;

    @ApiModelProperty(value = "订单商品总数量")
    private Integer orderGoodsNum;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "电话")
    private String mobile;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "物流错误")
    private String trackError;

    @ApiModelProperty(value = "物流轨迹")
    private List<KdTrackResultVo> track;


}
