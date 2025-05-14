package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IGoodsService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.goods.GoodsCreateValidate;
import com.mdd.admin.validate.goods.GoodsSearchValidate;
import com.mdd.admin.validate.goods.GoodsUpdateValidate;
import com.mdd.admin.vo.goods.*;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.distribution.DistributionGoods;
import com.mdd.common.entity.goods.*;
import com.mdd.common.entity.seckill.SeckillGoods;
import com.mdd.common.enums.SeckillEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.distribution.DistributionGoodsMapper;
import com.mdd.common.mapper.goods.*;
import com.mdd.common.mapper.seckill.SeckillGoodsMapper;
import com.mdd.common.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 商品管理服务实现类
 */
@Service
public class GoodsServiceImpI implements IGoodsService {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsImageMapper goodsImageMapper;

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    GoodsCategoryIndexMapper goodsCategoryIndexMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;

    @Resource
    GoodsSkuNameMapper goodsSkuNameMapper;

    @Resource
    GoodsSkuValueMapper goodsSkuValueMapper;

    @Resource
    SeckillGoodsMapper seckillGoodsMapper;

    @Resource
    DistributionGoodsMapper distributionGoodsMapper;

    @Override
    public PageResult<GoodsCommonVo> common(PageValidate pageValidate, GoodsSearchValidate goodsSearchValidate) {
        Integer page = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();

        // 基础搜索
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc("id");
        goodsMapper.setSearch(queryWrapper, goodsSearchValidate, new String[]{
                "=:code:str",
                "=:name:str",
                "=:type@status:int"
        });

        // 分类搜索
        Integer cid = goodsSearchValidate.getCategoryId();
        if (StringUtils.isNotNull(cid) && cid > 0) {
            List<GoodsCategoryIndex> categoryIndexList = goodsCategoryIndexMapper.selectList(
                    new QueryWrapper<GoodsCategoryIndex>()
                            .eq("category_id", cid));

            List<Integer> categoryIds = new ArrayList<>();
            for (GoodsCategoryIndex goodsCategoryIndex : categoryIndexList) {
                categoryIds.add(goodsCategoryIndex.getGoodsId());
            }

            if (categoryIds.size() > 0) {
                queryWrapper.in("id", categoryIds);
            }
        }

        IPage<Goods> iPage = goodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsCommonVo> list = new LinkedList<>();
        for (Goods record : iPage.getRecords()) {
            GoodsCommonVo vo = new GoodsCommonVo();
            BeanUtils.copyProperties(record, vo);
            vo.setCategory("");
            vo.setStatusMsg(record.getStatus().equals(0) ? "已下架" : "销售中");
            vo.setImage(UrlUtils.toAbsoluteUrl(record.getImage()));
            vo.setCreateTime(TimeUtils.timestampToDate(record.getCreateTime()));
            if (record.getMinPrice().subtract(record.getMaxPrice()).abs().compareTo(BigDecimal.ZERO) >= 0) {
                vo.setSellPrice("￥" + record.getMinPrice() + " ~ " + record.getMaxPrice());
            }

            // 商品分类
            List<Integer> cidList = new LinkedList<>();
            List<GoodsCategoryIndex> categoryIndexList = goodsCategoryIndexMapper.selectList(
                    new QueryWrapper<GoodsCategoryIndex>()
                            .eq("goods_id", record.getId()));

            for (GoodsCategoryIndex cIndex : categoryIndexList) {
                cidList.add(cIndex.getCategoryId());
            }

            if (cidList.size() > 0) {
                List<String> categoryNames = new LinkedList<>();
                List<GoodsCategory> categoryList = goodsCategoryMapper.selectList(
                        new QueryWrapper<GoodsCategory>()
                                .in("id", cidList)
                                .eq("is_delete", 0));
                for (GoodsCategory gc : categoryList) {
                    categoryNames.add(gc.getName());
                }
                vo.setCategory(ListUtils.listToStringByStr(categoryNames, ","));
            }

            // 是否秒杀
            long currTime = System.currentTimeMillis() / 1000;
            long isSeckill = seckillGoodsMapper.selectJoinCount(new MPJQueryWrapper<SeckillGoods>()
                    .eq("t.goods_id", record.getId())
                    .ne("SA.status", SeckillEnum.STATUS_END.getCode())
                    .ge("SA.end_time", currTime)
                    .innerJoin("?_seckill_activity SA ON t.seckill_id=SA.id".replace("?_", GlobalConfig.tablePrefix)));

            vo.setIsSeckill(isSeckill > 0 ? 1 : 0);

            // 商品规格
            List<GoodsSkuVo> skuVos = new LinkedList<>();
            List<GoodsSku> goodsSkus = goodsSkuMapper.selectList(new QueryWrapper<GoodsSku>().eq("goods_id", vo.getId()));
            for (GoodsSku sku : goodsSkus) {
                GoodsSkuVo skuVo = new GoodsSkuVo();
                BeanUtils.copyProperties(sku, skuVo);
                skuVo.setImage(UrlUtils.toAbsoluteUrl(sku.getImage()));
                skuVos.add(skuVo);
            }

            vo.setSkuList(skuVos);
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 商品列表
     *
     * @author cjhao
     * @param pageValidate 分页参数
     * @param goodsSearchValidate 搜索参数
     * @return PageResult<GoodsListVo>
     */
    @Override
    public PageResult<GoodsListedVo> list(PageValidate pageValidate, GoodsSearchValidate goodsSearchValidate) {
        Integer page = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0)
            .orderByDesc(Arrays.asList("sort", "id"));


        // 商品code搜索
        if (StringUtils.isNotEmpty(goodsSearchValidate.getCode())) {
            queryWrapper.like("code", goodsSearchValidate.getCode());
        }

        // 商品名称搜索
        if (StringUtils.isNotEmpty(goodsSearchValidate.getName())) {
            queryWrapper.like("name", goodsSearchValidate.getName());
        }

        // 商品分类搜索
        if (null != goodsSearchValidate.getCategoryId() && goodsSearchValidate.getCategoryId() > 0) {
            List<GoodsCategoryIndex> categoryIndexList = goodsCategoryIndexMapper.selectList(
                    new QueryWrapper<GoodsCategoryIndex>()
                            .eq("category_id", goodsSearchValidate.getCategoryId()));

            List<Integer> categoryIds = new ArrayList<>();
            for (GoodsCategoryIndex goodsCategoryIndex : categoryIndexList) {
                categoryIds.add(goodsCategoryIndex.getGoodsId());
            }

            if (categoryIds.size() > 0) {
                queryWrapper.in("id", categoryIds);
            } else {
                queryWrapper.apply("1=0");
            }
        }

        // 按类型查找
        Map<String, Object> statistics = this.__statistics(queryWrapper.clone());
        Integer type = goodsSearchValidate.getType();
        if(StringUtils.isNotNull(type)){
            switch (type) {
                case 1:
                    queryWrapper.eq("status", 1);
                    break;
                case 2:
                    queryWrapper.apply("stock_warning > total_stock");
                    break;
                case 3:
                    queryWrapper.eq("status", 0);
                    break;
            }
        }
        IPage<Goods> iPage = goodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsListedVo> list = new LinkedList<>();
        for (Goods record : iPage.getRecords()) {
            GoodsListedVo goodsListedVo = new GoodsListedVo();
            BeanUtils.copyProperties(record, goodsListedVo);
            goodsListedVo.setImage(UrlUtils.toAbsoluteUrl(record.getImage()));
            goodsListedVo.setSellPrice(String.valueOf(record.getMinPrice()));
            goodsListedVo.setCreateTime(TimeUtils.timestampToDate(record.getCreateTime()));

            if (!(record.getMaxPrice().compareTo(record.getMinPrice()) == 0) && record.getSpecType().equals(2)) {
                goodsListedVo.setSellPrice(record.getMinPrice() + "~" + record.getMaxPrice());
            }

            list.add(goodsListedVo);
        }

        statistics.put("allNum", iPage.getTotal());
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list, statistics);
    }

    /**
     * 商品详情
     *
     * @author cjhao
     * @param id 主键
     * @return GoodsDetailVo
     */
    @Override
    public GoodsDetailVo detail(Integer id) {
        // 基础信息
        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(goods, "商品不存在了!");
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        BeanUtils.copyProperties(goods,goodsDetailVo);

        // 轮播图片
        List<GoodsImage> goodsImages = goodsImageMapper.selectList(new QueryWrapper<GoodsImage>().eq("goods_id", id));
        List<String> goodsImage = new LinkedList<>();
        for (GoodsImage image : goodsImages) {
            goodsImage.add(UrlUtils.toAbsoluteUrl(image.getImage()));
        }
        goodsDetailVo.setGoodsImage(goodsImage);

        // 商品分类
        List<GoodsCategoryIndex> goodsCategoryIndexList = goodsCategoryIndexMapper.selectList(new QueryWrapper<GoodsCategoryIndex>().eq("goods_id",id));
        List<Integer> goodsCategory = new LinkedList<>();
        for (GoodsCategoryIndex goodsCategoryIndex : goodsCategoryIndexList) {
            goodsCategory.add(goodsCategoryIndex.getCategoryId());
        }
        goodsDetailVo.setCategoryId(goodsCategory);

        // 商品规格
        goodsDetailVo.setSpecValueList(goodsSkuMapper.selectList(new QueryWrapper<GoodsSku>().eq("goods_id", id)));

        // 商品SKU
        List<GoodsSkuName> goodsSkuNameList = goodsSkuNameMapper.selectList(new QueryWrapper<GoodsSkuName>().eq("goods_id", id));
        List<GoodsSkuValue> goodsSkuValueList = goodsSkuValueMapper.selectList(new QueryWrapper<GoodsSkuValue>().eq("goods_id", id));
        List<SpecValueVo> specValue = new ArrayList<>();
        for (GoodsSkuName goodsSkuName : goodsSkuNameList) {
            SpecValueVo specValueVo = new SpecValueVo();
            BeanUtils.copyProperties(goodsSkuName,specValueVo);
            List<SpecListVo> specListVos = new ArrayList<>();
            for (GoodsSkuValue goodsSkuValue : goodsSkuValueList) {
                if(goodsSkuValue.getSkuNameId().equals(goodsSkuName.getId())){
                    SpecListVo specListVo = new SpecListVo();
                    BeanUtils.copyProperties(goodsSkuValue,specListVo);
                    specListVos.add(specListVo);
                }
            }
            specValueVo.setSpecList(specListVos);
            specValue.add(specValueVo);
        }

        goodsDetailVo.setSpecValue(specValue);

        // 转绝对路径
        String domain = RequestUtils.domain();
        String engine = ConfigUtils.get("storage", "default", "local");
        if (!engine.equals("") && !engine.equals("local")) {
            Map<String, String> mapStorage = ConfigUtils.getMap("storage", engine);
            domain = mapStorage.getOrDefault("domain", RequestUtils.domain());
        }
        String content = goods.getContent().replaceAll("##domain##", domain);
        goodsDetailVo.setContent(content);

        return goodsDetailVo;
    }

    /**
     * 商品新增
     *
     * @author cjhao
     * @param createValidate 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(GoodsCreateValidate createValidate) {
        // 笛卡尔积规格
        createValidate.setServiceSpecValueList(this.__cartesianSpec(createValidate));

        // 验证商品数据
        this.__checkAddGoodsData(createValidate);

        // 转相对路径
        String domain = RequestUtils.domain();
        String engine = ConfigUtils.get("storage", "default", "local");
        if (!engine.equals("local")) {
            Map<String, String> mapStorage = ConfigUtils.getMap("storage", engine);
            domain = mapStorage.getOrDefault("domain", RequestUtils.domain());
        }
        String content = createValidate.getContent().replaceAll(domain, "##domain##");

        // 添加商品信息
        Goods goods = new Goods();
        goods.setCode(createValidate.getCode());
        goods.setName(createValidate.getName());
        goods.setStatus(createValidate.getStatus());
        goods.setDescription(createValidate.getDescription());
        goods.setVideoStatus(createValidate.getVideoStatus());
        goods.setVideoSource(createValidate.getVideoSource());
        goods.setVideoCover(createValidate.getVideoCover());
        goods.setVideo(createValidate.getVideo());
        goods.setImage(UrlUtils.toRelativeUrl(createValidate.getGoodsImage().get(0)));
        goods.setExpressType(createValidate.getExpressType());
        goods.setExpressTemplateId(createValidate.getExpressTemplateId());
        goods.setVirtualSalesNum(createValidate.getVirtualSalesNum() < 0 ? 0 : createValidate.getVirtualSalesNum());
        goods.setVirtualClickNum(createValidate.getVirtualClickNum() < 0 ? 0 : createValidate.getVirtualClickNum());
        goods.setStockWarning(createValidate.getStockWarning());
        goods.setSpecType(createValidate.getSpecType());
        goods.setSort(createValidate.getSort() < 0 ? 0 : createValidate.getSort());
        goods.setContent(content);
        goods.setCreateTime(System.currentTimeMillis() / 1000);
        goods.setUpdateTime(System.currentTimeMillis() / 1000);
        goods.setIsExpress(createValidate.getIsExpress());
        goods.setIsSelffetch(createValidate.getIsSelffetch());
        goodsMapper.insert(goods);

        // 添加商品规格
        Map<String, Object> goodsSkuData = this.__addGoodsSpec(goods.getId(), createValidate);
        goods.setMinPrice((BigDecimal) goodsSkuData.get("minPrice"));
        goods.setMaxPrice((BigDecimal) goodsSkuData.get("maxPrice"));
        goods.setMinLineationPrice((BigDecimal) goodsSkuData.get("minLineationPrice"));
        goods.setMaxLineationPrice((BigDecimal) goodsSkuData.get("maxLineationPrice"));
        goods.setMaxLineationPrice((BigDecimal) goodsSkuData.get("maxLineationPrice"));
        goods.setTotalStock((Integer) goodsSkuData.get("totalStock"));
        goodsMapper.updateById(goods);

        // 添加商品图片
        for (String image : createValidate.getGoodsImage()) {
            GoodsImage goodsImage = new GoodsImage();
            goodsImage.setGoodsId(goods.getId());
            goodsImage.setImage(UrlUtils.toRelativeUrl(image));
            goodsImageMapper.insert(goodsImage);
        }

        // 添加商品分类
        for (Integer categoryId : createValidate.getCategoryId()) {
            GoodsCategoryIndex goodsCategoryIndex = new GoodsCategoryIndex();
            goodsCategoryIndex.setGoodsId(goods.getId());
            goodsCategoryIndex.setCategoryId(categoryId);
            goodsCategoryIndexMapper.insert(goodsCategoryIndex);
        }
    }

    /**
     * 商品编辑
     *
     * @author cjhao
     * @param updateValidate 参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(GoodsUpdateValidate updateValidate) {
        // 查询商品信息
        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("id", updateValidate.getId())
                        .eq("is_delete", 0));

        // 验证商品存在
        Assert.notNull(goods,"商品不存在了!");

        // 验证是否为营销活动中的商品
        if (goods.getIsActivity() == 1){
            throw new OperateException("商品正在参加活动中，不允许修改！");
        }

        // 旧的规格类型
        Integer goodsSpec = goods.getSpecType();

        // 转相对路径
        String engine = ConfigUtils.get("storage", "default", "local");
        String domain = RequestUtils.domain();
        if (!engine.equals("local")) {
            Map<String, String> mapStorage = ConfigUtils.getMap("storage", engine);
            domain = mapStorage.getOrDefault("domain", RequestUtils.domain());
        }
        String content = updateValidate.getContent().replaceAll(domain, "##domain##");

        // 更新商品信息
        goods.setName(updateValidate.getName());
        goods.setCode(updateValidate.getCode());
        goods.setDescription(updateValidate.getDescription());
        goods.setVideoStatus(updateValidate.getVideoStatus());
        goods.setVideoSource(updateValidate.getVideoSource());
        goods.setVideoCover(updateValidate.getVideoCover());
        goods.setVideo(updateValidate.getVideo());
        goods.setStatus(updateValidate.getStatus());
        goods.setImage(UrlUtils.toRelativeUrl(updateValidate.getGoodsImage().get(0)));
        goods.setExpressTemplateId(updateValidate.getExpressTemplateId());
        goods.setExpressType(updateValidate.getExpressType());
        goods.setVirtualSalesNum(updateValidate.getVirtualSalesNum() < 0 ? 0 : updateValidate.getVirtualSalesNum());
        goods.setVirtualClickNum(updateValidate.getVirtualClickNum() < 0 ? 0 : updateValidate.getVirtualClickNum());
        goods.setStockWarning(updateValidate.getStockWarning());
        goods.setSpecType(updateValidate.getSpecType());
        goods.setContent(content);
        goods.setSort(updateValidate.getSort() < 0 ? 0 :updateValidate.getSort());
        goods.setUpdateTime(System.currentTimeMillis() / 1000);
        goods.setIsExpress(updateValidate.getIsExpress());
        goods.setIsSelffetch(updateValidate.getIsSelffetch());
        goodsMapper.updateById(goods);

        // 更新商品分类
        goodsCategoryIndexMapper.delete(new QueryWrapper<GoodsCategoryIndex>().eq("goods_id", goods.getId()));
        for (Integer categoryId : updateValidate.getCategoryId()) {
            GoodsCategoryIndex goodsCategoryIndex = new GoodsCategoryIndex();
            goodsCategoryIndex.setGoodsId(goods.getId());
            goodsCategoryIndex.setCategoryId(categoryId);
            goodsCategoryIndexMapper.insert(goodsCategoryIndex);
        }

        // 删除商品轮播
        goodsImageMapper.delete(new QueryWrapper<GoodsImage>().eq("goods_id", goods.getId()));
        for (String image : updateValidate.getGoodsImage()) {
            GoodsImage goodsImage = new GoodsImage();
            goodsImage.setGoodsId(goods.getId());
            goodsImage.setImage(UrlUtils.toRelativeUrl(image));
            goodsImageMapper.insert(goodsImage);
        }

        // 处理商品规格
        Map<String, Object> goodsSkuData = this.__editGoodsSpec(goodsSpec, updateValidate);
        goods.setMaxPrice((BigDecimal) goodsSkuData.get("maxPrice"));
        goods.setMinPrice((BigDecimal) goodsSkuData.get("minPrice"));
        goods.setMinLineationPrice((BigDecimal) goodsSkuData.get("minLineationPrice"));
        goods.setMaxLineationPrice((BigDecimal) goodsSkuData.get("maxLineationPrice"));
        goods.setMaxLineationPrice((BigDecimal) goodsSkuData.get("maxLineationPrice"));
        goods.setTotalStock((Integer) goodsSkuData.get("totalStock"));
        goodsMapper.updateById(goods);

        // 验证是否为分销商品
        DistributionGoods distributionGoods = distributionGoodsMapper.selectOne(new QueryWrapper<DistributionGoods>()
                .eq("goods_id", goods.getId())
                .eq("is_delete", 0)
                .last("limit 1"));

        if (distributionGoods != null){
            distributionGoods.setIsDistribution(0);
            distributionGoods.setRule(1);
            distributionGoods.setUpdateTime(System.currentTimeMillis() / 1000);
            return false;
        }
        return true;
    }

    /**
     * 商品删除
     *
     * @author cjhao
     * @param id 商品ID
     */
    @Override
    public void del(Integer id) {
        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("id", id)
                        .eq("is_delete", 0));

        Assert.notNull(goods,"商品不存在了!");

        // 验证是否为营销活动中的商品
        if (goods.getIsActivity() == 1){
            throw new OperateException("商品正在参加活动中，不允许删除！");
        }

        goods.setIsDelete(1);
        goods.setUpdateTime(TimeUtils.timestamp());
        goodsMapper.updateById(goods);
    }

    /**
     * 商品状态
     *
     * @author cjhao
     * @param id 商品ID
     */
    public void change(Integer id){
        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("id", id)
                        .eq("is_delete", 0));

        Assert.notNull(goods,"商品不存在了!");

        // 验证是否为营销活动中的商品
        if (goods.getIsActivity() == 1){
            throw new OperateException("商品正在参加活动中，不允许修改状态！");
        }

        goods.setStatus(goods.getStatus()==0?1:0);
        goods.setUpdateTime(TimeUtils.timestamp());
        goodsMapper.updateById(goods);
    }

    /**
     * 商品排序
     *
     * @author fzr
     * @param id 主键
     * @param sort 排序
     */
    @Override
    public void sort(Integer id, Integer sort) {
        if (sort < 0) {
            throw new OperateException("排序值需大于0");
        }

        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("id", id)
                        .eq("is_delete", 0));

        Assert.notNull(goods,"商品不存在了!");

        goods.setSort(sort);
        goods.setUpdateTime(TimeUtils.timestamp());
        goodsMapper.updateById(goods);
    }

    /**
     * 批量删除
     *
     * @author fzr
     * @param ids 主键集
     */
    @Override
    public void batchDelete(List<Integer> ids) {
        List<Goods> goodsList = goodsMapper.selectList(new QueryWrapper<Goods>()
                .in("id", ids)
                .eq("is_delete", 0));

        for (Goods goods : goodsList) {
            goods.setStatus(0);
            goods.setIsDelete(1);
            goods.setUpdateTime(System.currentTimeMillis() / 1000);
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 批量上架
     *
     * @author fzr
     * @param ids 主键集
     */
    @Override
    public void batchUpper(List<Integer> ids) {
        List<Goods> goodsList = goodsMapper.selectList(new QueryWrapper<Goods>()
                .in("id", ids)
                .eq("is_delete", 0));

        for (Goods goods : goodsList) {
            goods.setStatus(1);
            goods.setUpdateTime(System.currentTimeMillis() / 1000);
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 批量下架
     *
     * @author fzr
     * @param ids 主键集
     */
    @Override
    public void batchLower(List<Integer> ids) {
        List<Goods> goodsList = goodsMapper.selectList(new QueryWrapper<Goods>()
                .in("id", ids)
                .eq("is_delete", 0));

        for (Goods goods : goodsList) {
            goods.setStatus(0);
            goods.setUpdateTime(System.currentTimeMillis() / 1000);
            goodsMapper.updateById(goods);
        }
    }

    /**
     * 统计商品
     *
     * @author cjhao
     * @param queryWrapper 搜索器
     * @return Map<Object>
     */
    private Map<String,Object> __statistics(QueryWrapper<Goods> queryWrapper) {
        Map<String,Object> statistics = new LinkedHashMap<>();
        // 销售中的
        QueryWrapper<Goods> salesNumWrapper = queryWrapper.clone();
        Long salesNum = goodsMapper.selectCount(salesNumWrapper.eq("status", 1));
        // 库存预警
        QueryWrapper<Goods> warningNumWrapper = queryWrapper.clone();
        Long warningNum = goodsMapper.selectCount(warningNumWrapper.apply("stock_warning > total_stock"));
        // 仓库中的
        QueryWrapper<Goods> storageNumWrapper = queryWrapper.clone();
        Long storageNum = goodsMapper.selectCount(storageNumWrapper.eq("status", 0));
        // 返回数据
        statistics.put("salesNum",salesNum);
        statistics.put("warningNum",warningNum);
        statistics.put("storageNum",storageNum);
        return statistics;

    }

    /**
     * 处理商品规格(创建)
     *
     * @author cjhao
     * @param goodsCreateValidate 参数
     * @return List<GoodsSkuVo>
     */
    private List<GoodsSkuVo> __cartesianSpec(GoodsCreateValidate goodsCreateValidate) {
        List<SpecValueVo> specValue = goodsCreateValidate.getSpecValue();
        List<List<String>> specLists = new ArrayList<>();
        List<GoodsSkuVo> specValueList = goodsCreateValidate.getSpecValueList();
        if (specValueList.size() == 1) {
            return specValueList;
        }

        for (SpecValueVo specValueVo : specValue) {
            List<SpecListVo> specList = specValueVo.getSpecList();
            List<String> specName = new ArrayList<>();
            for (SpecListVo specListVo : specList) {
                specName.add(specListVo.getValue());
            }
            specLists.add(specName);
        }

        List<String> firstList = specLists.get(0);
        specLists.remove(0);
        List<SpecValueListVo> VoLists = new ArrayList<>();
        int firstKey = 0;
        for (String first : firstList) {
            SpecValueListVo specValueListVo = new SpecValueListVo();
            specValueListVo.setIds(Integer.toString(firstKey));
            specValueListVo.setValue(first);
            firstKey++;
            VoLists.add(specValueListVo);
        }

        for (List<String> strings : specLists) {
            List<SpecValueListVo> list = new ArrayList<>();
            for (SpecValueListVo voList : VoLists) {
                int specKey = 0;
                for (String spec : strings) {
                    SpecValueListVo specValueListVo = new SpecValueListVo();
                    specValueListVo.setValue(voList.getValue() + "," + spec);
                    specValueListVo.setIds(voList.getIds() + "," + specKey);
                    list.add(specValueListVo);
                    specKey++;
                }
            }
            VoLists = list;
        }

        List<GoodsSkuVo> serviceSpecGoods = new ArrayList<>();
        for (SpecValueListVo voList : VoLists) {
            String ids = "";
            for (GoodsSkuVo goodsSku : specValueList) {
                if (goodsSku.getIds().equals(voList.getIds())) {
                    ids = goodsSku.getIds();
                    serviceSpecGoods.add(goodsSku);
                }
            }
            if (ids.equals("")) {
                throw new OperateException("商品规格信息错误!");
            }

        }

        return serviceSpecGoods;
    }

    /**
     * 处理商品规格(更新)
     *
     * @author cjhao
     * @param goodsUpdateValidate 参数
     * @return List<GoodsSkuVo>
     */
    public List<GoodsSkuVo> __cartesianSpec(GoodsUpdateValidate goodsUpdateValidate){
        List<List<String>> specLists   = new ArrayList<>();
        List<SpecValueVo> specValue    = goodsUpdateValidate.getSpecValue();
        List<GoodsSkuVo> specValueList = goodsUpdateValidate.getSpecValueList();

        if (1 == specValueList.size()) {
            return specValueList;
        }

        for (SpecValueVo specValueVo : specValue) {
            List<String> specName = new ArrayList<>();
            List<SpecListVo> specList = specValueVo.getSpecList();
            for (SpecListVo specListVo : specList) {
                specName.add(specListVo.getValue());
            }
            specLists.add(specName);
        }

        List<SpecValueListVo> VoLists = new ArrayList<>();
        List<String> firstList = specLists.get(0);
        specLists.remove(0);
        int firstKey = 0;
        for (String first : firstList) {
            SpecValueListVo specValueListVo = new SpecValueListVo();
            specValueListVo.setValue(first);
            specValueListVo.setIds(Integer.toString(firstKey));
            firstKey++;
            VoLists.add(specValueListVo);
        }

        for (List<String> strings : specLists) {
            List<SpecValueListVo> list = new ArrayList<>();
            for (SpecValueListVo voList : VoLists) {
                int specKey = 0;
                for (String spec : strings) {
                    SpecValueListVo specValueListVo = new SpecValueListVo();
                    specValueListVo.setIds(voList.getIds() + "," + specKey);
                    specValueListVo.setValue(voList.getValue() + "," + spec);
                    list.add(specValueListVo);
                    specKey++;
                }

            }
            VoLists = list;
        }

        List<GoodsSkuVo> serviceSpecGoods = new ArrayList<>();
        for (SpecValueListVo voList : VoLists) {
            String ids = "";
            for (GoodsSkuVo goodsSku : specValueList) {
                if (goodsSku.getIds().equals(voList.getIds())) {
                    ids = goodsSku.getIds();
                    serviceSpecGoods.add(goodsSku);
                }
            }
            if (ids.equals("")) {
                throw new OperateException("商品规格信息错误");
            }
        }

        return serviceSpecGoods;
    }

    /**
     * 添加商品规格
     *
     * @author cjhao
     * @param goodsId 商品ID
     * @param goodsCreateValidate 参数
     * @return Map<Object>
     */
    private Map<String, Object> __addGoodsSpec(Integer goodsId, GoodsCreateValidate goodsCreateValidate) {
        Integer specType = goodsCreateValidate.getSpecType();
        Map<String, Object> goodsSkuData = new LinkedHashMap<>();

        for (GoodsSkuVo goodSkuVo : goodsCreateValidate.getSpecValueList()) {
            if (goodSkuVo.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new OperateException("销售价格不能少于等于0");
            }
            if (goodSkuVo.getStock() < 0) {
                throw new OperateException("库存数量不能少于0");
            }
        }

        //单规格
        if (1 == specType) {
            //规格名
            GoodsSkuName goodsSkuName = new GoodsSkuName();
            goodsSkuName.setGoodsId(goodsId);
            goodsSkuName.setName("默认");
            goodsSkuNameMapper.insert(goodsSkuName);
            //规格值
            GoodsSkuValue goodsSkuValue = new GoodsSkuValue();
            goodsSkuValue.setGoodsId(goodsId);
            goodsSkuValue.setValue("默认");
            goodsSkuValue.setSkuNameId(goodsSkuName.getId());
            goodsSkuValueMapper.insert(goodsSkuValue);
            //规格项
            GoodsSku goodsSku = new GoodsSku();
            BeanUtils.copyProperties(goodsCreateValidate.getSpecValueList().get(0), goodsSku);
            goodsSku.setGoodsId(goodsId);
            goodsSku.setSkuValueIds(goodsSkuValue.getId().toString());
            goodsSku.setSkuValueArr("默认");
            goodsSku.setImage(UrlUtils.toRelativeUrl(goodsSku.getImage()));
            goodsSkuMapper.insert(goodsSku);

            goodsSkuData.put("minPrice", goodsSku.getPrice());
            goodsSkuData.put("maxPrice", goodsSku.getPrice());
            goodsSkuData.put("minLineationPrice", goodsSku.getLinePrice());
            goodsSkuData.put("maxLineationPrice", goodsSku.getLinePrice());
            goodsSkuData.put("totalStock", goodsSku.getStock());

        } else {
            List<SpecValueVo> specValue = goodsCreateValidate.getSpecValue();
            Map<String, Integer> specListData = new LinkedHashMap<>();
            for (SpecValueVo specValueVo : specValue) {
                GoodsSkuName goodsSkuName = new GoodsSkuName();
                goodsSkuName.setGoodsId(goodsId);
                goodsSkuName.setName(specValueVo.getName());
                goodsSkuNameMapper.insert(goodsSkuName);
                List<SpecListVo> specList = specValueVo.getSpecList();
                for (SpecListVo specListVo : specList) {
                    GoodsSkuValue goodsSkuValue = new GoodsSkuValue();
                    goodsSkuValue.setGoodsId(goodsId);
                    goodsSkuValue.setSkuNameId(goodsSkuName.getId());
                    goodsSkuValue.setValue(specListVo.getValue());
                    goodsSkuValueMapper.insert(goodsSkuValue);
                    specListData.put(specListVo.getValue(), goodsSkuValue.getId());
                }
            }
            List<GoodsSkuVo> serviceSpecValueList = goodsCreateValidate.getServiceSpecValueList();

            List<BigDecimal> priceList = new LinkedList<>();
            List<BigDecimal> lineationPrice = new LinkedList<>();
            Integer totalStock = 0;
            for (GoodsSkuVo goodsSkuVo : serviceSpecValueList) {
                String skuValueArr = goodsSkuVo.getSkuValueArr();
                String[] skuValueArrList = skuValueArr.split(","+"");
                StringBuilder skuIds = new StringBuilder();
                for (String skuName : skuValueArrList) {
                    Integer skuNameId = specListData.get(skuName);
                    if (skuIds.toString().equals(""+"")) {
                        skuIds = new StringBuilder(skuNameId.toString());
                    } else {
                        skuIds.append(",").append(skuNameId.toString());
                    }
                }

                goodsSkuVo.setSkuValueIds(skuIds.toString());
                goodsSkuVo.setGoodsId(goodsId);
                GoodsSku goodsSku = new GoodsSku();
                BeanUtils.copyProperties(goodsSkuVo, goodsSku);
                goodsSkuMapper.insert(goodsSku);
                priceList.add(goodsSkuVo.getPrice());
                lineationPrice.add(StringUtils.isNull(goodsSkuVo.getLinePrice()) ? new BigDecimal(0) : goodsSkuVo.getLinePrice());
                totalStock += goodsSkuVo.getStock();
            }
            goodsSkuData.put("minPrice", Collections.min(priceList));
            goodsSkuData.put("maxPrice", Collections.max(priceList));
            goodsSkuData.put("minLineationPrice", Collections.min(lineationPrice));
            goodsSkuData.put("maxLineationPrice", Collections.max(lineationPrice));
            goodsSkuData.put("totalStock", totalStock);
        }
        return goodsSkuData;
    }

    /** 
     * 编辑商品规格
     *
     * @author cjhao
     * @param oldGoodsSpec 旧商品规格
     * @param goodsUpdateValidate 商品参数
     */
    private Map<String, Object> __editGoodsSpec(Integer oldGoodsSpec, GoodsUpdateValidate goodsUpdateValidate) {
        Map<String, Object> goodsSkuData = new LinkedHashMap<>();
        List<BigDecimal> priceList = new LinkedList<>();
        List<BigDecimal> lineationPrice = new LinkedList<>();
        Integer totalStock = 0;

        for (GoodsSkuVo goodSkuVo : goodsUpdateValidate.getSpecValueList()) {
            if (goodSkuVo.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new OperateException("销售价格不能少于等于0");
            }

            if (ObjectUtils.isEmpty(goodSkuVo.getLinePrice())) {
                goodSkuVo.setLinePrice(BigDecimal.ZERO);
            } else {
                if (goodSkuVo.getLinePrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new OperateException("划线价格不能少于0");
                }
            }

            if (ObjectUtils.isEmpty(goodSkuVo.getMarketPrice())) {
                goodSkuVo.setMarketPrice(BigDecimal.ZERO);
            } else {
                if (goodSkuVo.getMarketPrice().compareTo(BigDecimal.ZERO) < 0) {
                    throw new OperateException("市场价格不能少于0");
                }
            }

            if (ObjectUtils.isEmpty(goodSkuVo.getStock()) || goodSkuVo.getStock() < 0) {
                throw new OperateException("库存数量不能少于0");
            }

            if (ObjectUtils.isEmpty(goodSkuVo.getWeight())) {
                goodSkuVo.setWeight(0.0);
            } else {
                if (goodSkuVo.getWeight() < 0) {
                    throw new OperateException("重量值不能少于0");
                }
            }

            if (ObjectUtils.isEmpty(goodSkuVo.getVolume())) {
                goodSkuVo.setVolume(0.0);
            } else {
                if (goodSkuVo.getVolume() < 0) {
                    throw new OperateException("体积值不能少于0");
                }
            }

            if (ObjectUtils.isEmpty(goodSkuVo.getCode())) {
                goodSkuVo.setCode("");
            }
        }

        // 单规格数据保存
        if (1 == goodsUpdateValidate.getSpecType()){
            if(1 == oldGoodsSpec){
                // 单规格 =》单规格
                GoodsSku goodsSku = goodsSkuMapper.selectOne(new QueryWrapper<GoodsSku>().eq("goods_id", goodsUpdateValidate.getId()));
                BeanUtils.copyProperties(goodsUpdateValidate.getSpecValueList().get(0), goodsSku);
                goodsSku.setGoodsId(goodsUpdateValidate.getId());
                goodsSku.setSkuValueArr("默认");
                goodsSku.setImage(UrlUtils.toRelativeUrl(goodsSku.getImage()));
                goodsSkuMapper.updateById(goodsSku);

                priceList.add(goodsSku.getPrice());
                lineationPrice.add(goodsSku.getLinePrice());
                totalStock = goodsSku.getStock();
            } else {
                // 多规格 =》单规格
                Map<String, Object> whereMap = new LinkedHashMap<>();
                whereMap.put("goods_id",goodsUpdateValidate.getId());
                goodsSkuNameMapper.deleteByMap(whereMap);
                goodsSkuValueMapper.deleteByMap(whereMap);
                goodsSkuMapper.deleteByMap(whereMap);
                // 规格名
                GoodsSkuName goodsSkuName = new GoodsSkuName();
                goodsSkuName.setGoodsId(goodsUpdateValidate.getId());
                goodsSkuName.setName("默认");
                goodsSkuNameMapper.insert(goodsSkuName);
                // 规格值
                GoodsSkuValue goodsSkuValue = new GoodsSkuValue();
                goodsSkuValue.setGoodsId(goodsUpdateValidate.getId());
                goodsSkuValue.setValue("默认");
                goodsSkuValue.setSkuNameId(goodsSkuName.getId());
                goodsSkuValueMapper.insert(goodsSkuValue);
                // 规格项
                GoodsSku goodsSku = new GoodsSku();
                BeanUtils.copyProperties(goodsUpdateValidate.getSpecValueList().get(0), goodsSku);
                goodsSku.setGoodsId(goodsUpdateValidate.getId());
                goodsSku.setSkuValueIds(goodsSkuValue.getId().toString());
                goodsSku.setSkuValueArr("默认");
                goodsSku.setImage(UrlUtils.toRelativeUrl(goodsSku.getImage()));
                goodsSkuMapper.insert(goodsSku);

                priceList.add(goodsSku.getPrice());
                lineationPrice.add(goodsSku.getLinePrice());
                totalStock = goodsSku.getStock();
            }
        }

        // 多规格数据保存
        if (1 != goodsUpdateValidate.getSpecType()) {
            // 单规格 => 多规格
            if (1 == oldGoodsSpec) {
                Map<String,Object> whereMap = new LinkedHashMap<>();
                whereMap.put("goods_id",goodsUpdateValidate.getId());
                goodsSkuNameMapper.deleteByMap(whereMap);
                goodsSkuValueMapper.deleteByMap(whereMap);
                goodsSkuMapper.deleteByMap(whereMap);
                List<SpecValueVo> specValue = goodsUpdateValidate.getSpecValue();
                Map<String, Integer> specListData = new LinkedHashMap<>();
                for (SpecValueVo specValueVo : specValue) {
                    GoodsSkuName goodsSkuName = new GoodsSkuName();
                    goodsSkuName.setGoodsId(goodsUpdateValidate.getId());
                    goodsSkuName.setName(specValueVo.getName());
                    goodsSkuNameMapper.insert(goodsSkuName);
                    List<SpecListVo> specList = specValueVo.getSpecList();
                    for (SpecListVo specListVo : specList) {
                        GoodsSkuValue goodsSkuValue = new GoodsSkuValue();
                        goodsSkuValue.setGoodsId(goodsUpdateValidate.getId());
                        goodsSkuValue.setSkuNameId(goodsSkuName.getId());
                        goodsSkuValue.setValue(specListVo.getValue());
                        goodsSkuValueMapper.insert(goodsSkuValue);
                        specListData.put(specListVo.getValue(), goodsSkuValue.getId());
                    }
                }

                List<GoodsSkuVo> serviceSpecValueList = goodsUpdateValidate.getSpecValueList();
                for (GoodsSkuVo goodsSkuVo : serviceSpecValueList) {
                    String skuValueArr = goodsSkuVo.getSkuValueArr();
                    String[] skuValueArrList = skuValueArr.split(",");
                    StringBuilder skuIds = new StringBuilder();
                    for (String skuName : skuValueArrList) {
                        Integer skuNameId = specListData.get(skuName);
                        if (skuIds.toString().equals("")) {
                            skuIds = new StringBuilder(skuNameId.toString());
                        } else {
                            skuIds.append(",").append(skuNameId.toString());
                        }
                    }
                    goodsSkuVo.setSkuValueIds(skuIds.toString());
                    goodsSkuVo.setGoodsId(goodsUpdateValidate.getId());
                    GoodsSku goodsSku = new GoodsSku();
                    BeanUtils.copyProperties(goodsSkuVo, goodsSku);
                    goodsSkuMapper.insert(goodsSku);
                    priceList.add(goodsSkuVo.getPrice());
                    lineationPrice.add(goodsSkuVo.getLinePrice());
                    totalStock += goodsSkuVo.getStock();
                }
            } else {
                // 多规格 => 多规格
                goodsUpdateValidate.setServiceSpecValueList(this.__cartesianSpec(goodsUpdateValidate));
                List<SpecValueVo> specValueVoLists = goodsUpdateValidate.getSpecValue();
                List<GoodsSkuName> specNameLists = goodsSkuNameMapper.selectList(new QueryWrapper<GoodsSkuName>().eq("goods_id",goodsUpdateValidate.getId()));
                List<GoodsSkuValue> skuValueLists = goodsSkuValueMapper.selectList(new QueryWrapper<GoodsSkuValue>().eq("goods_id",goodsUpdateValidate.getId()));
                List<GoodsSku> goodsSkuLists = goodsSkuMapper.selectList(new QueryWrapper<GoodsSku>().eq("goods_id",goodsUpdateValidate.getId()));

                List<Integer> goodsSkuIds = new LinkedList<>();
                List<Integer> specValueNameIds = new LinkedList<>();
                List<Integer> goodsSkuValueIds = new LinkedList<>();
                Map<String,Integer> specListData = new LinkedHashMap<>();

                // 获取全部的规格id
                for (GoodsSkuName specNameList : specNameLists) {
                    specValueNameIds.add(specNameList.getId());
                }

                // 获取全部的规格值id
                for (GoodsSkuValue skuValueList : skuValueLists) {
                    goodsSkuValueIds.add(skuValueList.getId());
                }

                // 获取全部的规格id
                for (GoodsSku goodsSkuList : goodsSkuLists) {
                    goodsSkuIds.add(goodsSkuList.getId());
                }

                // 更新规格项信息
                for (SpecValueVo specValueVoList : specValueVoLists) {
                    GoodsSkuName goodsSkuName = new GoodsSkuName();
                    goodsSkuName.setName(specValueVoList.getName());
                    goodsSkuName.setGoodsId(goodsUpdateValidate.getId());
                    Integer specValueNameId = specValueVoList.getId();
                    if(specValueVoList.getId() != null && specValueVoList.getId() > 0){
                        goodsSkuNameMapper.updateById(goodsSkuName);
                    }else{
                        goodsSkuNameMapper.insert(goodsSkuName);
                        specValueNameId = goodsSkuName.getId();
                    }

                    specValueNameIds.remove(specValueNameId);
                    List<SpecListVo> specLists = specValueVoList.getSpecList();
                    for (SpecListVo specList : specLists) {
                        GoodsSkuValue goodsSkuValue = new GoodsSkuValue();
                        goodsSkuValue.setSkuNameId(specValueNameId);
                        goodsSkuValue.setGoodsId(goodsUpdateValidate.getId());
                        goodsSkuValue.setValue(specList.getValue());
                        Integer specValueId = specList.getId();
                        if(specValueId != null && specValueId > 0){
                            goodsSkuValueMapper.updateById(goodsSkuValue);
                            specListData.put(specList.getValue(),specList.getId());
                            goodsSkuValueIds.remove(specValueId);
                        }else{
                            specValueId = goodsSkuValueMapper.insert(goodsSkuValue);
                            specListData.put(specList.getValue(), goodsSkuValue.getId());
                        }
                    }
                }
                // 获取规格全部信息，更新规格信息
                List<GoodsSkuVo> serviceSpecValueList = goodsUpdateValidate.getServiceSpecValueList();
                for (GoodsSkuVo goodsSkuVo : serviceSpecValueList) {
                    Integer goodsSkuId = goodsSkuVo.getId();
                    if(goodsSkuId != null && goodsSkuId > 0){
                        GoodsSku goodsSku = new GoodsSku();
                        BeanUtils.copyProperties(goodsSkuVo, goodsSku);
                        goodsSkuMapper.updateById(goodsSku);
                    }else{
                        String skuValueArr = goodsSkuVo.getSkuValueArr();
                        String[] skuValueArrList = skuValueArr.split(",");
                        StringBuilder skuIds = new StringBuilder();
                        for (String skuName : skuValueArrList) {
                            Integer skuNameId = specListData.get(skuName);
                            if (!skuIds.toString().equals("")) {
                                skuIds.append(",").append(skuNameId.toString());
                            } else {
                                skuIds = new StringBuilder(skuNameId.toString());
                            }
                        }
                        goodsSkuVo.setSkuValueIds(skuIds.toString());
                        goodsSkuVo.setGoodsId(goodsUpdateValidate.getId());
                        GoodsSku goodsSku = new GoodsSku();
                        BeanUtils.copyProperties(goodsSkuVo, goodsSku);
                        goodsSkuId = goodsSkuMapper.insert(goodsSku);
                    }
                    goodsSkuIds.remove(goodsSkuId);
                    priceList.add(goodsSkuVo.getPrice());
                    lineationPrice.add(goodsSkuVo.getLinePrice());
                    totalStock += goodsSkuVo.getStock();
                }

                if(specValueNameIds.size() > 0){
                    goodsSkuNameMapper.deleteBatchIds(specValueNameIds);
                }

                if(goodsSkuValueIds.size() > 0){
                    goodsSkuValueMapper.deleteBatchIds(goodsSkuValueIds);
                }

                if(goodsSkuIds.size() > 0){
                    goodsSkuMapper.deleteBatchIds(goodsSkuIds);
                }
            }
        }

        goodsSkuData.put("maxPrice", Collections.max(priceList));
        goodsSkuData.put("minPrice", Collections.min(priceList));
        goodsSkuData.put("minLineationPrice", Collections.min(lineationPrice));
        goodsSkuData.put("maxLineationPrice", Collections.max(lineationPrice));
        goodsSkuData.put("totalStock", totalStock);
        return goodsSkuData;
    }

    /**
     * 检查商品数据
     *
     * @author cjhao
     * @param goodsCreateValidate 参数
     */
    private void __checkAddGoodsData(GoodsCreateValidate goodsCreateValidate) {
        // 验证商品名称
        Goods goodsName = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("is_delete", 0)
                        .eq("name", goodsCreateValidate.getName()).last("limit 1"));

        Assert.isNull(goodsName, "商品名称已存在");

        // 验证商品编码
        Goods goodsCode = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("is_delete", 0)
                        .eq("code", goodsCreateValidate.getCode()).last("limit 1"));

        Assert.isNull(goodsCode, "商品编码已存在");

        // 验证商品分类
        List<GoodsCategory> goodsCategoryList = goodsCategoryMapper.selectBatchIds(goodsCreateValidate.getCategoryId());
        if (goodsCategoryList.size() != goodsCreateValidate.getCategoryId().size()) {
            throw new OperateException("分类信息错误，请刷新分类");
        }

        // 验证商品SKU
        List<GoodsSkuVo> serviceSpecValueList = goodsCreateValidate.getServiceSpecValueList();
        for (GoodsSkuVo goodsSkuVo : serviceSpecValueList) {
            if (StringUtils.isNull(goodsSkuVo.getPrice()) || goodsSkuVo.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new OperateException("销售价格不能少于0");
            }
            if (StringUtils.isNull(goodsSkuVo.getStock()) || goodsSkuVo.getStock() < 0) {
                throw new OperateException("库存数不能少于0");
            }
        }
    }

}
