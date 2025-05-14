package com.mdd.front.vo.goods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "商品评价Vo")
public class CommentListDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer Id;

    @ApiModelProperty(value = "商品评分")
    private Integer goodsScore;

    @ApiModelProperty(value = "评价内容")
    private String content;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "描述评分")
    private Integer describeScore;

    @ApiModelProperty(value = "服务评分")
    private Integer serviceScore;

    @ApiModelProperty(value = "物流评分")
    private Integer logisticsScore;

    @ApiModelProperty(value = "评论图片")
    private List<String> images;

    @ApiModelProperty(value = "评论时间")
    private String createTime;
}
