package com.mdd.front.service;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.front.validate.goods.GoodsListSearchValidate;
import com.mdd.front.vo.goods.GoodsImageResourceVo;
import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.vo.goods.GoodsDetailVo;
import com.mdd.front.vo.goods.GoodsListsVo;

import java.util.List;
import java.util.Map;

/**
 * 商品接口
 */
public interface IGoodsService {

    /**
     * 搜索记录
     * 
     * @author cjhao
     * @return Map<String,Object>
     */
    Map<String, Object> searchRecord();

    /**
     * 尚欧品列表
     *
     * @author cjhao
     * @param userId Integer
     * @param pageValidate PageValidate
     * @param searchValidate GoodsListSearchValidate
     * @return PageResult<GoodsListsVo>
     */
    PageResult<GoodsListsVo> list(Integer userId, PageValidate pageValidate, GoodsListSearchValidate searchValidate);

    /**
     * 商品详情
     *
     * @author cjhao
     * @param userId Integer
     * @param id Integer
     * @return GoodsDetailVo
     */
    GoodsDetailVo detail(Integer userId, Integer id);

    /**
     * 收藏
     * @author cjhao
     * @param id Integer
     */
    void collect(Integer id);

    /**
     * 收藏列表
     *
     * @author cjhao
     * @param pageValidate PageValidate
     * @return PageResult<GoodsListsVo>
     */
    PageResult<GoodsListsVo> collectList(PageValidate pageValidate);

    /**
     * 指定数量商品列表
     *
     * @author mjf
     * @param type String
     * @param limit Integer
     * @param categoryId String
     * @return List<GoodsListsVo>
     */
    List<GoodsListsVo> limitList(String type, Integer limit, String categoryId);


    /**
     * 指定数量商品列表
     *
     * @author mjf
     * @param type String
     * @param limit Integer
     * @return List<GoodsListsVo>
     */
    List<GoodsListsVo> limitList(String type, Integer limit);

    /**
     * 商品分类
     *
     * @author mjf
     * @return JSONArray
     */
    JSONArray category();

    /**
     * 商品分享-图片资源(base64)
     *
     * @author mjf
     * @param id Integer
     * @return GoodsImageResourceVo
     */
    GoodsImageResourceVo imageResource(Integer userId, Integer id);


    /**
     * 记录搜索关键词
     *
     * @author mjf
     * @param userId Integer
     * @param keyword String
     */
    void recordKeyword(Integer userId, String keyword);
}
