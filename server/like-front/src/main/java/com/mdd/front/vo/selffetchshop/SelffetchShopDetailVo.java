package com.mdd.front.vo.selffetchshop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;

@Data
@ApiModel("自提门店详情Vo")
public class SelffetchShopDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "")
    private Integer id;

    @ApiModelProperty(value = "门店名称")
    private String name;

    @ApiModelProperty(value = "门店LOGO")
    private String image;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系电话")
    private String mobile;

    @ApiModelProperty(value = "门店完整地址")
    private String shopAddress;

    @ApiModelProperty(value = "营业开始时间")
    private String businessStartTime;

    @ApiModelProperty(value = "营业结束时间")
    private String businessEndTime;

    @ApiModelProperty(value = "营业周天,逗号隔开如 1,2,3,4,5,6,7")
    private String weekdays;
    @ApiModelProperty(value = "营业周天")
    private String weekdaysStr;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;
}
