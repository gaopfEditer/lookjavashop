package com.mdd.admin.vo.delivery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("快递公司Vo")
public class DeliverCompanyVo {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "图标")
    private String image;

    @ApiModelProperty(value = "快递编码")
    private String codeKd;

    @ApiModelProperty(value = "快递100编码")
    private String codeKd100;

    @ApiModelProperty(value = "快递鸟编码")
    private String codeKdniao;

    @ApiModelProperty(value = "排序编号")
    private String sort;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
