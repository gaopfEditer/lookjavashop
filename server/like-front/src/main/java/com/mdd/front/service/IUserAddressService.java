package com.mdd.front.service;

import com.mdd.front.validate.user.UserAddressCreateValidate;
import com.mdd.front.validate.user.UserAddressRegionIdValidate;
import com.mdd.front.validate.user.UserAddressUpdateValidate;
import com.mdd.front.vo.goods.GoodsAddressVo;
import com.mdd.front.vo.user.UserAddressRegionIdVo;
import com.mdd.front.vo.user.UserAddressVo;

import java.util.List;

/**
 * 用户地址接口服务类
 */
public interface IUserAddressService {

    /**
     * 用户地址列表
     *
     * @author mjf
     * @param userId Integer
     * @return List<UserAdressVo>
     */
    List<UserAddressVo> list(Integer userId);

    /**
     * 添加用户地址
     *
     * @author mjf
     * @param userId Integer
     * @param userAddressAddValidate UserAddressAddValidate
     */
    void add(Integer userId, UserAddressCreateValidate userAddressAddValidate);

    /**
     * 编辑用户地址
     *
     * @author mjf
     * @param userId Integer
     * @param userAddressUpdateValidate UserAddressUpdateValidate
     */
    void edit(Integer userId, UserAddressUpdateValidate userAddressUpdateValidate);

    /**
     * 用户地址详情
     *
     * @author mjf
     * @param id Integer
     * @param userId Integer
     * @return UserAdressVo
     */
    UserAddressVo detail(Integer userId, Integer id);

    /**
     * 删除用户地址
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     */
    void del(Integer userId, Integer id);

    /**
     * 根据id获取用户地址
     *
     * @author mjf
     * @param userId Integer
     * @param addressId Integer
     * @return UserAddressVo
     */
    UserAddressVo getUserAddressById(Integer userId, Integer addressId);

    /**
     * 根据地区名称获取地址地区id
     *
     * @author mjf
     * @param regionIdValidate UserAddressRegionIdValidate
     * @return UserAddressRegionIdVo
     */
    UserAddressRegionIdVo getRegionId(UserAddressRegionIdValidate regionIdValidate);

    /**
     * 商品详情页默认地址
     *
     * @author mjf
     * @param userId Integer
     * @return GoodsAddressVo
     */
    GoodsAddressVo getGoodsDefaultAddress(Integer userId);
}
