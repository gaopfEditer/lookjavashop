package com.mdd.front.validate.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 获取地址地区id参数
 */
@Data
@ApiModel("获取用户地址地区id参数")
public class UserAddressRegionIdValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "省份参数丢失")
    @ApiModelProperty(value = "省份", required = true)
    private String province;

    @NotNull(message = "城市参数丢失")
    @ApiModelProperty(value = "城市", required = true)
    private String city;

    @NotNull(message = "地区参数丢失")
    @ApiModelProperty(value = "地区", required = true)
    private String district;


}
