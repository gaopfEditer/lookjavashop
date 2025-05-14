package com.mdd.admin.service;

import com.mdd.admin.validate.delivery.DeliverTplCreateValidate;
import com.mdd.admin.validate.delivery.DeliverTplUpdateValidate;
import com.mdd.admin.vo.delivery.DeliverTplListedVo;
import com.mdd.admin.vo.delivery.DeliverTplDetailVo;

import java.util.List;

/**
 * 运费模板服务接口类
 */
public interface IExpressTplService {

    /**
     * 运费模板列表
     *
     * @author cjh
     * @return List<ExpressTplListVo>
     */
    List<DeliverTplListedVo> list();

    /**
     * 运费模板详情
     *
     * @author cjh
     * @param id 主键
     * @return ExpressTplVo
     */
    DeliverTplDetailVo detail(Integer id);

    /**
     * 运费模板新增
     *
     * @author cjh
     * @param deliverTplCreateValidate 参数
     */
    void add(DeliverTplCreateValidate deliverTplCreateValidate);

    /**
     * 运费模板编辑
     *
     * @author cjh
     * @param deliverTplUpdateValidate 参数
     */
    void edit(DeliverTplUpdateValidate deliverTplUpdateValidate);

    /**
     * 运费模板删除
     *
     * @param id 主键
     */
    void del(Integer id);

}
