package com.mdd.admin.validate.order;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("审核内容参数")
public class OrderSelffetchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "订单ID", required = true)
    private Integer id;

    @NotNull(message = "至少上传1条信息")
    @ApiModelProperty(value = "审核内容")
    private JSONArray items;

}
