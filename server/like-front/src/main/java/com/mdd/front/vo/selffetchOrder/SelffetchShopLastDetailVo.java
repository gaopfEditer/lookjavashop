package com.mdd.front.vo.selffetchOrder;

import com.mdd.front.vo.order.OrderGoodsListVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel("最后一次自提订单详情Vo")
public class SelffetchShopLastDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "")
    private Integer id;
    @ApiModelProperty(value = "门店名称")
    private String name;
    @ApiModelProperty(value = "门店完整地址")
    private String shopAddress;
    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;
}
