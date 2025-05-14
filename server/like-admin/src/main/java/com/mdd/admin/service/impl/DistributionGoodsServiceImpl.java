package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.service.IDistributionGoodsService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsJoinValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsSearchValidate;
import com.mdd.admin.validate.distribution.DistributionGoodsSetValidate;
import com.mdd.admin.vo.distribution.DistributionGoodsDetailVo;
import com.mdd.admin.vo.distribution.DistributionGoodsListedVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.distribution.DistributionGoods;
import com.mdd.common.entity.distribution.DistributionLevel;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsCategoryIndex;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.mapper.distribution.DistributionGoodsMapper;
import com.mdd.common.mapper.distribution.DistributionLevelMapper;
import com.mdd.common.mapper.goods.GoodsCategoryIndexMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 分销商品服务实现类
 */
@Service
public class DistributionGoodsServiceImpl implements IDistributionGoodsService {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;

    @Resource
    GoodsCategoryIndexMapper goodsCategoryIndexMapper;

    @Resource
    DistributionGoodsMapper distributionGoodsMapper;

    @Resource
    DistributionLevelMapper distributionLevelMapper;

    /**
     * 分销商品列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<DistributionGoodsListedVo>
     */
    @Override
    public PageResult<DistributionGoodsListedVo> list(PageValidate pageValidate, DistributionGoodsSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        List<Integer> disGoodsIds = new LinkedList<>();
        List<DistributionGoods> distributionGoods = distributionGoodsMapper.selectList(
                new QueryWrapper<DistributionGoods>()
                        .eq("is_distribution", 1)
                        .eq("is_delete", 0));

        disGoodsIds.add(0);
        for (DistributionGoods item : distributionGoods) {
            disGoodsIds.add(item.getGoodsId());
        }

        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,name,image,spec_type,min_price,max_price,status");
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));

        // 分销搜索
        if (StringUtils.isNotNull(searchValidate.getIsDistribution())) {
            if (searchValidate.getIsDistribution().equals(1)) {
                queryWrapper.in("id", disGoodsIds);
            } else if (searchValidate.getIsDistribution().equals(0)) {
                queryWrapper.notIn("id", disGoodsIds);
            }
        }

        // 关键词搜索
        if (StringUtils.isNotNull(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            queryWrapper.nested(wq->wq
                    .like("name", keyword).or()
                    .like("id", keyword).or()
                    .like("code", keyword));
        }

        // 状态搜索
        if (StringUtils.isNotNull(searchValidate.getStatus())) {
            if (searchValidate.getStatus().equals(2)) {
                queryWrapper.le("total_stock", 0);
            } else {
                queryWrapper.eq("status", searchValidate.getStatus());
            }
        }

        // 分类搜索
        if (StringUtils.isNotNull(searchValidate.getCategoryId())) {
             List<GoodsCategoryIndex> cList = goodsCategoryIndexMapper.selectList(
                     new QueryWrapper<GoodsCategoryIndex>()
                        .eq("category_id", searchValidate.getCategoryId()));

             List<Integer> goodsIds = new LinkedList<>();
             for (GoodsCategoryIndex item : cList) {
                 goodsIds.add(item.getGoodsId());
             }

             goodsIds.add(0);
             queryWrapper.in("id", goodsIds);
        }

        IPage<Goods> iPage = goodsMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);
        List<DistributionGoodsListedVo> list = new LinkedList<>();
        for (Goods goods : iPage.getRecords()) {
            DistributionGoodsListedVo vo = new DistributionGoodsListedVo();
            BeanUtils.copyProperties(goods, vo);

            vo.setImage(UrlUtils.toAbsoluteUrl(goods.getImage()));
            vo.setGoodsStatus(goods.getStatus().equals(1) ? "销售中" : "仓库中");
            vo.setDistributionStatus(disGoodsIds.contains(goods.getId()) ? "参与" : "不参与");
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 分销商品详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionGoodsDetailVo
     */
    @Override
    public DistributionGoodsDetailVo detail(Integer id) {
        Goods goods = goodsMapper.selectOne(new QueryWrapper<Goods>()
                .eq("id", id)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(goods, "商品不存在了!");

        List<DistributionGoods> distributionGoodsList = distributionGoodsMapper.selectList(
                new QueryWrapper<DistributionGoods>()
                        .eq("is_delete", 0)
                        .eq("goods_id", id));

        DistributionGoodsDetailVo vo = new DistributionGoodsDetailVo();
        vo.setId(goods.getId());
        vo.setCode(goods.getCode());
        vo.setName(goods.getName());
        vo.setImage(UrlUtils.toAbsoluteUrl(goods.getImage()));

        // 没有分销商品数据
        if (distributionGoodsList.size() == 0) {
            vo.setIsDistribution(0);
            vo.setRule(DistributionEnum.GOODS_RULE_LEVEL.getCode());
            vo.setRatioData(this.__defaultRatio(goods.getId()));
            return vo;
        }

        vo.setIsDistribution(distributionGoodsList.get(0).getIsDistribution());
        vo.setRule(distributionGoodsList.get(0).getRule());

        // 商品根据分销会员等级分佣
        if (distributionGoodsList.get(0).getRule().equals(DistributionEnum.GOODS_RULE_LEVEL.getCode())) {
            vo.setRatioData(this.__defaultRatio(goods.getId()));
        }

        // 商品单独设置分佣比例
        if (distributionGoodsList.get(0).getRule().equals(DistributionEnum.GOODS_RULE_SINGLE.getCode())) {
            List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>()
                    .eq("is_delete", 0)
                    .orderByAsc("weights"));

            List<DistributionGoodsDetailVo.ratioData> ratioDataList = new LinkedList<>();
            for (DistributionLevel level : levelList) {
                DistributionGoodsDetailVo.ratioData ratioData = new DistributionGoodsDetailVo.ratioData();
                BeanUtils.copyProperties(level, ratioData);
                List<DistributionGoodsDetailVo.skuItem> skuItemList = new LinkedList<>();
                for (DistributionGoods distributionGoods : distributionGoodsList) {
                    if (!level.getId().equals(distributionGoods.getLevelId())) {
                        continue;
                    }
                    DistributionGoodsDetailVo.skuItem skuItem = new DistributionGoodsDetailVo.skuItem();
                    GoodsSku goodsSku = goodsSkuMapper.selectById(distributionGoods.getSkuId());
                    skuItem.setId(goodsSku.getId());
                    skuItem.setSellPrice(goodsSku.getPrice());
                    skuItem.setSpecValueStr(goodsSku.getSkuValueArr());
                    skuItem.setLevelId(level.getId());
                    skuItem.setSelfRatio(distributionGoods.getSelfRatio());
                    skuItem.setFirstRatio(distributionGoods.getFirstRatio());
                    skuItem.setSecondRatio(distributionGoods.getSecondRatio());
                    skuItemList.add(skuItem);
                }
                ratioData.setSkuItems(skuItemList);
                ratioDataList.add(ratioData);
            }
            vo.setRatioData(ratioDataList);
        }

        return vo;
    }

    /**
     * 设置佣金
     *
     * @author mjf
     * @param setValidate DistributionGoodsSetValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void set(DistributionGoodsSetValidate setValidate) {
        // 删除旧数据
        DistributionGoods goods = new DistributionGoods();
        goods.setIsDelete(1);
        goods.setUpdateTime(System.currentTimeMillis() / 1000);
        goods.setDeleteTime(System.currentTimeMillis() / 1000);
        distributionGoodsMapper.update(goods, new QueryWrapper<DistributionGoods>()
                .eq("is_delete", 0)
                .eq("goods_id", setValidate.getGoodsId()));

        if (setValidate.getRule().equals(DistributionEnum.GOODS_RULE_SINGLE.getCode())) {
            // 单独设置佣金
            for (DistributionGoodsSetValidate.ratioData radioData : setValidate.getRatioData()) {
                DistributionGoods goodsData = new DistributionGoods();
                goodsData.setGoodsId(setValidate.getGoodsId());
                goodsData.setIsDistribution(setValidate.getIsDistribution());
                goodsData.setRule(setValidate.getRule());
                goodsData.setSelfRatio(radioData.getSelfRatio());
                goodsData.setFirstRatio(radioData.getFirstRatio());
                goodsData.setSecondRatio(radioData.getSecondRatio());
                goodsData.setSkuId(radioData.getSkuId());
                goodsData.setLevelId(radioData.getLevelId());
                goodsData.setCreateTime(System.currentTimeMillis() / 1000);
                goodsData.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionGoodsMapper.insert(goodsData);
            }
        } else {
            // 根据分销会员等级比例设置
            DistributionGoods goodsData = new DistributionGoods();
            goodsData.setGoodsId(setValidate.getGoodsId());
            goodsData.setIsDistribution(setValidate.getIsDistribution());
            goodsData.setRule(setValidate.getRule());
            goodsData.setCreateTime(System.currentTimeMillis() / 1000);
            goodsData.setUpdateTime(System.currentTimeMillis() / 1000);
            distributionGoodsMapper.insert(goodsData);
        }
    }

    /**
     * 参与/不参与分销
     *
     * @author mjf
     * @param joinValidate DistributionGoodsJoinValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void join(DistributionGoodsJoinValidate joinValidate) {
        for (Integer id : joinValidate.getIds()) {
            DistributionGoods goods = distributionGoodsMapper.selectOne(new QueryWrapper<DistributionGoods>()
                    .eq("is_delete", 0)
                    .eq("goods_id", id)
                    .last("limit 1"));
            if (goods != null) {
                // 更新改商品所有的记录
                DistributionGoods updateGoods = new DistributionGoods();
                updateGoods.setIsDistribution(joinValidate.getIsDistribution());
                updateGoods.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionGoodsMapper.update(updateGoods, new QueryWrapper<DistributionGoods>()
                        .eq("goods_id", goods.getGoodsId())
                        .eq("is_delete", 0));
            } else {
                // 新增一个该商品记录
                DistributionGoods insertGoods = new DistributionGoods();
                insertGoods.setGoodsId(id);
                insertGoods.setIsDistribution(joinValidate.getIsDistribution());
                insertGoods.setRule(DistributionEnum.GOODS_RULE_LEVEL.getCode());
                insertGoods.setCreateTime(System.currentTimeMillis() / 1000);
                insertGoods.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionGoodsMapper.insert(insertGoods);
            }
        }
    }

    /**
     * 默认分销等级分销比例
     *
     * @author mjf
     * @param goodsId Integer
     * @return List<ratioData>
     */
    private List<DistributionGoodsDetailVo.ratioData> __defaultRatio(Integer goodsId) {
        List<GoodsSku> goodsSkuList = goodsSkuMapper.selectList(new QueryWrapper<GoodsSku>()
                .eq("goods_id", goodsId));

        List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>()
                .eq("is_delete", 0)
                .orderByAsc("weights"));

        List<DistributionGoodsDetailVo.ratioData> ratioDataList = new LinkedList<>();
        for (DistributionLevel level : levelList) {
            DistributionGoodsDetailVo.ratioData ratioData = new DistributionGoodsDetailVo.ratioData();
            BeanUtils.copyProperties(level, ratioData);
            List<DistributionGoodsDetailVo.skuItem> skuItemList = new LinkedList<>();
            for (GoodsSku goodsSku : goodsSkuList) {
                DistributionGoodsDetailVo.skuItem skuItem = new DistributionGoodsDetailVo.skuItem();
                skuItem.setId(goodsSku.getId());
                skuItem.setSellPrice(goodsSku.getPrice());
                skuItem.setSpecValueStr(goodsSku.getSkuValueArr());
                skuItem.setLevelId(level.getId());
                skuItem.setSelfRatio(level.getSelfRatio());
                skuItem.setFirstRatio(level.getFirstRatio());
                skuItem.setSecondRatio(level.getSecondRatio());
                skuItemList.add(skuItem);
            }
            ratioData.setSkuItems(skuItemList);
            ratioDataList.add(ratioData);
        }
        return ratioDataList;
    }
}
