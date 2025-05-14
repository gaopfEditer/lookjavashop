package com.mdd.front.validate.selffetchorder;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("核销码检查")
public class SelffetchOrderPickCodeVerifyValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前用户ID", required = true)
    private Integer currUserId;

    @NotNull(message = "必须提交核销码")
    @ApiModelProperty(value = "审核内容")
    private String pickupCode;

}
