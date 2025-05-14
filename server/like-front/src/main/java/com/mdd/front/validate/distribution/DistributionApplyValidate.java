package com.mdd.front.validate.distribution;

import com.mdd.common.validator.annotation.IDMust;
import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@ApiModel("分销申请参数")
public class DistributionApplyValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "请填写真实姓名")
    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;

    @NotNull(message = "请填写手机号")
    @Length(min = 11, max = 11, message = "手机号只能为11位")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    @ApiModelProperty(value = "手机号", required = true)
    private String mobile;

    @NotNull(message = "请选择省份")
    @ApiModelProperty(value = "省份ID", required = true)
    private Integer province;

    @NotNull(message = "请选择城市")
    @ApiModelProperty(value = "城市ID", required = true)
    private Integer city;

    @NotNull(message = "请选择地区")
    @ApiModelProperty(value = "地区ID", required = true)
    private Integer district;

    @NotNull(message = "请填写申请原因")
    @ApiModelProperty(value = "申请原因", required = true)
    private String reason;

    @NotNull(message = "请同意分销协议")
    @IntegerContains(values = {1}, message = "请同意分销协议")
    @ApiModelProperty(value = "同意分销协议", required = true)
    private Integer agree;
}
