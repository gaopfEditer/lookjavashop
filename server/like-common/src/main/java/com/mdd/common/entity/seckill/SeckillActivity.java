package com.mdd.common.entity.seckill;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("秒杀活动实体")
public class SeckillActivity {

    @TableId(value="id", type= IdType.AUTO)
    @ApiModelProperty("ID")
    private Integer id;

    @ApiModelProperty("活动编号")
    private String sn;

    @ApiModelProperty("活动名称")
    private String name;

    @ApiModelProperty("每单限制: [0=不限制, 1=限制]")
    private Integer limitStatus;

    @ApiModelProperty("单笔最多购买的商品件数")
    private Integer maxBuy;

    @ApiModelProperty("活动说明")
    private String remarks;

    @ApiModelProperty("活动开始时间")
    private Long startTime;

    @ApiModelProperty("活动结束时间")
    private Long endTime;

    @ApiModelProperty("活动状态[1=未开始，2=进行中, 3=已结束]")
    private Integer status;

    @ApiModelProperty("是否删除: [0=否, 1=是]")
    private Integer isDelete;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("更新时间")
    private Long updateTime;

    @ApiModelProperty("删除时间")
    private Long deleteTime;

}
