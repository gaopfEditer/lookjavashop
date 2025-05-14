package com.mdd.front.vo.goods;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "商品分享图片Vo")
public class GoodsImageResourceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品图片")
    private String goodsImage;

    @ApiModelProperty(value = "用户头像")
    private String userAvatar;

}
