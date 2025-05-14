package com.mdd.admin.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("开通分销商分销参数")
public class DistributionStoreOpenValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids参数缺失")
    @ApiModelProperty(value = "商品ID数组", required = true)
    private List<Integer> ids;

    @NotNull(message = "分销等级参数缺失")
    @ApiModelProperty("分销等级")
    private Integer levelId;

}
