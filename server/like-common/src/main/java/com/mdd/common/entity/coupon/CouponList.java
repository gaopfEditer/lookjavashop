package com.mdd.common.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("优惠券领取实体")
public class CouponList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("获取途径: 1=用户领取, 2=商家发放")
    private Integer channel;

    @ApiModelProperty("优惠券编码")
    private String couponCode;

    @ApiModelProperty("发放人ID")
    private Integer issuerId;

    @ApiModelProperty("优惠券ID")
    private Integer couponId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("使用状态: [0=未使用，1=已使用，2=已过期，3=已作废]")
    private Integer status;

    @ApiModelProperty("使用时间")
    private Long useTime;

    @ApiModelProperty("是否删除: [0=否, 1=是]")
    private Integer isDelete;

    @ApiModelProperty("失效时间")
    private Long invalidTime;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
