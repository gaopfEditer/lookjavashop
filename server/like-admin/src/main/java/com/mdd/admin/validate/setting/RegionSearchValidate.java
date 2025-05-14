package com.mdd.admin.validate.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("腾讯地图区域搜索")
public class RegionSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "搜索关键字")
    @NotNull(message = "搜索关键字不能为空")
    private String keyword;

    @ApiModelProperty(value = "搜索区域")
    @NotNull(message = "搜索区域不能为空")
    private String boundary;

    @ApiModelProperty(value = "腾讯地图开发密钥")
    @NotNull(message = "腾讯地图开发密钥不能为空")
    private String key;

}
