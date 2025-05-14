package com.mdd.common.entity.goods;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.Version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("商品产品实体")
public class GoodsNew implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("货号")
    private String code;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("商品主图")
    private String image;

    @ApiModelProperty("运费设置: 1=包邮, 2=运费模板")
    private Integer expressType;

    @ApiModelProperty("运费模板")
    private Integer expressTemplateId;

    @ApiModelProperty("虚拟销量")
    private Integer virtualSalesNum;

    @ApiModelProperty("库存预警")
    private Integer stockWarning;

    @ApiModelProperty("规格类型: [1=单规格, 2=多规格]")
    private Integer specType;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("视频开关: [0=关闭, 1=开始]")
    private Integer videoStatus;

    @ApiModelProperty("视频来源: [1=视频素材库, 2=视频链接]")
    private Integer videoSource;

    @ApiModelProperty("视频封面")
    private String videoCover;

    @ApiModelProperty("商品主图")
    private String video;

    @ApiModelProperty("最小价")
    private BigDecimal minPrice;

    @ApiModelProperty("最大价")
    private BigDecimal maxPrice;

    @ApiModelProperty("最小划线价")
    private BigDecimal minLineationPrice;

    @ApiModelProperty("最大划线价")
    private BigDecimal maxLineationPrice;

    @ApiModelProperty("总库存")
    private Integer totalStock;

    @ApiModelProperty("虚拟点击量")
    private Integer virtualClickNum;

    @ApiModelProperty("商品销量")
    private Integer salesNum;

    @ApiModelProperty("商品浏览量")
    private Integer clickNum;

    @ApiModelProperty("商品详情")
    private String content;

    @ApiModelProperty("活动状态: 1=是, 0=否")
    private Integer isActivity;

    @ApiModelProperty("删除状态: 1=是, 0=否")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "是否开启快递配送")
    private Integer isExpress;

    @ApiModelProperty(value = "是否开启上门自提")
    private Integer isSelffetch;

    @Version
    private Integer version;

}
