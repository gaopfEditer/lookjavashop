package com.mdd.admin.validate.distribution;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("分销商品参与分销参数")
public class DistributionGoodsJoinValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "ids参数缺失")
    @ApiModelProperty(value = "商品ID数组", required = true)
    private List<Integer> ids;

    @NotNull(message = "分销状态参数缺失")
    @IntegerContains(values = {0, 1}, message = "分销状态异常")
    @ApiModelProperty("是否参与分销: [0=不参与 1=参与]")
    private Integer isDistribution;

}
