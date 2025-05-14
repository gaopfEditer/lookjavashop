package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.IExpressCompanyService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.delivery.DeliverCompanyCreateValidate;
import com.mdd.admin.validate.delivery.DeliverCompanyUpdateValidate;
import com.mdd.admin.vo.delivery.DeliverCompanyVo;
import com.mdd.common.entity.delivery.ExpressCompany;
import com.mdd.common.mapper.delivery.ExpressCompanyMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 快递公司服务实现类
 */
@Service
public class ExpressCompanyServiceImpl implements IExpressCompanyService {

    @Resource
    ExpressCompanyMapper expressCompanyMapper;

    /**
     * 快递列表
     *
     * @author cjh
     * @param pageValidate 分页参数
     * @param name 快递名称
     * @return List<ExpressCompanyVo>
     */
    @Override
    public List<DeliverCompanyVo> list(PageValidate pageValidate, String name) {
        QueryWrapper<ExpressCompany> expressCompanyQueryWrapper = new QueryWrapper<>();
        expressCompanyQueryWrapper.eq("is_delete",0);
        expressCompanyQueryWrapper.orderByAsc("id");

        if(StringUtils.isNotEmpty(name)){
            expressCompanyQueryWrapper.like("name", name);
        }

        List<ExpressCompany> ExpressCompanyLists = expressCompanyMapper.selectList(expressCompanyQueryWrapper);

        List<DeliverCompanyVo> list = new LinkedList<>();
        for (ExpressCompany expressCompany : ExpressCompanyLists) {
            DeliverCompanyVo deliverCompanyVo = new DeliverCompanyVo();
            deliverCompanyVo.setId(expressCompany.getId());
            deliverCompanyVo.setName(expressCompany.getName());
            deliverCompanyVo.setImage(UrlUtils.toAbsoluteUrl(expressCompany.getImage()));
            deliverCompanyVo.setCodeKd(expressCompany.getCodeKd());
            deliverCompanyVo.setCodeKd100(expressCompany.getCodeKd100());
            deliverCompanyVo.setCodeKdniao(expressCompany.getCodeKdniao());
            deliverCompanyVo.setSort(expressCompany.getSort());
            deliverCompanyVo.setCreateTime(TimeUtils.timestampToDate(expressCompany.getCreateTime()));
            list.add(deliverCompanyVo);
        }

        return list;
    }

    /**
     * 快递详情
     *
     * @author cjh
     * @param id 主键
     * @return ExpressCompanyVo
     */
    @Override
    public DeliverCompanyVo detail(Integer id) {
        ExpressCompany expressCompany = expressCompanyMapper.selectOne(
                new QueryWrapper<ExpressCompany>()
                        .eq("id",id)
                        .eq("is_delete",0)
                        .last("limit 1"));

        Assert.notNull(expressCompany, "快递公司不存在");

        DeliverCompanyVo deliverCompanyVo = new DeliverCompanyVo();
        BeanUtils.copyProperties(expressCompany, deliverCompanyVo);
        deliverCompanyVo.setImage(UrlUtils.toAbsoluteUrl(expressCompany.getImage()));
        deliverCompanyVo.setCreateTime(TimeUtils.timestampToDate(expressCompany.getCreateTime()));
        return deliverCompanyVo;
    }

    /**
     * 快递新增
     *
     * @author cjh
     * @param deliverCompanyCreateValidate 参数
     */
    @Override
    public void add(DeliverCompanyCreateValidate deliverCompanyCreateValidate){
        ExpressCompany expressCompany = new ExpressCompany();
        expressCompany.setName(deliverCompanyCreateValidate.getName());
        expressCompany.setImage(deliverCompanyCreateValidate.getImage());
        expressCompany.setCodeKd(deliverCompanyCreateValidate.getCodeKd());
        expressCompany.setCodeKd100(deliverCompanyCreateValidate.getCodeKd100());
        expressCompany.setCodeKdniao(deliverCompanyCreateValidate.getCodeKdniao());
        expressCompany.setUpdateTime(TimeUtils.timestamp());
        expressCompany.setCreateTime(TimeUtils.timestamp());
        expressCompanyMapper.insert(expressCompany);
    }

    /**
     * 快递编辑
     *
     * @author cjh
     * @param expressCompanyCreateValidate 参数
     */
    @Override
    public void edit(DeliverCompanyUpdateValidate expressCompanyCreateValidate) {
        ExpressCompany expressCompany = expressCompanyMapper.selectOne(
                new QueryWrapper<ExpressCompany>()
                        .eq("id", expressCompanyCreateValidate.getId())
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(expressCompany, "快递公司不存在!");

        expressCompany.setName(expressCompanyCreateValidate.getName());
        expressCompany.setImage(expressCompanyCreateValidate.getImage());
        expressCompany.setCodeKd(expressCompanyCreateValidate.getCodeKd());
        expressCompany.setCodeKd100(expressCompanyCreateValidate.getCodeKd100());
        expressCompany.setCodeKdniao(expressCompanyCreateValidate.getCodeKdniao());
        expressCompany.setCodeKdniao(expressCompanyCreateValidate.getCodeKdniao());
        expressCompany.setUpdateTime(TimeUtils.timestamp());
        expressCompanyMapper.updateById(expressCompany);
    }

    /**
     * 快递删除
     *
     * @author cjh
     * @param id 主键
     */
    @Override
    public void del(Integer id) {
        ExpressCompany expressCompany = expressCompanyMapper.selectOne(
                new QueryWrapper<ExpressCompany>()
                        .eq("id",id)
                        .eq("is_delete",0)
                        .last("limit 1"));

        Assert.notNull(expressCompany, "快递公司不存在");

        expressCompany.setIsDelete(1);
        expressCompany.setUpdateTime(TimeUtils.timestamp());
        expressCompanyMapper.updateById(expressCompany);
    }

}
