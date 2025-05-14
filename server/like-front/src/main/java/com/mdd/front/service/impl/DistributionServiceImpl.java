package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.DevRegion;
import com.mdd.common.entity.distribution.*;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.enums.ErrorEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.DevRegionMapper;
import com.mdd.common.mapper.distribution.*;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import com.mdd.front.service.IDistributionService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.distribution.DistributionApplyValidate;
import com.mdd.front.validate.distribution.DistributionFansSearchValidate;
import com.mdd.front.validate.distribution.DistributionOrderSearchValidate;
import com.mdd.front.vo.distribution.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 分销服务实现类
 */
@Service
public class DistributionServiceImpl implements IDistributionService {

    @Resource
    UserMapper userMapper;

    @Resource
    DevRegionMapper devRegionMapper;

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    DistributionApplyMapper distributionApplyMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;

    @Resource
    DistributionGoodsMapper distributionGoodsMapper;

    @Resource
    DistributionConfigMapper distributionConfigMapper;

    @Resource
    DistributionLevelMapper distributionLevelMapper;

    @Resource
    DistributionLevelUpdateMapper distributionLevelUpdateMapper;

    /**
     * 分销配置
     *
     * @author mjf
     * @return DistributionConfigVo
     */
    @Override
    public DistributionConfigVo config() {
        QueryWrapper<DistributionConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        List<DistributionConfig> list = distributionConfigMapper.selectList(queryWrapper);

        Map<String, String> map = new LinkedHashMap<>();
        for (DistributionConfig config : list) {
            map.put(config.getName(), StringUtils.isNotNull(config.getValue()) ? config.getValue().trim() : null);
        }

        DistributionConfigVo vo = new DistributionConfigVo();
        vo.setOpen(Integer.parseInt(map.getOrDefault("open", "0")));
        vo.setLevel(Integer.parseInt(map.getOrDefault("level", "1")));
        vo.setIsSelfRebate(Integer.parseInt(map.getOrDefault("isSelfRebate", "0")));
        vo.setIsEarningsShow(Integer.parseInt(map.getOrDefault("isEarningsShow", "0")));
        vo.setIsEarningsScope(Integer.parseInt(map.getOrDefault("isEarningsScope", "0")));
        vo.setOpenCondition(Integer.parseInt(map.getOrDefault("openCondition", "0")));
        vo.setProtocolShow(Integer.parseInt(map.getOrDefault("protocolShow", "0")));
        vo.setProtocolContent(map.getOrDefault("protocolContent", ""));
        vo.setApplyImage(UrlUtils.toAbsoluteUrl(map.getOrDefault("applyImage", "")));
        vo.setEarningsCalMethod(Integer.parseInt(map.getOrDefault("earningsCalMethod", "1")));
        vo.setSettlementTiming(Integer.parseInt(map.getOrDefault("settlementTiming", "1")));
        vo.setSettlementTime(map.getOrDefault("settlementTime", "0"));
        return vo;
    }

    /**
     * 绑定上下级关系
     *
     * @author mjf
     * @param code String
     * @param userId Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bind(Integer userId, String code) {
        // 校验邀请码
        checkAbleBind(userId, code);

        // 绑定上级,更新相关关系链
        bindLeader(userId, code);
    }

    /**
     * 申请分销
     *
     * @author mjf
     * @param userId Integer
     * @param applyValidate DistributionApplyValidate
     */
    @Override
    public void apply(Integer userId, DistributionApplyValidate applyValidate) {
        // 分销是否开启
        DistributionConfigVo config = config();
        if (config.getOpen().equals(0)) {
            throw new OperateException("分销功能已关闭");
        }
        if (!config.getOpenCondition().equals(2)) {
            throw new OperateException("暂不可申请");
        }

        // 是否已是分销用户
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_Id", userId)
                .last("limit 1"));
        if (distribution != null && distribution.getIsDistribution() > 0) {
            throw new OperateException("您已是分销会员，无需申请");
        }

        // 是否已有审核中的申请记录
        DistributionApply distributionApply = distributionApplyMapper.selectOne(new QueryWrapper<DistributionApply>()
                .eq("user_Id", userId)
                .last("limit 1"));
        if (distributionApply != null && distributionApply.getStatus().equals(DistributionEnum.APPLY_STATUS_ING.getCode())) {
            throw new OperateException("审核中,请勿重复提交申请");
        }

        // 生成申请记录
        DistributionApply applyData = new DistributionApply();
        applyData.setUserId(userId);
        applyData.setRealName(applyValidate.getRealName());
        applyData.setMobile(applyValidate.getMobile());
        applyData.setProvince(applyValidate.getProvince());
        applyData.setCity(applyValidate.getCity());
        applyData.setDistrict(applyValidate.getDistrict());
        applyData.setReason(applyValidate.getReason());
        applyData.setStatus(DistributionEnum.APPLY_STATUS_ING.getCode());
        applyData.setCreateTime(System.currentTimeMillis() / 1000);
        applyData.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionApplyMapper.insert(applyData);
    }

    /**
     * 申请详情
     *
     * @author mjf
     * @param userId Integer
     * @return DistributionApplyDetailVo
     */
    @Override
    public DistributionApplyDetailVo applyDetail(Integer userId) {
        DistributionApply distributionApply = distributionApplyMapper.selectOne(new QueryWrapper<DistributionApply>()
                .eq("user_Id", userId)
                .orderByDesc("id")
                .last("limit 1"));

        if (distributionApply == null) {
            return null;
        }

        DistributionApplyDetailVo vo = new DistributionApplyDetailVo();
        BeanUtils.copyProperties(distributionApply, vo);
        vo.setStatusMsg(DistributionEnum.getApplyStatusMsg(distributionApply.getStatus()));
        vo.setCreateTime(TimeUtils.timestampToDate(distributionApply.getCreateTime()));

        DevRegion province = devRegionMapper.selectById(distributionApply.getProvince());
        DevRegion city = devRegionMapper.selectById(distributionApply.getCity());
        DevRegion district = devRegionMapper.selectById(distributionApply.getDistrict());

        vo.setProvinceMsg(province != null ? province.getName() : "");
        vo.setCityMsg(city != null ? city.getName() : "");
        vo.setDistrictMsg(district != null ? district.getName() : "");
        return vo;
    }

    /**
     * 新增用户分销信息
     *
     * @author mjf
     * @param userId Integer
     */
    @Override
    public void addDistributionData(Integer userId) {
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

        // 新增分销信息表
        Distribution distributionData = new Distribution();
        distributionData.setUserId(userId);
        distributionData.setLevelId(levelId);
        distributionData.setIsDistribution(0);

        // 分销会员开通方式
        DistributionConfigVo config = config();
        if (config.getOpen().equals(1) && config.getOpenCondition().equals(1)) {
            distributionData.setIsDistribution(1);
            distributionData.setDistributionTime(System.currentTimeMillis() / 1000);
        }

        distributionData.setIsFreeze(0);
        distributionData.setRemark("");
        distributionData.setCreateTime(System.currentTimeMillis() / 1000);
        distributionData.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionMapper.insert(distributionData);
    }

    /**
     * 我的粉丝
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionFansSearchValidate
     * @param userId Integer
     * @return PageResult<DistributionFansVo>
     */
    @Override
    public PageResult<DistributionFansVo> fans(PageValidate pageValidate, DistributionFansSearchValidate searchValidate, Integer userId) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        String keyword = searchValidate.getKeyword();
        String fansType = searchValidate.getType();
        String sortFans = searchValidate.getSortFans();
        String sortAmount = searchValidate.getSortAmount();
        String sortOrder = searchValidate.getSortOrder();

        String fansCount = "(SELECT COUNT(id) FROM ?_user WHERE first_leader = t.id OR second_leader = t.id) as fans".replace("?_", GlobalConfig.tablePrefix);

        MPJQueryWrapper<User> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.leftJoin("?_order O ON t.id=O.user_id and O.pay_is = 1".replace("?_", GlobalConfig.tablePrefix));
        mpjQueryWrapper.select("t.id, t.avatar, t.sn, t.nickname, t.mobile, t.create_time, COUNT(O.id) as order_num, IFNULL(SUM(O.pay_money), 0) as order_amount");
        mpjQueryWrapper.select(fansCount);

        // 粉丝信息
        if (StringUtils.isNotBlank(keyword)) {
            mpjQueryWrapper.nested(wq->wq
                    .like("nickname", keyword).or()
                    .like("sn", keyword).or()
                    .like("mobile", keyword));
        }

        // 粉丝类型
        fansType = StringUtils.isNotBlank(fansType) ? fansType : "all";
        switch (fansType) {
            case "first":
                mpjQueryWrapper.eq("first_leader", userId);
                break;
            case "second":
                mpjQueryWrapper.eq("second_leader", userId);
                break;
            default:
                mpjQueryWrapper.nested(wq -> wq
                        .eq("first_leader", userId).or()
                        .eq("second_leader", userId));
        }

        // 排序
        if (StringUtils.isNotBlank(sortFans)) {
            if (sortFans.equals("asc")) {
                mpjQueryWrapper.orderByAsc("fans");
            } else {
                mpjQueryWrapper.orderByDesc("fans");
            }
        }

        if (StringUtils.isNotBlank(sortAmount)) {
            if (sortFans.equals("asc")) {
                mpjQueryWrapper.orderByAsc("order_amount");
            } else {
                mpjQueryWrapper.orderByDesc("order_amount");
            }
        }

        if (StringUtils.isNotBlank(sortOrder)) {
            if (sortFans.equals("asc")) {
                mpjQueryWrapper.orderByAsc("order_num");
            } else {
                mpjQueryWrapper.orderByDesc("order_num");
            }
        }

        mpjQueryWrapper.orderByDesc("t.id");
        mpjQueryWrapper.groupBy("t.id");

        IPage<DistributionFansVo> iPage = userMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionFansVo.class,
                mpjQueryWrapper
        );

        for (DistributionFansVo vo : iPage.getRecords()) {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime(), "yyyy-MM-dd"));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 分销订单
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionOrderSearchValidate
     * @param userId Integer
     * @return PageResult<DistributionOrderVo>
     */
    @Override
    public PageResult<DistributionOrderVo> order(PageValidate pageValidate, DistributionOrderSearchValidate searchValidate, Integer userId) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        String keyword = searchValidate.getKeyword();
        int status = searchValidate.getStatus() == null ? 0 : searchValidate.getStatus();

        MPJQueryWrapper<DistributionOrder> mpjQueryWrapper = new MPJQueryWrapper<DistributionOrder>()
                .leftJoin("?_order_goods OG ON t.order_goods_id=OG.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_order O ON t.order_id=O.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_user U ON U.id=O.user_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.user_id", userId)
                .select("t.id,O.order_sn,t.earnings,t.status,U.nickname,U.avatar," +
                        "OG.goods_name,OG.goods_num,OG.goods_price,OG.goods_image,OG.pay_money,O.create_time");

        // 关键词信息
        if (StringUtils.isNotBlank(keyword)) {
            mpjQueryWrapper.nested(wq -> wq
                    .like("OG.goods_name", keyword).or()
                    .like("O.order_sn", keyword).or()
                    .like("U.nickname", keyword));
        }

        if (status > 0) {
            mpjQueryWrapper.eq("t.status", status);
        }
        mpjQueryWrapper.orderByDesc("t.id");
        mpjQueryWrapper.groupBy("t.id");

        IPage<DistributionOrderVo> iPage = distributionOrderMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionOrderVo.class,
                mpjQueryWrapper
        );

        for (DistributionOrderVo vo : iPage.getRecords()) {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setStatusMsg(DistributionEnum.getOrderStatusMsg(vo.getStatus()));
            vo.setGoodsImage(UrlUtils.toAbsoluteUrl(vo.getGoodsImage()));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 分销主页信息
     *
     * @author mjf
     * @param userId Integer
     * @return DistributionIndexVo
     */
    @Override
    public DistributionIndexVo index(Integer userId) {
        User user = userMapper.selectById(userId);
        Assert.notNull(user, "用户信息不存在");

        // 分销配置
        DistributionConfigVo config = config();

        DistributionIndexVo vo = new DistributionIndexVo();
        vo.setId(user.getId());
        vo.setCode(user.getCode());
        vo.setEarnings(user.getEarnings());

        // 是否为分销会员
        int isDistribution = 1;
        // 是否显示分销申请页
        int isShowApplyPage = 0;
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", userId)
                .last("limit 1"));

        if (distribution == null || distribution.getIsDistribution().equals(0)) {
            isDistribution = 0;
        } else {
            if (distribution.getLevelId() != null && distribution.getLevelId() > 0) {
                DistributionLevel distributionLevel = distributionLevelMapper.selectById(distribution.getLevelId());
                vo.setLevelId(distribution.getLevelId());
                vo.setLevelName(distributionLevel.getName());
                vo.setLevelIcon(UrlUtils.toAbsoluteUrl(distributionLevel.getIcon()));
                vo.setLevelImage(UrlUtils.toAbsoluteUrl(distributionLevel.getImage()));
            }
        }

        // 分销会员开通方式
        if (config.getOpen().equals(1) && config.getOpenCondition().equals(2)) {
            isShowApplyPage = 1;
        }

        vo.setIsDistribution(isDistribution);
        vo.setIsShowApplyPage(isShowApplyPage);

        // 上级信息
        if (user.getFirstLeader() != null && user.getFirstLeader() > 0) {
            User firstLeader = userMapper.selectById(user.getFirstLeader());
            vo.setFirstLeader(firstLeader.getNickname());
        }

        // 粉丝数量
        Long totalFans = userMapper.selectCount(new QueryWrapper<User>()
                .nested(wq -> wq
                        .eq("first_leader", userId).or()
                        .eq("second_leader", userId)));

        // 今日预估
        BigDecimal todayEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("user_id", userId)
                .ge("create_time", TimeUtils.today().get(0))
                .le("create_time", TimeUtils.today().get(1))
                .ne("status", DistributionEnum.ORDER_EXPIRED.getCode()));

        // 本月预估
        BigDecimal monthEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("user_id", userId)
                .ge("create_time", TimeUtils.month().get(0))
                .le("create_time", TimeUtils.month().get(1))
                .ne("status", DistributionEnum.ORDER_EXPIRED.getCode()));

        // 累计佣金
        BigDecimal totalEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                .eq("user_id", userId)
                .eq("status", DistributionEnum.ORDER_RETURNED.getCode()));

        // 订单数量
        Long totalOrder = distributionOrderMapper.selectCount(new QueryWrapper<DistributionOrder>().eq("user_id", userId));

        vo.setTodayEarnings(todayEarnings);
        vo.setMonthEarnings(monthEarnings);
        vo.setTotalEarnings(totalEarnings);
        vo.setTotalFans(totalFans);
        vo.setTotalOrder(totalOrder);
        vo.setApplyImage(config.getApplyImage());
        vo.setIsShowProtocol(config.getProtocolShow());
        return vo;
    }

    /**
     * 校验能否绑定上下级关系
     *
     * @author mjf
     * @param code String
     * @param userId Integer
     */
    @Override
    public void checkAbleBind(Integer userId, String code) {
        // 获取分销配置
        DistributionConfigVo config = config();
        if (config.getOpen().equals(0)) {
            throw new OperateException("分销功能已关闭，无法绑定上下级", ErrorEnum.BIND_RELATION_ERROR.getCode());
        }

        // 准备绑定的上级
        User firstLeader = userMapper.selectOne(new QueryWrapper<User>()
                .eq("code", code)
                .eq("is_delete", 0)
                .last("limit 1"));

        if (firstLeader == null) {
            throw new OperateException("无效的邀请码", ErrorEnum.BIND_RELATION_ERROR.getCode());
        }

        // 上级需为分销会员才可以绑定下级
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", firstLeader.getId())
                .eq("is_delete", 0)
                .last("limit 1"));

        if (ObjectUtils.isEmpty(distribution) || distribution.getIsDistribution().equals(0)) {
            throw new OperateException("邀请码所属人非分销商无法绑定", ErrorEnum.BIND_RELATION_ERROR.getCode());
        }

        // 当前用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new OperateException("用户信息不存在", ErrorEnum.BIND_RELATION_ERROR.getCode());
        }

        // 是否已有上级
        if (user.getFirstLeader() > 0) {
            throw new OperateException("已有上级", ErrorEnum.BIND_RELATION_ERROR.getCode());
        }

        //上级不能是自己
        if (firstLeader.getId().equals(userId)) {
            throw new OperateException("上级不能是自己", ErrorEnum.BIND_RELATION_ERROR.getCode());
        }

        // 不可填写自己任一下级的邀请码
        if (StringUtils.isNotBlank(firstLeader.getAncestorRelation())) {
            String[] list = firstLeader.getAncestorRelation().split(",");
            if (list.length > 0) {
                for (String item : list) {
                    if (Integer.parseInt(item) == userId) {
                        throw new OperateException("不允许填写自己任一下级的邀请码", ErrorEnum.BIND_RELATION_ERROR.getCode());
                    }
                }
            }
        }
    }

    /**
     * 校验能否绑定上级
     *
     * @author mjf
     * @param userId Integer
     * @param inviteCode String
     * @return boolean
     */
    @Override
    public boolean checkAbleBindLeader(Integer userId, String inviteCode) {
        try {
            checkAbleBind(userId, inviteCode);
            return true;
        } catch (OperateException e) {
            return false;
        }
    }

    /**
     * 绑定上级
     *
     * @author mjf
     * @param code String
     * @param userId Integer
     */
    @Override
    public void bindLeader(Integer userId, String code) {
        // 建立绑定关系
        User firstLeader = userMapper.selectOne(new QueryWrapper<User>()
                .eq("code", code)
                .last("limit 1"));

        // 上级id
        Integer firstLeaderId = firstLeader.getId();
        // 上上级id
        Integer secondLeaderId = firstLeader.getFirstLeader();
        // 上上上级id
        Integer thirdLeaderId = firstLeader.getSecondLeader();

        // 上级关系链
        String firstLeaderRelation = "";
        if (StringUtils.isNotBlank(firstLeader.getAncestorRelation())) {
            firstLeaderRelation = firstLeader.getAncestorRelation();
        }

        // 当前用户准备更新的关系链
        String userNowRelation = firstLeaderId + "," + firstLeaderRelation;
        userNowRelation = StringUtils.removeEnd(userNowRelation, ",").trim();

        // 当前用户
        User user = userMapper.selectById(userId);
        // 当前用户下级关系链 (更新当前用户的下级关系链)
        String userSubOldRelation = String.valueOf(user.getId());
        if (StringUtils.isNotBlank(user.getAncestorRelation())) {
            userSubOldRelation = user.getId() + user.getAncestorRelation();
        }

        // 更新当前用户的关系链
        user.setFirstLeader(firstLeaderId);
        user.setSecondLeader(secondLeaderId);
        user.setThirdLeader(thirdLeaderId);
        user.setAncestorRelation(userNowRelation);
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
        String userSubNewRelation = userId + "," + userNowRelation;
        for (User subUser : allSubUser) {
            String newRelation = subUser.getAncestorRelation().replaceAll(userSubOldRelation, userSubNewRelation);
            subUser.setAncestorRelation(newRelation.trim());
            subUser.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(subUser);
        }
    }

    /**
     * 绑定邀请人
     *
     * @author mjf
     * @param userId Integer
     * @param inviteCode String
     */
    @Override
    public void bindInviter(Integer userId, String inviteCode) {
        User user = userMapper.selectById(userId);

        if (ObjectUtils.isEmpty(user)) {
            return;
        }

        User inviter = userMapper.selectOne(new QueryWrapper<User>()
                .eq("code", inviteCode));

        if (ObjectUtils.isEmpty(inviter)) {
            return;
        }

        user.setInviterId(inviter.getId());
        userMapper.updateById(user);
    }

    /**
     * 下单时生成分销订单
     *
     * @author mjf
     * @param orderId Integer
     */
    @Override
    public void addDistributionOrder(Integer orderId) {
        // 分销已关闭，不再生成分销订单
        DistributionConfigVo config = config();
        if (config.getOpen().equals(0)) {
            return;
        }

        // 订单信息
        Order order = orderMapper.selectById(orderId);
        if (ObjectUtils.isEmpty(order)) {
            return;
        }

        // 订单商品信息
        List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>()
                .eq("order_id", orderId));

        // 下单用户信息
        DistributionOrderUserVo user = __getDistributionUser(order.getUserId());
        if (ObjectUtils.isEmpty(user)) {
            return;
        }
        // 用户上一级
        DistributionOrderUserVo firstLeader = __getDistributionUser(user.getFirstLeader());
        // 用户上二级
        DistributionOrderUserVo secondLeader = __getDistributionUser(user.getSecondLeader());

        for (OrderGoods orderGoods : orderGoodsList) {
            // 商品是否参与分销
            DistributionGoods distributionGoods = distributionGoodsMapper.selectOne(new QueryWrapper<DistributionGoods>()
                    .eq("is_delete", 0)
                    .eq("goods_id", orderGoods.getGoodsId())
                    .last("limit 1"));

            if (ObjectUtils.isEmpty(distributionGoods) || distributionGoods.getIsDistribution().equals(0)) {
                continue;
            }

            // 一级分佣
            if (config.getLevel().equals(1)) {
                this.__distributionUserCommission(DistributionEnum.ORDER_LEVEL_FIRST.getCode(), firstLeader, orderGoods, distributionGoods);
            }

            // 二级分佣
            if (config.getLevel().equals(2)) {
                this.__distributionUserCommission(DistributionEnum.ORDER_LEVEL_FIRST.getCode(), firstLeader, orderGoods, distributionGoods);
                this.__distributionUserCommission(DistributionEnum.ORDER_LEVEL_SECOND.getCode(), secondLeader, orderGoods, distributionGoods);
            }

            // 自购返佣
            if (config.getIsSelfRebate().equals(1)) {
                this.__distributionUserCommission(DistributionEnum.ORDER_LEVEL_SELF.getCode(), user, orderGoods, distributionGoods);
            }
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
        if (config().getOpen().equals(0)) {
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
     * 分销等级
     *
     * @author mjf
     * @param userId Integer
     * @return List<DistributionLevelVo>
     */
    @Override
    public List<DistributionLevelVo> level(Integer userId) {

        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", userId)
                .eq("is_delete", 0));

        List<DistributionLevel> levelList = distributionLevelMapper.selectList(new QueryWrapper<DistributionLevel>()
                .eq("is_delete", 0)
                .orderByAsc("weights"));

        List<DistributionLevelVo> list = new ArrayList<>();

        for (DistributionLevel level : levelList) {
            // 当前等级升级条件
            List<DistributionLevelUpdate> levelUpdateList = distributionLevelUpdateMapper.selectList(new QueryWrapper<DistributionLevelUpdate>()
                    .eq("level_id", level.getId())
                    .eq("is_delete", 0));

            List<DistributionLevelVo.condition> conditionList = new ArrayList<>();

            for (DistributionLevelUpdate levelUpdate : levelUpdateList) {
                switch (levelUpdate.getScene()) {
                    case 1:
                        // 单笔消费金额
                        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                                .eq("user_id", userId)
                                .eq("is_delete", 0)
                                .eq("pay_is", 1)
                                .orderByDesc("pay_money")
                                .last("limit 1"));

                        DistributionLevelVo.condition conditionSingle = new DistributionLevelVo.condition();
                        conditionSingle.setLevelId(levelUpdate.getLevelId());
                        conditionSingle.setIsFinish(0);
                        conditionSingle.setConditionMsg("单笔消费金额满" + levelUpdate.getValue() + "元");

                        BigDecimal orderMoney = BigDecimal.ZERO;
                        if (!ObjectUtils.isEmpty(order)) {
                            orderMoney = order.getPayMoney();
                        }

                        conditionSingle.setProgressMsg("完成" + orderMoney + "/" + levelUpdate.getValue());
                        // 比较订单金额与等级条件
                        if (orderMoney.compareTo(levelUpdate.getValue()) >= 0) {
                            conditionSingle.setIsFinish(1);
                        }
                        conditionList.add(conditionSingle);
                        break;
                    case 2:
                        // 累计消费金额
                        BigDecimal totalAmount = orderMapper.sum("pay_money", new QueryWrapper<Order>()
                                .eq("user_id", userId)
                                .eq("is_delete", 0)
                                .eq("pay_is", 1));

                        DistributionLevelVo.condition conditionTotalAmount = new DistributionLevelVo.condition();
                        conditionTotalAmount.setLevelId(levelUpdate.getLevelId());
                        conditionTotalAmount.setIsFinish(0);
                        conditionTotalAmount.setConditionMsg("累计消费金额满" + levelUpdate.getValue() + "元");
                        conditionTotalAmount.setProgressMsg("完成" + totalAmount + "/" + levelUpdate.getValue());
                        // 累计金额比较等级条件
                        if (totalAmount.compareTo(levelUpdate.getValue()) >= 0) {
                            conditionTotalAmount.setIsFinish(1);
                        }
                        conditionList.add(conditionTotalAmount);
                        break;
                    case 3:
                        // 累计消费次数
                        Long totalOrder = orderMapper.selectCount(new QueryWrapper<Order>()
                                .eq("user_id", userId)
                                .eq("is_delete", 0)
                                .eq("pay_is", 1));

                        BigDecimal totalCount = new BigDecimal(totalOrder);

                        DistributionLevelVo.condition conditionTotalOrder = new DistributionLevelVo.condition();
                        conditionTotalOrder.setLevelId(levelUpdate.getLevelId());
                        conditionTotalOrder.setIsFinish(0);
                        conditionTotalOrder.setConditionMsg("累计消费次数满" + levelUpdate.getValue().intValue() + "次");
                        conditionTotalOrder.setProgressMsg("完成" + totalCount + "/" + levelUpdate.getValue().intValue());
                        // 累计次数比较等级条件
                        if (totalCount.compareTo(levelUpdate.getValue()) >= 0) {
                            conditionTotalOrder.setIsFinish(1);
                        }
                        conditionList.add(conditionTotalOrder);
                        break;
                    case 4:
                        // 已结算佣金收入
                        BigDecimal totalEarnings = distributionOrderMapper.sum("earnings", new QueryWrapper<DistributionOrder>()
                                .eq("user_id", userId)
                                .eq("status", DistributionEnum.ORDER_RETURNED.getCode()));

                        DistributionLevelVo.condition conditionTotalEarnings = new DistributionLevelVo.condition();
                        conditionTotalEarnings.setLevelId(levelUpdate.getLevelId());
                        conditionTotalEarnings.setIsFinish(0);
                        conditionTotalEarnings.setConditionMsg("累计结算佣金收入" + levelUpdate.getValue() + "元");
                        conditionTotalEarnings.setProgressMsg("完成" + totalEarnings + "/" + levelUpdate.getValue());
                        // 累计佣金收入比较等级条件
                        if (totalEarnings.compareTo(levelUpdate.getValue()) >= 0) {
                            conditionTotalEarnings.setIsFinish(1);
                        }
                        conditionList.add(conditionTotalEarnings);
                        break;
                }
            }

            DistributionLevelVo vo = new DistributionLevelVo();
            vo.setLevelId(level.getId());
            vo.setLevelName(level.getName());
            vo.setSelfRatio(level.getSelfRatio());
            vo.setFirstRatio(level.getFirstRatio());
            vo.setSecondRatio(level.getSecondRatio());
            vo.setIcon(UrlUtils.toAbsoluteUrl(level.getIcon()));
            vo.setImage(UrlUtils.toAbsoluteUrl(level.getImage()));
            vo.setIsNowLevel(distribution.getLevelId().equals(level.getId()) ? 1 : 0);
            vo.setCondition(conditionList);
            vo.setConditionMsg("满足以下全部条件即可升级");
            if (level.getUpdateType().equals(DistributionEnum.LEVEL_UPDATE_TYPE_OR.getCode())) {
                vo.setConditionMsg("满足以下任意条件即可升级");
            }

            list.add(vo);
        }
        return list;
    }

    /**
     * 生成分销订单
     *
     * @author mjf
     * @param levelType Integer
     * @param distributionUser DistributionOrderUserVo
     * @param orderGoods OrderGoods
     * @param distributionGoods DistributionGoods
     */
    private void __distributionUserCommission(Integer levelType, DistributionOrderUserVo distributionUser, OrderGoods orderGoods, DistributionGoods distributionGoods) {
        // 分销用户信息是否存在
        if (ObjectUtils.isEmpty(distributionUser)) {
            return;
        }

        // 需为分销会员 且未被冻结
        if (distributionUser.getIsDistribution().equals(0) || distributionUser.getIsFreeze().equals(1)) {
            return;
        }

        // 佣金比例
        Double ratio = this.__getRatio(distributionGoods.getRule(), orderGoods.getGoodsSkuId(), distributionUser.getLevelId(), levelType);

        // 佣金
        BigDecimal earnings = BigDecimal.ZERO;
        DistributionConfigVo config = config();
        if (config.getEarningsCalMethod().equals(1)) {
            earnings = BigDecimal.valueOf(ratio).multiply(orderGoods.getPayMoney()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // 小于0.01不处理
        if (earnings.compareTo(BigDecimal.valueOf(0.01)) < 0) {
            return;
        }

        String sn = distributionOrderMapper.randMakeOrderSn("sn");
        // 创建分销订单
        DistributionOrder distributionOrder = new DistributionOrder();
        distributionOrder.setSn(sn);
        distributionOrder.setUserId(distributionUser.getId());
        distributionOrder.setLevelId(distributionUser.getLevelId());
        distributionOrder.setLevel(levelType);
        distributionOrder.setRatio(ratio);
        distributionOrder.setOrderId(orderGoods.getOrderId());
        distributionOrder.setOrderGoodsId(orderGoods.getId());
        distributionOrder.setGoodsId(orderGoods.getGoodsId());
        distributionOrder.setSkuId(orderGoods.getGoodsSkuId());
        distributionOrder.setEarnings(earnings);
        distributionOrder.setStatus(DistributionEnum.ORDER_UN_RETURNED.getCode());
        distributionOrder.setCreateTime(System.currentTimeMillis() / 1000);
        distributionOrder.setUpdateTime(System.currentTimeMillis() / 1000);
        distributionOrderMapper.insert(distributionOrder);
    }

    /**
     * 获取分销用户信息
     *
     * @author mjf
     * @param id Integer
     * @return DistributionOrderUserVo
     */
    private DistributionOrderUserVo __getDistributionUser(Integer id) {
        if (ObjectUtils.isEmpty(id) || id <= 0) {
            return null;
        }

        User user = userMapper.selectById(id);

        DistributionOrderUserVo userInfo = new DistributionOrderUserVo();
        userInfo.setId(user.getId());
        userInfo.setFirstLeader(user.getFirstLeader());
        userInfo.setSecondLeader(user.getSecondLeader());

        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", id)
                .eq("is_delete", 0)
                .last("limit 1"));

        if (ObjectUtils.isEmpty(distribution)) {
            return userInfo;
        }

        userInfo.setIsDistribution(distribution.getIsDistribution());
        userInfo.setIsFreeze(distribution.getIsFreeze());
        userInfo.setLevelId(distribution.getLevelId());
        return userInfo;
    }

    /**
     * 获取佣金比例
     *
     * @author mjf
     * @param rule Integer
     * @param skuId Integer
     * @param levelId Integer
     * @param levelType Integer
     * @return Double
     */
    private Double __getRatio(Integer rule, Integer skuId, Integer levelId, Integer levelType) {
        Double ratio = 0.0;
        // 按分销等级对应的比例
        if (rule.equals(DistributionEnum.GOODS_RULE_LEVEL.getCode())) {
            DistributionLevel level = distributionLevelMapper.selectOne(new QueryWrapper<DistributionLevel>()
                    .eq("id", levelId)
                    .eq("is_delete", 0));

            if (ObjectUtils.isEmpty(level)) {
                return ratio;
            }

            if (levelType.equals(DistributionEnum.ORDER_LEVEL_SELF.getCode())) {
                return level.getSelfRatio() != null ? level.getSelfRatio() : ratio;
            }

            if (levelType.equals(DistributionEnum.ORDER_LEVEL_FIRST.getCode())) {
                return level.getFirstRatio() != null ? level.getFirstRatio() : ratio;
            }

            if (levelType.equals(DistributionEnum.ORDER_LEVEL_SECOND.getCode())) {
                return level.getSecondRatio() != null ? level.getSecondRatio() : ratio;
            }
        }

        // 单独设置比例
        if (rule.equals(DistributionEnum.GOODS_RULE_SINGLE.getCode())) {
            DistributionGoods goods = distributionGoodsMapper.selectOne(new QueryWrapper<DistributionGoods>()
                    .eq("sku_id", skuId)
                    .eq("level_id", levelId)
                    .eq("is_delete", 0));

            if (ObjectUtils.isEmpty(goods)) {
                return ratio;
            }

            if (levelType.equals(DistributionEnum.ORDER_LEVEL_SELF.getCode())) {
                return goods.getSelfRatio() != null ? goods.getSelfRatio() : ratio;
            }

            if (levelType.equals(DistributionEnum.ORDER_LEVEL_FIRST.getCode())) {
                return goods.getFirstRatio() != null ? goods.getFirstRatio() : ratio;
            }

            if (levelType.equals(DistributionEnum.ORDER_LEVEL_SECOND.getCode())) {
                return goods.getSecondRatio() != null ? goods.getSecondRatio() : ratio;
            }
        }
        return ratio;
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

    /**
     * 获取省市区
     *
     * @author mjf
     * @param provinceId Integer
     * @param cityId Integer
     * @param districtId Integer
     * @return String
     */
    @Override
    public String getRegion(Integer provinceId, Integer cityId, Integer districtId) {

        List<DevRegion> regionList = devRegionMapper.selectList(new QueryWrapper<DevRegion>()
                .in("id", Arrays.asList(provinceId, cityId, districtId)));

        String provinceName = "";
        String cityName = "";
        String districtName = "";

        for (DevRegion devRegion : regionList) {
            if (1 == devRegion.getLevel()) {
                provinceName = devRegion.getName();
            }
            if (2 == devRegion.getLevel()) {
                cityName = devRegion.getName();
            }
            if (3 == devRegion.getLevel()) {
                districtName = devRegion.getName();
            }
        }
        return provinceName + cityName + districtName;
    }
}
