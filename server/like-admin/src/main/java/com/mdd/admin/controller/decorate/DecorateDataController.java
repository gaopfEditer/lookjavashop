package com.mdd.admin.controller.decorate;

import com.mdd.admin.vo.decorate.DecorateDataCouponVo;
import com.mdd.admin.vo.decorate.DecorateDataGoodsVo;
import com.mdd.admin.vo.decorate.DecorateDataSeckillVo;
import com.mdd.common.aop.NotPower;
import com.mdd.admin.service.IDecorateDataService;
import com.mdd.admin.vo.decorate.DecorateDataArticleVo;
import com.mdd.common.core.AjaxResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/decorate/data")
@Api(tags = "装修数据管理")
public class DecorateDataController {

    @Resource
    IDecorateDataService iDecorateDataService;

    @NotPower
    @GetMapping("/article")
    @ApiOperation(value="获取文章数据")
    public AjaxResult<List<DecorateDataArticleVo>> article(@RequestParam(defaultValue = "10") Integer limit) {
        List<DecorateDataArticleVo> list = iDecorateDataService.article(limit);
        return AjaxResult.success(list);
    }

    @NotPower
    @GetMapping("/goods")
    @ApiOperation(value="获取商品数据")
    public AjaxResult<List<DecorateDataGoodsVo>> goods(@RequestParam(defaultValue = "5") Integer limit,
                                                       @RequestParam(defaultValue = "hot") String type) {
        List<DecorateDataGoodsVo> list = iDecorateDataService.goods(limit, type);
        return AjaxResult.success(list);
    }

    @NotPower
    @GetMapping("/coupon")
    @ApiModelProperty(value = "获取优惠券数据")
    public AjaxResult<List<DecorateDataCouponVo>> coupon(@RequestParam(defaultValue = "5") Integer limit) {
        List<DecorateDataCouponVo> list = iDecorateDataService.coupon(limit);
        return AjaxResult.success(list);
    }

    @NotPower
    @GetMapping("/seckill")
    @ApiModelProperty(value = "获取秒杀数据")
    public AjaxResult<Object> seckill(@RequestParam(defaultValue = "5") Integer limit) {
        List<DecorateDataSeckillVo> list = iDecorateDataService.seckill(limit);
        return AjaxResult.success(list);
    }

}
