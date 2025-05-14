package com.mdd.front.validate.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("小程序码参数")
public class WechatMnpCodeValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "scene参数缺失")
    @ApiModelProperty("场景")
    private String scene;

    @NotNull(message = "小程序路径不能为空")
    @ApiModelProperty("小程序路径")
    private String page;

    @ApiModelProperty("小程序码宽度")
    private Integer width;


}
