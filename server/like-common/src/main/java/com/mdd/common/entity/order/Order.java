package com.mdd.common.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("订单记录实体")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("订单编码")
    private String orderSn;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("订单类型: [1=普通订单]")
    private Integer orderType;

    @ApiModelProperty("订单来源: [1=微信小程序]")
    private Integer orderSource;

    @ApiModelProperty("订单状态：1=待付款, 2=待发货, 3=待收货, 4=已完成, 5=已取消")
    private Integer orderStatus;

    @ApiModelProperty("支付状态：0-已支付；1-已支付")
    private Integer payIs;

    @ApiModelProperty("支付方式: [0=未知, 1=余额, 2=微信, 3=支付宝]")
    private Integer payWay;

    @ApiModelProperty("支付时间")
    private Long payTime;

    @ApiModelProperty("支付流水号")
    private String transactionId;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("联系人")
    private String addressContact;

    @ApiModelProperty("联系电话")
    private String addressMobile;

    @ApiModelProperty("联系地址")
    private String addressContent;

    @ApiModelProperty("配送方式")
    private Integer deliveryType;

    @ApiModelProperty("总金额")
    private BigDecimal money;

    @ApiModelProperty("商品金额")
    private BigDecimal goodsMoney;

    @ApiModelProperty("运费金额")
    private BigDecimal expressMoney;

    @ApiModelProperty("优惠金额")
    private BigDecimal couponMoney;

    @ApiModelProperty("应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty("实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty("商品数量")
    private Integer goodsNum;

    @ApiModelProperty("发货状态：0-未发货；1-已发货")
    private Integer expressIs;

    @ApiModelProperty("发货时间")
    private Long expressTime;

    @ApiModelProperty("物流公司")
    private Integer expressId;

    @ApiModelProperty("物流单号")
    private String expressNo;

    @ApiModelProperty("收货时间")
    private Long confirmTime;

    @ApiModelProperty("取消时间")
    private Long cancelTime;

    @ApiModelProperty("售后截止时间")
    private Long afterDeadline;

    @ApiModelProperty("用户备注")
    private String userRemark;

    @ApiModelProperty("用户备注")
    private String shopRemark;

    @ApiModelProperty("秒杀活动ID")
    private Integer seckillId;

    @ApiModelProperty("优惠券领取记录ID")
    private Integer couponListId;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;

    @ApiModelProperty("删除状态: [1=是, 0=否]")
    private Integer isDelete;

    @ApiModelProperty("提货码")
    private String pickupCode;

    @ApiModelProperty("核销状态:0-待核销;1-已核销;")
    private Integer verificationStatus;
    @ApiModelProperty("核销时间")
    private Long verificationTime;
    @ApiModelProperty("自提门店ID")
    private Integer selffetchShopId;

    @ApiModelProperty("审核人id")
    private Integer verificationBy;

    @ApiModelProperty("审核人类型 1 admin 2. user")
    private Integer verificationByType;

    @ApiModelProperty("微信小程序发货信息录入")
    private Integer wechatMiniExpressSync;
    @ApiModelProperty("录入时间")
    private Long wechatMiniExpressSyncTime;
}
