package com.mdd.front.vo.seckill;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("秒杀列表Vo")
public class SeckillListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "秒杀活动ID")
    private Integer seckillId;

    @ApiModelProperty(value = "秒杀关联ID")
    private Integer seckillGId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodsId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "最低秒杀价格")
    private BigDecimal seckillPrice;

    @ApiModelProperty(value = "销量")
    private Integer salesVolume;


}
