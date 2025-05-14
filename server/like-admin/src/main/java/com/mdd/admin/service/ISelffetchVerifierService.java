package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierCreateValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierStatusValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierUpdateValidate;
import com.mdd.admin.validate.selffetchVerifier.SelffetchVerifierSearchValidate;
import com.mdd.admin.validate.selffetchshop.SelffetchShopStatusValidate;
import com.mdd.admin.vo.selffetchVerifier.SelffetchVerifierListedVo;
import com.mdd.admin.vo.selffetchVerifier.SelffetchVerifierDetailVo;
import com.mdd.common.core.PageResult;

/**
 * 核销员服务接口类
 * @author LikeAdmin
 */
public interface ISelffetchVerifierService {

    /**
     * 核销员列表
     *
     * @author LikeAdmin
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<SelffetchVerifierListedVo>
     */
    PageResult<SelffetchVerifierListedVo> list(PageValidate pageValidate, SelffetchVerifierSearchValidate searchValidate);

    /**
     * 核销员详情
     *
     * @author LikeAdmin
     * @param id 主键ID
     * @return SelffetchVerifierDetailVo
     */
    SelffetchVerifierDetailVo detail(Integer id);

    /**
     * 核销员新增
     *
     * @author LikeAdmin
     * @param createValidate 参数
     */
    void add(SelffetchVerifierCreateValidate createValidate);

    /**
     * 核销员编辑
     *
     * @author LikeAdmin
     * @param updateValidate 参数
     */
    void edit(SelffetchVerifierUpdateValidate updateValidate);

    /**
     * 核销员删除
     *
     * @author LikeAdmin
     * @param id 主键ID
     */
    void del(Integer id);


    /**
     * 判断审核员名称是否存在
     * @param name
     * @return
     */
    Boolean nameUnique(String name);
    Boolean nameUnique(String name, Integer id);

    /**
     * 根据名称返回审核员名称
     * @param id
     * @return
     */
    String getNameById(Integer id);

    /**
     * @notes 修改自提门店状态
     * @author damonyuan
     */
    void status(SelffetchVerifierStatusValidate selffetchVerifierStatusValidate);

}
