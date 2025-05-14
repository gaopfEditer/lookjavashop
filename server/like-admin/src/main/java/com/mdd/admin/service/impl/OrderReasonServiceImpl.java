package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.admin.service.IOrderReasonService;
import com.mdd.admin.validate.order.OrderReasonCreateValidate;
import com.mdd.admin.validate.order.OrderReasonSearchValidate;
import com.mdd.admin.validate.order.OrderReasonUpdateValidate;
import com.mdd.common.entity.order.OrderAfterReason;
import com.mdd.common.entity.server.Sys;
import com.mdd.common.mapper.order.OrderAfterReasonMapper;
import com.mdd.common.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 订单售后服务原因服务实现类
 */
@Service
public class OrderReasonServiceImpl implements IOrderReasonService {

    @Resource
    OrderAfterReasonMapper orderAfterReasonMapper;

    /**
     * 原因列表
     *
     * @author fzr
     * @param searchValidate 搜索参数
     * @return List<OrderAfterReason>
     */
    @Override
    public List<OrderAfterReason> list(OrderReasonSearchValidate searchValidate) {

        QueryWrapper<OrderAfterReason> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));

        Integer type = searchValidate.getType();
        searchValidate.setType(StringUtils.isNotNull(type) && type <= 0 ? null : type);

        orderAfterReasonMapper.setSearch(queryWrapper, searchValidate, new String[]{
                "=:type:int",
                "like:keyword@content:str",
                "like:isShow@is_show:str"
        });

        return orderAfterReasonMapper.selectList(queryWrapper);
    }

    /**
     * 原因详情
     *
     * @author fzr
     * @param id 主键
     * @return OrderAfterReason
     */
    @Override
    public OrderAfterReason detail(Integer id) {
        OrderAfterReason reason = orderAfterReasonMapper.selectOne(
                new QueryWrapper<OrderAfterReason>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(reason, "数据不存在!");

        return reason;
    }

    /**
     * 原因新增
     *
     * @author fzr
     * @param createValidate 创建参数
     */
    @Override
    public void add(OrderReasonCreateValidate createValidate) {
        OrderAfterReason reason = new OrderAfterReason();
        reason.setType(createValidate.getType());
        reason.setContent(createValidate.getContent());
        reason.setSort(createValidate.getSort());
        reason.setIsShow(createValidate.getIsShow());
        reason.setIsDelete(0);
        reason.setCreateTime(System.currentTimeMillis() / 1000);
        reason.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterReasonMapper.insert(reason);
    }

    /**
     * 原因编辑
     *
     * @author fzr
     * @param updateValidate 更新参数
     */
    @Override
    public void edit(OrderReasonUpdateValidate updateValidate) {
        OrderAfterReason reason = orderAfterReasonMapper.selectOne(
                new QueryWrapper<OrderAfterReason>()
                    .eq("id", updateValidate.getId())
                    .eq("is_delete", 0)
                    .last("limit 1"));

        Assert.notNull(reason, "数据不存在!");

        reason.setType(updateValidate.getType());
        reason.setContent(updateValidate.getContent());
        reason.setSort(updateValidate.getSort());
        reason.setIsShow(updateValidate.getIsShow());
        reason.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterReasonMapper.updateById(reason);
    }

    /**
     * 原因删除
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void del(Integer id) {
        OrderAfterReason reason = orderAfterReasonMapper.selectOne(
                new QueryWrapper<OrderAfterReason>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(reason, "数据不存在!");

        reason.setIsDelete(1);
        reason.setDeleteTime(System.currentTimeMillis() / 1000);
        orderAfterReasonMapper.updateById(reason);
    }

    /**
     * 原因状态
     *
     * @author fzr
     * @param id 主键
     */
    @Override
    public void change(Integer id) {
        OrderAfterReason reason = orderAfterReasonMapper.selectOne(
                new QueryWrapper<OrderAfterReason>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(reason, "数据不存在!");

        reason.setIsShow(reason.getIsShow().equals(1) ? 0 : 1);
        reason.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterReasonMapper.updateById(reason);
    }

}
