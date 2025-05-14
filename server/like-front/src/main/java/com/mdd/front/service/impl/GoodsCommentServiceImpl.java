package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.enums.OrderEnum;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.IGoodsCommentService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.goods.GoodsCommentCreateValidate;
import com.mdd.front.validate.goods.GoodsDetailCommentListValidate;
import com.mdd.front.vo.goods.CommentListDetailVo;
import com.mdd.front.vo.goods.CommentListVo;
import com.mdd.front.vo.goods.GoodsDetailCommentVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.goods.GoodsComment;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.goods.GoodsCommentMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品评价服务实现类
 */
@Service
public class GoodsCommentServiceImpl implements IGoodsCommentService {

    @Resource
    private OrderGoodsMapper orderGoodsMapper;

    @Resource
    private GoodsCommentMapper goodsCommentMapper;

    /**
     * 添加评论
     *
     * @author mjf
     * @param commentValidate GoodsCommentValidate
     */
    @Override
    @Transactional
    public void add(GoodsCommentCreateValidate commentValidate) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 检查订单商品是否存在，是否已评价， 订单是否已完成
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("id", commentValidate.getOrderGoodsId())
                .last("limit 1"));

        Assert.notNull(orderGoods, "订单商品不存在");
        if (orderGoods.getIsComment() == 1) {
            throw new OperateException("该商品已评价");
        }

        // 添加评价
        GoodsComment goodsComment = new GoodsComment();
        goodsComment.setGoodsId(orderGoods.getGoodsId());
        goodsComment.setGoodsSkuId(orderGoods.getGoodsSkuId());
        goodsComment.setUserId(userId);
        goodsComment.setOrderId(orderGoods.getOrderId());
        goodsComment.setOrderGoodsId(orderGoods.getId());
        goodsComment.setGoodsScore(commentValidate.getScore());
        goodsComment.setContent(commentValidate.getContent());
        goodsComment.setServiceScore(commentValidate.getServiceScore());
        goodsComment.setDescribeScore(commentValidate.getDescribeScore());
        goodsComment.setLogisticsScore(commentValidate.getLogisticsScore());
        goodsComment.setCreateTime(System.currentTimeMillis() / 1000);
        // 图片处理
        if (!commentValidate.getImages().isEmpty()) {
            StringBuilder commentImages = new StringBuilder();
            for (String image : commentValidate.getImages()) {
                String imageItem = UrlUtils.toRelativeUrl(image) + ",";
                commentImages.append(imageItem);
            }
            goodsComment.setImages(commentImages.substring(0, commentImages.length() - 1));
        }
        goodsCommentMapper.insert(goodsComment);

        // 更新订单商品表评价状态
        orderGoods.setIsComment(1);
        orderGoods.setUpdateTime(System.currentTimeMillis() / 1000);
        orderGoodsMapper.updateById(orderGoods);
    }

    /**
     * 商品评论列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param isComment Integer
     * @return PageResult<CommentListVo>
     */
    @Override
    public PageResult<CommentListVo> list(PageValidate pageValidate, Integer isComment) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        // 查询分页订单商品
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        IPage<OrderGoods> iPage = orderGoodsMapper.selectJoinPage(new Page<>(pageNo, pageSize),
                OrderGoods.class,
                new MPJQueryWrapper<OrderGoods>()
                        .selectAll(OrderGoods.class)
                        .innerJoin("?_order o ON o.id = t.order_id".replace("?_", GlobalConfig.tablePrefix))
                        .innerJoin("?_user u ON u.id = t.user_id".replace("?_", GlobalConfig.tablePrefix))
                        .eq("u.is_close", 0)
                        .eq("t.user_id", userId)
                        .eq("t.is_comment", isComment)
                        .eq("o.order_status", OrderEnum.ORDER_STATUS_COMPLETED.getCode())
                        .orderByDesc("t.id"));

        List<CommentListVo> list = new ArrayList<>();
        for (OrderGoods orderGoods : iPage.getRecords()) {
            CommentListVo commentListVo = new CommentListVo();
            BeanUtils.copyProperties(orderGoods, commentListVo);
            commentListVo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
            commentListVo.setOrderGoodsId(orderGoods.getId());
            // 查询评论
            GoodsComment goodsComment = goodsCommentMapper.selectOne(new QueryWrapper<GoodsComment>()
                    .eq("order_goods_id", orderGoods.getId())
                    .last("limit 1"));

            if (goodsComment != null) {
                CommentListDetailVo comment = new CommentListDetailVo();
                BeanUtils.copyProperties(goodsComment, comment);
                if (!goodsComment.getImages().isEmpty()) {
                    comment.setImages(__imageStrToList(goodsComment.getImages()));
                }
                comment.setCreateTime(TimeUtils.timestampToDate(goodsComment.getCreateTime()));
                commentListVo.setComment(comment);
            }
            list.add(commentListVo);
        }

        // 扩展数据
        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("wait", orderGoodsMapper.selectCount(new MPJQueryWrapper<OrderGoods>()
                .select("t.id")
                .innerJoin("?_order o ON o.id = t.order_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("o.order_status", OrderEnum.ORDER_STATUS_COMPLETED.getCode())
                .eq("t.user_id", userId)
                .eq("t.is_comment", 0)));
        extend.put("finish", orderGoodsMapper.selectCount(new MPJQueryWrapper<OrderGoods>()
                .select("t.id")
                .innerJoin("?_order o ON o.id = t.order_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("o.order_status", OrderEnum.ORDER_STATUS_COMPLETED.getCode())
                .eq("t.user_id", userId)
                .eq("t.is_comment", 1)));

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list, extend);
    }

    /**
     * 获取商品评论
     *
     * @author cjhao
     * @param goodsId Integer
     * @return GoodsCommentVo
     */
    @Override
    public GoodsDetailCommentVo getGoodsDetailComment(Integer goodsId) {
        GoodsDetailCommentVo goodsCommentVo = goodsCommentMapper.selectJoinOne(
                GoodsDetailCommentVo.class,
                new MPJQueryWrapper<GoodsComment>()
                        .eq("t.goods_id", goodsId)
                        .eq("t.is_show", 1)
                        .eq("u.is_close", 0)
                        .leftJoin("?_user u ON u.id=t.user_id".replace("?_", GlobalConfig.tablePrefix))
                        .leftJoin("?_order_goods og ON og.id = t.order_goods_id".replace("?_", GlobalConfig.tablePrefix))
                        .select("t.id,u.nickname,u.avatar,t.user_id,og.goods_sku_value,t.goods_score,t.content,t.reply_content," +
                                "t.describe_score,t.service_score,t.logistics_score,t.images,t.create_time")
                        .last("limit 1")
                        .orderByDesc("t.id"));

        if (goodsCommentVo != null) {
            goodsCommentVo.setAvatar(UrlUtils.toAbsoluteUrl(goodsCommentVo.getAvatar()));
            goodsCommentVo.setNickname(ToolUtils.caseStarSymbol(goodsCommentVo.getNickname()));
            goodsCommentVo.setScoreMsg(goodsCommentVo.getGoodsScore());
            goodsCommentVo.setCreateTime(TimeUtils.timestampToDate(goodsCommentVo.getCreateTime(), "yyyy-MM-dd HH:mm"));
            String images = goodsCommentVo.getImages();
            if (!images.isEmpty()) {
                goodsCommentVo.setImagesList(__imageStrToList(images));
            }
        }
        return goodsCommentVo;
    }


    /**
     * 获取商品页评论列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param commentListValidate GoodsDetailCommentListValidate
     * @return PageResult<GoodsDetailCommentVo>
     */
    @Override
    public PageResult<GoodsDetailCommentVo> getGoodsDetailCommentList(PageValidate pageValidate, GoodsDetailCommentListValidate commentListValidate) {
        // 查询关联对应的商品评论
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        Integer goodsId = commentListValidate.getGoodsId();
        Integer hasImage = commentListValidate.getHasImage();

        MPJQueryWrapper<GoodsComment> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.selectAll(GoodsComment.class)
                .select("u.nickname,u.avatar,og.goods_sku_value")
                .innerJoin("?_user u ON u.id = t.user_id" .replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_order_goods og ON og.id = t.order_goods_id" .replace("?_", GlobalConfig.tablePrefix))
                .eq("u.is_close", 0)
                .eq("t.is_show", 1)
                .eq("t.goods_id", goodsId)
                .orderByDesc("t.id");

        if (hasImage != null && hasImage == 1) {
            mpjQueryWrapper.isNotNull("images").ne("images", "");
        }

        IPage<GoodsDetailCommentVo> iPage = goodsCommentMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                GoodsDetailCommentVo.class,
                mpjQueryWrapper);

        for (GoodsDetailCommentVo vo : iPage.getRecords()) {
            vo.setScoreMsg(vo.getGoodsScore());
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            // 图片处理
            if (!vo.getImages().isEmpty()) {
                vo.setImagesList(this.__imageStrToList(vo.getImages()));
            }
        }

        // 扩展参数
        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("all", goodsCommentMapper.selectCount(new QueryWrapper<GoodsComment>().eq("is_show", 1).eq("goods_id", goodsId)));
        extend.put("has_image", goodsCommentMapper.selectCount(new QueryWrapper<GoodsComment>()
                .eq("is_show", 1)
                .eq("goods_id", goodsId)
                .isNotNull("images")
                .ne("images", "")));

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }


    /**
     * 逗号分隔图片字符串转图片集合
     *
     * @author mjf
     * @param imageStr String
     * @return List<String>
     */
    private List<String> __imageStrToList(String imageStr) {
        String[] imageList = imageStr.split(",");
        List<String> imagesLists = new LinkedList<>();
        for (String image : imageList) {
            imagesLists.add(UrlUtils.toAbsoluteUrl(image));
        }
        return imagesLists;
    }

}
