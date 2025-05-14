package com.mdd.admin.service;

import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.delivery.DeliverCompanyCreateValidate;
import com.mdd.admin.validate.delivery.DeliverCompanyUpdateValidate;
import com.mdd.admin.vo.delivery.DeliverCompanyVo;

import java.util.List;

/**
 * 快递公司服务接口类
 */
public interface IExpressCompanyService {

    /**
     * 快递列表
     *
     * @author cjh
     * @param pageValidate 分页参数
     * @param name 快递名称
     * @return List<ExpressCompanyVo>
     */
    List<DeliverCompanyVo> list(PageValidate pageValidate, String name);

    /**
     * 快递详情
     *
     * @author cjh
     * @param id 主键
     * @return ExpressCompanyVo
     */
    DeliverCompanyVo detail(Integer id);

    /**
     * 快递新增
     *
     * @author cjh
     * @param deliverCompanyCreateValidate 参数
     */
    void add(DeliverCompanyCreateValidate deliverCompanyCreateValidate);

    /**
     * 快递编辑
     *
     * @author cjh
     * @param deliverCompanyUpdateValidate 参数
     */
    void edit(DeliverCompanyUpdateValidate deliverCompanyUpdateValidate);

    /**
     * 快递删除
     *
     * @author cjh
     * @param id 主键
     */
    void del(Integer id);

}
