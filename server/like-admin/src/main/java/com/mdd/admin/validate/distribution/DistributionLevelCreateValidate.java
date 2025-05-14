package com.mdd.admin.validate.distribution;

import com.mdd.common.validator.annotation.IntegerContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Data
@ApiModel("分销等级创建参数")
public class DistributionLevelCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "等级名称不能为空")
    @ApiModelProperty(value = "等级名称", required = true)
    private String name;

    @NotNull(message = "级别不能为空")
    @Min(value = 1, message = "级别需大于1")
    @Max(value = 999, message = "级别不能大于1000")
    @ApiModelProperty(value = "等级权重", required = true)
    private Integer weights;

    @NotEmpty(message = "图标不能为空")
    @ApiModelProperty("等级图标")
    private String icon;

    @NotEmpty(message = "背景图不能为空")
    @ApiModelProperty("等级背景")
    private String image;

    @ApiModelProperty(value = "等级说明")
    private String remark;

    @NotNull(message = "自购佣金比例不能为空")
    @Min(value = 0, message = "自购佣金比例不能小于0")
    @Max(value = 100, message = "自购佣金比例不能大于100")
    @ApiModelProperty(value = "自购佣金比例")
    private Double selfRatio;

    @NotNull(message = "一级佣金比例不能为空")
    @Min(value = 0, message = "一级佣金比例不能小于0")
    @Max(value = 100, message = "一级佣金比例不能大于100")
    @ApiModelProperty(value = "一级佣金比例")
    private Double firstRatio;

    @NotNull(message = "二级佣金比例不能为空")
    @Min(value = 0, message = "二级佣金比例不能小于0")
    @Max(value = 100, message = "二级佣金比例不能大于100")
    @ApiModelProperty(value = "二级佣金比例")
    private Double secondRatio;

    @NotNull(message = "等级条件类型不能为空")
    @IntegerContains(values = {1, 2}, message = "等级条件类型参数不在合法值内")
    @ApiModelProperty(value = "等级更新条件类型")
    private Integer updateType;

    @Min(value = 0, message = "单笔消费金额不能小于0")
    @ApiModelProperty("单笔消费金额")
    private BigDecimal singleConsumptionAmount;

    @Min(value = 0, message = "累计消费金额不能小于0")
    @ApiModelProperty("累计消费金额")
    private BigDecimal cumulativeConsumptionAmount;

    @Min(value = 0, message = "累计消费次数不能小于0")
    @ApiModelProperty("累计消费次数")
    private Integer cumulativeConsumptionTimes;

    @Min(value = 0, message = "已结算佣金收入不能小于0")
    @ApiModelProperty("已结算佣金收入")
    private BigDecimal returnedCommission;

}
