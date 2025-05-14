package com.mdd.front.vo.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "微信小程序码Vo")
public class WechatMnpCodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "小程序码")
    private String image;

}
