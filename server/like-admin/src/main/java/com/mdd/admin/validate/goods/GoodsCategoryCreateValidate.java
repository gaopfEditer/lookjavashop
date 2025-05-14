package com.mdd.admin.validate.goods;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("商品分类创建参数")
public class GoodsCategoryCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "分类名称不能为空")
    @Length(min = 1, max = 32, message = "分类名称不能大于32个字符")
    @ApiModelProperty(value = "分类名称")
    private String name;

    @Min(value = 0,message = "pid参数必传且需大于0")
    @NotNull(message = "pid参数必传")
    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    @Length(max = 200, message = "分类图片过长不能超200个字符")
    @ApiModelProperty(value = "分类图片")
    private String image = "";

    @NotNull(message = "排序号不能为空")
    @DecimalMin(value = "0", message = "排序号值不能少于0")
    @ApiModelProperty(value = "排序编号")
    private Integer sort;

    @NotNull(message = "缺少isShow参数")
    @IntegerContains(values = {0, 1}, message = "isShow不是合法值")
    @ApiModelProperty(value = "是否显示: 0=否, 1=是")
    private Integer isShow;
}
