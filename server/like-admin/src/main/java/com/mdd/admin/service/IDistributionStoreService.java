package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.*;
import com.mdd.admin.vo.distribution.*;
import com.mdd.common.core.PageResult;

/**
 * 分销商服务接口类
 */
public interface IDistributionStoreService {

    /**
     * 分销商列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionStoreSearchValidate
     * @return PageResult<DistributionStoreListedVo>
     */
    PageResult<DistributionStoreListedVo> list(PageValidate pageValidate, DistributionStoreSearchValidate searchValidate);

    /**
     * 开通分销商
     *
     * @author mjf
     * @param openValidate DistributionStoreOpenValidate
     */
    void open(DistributionStoreOpenValidate openValidate);

    /**
     * 冻结/解冻
     *
     * @author mjf
     * @param id Integer
     */
    void freeze(Integer id);

    /**
     * 调整等级详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionAdjustLevelVo
     */
    DistributionAdjustLevelVo adjustLevelInfo(Integer id);

    /**
     * 调整等级
     *
     * @author mjf
     * @param adjustValidate DistributionAdjustLevelValidate
     */
    void adjustLevel(DistributionAdjustLevelValidate adjustValidate);

    /**
     * 分销商详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionStoreDetailVo
     */
    DistributionStoreDetailVo detail(Integer id);

    /**
     * 下级信息详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionFansInfoVo
     */
    DistributionFansInfoVo fansInfo(Integer id);

    /**
     * 下级列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionFansSearchValidate
     * @return PageResult<DistributionFansListedVo>
     */
    PageResult<DistributionFansListedVo> fansList(PageValidate pageValidate, DistributionFansSearchValidate searchValidate);

    /**
     * 调整上级
     *
     * @author mjf
     * @param adjustValidate DistributionAdjustLeaderValidate
     */
    void adjustLeader(DistributionAdjustLeaderValidate adjustValidate);

    /**
     * 更新分销会员等级
     *
     * @author mjf
     * @param userId Integer
     */
    void updateDistributionLevel(Integer userId);

    /**
     * 分销初始化数据
     *
     * @author mjf
     */
    void updateData();
}
