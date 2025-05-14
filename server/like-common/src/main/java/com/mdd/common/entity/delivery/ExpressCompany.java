package com.mdd.common.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("快递公司实体")
public class ExpressCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("图标")
    private String Image;

    @ApiModelProperty("快递编码")
    private String codeKd;

    @ApiModelProperty("快递100编码")
    private String codeKd100;

    @ApiModelProperty("快递鸟编码")
    private String codeKdniao;

    @ApiModelProperty("排序编号")
    private String sort;

    @ApiModelProperty("删除状态：[1=是；0=否]")
    private Integer IsDelete;

    @ApiModelProperty("修改时间")
    private Long createTime;

    @ApiModelProperty("创建时间")
    private Long updateTime;

}
