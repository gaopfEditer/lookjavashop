package com.mdd.common.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品评论实体")
public class GoodsComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer Id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("订单商品ID")
    private Integer orderGoodsId;

    @ApiModelProperty("商品产品ID")
    private Integer goodsId;

    @ApiModelProperty("商品属性ID")
    private Integer goodsSkuId;

    @ApiModelProperty("商品评分")
    private Integer goodsScore;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("是否显示")
    private Integer isShow;

    @ApiModelProperty("是否回复")
    private Integer isReply;

    @ApiModelProperty("回复内容")
    private String replyContent;

    @ApiModelProperty("描述评分")
    private Integer describeScore;

    @ApiModelProperty("物流评分")
    private Integer serviceScore;

    @ApiModelProperty("物流评分")
    private Integer logisticsScore;

    @ApiModelProperty("评论图片")
    private String images;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;

}
