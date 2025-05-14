package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.service.ISelffetchShopService;
import com.mdd.admin.service.IUserService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.service.ISelffetchVerifierService;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierCreateValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierStatusValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierUpdateValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierSearchValidate;
import com.mdd.admin.vo.selffetchVerifier.SelffetchVerifierListedVo;
import com.mdd.admin.vo.selffetchVerifier.SelffetchVerifierDetailVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.selffetchVerifier.SelffetchVerifier;
import com.mdd.common.entity.selffetchshop.SelffetchShop;
import com.mdd.common.entity.user.User;
import com.mdd.common.mapper.selffetchVerifier.SelffetchVerifierMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 核销员实现类
 * @author LikeAdmin
 */
@Service
public class SelffetchVerifierServiceImpl implements ISelffetchVerifierService {
        
    @Resource
    SelffetchVerifierMapper selffetchVerifierMapper;
    @Resource
    ISelffetchShopService iSelffetchShopService;
    @Resource
    IUserService iUserService;

    /**
     * 核销员列表
     *
     * @author LikeAdmin
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<SelffetchVerifierListedVo>
     */
    @Override
    public PageResult<SelffetchVerifierListedVo> list(PageValidate pageValidate, SelffetchVerifierSearchValidate searchValidate) {
        Integer page  = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();

        QueryWrapper<SelffetchVerifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        selffetchVerifierMapper.setSearch(queryWrapper, searchValidate, new String[]{
            "=:selffetchShopId@selffetch_shop_id:int",
            "=:userId@user_id:int",
            "=:sn:str",
            "like:name:str",
            "like:mobile:str",
            "=:status:int",
        });

        IPage<SelffetchVerifier> iPage = selffetchVerifierMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<SelffetchVerifierListedVo> list = new LinkedList<>();
        for(SelffetchVerifier item : iPage.getRecords()) {
            SelffetchVerifierListedVo vo = new SelffetchVerifierListedVo();
            BeanUtils.copyProperties(item, vo);
            vo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            vo.setUpdateTime(TimeUtils.timestampToDate(item.getUpdateTime()));
            vo.setSelffetchShopName(iSelffetchShopService.getNameById(item.getSelffetchShopId()));
            User user = iUserService.getUserByID(vo.getUserId());
            vo.setNickname(StringUtils.isNull(user) ? "" : user.getNickname());
            vo.setAvatar(StringUtils.isNull(user) ? "" : UrlUtils.toAbsoluteUrl(user.getAvatar()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 核销员详情
     *
     * @author LikeAdmin
     * @param id 主键参数
     * @return SelffetchVerifier
     */
    @Override
    public SelffetchVerifierDetailVo detail(Integer id) {
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(
                new QueryWrapper<SelffetchVerifier>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SelffetchVerifierDetailVo vo = new SelffetchVerifierDetailVo();
        BeanUtils.copyProperties(model, vo);
        User user = iUserService.getUserByID(vo.getUserId());
        vo.setSelffetchShopName(iSelffetchShopService.getNameById(vo.getSelffetchShopId()));
        vo.setNickname(StringUtils.isNull(user) ? "" : user.getNickname());
        return vo;
    }

    /**
     * 核销员新增
     *
     * @author LikeAdmin
     * @param createValidate 参数
     */
    @Override
    public void add(SelffetchVerifierCreateValidate createValidate) {
        Assert.isTrue(nameUnique(createValidate.getName()), "核销员名称已存在");
        SelffetchVerifier model = new SelffetchVerifier();
        model.setSelffetchShopId(createValidate.getSelffetchShopId());
        model.setUserId(createValidate.getUserId());
        model.setSn(this.__generateSn().toString());
        model.setName(createValidate.getName());
        model.setMobile(createValidate.getMobile());
        model.setStatus(createValidate.getStatus());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        selffetchVerifierMapper.insert(model);
    }

    /**
     * 核销员编辑
     *
     * @author LikeAdmin
     * @param updateValidate 参数
     */
    @Override
    public void edit(SelffetchVerifierUpdateValidate updateValidate) {
        Assert.isTrue(nameUnique(updateValidate.getName(), updateValidate.getId()), "核销员名称已存在");
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(
                new QueryWrapper<SelffetchVerifier>()
                    .eq("id",  updateValidate.getId())
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setId(updateValidate.getId());
        model.setSelffetchShopId(updateValidate.getSelffetchShopId());
        model.setUserId(updateValidate.getUserId());
        model.setName(updateValidate.getName());
        model.setMobile(updateValidate.getMobile());
        model.setStatus(updateValidate.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        selffetchVerifierMapper.updateById(model);
    }

    /**
     * 核销员删除
     *
     * @author LikeAdmin
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(
                new QueryWrapper<SelffetchVerifier>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        selffetchVerifierMapper.delete(new QueryWrapper<SelffetchVerifier>().eq("id", id));
    }

    /**
     * 生成编号
     *
     * @author fzr
     * @return Integer
     */
    private Integer __generateSn() {
        Integer sn;
        while (true) {
            sn = Integer.parseInt(ToolUtils.randomInt(8));
            SelffetchVerifier snModel = selffetchVerifierMapper.selectOne(new QueryWrapper<SelffetchVerifier>()
                    .select("id,sn")
                    .eq("sn", sn)
                    .last("limit 1"));
            if (snModel == null) {
                break;
            }
        }
        return sn;
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
        QueryWrapper<SelffetchVerifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).select("name");
        queryWrapper.last("limit 1");
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(queryWrapper);
        return StringUtils.isNull(model) ? "" : model.getName();
    }

    @Override
    public void status(SelffetchVerifierStatusValidate selffetchVerifierStatusValidate) {
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(
                new QueryWrapper<SelffetchVerifier>()
                        .eq("id",  selffetchVerifierStatusValidate.getId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setId(selffetchVerifierStatusValidate.getId());
        model.setStatus(selffetchVerifierStatusValidate.getStatus());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        selffetchVerifierMapper.updateById(model);
    }

    /**
     * 返回 true 代表唯一
     * 返回 false 代表已有记录
     * @param name
     * @param id
     * @return
     */
    private Boolean getNameUnique(String name, Integer id) {
        QueryWrapper<SelffetchVerifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name).select("id");
        if (id != 0) {
            queryWrapper.ne("id", id);
        }
        queryWrapper.last("limit 1");
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(queryWrapper);
        return StringUtils.isNull(model) ? true : false;
    }

}
