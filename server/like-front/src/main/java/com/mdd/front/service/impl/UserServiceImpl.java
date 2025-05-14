package com.mdd.front.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.distribution.Distribution;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.entity.user.User;
import com.mdd.common.entity.user.UserAuth;
import com.mdd.common.entity.withdraw.WithdrawApply;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.distribution.DistributionMapper;
import com.mdd.common.mapper.order.OrderAfterMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.user.UserAuthMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.mapper.withdraw.WithdrawApplyMapper;
import com.mdd.common.plugin.notice.NoticeCheck;
import com.mdd.common.plugin.wechat.WxMnpDriver;
import com.mdd.common.util.*;
import com.mdd.front.LikeFrontThreadLocal;
import com.mdd.front.service.ISelffetchVerifierService;
import com.mdd.front.service.IUserService;
import com.mdd.front.validate.user.*;
import com.mdd.front.vo.user.UserCenterVo;
import com.mdd.front.vo.user.UserInfoVo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements IUserService {

    @Lazy
    @Resource
    ISelffetchVerifierService iSelffetchVerifierService;
    @Resource
    UserMapper userMapper;

    @Resource
    UserAuthMapper userAuthMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    DistributionMapper distributionMapper;

    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    WithdrawApplyMapper withdrawApplyMapper;

    /**
     * 个人中心
     *
     * @author fzr
     * @param userId 用户ID
     * @return UserCenterVo
     */
    @Override
    public UserCenterVo center(Integer userId, Integer terminal) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,sn,avatar,real_name,nickname,username,mobile,money,is_new,code,earnings")
                .eq("id", userId)
                .last("limit 1"));

        UserCenterVo vo = new UserCenterVo();
        BeanUtils.copyProperties(user, vo);
        if (user.getAvatar().equals("")) {
            String avatar = ConfigUtils.get("user", "defaultAvatar", "");
            vo.setAvatar(UrlUtils.toAbsoluteUrl(avatar));
        } else {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
        }

        vo.setIsBindWechat(false);
        if (terminal.equals(ClientEnum.OA.getCode()) || terminal.equals(ClientEnum.MNP.getCode())) {
            UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                    .select("id,openid,terminal")
                    .eq("user_id", userId)
                    .eq("terminal", terminal)
                    .last("limit 1"));
            vo.setIsBindWechat(userAuth != null);
        }

        // 订单统计
        Long waitPayCount = orderMapper.selectCount(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .eq("order_status", OrderEnum.ORDER_STATUS_WAIT_PAY.getCode()));
        Long waitDeliveryCount = orderMapper.selectCount(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .eq("order_status", OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()));
        Long waitConfirmCount = orderMapper.selectCount(new QueryWrapper<Order>()
                .eq("user_id", userId)
                .eq("is_delete", 0)
                .eq("order_status", OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode()));

        // 商品评价统计
        Long waitCommentCount = orderGoodsMapper.selectCount(new MPJQueryWrapper<OrderGoods>()
                .select("t.id")
                .innerJoin("?_order o ON o.id = t.order_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("o.order_status", OrderEnum.ORDER_STATUS_COMPLETED.getCode())
                .eq("t.user_id", userId)
                .eq("t.is_comment", 0));

        // 售后统计
        MPJQueryWrapper<OrderGoods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.innerJoin("?_order o ON o.id=t.order_id".replace("?_", GlobalConfig.tablePrefix))
                .select("t.id")
                .eq("t.after_sale", OrderGoodsEnum.AFTER_STATUS_ING.getCode())
                .eq("o.user_id", userId)
                .eq("o.is_delete", 0)
                .eq("o.pay_is", PaymentEnum.OK_PAID.getCode())
                .gt("o.order_status", OrderEnum.ORDER_STATUS_WAIT_PAY.getCode())
                .lt("o.order_status", OrderEnum.ORDER_STATUS_CANCEL.getCode());
        Long waitAfterSaleCount = orderGoodsMapper.selectCount(mpjQueryWrapper);

        // 是否为分销会员
        vo.setIsDistribution(0);
        Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                .eq("user_id", userId).eq("delete_time", 0).orderByDesc("id").last("limit 1"));

        if (!ObjectUtils.isEmpty(distribution) && distribution.getIsDistribution().equals(1)) {
            vo.setIsDistribution(1);
        }

        // 是否为自提核销员
        vo.setIsSelffetchVerifier(iSelffetchVerifierService.isSelffetchVerifier(userId));
        vo.setWaitPayCount(waitPayCount);
        vo.setWaitDeliveryCount(waitDeliveryCount);
        vo.setWaitConfirmCount(waitConfirmCount);
        vo.setWaitCommentCount(waitCommentCount);
        vo.setWaitAfterSaleCount(waitAfterSaleCount);
        return vo;
    }

    /**
     * 个人信息
     *
     * @author fzr
     * @param userId 用户ID
     * @return UserInfoVo
     */
    @Override
    public UserInfoVo info(Integer userId) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,sn,avatar,real_name,nickname,username,mobile,password,sex,create_time")
                .eq("id", userId)
                .last("limit 1"));

        UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .select("id,openid")
                .eq("user_id", userId)
                .last("limit 1"));

        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(user, vo);
        vo.setIsPassword(!user.getPassword().equals(""));
        vo.setIsBindMnp(userAuth != null);
        vo.setVersion(GlobalConfig.version);
        vo.setSex(user.getSex());
        vo.setCreateTime(TimeUtils.timestampToDate(user.getCreateTime()));

        if (!user.getAvatar().equals("")) {
            vo.setAvatar(UrlUtils.toAbsoluteUrl(user.getAvatar()));
        } else {
            String avatar = ConfigUtils.get("user", "defaultAvatar", "");
            vo.setAvatar(UrlUtils.toAbsoluteUrl(avatar));
        }

        return vo;
    }

    /**
     * 编辑信息
     *
     * @author fzr
     * @param updateValidate 参数
     * @param userId 用户ID
     */
    @Override
    public void edit(UserUpdateValidate updateValidate, Integer userId) {
        String field = updateValidate.getField();
        String value = updateValidate.getValue();

        switch (field) {
            case "avatar":
                User avatarUser = new User();
                avatarUser.setId(userId);
                avatarUser.setAvatar(UrlUtils.toRelativeUrl(value));
                avatarUser.setUpdateTime(System.currentTimeMillis() / 1000);
                userMapper.updateById(avatarUser);
                break;
            case "username":
                User usernameUser = userMapper.selectOne(new QueryWrapper<User>()
                        .select("id,username")
                        .eq("username", value)
                        .eq("is_delete", 0)
                        .last("limit 1"));

                if (StringUtils.isNotNull(usernameUser) && !usernameUser.getId().equals(userId)) {
                    throw new OperateException("账号已被使用!");
                }

                if (StringUtils.isNotNull(usernameUser) && usernameUser.getUsername().equals(value)) {
                    throw new OperateException("新账号与旧账号一致,修改失败!");
                }

                User u = new User();
                u.setId(userId);
                u.setUsername(value);
                u.setUpdateTime(System.currentTimeMillis() / 1000);
                userMapper.updateById(u);
                break;
            case "nickname":
                User nicknameUser = new User();
                nicknameUser.setId(userId);
                nicknameUser.setNickname(value);
                nicknameUser.setUpdateTime(System.currentTimeMillis() / 1000);
                userMapper.updateById(nicknameUser);
                break;
            case "sex":
                User sexUser = new User();
                sexUser.setId(userId);
                sexUser.setSex(Integer.parseInt(value));
                sexUser.setUpdateTime(System.currentTimeMillis() / 1000);
                userMapper.updateById(sexUser);
                break;
            default:
                throw new OperateException("不被支持的类型");
        }
    }

    /**
     * 修改密码
     *
     * @author fzr
     * @param password 新密码
     * @param userId 用户ID
     */
    @Override
    public void changePwd(String password, String oldPassword, Integer userId) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,password,salt")
                .eq("id", userId)
                .eq("is_delete", 0)
                .last("limit 1"));

        Assert.notNull(user, "用户不存在");

        if (!user.getPassword().equals("")) {
            Assert.notNull(oldPassword, "oldPassword参数缺失");
            String oldPwd = ToolUtils.makeMd5(oldPassword.trim() + user.getSalt());
            if (!oldPwd.equals(user.getPassword())) {
                throw new OperateException("原密码不正确!");
            }
        }

        String salt = ToolUtils.randomString(5);
        String pwd  = ToolUtils.makeMd5(password.trim()+salt);

        User u = new User();
        u.setId(userId);
        u.setPassword(pwd);
        u.setSalt(salt);
        u.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(u);
    }

    /**
     * 忘记密码
     *
     * @author fzr
     * @param password 新密码
     * @param mobile 手机号
     * @param code 验证码
     */
    @Override
    public void forgotPwd(String password, String mobile, String code) {
        // 校验验证码
        int sceneCode = NoticeEnum.FORGOT_PASSWORD_CODE.getCode();
        if (!NoticeCheck.verify(sceneCode, code)) {
            throw new OperateException("验证码错误!");
        }

        // 查询手机号
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,username,mobile,is_disable")
                .eq("is_delete", 0)
                .eq("mobile", mobile)
                .last("limit 1"));

        // 验证账号
        com.baomidou.mybatisplus.core.toolkit.Assert.notNull(user, "账号不存在!");

        String salt = ToolUtils.randomString(5);
        String pwd  = ToolUtils.makeMd5(password.trim()+salt);

        // 更新密码
        user.setPassword(pwd);
        user.setSalt(salt);
        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(user);
    }

    /**
     * 绑定手机
     *
     * @author fzr
     * @param mobileValidate 参数
     * @param userId 用户ID
     */
    @Override
    public void bindMobile(UserPhoneBindValidate mobileValidate, Integer userId) {
        String type   = mobileValidate.getType();
        String mobile = mobileValidate.getMobile();
        String code   = mobileValidate.getCode().toLowerCase();

        // 校验验证码
        int sceneCode = type.equals("bind") ? NoticeEnum.BIND_MOBILE_CODE.getCode() : NoticeEnum.CHANGE_MOBILE_CODE.getCode() ;
        if (!NoticeCheck.verify(sceneCode, code)) {
            throw new OperateException("验证码错误!");
        }

        User user = userMapper.selectOne(new QueryWrapper<User>()
                .select("id,username,mobile")
                .eq("mobile", mobile)
                .eq("is_delete", 0)
                .last("limit 1"));

        if (StringUtils.isNotNull(user) && user.getId().equals(userId)) {
            throw new OperateException("手机号已被其它账号绑定!");
        }

        User u = new User();
        u.setId(userId);
        u.setMobile(mobile);
        u.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(u);
    }

    /**
     * 微信手机号
     *
     * @author fzr
     * @param code 获取手机号的Code
     */
    @Override
    public void mnpMobile(String code) {
        Map<String, String> config = ConfigUtils.get("mp_channel");
        WxMaService wxMaService = new WxMaServiceImpl();
        WxMaDefaultConfigImpl wxConfig = new WxMaDefaultConfigImpl();
        wxConfig.setSecret(config.getOrDefault("appSecret", ""));
        wxConfig.setAppid(config.getOrDefault("appId", ""));
        wxMaService.setWxMaConfig(wxConfig);

        try {
            WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxMaService.getUserService().getPhoneNumber(code);

            Integer userId = LikeFrontThreadLocal.getUserId();
            User userCheck = userMapper.selectOne(new QueryWrapper<User>()
                    .select("id,username,mobile")
                    .eq("mobile", wxMaPhoneNumberInfo.getPhoneNumber())
                    .eq("is_delete", 0)
                    .last("limit 1"));

            if (StringUtils.isNotNull(userCheck) && (!userCheck.getId().equals(userId))) {
                throw new OperateException("手机号已被其它账号绑定!");
            }
            User user = new User();
            user.setId(userId);
            user.setMobile(wxMaPhoneNumberInfo.getPhoneNumber());
            user.setUpdateTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(user);
        } catch (WxErrorException e) {
            throw new OperateException(e.getError().getErrorCode() + ", " + e.getError().getErrorMsg());
        }
    }


    /**
     * 更新新用户昵称头像等信息
     *
     * @param newUserUpdateValidate 参数
     * @param userId 用户id
     */
    @Override
    public void updateNewUserInfo(NewUserUpdateValidate newUserUpdateValidate, Integer userId) {
        User user = new User();
        user.setId(userId);
        user.setNickname(newUserUpdateValidate.getNickname());
        user.setAvatar(UrlUtils.toRelativeUrl(newUserUpdateValidate.getAvatar()));
        user.setIsNew(0);
        user.setUpdateTime(System.currentTimeMillis() / 1000);
        userMapper.updateById(user);
    }

    /**
     * 绑定小程序
     *
     * @param bindMnpValidate 参数
     * @param userId 用户ID
     */
    @Override
    public void bindMnp(UserBindWechatValidate bindMnpValidate, Integer userId) {
        try {
            // 通过code获取微信信息
            String code = bindMnpValidate.getCode();
            WxMaService wxMaService = WxMnpDriver.mnp();
            WxMaJscode2SessionResult sessionResult = wxMaService.getUserService().getSessionInfo(code);
            String openId = sessionResult.getOpenid();
            String uniId = sessionResult.getUnionid();
            String unionId = uniId == null ? "0" : uniId;

            // 授权校验,未授权创建授权，已授权返回
            bindWechatAuth(openId, unionId, ClientEnum.MNP.getCode(), userId);

        } catch (WxErrorException e) {
            throw new OperateException(e.getError().getErrorCode() + ", " + e.getError().getErrorMsg());
        }
    }

    /**
     * 绑定公众号
     *
     * @param bindOaValidate 参数
     * @param userId 用户ID
     */
    @Override
    public void bindOa(UserBindWechatValidate bindOaValidate, Integer userId) {
        try {
            // 通过code获取微信信息
            WxMpService wxMpService = WxMnpDriver.oa();
            WxOAuth2AccessToken wxOAuth2AccessToken = wxMpService.getOAuth2Service().getAccessToken(bindOaValidate.getCode());
            String uniId = wxOAuth2AccessToken.getUnionId();
            String openId  = wxOAuth2AccessToken.getOpenId();
            String unionId = uniId == null ? "0" : uniId;

            // 授权校验,未授权创建授权，已授权返回
            bindWechatAuth(openId, unionId, ClientEnum.OA.getCode(), userId);

        } catch (WxErrorException e) {
            throw new OperateException(e.getError().getErrorCode() + ", " + e.getError().getErrorMsg());
        }
    }

    /**
     * 注销账户
     *
     * @param userId 用户ID
     */
    @Override
    public List<Map<String, Object>> cancellation(Integer userId) {
        List<Map<String, Object>> linkedList = new LinkedList<>();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("id", userId);
        userQueryWrapper.eq("is_delete",0);
        userQueryWrapper.last("limit 1");
        User user = userMapper.selectOne(userQueryWrapper);
        Assert.notNull(user, "该用户不存在！");

        List<Integer> errorOrderStatusList = Arrays.asList(
                OrderEnum.ORDER_STATUS_WAIT_PAY.getCode(),
                OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode(),
                OrderEnum.ORDER_STATUS_TAKE_DELIVER.getCode());

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("order_status",errorOrderStatusList);
        queryWrapper.eq("is_delete",0);
        queryWrapper.eq("user_id", userId);
        queryWrapper.last("limit 1");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        if (!orderList.isEmpty()){
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code",1);
            map.put("title","存在未完成订单");
            map.put("content","发现你的账号内有未处理完成的商城订单，待完成后可重新提交申请。");
            linkedList.add(map);
        }

        QueryWrapper<OrderAfter> orderAfterQueryWrapper = new QueryWrapper<>();
        orderAfterQueryWrapper.eq("is_delete",0);
        orderAfterQueryWrapper.eq("user_id", userId);
        orderAfterQueryWrapper.eq("after_status",OrderAfterEnum.AFTER_STATUS_ING.getCode());
        List<OrderAfter> orderAfterList = orderAfterMapper.selectList(orderAfterQueryWrapper);
        if (!orderAfterList.isEmpty()){
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code",2);
            map.put("title","存在售后中订单");
            map.put("content","发现你的账号内有订单处于售后中，待商家处理完成后可重新提交申请。");
            linkedList.add(map);
        }

        QueryWrapper<WithdrawApply> withdrawApplyQueryWrapper = new QueryWrapper<>();
        withdrawApplyQueryWrapper.eq("is_delete",0);
        withdrawApplyQueryWrapper.in("status",Arrays.asList(1,2));
        withdrawApplyQueryWrapper.eq("user_id", userId);
        List<WithdrawApply> withdrawApplyList = withdrawApplyMapper.selectList(withdrawApplyQueryWrapper);

        if (!withdrawApplyList.isEmpty()){
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code",3);
            map.put("title","存在佣金待提现申请");
            map.put("content","发现你的账号内有佣金处于提现中，待商家处理完成后可重新提交申请。");
            linkedList.add(map);
        }
        if (linkedList.isEmpty()){
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code",0);
            map.put("content","注销成功！");
            linkedList.add(map);
            // 把用户设置为注销状态
            user.setIsClose(1);
            // 清空用户手机号
            user.setMobile("");
            user.setUpdateTime(System.currentTimeMillis() / 1000);
            user.setDeleteTime(System.currentTimeMillis() / 1000);
            userMapper.updateById(user);
            // 查询是否绑定小程序或公众号
            QueryWrapper<UserAuth> userAuthQueryWrapper = new QueryWrapper<>();
            userAuthQueryWrapper.eq("user_id", userId);
            List<UserAuth> userAuthList = userAuthMapper.selectList(userAuthQueryWrapper);
            // 清除openid
            if (!userAuthList.isEmpty()){
                for (UserAuth userAuth : userAuthList) {
                    userAuthMapper.deleteById(userAuth);
                }
            }
            // 修改分销功能
            Distribution distribution = distributionMapper.selectOne(new QueryWrapper<Distribution>()
                    .eq("user_id", userId)
                    .eq("is_delete", 0)
                    .last("limit 1"));

            if (null != distribution){
                // 冻结分销商
                distribution.setIsFreeze(1);
                // 锁定分销商不能修改
                distribution.setIsClose(1);
                distribution.setUpdateTime(System.currentTimeMillis() / 1000);
                distributionMapper.updateById(distribution);
            }

            // 查询是否有下级分销用户
            QueryWrapper<User> queryUserWrapper = new QueryWrapper<>();
            queryUserWrapper.eq("first_leader", userId)
                            .or()
                            .eq("second_leader", userId)
                            .or()
                            .eq("third_leader", userId);
            queryUserWrapper.eq("is_delete",0);
            queryUserWrapper.eq("is_close", 0);
            List<User> distributionUserList = userMapper.selectList(queryUserWrapper);
            if (!distributionUserList.isEmpty()){
                // 取消所有下级用户
                for (User distributionUser : distributionUserList) {
                    if (distributionUser.getFirstLeader().equals(userId)){
                        distributionUser.setFirstLeader(0);
                        // distributionUser.setIsLockLeader(1);
                    }
                    if (distributionUser.getSecondLeader().equals(userId)){
                        distributionUser.setSecondLeader(0);
                        // distributionUser.setIsLockLeader(1);
                    }
                    if (distributionUser.getThirdLeader().equals(userId)){
                        distributionUser.setThirdLeader(0);
                        // distributionUser.setIsLockLeader(1);
                    }
                    userMapper.updateById(distributionUser);
                }
            }
            // 将分销重置为系统
            user.setFirstLeader(0);
            user.setSecondLeader(0);
            user.setThirdLeader(0);
            userMapper.updateById(user);
        }
        return linkedList;
    }

    /**
     * 绑定微信授权
     *
     * @param openId openId
     * @param unionId unionId
     * @param terminal 客户端端
     * @param userId 用户ID
     */
    public void bindWechatAuth(String openId, String unionId, Integer terminal, Integer userId) {
        // 授权表中查找授权记录
        UserAuth userAuthOpenId = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                .eq("openid", openId)
                .last("limit 1"));

        if (userAuthOpenId != null) {
            // 该微信已绑定
            throw new OperateException("该微信已绑定");
        }

        // 已有授权，返回已绑定微信。 没有授权，绑定微信
        if (!StringUtils.isBlank(unionId)) {
            UserAuth userAuthUnionId = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                    .eq("unionid", unionId)
                    .last("limit 1"));

            if (userAuthUnionId != null && !userId.equals(userAuthUnionId.getUserId())) {
                // 该微信已绑定
                throw new OperateException("该微信已绑定");
            }
        }

        if (StringUtils.isNotNull(unionId)) {
            if (unionId.equals("0") == false) {
                //在用unionid找记录，防止生成两个账号，同个unionid的问题
                UserAuth userAuth = userAuthMapper.selectOne(new QueryWrapper<UserAuth>()
                        .eq("unionid", unionId)
                        .last("limit 1"));
                if (StringUtils.isNotNull(userAuth)) {
                    throw new OperateException("该微信已被绑定");
                }
            }
        }

        // 记录微信授权
        UserAuth authModel = new UserAuth();
        authModel.setUserId(userId);
        authModel.setUnionid(unionId);
        authModel.setOpenid(openId);
        authModel.setTerminal(terminal);
        authModel.setCreateTime(System.currentTimeMillis() / 1000);
        authModel.setUpdateTime(System.currentTimeMillis() / 1000);
        userAuthMapper.insert(authModel);
    }

}
