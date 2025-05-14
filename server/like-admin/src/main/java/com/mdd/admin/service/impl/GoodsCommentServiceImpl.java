package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IGoodsCommentService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.goods.GoodsCommentReplyValidate;
import com.mdd.admin.validate.goods.GoodsCommentSearchValidate;
import com.mdd.admin.vo.goods.GoodsCommentVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.goods.GoodsComment;
import com.mdd.common.mapper.goods.GoodsCommentMapper;
import com.mdd.common.util.ListUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class GoodsCommentServiceImpl implements IGoodsCommentService {

    @Resource
    GoodsCommentMapper goodsCommentMapper;

    /**
     * 评论列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<GoodsCommentVo>
     */
    @Override
    public PageResult<GoodsCommentVo> list(PageValidate pageValidate, GoodsCommentSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<GoodsComment> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper
//                .selectAll(GoodsComment.class)
//                .select("OG.goods_name,OG.goods_image,OG.goods_sku_value,U.nickname")
                .leftJoin("?_user U ON t.user_id=U.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_order_goods OG ON t.order_goods_id=OG.id".replace("?_", GlobalConfig.tablePrefix));

        // 关键词
        String searchType = searchValidate.getSearchType();
        String searchKeyword = searchValidate.getKeyword();
        if(StringUtils.isNotNull(searchValidate.getSearchType()) && null != searchKeyword){
            switch (searchType){
                case "goodsName":
                    mpjQueryWrapper.like("OG.goods_name","%"+searchKeyword+"%");
                    break;
                case "userSn":
                    mpjQueryWrapper.like("U.sn","%"+searchKeyword+"%");
                    break;
                case "nickname":
                    mpjQueryWrapper.like("U.nickname","%"+searchKeyword+"%");
                    break;
            }
        }

        // 状态筛选
        if (StringUtils.isNotNull(searchValidate.getStatus()) && searchValidate.getStatus() > 0) {
            Integer status = searchValidate.getStatus().equals(2) ? 0 : 1;
            mpjQueryWrapper.eq("t.is_show", status);
        }

        // 等级筛选
        if (StringUtils.isNotNull(searchValidate.getLevel()) && searchValidate.getLevel() > 0) {
            // 好评
            if (searchValidate.getLevel() == 1) {
                mpjQueryWrapper.ge("t.goods_score", 4);
            }
            // 中评
            if (searchValidate.getLevel() == 2) {
                mpjQueryWrapper.eq("t.goods_score", 3);
            }
            // 差评
            if (searchValidate.getLevel() == 3) {
                mpjQueryWrapper.le("t.goods_score", 2);
            }
        }

        // 时间筛选
        goodsCommentMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "datetime:startTime-endTime@t.create_time:long",
        });

        // 扩展查询
        Map<String, Object> extend = new LinkedHashMap<>();
        MPJQueryWrapper<GoodsComment> allMPJ = mpjQueryWrapper.clone();
        MPJQueryWrapper<GoodsComment> waitReplyMPJ = mpjQueryWrapper.clone();
        MPJQueryWrapper<GoodsComment> repliedMPJ = mpjQueryWrapper.clone();
        extend.put("all", goodsCommentMapper.selectCount(allMPJ.select("t.id")));
        extend.put("waitReply", goodsCommentMapper.selectCount(waitReplyMPJ.select("t.id").eq("is_reply", 0)));
        extend.put("replied", goodsCommentMapper.selectCount(repliedMPJ.select("t.id").eq("is_reply", 1)));

        // 回复筛选
        if (StringUtils.isNotNull(searchValidate.getType()) && searchValidate.getType() > 0) {
            Integer type = searchValidate.getType().equals(2) ? 1 : 0;
            mpjQueryWrapper.eq("t.is_reply", type);
        }

        mpjQueryWrapper
                .selectAll(GoodsComment.class)
                .orderByDesc("t.id")
                .select("OG.goods_name,OG.goods_image,OG.goods_sku_value,U.nickname");

        // 分页查询
        IPage<GoodsCommentVo> iPage = goodsCommentMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                GoodsCommentVo.class,
                mpjQueryWrapper);

        // 数据出来
        for (GoodsCommentVo vo : iPage.getRecords()) {
            List<String> banner = new LinkedList<>();
            List<String> images = ListUtils.stringToListAsStr(vo.getImages().toString(), ",");
            for (String url : images) {
                banner.add(UrlUtils.toAbsoluteUrl(url));
            }
            vo.setImages(banner);
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setGoodsImage(UrlUtils.toAbsoluteUrl(vo.getGoodsImage()));
            vo.setScoreMsg(vo.getGoodsScore());
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 评论回复
     *
     * @author fzr
     * @param replyValidate 回复参数
     */
    @Override
    public void reply(GoodsCommentReplyValidate replyValidate) {
        GoodsComment goodsComment = goodsCommentMapper.selectOne(
                new QueryWrapper<GoodsComment>()
                    .eq("id", replyValidate.getId())
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(goodsComment, "评论记录不存在!");

        goodsComment.setReplyContent(replyValidate.getContent());
        goodsComment.setIsReply(1);
        goodsComment.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsCommentMapper.updateById(goodsComment);
    }

    /**
     * 评论状态
     *
     * @author fzr
     * @param id 评论ID
     */
    @Override
    public void change(Integer id) {
        GoodsComment goodsComment = goodsCommentMapper.selectOne(
                new QueryWrapper<GoodsComment>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(goodsComment, "评论记录不存在!");

        goodsComment.setIsShow(goodsComment.getIsShow().equals(0) ? 1 : 0);
        goodsComment.setUpdateTime(System.currentTimeMillis() / 1000);
        goodsCommentMapper.updateById(goodsComment);
    }

}
