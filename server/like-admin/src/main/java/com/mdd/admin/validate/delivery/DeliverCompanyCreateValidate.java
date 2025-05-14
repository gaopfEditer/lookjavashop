package com.mdd.admin.validate.delivery;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("快递公司创建参数")
public class DeliverCompanyCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "快递名称不能为空")
    @Length(min = 2, max = 30, message = "快递名称必须在2~30个字符内")
    @ApiModelProperty("快递名称")
    private String name;

    @ApiModelProperty("快递图标")
    private String image;

    @ApiModelProperty("快递编码")
    @Length(max = 30, message = "快递编码不能超出30个字符")
    private String codeKd;

    @ApiModelProperty("快递100编码")
    @Length(max = 30, message = "快递100编码不能超出30个字符")
    private String codeKd100;

    @ApiModelProperty("快递鸟编码")
    @Length(max = 30, message = "快递鸟编码不能超出30个字符")
    private String codeKdniao;

    @NotNull(message = "排序号不能为空")
    @DecimalMin(value = "0", message = "排序号值不能少于0")
    @ApiModelProperty("排序编号")
    private Integer sort;

}
