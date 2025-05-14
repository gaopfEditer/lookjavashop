package com.mdd.common.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.common.entity.DevRegion;
import com.mdd.common.mapper.DevRegionMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 地区工具类
 */
public class RegionUtils {

    /**
     * 地区详情
     *
     * @author cjhao
     * @param addressId List<Integer>
     * @return Map<String, Object>
     */
    public static Map<String, Object> getDetail(List<Integer> addressId) {

        DevRegionMapper devRegionMapper = SpringUtils.getBean(DevRegionMapper.class);
        List<DevRegion> regionList = devRegionMapper.selectList(new QueryWrapper<DevRegion>()
                .in("id", addressId));

        HashMap<String, Object> regionMap = new HashMap<>();
        for (DevRegion devRegion : regionList) {
            if (1 == devRegion.getLevel()) {
                regionMap.put("provinceId", devRegion.getId());
                regionMap.put("provinceName", devRegion.getName());
            }
            if (2 == devRegion.getLevel()) {
                regionMap.put("cityId", devRegion.getId());
                regionMap.put("cityName", devRegion.getName());
            }
            if (3 == devRegion.getLevel()) {
                regionMap.put("districtId", devRegion.getId());
                regionMap.put("districtName", devRegion.getName());
            }
        }

        return regionMap;
    }
}
