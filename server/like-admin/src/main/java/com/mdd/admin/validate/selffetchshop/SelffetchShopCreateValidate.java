package com.mdd.admin.validate.selffetchshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import javax.validation.constraints.*;

@Data
@ApiModel("自提门店创建参数")
public class SelffetchShopCreateValidate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "name参数缺失")
    @ApiModelProperty(value = "门店名称")
    private String name;

    @ApiModelProperty(value = "门店LOGO")
    private String image;

    @NotNull(message = "contact参数缺失")
    @ApiModelProperty(value = "联系人")
    private String contact;

    @NotNull(message = "mobile参数缺失")
    @ApiModelProperty(value = "联系电话")
    @Length(min = 11, max = 11, message = "手机号只能为11位")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    private String mobile;

    @NotNull(message = "province参数缺失")
    @ApiModelProperty(value = "省")
    private Integer province;

    @NotNull(message = "city参数缺失")
    @ApiModelProperty(value = "市")
    private Integer city;

    @NotNull(message = "district参数缺失")
    @ApiModelProperty(value = "区")
    private Integer district;

    @NotNull(message = "address参数缺失")
    @ApiModelProperty(value = "详细地址")
    private String address;

    @NotNull(message = "longitude参数缺失")
    @ApiModelProperty(value = "经度")
    private String longitude;

    @NotNull(message = "latitude参数缺失")
    @ApiModelProperty(value = "纬度")
    private String latitude;

    @NotNull(message = "businessStartTime参数缺失")
    @ApiModelProperty(value = "营业开始时间")
    private String businessStartTime;

    @NotNull(message = "businessEndTime参数缺失")
    @ApiModelProperty(value = "营业结束时间")
    private String businessEndTime;

    @NotNull(message = "weekdays参数缺失")
    @ApiModelProperty(value = "营业周天,逗号隔开如 1,2,3,4,5,6,7")
    private String weekdays;

    @NotNull(message = "status参数缺失")
    @ApiModelProperty(value = "门店状态:1-启用;0-停用;")
    private Integer status;

    @ApiModelProperty(value = "门店简介")
    private String remark;

}
