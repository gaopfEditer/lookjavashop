package com.mdd.front.service;

import com.alibaba.fastjson2.JSONObject;
import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.selffetchshop.SelffetchShopSearchValidate;
import com.mdd.front.vo.selffetchOrder.SelffetchShopLastDetailVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopListedVo;

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
     * 最后一次使用的自提点
     * @param id
     * @return
     */
    SelffetchShopLastDetailVo lastDetailById(Integer id);
}
