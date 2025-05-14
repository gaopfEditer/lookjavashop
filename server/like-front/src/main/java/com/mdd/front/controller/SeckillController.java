package com.mdd.front.controller;

import com.mdd.front.service.ISeckillService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.seckill.SeckillDetailVo;
import com.mdd.front.vo.seckill.SeckillListVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/seckill")
@Api(tags = "秒杀管理")
public class SeckillController {

    @Resource
    ISeckillService iSeckillService;

    @NotLogin
    @GetMapping("/list")
    @ApiOperation(value = "秒杀活动列表")
    public AjaxResult<PageResult<SeckillListVo>> list(@Validated PageValidate pageValidate) {
        PageResult<SeckillListVo> pageResult = iSeckillService.list(pageValidate);
        return AjaxResult.success(pageResult);
    }


    @NotLogin
    @GetMapping("/detail")
    @ApiOperation(value = "秒杀活动详情")
    public AjaxResult<SeckillDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        SeckillDetailVo detailVo = iSeckillService.detail(id);
        return AjaxResult.success(detailVo);
    }


}
