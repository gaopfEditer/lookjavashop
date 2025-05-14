package com.mdd.common.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("运费模板实体")
public class ExpressTpl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("计费方式: [0=件, 1=体积,  2=重量]")
    private Integer type;

    @ApiModelProperty("首价")
    private Integer firstNum;

    @ApiModelProperty("首价")
    private BigDecimal firstPrice;

    @ApiModelProperty("续数")
    private Integer continueNum;

    @ApiModelProperty("续价")
    private BigDecimal continuePrice;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("删除状态: [1=是, 0=否]")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("修改时间")
    private Long updateTime;

}
