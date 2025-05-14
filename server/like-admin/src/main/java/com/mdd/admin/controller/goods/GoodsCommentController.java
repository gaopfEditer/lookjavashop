package com.mdd.admin.controller.goods;

import com.mdd.admin.service.IGoodsCommentService;
import com.mdd.admin.validate.commons.IdValidate;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.goods.GoodsCommentReplyValidate;
import com.mdd.admin.validate.goods.GoodsCommentSearchValidate;
import com.mdd.admin.vo.goods.GoodsCommentVo;
import com.mdd.common.core.AjaxResult;
import com.mdd.common.core.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/goods/comment")
@Api(tags = "商品评论管理")
public class GoodsCommentController {

    @Resource
    IGoodsCommentService iGoodsCommentService;

    @GetMapping("/list")
    @ApiOperation("评论列表")
    public AjaxResult<Object> list(@Validated PageValidate pageValidate, @Validated GoodsCommentSearchValidate searchValidate) {
        PageResult<GoodsCommentVo> list = iGoodsCommentService.list(pageValidate, searchValidate);
        return AjaxResult.success(list);
    }

    @PostMapping("/reply")
    @ApiOperation("评论回复")
    public AjaxResult<Object> reply(@Validated @RequestBody GoodsCommentReplyValidate replyValidate) {
        iGoodsCommentService.reply(replyValidate);
        return AjaxResult.success();
    }

    @PostMapping("/change")
    @ApiOperation("修改状态")
    public AjaxResult<Object> change(@Validated @RequestBody IdValidate idValidate) {
        iGoodsCommentService.change(idValidate.getId());
        return AjaxResult.success();
    }

}
