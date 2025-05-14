package com.mdd.common.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("订单售后实体")
public class OrderAfter implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("售后编码")
    private String afterSn;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("订单ID")
    private Integer orderId;

    @ApiModelProperty("退款类型: [1-整单退款 2-商品售后]")
    private Integer afterType;

    @ApiModelProperty("退款编号")
    private String refundSn;

    @ApiModelProperty("退款原因")
    private String refundReason;

    @ApiModelProperty("退款说明")
    private String refundRemark;

    @ApiModelProperty("退款图片")
    private String refundImage;

    @ApiModelProperty("退款类型: [1=仅退款 2=退货退款]")
    private Integer refundType;

    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney;

    @ApiModelProperty("退款路径 1-原路退回")
    private Integer refundWay;

    @ApiModelProperty("物流公司id")
    private Integer expressId;

    @ApiModelProperty("快递公司名称")
    private String expressName;

    @ApiModelProperty("快递单号")
    private String invoiceNo;

    @ApiModelProperty("物流备注说明")
    private String expressRemark;

    @ApiModelProperty("物流联系人")
    private String expressContact;

    @ApiModelProperty("买家退货时间")
    private Long expressTime;

    @ApiModelProperty("确认收货时间")
    private Long confirmTime;

    @ApiModelProperty("售后状态: [1=申请中, 2=售后成功, 3=售后关闭]")
    private Integer afterStatus;

    @ApiModelProperty("售后子状态: [11-买家申请售后,待商家同意 12-商家同意售后申请,待买家退货 13-买家已退货,待商家处理 14-商家确认收货,等待退款 15-商家同意售后申请,等待退款" +
            "21-商家已确认退款,售后成功" +
            "31-买家撤销申请；32-商家已拒绝售后申请 33-商家拒绝收货]")
    private Integer subStatus;

    @ApiModelProperty("审核时间")
    private Long auditTime;

    @ApiModelProperty("处理人id")
    private Integer handleId;

    @ApiModelProperty("处理备注")
    private String handleRemark;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

    @ApiModelProperty("删除状态: [1=是, 0=否]")
    private Integer isDelete;
}
