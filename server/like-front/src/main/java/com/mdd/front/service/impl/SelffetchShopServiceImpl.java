package com.mdd.front.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.selffetchshop.SelffetchShop;
import com.mdd.common.mapper.selffetchshop.SelffetchShopMapper;
import com.mdd.common.util.*;
import com.mdd.front.service.IDistributionService;
import com.mdd.front.service.ISelffetchShopService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.selffetchshop.SelffetchShopSearchValidate;
import com.mdd.front.vo.selffetchOrder.SelffetchShopLastDetailVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.front.vo.selffetchshop.SelffetchShopListedVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 自提门店实现类
 * @author LikeAdmin
 */
@Service
public class SelffetchShopServiceImpl implements ISelffetchShopService {
        
    @Resource
    SelffetchShopMapper selffetchShopMapper;
    @Resource
    IDistributionService iDistributionService;

    /**
     * 自提门店列表
     *
     * @author LikeAdmin
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<SelffetchShopListedVo>
     */
    @Override
    public PageResult<SelffetchShopListedVo> list(PageValidate pageValidate, SelffetchShopSearchValidate searchValidate) {
        Integer page  = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();

        QueryWrapper<SelffetchShop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc("id");

        selffetchShopMapper.setSearch(queryWrapper, searchValidate, new String[]{
            "like:name:str",
            "=:image:str",
            "=:contact:str",
            "like:mobile:str",
            "=:province:int",
            "=:city:int",
            "=:district:int",
            "=:address:str",
            "=:businessStartTime@business_start_time:str",
            "=:businessEndTime@business_end_time:str",
            "=:weekdays:str",
            "=:status:int",
            "=:remark:str",
        });
        if (StringUtils.isNotNull(searchValidate.getLatitude()) && StringUtils.isNotNull(searchValidate.getLongitude())) {
            queryWrapper.select("*,ST_DISTANCE_SPHERE(point(longitude, latitude),point(" + searchValidate.getLongitude() +", "+ searchValidate.getLatitude() +")) AS distance").orderByAsc("distance");
        }
        IPage<SelffetchShop> iPage = selffetchShopMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SelffetchShopListedVo> list = new LinkedList<>();
        for(SelffetchShop item : iPage.getRecords()) {
            SelffetchShopListedVo vo = new SelffetchShopListedVo();
            BeanUtils.copyProperties(item, vo);
            vo.setImage(UrlUtils.toAbsoluteUrl(item.getImage()));
            vo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(item.getUpdateTime()));
            vo.setDetailedAddress(iDistributionService.getRegion(item.getProvince(), item.getCity(), item.getDistrict()) + item.getAddress());
            list.add(vo);
        }
        //list.sort(Comparator.comparingInt(map -> map.getDistrict()));
        Collections.sort(list, new Comparator<SelffetchShopListedVo>() {
            @Override
            public int compare(SelffetchShopListedVo o1, SelffetchShopListedVo o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 自提门店详情
     *
     * @author LikeAdmin
     * @param id 主键参数
     * @return SelffetchShop
     */
    @Override
    public SelffetchShopDetailVo detail(Integer id) {
        SelffetchShop model = selffetchShopMapper.selectOne(
                new QueryWrapper<SelffetchShop>()
                    .eq("id", id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        //Assert.notNull(model, "数据不存在");
        SelffetchShopDetailVo vo = new SelffetchShopDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setImage(UrlUtils.toAbsoluteUrl(model.getImage()));
        vo.setShopAddress(iDistributionService.getRegion(model.getProvince(), model.getCity(), model.getDistrict()) + model.getAddress());
        vo.setWeekdaysStr(ToolUtils.convertNumbersToWeekdays(model.getWeekdays()));
        return vo;
    }

    @Override
    public SelffetchShopLastDetailVo lastDetailById(Integer id) {
        SelffetchShop model = selffetchShopMapper.selectOne(
                new QueryWrapper<SelffetchShop>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .eq("status", 1)
                        .last("limit 1"));
        if (StringUtils.isNull(model)) {
            return null;
        }

        SelffetchShopLastDetailVo vo = new SelffetchShopLastDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setShopAddress(iDistributionService.getRegion(model.getProvince(), model.getCity(), model.getDistrict()) + model.getAddress());
        return vo;
    }
}
