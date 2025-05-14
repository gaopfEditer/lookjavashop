package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.DistributionLevelCreateValidate;
import com.mdd.admin.validate.distribution.DistributionLevelUpdateValidate;
import com.mdd.admin.vo.distribution.DistributionLevelDetailVo;
import com.mdd.admin.vo.distribution.DistributionLevelListedVo;
import com.mdd.admin.vo.distribution.DistributionLevelSelectListVo;
import com.mdd.common.core.PageResult;

import java.util.List;

/**
 * 分销等级服务接口类
 */
public interface IDistributionLevelService {

    /**
     * 等级列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @return PageResult<DistributionLevelListedVo>
     */
    PageResult<DistributionLevelListedVo> list(PageValidate pageValidate);

    /**
     * 等级新增
     *
     * @author mjf
     * @param createValidate DistributionLevelCreateValidate
     */
    void add(DistributionLevelCreateValidate createValidate);

    /**
     * 等级编辑
     *
     * @author mjf
     * @param updateValidate DistributionLevelUpdateValidate
     */
    void edit(DistributionLevelUpdateValidate updateValidate);

    /**
     * 等级详情
     *
     * @author mjf
     * @param id Integer
     * @return DistributionLevelDetailVo
     */
    DistributionLevelDetailVo detail(Integer id);

    /**
     * 等级删除
     *
     * @author mjf
     * @param id Integer
     */
    void delete(Integer id);

    /**
     * 等级列表
     *
     * @author mjf
     * @return List<DistributionLevelSelectListVo>
     */
    List<DistributionLevelSelectListVo> selectList();
}
