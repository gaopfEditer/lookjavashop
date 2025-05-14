package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.goods.GoodsCreateValidate;
import com.mdd.admin.validate.goods.GoodsSearchValidate;
import com.mdd.admin.validate.goods.GoodsUpdateValidate;
import com.mdd.admin.vo.goods.GoodsCommonVo;
import com.mdd.admin.vo.goods.GoodsDetailVo;
import com.mdd.admin.vo.goods.GoodsListedVo;
import com.mdd.common.core.PageResult;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 商品管理服务接口类
 */
public interface IGoodsService {

    /**
     * 公共商品
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param goodsSearchValidate 搜索参数
     * @return PageResult<GoodsListVo>
     */
    PageResult<GoodsCommonVo> common(@Validated PageValidate pageValidate, @Validated GoodsSearchValidate goodsSearchValidate);

    /**
     * 商品列表
     *
     * @author cjh
     * @param pageValidate 分页参数
     * @param goodsSearchValidate 搜索参数
     * @return PageResult<GoodsListVo>
     */
    PageResult<GoodsListedVo> list(@Validated PageValidate pageValidate, @Validated GoodsSearchValidate goodsSearchValidate);

    /**
     * 商品详情
     *
     * @author cjh
     * @param id 主键
     * @return GoodsDetailVo
     */
    GoodsDetailVo detail(Integer id);

    /**
     * 商品新增
     *
     * @author cjh
     * @param goodsCreateValidate 参数
     */
    void add(GoodsCreateValidate goodsCreateValidate);

    /**
     * 商品新增
     *
     * @author cjh
     * @param goodsUpdateValidate 参数
     */
    boolean edit(GoodsUpdateValidate goodsUpdateValidate);

    /**
     * 商品删除
     *
     * @author cjh
     * @param id 主键
     */
    void del(Integer id);

    /**
     * 商品状态
     *
     * @author cjh
     * @param id 主键
     */
    void change(Integer id);

    /**
     * 商品排序
     *
     * @author fzr
     * @param id 主键
     * @param sort 排序
     */
    void sort(Integer id, Integer sort);

    /**
     * 批量删除
     *
     * @author fzr
     * @param ids 主键集
     */
    void batchDelete(List<Integer> ids);

    /**
     * 批量上架
     *
     * @author fzr
     * @param ids 主键集
     */
    void batchUpper(List<Integer> ids);

    /**
     * 批量下架
     *
     * @author fzr
     * @param ids 主键集
     */
    void batchLower(List<Integer> ids);

}
