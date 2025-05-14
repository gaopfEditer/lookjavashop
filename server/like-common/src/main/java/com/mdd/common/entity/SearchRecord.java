package com.mdd.common.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("搜索记录实体类")
public class SearchRecord {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("关键字搜索")
    private String keyword;

    @ApiModelProperty("次数")
    private Integer count;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除状态: [1=是,0=否]")
    private Long isDelete;

}
