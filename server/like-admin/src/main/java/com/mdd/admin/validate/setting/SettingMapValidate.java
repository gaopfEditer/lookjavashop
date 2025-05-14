package com.mdd.admin.validate.setting;

import com.mdd.admin.vo.setting.SettingCopyrightVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("网站地图Key信息参数")
public class SettingMapValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "网站地图Key信息")
    private String tencentMapKey;

}
