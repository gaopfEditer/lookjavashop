package com.mdd.admin.vo.decorate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("装修优惠券数据Vo")
public class DecorateDataCouponVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券名称")
    private String name;

    @ApiModelProperty(value = "优惠券编号")
    private String sn;

    @ApiModelProperty(value = "优惠券金额")
    private BigDecimal money;

    @ApiModelProperty(value = "优惠券内容")
    private String discountContent;

    @ApiModelProperty(value = "使用时间描述")
    private String useTimeMsg;

    @ApiModelProperty(value = "适用商品描述")
    private String useGoodsTypeMsg;

    @ApiModelProperty(value = "适用商品类型: 1=全部商品,2=指定商品,3=指定商品不可用")
    private Integer useGoodsType;

    @ApiModelProperty(value = "发放状态: [1=待发布,2=未开始, 3=进行中, 4=已结束]")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
