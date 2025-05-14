package com.mdd.front.controller;

import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.validate.user.UserAddressRegionIdValidate;
import com.mdd.front.vo.user.UserAddressRegionIdVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.validator.annotation.IDMust;
import com.mdd.front.service.IUserAddressService;
import com.mdd.front.validate.user.UserAddressCreateValidate;
import com.mdd.front.validate.user.UserAddressUpdateValidate;
import com.mdd.front.validate.common.IdValidate;
import com.mdd.front.vo.user.UserAddressVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api/userAddress")
@Api(tags = "用户地址管理")
public class UserAddressController {

    @Resource
    IUserAddressService iUserAddressService;

    @GetMapping("/list")
    @ApiOperation(value = "用户地址列表")
    public AjaxResult<List<UserAddressVo>> list() {
        Integer userId = LikeFrontThreadLocal.getUserId();
        List<UserAddressVo> list = iUserAddressService.list(userId);
        return AjaxResult.success(list);
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加用户地址")
    public AjaxResult<Object> add(@Validated @RequestBody UserAddressCreateValidate UserAddressAddValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iUserAddressService.add(userId, UserAddressAddValidate);
        return AjaxResult.success();
    }

    @PostMapping("/edit")
    @ApiOperation(value = "编辑用户地址")
    public AjaxResult<Object> edit(@Validated @RequestBody UserAddressUpdateValidate UserAddressUpdateValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iUserAddressService.edit(userId, UserAddressUpdateValidate);
        return AjaxResult.success();
    }

    @GetMapping("/detail")
    @ApiOperation(value = "用户地址详情")
    public AjaxResult<UserAddressVo> detail(@Validated @IDMust() @RequestParam("id") Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        UserAddressVo detail = iUserAddressService.detail(userId, id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/del")
    @ApiOperation(value = "删除用户地址")
    public AjaxResult<Object> del(@Validated @RequestBody IdValidate idValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        iUserAddressService.del(userId, idValidate.getId());
        return AjaxResult.success();
    }

    @GetMapping("/getRegionId")
    @ApiOperation(value = "根据地区名称获取id")
    public AjaxResult<UserAddressRegionIdVo> getRegionId(@Validated UserAddressRegionIdValidate regionIdValidate) {
        UserAddressRegionIdVo regionIdVo = iUserAddressService.getRegionId(regionIdValidate);
        return AjaxResult.success(regionIdVo);
    }

}
