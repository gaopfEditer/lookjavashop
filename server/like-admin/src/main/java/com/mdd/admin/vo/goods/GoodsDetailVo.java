package com.mdd.admin.vo.goods;

import com.mdd.common.entity.goods.GoodsSku;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("商品详情Vo")
public class GoodsDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "货号")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;                    //名称

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "运费设置: 1=包邮, 2=运费模板")
    private Integer expressType;

    @ApiModelProperty(value = "运费模板")
    private Integer expressTemplateId;

    @ApiModelProperty(value = "虚拟销量")
    private Integer virtualSalesNum;

    @ApiModelProperty(value = "库存预警")
    private Integer stockWarning;

    @ApiModelProperty(value = "规格类型: 1=单规格, 2-多规格")
    private Integer specType;

    @ApiModelProperty(value = "排序编号")
    private Integer sort;

    @ApiModelProperty(value = "视频开关: [0=关闭, 1=开始]")
    private Integer videoStatus;

    @ApiModelProperty(value = "视频来源: [1=视频素材库, 2=视频链接]")
    private Integer videoSource;

    @ApiModelProperty(value = "视频封面")
    private String videoCover;

    @ApiModelProperty(value = "商品主图")
    private String video;

    @ApiModelProperty(value = "虚拟点击量")
    private Integer virtualClickNum;

    @ApiModelProperty(value = "商品销量")
    private Integer salesNum;

    @ApiModelProperty(value = "商品详情")
    private String content;

    @ApiModelProperty(value = "商品主图")
    private List<String> goodsImage;

    @ApiModelProperty(value = "商品分类ID")
    private List<Integer> categoryId;

    @ApiModelProperty(value = "规格列表")
    private List<SpecValueVo> specValue;

    @ApiModelProperty(value = "规格值列表")
    private List<GoodsSku> specValueList;

    @ApiModelProperty(value = "是否开启快递配送")
    private Integer isExpress;

    @ApiModelProperty(value = "是否开启上门自提")
    private Integer isSelffetch;

}
