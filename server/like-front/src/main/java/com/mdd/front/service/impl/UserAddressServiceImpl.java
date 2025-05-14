package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mdd.front.validate.user.UserAddressCreateValidate;
import com.mdd.front.validate.user.UserAddressRegionIdValidate;
import com.mdd.front.vo.goods.GoodsAddressVo;
import com.mdd.front.vo.user.UserAddressRegionIdVo;
import com.mdd.common.entity.DevRegion;
import com.mdd.common.entity.user.UserAddress;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.DevRegionMapper;
import com.mdd.common.mapper.user.UserAddressMapper;
import com.mdd.common.util.RegionUtils;
import com.mdd.front.service.IUserAddressService;
import com.mdd.front.validate.user.UserAddressUpdateValidate;
import com.mdd.front.vo.user.UserAddressVo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户地址服务实现类
 */
@Service
public class UserAddressServiceImpl implements IUserAddressService {

    @Resource
    UserAddressMapper userAddressMapper;

    @Resource
    DevRegionMapper devRegionMapper;

    /**
     * 用户地址列表
     *
     * @author cjhao
     * @param userId Integer
     * @return List<UserAddressVo>
     */
    @Override
    public List<UserAddressVo> list(Integer userId) {
        List<UserAddress> userAddressList = userAddressMapper.selectList(new QueryWrapper<UserAddress>()
                .eq("is_delete", 0)
                .eq("user_id", userId));

        List<UserAddressVo> lists = new ArrayList<>();
        for (UserAddress userAddress : userAddressList) {
            lists.add(this.__setAddressVo(userAddress));
        }
        return lists;
    }

    /**
     * 添加用户地址
     *
     * @author cjhao
     * @param userId Integer
     * @param addValidate addValidate
     */
    @Override
    public void add(Integer userId, UserAddressCreateValidate addValidate) {
        Map<String, Object> detail = this.__getRegionDetail(
                addValidate.getProvinceId(),
                addValidate.getCityId(),
                addValidate.getDistrictId());

        Assert.notNull(detail.get("provinceId"), "省份错误");
        Assert.notNull(detail.get("cityId"), "市区错误");
        Assert.notNull(detail.get("districtId"), "地区错误");

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setContact(addValidate.getContact());
        userAddress.setMobile(addValidate.getMobile());
        userAddress.setProvinceId(addValidate.getProvinceId());
        userAddress.setCityId(addValidate.getCityId());
        userAddress.setDistrictId(addValidate.getDistrictId());
        userAddress.setInfo(addValidate.getInfo());
        userAddress.setIsDefault(addValidate.getIsDefault());
        userAddress.setCreateTime(System.currentTimeMillis() / 1000);

        if (1 == addValidate.getIsDefault()) {
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0);
            userAddressMapper.update(updateUserAddress, new UpdateWrapper<UserAddress>()
                    .eq("is_default", 1)
                    .eq("user_id", userId));
        } else {
            // 当前没有选中为默认地址，查找当前是否为第一个地址，非第一个则设置为默认
            UserAddress oldUserAddress = userAddressMapper.selectOne(new QueryWrapper<UserAddress>()
                    .eq("is_delete", 0)
                    .eq("user_id", userId)
                    .last("limit 1")); // 修复添加地址错误
            if (oldUserAddress == null) {
                userAddress.setIsDefault(1);
            }
        }
        userAddressMapper.insert(userAddress);
    }

    /**
     * 编辑用户地址
     *
     * @author mjf
     * @param userId Integer
     * @param updateValidate UserAddressUpdateValidate
     */
    @Override
    public void edit(Integer userId, UserAddressUpdateValidate updateValidate) {
        UserAddress userAddress = userAddressMapper.selectOne(new QueryWrapper<UserAddress>()
                .eq("id", updateValidate.getId())
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(userAddress, "用户地址不存在");

        Map<String, Object> detail = this.__getRegionDetail(
                updateValidate.getProvinceId(),
                updateValidate.getCityId(),
                updateValidate.getDistrictId());
        Assert.notNull(detail.get("provinceId"), "省份错误");
        Assert.notNull(detail.get("cityId"), "市区错误");
        Assert.notNull(detail.get("districtId"), "地区错误");

        if (1 == updateValidate.getIsDefault()) {
            UserAddress updateUserAddress = new UserAddress();
            updateUserAddress.setIsDefault(0);
            userAddressMapper.update(updateUserAddress, new UpdateWrapper<UserAddress>()
                    .eq("is_default", 1)
                    .eq("user_id", userId));
        }
        userAddress.setUserId(userId);
        userAddress.setContact(updateValidate.getContact());
        userAddress.setMobile(updateValidate.getMobile());
        userAddress.setProvinceId(updateValidate.getProvinceId());
        userAddress.setCityId(updateValidate.getCityId());
        userAddress.setDistrictId(updateValidate.getDistrictId());
        userAddress.setInfo(updateValidate.getInfo());
        userAddress.setIsDefault(updateValidate.getIsDefault());
        userAddress.setUpdateTime(System.currentTimeMillis() / 1000);
        userAddressMapper.updateById(userAddress);
    }

    /**
     * 编辑用户地址详情
     *
     * @author cjhao
     * @param userId Integer
     * @param id Integer
     * @return UserAddressVo
     */
    @Override
    public UserAddressVo detail(Integer userId, Integer id) {
        UserAddress userAddress = userAddressMapper.selectOne(new QueryWrapper<UserAddress>()
                .eq("is_delete", 0)
                .eq("user_id", userId)
                .eq("id", id)
                .last("limit 1"));
        return __setAddressVo(userAddress);
    }

    /**
     * 删除用户地址详情
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     */
    @Override
    public void del(Integer userId, Integer id) {
        UserAddress userAddress = userAddressMapper.selectOne(new QueryWrapper<UserAddress>()
                .eq("id", id)
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));
        if (null == userAddress) {
            throw new OperateException("用户地址不存在");
        }
        userAddress.setIsDelete(1);
        userAddress.setUpdateTime(System.currentTimeMillis() / 1000);
        userAddressMapper.updateById(userAddress);
    }

    /**
     * 根据id获取用户地址(没有id时获取默认地址,没有默认地址，获取最新一条地址)
     *
     * @author mjf
     * @param addressId Integer
     * @return UserAddressVo
     */
    @Override
    public UserAddressVo getUserAddressById(Integer userId, Integer addressId) {
        QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.eq("user_id", userId);
        // 有地址id则取id，无则取默认地址
        if (addressId != null && addressId > 0) {
            queryWrapper.eq("id", addressId);
        } else {
            queryWrapper.eq("is_default", 1);
        }
        queryWrapper.last("limit 1");
        UserAddress userAddress = userAddressMapper.selectOne(queryWrapper);

        if (null == userAddress) {
            // 没有默认地址且找不到对应地址id时 查找最新一次地址记录
            UserAddress latestAddress = userAddressMapper.selectOne(new QueryWrapper<UserAddress>()
                    .eq("is_delete", 0)
                    .eq("user_id", userId)
                    .orderByDesc("id")
                    .last("limit 1"));

            if (latestAddress == null) {
                return null;
            }
            return __setAddressVo(latestAddress);
        }
        return __setAddressVo(userAddress);
    }

    /**
     * 根据地区名称获取地区id
     *
     * @author mjf
     * @param regionIdValidate UserAddressRegionIdValidate
     * @return UserAddressRegionIdVo
     */
    @Override
    public UserAddressRegionIdVo getRegionId(UserAddressRegionIdValidate regionIdValidate) {
        UserAddressRegionIdVo regionIdVo = new UserAddressRegionIdVo();
        regionIdVo.setProvinceId(__getRegionIdByName(regionIdValidate.getProvince(), 1));
        regionIdVo.setCityId(__getRegionIdByName(regionIdValidate.getCity(), 2));
        regionIdVo.setDistrictId(__getRegionIdByName(regionIdValidate.getDistrict(), 3));
        return regionIdVo;
    }

    /**
     * 商品页默认地址
     *
     * @author mjf
     * @param userId Integer
     * @return GoodsAddressVo
     */
    @Override
    public GoodsAddressVo getGoodsDefaultAddress(Integer userId) {
        GoodsAddressVo vo = new GoodsAddressVo();
        if (userId > 0) {
            QueryWrapper<UserAddress> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_delete", 0);
            queryWrapper.eq("user_id", userId);
            queryWrapper.orderByDesc("is_default");
            queryWrapper.last("limit 1");
            UserAddress userAddress = userAddressMapper.selectOne(queryWrapper);
            if (userAddress != null) {
                DevRegion regionProvince = devRegionMapper.selectById(userAddress.getProvinceId());
                DevRegion regionCity = devRegionMapper.selectById(userAddress.getCityId());
                DevRegion regionDistrict = devRegionMapper.selectById(userAddress.getDistrictId());
                vo.setAddressId(userAddress.getId());
                vo.setProvinceName(regionProvince.getName());
                vo.setCityName(regionCity.getName());
                vo.setDistrictName(regionDistrict.getName());
                return vo;
            }
        }
        vo.setAddressId(0);
        vo.setProvinceName("北京");
        vo.setCityName("北京市");
        vo.setDistrictName("朝阳区");
        return vo;
    }

    /**
     * 根据地区名称获取地区id
     *
     * @author mjf
     * @param regionName String
     * @param level Integer
     * @return Integer
     */
    private Integer __getRegionIdByName(String regionName, Integer level) {
        // 地区列表
        List<DevRegion> regionList = devRegionMapper.selectList(new QueryWrapper<DevRegion>()
                .eq("level", level));
        // 地区id
        Integer regionId = 0;
        for (DevRegion devRegion : regionList) {
            if (devRegion.getName().equals(regionName)) {
                regionId = devRegion.getId();
            }
        }

        if (regionId != 0) {
            return regionId;
        }

        for (DevRegion devRegion : regionList) {
            if (devRegion.getName().contains(regionName)) {
                regionId = devRegion.getId();
            }
        }

        if (regionId == 0) {
            for (DevRegion devRegion : regionList) {
                if (regionName.contains(devRegion.getName())) {
                    regionId = devRegion.getId();
                }
            }
        }
        return regionId;
    }

    /**
     * 获取地区详情
     *
     * @author mjf
     * @param provinceId Integer
     * @param cityId Integer
     * @param districtId Integer
     * @return Map<String,Object>
     */
    private Map<String, Object> __getRegionDetail(Integer provinceId, Integer cityId, Integer districtId) {
        ArrayList<Integer> addressIds = new ArrayList<>();
        addressIds.add(provinceId);
        addressIds.add(cityId);
        addressIds.add(districtId);
        return RegionUtils.getDetail(addressIds);
    }

    /**
     * 设置addressVo
     *
     * @author mjf
     * @param userAddress UserAddress
     * @return UserAddressVo
     */
    private UserAddressVo __setAddressVo(UserAddress userAddress) {
        Map<String, Object> detail = this.__getRegionDetail(
                userAddress.getProvinceId(),
                userAddress.getCityId(),
                userAddress.getDistrictId());

        UserAddressVo userAddressVo = new UserAddressVo();
        userAddressVo.setId(userAddress.getId());
        userAddressVo.setContact(userAddress.getContact());
        userAddressVo.setMobile(userAddress.getMobile());
        userAddressVo.setProvinceId((Integer) detail.get("provinceId"));
        userAddressVo.setCityId((Integer) detail.get("cityId"));
        userAddressVo.setDistrictId((Integer) detail.get("districtId"));
        userAddressVo.setProvinceName(detail.get("provinceName").toString());
        userAddressVo.setCityName(detail.get("cityName").toString());
        userAddressVo.setDistrictName(detail.get("districtName").toString());
        userAddressVo.setInfo(userAddress.getInfo());
        String addressDetail = detail.get("provinceName").toString() + detail.get("cityName").toString()
                + detail.get("districtName").toString() + userAddress.getInfo();
        userAddressVo.setAddressDetail(addressDetail);
        userAddressVo.setIsDefault(userAddress.getIsDefault());
        return userAddressVo;
    }

}
