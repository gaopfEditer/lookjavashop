package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.distribution.DistributionGoods;
import com.mdd.common.entity.distribution.DistributionLevel;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.mapper.distribution.DistributionGoodsMapper;
import com.mdd.common.mapper.distribution.DistributionLevelMapper;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.front.service.*;
import com.mdd.front.validate.goods.GoodsListSearchValidate;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.SearchRecord;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.delivery.ExpressTpl;
import com.mdd.common.entity.goods.*;
import com.mdd.common.entity.setting.HotSearch;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.SearchRecordMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.delivery.ExpressTplMapper;
import com.mdd.common.mapper.goods.*;
import com.mdd.common.mapper.setting.HotSearchMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.*;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.distribution.DistributionConfigVo;
import com.mdd.front.vo.goods.*;
import com.tencentcloudapi.essbasic.v20201222.models.FileUrl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

/**
 * 商品实现类
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    GoodsMapper goodsMapper;

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    GoodsCategoryIndexMapper goodsCategoryIndexMapper;

    @Resource
    GoodsImageMapper goodsImageMapper;

    @Resource
    GoodsSkuNameMapper goodsSkuNameMapper;

    @Resource
    GoodsSkuValueMapper goodsSkuValueMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;

    @Resource
    SearchRecordMapper searchRecordMapper;

    @Resource
    HotSearchMapper hotSearchMapper;

    @Resource
    GoodsCollectMapper goodsCollectMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    CouponMapper couponMapper;

    @Resource
    ExpressTplMapper expressTplMapper;

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    DistributionGoodsMapper distributionGoodsMapper;

    @Resource
    DistributionLevelMapper distributionLevelMapper;

    @Resource
    IUserAddressService iUserAddressService;

    @Resource
    ICouponService iCouponService;

    @Resource
    IGoodsCommentService iGoodsCommentService;

    @Resource
    IDistributionService iDistributionService;

    /**
     * 搜索记录
     *
     * @author cjhao
     * @return Map<String,Object>
     */
    @Override
    public Map<String, Object> searchRecord() {
        Map<String, Object> recordMap = new LinkedHashMap<>();
        List<HotSearch> hotSearchList = hotSearchMapper.selectList(
                new QueryWrapper<HotSearch>()
                        .orderByDesc("sort")
        );
        List<String> hostList = new LinkedList<>();
        List<String> recordList = new LinkedList<>();
        for (HotSearch hotSearch : hotSearchList) {
            hostList.add(hotSearch.getName());
        }
        recordMap.put("hostList", hostList);
        recordMap.put("recordList", recordList);
        Integer userId = LikeFrontThreadLocal.getUserId();
        if (userId > 0) {
            List<SearchRecord> searchRecordList = searchRecordMapper.selectList(
                    new QueryWrapper<SearchRecord>()
                            .eq("user_id", userId)
                            .eq("is_delete", 0)
            );
            for (SearchRecord searchRecord : searchRecordList) {
                recordList.add(searchRecord.getKeyword());
            }
            recordMap.put("recordList", recordList);
        }
        return recordMap;
    }

    /**
     * 收藏列表
     *
     * @author cjhao
     * @param pageValidate PageValidate
     * @return PageResult<GoodsListsVo>
     */
    @Override
    public PageResult<GoodsListsVo> collectList(PageValidate pageValidate) {
        Integer page = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();
        MPJQueryWrapper<Goods> MPJQueryWrapper = new MPJQueryWrapper<>();
        Integer userId = LikeFrontThreadLocal.getUserId();
        MPJQueryWrapper
                .leftJoin("?_goods_collect GC ON GC.goods_id=t.id".replace("?_", GlobalConfig.tablePrefix))
                .select("t.id,t.name,t.image,t.status,t.sales_num+virtual_sales_num as sales_num,t.min_price,t.min_lineation_price")
                .eq("GC.user_id", userId)
                .eq("GC.status", 1)
                .eq("t.is_delete", 0)
                .eq("GC.is_delete",0)
                .orderByDesc("id");

        IPage<Goods> iPage = goodsMapper.selectPage(new Page<>(page, limit), MPJQueryWrapper);
        List<GoodsListsVo> list = new ArrayList<>();
        for (Goods record : iPage.getRecords()) {
            GoodsListsVo goodsListsVo = new GoodsListsVo();
            BeanUtils.copyProperties(record, goodsListsVo);
            goodsListsVo.setImage(UrlUtils.toAbsoluteUrl(record.getImage()));
            goodsListsVo.setPrice(record.getMinPrice());
            goodsListsVo.setLineationPrice(record.getMinLineationPrice());
            list.add(goodsListsVo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 获取限定数量商品
     *
     * @author mjf
     * @param type String
     * @param limit Integer
     * @param categoryId String
     * @return List<GoodsListsVo>
     */
    @Override
    public List<GoodsListsVo> limitList(String type, Integer limit, String categoryId) {
        MPJQueryWrapper<Goods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.innerJoin("?_goods_category_index gc ON gc.goods_id = t.id" .replace("?_", GlobalConfig.tablePrefix));
        mpjQueryWrapper.eq("t.status", 1);
        mpjQueryWrapper.eq("t.is_delete", 0);
        mpjQueryWrapper.select("t.id,name,image,status,sales_num+virtual_sales_num as sales_num," +
                "click_num+virtual_click_num as click_num,min_price,min_lineation_price");
        // 猜你喜欢,同分类 销量->浏览量->创建时间
        if (type.equals("like")) {
            if (!categoryId.isEmpty()) {
                mpjQueryWrapper.orderByDesc("field(gc.category_id," + categoryId + ")");
                mpjQueryWrapper.groupBy("field(gc.category_id," + categoryId + ")");
            }
            mpjQueryWrapper.orderByDesc(Arrays.asList("sales_num", "click_num"));
        }
        // 热门排序, 销量->浏览量->创建时间
        if (type.equals("hot")) {
            mpjQueryWrapper.orderByDesc(Arrays.asList("sales_num", "click_num"));
        }
        mpjQueryWrapper.orderByDesc("t.id");
        mpjQueryWrapper.groupBy("t.id");
        mpjQueryWrapper.last("limit " + limit);
        List<Goods> goodsList = goodsMapper.selectJoinList(Goods.class, mpjQueryWrapper);
        return __copyGoodsList(goodsList);
    }

    /**
     * 获取限定数量商品
     *
     * @author mjf
     * @param type String
     * @param limit Integer
     * @return List<GoodsListsVo>
     */
    @Override
    public List<GoodsListsVo> limitList(String type, Integer limit) {
        MPJQueryWrapper<Goods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.innerJoin("?_goods_category_index gc ON gc.goods_id = t.id" .replace("?_", GlobalConfig.tablePrefix));
        mpjQueryWrapper.eq("status", 1);
        mpjQueryWrapper.eq("is_delete", 0);
        mpjQueryWrapper.select("t.id,name,image,status,sales_num+virtual_sales_num as sales_num," +
                "click_num+virtual_click_num as click_num,min_price,min_lineation_price");
        // 猜你喜欢,同分类 销量->浏览量->创建时间
        // 热门排序, 销量->浏览量->创建时间
        if (type.equals("hot") || type.equals("like")) {
            mpjQueryWrapper.orderByDesc(Arrays.asList("sales_num", "click_num"));
        }
        mpjQueryWrapper.orderByDesc("t.id");
        mpjQueryWrapper.groupBy("t.id");
        mpjQueryWrapper.last("limit " + limit);
        List<Goods> goodsList = goodsMapper.selectJoinList(Goods.class, mpjQueryWrapper);
        return __copyGoodsList(goodsList);
    }

    /**
     * 商品列表
     *
     * @author cjhao
     * @param pageValidate PageValidate
     * @param searchValidate GoodsListSearchValidate
     * @return PageResult<GoodsListsVo>
     */
    @Override
    public PageResult<GoodsListsVo> list(Integer userId, PageValidate pageValidate, GoodsListSearchValidate searchValidate) {
        Integer page = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();
        String keyword = searchValidate.getKeyword();
        String type = searchValidate.getType();
        String sort = searchValidate.getSort();
        Integer couponId = searchValidate.getCouponId();
        Integer categoryId = searchValidate.getCategoryId();

        // 列表扩展参数
        Map<String, Object> extend = new LinkedHashMap<>();

        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1);
        queryWrapper.eq("is_delete", 0);
        queryWrapper.select("id,name,image,sales_num+virtual_sales_num as sales_num,click_num+virtual_click_num as click_num,min_price,min_lineation_price");

        // 商品分类
        if(categoryId != null && categoryId > 0) {
            List<GoodsCategoryIndex> goodsCategoryIndexList = goodsCategoryIndexMapper.selectList(new QueryWrapper<GoodsCategoryIndex>()
                    .eq("category_id", categoryId));
            List<Integer> cateGoodsIds = new LinkedList<>();
            if(goodsCategoryIndexList.size() > 0) {
                for (GoodsCategoryIndex goodsCategoryIndex : goodsCategoryIndexList) {
                    cateGoodsIds.add(goodsCategoryIndex.getGoodsId());
                }
                queryWrapper.in("id", cateGoodsIds);
            } else {
                queryWrapper.apply("1=0");
            }
        }

        // 商品名称搜索
        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("name", keyword);
            // 记录搜索记录
            if (userId != null && userId > 0) {
                recordKeyword(userId, keyword);
            }
        }
        // 热门商品排序
        if (StringUtils.isNotBlank(type) && type.equals("hot")) {
            queryWrapper.orderByDesc(Arrays.asList("sales_num", "click_num"));
        }
        // 商品价格排序
        if (StringUtils.isNotBlank(sort)) {
            switch (sort) {
                case "priceDesc":
                    queryWrapper.orderByDesc("min_price");
                    break;
                case "priceAsc":
                    queryWrapper.orderByAsc("min_price");
                    break;
                case "salesDesc":
                    queryWrapper.orderByDesc("sales_num");
                    break;
                case "salesAsc":
                    queryWrapper.orderByAsc("sales_num");
                    break;
            }
        }
        // 优惠券商品
        if (couponId != null && couponId > 0) {
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon != null && StringUtils.isNotBlank(coupon.getUseGoodsIds())) {
                if (coupon.getUseGoodsType().equals(CouponEnum.USE_GOODS_TYPE_ALLOW.getCode())) {
                    List<Integer> allowGoodsIds = ListUtils.stringToListAsInt(coupon.getUseGoodsIds(), ",");
                    queryWrapper.in("id", allowGoodsIds);
                }
                if (coupon.getUseGoodsType().equals(CouponEnum.USE_GOODS_TYPE_BAN.getCode())) {
                    List<Integer> banGoodsIds = ListUtils.stringToListAsInt(coupon.getUseGoodsIds(), ",");
                    queryWrapper.notIn("id", banGoodsIds);
                }
                extend.put("couponId", couponId);
                extend.put("couponCondition", iCouponService.getConditionMsg(coupon.getConditionType(),coupon.getConditionMoney()));
            }
        }

        queryWrapper.orderByDesc("id");

        IPage<Goods> iPage = goodsMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsListsVo> list = new ArrayList<>();
        for (Goods record : iPage.getRecords()) {
            GoodsListsVo goodsListsVo = new GoodsListsVo();
            BeanUtils.copyProperties(record, goodsListsVo);
            goodsListsVo.setImage(UrlUtils.toAbsoluteUrl(record.getImage()));
            goodsListsVo.setPrice(record.getMinPrice());
            goodsListsVo.setStatus(record.getStatus());
            goodsListsVo.setLineationPrice(record.getMinLineationPrice());
            list.add(goodsListsVo);
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list, extend);
    }

    /**
     * 获取商品详情
     *
     * @author cjhao
     * @param id Integer
     * @return GoodsDetailVo
     */
    @Override
    public GoodsDetailVo detail(Integer userId, Integer id) {
        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(goods, "商品不存在!");

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        BeanUtils.copyProperties(goods, goodsDetailVo);

        // 销量,浏览量
        goodsDetailVo.setClickNum(goods.getVirtualClickNum() + goods.getClickNum());
        goodsDetailVo.setSalesNum(goods.getVirtualSalesNum() + goods.getSalesNum());

        // 商品图片
        List<GoodsImage> goodsImages = goodsImageMapper.selectList(
                new QueryWrapper<GoodsImage>()
                        .eq("goods_id", id));

        List<String> goodsImage = new LinkedList<>();
        for (GoodsImage image : goodsImages) {
            goodsImage.add(UrlUtils.toAbsoluteUrl(image.getImage()));
        }
        goodsDetailVo.setGoodsImage(goodsImage);

        List<GoodsCategoryIndex> goodsCategoryIndexList = goodsCategoryIndexMapper.selectList(
                new QueryWrapper<GoodsCategoryIndex>()
                        .eq("goods_id", id)
        );
        List<Integer> goodsCategory = new LinkedList<>();
        for (GoodsCategoryIndex goodsCategoryIndex : goodsCategoryIndexList) {
            goodsCategory.add(goodsCategoryIndex.getCategoryId());
        }
        goodsDetailVo.setCategoryId(goodsCategory);

        // 规格项
        goodsDetailVo.setSpecValueList(goodsSkuMapper.selectList(new QueryWrapper<GoodsSku>().eq("goods_id", id)));

        List<GoodsSkuName> goodsSkuNameList = goodsSkuNameMapper.selectList(new QueryWrapper<GoodsSkuName>().eq("goods_id", id));
        List<GoodsSkuValue> goodsSkuValueList = goodsSkuValueMapper.selectList(new QueryWrapper<GoodsSkuValue>().eq("goods_id", id));
        List<SpecValueVo> specValue = new ArrayList<>();
        for (GoodsSkuName goodsSkuName : goodsSkuNameList) {
            SpecValueVo specValueVo = new SpecValueVo();
            BeanUtils.copyProperties(goodsSkuName, specValueVo);
            List<SpecListVo> specListVos = new ArrayList<>();
            for (GoodsSkuValue goodsSkuValue : goodsSkuValueList) {
                if (goodsSkuValue.getSkuNameId().equals(goodsSkuName.getId())) {
                    SpecListVo specListVo = new SpecListVo();
                    BeanUtils.copyProperties(goodsSkuValue, specListVo);
                    specListVos.add(specListVo);
                }
            }
            specValueVo.setSpecList(specListVos);
            specValue.add(specValueVo);
        }

        // 规格项
        goodsDetailVo.setSpecValue(specValue);

        // 商品价格
        goodsDetailVo.setSellPrice(goods.getMinPrice());
        goodsDetailVo.setLinePrice(goods.getMinLineationPrice());

        // 商品评论
        goodsDetailVo.setGoodsComment(iGoodsCommentService.getGoodsDetailComment(id));

        // 是否收藏
        goodsDetailVo.setIsCollect(0);
        if (userId != null && userId > 0) {
            GoodsCollect goodsCollect = goodsCollectMapper.selectOne(new QueryWrapper<GoodsCollect>()
                    .eq("user_id", userId)
                    .eq("goods_id", id)
                    .eq("is_delete", 0)
                    .eq("status", 1)
                    .last("limit 1"));
            if (goodsCollect != null) {
                goodsDetailVo.setIsCollect(1);
            }
        }

        // 增加浏览量
        goods.setClickNum(goods.getClickNum() + 1);
        goodsMapper.updateById(goods);

        // 默认物流信息 默认取第一个规格数据
        GoodsSku firstGoodsSku = goodsDetailVo.getSpecValueList().get(0);
        BigDecimal expressMoney = this.__calculateFreight(goods, 1, firstGoodsSku.getWeight(),firstGoodsSku.getVolume());
        GoodsAddressVo goodsAddress = iUserAddressService.getGoodsDefaultAddress(userId);
        goodsDetailVo.setDefaultFreight(expressMoney);
        goodsDetailVo.setDefaultAddress(goodsAddress);

        // 转换图片
        String engine = ConfigUtils.get("storage", "default", "local");
        Map<String, String> mapStorage = ConfigUtils.getMap("storage", engine);
        String domain = mapStorage.getOrDefault("domain", RequestUtils.domain());
        goodsDetailVo.setContent(goods.getContent().replace("##domain##", domain));

        // 商品佣金
        goodsDetailVo.setEarningsData(this.__goodsDistributionEarnings(goods.getId(), userId));

        return goodsDetailVo;
    }

    /**
     * 商品收藏
     *
     * @author cjhao
     * @param id Integer
     */
    @Override
    public void collect(Integer id) {
        Integer userId = LikeFrontThreadLocal.getUserId();
        GoodsCollect goodsCollect = goodsCollectMapper.selectOne(
                new QueryWrapper<GoodsCollect>()
                        .eq("is_delete", 0)
                        .eq("user_id", userId)
                        .eq("goods_id", id)
                        .last("limit 1")
        );
        if (null == goodsCollect) {
            goodsCollect = new GoodsCollect();
            goodsCollect.setGoodsId(id);
            goodsCollect.setUserId(userId);
            goodsCollect.setStatus(1);
            goodsCollect.setCreateTime(TimeUtils.timestamp());
            goodsCollect.setUpdateTime(TimeUtils.timestamp());
            goodsCollectMapper.insert(goodsCollect);
        } else {
            goodsCollect.setStatus(0 == goodsCollect.getStatus() ? 1 : 0);
            goodsCollectMapper.updateById(goodsCollect);
        }
    }

    /**
     * 商品分类
     *
     * @author mjf
     * @return JSONArray
     */
    @Override
    public JSONArray category() {
        List<GoodsCategory> lists = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>()
                        .eq("is_delete", 0)
                        .eq("is_show", 1)
                        .orderByDesc(Arrays.asList("sort", "id")));

        List<GoodsCategoryVo> list = new ArrayList<>();
        for (GoodsCategory item : lists) {
            GoodsCategoryVo goodsCategoryVo = new GoodsCategoryVo();
            goodsCategoryVo.setId(item.getId());
            goodsCategoryVo.setPid(item.getPid());
            goodsCategoryVo.setName(item.getName());
            goodsCategoryVo.setImage(UrlUtils.toAbsoluteUrl(item.getImage()));
            goodsCategoryVo.setIsShow(item.getIsShow());
            goodsCategoryVo.setSort(item.getSort());
            goodsCategoryVo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            list.add(goodsCategoryVo);
        }

        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(list));
        return ListUtils.listToTree(jsonArray, "id", "pid", "children");
    }

    /**
     * 商品分享-图片资源(base64)
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     * @return GoodsImageResourceVo
     */
    @Override
    public GoodsImageResourceVo imageResource(Integer userId, Integer id) {
        try {
            User user = userMapper.selectById(userId);
            Goods goods = goodsMapper.selectById(id);

            Assert.notNull(goods, "商品信息不存在");

            GoodsImageResourceVo vo = new GoodsImageResourceVo();

            vo.setGoodsImage(this.__imageToBase64(goods.getImage()));
            vo.setUserAvatar(this.__imageToBase64(user.getAvatar()));
            return vo;

        } catch (IOException e) {
            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 记录搜索关键词
     *
     * @author cjhao
     * @param userId Integer
     * @param keyword String
     */
    @Override
    public void recordKeyword(Integer userId, String keyword) {
        SearchRecord userSearchRecord = searchRecordMapper.selectOne(
                new QueryWrapper<SearchRecord>()
                        .eq("is_delete", 0)
                        .eq("user_id", userId)
                        .eq("keyword", keyword)
                        .last("limit 1")
        );

        if (null != userSearchRecord) {
            userSearchRecord.setCount(userSearchRecord.getCount() + 1);
            userSearchRecord.setUpdateTime(TimeUtils.timestamp());
            searchRecordMapper.updateById(userSearchRecord);
        } else {
            SearchRecord searchRecord = new SearchRecord();
            searchRecord.setUserId(userId);
            searchRecord.setKeyword(keyword);
            searchRecord.setCreateTime(TimeUtils.timestamp());
            searchRecordMapper.insert(searchRecord);
        }
    }

    /**
     * 商品列表项处理
     *
     * @author mjf
     * @param goodsList List<Goods>
     * @return List<GoodsListsVo>
     */
    private List<GoodsListsVo> __copyGoodsList(List<Goods> goodsList) {
        List<GoodsListsVo> list = new ArrayList<>();
        for (Goods goods : goodsList) {
            GoodsListsVo goodsListsVo = new GoodsListsVo();
            BeanUtils.copyProperties(goods, goodsListsVo);
            goodsListsVo.setImage(UrlUtils.toAbsoluteUrl(goods.getImage()));
            goodsListsVo.setPrice(goods.getMinPrice());
            goodsListsVo.setLineationPrice(goods.getMinLineationPrice());
            list.add(goodsListsVo);
        }
        return list;
    }

    /**
     * 图片文件转base64
     *
     * @author mjf
     * @param fileUrl String
     * @return String
     */
    private String __imageToBase64(String fileUrl) throws IOException {
        String fileBase64 = "";
        fileUrl = UrlUtils.toRelativeUrl(fileUrl);
        String engine = ConfigUtils.get("storage", "default", "local");
        engine = engine.equals("") ? "local" : engine;

        if (engine.equals("local")) {
            String directory = YmlUtils.get("like.upload-directory");
            String fullFilePath = directory + "/" + fileUrl;
            // 图片转base64
            File file = new File(fullFilePath);
            if (file.exists()) {
                // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
                InputStream in = new FileInputStream(file);
                // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
                byte[] fileByte = new byte[in.available()];
                in.read(fileByte);
                in.close();
                fileBase64 = "data:image/jpeg;base64," + Base64Util.encode(fileByte);
            }
        } else {
            URL url = new URL(UrlUtils.toAbsoluteUrl(fileUrl));
            InputStream is = url.openStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int length;
            byte[] buffer = new byte[1024];
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            is.close();
            baos.close();

            fileBase64 = "data:image/jpeg;base64," + base64Image;
        }
        return fileBase64;
    }

    /**
     * 计算商品默认运费
     *
     * @author mjf
     * @param goods Goods
     * @param num Integer
     * @param weight Double
     * @param volume Double
     * @return BigDecimal
     */
    private BigDecimal __calculateFreight(Goods goods, Integer num, Double weight, Double volume) {
        // 运费
        BigDecimal expressMoney = BigDecimal.ZERO;

        // 1=包邮 2=运费模板
        if (1 == goods.getExpressType()) {
            return expressMoney;
        }

        // 运费模板
        ExpressTpl expressTpl = expressTplMapper.selectOne(new QueryWrapper<ExpressTpl>()
                .eq("id", goods.getExpressTemplateId())
                .eq("is_delete", 0)
                .last("limit 1"));
        if (expressTpl == null) {
            return expressMoney;
        }

        // 商品比较单位
        Double unit = 0.0;
        switch (expressTpl.getType()) {
            //按件计费
            case 0:
                unit = Double.valueOf(num);
                break;
            //体积计费
            case 1:
                unit = volume;
                break;
            //重量计费
            case 2:
                unit = weight;
                break;
        }

        // 首(重/件/立方)
        Integer firstNum = expressTpl.getFirstNum();
        if (unit <= firstNum) {
            // 小于首重，仅付首重金额
            expressMoney = expressTpl.getFirstPrice();
        } else {
            // 减去首重后的重量 / 续重
            int left = (int) Math.ceil((unit - firstNum) / (double) expressTpl.getContinueNum());
            // 续重需付金额
            BigDecimal continuePrice = expressTpl.getContinuePrice().multiply(BigDecimal.valueOf(left));
            // 首重金额 + 续重金额
            expressMoney = expressTpl.getFirstPrice().add(continuePrice);
        }
        return expressMoney;
    }

    /**
     * 商品分销佣金
     *
     * @author mjf
     * @param goodsId Integer
     * @param userId Integer
     * @return earningsData
     */
    private GoodsDetailVo.earningsData __goodsDistributionEarnings (Integer goodsId, Integer userId) {
        // 可获得的最高佣金
        BigDecimal topEarnings = BigDecimal.ZERO;
        // 可获得最高佣金百分比
        Double topPercent = 0.0;

        // 分销商品记录
        List<DistributionGoods> distributionList = distributionGoodsMapper.selectList(new QueryWrapper<DistributionGoods>()
                .eq("is_delete", 0)
                .eq("goods_id", goodsId));

        // 商品信息
        Goods goods = goodsMapper.selectById(goodsId);

        if (distributionList.size() > 0 && distributionList.get(0).getIsDistribution().equals(1)) {
            // 按分销等级比例分佣
            if (distributionList.get(0).getRule().equals(DistributionEnum.GOODS_RULE_SINGLE.getCode())) {
                for (DistributionGoods distributionGoods : distributionList) {
                    BigDecimal levelFirstEarnings = goods.getMaxPrice()
                            .multiply(BigDecimal.valueOf(distributionGoods.getFirstRatio()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                    if (levelFirstEarnings.compareTo(topEarnings) > 0) {
                        topEarnings = levelFirstEarnings;
                        topPercent = distributionGoods.getFirstRatio();
                    }

                    BigDecimal levelSecondEarnings = goods.getMaxPrice()
                            .multiply(BigDecimal.valueOf(distributionGoods.getSecondRatio()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                    if (levelSecondEarnings.compareTo(topEarnings) > 0) {
                        topEarnings = levelSecondEarnings;
                        topPercent = distributionGoods.getSecondRatio();
                    }
                }
            }

            // 单独设置分佣比例
            if (distributionList.get(0).getRule().equals(DistributionEnum.GOODS_RULE_LEVEL.getCode())) {
                List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>().eq("is_delete", 0));
                for (DistributionLevel level : levelList) {
                    BigDecimal singleFirstEarnings = goods.getMaxPrice()
                            .multiply(BigDecimal.valueOf(level.getFirstRatio()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                    if (singleFirstEarnings.compareTo(topEarnings) > 0) {
                        topEarnings = singleFirstEarnings;
                        topPercent = level.getFirstRatio();
                    }

                    BigDecimal singleSecondEarnings = goods.getMaxPrice()
                            .multiply(BigDecimal.valueOf(level.getSecondRatio()))
                            .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

                    if (singleSecondEarnings.compareTo(topEarnings) > 0) {
                        topEarnings = singleSecondEarnings;
                        topPercent = level.getSecondRatio();
                    }
                }
            }
        }

        // 分销佣金信息初始化
        GoodsDetailVo.earningsData earningsData = new GoodsDetailVo.earningsData();
        earningsData.setIsShow(0);
        earningsData.setEarnings(topEarnings);
        earningsData.setPercent(topPercent);

        // 分销配置
        DistributionConfigVo distributionConfig = iDistributionService.config();
        if (distributionConfig.getOpen().equals(0)) {
            return earningsData;
        }

        // 商品详情显示佣金: 0=隐藏, 1=显示
        if (distributionConfig.getIsEarningsShow().equals(1)) {
            earningsData.setIsShow(1);
            // 详情页佣金可见用：1=全部用户, 2=分销商户
            if (distributionConfig.getIsEarningsScope().equals(2)) {
                Distribution distributionUser = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                        .eq("user_id", userId)
                        .last("limit 1"));
                if (ObjectUtils.isEmpty(distributionUser) || StringUtils.isNull(distributionUser)) {
                    earningsData.setIsShow(0);
                } else if (distributionUser.getIsDistribution().equals(0)) {
                    earningsData.setIsShow(0);
                }
            }
        }

        // 小于1分钱不显示
        if (topEarnings.compareTo(new BigDecimal("0.01")) < 0) {
            earningsData.setIsShow(0);
        }

        return earningsData;
    }

}
