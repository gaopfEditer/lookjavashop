package com.mdd.admin.service;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopCreateValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopStatusValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopUpdateValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopSearchValidate;
import com.mdd.admin.validate.setting.RegionSearchValidate;
import com.mdd.admin.vo.selffetchshop.SelffetchShopListedVo;
import com.mdd.admin.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.common.core.PageResult;

/**
 * 自提门店服务接口类
 * @author LikeAdmin
 */
public interface ISelffetchShopService {

    /**
     * 自提门店列表
     *
     * @author LikeAdmin
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<SelffetchShopListedVo>
     */
    PageResult<SelffetchShopListedVo> list(PageValidate pageValidate, SelffetchShopSearchValidate searchValidate);

    /**
     * 自提门店详情
     *
     * @author LikeAdmin
     * @param id 主键ID
     * @return SelffetchShopDetailVo
     */
    SelffetchShopDetailVo detail(Integer id);

    /**
     * 自提门店新增
     *
     * @author LikeAdmin
     * @param createValidate 参数
     */
    void add(SelffetchShopCreateValidate createValidate);

    /**
     * 自提门店编辑
     *
     * @author LikeAdmin
     * @param updateValidate 参数
     */
    void edit(SelffetchShopUpdateValidate updateValidate);

    /**
     * 自提门店删除
     *
     * @author LikeAdmin
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * @notes 腾讯地图区域搜索
     * @param $params
     * @return mixed
     * @author damonyuan
     */
    JSONObject regionSearch(RegionSearchValidate regionSearchValidate);

    /**
     * @notes 修改自提门店状态
     * @author damonyuan
     */
    void status(SelffetchShopStatusValidate selffetchShopStatusValidate);


    /**
     * 判断门店名称是否存在
     * @param title
     * @return
     */
    Boolean nameUnique(String name);
    Boolean nameUnique(String name, Integer id);

    /**
     * 根据名称返回自提门店名称
     * @param id
     * @return
     */
    String getNameById(Integer id);

    /**
     * 根据id 返回自提门店地址
     * @param id
     * @return
     */
    String getAddressStrById(Integer id);

    /**
     * 根据名称返回自提门店名称
     * @param id
     * @return
     */
    String getMobileById(Integer id);

}
