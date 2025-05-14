package com.mdd.admin.validate.selffetchshop;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 自提门店参数
 * @author LikeAdmin
 */
@Data
@ApiModel("自提门店更新状态")
public class SelffetchShopStatusValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "")
    private Integer id;

    @NotNull(message = "status参数缺失")
    @ApiModelProperty(value = "门店状态:1-启用;0-停用;")
    private Integer status;


}
