package com.mdd.front.service;

import com.mdd.common.core.PageResult;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.selffetchVerifier.SelffetchVerifierSearchValidate;
import com.mdd.front.vo.selffetchVerifier.SelffetchVerifierDetailVo;
import com.mdd.front.vo.selffetchVerifier.SelffetchVerifierListedVo;

import java.util.List;

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
     * 返回用户ids
     * @param userId
     * @return
     */
    List<Integer> getShopIds(Integer userId);

    /**
     * 判断是否为有效的审核员
     */
    Integer isSelffetchVerifier(Integer userId);

    /**
     * 获取审核权限
     * @param shopId
     * @param userId
     * @return
     */
    Boolean verifyShopRights(Integer shopId, Integer userId);


}
