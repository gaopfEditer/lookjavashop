package com.mdd.front.service;

import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.goods.GoodsCommentCreateValidate;
import com.mdd.front.validate.goods.GoodsDetailCommentListValidate;
import com.mdd.front.vo.goods.CommentListVo;
import com.mdd.front.vo.goods.GoodsDetailCommentVo;
import com.mdd.common.core.PageResult;

/**
 * 商品评价
 */
public interface IGoodsCommentService {

    /**
     * 商品评价
     *
     * @author mjf
     * @param commentValidate GoodsCommentValidate
     */
    void add(GoodsCommentCreateValidate commentValidate);

    /**
     * 评论列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param isComment Integer
     * @return PageResult<CommentListVo>
     */
    PageResult<CommentListVo> list(PageValidate pageValidate, Integer isComment);

    /**
     * 商品详情页中的评论列表
     *
     * @author mjf
     * @param goodsId Integer
     * @return GoodsDetailCommentVo
     */
    GoodsDetailCommentVo getGoodsDetailComment(Integer goodsId);


    // 商品详情页页内的评论列表
    /**
     * 商品详情页评论列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param commentListValidate GoodsDetailCommentListValidate
     * @return PageResult<GoodsDetailCommentVo>
     */
    PageResult<GoodsDetailCommentVo> getGoodsDetailCommentList(PageValidate pageValidate, GoodsDetailCommentListValidate commentListValidate);
}
