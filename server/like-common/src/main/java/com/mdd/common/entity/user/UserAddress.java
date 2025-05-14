package com.mdd.common.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("用户地址实体")
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("联系人名")
    private String contact;

    @ApiModelProperty("联系方式")
    private String mobile;

    @ApiModelProperty("省")
    private Integer provinceId;

    @ApiModelProperty("市")
    private Integer cityId;

    @ApiModelProperty("区")
    private Integer districtId;

    @ApiModelProperty("详细地址")
    private String info;

    @ApiModelProperty("经度")
    private Integer longitude;

    @ApiModelProperty("纬度")
    private String latitude;

    @ApiModelProperty("是否默认: [0=否, 1=是]")
    private Integer isDefault;

    @ApiModelProperty("是否删除: [0=否, 1=是]")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;


}
