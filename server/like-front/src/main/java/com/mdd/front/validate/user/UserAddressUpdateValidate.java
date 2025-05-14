package com.mdd.front.validate.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户地址更新参数
 */
@Data
@ApiModel("用户地址更新参数")
public class UserAddressUpdateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "请选择地址")
    @ApiModelProperty(value = "地址ID", required = true)
    private Integer id;

    @NotNull(message = "请选择联系人")
    @ApiModelProperty(value = "联系人", required = true)
    private String contact;

    @NotNull(message = "请选择电话")
    @ApiModelProperty(value = "电话", required = true)
    private String mobile;

    @NotNull(message = "请选择省份")
    @ApiModelProperty(value = "省份", required = true)
    private Integer provinceId;

    @NotNull(message = "请选择城市")
    @ApiModelProperty(value = "城市", required = true)
    private Integer cityId;

    @NotNull(message = "请选择地区")
    @ApiModelProperty(value = "地区", required = true)
    private Integer districtId;

    @NotNull(message = "请输入门牌号")
    @ApiModelProperty(value = "详细地址", required = true)
    private String info;

    @NotNull(message = "请选择是否默认")
    @ApiModelProperty(value = "是否默认", required = true)
    private Integer isDefault;


}
