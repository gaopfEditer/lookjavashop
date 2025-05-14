package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.service.IVerificationService;
import com.mdd.admin.validate.VerificationCreateValidate;
import com.mdd.admin.validate.VerificationUpdateValidate;
import com.mdd.admin.validate.VerificationSearchValidate;
import com.mdd.admin.vo.VerificationListedVo;
import com.mdd.admin.vo.VerificationDetailVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.Verification;
import com.mdd.common.mapper.VerificationMapper;
import com.mdd.common.util.ListUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * 核销实现类
 * @author LikeAdmin
 */
@Service
public class VerificationServiceImpl implements IVerificationService {
        
    @Resource
    VerificationMapper verificationMapper;

    /**
     * 核销列表
     *
     * @author LikeAdmin
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<VerificationListedVo>
     */
    @Override
    public PageResult<VerificationListedVo> list(PageValidate pageValidate, VerificationSearchValidate searchValidate) {
        Integer page  = pageValidate.getPageNo();
        Integer limit = pageValidate.getPageSize();

        QueryWrapper<Verification> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");

        verificationMapper.setSearch(queryWrapper, searchValidate, new String[]{
            "=:orderId@order_id:int",
            "=:selffetchShopId@selffetch_shop_id:int",
            "=:handleId@handle_id:int",
            "=:verificationScene@verification_scene:int",
            "=:snapshot:str",
        });

        IPage<Verification> iPage = verificationMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<VerificationListedVo> list = new LinkedList<>();
        for(Verification item : iPage.getRecords()) {
            VerificationListedVo vo = new VerificationListedVo();
            BeanUtils.copyProperties(item, vo);
            vo.setCreateTime(TimeUtils.timestampToDate(item.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 核销详情
     *
     * @author LikeAdmin
     * @param id 主键参数
     * @return Verification
     */
    @Override
    public VerificationDetailVo detail(Integer id) {
        Verification model = verificationMapper.selectOne(
                new QueryWrapper<Verification>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        VerificationDetailVo vo = new VerificationDetailVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 核销新增
     *
     * @author LikeAdmin
     * @param createValidate 参数
     */
    @Override
    public void add(VerificationCreateValidate createValidate) {
        Verification model = new Verification();
        model.setOrderId(createValidate.getOrderId());
        model.setSelffetchShopId(createValidate.getSelffetchShopId());
        model.setHandleId(createValidate.getHandleId());
        model.setVerificationScene(createValidate.getVerificationScene());
        model.setSnapshot(createValidate.getSnapshot());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        verificationMapper.insert(model);
    }

    /**
     * 核销编辑
     *
     * @author LikeAdmin
     * @param updateValidate 参数
     */
    @Override
    public void edit(VerificationUpdateValidate updateValidate) {
        Verification model = verificationMapper.selectOne(
                new QueryWrapper<Verification>()
                    .eq("id",  updateValidate.getId())
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setId(updateValidate.getId());
        model.setOrderId(updateValidate.getOrderId());
        model.setSelffetchShopId(updateValidate.getSelffetchShopId());
        model.setHandleId(updateValidate.getHandleId());
        model.setVerificationScene(updateValidate.getVerificationScene());
        model.setSnapshot(updateValidate.getSnapshot());
        verificationMapper.updateById(model);
    }

    /**
     * 核销删除
     *
     * @author LikeAdmin
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Verification model = verificationMapper.selectOne(
                new QueryWrapper<Verification>()
                    .eq("id", id)
                    .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        verificationMapper.delete(new QueryWrapper<Verification>().eq("id", id));
    }

}
