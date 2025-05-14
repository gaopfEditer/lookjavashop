package com.mdd.front.validate.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("分销商粉丝列表参数")
public class DistributionFansSearchValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户信息关键词")
    private String keyword;

    @ApiModelProperty(value = "粉丝类型: first=一级 second=二级 all=全部")
    private String type;

    @ApiModelProperty(value = "粉丝排序 asc=升序 desc降序")
    private String sortFans;

    @ApiModelProperty(value = "订单金额排序 asc=升序 desc降序")
    private String sortAmount;

    @ApiModelProperty(value = "订单数量排序 asc=升序 desc降序")
    private String sortOrder;
}
