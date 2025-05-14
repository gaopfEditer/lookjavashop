package com.mdd.front.vo.goods;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "商品评价Vo")
public class GoodsDetailCommentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer Id;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "商品规格值")
    private String goodsSkuValue;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "评价内容")
    private String content;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "评分描述")
    private String scoreMsg;

    @ApiModelProperty(value = "商品评分")
    private Integer goodsScore;

    @ApiModelProperty(value = "描述评分")
    private Integer describeScore;

    @ApiModelProperty(value = "服务评分")
    private Integer serviceScore;

    @ApiModelProperty(value = "物流评分")
    private Integer logisticsScore;

    @ApiModelProperty(value = "评论图片")
    private String images;

    @ApiModelProperty(value = "评论图片列表")
    private List<String> imagesList;

    @ApiModelProperty(value = "评论时间")
    private String createTime;

    public void setScoreMsg(Integer goodsScore) {
        if (goodsScore >= 5) {
            this.scoreMsg = "好评";
        }

        if (goodsScore > 2 && goodsScore < 5) {
            this.scoreMsg = "中评";
        }

        if (goodsScore <= 2) {
            this.scoreMsg = "差评";
        }
    }
}
