package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("支付方式Vo")
public class SettingPaymentMethodVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "显示名称")
    private String showName;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "是否默认: 0=否, 1=是")
    private Integer isDefault;

    @ApiModelProperty(value = "启用状态: 0=否, 1=是")
    private Integer status;

}
