package com.mdd.front.service;

import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.distribution.DistributionApplyValidate;
import com.mdd.front.validate.distribution.DistributionFansSearchValidate;
import com.mdd.front.validate.distribution.DistributionOrderSearchValidate;
import com.mdd.front.vo.distribution.*;

import java.util.List;

/**
 * 分销接口类
 */
public interface IDistributionService {

    /**
     * 分销配置
     *
     * @author mjf
     * @return DistributionConfigVo
     */
    DistributionConfigVo config();

    /**
     * 邀请码绑定关系
     *
     * @author mjf
     * @param userId Integer
     * @param code String
     */
    void bind(Integer userId, String code);

    /**
     * 申请分销
     *
     * @author mjf
     * @param userId Integer
     * @param applyValidate DistributionApplyValidate
     */
    void apply(Integer userId, DistributionApplyValidate applyValidate);

    /**
     * 申请记录详情
     *
     * @author mjf
     * @param userId Integer
     * @return DistributionApplyDetailVo
     */
    DistributionApplyDetailVo applyDetail(Integer userId);

    /**
     * 新增用户分销信息(用户注册时使用)
     *
     * @author mjf
     * @param userId Integer
     */
    void addDistributionData(Integer userId);

    /**
     * 粉丝列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionFansSearchValidate
     * @param userId Integer // 当前用户id
     * @return PageResult<DistributionFansVo>
     */
    PageResult<DistributionFansVo> fans(PageValidate pageValidate, DistributionFansSearchValidate searchValidate, Integer userId);

    /**
     * 分销订单
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionOrderSearchValidate
     * @param userId Integer
     * @return PageResult<Object>
     */
    PageResult<DistributionOrderVo> order(PageValidate pageValidate, DistributionOrderSearchValidate searchValidate, Integer userId);

    /**
     * 分销页信息
     *
     * @author mjf
     * @param userId Integer
     * @return DistributionIndexVo
     */
    DistributionIndexVo index(Integer userId);

    /**
     * 校验能否绑定上下级关系
     *
     * @author mjf
     * @param code String
     * @param userId Integer
     */
    boolean checkAbleBindLeader(Integer userId, String code);

    /**
     * 校验能否绑定上下级关系
     *
     * @author mjf
     * @param userId Integer
     * @param code String
     */
    void checkAbleBind(Integer userId, String code);

    /**
     * 绑定上级
     *
     * @author mjf
     * @param code String
     * @param userId Integer
     */
    void bindLeader(Integer userId, String code);

    /**
     * 绑定邀请人
     *
     * @author mjf
     * @param userId Integer
     * @param inviteCode String
     */
    void bindInviter(Integer userId, String inviteCode);

    /**
     * 生成分销订单
     *
     * @author mjf
     * @param orderId Integer
     */
    void addDistributionOrder(Integer orderId);

    /**
     * 更新分销等级
     *
     * @author mjf
     * @param userId Integer
     */
    void updateDistributionLevel(Integer userId);

    /**
     * 分销等级
     *
     * @author mjf
     * @param userId Integer
     * @return List<DistributionLevelVo>
     */
    List<DistributionLevelVo> level(Integer userId);

    /**
     * 返回省市区字符串
     */
    String getRegion(Integer provinceId, Integer cityId, Integer districtId);
}
