package com.mdd.common.entity.cart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("购物车实体类")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("商品ID")
    private Integer goodsId;

    @ApiModelProperty("规格ID")
    private Integer goodsSkuId;

    @ApiModelProperty("商品数量")
    private Integer num;

    @ApiModelProperty("是否选中")
    private Integer selected;

    @ApiModelProperty("删除状态")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

}
