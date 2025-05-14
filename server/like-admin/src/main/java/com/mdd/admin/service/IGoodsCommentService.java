package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.goods.GoodsCommentReplyValidate;
import com.mdd.admin.validate.goods.GoodsCommentSearchValidate;
import com.mdd.admin.vo.goods.GoodsCommentVo;
import com.mdd.common.core.PageResult;

public interface IGoodsCommentService {

    /**
     * 评论列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<GoodsCommentVo>
     */
    PageResult<GoodsCommentVo> list(PageValidate pageValidate, GoodsCommentSearchValidate searchValidate);

    /**
     * 评论回复
     *
     * @author fzr
     * @param replyValidate 回复参数
     */
    void reply(GoodsCommentReplyValidate replyValidate);

    /**
     * 评论状态
     *
     * @author fzr
     * @param id 评论ID
     */
    void change(Integer id);

}
