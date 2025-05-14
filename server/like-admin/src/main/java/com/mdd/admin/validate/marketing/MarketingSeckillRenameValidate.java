package com.mdd.admin.validate.marketing;

import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("秒杀命名参数")
public class MarketingSeckillRenameValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @IDMust(message = "id参数必传且需大于0")
    @ApiModelProperty(value = "ID")
    private Integer id;

    @NotNull(message = "name参数缺失")
    @Length(max = 250, message = "活动名称不能超出250个字符")
    @ApiModelProperty(value = "活动名称")
    private String name;

}
