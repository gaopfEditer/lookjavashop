package com.mdd.admin.service;

import com.mdd.admin.validate.distribution.DistributionConfigValidate;
import com.mdd.admin.vo.distribution.DistributionConfigVo;

/**
 * 分销配置服务接口累
 */
public interface IDistributionConfigService {

    /**
     * 分销配置详情
     *
     * @author fzr
     * @return DistributionConfigVo
     */
    DistributionConfigVo detail();

    /**
     * 分销配置保存
     *
     * @author fzr
     * @param configValidate 参数
     */
    void save(DistributionConfigValidate configValidate);

}
