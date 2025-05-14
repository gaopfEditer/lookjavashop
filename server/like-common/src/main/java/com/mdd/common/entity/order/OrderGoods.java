package com.mdd.common.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("订单商品实体")
public class OrderGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer orderId;

    @ApiModelProperty("ID")
    private Integer userId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("商品类型: [1=普通商品, 2=秒杀商品]")
    private Integer goodsType;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品货号")
    private String goodsCode;

    @ApiModelProperty("SKU_ID")
    private Integer goodsSkuId;

    @ApiModelProperty("SKU值名")
    private String goodsSkuValue;

    @ApiModelProperty("商品原价")
    private BigDecimal goodsOriginalPrice;

    @ApiModelProperty("购买价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("购买数量")
    private Integer goodsNum;

    @ApiModelProperty("订单总额")
    private BigDecimal money;

    @ApiModelProperty("商品总额")
    private BigDecimal goodsMoney;

    @ApiModelProperty("运费")
    private BigDecimal expressMoney;

    @ApiModelProperty("优惠金额")
    private BigDecimal couponMoney;

    @ApiModelProperty("应付金额")
    private BigDecimal needPayMoney;

    @ApiModelProperty("实付金额")
    private BigDecimal payMoney;

    @ApiModelProperty("售后状态: [1=无售后, 2=售后中, 3=售后完成, 4=售后失败]")
    private Integer afterSale;

    @ApiModelProperty("可评论: [0=否, 1=是]")
    private Integer canComment;

    @ApiModelProperty("已评论: [0=否, 1=是]")
    private Integer isComment;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;

    @ApiModelProperty("核销状态:0-待核销;1-已核销;")
    private Integer verificationStatus;
    @ApiModelProperty("核销时间")
    private Long verificationTime;



}
