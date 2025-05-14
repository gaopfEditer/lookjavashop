package com.mdd.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("地区实体")
public class DevRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("地区ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("父级地区ID")
    private Integer parentId;

    @ApiModelProperty("等级")
    private Integer level;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("简称")
    private String shortName;

    @ApiModelProperty("地区编码")
    private String cityCode;

    @ApiModelProperty("邮政编码")
    private String zipCode;

    @ApiModelProperty("纬度")
    private String gcj02Lng;

    @ApiModelProperty("经度")
    private String gcj02Lat;

    @ApiModelProperty("纬度")
    private String db09Lng;

    @ApiModelProperty("经度")
    private String db09Lat;

    @ApiModelProperty("备注")
    private String remark1;

    @ApiModelProperty("备注")
    private String remark2;


}
