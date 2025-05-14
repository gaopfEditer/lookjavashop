package com.mdd.admin.validate.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("评论搜索参数")
public class GoodsCommentSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("查询类型: 0=全部, 1=待回复, 2=已回复")
    private Integer type;

    @ApiModelProperty("搜索类型")
    private String searchType;

    @ApiModelProperty("搜索关键词")
    private String keyword;

    @ApiModelProperty("评价等级: 0=全部, 1=好评, 2=中评, 3=差评")
    private Integer level;

    @ApiModelProperty("评级状态: 0=全部, 1=显示, 2=隐藏")
    private Integer status;

    @ApiModelProperty("评论开始时间")
    private Long startTime;

    @ApiModelProperty("评论结束时间")
    private Long endTime;

}
