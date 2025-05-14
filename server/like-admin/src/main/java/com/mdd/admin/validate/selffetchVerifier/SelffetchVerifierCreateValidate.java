package com.mdd.admin.validate.selffetchVerifier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import javax.validation.constraints.*;

@Data
@ApiModel("核销员创建参数")
public class SelffetchVerifierCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "selffetchShopId参数缺失")
    @ApiModelProperty(value = "自取门店ID")
    private Integer selffetchShopId;

    @NotNull(message = "userId参数缺失")
    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "核销员编号")
    private String sn;

    @NotNull(message = "name参数缺失")
    @ApiModelProperty(value = "核销员名称")
    private String name;

    @NotNull(message = "mobile参数缺失")
    @ApiModelProperty(value = "联系电话")
    @Length(min = 11, max = 11, message = "手机号只能为11位")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    private String mobile;

    @NotNull(message = "status参数缺失")
    @ApiModelProperty(value = "核销员状态:1-启用;0-停用;")
    private Integer status;

}
