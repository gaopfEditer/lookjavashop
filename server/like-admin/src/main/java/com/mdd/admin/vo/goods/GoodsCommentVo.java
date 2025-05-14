package com.mdd.admin.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品评论列表Vo")
public class GoodsCommentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品规格值")
    private String goodsSkuValue;

    @ApiModelProperty(value = "买家信息")
    private String nickname;

    @ApiModelProperty(value = "评分描述")
    private String scoreMsg;

    @ApiModelProperty(value = "服务评分")
    private Integer goodsScore;

    @ApiModelProperty(value = "评论图片")
    private Object images;

    @ApiModelProperty(value = "评论内容")
    private String content;

    @ApiModelProperty(value = "回复内容")
    private String replyContent;

    @ApiModelProperty(value = "是否显示: [0=否, 1=是]")
    private Integer isShow;

    @ApiModelProperty(value = "是否回复: [0=否, 1=是]")
    private Integer isReply;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    public void setScoreMsg(Integer goodsScore) {
        if (goodsScore >= 4) {
            this.scoreMsg = "好评";
        }

        if (goodsScore == 3) {
            this.scoreMsg = "中评";
        }

        if (goodsScore <= 2) {
            this.scoreMsg = "差评";
        }
    }
}
