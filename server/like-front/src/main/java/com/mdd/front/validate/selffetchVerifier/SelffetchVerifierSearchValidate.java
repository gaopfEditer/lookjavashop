package com.mdd.front.validate.selffetchVerifier;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("核销员搜素参数")
public class SelffetchVerifierSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自取门店ID")
    private Integer selffetchShopId;

    @ApiModelProperty(value = "自取门店名称")
    private String selffetchShopName;

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "核销员编号")
    private String sn;

    @ApiModelProperty(value = "核销员名称")
    private String name;

    @ApiModelProperty(value = "联系电话")
    private String mobile;

    @ApiModelProperty(value = "核销员状态:1-启用;0-停用;")
    private Integer status;

}
