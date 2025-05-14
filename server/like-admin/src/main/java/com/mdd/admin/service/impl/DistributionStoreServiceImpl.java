package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IDistributionConfigService;
import com.mdd.admin.service.IDistributionStoreService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.*;
import com.mdd.admin.vo.distribution.*;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.distribution.DistributionLevel;
import com.mdd.common.entity.distribution.DistributionLevelUpdate;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.distribution.DistributionLevelMapper;
import com.mdd.common.mapper.distribution.DistributionLevelUpdateMapper;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.common.mapper.distribution.DistributionOrderMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 分销商服务实现类
 */
@Service
public class DistributionStoreServiceImpl implements IDistributionStoreService {

    @Resource
    UserMapper userMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;

    @Resource
    DistributionLevelMapper distributionLevelMapper;

    @Resource
    IDistributionConfigService iDistributionConfigService;

    @Resource
    DistributionLevelUpdateMapper distributionLevelUpdateMapper;

    /**
     * 分销商列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionStoreSearchValidate
     * @return PageResult<DistributionStoreListedVo>
     */
    @Override
    public PageResult<DistributionStoreListedVo> list(PageValidate pageValidate, DistributionStoreSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<User> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.eq("D.is_distribution", 1);
        mpjQueryWrapper.eq("t.is_delete", 0);
        mpjQueryWrapper.eq("t.is_close", 0);
        mpjQueryWrapper.leftJoin("?_distribution D ON D.user_id=t.id".replace("?_", GlobalConfig.tablePrefix));
        mpjQueryWrapper.orderByDesc("t.id");
        mpjQueryWrapper.select("t.id,t.nickname,t.avatar,t.first_leader,t.sn,t.username," +
                "D.id as distribution_id,D.level_id,D.is_freeze,D.distribution_time,D.is_close");

        if (StringUtils.isNotNull(searchValidate.getKeyword()) && StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            mpjQueryWrapper.nested(wq->wq
                    .like("t.sn", keyword).or()
                    .like("t.nickname", keyword).or()
                    .like("t.mobile", keyword));
        }

        // 其它搜索
        userMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "=:levelId@D.level_id:int",
                "=:isFreeze@D.is_freeze:int",
                "datetime:startTime-endTime@D.distribution_time:str",
        });

        IPage<DistributionStoreListedVo> iPage = userMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionStoreListedVo.class,
                mpjQueryWrapper);

        for (DistributionStoreListedVo user : iPage.getRecords()) {
            user.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
            user.setIsFreezeMsg(user.getIsFreeze().equals(0) ? "正常" : "冻结");
            user.setDistributionTime(TimeUtils.timestampToDate(user.getDistributionTime()));
            // 用户等级
            DistributionLevel distributionLevel = distributionLevelMapper.selectById(user.getLevelId());
            user.setLevelName(distributionLevel.getName());
            // 上级名称
            User firstLeader = userMapper.selectById(user.getFirstLeader());
            if (firstLeader != null) {
                user.setFirstLeaderName(firstLeader.getNickname());
            } else {
                user.setFirstLeaderName("系统");
            }
            // 已入账佣金
            user.setReceivedEarnings(this.__getReceivedEarnings(user.getId()));
            // 待结算佣金
            user.setWaitEarnings(this.__getWaitEarnings(user.getId()));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 开通分销商
     *
     * @author mjf
     * @param openValidate DistributionStoreOpenValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void open(DistributionStoreOpenValidate openValidate) {
        Integer levelId = openValidate.getLevelId();
        List<Integer> ids = openValidate.getIds();
        List<Distribution> distributionList = distributionMapper.selectList(new QueryWrapper<Distribution>()
                .eq("is_delete", 0)
                .eq("is_close", 0)
                .in("user_id", ids));

        if (distributionList.size() != ids.size()) {
            throw new OperateException("存在不合法的ID");
        }

        DistributionLevel level = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("id", levelId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(level, "无效的分销等级");

        for (Distribution distribution : distributionList) {
            distribution.setIsDistribution(1);
            distribution.setLevelId(levelId);
            distribution.setDistributionTime(System.currentTimeMillis() / 1000);
            distribution.setUpdateTime(System.currentTimeMillis() / 1000);
            distributionMapper.updateById(distribution);
        }
    }

    /**
     * 冻结/解冻
     *
     * @author mjf
     * @param id Integer
     */
    @Override
    public void freeze(Integer id) {
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", id)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(distribution, "分销商不存在");

        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", id)
                .last("limit 1"));
        if (null != user && user.getIsClose() == 1){
            throw new OperateException("分销商已注销，无法冻结或解冻！");
        }

        distribution.setIsFreeze(distribution.getIsFreeze() == 1 ? 0 : 1);
        distribution.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionMapper.updateById(distribution);
    }

    /**
     *调整等级详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionAdjustLevelVo
     */
    @Override
    public DistributionAdjustLevelVo adjustLevelInfo(Integer id) {
        User user = userMapper.selectById(id);
        Assert.notNull(user, "用户信息不存在");

        // 判断用户是否注销
        if (user.getIsClose() == 1){
            throw new OperateException("已注销用户无法修改信息！");
        }

        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", id)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(distribution, "当前用户非分销用户");

        DistributionAdjustLevelVo vo = new DistributionAdjustLevelVo();
        vo.setId(user.getId());
        vo.setSn(user.getSn());
        vo.setNickname(user.getNickname());
        vo.setLevelId(distribution.getLevelId());
        vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
        vo.setLevelName("");

        DistributionLevel distributionLevel = distributionLevelMapper.selectById(distribution.getLevelId());
        if (distributionLevel != null) {
            vo.setLevelName(distributionLevel.getName() + "(" + distributionLevel.getWeights() + "级)");
        }

        List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>()
                .eq("is_delete", 0)
                .orderByAsc("weights"));

        List<DistributionAdjustLevelVo.levelData> levelData = new LinkedList<>();
        for (DistributionLevel level : levelList) {
            DistributionAdjustLevelVo.levelData levelVo = new DistributionAdjustLevelVo.levelData();
            levelVo.setId(level.getId());
            levelVo.setName(level.getName());
            levelVo.setWeights(level.getWeights());
            levelData.add(levelVo);
        }

        vo.setLevelData(levelData);
        return vo;
    }

    /**
     * 调整分销商等级
     *
     * @author mjf
     * @param adjustValidate DistributionAdjustLevelValidate
     */
    @Override
    public void adjustLevel(DistributionAdjustLevelValidate adjustValidate) {
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", adjustValidate.getUserId())
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(distribution, "分销商不存在");

        // 判断用户是否注销
        if (distribution.getIsClose() == 1){
            throw new OperateException("已注销分销商无法修改信息！");
        }

        DistributionLevel level = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("id", adjustValidate.getLevelId())
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(level, "分销等级不存在");

        distribution.setLevelId(level.getId());
        distribution.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionMapper.updateById(distribution);
    }

    /**
     * 分销商详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionStoreDetailVo
     */
    @Override
    public DistributionStoreDetailVo detail(Integer id) {
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", id)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(distribution, "分销商不存在");

        User user = userMapper.selectById(id);
        Assert.notNull(user, "用户信息不存在");

        DistributionStoreDetailVo vo = new DistributionStoreDetailVo();
        vo.setId(user.getId());
        vo.setSn(user.getSn());
        vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setFirstLeader(user.getFirstLeader());
        vo.setIsFreeze(distribution.getIsFreeze());
        vo.setIsFreezeMsg(distribution.getIsFreeze() == 1 ? "冻结" : "正常");
        vo.setLevelId(distribution.getLevelId());
        vo.setIsClose(distribution.getIsClose());
        vo.setDistributionTime(TimeUtils.timestampToDate(distribution.getDistributionTime()));

        // 分销等级
        DistributionLevel distributionLevel = distributionLevelMapper.selectById(distribution.getLevelId());
        vo.setLevelName(distributionLevel == null ? "无等级" : distributionLevel.getName());

        // 上级名称
        User firstLeader = userMapper.selectById(user.getFirstLeader());
        vo.setFirstLeaderName(firstLeader == null ? "系统" : firstLeader.getNickname());

        // 已入账佣金
        vo.setReceivedEarnings(this.__getReceivedEarnings(user.getId()));
        // 待结算佣金
        vo.setWaitEarnings(this.__getWaitEarnings(user.getId()));

        // 分销订单数量
        Long orderCount = distributionOrderMapper.selectCount(new QueryWrapper<DistributionOrder>()
                .select("DISTINCT order_goods_id")
                .eq("user_id", user.getId())
                .eq("is_delete", 0));
        vo.setDistributionOrderNum(orderCount);

        // 粉丝数量
        List<Integer> fansIds = this.__getFansCount(user.getId(), "all");
        List<Integer> fansFirstIds = this.__getFansCount(user.getId(), "first");
        List<Integer> fansSecondIds = this.__getFansCount(user.getId(), "second");
        vo.setFans(fansIds.size());
        vo.setFansFirst(fansFirstIds.size());
        vo.setFansSecond(fansSecondIds.size());
        vo.setFansDistribution(this.__getDistributionFansCount(fansIds));
        vo.setFansFirstDistribution(this.__getDistributionFansCount(fansFirstIds));
        vo.setFansSecondDistribution(this.__getDistributionFansCount(fansSecondIds));
        return vo;
    }

    /**
     * 下级详情信息
     *
     * @author mjf
     * @param id Integer
     * @return DistributionFansInfoVo
     */
    @Override
    public DistributionFansInfoVo fansInfo(Integer id) {
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", id)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(distribution, "分销商不存在");

        User user = userMapper.selectById(id);
        Assert.notNull(user, "用户信息不存在");

        // 粉丝数量
        List<Integer> fansIds = this.__getFansCount(user.getId(), "all");
        List<Integer> fansFirstIds = this.__getFansCount(user.getId(), "first");
        List<Integer> fansSecondIds = this.__getFansCount(user.getId(), "second");

        DistributionFansInfoVo vo = new DistributionFansInfoVo();
        vo.setId(user.getId());
        vo.setSn(user.getSn());
        vo.setNickname(user.getNickname());
        vo.setFans(fansIds.size());
        vo.setFansFirst(fansFirstIds.size());
        vo.setFansSecond(fansSecondIds.size());
        vo.setFansDistribution(this.__getDistributionFansCount(fansIds));
        vo.setFansFirstDistribution(this.__getDistributionFansCount(fansFirstIds));
        vo.setFansSecondDistribution(this.__getDistributionFansCount(fansSecondIds));
        return vo;
    }

    /**
     * 下级列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionFansSearchValidate
     * @return PageResult<DistributionFansListedVo>
     */
    @Override
    public PageResult<DistributionFansListedVo> fansList(PageValidate pageValidate, DistributionFansSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        Integer userId = searchValidate.getUserId();

        MPJQueryWrapper<User> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.leftJoin("?_distribution D ON D.user_id=t.id".replace("?_", GlobalConfig.tablePrefix));

        if (StringUtils.isNotNull(searchValidate.getKeyword()) && StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            mpjQueryWrapper.nested(wq->wq
                    .like("t.sn", keyword).or()
                    .like("t.nickname", keyword).or()
                    .like("t.mobile", keyword));
        }

        // 其它搜索
        userMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "=:isDistribution@D.is_distribution:int",
        });

        if (searchValidate.getLevel() != null && searchValidate.getLevel() > 0) {
            if (searchValidate.getLevel().equals(1)) {
                mpjQueryWrapper.eq("t.first_leader", userId);
            }
            if (searchValidate.getLevel().equals(2)) {
                mpjQueryWrapper.eq("t.second_leader", userId);
            }
        } else {
            mpjQueryWrapper.nested(wq->wq
                    .eq("t.first_leader", userId).or()
                    .eq("t.second_leader", userId));
        }

        mpjQueryWrapper.select("t.id, t.sn,t.avatar,t.nickname,t.first_leader,t.earnings,t.create_time," +
                "D.level_id,D.is_freeze,D.is_distribution,D.distribution_time");

        IPage<DistributionFansListedVo> iPage = userMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionFansListedVo.class,
                mpjQueryWrapper);

        for (DistributionFansListedVo vo : iPage.getRecords()) {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setIsFreezeMsg(vo.getIsFreeze().equals(0) ? "正常" : "冻结");
            vo.setFirstLeaderName("系统");
            User firstLeader = userMapper.selectById(vo.getFirstLeader());
            if (firstLeader != null) {
                vo.setFirstLeaderName(firstLeader.getNickname());
            }
            vo.setTotalEarnings(this.__getReceivedEarnings(vo.getId()));

            if (vo.getIsDistribution().equals(1)) {
                vo.setIsDistributionMsg("已开通");
                vo.setDistributionTime(TimeUtils.timestampToDate(vo.getDistributionTime()));
            } else {
                vo.setIsDistributionMsg("未开通");
                vo.setDistributionTime("");
            }
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
        }

        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("all", this.__getFansCount(userId, "all").size());
        extend.put("first", this.__getFansCount(userId, "first").size());
        extend.put("second", this.__getFansCount(userId, "second").size());

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 调整上级
     *
     * @author mjf
     * @param adjustValidate DistributionAdjustLeaderValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustLeader(DistributionAdjustLeaderValidate adjustValidate) {
        // 用户ID
        Integer userId = adjustValidate.getUserId();
        // 上级ID
        Integer adjustFirstId = adjustValidate.getFirstId();
        // 初始化调整信息为系统
        Integer firstLeaderId = 0;
        Integer secondLeaderId = 0;
        Integer thirdLeaderId = 0;
        String ancestorRelation = "";

        // 调整类型为指定推荐人
        if (adjustValidate.getType().equals("assign")) {
            if (ObjectUtils.isEmpty(adjustFirstId)) {
                throw new OperateException("请选择上级分销商");
            }
            if (adjustFirstId.equals(userId)) {
                throw new OperateException("上级分销商不可以选择自己");
            }

            User firstLeader = userMapper.selectById(adjustFirstId);
            Assert.notNull(firstLeader, "分销商不存在");

            if (StringUtils.isNotBlank(firstLeader.getAncestorRelation())) {
                String[] list = firstLeader.getAncestorRelation().split(",");
                if (list.length > 0) {
                    for (String item : list) {
                        if (Integer.parseInt(item) == userId) {
                            throw new OperateException("不允许填写自己任一下级的邀请码");
                        }
                    }
                }
            }

            // 上级
            firstLeaderId = firstLeader.getId();
            // 上上级
            secondLeaderId = firstLeader.getFirstLeader();
            // 上上上级
            thirdLeaderId = firstLeader.getSecondLeader();

            // 上级关系链
            String firstLeaderRelation = "";
            if (StringUtils.isNotBlank(firstLeader.getAncestorRelation())) {
                firstLeaderRelation = firstLeader.getAncestorRelation();
            }

            // 当前用户准备更新的关系链
            String userNowRelation = firstLeaderId + "," + firstLeaderRelation;
            ancestorRelation = StringUtils.removeEnd(userNowRelation, ",").trim();
        }

        User user = userMapper.selectById(userId);
        Assert.notNull(user, "用户信息不存在");

//        if (user.getIsLockLeader() == 1){
//            throw new OperateException("用户上级已注销，无法修改分销关系！");
//        }

        // 判断用户是否注销
        if (user.getIsClose() == 1){
            throw new OperateException("已注销用户无法修改信息！");
        }

        // 用户下级关系链 (更新用户的下级关系链)
        String userSubOldRelation = String.valueOf(user.getId());
        if (StringUtils.isNotBlank(user.getAncestorRelation())) {
            userSubOldRelation = user.getId() + user.getAncestorRelation();
        }

        // 更新用户的关系链
        user.setFirstLeader(firstLeaderId);
        user.setSecondLeader(secondLeaderId);
        user.setThirdLeader(thirdLeaderId);
        user.setAncestorRelation(ancestorRelation);
        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(user);

        // 更新当前用户直属下级关系链
        User firstSubUser = new User();
        firstSubUser.setSecondLeader(firstLeaderId);
        firstSubUser.setThirdLeader(secondLeaderId);
        firstSubUser.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.update(firstSubUser, new QueryWrapper<User>().eq("first_leader", userId));

        // 更新当前用户下下级关系链
        User secondSubUser = new User();
        secondSubUser.setThirdLeader(firstLeaderId);
        secondSubUser.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.update(secondSubUser, new QueryWrapper<User>().eq("second_leader", userId));

        // 更新当前用户所有后台关系链
        List<User> allSubUser = userMapper.selectList(new QueryWrapper<User>()
                .apply(userId != null, "FIND_IN_SET('" + userId + "', ancestor_relation)"));

        // 当前用户下级准备更新的关系链
        String userSubNewRelation = StringUtils.removeEnd(userId + "," + ancestorRelation, ",").trim();
        for (User subUser : allSubUser) {
            String newRelation = subUser.getAncestorRelation().replaceAll(userSubOldRelation, userSubNewRelation);
            subUser.setAncestorRelation(newRelation.trim());
            subUser.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(subUser);
        }
    }

    /**
     * 更新分销等级
     *
     * @author mjf
     * @param userId Integer
     */
    @Override
    public void updateDistributionLevel(Integer userId) {
        // 分销功能关闭不升级
        DistributionConfigVo config = iDistributionConfigService.detail();
        if (config.getOpen().equals(0)) {
            return;
        }

        // 分销信息
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("is_delete", 0)
                .eq("is_distribution", 1)
                .eq("user_id", userId)
                .last("limit 1"));
        if (ObjectUtils.isEmpty(distribution)) {
            return;
        }

        // 判断用户是否注销
        if (distribution.getIsClose() == 1){
            throw new OperateException("已注销分销商无法修改信息！");
        }

        // 分销等级
        DistributionLevel userLevel = distributionLevelMapper.selectById(distribution.getLevelId());
        if (ObjectUtils.isEmpty(userLevel)) {
            return;
        }

        // 非默认等级
        List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>()
                .eq("is_delete", 0)
                .eq("is_default", 0)
                .orderByDesc("weights"));
        if (levelList.size() <= 0) {
            return;
        }

        for (DistributionLevel level : levelList) {
            if (this.__isMeetConditions(userId, level) && level.getWeights() > userLevel.getWeights()) {
                // 满足升级条件且是升更高的等级
                Distribution updateData = new Distribution();
                updateData.setLevelId(level.getId());
                updateData.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionMapper.update(updateData, new QueryWrapper<Distribution>()
                        .eq("user_id", userId));
            }
        }
    }

    /**
     * 初始化默认分销数据
     *
     * @author mjf
     */
    @Override
    public void updateData() {
        // 默认等级id
        Integer levelId = 0;
        // 默认等级
        DistributionLevel defaultLevel = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                .eq("is_default", 1)
                .last("limit 1"));

        if (ObjectUtils.isEmpty(defaultLevel)) {
            DistributionLevel level = new DistributionLevel();
            level.setName("默认等级");
            level.setWeights(1);
            level.setIsDefault(1);
            level.setRemark("默认等级");
            // 升级关系，1-OR关系 2-AND关系
            level.setUpdateType(DistributionEnum.LEVEL_UPDATE_TYPE_OR.getCode());
            level.setCreateTime(System.currentTimeMillis() / 1000);
            level.setUpdateTime(System.currentTimeMillis() / 1000);
            distributionLevelMapper.insert(level);
            levelId = level.getId();
        } else {
            levelId = defaultLevel.getId();
        }

        // 用户分销信息表,已有分销信息用户不处理
        List<Distribution> distributionList = distributionMapper.selectList(null);
        List<Integer> userIds = new LinkedList<>();
        for (Distribution distribution : distributionList) {
            userIds.add(distribution.getUserId());
        }

        if (userIds.size() <= 0) {
            userIds.add(0);
        }

        List<User> userList = userMapper.selectList(new QueryWrapper<User>()
                .notIn("id", userIds));

        if (userList.size() > 0) {
            for (User user : userList) {
                Distribution distribution = new Distribution();
                distribution.setUserId(user.getId());
                distribution.setLevelId(levelId);
                distribution.setIsDistribution(0);
                distribution.setIsFreeze(0);
                distribution.setRemark("");
                distribution.setCreateTime(System.currentTimeMillis() / 1000);
                distribution.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionMapper.insert(distribution);
            }
        }

        // 处理无邀请码的用户
        List<User> noCodeUserList = userMapper.selectList(new QueryWrapper<User>()
                .nested(wq -> wq
                        .eq("code", "").or()
                        .isNull("code")));

        if (noCodeUserList.size() > 0) {
            for (User noCodeUser : noCodeUserList) {
                String code = userMapper.randMakeOrderSn("code");
                noCodeUser.setCode(code);
                userMapper.updateById(noCodeUser);
            }
        }
    }

    /**
     * 分销会员数量
     *
     * @author mjf
     * @param ids List<Integer>
     * @return Long
     */
    private Long __getDistributionFansCount (List<Integer> ids) {
        if (ids.size() <= 0) {
            return (long) 0;
        }
        return distributionMapper.selectCount(new QueryWrapper<Distribution>()
                .eq("is_distribution", 1)
                .in("user_id", ids));
    }

    /**
     * 下级数量
     *
     * @author mjf
     * @param userId Integer
     * @return Long
     */
    private List<Integer> __getFansCount(Integer userId, String type) {

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        switch (type) {
            case "first":
                queryWrapper.eq("first_leader", userId);
                break;
            case "second":
                queryWrapper.eq("second_leader", userId);
                break;
            default:
                queryWrapper.nested(wq->wq
                        .eq("first_leader", userId).or()
                        .eq("second_leader", userId));
        }

        List<User> userList = userMapper.selectList(queryWrapper);

        List<Integer> ids = new LinkedList<>();
        for (User user : userList) {
            ids.add(user.getId());
        }

        return ids;
    }

    /**
     * 已入账佣金
     *
     * @author mjf
     * @param userId Integer
     * @return BigDecimal
     */
    private BigDecimal __getReceivedEarnings(Integer userId) {
        // 已入账佣金
        return distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("user_id", userId)
                .eq("status", DistributionEnum.ORDER_RETURNED.getCode()));
    }

    /**
     * 待结算佣金
     *
     * @author mjf
     * @param userId Integer
     * @return BigDecimal
     */
    private BigDecimal __getWaitEarnings(Integer userId) {
        // 待结算佣金
        return distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("user_id", userId)
                .eq("status", DistributionEnum.ORDER_UN_RETURNED.getCode()));
    }

    /**
     * 是否满足条件
     *
     * @author mjf
     * @param userId Integer
     * @param level DistributionLevel
     * @return boolean
     */
    private boolean __isMeetConditions(Integer userId, DistributionLevel level) {
        if (level.getUpdateType().equals(DistributionEnum.LEVEL_UPDATE_TYPE_OR.getCode())) {
            return this.__singleConsumptionAmountFlag(userId, level)
                    || this.__cumulativeConsumptionAmountFlag(userId, level)
                    || this.__cumulativeConsumptionTimesFlag(userId, level)
                    || this.__returnedCommissionFlag(userId, level);
        }

        if (level.getUpdateType().equals(DistributionEnum.LEVEL_UPDATE_TYPE_AND.getCode())) {
            return this.__singleConsumptionAmountFlag(userId, level)
                    && this.__cumulativeConsumptionAmountFlag(userId, level)
                    && this.__cumulativeConsumptionTimesFlag(userId, level)
                    && this.__returnedCommissionFlag(userId, level);
        }
        return false;
    }

    /**
     * 是否满足单笔消费金额条件
     *
     * @author mjf
     * @param userId Integer
     * @param level DistributionLevel
     * @return boolean
     */
    private boolean __singleConsumptionAmountFlag(Integer userId, DistributionLevel level) {
        DistributionLevelUpdate condition = distributionLevelUpdateMapper.selectOne(new QueryWrapper<DistributionLevelUpdate>()
                .eq("level_id", level.getId())
                .eq("scene", DistributionEnum.LEVEL_UPDATE_SINGLE_AMOUNT.getCode())
                .last("limit 1"));

        if (ObjectUtils.isEmpty(condition)) {
            // 等级条件为满足任一条件(updateType=1)  返回false (满足已设置的任一条件时才升级,未设置的条件归为未满足,返回false)
            // 等级条件为满足全部条件(updateType=2)  返回true  (满足已设置的所有条件时才升级,未设置的条件归为已满足,返回true)
            return level.getUpdateType() == DistributionEnum.LEVEL_UPDATE_TYPE_AND.getCode();
        }

        // 最新支付的订单
        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("pay_is", 1)
                .orderByDesc("id")
                .last("limit 1"));

        if (ObjectUtils.isEmpty(order)) {
            return false;
        }

        return order.getPayMoney().compareTo(condition.getValue()) >= 0;
    }

    /**
     * 判断是否满足累计消费金额条件
     *
     * @author mjf
     * @param userId Integer
     * @param level DistributionLevel
     * @return boolean
     */
    private boolean __cumulativeConsumptionAmountFlag(Integer userId, DistributionLevel level) {
        DistributionLevelUpdate condition = distributionLevelUpdateMapper.selectOne(new QueryWrapper<DistributionLevelUpdate>()
                .eq("level_id", level.getId())
                .eq("scene", DistributionEnum.LEVEL_UPDATE_CUMULATIVE_AMOUNT.getCode())
                .last("limit 1"));

        if (ObjectUtils.isEmpty(condition)) {
            // 等级条件为满足任一条件(updateType=1)  返回false (满足已设置的任一条件时才升级,未设置的条件归为未满足,返回false)
            // 等级条件为满足全部条件(updateType=2)  返回true  (满足已设置的所有条件时才升级,未设置的条件归为已满足,返回true)
            return level.getUpdateType() == DistributionEnum.LEVEL_UPDATE_TYPE_AND.getCode();
        }

        BigDecimal orderAmount = orderMapper.sum("pay_money", new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("pay_is", 1));

        return orderAmount.compareTo(condition.getValue()) >= 0;
    }

    /**
     * 判断是否满足累计消费次数条件
     *
     * @author mjf
     * @param userId Integer
     * @param level DistributionLevel
     * @return boolean
     */
    private boolean __cumulativeConsumptionTimesFlag(Integer userId, DistributionLevel level) {
        DistributionLevelUpdate condition = distributionLevelUpdateMapper.selectOne(new QueryWrapper<DistributionLevelUpdate>()
                .eq("level_id", level.getId())
                .eq("scene", DistributionEnum.LEVEL_UPDATE_CUMULATIVE_TIME.getCode())
                .last("limit 1"));

        if (ObjectUtils.isEmpty(condition)) {
            // 等级条件为满足任一条件(updateType=1)  返回false (满足已设置的任一条件时才升级,未设置的条件归为未满足,返回false)
            // 等级条件为满足全部条件(updateType=2)  返回true  (满足已设置的所有条件时才升级,未设置的条件归为已满足,返回true)
            return level.getUpdateType() == DistributionEnum.LEVEL_UPDATE_TYPE_AND.getCode();
        }

        Long orderCount = orderMapper.selectCount(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("pay_is", 1));

        return new BigDecimal(orderCount).compareTo(condition.getValue()) >= 0;
    }

    /**
     * 判断是否消费已返佣金条件
     *
     * @author mjf
     * @param userId Integer
     * @param level DistributionLevel
     * @return boolean
     */
    private boolean __returnedCommissionFlag(Integer userId, DistributionLevel level) {
        DistributionLevelUpdate condition = distributionLevelUpdateMapper.selectOne(new QueryWrapper<DistributionLevelUpdate>()
                .eq("level_id", level.getId())
                .eq("scene", DistributionEnum.LEVEL_UPDATE_SETTLE_AMOUNT.getCode())
                .last("limit 1"));

        if (ObjectUtils.isEmpty(condition)) {
            // 等级条件为满足任一条件(updateType=1)  返回false (满足已设置的任一条件时才升级,未设置的条件归为未满足,返回false)
            // 等级条件为满足全部条件(updateType=2)  返回true  (满足已设置的所有条件时才升级,未设置的条件归为已满足,返回true)
            return level.getUpdateType() == DistributionEnum.LEVEL_UPDATE_TYPE_AND.getCode();
        }

        BigDecimal earnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("user_id", userId)
                .eq("status", DistributionEnum.ORDER_RETURNED.getCode()));

        return earnings.compareTo(condition.getValue()) >= 0;
    }

}
