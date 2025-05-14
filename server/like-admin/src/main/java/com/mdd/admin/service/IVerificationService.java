package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.VerificationCreateValidate;
import com.mdd.admin.validate.VerificationUpdateValidate;
import com.mdd.admin.validate.VerificationSearchValidate;
import com.mdd.admin.vo.VerificationListedVo;
import com.mdd.admin.vo.VerificationDetailVo;
import com.mdd.common.core.PageResult;

/**
 * 核销服务接口类
 * @author LikeAdmin
 */
public interface IVerificationService {

    /**
     * 核销列表
     *
     * @author LikeAdmin
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<VerificationListedVo>
     */
    PageResult<VerificationListedVo> list(PageValidate pageValidate, VerificationSearchValidate searchValidate);

    /**
     * 核销详情
     *
     * @author LikeAdmin
     * @param id 主键ID
     * @return VerificationDetailVo
     */
    VerificationDetailVo detail(Integer id);

    /**
     * 核销新增
     *
     * @author LikeAdmin
     * @param createValidate 参数
     */
    void add(VerificationCreateValidate createValidate);

    /**
     * 核销编辑
     *
     * @author LikeAdmin
     * @param updateValidate 参数
     */
    void edit(VerificationUpdateValidate updateValidate);

    /**
     * 核销删除
     *
     * @author LikeAdmin
     * @param id 主键ID
     */
    void del(Integer id);

}
