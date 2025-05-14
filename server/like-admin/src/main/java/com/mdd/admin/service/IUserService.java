package com.mdd.admin.service;

import com.mdd.admin.validate.user.*;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.vo.user.InviterVo;
import com.mdd.admin.vo.user.UserVo;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.user.User;

import java.util.List;

/**
 * 用户服务接口类
 */
public interface IUserService {

    /**
     * 用户列表
     *
     * @author fzr
     * @param pageValidate (分页参数)
     * @param searchValidate (搜索参数)
     * @return PageResult<UserVo>
     */
    PageResult<UserVo> list(PageValidate pageValidate, UserSearchValidate searchValidate);

    /**
     * 用户详情
     *
     * @author fzr
     * @param id 主键
     * @return UserVo
     */
    UserVo detail(Integer id);

    /**
     * 用户编辑
     *
     * @author fzr
     * @param updateValidate 参数
     */
    void edit(UserUpdateValidate updateValidate);

    /**
     * 余额调整
     *
     * @author cjh
     * @param userWalletValidate 余额
     */
    void adjustWallet(UserWalletValidate userWalletValidate);

    /**
     * 调整用户佣金
     *
     * @author mjf
     * @param earningsValidate UserEarningsValidate
     */
    void adjustEarnings(UserEarningsValidate earningsValidate);

    /**
     * 发放优惠券
     *
     * @author fzr
     * @param couponIds 优惠券IDs
     * @param userIds 用户IDs
     * @param number 发放数量
     * @param adminId 管理员ID
     */
    void sendCoupon(List<Integer> couponIds, List<Integer> userIds, Integer number, Integer adminId);

    /**
     * 邀请用户
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate InviterSearchValidate
     * @return PageResult<InviterVo>
     */
    PageResult<InviterVo> inviterList(PageValidate pageValidate, InviterSearchValidate searchValidate);

    /**
     * 通过手机号码查找用户
     * @param mobile
     * @return
     */
    User getUserByMobile(String mobile);

    /**
     * 通过ID查找用户
     * @param id
     * @return
     */
    User getUserByID(Integer id);
}
