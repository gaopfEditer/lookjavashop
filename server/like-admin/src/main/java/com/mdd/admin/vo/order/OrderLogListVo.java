package com.mdd.admin.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("订单日志列表Vo")
public class OrderLogListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("渠道")
    private Integer channel;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作内容")
    private String content;

    @ApiModelProperty("操作时间")
    private String createTime;

}
