package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.mdd.admin.service.IExpressTplService;
import com.mdd.admin.validate.delivery.DeliverTplCreateValidate;
import com.mdd.admin.validate.delivery.DeliverTplUpdateValidate;
import com.mdd.admin.vo.delivery.DeliverTplListedVo;
import com.mdd.admin.vo.delivery.DeliverTplDetailVo;
import com.mdd.common.entity.delivery.ExpressTpl;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.mapper.delivery.ExpressTplMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.util.TimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * 运费模板服务实现类
 */
@Service
public class ExpressTplServiceImpI implements IExpressTplService {

    @Resource
    ExpressTplMapper expressTplMapper;

    @Resource
    GoodsMapper goodsMapper;

    /**
     * 快递模板列表
     *
     * @author cjh
     * @return List<ExpressTplListVo>
     */
    @Override
    public List<DeliverTplListedVo> list() {
        List<ExpressTpl> expressTplList = expressTplMapper.selectList(
                new QueryWrapper<ExpressTpl>()
                        .eq("is_delete",0)
                        .orderByAsc("id"));

        List<DeliverTplListedVo> list = new LinkedList<>();
        for (ExpressTpl expressTpl : expressTplList) {
            DeliverTplListedVo deliverTplListedVo = new DeliverTplListedVo();
            switch (expressTpl.getType()){
                case 0:
                    deliverTplListedVo.setTypeDesc("件数计费");
                    break;
                case 1:
                    deliverTplListedVo.setTypeDesc("体积计费");
                    break;
                case 2:
                    deliverTplListedVo.setTypeDesc("重量计费");
                    break;
            }

            deliverTplListedVo.setId(expressTpl.getId());
            deliverTplListedVo.setName(expressTpl.getName());
            deliverTplListedVo.setRemark(expressTpl.getRemark());
            deliverTplListedVo.setType(expressTpl.getType());
            deliverTplListedVo.setCreateTime(TimeUtils.timestampToDate(expressTpl.getCreateTime()));
            list.add(deliverTplListedVo);
        }

        return list;
    }

    /**
     * 快递模板详情
     *
     * @author cjh
     * @param id 主键
     * @return ExpressTplVo
     */
    @Override
    public DeliverTplDetailVo detail(Integer id) {
        ExpressTpl expressTpl = expressTplMapper.selectOne(
                new QueryWrapper<ExpressTpl>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(expressTpl, "运费模板不存在!");

        DeliverTplDetailVo deliverTplDetailVo = new DeliverTplDetailVo();
        deliverTplDetailVo.setId(expressTpl.getId());
        deliverTplDetailVo.setName(expressTpl.getName());
        deliverTplDetailVo.setType(expressTpl.getType());
        deliverTplDetailVo.setFirstNum(expressTpl.getFirstNum());
        deliverTplDetailVo.setFirstPrice(expressTpl.getFirstPrice());
        deliverTplDetailVo.setContinueNum(expressTpl.getContinueNum());
        deliverTplDetailVo.setContinuePrice(expressTpl.getContinuePrice());
        deliverTplDetailVo.setRemark(expressTpl.getRemark());
        deliverTplDetailVo.setCreateTime(TimeUtils.timestampToDate(expressTpl.getCreateTime()));
        return deliverTplDetailVo;
    }

    /**
     * 快递模板新增
     *
     * @author cjh
     * @param deliverTplCreateValidate 参数
     */
    @Override
    public void add(DeliverTplCreateValidate deliverTplCreateValidate) {
        ExpressTpl expressTpl = new ExpressTpl();
        expressTpl.setName(deliverTplCreateValidate.getName());
        expressTpl.setType(deliverTplCreateValidate.getType());
        Integer firstNum = deliverTplCreateValidate.getFirstNum();
        Integer continueNum = deliverTplCreateValidate.getContinueNum();
        BigDecimal firstPrice = deliverTplCreateValidate.getFirstPrice();
        BigDecimal continuePrice = deliverTplCreateValidate.getContinuePrice();

        // Todo 后面优化
        switch (deliverTplCreateValidate.getType()){
            case 0:
                Assert.notNull(firstNum,"请输入首件数量");
                Assert.isFalse(firstNum < 0,"首件数量不能小于零");
                Assert.notNull(firstPrice,"请输入运费");
                Assert.isFalse(firstPrice.compareTo(BigDecimal.ZERO) < 0,"运费不能小于零");
                Assert.notNull(continueNum,"请输入运费");
                Assert.isFalse(continueNum < 0,"运费不能小于零");
                Assert.notNull(continuePrice,"请输入续件");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续件不能小于零");
                Assert.notNull(continuePrice,"请输入续费");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续费不能小于零");
                break;
            case 1:
                Assert.notNull(firstNum,"请输入首重");
                Assert.isFalse(firstNum < 0,"首件数量不能小于零");
                Assert.notNull(firstPrice,"请输入运费");
                Assert.isFalse(firstPrice.compareTo(BigDecimal.ZERO) < 0,"运费不能小于零");
                Assert.notNull(continueNum,"请输入运费");
                Assert.isFalse(continueNum < 0,"运费不能小于零");
                Assert.notNull(continuePrice,"请输入续重");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续件不能小于零");
                Assert.notNull(continuePrice,"请输入续费");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续费不能小于零");
                break;
            case 2:
                Assert.notNull(firstNum,"请输入首体积");
                Assert.isFalse(firstNum < 0,"首体积不能小于零");
                Assert.notNull(firstPrice,"请输入运费");
                Assert.isFalse(firstPrice.compareTo(BigDecimal.ZERO) < 0,"运费不能小于零");
                Assert.notNull(continueNum,"请输入运费");
                Assert.isFalse(continueNum < 0,"运费不能小于零");
                Assert.notNull(continuePrice,"请输入续体积");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续件不能小于零");
                Assert.notNull(continuePrice,"请输入续费");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续费不能小于零");
                break;
        }

        expressTpl.setFirstNum(deliverTplCreateValidate.getFirstNum());
        expressTpl.setFirstPrice(deliverTplCreateValidate.getFirstPrice());
        expressTpl.setContinueNum(deliverTplCreateValidate.getContinueNum());
        expressTpl.setContinuePrice(deliverTplCreateValidate.getContinuePrice());
        expressTpl.setRemark(deliverTplCreateValidate.getRemark());
        expressTpl.setCreateTime(TimeUtils.timestamp());
        expressTpl.setUpdateTime(TimeUtils.timestamp());
        expressTplMapper.insert(expressTpl);
    }

    /**
     * 快递模板编辑
     *
     * @author cjh
     * @param deliverTplUpdateValidate 参数
     */
    @Override
    public void edit(DeliverTplUpdateValidate deliverTplUpdateValidate) {
        ExpressTpl expressTpl = expressTplMapper.selectOne(
                new QueryWrapper<ExpressTpl>()
                        .eq("id", deliverTplUpdateValidate.getId())
                        .eq("is_delete", 0));

        Assert.notNull(expressTpl, "运费模板不存在!");

        expressTpl.setName(deliverTplUpdateValidate.getName());
        expressTpl.setType(deliverTplUpdateValidate.getType());
        Integer firstNum = deliverTplUpdateValidate.getFirstNum();
        BigDecimal firstPrice = deliverTplUpdateValidate.getFirstPrice();
        Integer continueNum = deliverTplUpdateValidate.getContinueNum();
        BigDecimal continuePrice = deliverTplUpdateValidate.getContinuePrice();

        // Todo 后面优化
        switch (deliverTplUpdateValidate.getType()){
            case 0:
                Assert.notNull(firstNum,"请输入首件数量!");
                Assert.isFalse(firstNum < 0,"首件数量不能小于零");
                Assert.notNull(firstPrice,"请输入运费");
                Assert.isFalse(firstPrice.compareTo(BigDecimal.ZERO) < 0,"运费不能小于零");
                Assert.notNull(continueNum,"请输入运费");
                Assert.isFalse(continueNum < 0,"运费不能小于零");
                Assert.notNull(continuePrice,"请输入续件");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续件不能小于零");
                Assert.notNull(continuePrice,"请输入续费");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续费不能小于零");
                break;
            case 1:
                Assert.notNull(firstNum,"请输入首重");
                Assert.isFalse(firstNum < 0,"首件数量不能小于零");
                Assert.notNull(firstPrice,"请输入运费");
                Assert.isFalse(firstPrice.compareTo(BigDecimal.ZERO) < 0,"运费不能小于零");
                Assert.notNull(continueNum,"请输入运费");
                Assert.isFalse(continueNum < 0,"运费不能小于零");
                Assert.notNull(continuePrice,"请输入续重");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续件不能小于零");
                Assert.notNull(continuePrice,"请输入续费");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续费不能小于零");
                break;
            case 2:
                Assert.notNull(firstNum,"请输入首体积");
                Assert.isFalse(firstNum < 0,"首体积不能小于零");
                Assert.notNull(firstPrice,"请输入运费");
                Assert.isFalse(firstPrice.compareTo(BigDecimal.ZERO) < 0,"运费不能小于零");
                Assert.notNull(continueNum,"请输入运费");
                Assert.isFalse(continueNum < 0,"运费不能小于零");
                Assert.notNull(continuePrice,"请输入续体积");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续件不能小于零");
                Assert.notNull(continuePrice,"请输入续费");
                Assert.isFalse(continuePrice.compareTo(BigDecimal.ZERO) < 0,"续费不能小于零");
                break;
        }
        expressTpl.setFirstNum(firstNum);
        expressTpl.setFirstPrice(firstPrice);
        expressTpl.setContinueNum(deliverTplUpdateValidate.getContinueNum());
        expressTpl.setContinuePrice(deliverTplUpdateValidate.getContinuePrice());
        expressTpl.setRemark(deliverTplUpdateValidate.getRemark());
        expressTpl.setUpdateTime(TimeUtils.timestamp());
        expressTplMapper.updateById(expressTpl);
    }

    /**
     * 运费模板删除
     *
     * @author cjh
     * @param id 主键
     */
    @Override
    public void del(Integer id) {
        ExpressTpl expressTpl = expressTplMapper.selectOne(
                new QueryWrapper<ExpressTpl>()
                        .eq("id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.notNull(expressTpl, "运费模板不存在!");

        Goods goods = goodsMapper.selectOne(
                new QueryWrapper<Goods>()
                        .eq("express_template_id", id)
                        .eq("is_delete", 0)
                        .last("limit 1"));

        Assert.isNull(goods, "该运费模板正在使用中,不允许删除!");

        expressTpl.setIsDelete(1);
        expressTpl.setUpdateTime(TimeUtils.timestamp());
        expressTplMapper.updateById(expressTpl);
    }
}
