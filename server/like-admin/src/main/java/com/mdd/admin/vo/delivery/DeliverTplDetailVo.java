package com.mdd.admin.vo.delivery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("快递模板详情Vo")
public class DeliverTplDetailVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "计费方式: [0=件, 1=体积,  2=重量]")
    private Integer type;

    @ApiModelProperty(value = "首数")
    private Integer firstNum;

    @ApiModelProperty(value = "首价")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续数")
    private Integer continueNum;

    @ApiModelProperty(value = "续价")
    private BigDecimal continuePrice;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private String createTime;
}
