package com.mdd.front.vo.goods;

import com.mdd.common.entity.goods.GoodsSku;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "商品详情Vo")
public class GoodsDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "货号")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "销售价格(最低价)")
    private BigDecimal sellPrice;

    @ApiModelProperty(value = "划线价")
    private BigDecimal linePrice;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "运费设置: [1=包邮,2=运费模板]")
    private Integer expressType;

    @ApiModelProperty(value = "运费模板")
    private Integer expressTemplateId;

    @ApiModelProperty(value = "库存预警")
    private Integer stockWarning;

    @ApiModelProperty(value = "规格类型; [1=单规格,2=多规格]")
    private Integer specType;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "视频来源; [1=视频素材库,2=视频链接]")
    private Integer videoSource;

    @ApiModelProperty(value = "视频封面")
    private String videoCover;

    @ApiModelProperty(value = "商品主图")
    private String video;

    @ApiModelProperty(value = "商品销量")
    private Integer salesNum;

    @ApiModelProperty(value = "商品浏览量")
    private Integer clickNum;

    @ApiModelProperty(value = "库存")
    private Integer totalStock;

    @ApiModelProperty(value = "是否收藏")
    private Integer isCollect;

    @ApiModelProperty(value = "商品详情")
    private String content;

    @ApiModelProperty(value = "商品主图")
    private List<String> goodsImage;

    @ApiModelProperty(value = "商品分类id")
    private List<Integer> categoryId;

    @ApiModelProperty(value = "商品规格")
    private List<SpecValueVo> specValue;

    @ApiModelProperty(value = "商品规格")
    private List<GoodsSku> specValueList;

    @ApiModelProperty(value = "商品评论")
    private GoodsDetailCommentVo goodsComment;

    @ApiModelProperty(value = "默认运费")
    private BigDecimal defaultFreight;

    @ApiModelProperty(value = "默认配送地区")
    private GoodsAddressVo defaultAddress;

    @ApiModelProperty(value = "佣金信息")
    private GoodsDetailVo.earningsData earningsData;

    @Data
    public static class earningsData {
        @ApiModelProperty(value = "是否显示佣金")
        private Integer isShow;

        @ApiModelProperty(value = "商品最高佣金百分比")
        private Double percent;

        @ApiModelProperty(value = "商品最高可得佣金")
        private BigDecimal earnings;
    }

}
