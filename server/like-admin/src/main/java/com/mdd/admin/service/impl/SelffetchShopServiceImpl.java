package com.mdd.admin.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.service.IDistributionApplyService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.service.ISelffetchShopService;
import com.mdd.admin.validate.selffetchshop.SelffetchShopCreateValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopStatusValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopUpdateValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopSearchValidate;
import com.mdd.admin.validate.setting.RegionSearchValidate;
import com.mdd.admin.vo.selffetchshop.SelffetchShopListedVo;
import com.mdd.admin.vo.selffetchshop.SelffetchShopDetailVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.selffetchshop.SelffetchShop;
import com.mdd.common.mapper.selffetchshop.SelffetchShopMapper;
import com.mdd.common.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 自提门店实现类
 * @author LikeAdmin
 */
@Service
public class SelffetchShopServiceImpl implements ISelffetchShopService {
        
    @Resource
    SelffetchShopMapper selffetchShopMapper;
    @Resource
    IDistributionApplyService iDistributionApplyService;

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
            "=:longitude:str",
            "=:latitude:str",
            "=:businessStartTime@business_start_time:str",
            "=:businessEndTime@business_end_time:str",
            "=:weekdays:str",
            "=:status:int",
            "=:remark:str",
        });

        IPage<SelffetchShop> iPage = selffetchShopMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<SelffetchShopListedVo> list = new LinkedList<>();
        for(SelffetchShop item : iPage.getRecords()) {
            SelffetchShopListedVo vo = new SelffetchShopListedVo();
            BeanUtils.copyProperties(item, vo);
            vo.setImage(UrlUtils.toAbsoluteUrl(item.getImage()));
            vo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(item.getUpdateTime()));
            vo.setDetailedAddress(iDistributionApplyService.getRegion(item.getProvince(), item.getCity(), item.getDistrict()) + item.getAddress());
            list.add(vo);
        }

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

        Assert.notNull(model, "数据不存在");

        SelffetchShopDetailVo vo = new SelffetchShopDetailVo();
        BeanUtils.copyProperties(model, vo);
        vo.setImage(UrlUtils.toAbsoluteUrl(model.getImage()));
        return vo;
    }

    /**
     * 自提门店新增
     *
     * @author LikeAdmin
     * @param createValidate 参数
     */
    @Override
    public void add(SelffetchShopCreateValidate createValidate) {
        Assert.isTrue(nameUnique(createValidate.getName()), "名称重复");
        SelffetchShop model = new SelffetchShop();
        model.setName(createValidate.getName());
        model.setImage(UrlUtils.toRelativeUrl(createValidate.getImage()));
        model.setContact(createValidate.getContact());
        model.setMobile(createValidate.getMobile());
        model.setProvince(createValidate.getProvince());
        model.setCity(createValidate.getCity());
        model.setDistrict(createValidate.getDistrict());
        model.setAddress(createValidate.getAddress());
        model.setLongitude(createValidate.getLongitude());
        model.setLatitude(createValidate.getLatitude());
        model.setBusinessStartTime(createValidate.getBusinessStartTime());
        model.setBusinessEndTime(createValidate.getBusinessEndTime());
        model.setWeekdays(createValidate.getWeekdays());
        model.setStatus(createValidate.getStatus());
        model.setRemark(createValidate.getRemark());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        selffetchShopMapper.insert(model);
    }

    /**
     * 自提门店编辑
     *
     * @author LikeAdmin
     * @param updateValidate 参数
     */
    @Override
    public void edit(SelffetchShopUpdateValidate updateValidate) {
        Assert.isTrue(nameUnique(updateValidate.getName(), updateValidate.getId()), "名称重复");
        SelffetchShop model = selffetchShopMapper.selectOne(
                new QueryWrapper<SelffetchShop>()
                    .eq("id",  updateValidate.getId())
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setId(updateValidate.getId());
        model.setName(updateValidate.getName());
        model.setImage(UrlUtils.toRelativeUrl(updateValidate.getImage()));
        model.setContact(updateValidate.getContact());
        model.setMobile(updateValidate.getMobile());
        model.setProvince(updateValidate.getProvince());
        model.setCity(updateValidate.getCity());
        model.setDistrict(updateValidate.getDistrict());
        model.setAddress(updateValidate.getAddress());
        model.setLongitude(updateValidate.getLongitude());
        model.setLatitude(updateValidate.getLatitude());
        model.setBusinessStartTime(updateValidate.getBusinessStartTime());
        model.setBusinessEndTime(updateValidate.getBusinessEndTime());
        model.setWeekdays(updateValidate.getWeekdays());
        model.setStatus(updateValidate.getStatus());
        model.setRemark(updateValidate.getRemark());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        selffetchShopMapper.updateById(model);
    }

    /**
     * 自提门店删除
     *
     * @author LikeAdmin
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SelffetchShop model = selffetchShopMapper.selectOne(
                new QueryWrapper<SelffetchShop>()
                    .eq("id", id)
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setIsDelete(1);
        model.setDeleteTime(System.currentTimeMillis() / 1000);
        selffetchShopMapper.updateById(model);
    }


    @Override
    public JSONObject regionSearch(RegionSearchValidate regionSearchValidate) {
        String ret = HttpUtils.sendGet("https://apis.map.qq.com/ws/place/v1/search", HttpUtils.httpBuildQuery(MapUtils.objectToMap(regionSearchValidate)));
        return JSONObject.parseObject(ret);
    }

    @Override
    public void status(SelffetchShopStatusValidate selffetchShopStatusValidate) {
        SelffetchShop model = selffetchShopMapper.selectOne(
                new QueryWrapper<SelffetchShop>()
                        .eq("id",  selffetchShopStatusValidate.getId())
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(selffetchShopStatusValidate.getId());
        model.setStatus(selffetchShopStatusValidate.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        selffetchShopMapper.updateById(model);
    }

    @Override
    public Boolean nameUnique(String name) {
        return  getNameUnique(name, 0);
    }

    @Override
    public Boolean nameUnique(String name, Integer id) {
        return  getNameUnique(name, id);
    }

    @Override
    public String getNameById(Integer id) {
        QueryWrapper<SelffetchShop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).select("name");
        queryWrapper.eq("is_delete", 0).last("limit 1");
        SelffetchShop model = selffetchShopMapper.selectOne(queryWrapper);
        return StringUtils.isNull(model) ? "" : model.getName();
    }

    @Override
    public String getAddressStrById(Integer id) {
        String ret = "";
        SelffetchShop model = selffetchShopMapper.selectOne(
                new QueryWrapper<SelffetchShop>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));
        if (StringUtils.isNull(model)) {
            return ret;
        } else {
            return iDistributionApplyService.getRegion(model.getProvince(), model.getCity(), model.getDistrict()) + model.getAddress();
        }
    }

    @Override
    public String getMobileById(Integer id) {
        QueryWrapper<SelffetchShop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).select("mobile");
        queryWrapper.eq("is_delete", 0).last("limit 1");
        SelffetchShop model = selffetchShopMapper.selectOne(queryWrapper);
        return StringUtils.isNull(model) ? "" : model.getMobile();
    }

    /**
     * 返回 true 代表唯一
     * 返回 false 代表已有记录
     * @param name
     * @param id
     * @return
     */
    private Boolean getNameUnique(String name, Integer id) {
        QueryWrapper<SelffetchShop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name).select("id");
        if (id != 0) {
            queryWrapper.ne("id", id);
        }
        queryWrapper.eq("is_delete", 0).last("limit 1");
        SelffetchShop model = selffetchShopMapper.selectOne(queryWrapper);
        return StringUtils.isNull(model) ? true : false;
    }
}
