package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.admin.service.IUserService;
import com.mdd.admin.validate.user.*;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.user.InviterVo;
import com.mdd.admin.vo.user.UserVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.coupon.Coupon;
import com.mdd.common.entity.coupon.CouponList;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.ClientEnum;
import com.mdd.common.enums.CouponEnum;
import com.mdd.common.enums.LogEarningsEnum;
import com.mdd.common.enums.LogMoneyEnum;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.coupon.CouponListMapper;
import com.mdd.common.mapper.coupon.CouponMapper;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.common.mapper.log.LogEarningsMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    UserMapper userMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    LogEarningsMapper logEarningsMapper;

    @Resource
    CouponMapper couponMapper;

    @Resource
    CouponListMapper couponListMapper;

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    OrderMapper orderMapper;

    /**
     * 用户列表
     *
     * @author fzr
     * @param pageValidate (分页参数)
     * @param searchValidate (搜索参数)
     * @return PageResult<UserVo>
     */
    @Override
    public PageResult<UserVo> list(PageValidate pageValidate, UserSearchValidate searchValidate) {
        Integer pageNo   = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.orderByDesc("id");
        queryWrapper.select(User.class, info->
                !info.getColumn().equals("is_delete") &&
                !info.getColumn().equals("delete_time") &&
                !info.getColumn().equals("update_time") &&
                !info.getColumn().equals("password") &&
                !info.getColumn().equals("salt")
        );

        if (StringUtils.isNotNull(searchValidate.getKeyword()) && StringUtils.isNotEmpty(searchValidate.getKeyword())) {
            String keyword = searchValidate.getKeyword();
            queryWrapper.nested(wq->wq
                    .like("sn", keyword).or()
                    .like("nickname", keyword).or()
                    .like("mobile", keyword));
        }

        userMapper.setSearch(queryWrapper, searchValidate, new String[]{
                "=:channel:int",
                "datetime:startTime-endTime@create_time:str"
        });

        // 是否分销会员
        if (searchValidate.getIsDistribution() != null && searchValidate.getIsDistribution() >= 0) {
            List<Distribution> distributionList = distributionMapper.selectList(new QueryWrapper<Distribution>()
                    .eq("is_distribution", 1)
                    .eq("is_delete",0));
            List<Integer> distributionUserIds = new LinkedList<>();
            for (Distribution distribution : distributionList) {
                distributionUserIds.add(distribution.getUserId());
            }
            if (!distributionUserIds.isEmpty()) {
                if (searchValidate.getIsDistribution() == 1) {
                    queryWrapper.in("id", distributionUserIds);
                } else {
                    queryWrapper.notIn("id", distributionUserIds);
                }
            }
        }

        IPage<User> iPage = userMapper.selectPage( new Page<>(pageNo, pageSize), queryWrapper);

        List<UserVo> list = new LinkedList<>();
        for (User user : iPage.getRecords()) {
            UserVo vo = new UserVo();
            BeanUtils.copyProperties(user, vo);
            vo.setSex(user.getSex());
            vo.setChannel(ClientEnum.getMsgByCode(user.getChannel()));
            vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
            vo.setLastLoginTime(TimeUtils.timestampToDate(user.getLastLoginTime()));
            vo.setCreateTime(TimeUtils.timestampToDate(user.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    /**
     * 用户详情
     *
     * @author fzr
     * @param id 主键
     * @return UserVo
     */
    @Override
    public UserVo detail(Integer id) {
        Assert.notNull(
                userMapper.selectOne(new QueryWrapper<User>()
                        .select("id")
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1")
                ), "数据不存在!");


        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select(User.class, info->
                    !info.getColumn().equals("is_delete") &&
                    !info.getColumn().equals("delete_time") &&
                    !info.getColumn().equals("update_time") &&
                    !info.getColumn().equals("password") &&
                    !info.getColumn().equals("salt")
                )
                .eq("id", id)
                .eq("is_delete", 0)
                .last("limit 1"));

        long currTime = System.currentTimeMillis() / 1000;
        long unUserCouponNum = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                .eq("user_id", user.getId())
                .eq("status", CouponEnum.USE_STATUS_NOT.getCode())
                .ge("invalid_time", currTime));

        UserVo vo = new UserVo();
        BeanUtils.copyProperties(user, vo);
        vo.setTotalMoney(user.getMoney().add(user.getEarnings()));
        vo.setSex(user.getSex());
        vo.setUnUseCouponNum(unUserCouponNum);
        vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
        vo.setChannel(ClientEnum.getMsgByCode(user.getChannel()));
        vo.setCreateTime(TimeUtils.timestampToDate(user.getCreateTime()));
        if (user.getLastLoginTime() <= 0) {
            vo.setLastLoginTime("无");
        } else {
            vo.setLastLoginTime(TimeUtils.timestampToDate(user.getLastLoginTime()));
        }

        // 邀请人
        vo.setInviterId(user.getInviterId());
        vo.setInviterName("-");
        User inviteUser = userMapper.selectById(user.getInviterId());
        if (!ObjectUtils.isEmpty(inviteUser)) {
            vo.setInviterName(inviteUser.getNickname());
        }

        // 邀请人数
        Long inviteNum = userMapper.selectCount(new QueryWrapper<User>().eq("inviter_id", user.getId()));
        vo.setInviteNum(inviteNum);

        // 上级
        vo.setFirstLeader(user.getFirstLeader());
        vo.setFirstLeaderName("系统");
        User firstLeader = userMapper.selectById(vo.getFirstLeader());
        if (!ObjectUtils.isEmpty(firstLeader)) {
            vo.setFirstLeaderName(firstLeader.getNickname());
        }
        return vo;
    }

    /**
     * 用户编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    @Override
    public void edit(UserUpdateValidate updateValidate) {
        Integer id = updateValidate.getId();
        String field = updateValidate.getField();
        String value = updateValidate.getValue();

        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", id)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(user, "用户不存在!");

        // 判断用户是否注销
        if (user.getIsClose() == 1){
            throw new OperateException("已注销用户无法修改信息！");
        }

        switch (field) {
            case "username":
                if (!user.getUsername().equals(value)) {
                    User u = userMapper.selectOne(new QueryWrapper<User>()
                            .eq("username", value)
                            .eq("is_delete", 0)
                            .last("limit 1"));

                    if (StringUtils.isNotNull(u) && !u.getId().equals(id)) {
                        throw new OperateException("当前账号已存在!");
                    }
                }
                Assert.isTrue(value.length() <= 32,"账号不能超过32个字符");
                user.setUsername(value);
                break;
            case "realName":
                Assert.isTrue(value.length() <= 32,"真实姓名不能超过32个字符");
                user.setRealName(value);
                break;
            case "sex":
                user.setSex(Integer.parseInt(value));
                break;
            case "mobile":
                if (!Pattern.matches("^[1][3-9][0-9]{9}$", value)) {
                    throw new OperateException("手机号格式不正确!");
                }
                //判断是否被别人绑定了
                User user1 = this.getUserByMobile(value);
                if (StringUtils.isNull(user1) || user1.getId().equals(user.getId())) {
                    user.setMobile(value);
                } else {
                    throw new OperateException("手机号已被其它账号绑定!!");
                }
                break;
            default:
                throw new OperateException("不被支持的字段类型!");
        }

        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(user);
    }

    /**
     * 余额调整
     *
     * @author cjh
     * @param userWalletValidate 余额
     */
    @Override
    @Transactional
    public void adjustWallet(UserWalletValidate userWalletValidate) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", userWalletValidate.getUserId())
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(user,"用户不存在!");
        // 判断用户是否注销
        if (user.getIsClose() == 1){
            throw new OperateException("已注销用户无法调整余额！");
        }
        BigDecimal userMoney = user.getMoney();
        BigDecimal amount = userWalletValidate.getAmount();
        BigDecimal surplusAmount;
        int changeType;

        if(userWalletValidate.getAction().equals(0) ){
            surplusAmount = userMoney.subtract(amount);
            if(surplusAmount.compareTo(BigDecimal.ZERO) < 0){
                throw new OperateException("用户余额仅剩："+ userMoney);
            }
            user.setMoney(surplusAmount);
            userMapper.updateById(user);

            changeType = LogMoneyEnum.UM_DEC_ADMIN.getCode();
            logMoneyMapper.dec(user.getId(), changeType, amount, 0, "", userWalletValidate.getRemark(), null);
        }else{
            surplusAmount = userMoney.add(amount);
            user.setMoney(surplusAmount);
            userMapper.updateById(user);

            changeType = LogMoneyEnum.UM_INC_ADMIN.getCode();
            logMoneyMapper.add(user.getId(), changeType, amount, 0, "", userWalletValidate.getRemark(), null);
        }
    }

    /**
     * 调整用户佣金
     *
     * @author mjf
     * @param earningsValidate UserEarningsValidate
     */
    @Override
    @Transactional
    public void adjustEarnings(UserEarningsValidate earningsValidate) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", earningsValidate.getUserId())
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(user, "用户不存在!");
        // 判断用户是否注销
        if (user.getIsClose() == 1){
            throw new OperateException("已注销用户无法调整佣金！");
        }
        BigDecimal userEarnings = user.getEarnings();
        BigDecimal amount = earningsValidate.getAmount();
        BigDecimal surplusAmount;
        int changeType;

        if (earningsValidate.getAction().equals(0)) {
            surplusAmount = userEarnings.subtract(amount);
            if (surplusAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new OperateException("用户可提现金额仅剩：" + userEarnings);
            }
            user.setEarnings(surplusAmount);
            userMapper.updateById(user);

            changeType = LogEarningsEnum.UE_DEC_ADMIN.getCode();
            logEarningsMapper.dec(user.getId(), changeType, amount, 0, "", earningsValidate.getRemark(), null);

        } else {
            surplusAmount = userEarnings.add(amount);
            user.setEarnings(surplusAmount);
            userMapper.updateById(user);

            changeType = LogEarningsEnum.UE_INC_ADMIN.getCode();
            logEarningsMapper.add(user.getId(), changeType, amount, 0, "", earningsValidate.getRemark(), null);
        }
    }

    /**
     * 发放优惠券
     *
     * @author fzr
     * @param couponIds 优惠券IDs
     * @param userIds 用户IDs
     * @param number 发放数量
     * @param adminId 管理员ID
     */
    @Override
    public void sendCoupon(List<Integer> couponIds, List<Integer> userIds, Integer number, Integer adminId) {
        List<Coupon> coupons = couponMapper.selectList(
                new QueryWrapper<Coupon>()
                    .in("id", couponIds)
                    .eq("get_type", CouponEnum.GET_TYPE_STORE.getCode())
                    .eq("status", CouponEnum.COUPON_STATUS_CONDUCT.getCode()));

        if (coupons.size() <= 0) {
            throw new OperateException("未能检测到可发放的优惠券,请刷新页面!");
        }

        number = StringUtils.isNull(number) ? 1 : number;
        if (number <= 0) {
            throw new OperateException("发放数量不能少于0");
        }

        for (Coupon coupon : coupons) {
            long invalidTime = 0;
            switch (coupon.getUseTimeType()) {
                case 1:
                    invalidTime = coupon.getUseTimeEnd();
                    break;
                case 2:
                    invalidTime = TimeUtils.timestamp() + (coupon.getUseTime() * 86400);
                    break;
                case 3:
                    invalidTime = TimeUtils.daysAfter(1) + (coupon.getUseTime() * 86400);
                    break;
            }

            // 固定数量
            if (coupon.getSendTotalType().equals(CouponEnum.SEND_TOTAL_TYPE_FIXED.getCode())) {
                int totalSendNum = userIds.size() * number;
                if (totalSendNum > coupon.getSendTotal()) {
                    throw new OperateException("优惠券(" + coupon.getName() + ")发放的数量超出库存数量,不能发放");
                }

                // 查询历史发放数量
                Long historyCount = couponListMapper.selectCount(new QueryWrapper<CouponList>()
                        .eq("coupon_id", coupon.getId()));

                if (coupon.getSendTotal() - historyCount < totalSendNum) {
                    throw new OperateException("优惠券(" + coupon.getName() + ")发放的数量超出库存数量,不能发放");
                }
            }

            for (Integer userId : userIds) {
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("is_delete", 0);
                userQueryWrapper.last("limit 1");
                User user = userMapper.selectOne(userQueryWrapper);
                // 判断用户是否注销
                if (user.getIsClose() == 1){
                    throw new OperateException("已注销用户无法发放优惠券！");
                }
                for (int i=1; i<=number; i++) {
                    String couponCode = couponListMapper.randMakeOrderSn("coupon_code");
                    Long currTime = System.currentTimeMillis() / 1000;
                    CouponList couponList = new CouponList();
                    couponList.setChannel(CouponEnum.GET_TYPE_STORE.getCode());
                    couponList.setCouponCode(couponCode);
                    couponList.setIssuerId(adminId);
                    couponList.setUserId(userId);
                    couponList.setCouponId(coupon.getId());
                    couponList.setStatus(CouponEnum.USE_STATUS_NOT.getCode());
                    couponList.setOrderId(0);
                    couponList.setUseTime(0L);
                    couponList.setInvalidTime(invalidTime);
                    couponList.setCreateTime(currTime);
                    couponList.setUpdateTime(currTime);
                    couponListMapper.insert(couponList);
                }
            }

        }
    }

    /**
     * 邀请用户列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate InviterSearchValidate
     * @return PageResult<InviterVo>
     */
    @Override
    public PageResult<InviterVo> inviterList(PageValidate pageValidate, InviterSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_delete", 0);
        queryWrapper.eq("inviter_id", searchValidate.getUserId());
        queryWrapper.orderByDesc("id");
        queryWrapper.select(User.class, info ->
                !info.getColumn().equals("is_delete") &&
                        !info.getColumn().equals("delete_time") &&
                        !info.getColumn().equals("update_time") &&
                        !info.getColumn().equals("password") &&
                        !info.getColumn().equals("salt")
        );

        if (StringUtils.isNotBlank(searchValidate.getNickname())) {
            queryWrapper.like("nickname", searchValidate.getNickname());
        }

        if (StringUtils.isNotBlank(searchValidate.getSn())) {
            queryWrapper.like("sn", searchValidate.getSn());
        }

        if (StringUtils.isNotBlank(searchValidate.getUsername())) {
            queryWrapper.like("username", searchValidate.getUsername());
        }

        IPage<User> iPage = userMapper.selectPage(new Page<>(pageNo, pageSize), queryWrapper);

        List<InviterVo> list = new LinkedList<>();
        for (User user : iPage.getRecords()) {
            InviterVo vo = new InviterVo();
            BeanUtils.copyProperties(user, vo);

            // 消费金额
            BigDecimal totalAmount = orderMapper.sum("pay_money", new QueryWrapper<Order>()
                    .eq("is_delete", 0)
                    .eq("user_id", user.getId())
                    .eq("pay_is", 1));

            vo.setTotalAmount(totalAmount);
            vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
            vo.setLastLoginTime(TimeUtils.timestampToDate(user.getLastLoginTime()));
            vo.setCreateTime(TimeUtils.timestampToDate(user.getCreateTime()));
            list.add(vo);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), list);
    }

    @Override
    public User getUserByMobile(String mobile) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("mobile", mobile)
                .eq("is_delete", 0)
                .last("limit 1")
        );
        return user;
    }

    @Override
    public User getUserByID(Integer id) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("id", id)
                .eq("is_delete", 0)
                .last("limit 1")
        );
        return user;
    }
}
