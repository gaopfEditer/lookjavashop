package com.mdd.common.entity.selffetchshop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("自提门店实体")
public class SelffetchShop implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
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

    @ApiModelProperty(value = "省")
    private Integer province;

    @ApiModelProperty(value = "市")
    private Integer city;

    @ApiModelProperty(value = "区")
    private Integer district;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "营业开始时间")
    private String businessStartTime;

    @ApiModelProperty(value = "营业结束时间")
    private String businessEndTime;

    @ApiModelProperty(value = "营业周天,逗号隔开如 1,2,3,4,5,6,7")
    private String weekdays;

    @ApiModelProperty(value = "门店状态:1-启用;0-停用;")
    private Integer status;

    @ApiModelProperty(value = "门店简介")
    private String remark;

    @ApiModelProperty(value = "删除时间")
    private Integer isDelete;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "删除时间")
    private Long deleteTime;

    @ApiModelProperty(value = "distance")
    @TableField(exist = false)
    private Integer distance;
}