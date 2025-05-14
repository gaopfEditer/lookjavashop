package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.selffetchVerifier.SelffetchVerifier;
import com.mdd.common.entity.user.User;
import com.mdd.common.mapper.selffetchVerifier.SelffetchVerifierMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.ToolUtils;
import com.mdd.common.util.UrlUtils;
import com.mdd.front.service.ISelffetchShopService;
import com.mdd.front.service.ISelffetchVerifierService;
import com.mdd.front.service.IUserService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.selffetchVerifier.SelffetchVerifierSearchValidate;
import com.mdd.front.vo.selffetchVerifier.SelffetchVerifierDetailVo;
import com.mdd.front.vo.selffetchVerifier.SelffetchVerifierListedVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        return vo;
    }

    @Override
    public List<Integer> getShopIds(Integer userId) {
        List<Integer> ret = new ArrayList<Integer>();
        List<SelffetchVerifier> list = selffetchVerifierMapper.selectList(new QueryWrapper<SelffetchVerifier>().eq("user_id", userId).eq("status", 1).select("selffetch_shop_id"));
        if (list.size() > 0) {
            for (SelffetchVerifier item : list) {
                if (ret.contains(item.getSelffetchShopId())) {
                    continue;
                } else {
                    ret.add(item.getSelffetchShopId());
                }
            }
        }
        return ret;
    }

    @Override
    public Integer isSelffetchVerifier(Integer userId) {
        SelffetchVerifier model = selffetchVerifierMapper.selectOne(
                new QueryWrapper<SelffetchVerifier>()
                        .eq("user_id", userId).isNull("delete_time")
                        .last("limit 1"));
        if (StringUtils.isNull(model)) {
            return 0;
        } else {
            if (model.getStatus().equals(1)) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public Boolean verifyShopRights(Integer shopId, Integer userId) {
        Integer isVerifier = isSelffetchVerifier(userId);
        if (isVerifier.equals(0)) {
            return false;
        } else {
            List<Integer> shopIds = getShopIds(userId);
            if (shopIds.contains(shopId)) {
                return true;
            } else {
                return false;
            }
        }
    }

}
