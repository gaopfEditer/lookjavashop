package com.mdd.common.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("订单发货实体")
public class OrderDelivery implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("管理ID")
    private Integer adminId;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("收货人")
    private String contact;

    @ApiModelProperty("联系方式")
    private String mobile;

    @ApiModelProperty("省")
    private Integer province;

    @ApiModelProperty("市")
    private Integer city;

    @ApiModelProperty("区")
    private Integer district;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("物流单号")
    private String invoiceNo;

    @ApiModelProperty("物流公司")
    private Integer expressId;

    @ApiModelProperty("快递名称")
    private String expressName;

    @ApiModelProperty("发货状态: [0=未发货, 1=已发货]")
    private Integer expressStatus;

    @ApiModelProperty("配送方式: [1=快递配送, 2=无需快递]")
    private Integer sendType;

    @ApiModelProperty("发货备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
