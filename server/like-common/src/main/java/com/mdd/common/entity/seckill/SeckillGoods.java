package com.mdd.common.entity.seckill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("秒杀商品实体")
public class SeckillGoods {

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("活动ID")
    private Integer seckillId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("最低秒杀价格")
    private BigDecimal minSeckillPrice;

    @ApiModelProperty("最高秒杀价格")
    private BigDecimal maxSeckillPrice;

    @ApiModelProperty("虚拟销售量")
    private Integer salesVolume;

    @ApiModelProperty("虚拟浏览量")
    private Integer browseVolume;

    @ApiModelProperty("实际销售量")
    private Integer actualSalesVolume;

    @ApiModelProperty("实际浏览量")
    private Integer actualBrowseVolume;

}
