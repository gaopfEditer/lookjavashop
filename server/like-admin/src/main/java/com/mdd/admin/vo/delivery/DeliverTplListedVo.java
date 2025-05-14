package com.mdd.admin.vo.delivery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("快递模板列表Vo")
public class DeliverTplListedVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "计费方式: [0=件, 1=体积,  2=重量]")
    private Integer type;

    @ApiModelProperty(value = "计费描述")
    private String typeDesc;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
