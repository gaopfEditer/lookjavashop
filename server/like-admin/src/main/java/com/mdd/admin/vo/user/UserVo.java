package com.mdd.admin.vo.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("用户Vo")
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户编码")
    private Integer sn;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "登录账号")
    private String username;

    @ApiModelProperty(value = "钱包金额 = 用户钱包+用户佣金")
    private BigDecimal totalMoney;

    @ApiModelProperty("用户钱包")
    private BigDecimal money;

    @ApiModelProperty("用户佣金")
    private BigDecimal earnings;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "用户性别")
    private String sex;

    @ApiModelProperty(value = "注册渠道")
    private String channel;

    @ApiModelProperty(value = "未使用优惠券数")
    private Long unUseCouponNum;

    @ApiModelProperty(value = "邀请人ID")
    private Integer inviterId;

    @ApiModelProperty(value = "邀请人")
    private String inviterName;

    @ApiModelProperty(value = "邀请人数")
    private Long inviteNum;

    @ApiModelProperty(value = "上级ID")
    private Integer firstLeader;

    @ApiModelProperty(value = "上级名称")
    private String firstLeaderName;

    @ApiModelProperty(value = "是否注销 [0=否 1=是]")
    private Integer isClose;

    @ApiModelProperty(value = "最后登录IP")
    private String lastLoginIp;

    @ApiModelProperty(value = "最后登录时间")
    private String lastLoginTime;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    public void setSex(Integer sex) {
        switch (sex) {
            case 0:
                this.sex = "未知";
                break;
            case 1:
                this.sex = "男";
                break;
            case 2:
                this.sex = "女";
                break;
        }
    }

}
