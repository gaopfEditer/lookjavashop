package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionGoodsService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsJoinValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsSearchValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsSetValidate;
import com.mdd.admin.vo.distribution.DistributionGoodsDetailVo;
import com.mdd.admin.vo.distribution.DistributionGoodsListedVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/distribution/goods")
@Api(tags = "分销商品管理")
public class DistributionGoodsController {

    @Resource
    IDistributionGoodsService iDistributionGoodsService;

    @GetMapping("/list")
    @ApiOperation("分销商品列表")
    public AjaxResult<PageResult<DistributionGoodsListedVo>> list(@Validated PageValidate pageValidate,
                                                                  @Validated DistributionGoodsSearchValidate searchValidate) {
        PageResult<DistributionGoodsListedVo> list = iDistributionGoodsService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation("分销商品详情")
    public AjaxResult<DistributionGoodsDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        DistributionGoodsDetailVo vo = iDistributionGoodsService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/set")
    @ApiOperation("设置商品佣金")
    public AjaxResult<Object> set(@Validated @RequestBody DistributionGoodsSetValidate setValidate) {
        iDistributionGoodsService.set(setValidate);
        return AjaxResult.success();
    }

    @PostMapping("/join")
    @ApiOperation("参与/不参与分销")
    public Object join(@Validated @RequestBody DistributionGoodsJoinValidate joinValidate) {
        iDistributionGoodsService.join(joinValidate);
        return AjaxResult.success();
    }

}
