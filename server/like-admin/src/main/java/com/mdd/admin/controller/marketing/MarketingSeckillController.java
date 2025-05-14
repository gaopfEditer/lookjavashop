package com.mdd.admin.controller.marketing;

import com.mdd.admin.service.IMarketingSeckillService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillCreateValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillRenameValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillSearchValidate;
import com.mdd.admin.validate.marketing.MarketingSeckillUpdateValidate;
import com.mdd.admin.vo.marketing.MarketingSeckillDetailVo;
import com.mdd.admin.vo.marketing.MarketingSeckillListedVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/marketing/seckill")
@Api(tags = "营销秒杀管理")
public class MarketingSeckillController {

    @Resource
    IMarketingSeckillService iMarketingSeckillService;

    @GetMapping("/list")
    @ApiOperation(value = "活动列表")
    public AjaxResult<PageResult<MarketingSeckillListedVo>> list(@Validated PageValidate pageValidate,
                                                                 @Validated MarketingSeckillSearchValidate searchValidate) {
        PageResult<MarketingSeckillListedVo> list = iMarketingSeckillService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "活动详情")
    public AjaxResult<MarketingSeckillDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        MarketingSeckillDetailVo vo = iMarketingSeckillService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/add")
    @ApiOperation(value = "活动新增")
    public AjaxResult<Object> add(@Validated @RequestBody MarketingSeckillCreateValidate createValidate) {
        iMarketingSeckillService.add(createValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value = "活动编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody MarketingSeckillUpdateValidate updateValidate) {
        iMarketingSeckillService.edit(updateValidate);
        return AjaxResult.success();
    }

    @PostMapping("/del")
    @ApiOperation(value = "活动删除")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        iMarketingSeckillService.del(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/rename")
    @ApiOperation(value = "活动命名")
    public AjaxResult<Object> rename(@Validated @RequestBody MarketingSeckillRenameValidate renameValidate) {
        iMarketingSeckillService.rename(renameValidate.getId(), renameValidate.getName());
        return AjaxResult.success();
    }

    @PostMapping("/release")
    @ApiOperation(value = "活动发布")
    public AjaxResult<Object> release(@Validated @RequestBody IdValidate idValidate) {
        iMarketingSeckillService.release(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/start")
    @ApiOperation(value = "活动开始")
    public AjaxResult<Object> start(@Validated @RequestBody IdValidate idValidate) {
        iMarketingSeckillService.start(idValidate.getId());
        return AjaxResult.success();
    }

    @PostMapping("/end")
    @ApiOperation(value = "活动结束")
    public AjaxResult<Object> end(@Validated @RequestBody IdValidate idValidate) {
        iMarketingSeckillService.end(idValidate.getId());
        return AjaxResult.success();
    }

}
