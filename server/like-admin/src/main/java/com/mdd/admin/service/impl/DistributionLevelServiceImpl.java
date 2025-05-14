package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.service.IDistributionLevelService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionLevelCreateValidate;
import com.mdd.admin.validate.distribution.DistributionLevelUpdateValidate;
import com.mdd.admin.vo.distribution.DistributionLevelDetailVo;
import com.mdd.admin.vo.distribution.DistributionLevelListedVo;
import com.mdd.admin.vo.distribution.DistributionLevelSelectListVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.distribution.DistributionGoods;
import com.mdd.common.entity.distribution.DistributionLevel;
import com.mdd.common.entity.distribution.DistributionLevelUpdate;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.distribution.DistributionGoodsMapper;
import com.mdd.common.mapper.distribution.DistributionLevelMapper;
import com.mdd.common.mapper.distribution.DistributionLevelUpdateMapper;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DistributionLevelServiceImpl implements IDistributionLevelService {

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    DistributionGoodsMapper distributionGoodsMapper;

    @Resource
    DistributionLevelMapper distributionLevelMapper;

    @Resource
    DistributionLevelUpdateMapper distributionLevelUpdateMapper;

    /**
     * 等级列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return PageResult<DistributionLevelListedVo>
     */
    @Override
    public PageResult<DistributionLevelListedVo> list(PageValidate pageValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        IPage<DistributionLevel> iPage = distributionLevelMapper.selectPage(new Page<>(pageNo, pageSize),
                new QueryWrapper<DistributionLevel>()
                        .eq("is_delete", 0)
                        .orderByAsc("weights"));

        List<DistributionLevelListedVo> list = new ArrayList<>();
        for (DistributionLevel item : iPage.getRecords()) {
            DistributionLevelListedVo vo = new DistributionLevelListedVo();
            vo.setId(item.getId());
            vo.setName(item.getName());
            vo.setWeights(item.getWeights());
            vo.setIcon(UrlUtils.toAbsoluteUrl(item.getIcon()));
            vo.setImage(UrlUtils.toAbsoluteUrl(item.getImage()));
            vo.setSelfRatio(item.getSelfRatio());
            vo.setFirstRatio(item.getFirstRatio());
            vo.setSecondRatio(item.getSecondRatio());
            vo.setIsDefault(item.getIsDefault());
            // 分销人数统计
            Long memberNum = distributionMapper.selectCount(new QueryWrapper<Distribution>()
                    .eq("is_delete", 0)
                    .eq("is_distribution", 1)
                    .eq("level_id", item.getId()));
            vo.setMemberNum(memberNum);
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 添加等级
     *
     * @author mjf
     * @param createValidate DistributionLevelCreateValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(DistributionLevelCreateValidate createValidate) {
        // 等级名称
        DistributionLevel checkLevelName = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("name", createValidate.getName())
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.isNull(checkLevelName, "等级名称已存在");

        // 等级级别
        DistributionLevel checkWeights = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("weights", createValidate.getWeights())
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.isNull(checkWeights, "等级级别已存在");

        // 佣金总比例
        double ratioSum = createValidate.getSelfRatio() + createValidate.getFirstRatio() + createValidate.getSecondRatio();
        if (ratioSum > 100) {
            throw new OperateException("等级佣金比例总和不能超过100%");
        }

        DistributionLevel level = new DistributionLevel();
        level.setName(createValidate.getName());
        level.setWeights(createValidate.getWeights());
        level.setSelfRatio(createValidate.getSelfRatio());
        level.setFirstRatio(createValidate.getFirstRatio());
        level.setSecondRatio(createValidate.getSecondRatio());
        level.setIcon(UrlUtils.toRelativeUrl(createValidate.getIcon()));
        level.setImage(UrlUtils.toRelativeUrl(createValidate.getImage()));
        level.setUpdateType(createValidate.getUpdateType());
        level.setCreateTime(System.currentTimeMillis() / 1000);
        level.setUpdateTime(System.currentTimeMillis() / 1000);
        if (StringUtils.isNotBlank(createValidate.getRemark())) {
            level.setRemark(createValidate.getRemark());
        }
        distributionLevelMapper.insert(level);

        if (createValidate.getSingleConsumptionAmount() == null
                && createValidate.getCumulativeConsumptionAmount() == null
                && createValidate.getCumulativeConsumptionTimes() == null
                && createValidate.getReturnedCommission() == null) {
            throw new OperateException("请选择升级条件");
        }

        // 单笔消费金额
        if (createValidate.getSingleConsumptionAmount() != null && createValidate.getSingleConsumptionAmount().compareTo(BigDecimal.ZERO) > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_SINGLE_AMOUNT.getCode(),
                    createValidate.getSingleConsumptionAmount());
        }
        // 累计消费金额
        if (createValidate.getCumulativeConsumptionAmount() != null && createValidate.getCumulativeConsumptionAmount().compareTo(BigDecimal.ZERO) > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_CUMULATIVE_AMOUNT.getCode(),
                    createValidate.getCumulativeConsumptionAmount());
        }
        // 累计消费次数
        if (createValidate.getCumulativeConsumptionTimes() != null && createValidate.getCumulativeConsumptionTimes() > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_CUMULATIVE_TIME.getCode(),
                    new BigDecimal(createValidate.getCumulativeConsumptionTimes()));
        }
        // 已结算佣金收入
        if (createValidate.getReturnedCommission() != null && createValidate.getReturnedCommission().compareTo(BigDecimal.ZERO) > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_SETTLE_AMOUNT.getCode(),
                    createValidate.getReturnedCommission());
        }

        //新增等级后增加分销商品记录(单独设置佣金类型)，初始化商品该等级对应的佣金比例
        List<DistributionGoods> goodsList = distributionGoodsMapper.selectList(new QueryWrapper<DistributionGoods>()
                .select("DISTINCT sku_id, goods_id")
                .eq("is_delete", 0)
                .eq("rule", DistributionEnum.GOODS_RULE_SINGLE.getCode()));

        for (DistributionGoods goods : goodsList) {
            DistributionGoods distributionGoods = new DistributionGoods();
            distributionGoods.setGoodsId(goods.getGoodsId());
            distributionGoods.setSkuId(goods.getSkuId());
            distributionGoods.setLevelId(level.getId());
            distributionGoods.setFirstRatio(0.0);
            distributionGoods.setSecondRatio(0.0);
            distributionGoods.setRule(DistributionEnum.GOODS_RULE_SINGLE.getCode());
            distributionGoods.setCreateTime(System.currentTimeMillis() / 1000);
            distributionGoods.setUpdateTime(System.currentTimeMillis() / 1000);
            distributionGoodsMapper.insert(distributionGoods);
        }
    }

    /**
     * 等级编辑
     *
     * @author mjf
     * @param updateValidate DistributionLevelUpdateValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(DistributionLevelUpdateValidate updateValidate) {
        // 等级名称
        DistributionLevel checkLevelName = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("name", updateValidate.getName())
                .eq("is_delete", 0)
                .ne("id", updateValidate.getId())
                .last("limit 1"));
        Assert.isNull(checkLevelName, "等级名称已存在");

        // 等级级别
        DistributionLevel checkWeights = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("weights", updateValidate.getWeights())
                .eq("is_delete", 0)
                .ne("id", updateValidate.getId())
                .last("limit 1"));
        Assert.isNull(checkWeights, "等级级别已存在");

        // 佣金总比例
        double ratioSum = updateValidate.getSelfRatio() + updateValidate.getFirstRatio() + updateValidate.getSecondRatio();
        if (ratioSum > 100) {
            throw new OperateException("等级佣金比例总和不能超过100%");
        }

        DistributionLevel level = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("id", updateValidate.getId())
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(level, "等级信息不存在");

        level.setName(updateValidate.getName());
        level.setSelfRatio(updateValidate.getSelfRatio());
        level.setFirstRatio(updateValidate.getFirstRatio());
        level.setSecondRatio(updateValidate.getSecondRatio());
        level.setRemark(updateValidate.getRemark());
        level.setIcon(UrlUtils.toRelativeUrl(updateValidate.getIcon()));
        level.setImage(UrlUtils.toRelativeUrl(updateValidate.getImage()));
        level.setUpdateTime(System.currentTimeMillis() / 1000);

        if (level.getIsDefault().equals(1)) {
            // 默认等级
            distributionLevelMapper.updateById(level);
            return;
        }

        // 非默认等级
        if (updateValidate.getWeights() <= 1) {
            throw new OperateException("级别须大于1");
        }
        if (updateValidate.getUpdateType() == null) {
            throw new OperateException("请选择升级类型");
        }
        if (updateValidate.getSingleConsumptionAmount() == null
                && updateValidate.getCumulativeConsumptionAmount() == null
                && updateValidate.getCumulativeConsumptionTimes() == null
                && updateValidate.getReturnedCommission() == null) {
            throw new OperateException("请选择升级条件");
        }

        level.setWeights(updateValidate.getWeights());
        level.setUpdateType(updateValidate.getUpdateType());
        distributionLevelMapper.updateById(level);

        // 删除旧升级条件
        DistributionLevelUpdate oldLevelCondition = new DistributionLevelUpdate();
        oldLevelCondition.setIsDelete(1);
        oldLevelCondition.setUpdateTime(System.currentTimeMillis() / 1000);
        oldLevelCondition.setDeleteTime(System.currentTimeMillis() / 1000);
        distributionLevelUpdateMapper.update(oldLevelCondition, new QueryWrapper<DistributionLevelUpdate>()
                .eq("level_id", level.getId()));

        // 新增新升级条件
        if (updateValidate.getSingleConsumptionAmount() != null && updateValidate.getSingleConsumptionAmount().compareTo(BigDecimal.ZERO) > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_SINGLE_AMOUNT.getCode(),
                    updateValidate.getSingleConsumptionAmount());
        }
        // 累计消费金额
        if (updateValidate.getCumulativeConsumptionAmount() != null && updateValidate.getCumulativeConsumptionAmount().compareTo(BigDecimal.ZERO) > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_CUMULATIVE_AMOUNT.getCode(),
                    updateValidate.getCumulativeConsumptionAmount());
        }
        // 累计消费次数
        if (updateValidate.getCumulativeConsumptionTimes() != null && updateValidate.getCumulativeConsumptionTimes() > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_CUMULATIVE_TIME.getCode(),
                    new BigDecimal(updateValidate.getCumulativeConsumptionTimes()));
        }
        // 已结算佣金收入
        if (updateValidate.getReturnedCommission() != null && updateValidate.getReturnedCommission().compareTo(BigDecimal.ZERO) > 0) {
            this.__insertLevelCondition(level.getId(),
                    DistributionEnum.LEVEL_UPDATE_SETTLE_AMOUNT.getCode(),
                    updateValidate.getReturnedCommission());
        }
    }

    /**
     * 等级详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionLevelDetailVo
     */
    @Override
    public DistributionLevelDetailVo detail(Integer id) {
        DistributionLevel level = distributionLevelMapper.selectById(id);
        Assert.notNull(level, "等级不存在");

        DistributionLevelDetailVo vo = new DistributionLevelDetailVo();
        BeanUtils.copyProperties(level, vo);
        vo.setIcon(UrlUtils.toAbsoluteUrl(vo.getIcon()));
        vo.setImage(UrlUtils.toAbsoluteUrl(vo.getImage()));

        // 等级条件
        List<DistributionLevelUpdate> levelCondition = distributionLevelUpdateMapper.selectList(new QueryWrapper<DistributionLevelUpdate>()
                .eq("is_delete", 0)
                .eq("level_id", level.getId()));

        for (DistributionLevelUpdate item : levelCondition) {
            if (item.getScene().equals(DistributionEnum.LEVEL_UPDATE_SINGLE_AMOUNT.getCode())) {
                vo.setSingleConsumptionAmount(item.getValue());
            }
            if (item.getScene().equals(DistributionEnum.LEVEL_UPDATE_CUMULATIVE_AMOUNT.getCode())) {
                vo.setCumulativeConsumptionAmount(item.getValue());
            }
            if (item.getScene().equals(DistributionEnum.LEVEL_UPDATE_CUMULATIVE_TIME.getCode())) {
                vo.setCumulativeConsumptionTimes(item.getValue().intValue());
            }
            if (item.getScene().equals(DistributionEnum.LEVEL_UPDATE_SETTLE_AMOUNT.getCode())) {
                vo.setReturnedCommission(item.getValue());
            }
        }
        return vo;
    }

    /**
     * 等级删除
     *
     * @author mjf
     * @param id Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        DistributionLevel level = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("is_delete", 0)
                .eq("id", id)
                .last("limit 1"));
        Assert.notNull(level, "等级信息不存在");

        if (level.getIsDefault().equals(1)) {
            throw new OperateException("默认等级不允许删除");
        }

        // 重置该等级下的分销会员为系统默认等级
        DistributionLevel defaultLevel = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("is_default", 1)
                .eq("is_delete", 0)
                .last("limit 1"));

        Distribution distribution = new Distribution();
        distribution.setLevelId(defaultLevel.getId());
        distribution.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionMapper.update(distribution, new QueryWrapper<Distribution>()
                .eq("level_id", level.getId()));

        // 删除关联升级条件
        DistributionLevelUpdate levelUpdate = new DistributionLevelUpdate();
        levelUpdate.setIsDelete(1);
        levelUpdate.setUpdateTime(System.currentTimeMillis() / 1000);
        levelUpdate.setDeleteTime(System.currentTimeMillis() / 1000);
        distributionLevelUpdateMapper.update(levelUpdate, new QueryWrapper<DistributionLevelUpdate>()
                .eq("level_id", level.getId()));

        // 删除关联分销商品比例
        DistributionGoods goods = new DistributionGoods();
        goods.setIsDelete(1);
        goods.setUpdateTime(System.currentTimeMillis() / 1000);
        goods.setDeleteTime(System.currentTimeMillis() / 1000);
        distributionGoodsMapper.update(goods, new QueryWrapper<DistributionGoods>()
                .eq("level_id", level.getId()));

        // 删除状态
        level.setIsDelete(1);
        level.setUpdateTime(System.currentTimeMillis() / 1000);
        level.setDeleteTime(System.currentTimeMillis() / 1000);
        distributionLevelMapper.updateById(level);
    }

    /**
     * 等级列表
     *
     * @author mjf
     * @return List<DistributionLevelSelectListVo>
     */
    @Override
    public List<DistributionLevelSelectListVo> selectList() {

        List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>()
                .eq("is_delete", 0)
                .orderByAsc("weights"));

        List<DistributionLevelSelectListVo> list = new ArrayList<>();
        for (DistributionLevel level : levelList) {
            DistributionLevelSelectListVo vo = new DistributionLevelSelectListVo();
            vo.setId(level.getId());
            vo.setName(level.getName());
            list.add(vo);
        }
        return list;
    }

    /**
     * 新增等级升级条件
     *
     * @author mjf
     * @param levelId Integer
     * @param scene Integer
     * @param value BigDecimal
     */
    private void __insertLevelCondition(Integer levelId, Integer scene, BigDecimal value) {
        DistributionLevelUpdate condition = new DistributionLevelUpdate();
        condition.setLevelId(levelId);
        condition.setScene(scene);
        condition.setValue(value);
        condition.setCreateTime(System.currentTimeMillis() / 1000);
        condition.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionLevelUpdateMapper.insert(condition);
    }

}
