package com.mdd.front.controller;

import com.mdd.front.service.IGoodsCommentService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.goods.GoodsCommentCreateValidate;
import com.mdd.front.validate.goods.GoodsDetailCommentListValidate;
import com.mdd.front.vo.goods.CommentListVo;
import com.mdd.front.vo.goods.GoodsDetailCommentVo;
import com.mdd.common.aop.NotLogin;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/goodsComment")
@Api(tags = "商品评论")
public class GoodsCommentController {

    @Resource
    private IGoodsCommentService iGoodsCommentService;

    @PostMapping("/add")
    @ApiOperation(value = "添加评论")
    public AjaxResult<Object> add(@Validated @RequestBody GoodsCommentCreateValidate commentValidate) {
        iGoodsCommentService.add(commentValidate);
        return AjaxResult.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "评论列表")
    public AjaxResult<PageResult<CommentListVo>> list(@Validated PageValidate pageValidate,
                                                      @RequestParam(name = "isComment", defaultValue = "0") Integer isComment) {
        PageResult<CommentListVo> pageResult = iGoodsCommentService.list(pageValidate, isComment);
        return AjaxResult.success(pageResult);
    }


    @NotLogin
    @GetMapping("/goodsCommentList")
    @ApiOperation(value = "商品页内评论列表")
    public AjaxResult<PageResult<GoodsDetailCommentVo>> goodsCommentList(@Validated PageValidate pageValidate,
                                                                         @Validated GoodsDetailCommentListValidate commentListValidate) {
        PageResult<GoodsDetailCommentVo> pageResult = iGoodsCommentService.getGoodsDetailCommentList(pageValidate, commentListValidate);
        return AjaxResult.success(pageResult);
    }

}
