package com.mdd.admin.vo.marketing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("营销秒杀列表Vo")
public class MarketingSeckillListedVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "活动ID")
    private Integer id;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "结束时间")
    private Long goodsNum;

    @ApiModelProperty(value = "秒杀订单数")
    private Long closingOrder;

    @ApiModelProperty(value = "秒杀销售量")
    private Long salesVolume;

    @ApiModelProperty(value = "秒杀销售额")
    private BigDecimal salesAmount;

    @ApiModelProperty(value = "活动状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
