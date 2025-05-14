package com.mdd.common.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("商品分类实体")
public class GoodsCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)

    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("分类图片")
    private String image;

    @ApiModelProperty("父级分类")
    private Integer pid;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否显示")
    private Integer isShow;

    @ApiModelProperty("删除状态")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

}
