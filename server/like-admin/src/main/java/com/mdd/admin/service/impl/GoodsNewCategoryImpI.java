package com.mdd.admin.service.impl;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IGoodsNewCategoryService;
import com.mdd.admin.validate.goods.GoodsCategoryCreateValidate;
import com.mdd.admin.validate.goods.GoodsCategoryUpdateValidate;
import com.mdd.admin.vo.goods.GoodsCategoryVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsCategory;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.goods.GoodsCategoryIndexMapper;
import com.mdd.common.mapper.goods.GoodsCategoryMapper;
import com.mdd.common.util.ListUtils;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;

@Service
public class GoodsNewCategoryImpI implements IGoodsNewCategoryService {

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    GoodsCategoryIndexMapper goodsCategoryIndexMapper;

    /**
     * 分类列表
     *
     * @author cjh
     * @return JSONArray
     */
    @Override
    public JSONArray list() {
        List<GoodsCategory> lists = goodsCategoryMapper.selectList(
                new QueryWrapper<GoodsCategory>()
                    .eq("is_delete", 0)
                    .orderByDesc("sort")
                    .orderByAsc("id"));

        List<GoodsCategoryVo> list = new ArrayList<>();
        for (GoodsCategory item : lists) {
            Integer goodsNum = goodsCategoryIndexMapper.selectJoinCount(new MPJQueryWrapper<Goods>()
                    .eq("t.category_id", item.getId())
                    .leftJoin("?_goods G ON G.id=t.goods_id".replace("?_", GlobalConfig.tablePrefix))
                    .eq("G.is_delete", 0));

            GoodsCategoryVo goodsCategoryVo = new GoodsCategoryVo();
            goodsCategoryVo.setId(item.getId());
            goodsCategoryVo.setPid(item.getPid());
            goodsCategoryVo.setName(item.getName());
            goodsCategoryVo.setImage(UrlUtils.toAbsoluteUrl(item.getImage()));
            goodsCategoryVo.setIsShow(item.getIsShow());
            goodsCategoryVo.setSort(item.getSort());
            goodsCategoryVo.setGoodsNum(goodsNum);
            goodsCategoryVo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            list.add(goodsCategoryVo);
        }

        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(list));
        System.out.println("分类列表结果: " + jsonArray);
        return ListUtils.listToTree(jsonArray, "id", "pid", "children");
    }

    /**
     * 分类详情
     *
     * @author cjh
     * @param id 主键
     * @return GoodsCategoryVo
     */
    @Override
    public GoodsCategoryVo detail(Integer id) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                    .eq("id", id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(goodsCategory, "数据不存在!");

        GoodsCategoryVo vo = new GoodsCategoryVo();
        vo.setId(goodsCategory.getId());
        vo.setImage(UrlUtils.toAbsoluteUrl(goodsCategory.getImage()));
        vo.setName(goodsCategory.getName());
        vo.setPid(goodsCategory.getPid());
        vo.setSort(goodsCategory.getSort());
        vo.setIsShow(goodsCategory.getIsShow());
        vo.setCreateTime(TimeUtils.timestampToDate(goodsCategory.getCreateTime()));
        return vo;
    }

    /**
     * 分类新增
     *
     * @author cjh
     * @param goodsCategoryCreateValidate 参数
     */
    @Override
    public void add(GoodsCategoryCreateValidate goodsCategoryCreateValidate) {
        GoodsCategory model = goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                        .eq("is_delete",0)
                        .eq("name", goodsCategoryCreateValidate.getName())
                        .last("limit 1"));

        Assert.isNull(model, "分类名称已存在");

        if(goodsCategoryCreateValidate.getPid() > 0){
            GoodsCategory Pmodel = goodsCategoryMapper.selectOne(
                    new QueryWrapper<GoodsCategory>()
                            .eq("is_delete",0)
                            .eq("id", goodsCategoryCreateValidate.getPid())
                            .last("limit 1"));
            Assert.notNull(Pmodel, "父级分类不存在");
        }

        GoodsCategory goodsCategory = new GoodsCategory();
        goodsCategory.setName(goodsCategoryCreateValidate.getName());
        goodsCategory.setImage(UrlUtils.toRelativeUrl(goodsCategoryCreateValidate.getImage()));
        goodsCategory.setPid(goodsCategoryCreateValidate.getPid());
        goodsCategory.setIsShow(goodsCategoryCreateValidate.getIsShow());
        goodsCategory.setCreateTime(TimeUtils.timestamp());
        goodsCategory.setUpdateTime(TimeUtils.timestamp());
        goodsCategoryMapper.insert(goodsCategory);
    }

    /**
     * 分类编辑
     *
     * @author cjh
     * @param goodsCategoryUpdateValidate 参数
     */
    @Override
    public void edit(GoodsCategoryUpdateValidate goodsCategoryUpdateValidate) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                        .eq("id", goodsCategoryUpdateValidate.getId())
                        .eq("is_delete",0)
                        .last("limit 1"));

        Assert.notNull(goodsCategory, "数据不存在!");

        GoodsCategory model = goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                        .ne("id",goodsCategoryUpdateValidate.getId())
                        .eq("is_delete",0)
                        .eq("name", goodsCategoryUpdateValidate.getName())
                        .last("limit 1"));

        Assert.isNull(model, "分类名称已存在");

        if(goodsCategoryUpdateValidate.getPid() > 0){
            GoodsCategory Pmodel = goodsCategoryMapper.selectOne(
                    new QueryWrapper<GoodsCategory>()
                            .eq("is_delete",0)
                            .eq("id", goodsCategoryUpdateValidate.getPid())
                            .last("limit 1"));
            Assert.notNull(Pmodel, "父级分类不存在");
        }

        if (goodsCategoryUpdateValidate.getPid().equals(goodsCategory.getId())) {
            throw new OperateException("父级分类不能自己");
        }

        goodsCategory.setName(goodsCategoryUpdateValidate.getName());
        goodsCategory.setImage(UrlUtils.toRelativeUrl(goodsCategoryUpdateValidate.getImage()));
        goodsCategory.setPid(goodsCategoryUpdateValidate.getPid());
        goodsCategory.setSort(goodsCategoryUpdateValidate.getSort());
        goodsCategory.setIsShow(goodsCategoryUpdateValidate.getIsShow());
        goodsCategory.setUpdateTime(TimeUtils.timestamp());
        goodsCategoryMapper.updateById(goodsCategory);
    }

    /**
     * 分类删除
     *
     * author cjh
     * @param id 主键
     */
    @Override
    public void del(Integer id) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                        .eq("id", id)
                        .eq("is_delete",0)
                        .last("limit 1"));

        Assert.notNull(goodsCategory, "数据不存在!");

        Integer goodsNum = goodsCategoryIndexMapper.selectJoinCount(new MPJQueryWrapper<Goods>()
                .eq("t.category_id", id)
                .leftJoin("?_goods G ON G.id=t.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("G.is_delete", 0));

        if (goodsNum > 0) {
            throw new OperateException("分类已被使用,不允许删除");
        }

        GoodsCategory childCategory = goodsCategoryMapper.selectOne(new QueryWrapper<GoodsCategory>()
                .eq("pid", id)
                .eq("is_delete",0)
                .last("limit 1"));

        if (StringUtils.isNotNull(childCategory)) {
            throw new OperateException("存在子级分类,不允许直接删除");
        }

        goodsCategory.setIsDelete(1);
        goodsCategoryMapper.updateById(goodsCategory);
    }

    /**
     * 分类状态
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void change(Integer id) {
        GoodsCategory goodsCategory =  goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                        .eq("id",id)
                        .eq("is_delete",0)
                        .last("limit 1"));

        Assert.notNull(goodsCategory, "数据不存在!");

        goodsCategory.setIsShow(goodsCategory.getIsShow()==0?1:0);
        goodsCategory.setUpdateTime(TimeUtils.timestamp());
        goodsCategoryMapper.updateById(goodsCategory);
    }

}
