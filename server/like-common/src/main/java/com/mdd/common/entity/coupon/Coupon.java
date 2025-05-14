package com.mdd.common.entity.coupon;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("优惠券管理实体")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty(value = "优惠券编号")
    private String sn;

    @ApiModelProperty("优惠券名称")
    private String name;

    @ApiModelProperty("优惠券面额")
    private BigDecimal money;

    @ApiModelProperty("发放数量限制: 1=无限数量,2=固定发放数量")
    private Integer sendTotalType;

    @ApiModelProperty("发放数量: send_total_type=2时生效")
    private Integer sendTotal;

    @ApiModelProperty("使门槛类型: 1=无门槛,2=订单满足金额")
    private Integer conditionType;

    @ApiModelProperty("使用条件(订单满多少可用): condition_money=2时生效")
    private BigDecimal conditionMoney;

    @ApiModelProperty("领取类型: [1=用户领取, 2=商家赠送]")
    private Integer getType;

    @ApiModelProperty("领取开始时间")
    private Long getTimeStart;

    @ApiModelProperty("领取结束时间")
    private Long getTimeEnd;

    @ApiModelProperty("领取次数类型：1=不限制领取次数；2=限制次数；3=每天限制数量'")
    private Integer getNumType;

    @ApiModelProperty("领取次数限制: get_type=2/3时生效")
    private Integer getNum;

    @ApiModelProperty("用券时间类型: 1=固定时间,2=领券当天起,3=领券次日起")
    private Integer useTimeType;

    @ApiModelProperty("用券开始时间: use_time_type=1时生效")
    private Long useTimeStart;

    @ApiModelProperty("用券结束时间: use_time_type=1时生效")
    private Long useTimeEnd;

    @ApiModelProperty("多少天内可用: use_time_type=2/3时生效")
    private Integer useTime;


    @ApiModelProperty("适用商品类型: 1=全部商品,2=指定商品,3=指定商品不可用")
    private Integer useGoodsType;

    @ApiModelProperty("使用商品ID")
    private String useGoodsIds;

    @ApiModelProperty("发放状态: 1=待发布,2=未开始,3=进行中,4=已结束")
    private Integer status;

    @ApiModelProperty("是否删除: 0=否, 1=是")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
