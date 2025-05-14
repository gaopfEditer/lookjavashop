package com.mdd.admin.validate.delivery;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "运费模板更新参数")
public class DeliverTplUpdateValidate implements Serializable {
    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "ID")
    private Integer id;

    @NotEmpty(message = "模板名称不能为空")
    @Length(min = 2, max = 30, message = "模板名称必须在2~30个字符内")
    @ApiModelProperty(value = "名称")
    private String name;

    @NotNull(message = "缺少type参数")
    @IntegerContains(values = {0, 1, 2}, message = "type不在合法值内")
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
}
