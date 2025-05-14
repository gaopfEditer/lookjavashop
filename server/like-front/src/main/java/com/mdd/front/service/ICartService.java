package com.mdd.front.service;

import com.mdd.front.validate.cart.CartAddValidate;
import com.mdd.front.validate.cart.CartChangeNumValidate;
import com.mdd.front.validate.cart.CartChangeSkuValidate;
import com.mdd.front.validate.cart.CartSelectedValidate;
import com.mdd.front.vo.cart.CartCountVo;
import com.mdd.front.vo.cart.CartListVo;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface ICartService {

    /**
     * 购物车列表
     *
     * @author mjf
     * @return CartListVo
     */
    CartListVo list(Integer userId);


    /**
     * 加入购物车
     *
     * @author mjf
     * @param addValidate CartAddValidate
     */
    void add(Integer userId, CartAddValidate addValidate);

    /**
     * 购物车选中
     *
     * @author mjf
     * @param cartSelectedValidate CartSelectedValidate
     */
    void select(Integer userId, CartSelectedValidate cartSelectedValidate);

    /**
     * 购物车变动数量
     *
     * @author mjf
     * @param changeNumValidate CartChangeNumValidate
     */
    void changeNum(Integer userId, CartChangeNumValidate changeNumValidate);

    /**
     * 购物车变动规格
     *
     * @author mjf
     * @param changeSkuValidate CartChangeSkuValidate
     */
    void changeSku(Integer userId, CartChangeSkuValidate changeSkuValidate);

    /**
     * 删除购物车
     *
     * @author mjf
     * @param ids List<Integer>
     */
    void del(Integer userId, List<Integer> ids);

    /**
     * 购物车数据
     *
     * @author mjf
     * @param userId Integer
     * @return Integer
     */
    CartCountVo cartCount(Integer userId);
}
