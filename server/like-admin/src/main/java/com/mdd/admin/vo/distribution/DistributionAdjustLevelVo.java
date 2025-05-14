package com.mdd.admin.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("分销等级调整详情Vo")
public class DistributionAdjustLevelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Integer id;

    @ApiModelProperty("用户编码")
    private Integer sn;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户头像")
    private String avatar;

    @ApiModelProperty("等级id")
    private Integer levelId;

    @ApiModelProperty("等级描述")
    private String levelName;

    @ApiModelProperty("等级信息")
    private List<levelData> levelData;

    @Data
    public static class levelData {
        @ApiModelProperty("分销等级ID")
        private Integer id;

        @ApiModelProperty("等级名称")
        private String name;

        @ApiModelProperty("等级级别")
        private Integer weights;
    }



}
