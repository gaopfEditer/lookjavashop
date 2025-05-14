package com.mdd.admin.controller.distribution;

import com.mdd.admin.service.IDistributionLevelService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionLevelCreateValidate;
import com.mdd.admin.validate.distribution.DistributionLevelUpdateValidate;
import com.mdd.admin.vo.distribution.DistributionLevelDetailVo;
import com.mdd.admin.vo.distribution.DistributionLevelListedVo;
import com.mdd.admin.vo.distribution.DistributionLevelSelectListVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import com.mdd.common.validator.annotation.IDMust;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/distribution/level")
@Api(tags = "分销商品管理")
public class DistributionLevelController {

    @Resource
    IDistributionLevelService iDistributionLevelService;

    @GetMapping("/list")
    @ApiOperation("等级列表")
    public AjaxResult<PageResult<DistributionLevelListedVo>> list(@Validated PageValidate pageValidate) {
        PageResult<DistributionLevelListedVo> list = iDistributionLevelService.list(pageValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/add")
    @ApiOperation("等级新增")
    public AjaxResult<Object> add(@Validated @RequestBody DistributionLevelCreateValidate createValidate) {
        iDistributionLevelService.add(createValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation("等级编辑")
    public AjaxResult<Object> edit(@Validated @RequestBody DistributionLevelUpdateValidate updateValidate) {
        iDistributionLevelService.edit(updateValidate);
        return AjaxResult.success();
    }

    @GetMapping("/detail")
    @ApiOperation("等级详情")
    public AjaxResult<DistributionLevelDetailVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        DistributionLevelDetailVo vo = iDistributionLevelService.detail(id);
        return AjaxResult.success(vo);
    }

    @PostMapping("/del")
    @ApiOperation("等级删除")
    public AjaxResult<Object> delete(@Validated @RequestBody IdValidate idValidate) {
        iDistributionLevelService.delete(idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/selectList")
    @ApiOperation("等级列表")
    public AjaxResult<Object> selectList() {
        List<DistributionLevelSelectListVo> list = iDistributionLevelService.selectList();
        return AjaxResult.success(list);
    }

}
