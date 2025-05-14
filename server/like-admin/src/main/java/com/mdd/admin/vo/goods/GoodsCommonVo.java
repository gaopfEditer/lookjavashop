package com.mdd.admin.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("公共商品列表Vo")
public class GoodsCommonVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品ID")
    private Integer id;

    @ApiModelProperty("商品编码")
    private String code;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品封面")
    private String image;

    @ApiModelProperty("规格类型: 1=单规格,2=多规格")
    private Integer specType;

    @ApiModelProperty("最低价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal maxPrice;

    @ApiModelProperty("价格范围")
    private String sellPrice;

    @ApiModelProperty("库存数量")
    private Integer totalStock;

    @ApiModelProperty("商品分类")
    private String category;

    @ApiModelProperty("是否秒杀: [0=否, 1=是]")
    private Integer isSeckill;

    @ApiModelProperty("商品状态: [0=下架, 1=上架]")
    private Integer status;

    @ApiModelProperty("商品状态描述")
    private String statusMsg;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("规格列表")
    private List<GoodsSkuVo> skuList;

}
