package com.mdd.admin.vo.setting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("腾讯地图的配置Vo")
public class SettingMapVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "腾讯地图的配置Key")
    private String tencentMapKey;


}
