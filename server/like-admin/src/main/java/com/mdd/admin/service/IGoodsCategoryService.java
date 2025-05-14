package com.mdd.admin.service;

import com.alibaba.fastjson2.JSONArray;
import com.mdd.admin.validate.goods.GoodsCategoryCreateValidate;
import com.mdd.admin.validate.goods.GoodsCategoryUpdateValidate;
import com.mdd.admin.vo.goods.GoodsCategoryVo;

public interface IGoodsCategoryService {

    /**
     * 分类列表
     *
     * @author cjh
     * @return JSONArray
     */
    JSONArray list();

    /**
     * 分类详情
     *
     * @author cjh
     * @param id 主键
     * @return GoodsCategoryVo
     */
    GoodsCategoryVo detail(Integer id);

    /**
     * 分类新增
     *
     * @author cjh
     * @param goodsCategoryCreateValidate 参数
     */
    void add(GoodsCategoryCreateValidate goodsCategoryCreateValidate);

    /**
     * 分类编辑
     *
     * @author cjh
     * @param goodsCategoryUpdateValidate 参数
     */
    void edit(GoodsCategoryUpdateValidate goodsCategoryUpdateValidate);

    /**
     * 分类删除
     *
     * @author cjh
     * @param id 参数
     */
    void del(Integer id);

    /**
     * 分类状态
     *
     * @author cjh
     * @param id 主键
     */
    void change(Integer id);

}
