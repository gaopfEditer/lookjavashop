package com.mdd.admin.validate.user;

import com.mdd.common.aop.NotLogin;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("邀请列表搜索参数")
public class InviterSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "用户id参数不能为空")
    @ApiModelProperty(value = "用户id", required = true)
    private Integer userId;

    @ApiModelProperty(value = "用户编号")
    private String sn;

    @ApiModelProperty(value = "用户账号")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

}
