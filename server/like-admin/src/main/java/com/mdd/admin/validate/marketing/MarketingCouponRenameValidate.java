package com.mdd.admin.validate.marketing;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("优惠券命名参数")
public class MarketingCouponRenameValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "ID")
    private Integer id;

    @NotNull(message = "name参数缺失")
    @Length(min = 1, max = 200, message = "优惠券名称最少1个字符,最大200个字符")
    @ApiModelProperty(value = "优惠券名称", required = true)
    private String name;

    @DecimalMin(value = "0", message = "发放数量不能少于0")
    @ApiModelProperty(value = "发放数量")
    private Integer sendTotal;

}
