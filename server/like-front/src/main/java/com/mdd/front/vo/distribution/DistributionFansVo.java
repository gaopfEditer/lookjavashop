package com.mdd.front.vo.distribution;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("分销商粉丝Vo")
public class DistributionFansVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户编号")
    private Integer sn;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "粉丝数量")
    private Integer fans;

    @ApiModelProperty(value = "已支付订单数")
    private Integer orderNum;

    @ApiModelProperty(value = "已支付订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "注册时间")
    private String createTime;



}
