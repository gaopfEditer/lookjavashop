package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.front.service.IOrderAfterService;
import com.mdd.front.validate.common.PageValidate;
import com.mdd.front.validate.order.OrderAfterCreateValidate;
import com.mdd.front.validate.order.OrderAfterDeliveryValidate;
import com.mdd.front.vo.order.*;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.DevRegion;
import com.mdd.common.entity.RefundRecord;
import com.mdd.common.entity.delivery.ExpressCompany;
import com.mdd.common.entity.order.*;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.DevRegionMapper;
import com.mdd.common.mapper.RefundRecordMapper;
import com.mdd.common.mapper.delivery.ExpressCompanyMapper;
import com.mdd.common.mapper.log.LogOrderAfterMapper;
import com.mdd.common.mapper.order.*;
import com.mdd.common.util.ConfigUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单售后服务实现类
 */
@Service
public class OrderAfterServiceImpl implements IOrderAfterService {

    @Resource
    OrderMapper orderMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    OrderAfterGoodsMapper orderAfterGoodsMapper;

    @Resource
    LogOrderAfterMapper logOrderAfterMapper;

    @Resource
    ExpressCompanyMapper expressCompanyMapper;

    @Resource
    OrderAfterReasonMapper orderAfterReasonMapper;

    @Resource
    RefundRecordMapper refundRecordMapper;

    @Resource
    DevRegionMapper devRegionMapper;

    /**
     * 售后列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param userId Integer
     * @param type String
     * @return PageResult<OrderAfterListVo>
     */
    @Override
    public PageResult<OrderAfterListVo> list(PageValidate pageValidate, Integer userId, String type) {
        if (type.equals("apply")) {
            return this.__waitApplyList(pageValidate, userId);
        }
        return this.__afterSaleList(pageValidate, userId, type);
    }

    /**
     * 申请售后
     *
     * @author mjf
     * @param userId Integer
     * @param orderAfterValidate OrderAfterCreateValidate
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderAfterCreateVo add(Integer userId, OrderAfterCreateValidate orderAfterValidate) {
        // 校验是否可以申请售后
        this.__checkAbleApplyOrderAfter(userId, orderAfterValidate);
        // 订单商品ID
        Integer orderGoodsId = orderAfterValidate.getOrderGoodsId();
        // 订单商品信息
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("id", orderGoodsId)
                .last("limit 1"));

        // 生成售后日志记录
        String afterSn = orderAfterMapper.randMakeOrderSn("after_sn");
        OrderAfter orderAfter = new OrderAfter();
        orderAfter.setAfterSn(afterSn);
        orderAfter.setAfterType(OrderAfterEnum.AFTER_TYPE_GOODS.getCode());
        orderAfter.setUserId(userId);
        orderAfter.setOrderId(orderGoods.getOrderId());
        orderAfter.setRefundReason(orderAfterValidate.getRefundReason());
        orderAfter.setRefundRemark(orderAfterValidate.getRefundRemark());
        orderAfter.setRefundMoney(orderAfterValidate.getRefundMoney());
        orderAfter.setRefundType(orderAfterValidate.getRefundType());// 退款类型 [1=仅退款 2=退货退款]'
        orderAfter.setRefundWay(1); // 退款路径 [1=原路退回]
        orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_ING.getCode());
        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode());
        orderAfter.setCreateTime(System.currentTimeMillis() / 1000);
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        // 图片处理
        if (!orderAfterValidate.getRefundImage().isEmpty()) {
            StringBuilder refundImage = new StringBuilder();
            for (String image : orderAfterValidate.getRefundImage()) {
                String imageItem = UrlUtils.toRelativeUrl(image) + ",";
                refundImage.append(imageItem);
            }
            orderAfter.setRefundImage(refundImage.substring(0, refundImage.length() - 1));
        }
        orderAfterMapper.insert(orderAfter);

        // 售后商品记录
        OrderAfterGoods orderAfterGoods = new OrderAfterGoods();
        orderAfterGoods.setOrderId(orderGoods.getOrderId());
        orderAfterGoods.setOrderGoodsId(orderGoods.getId());
        orderAfterGoods.setOrderAfterId(orderAfter.getId());
        orderAfterGoods.setGoodsId(orderGoods.getGoodsId());
        orderAfterGoods.setGoodsSkuId(orderGoods.getGoodsSkuId());
        orderAfterGoods.setGoodsNum(orderGoods.getGoodsNum());
        orderAfterGoods.setGoodsPrice(orderGoods.getGoodsPrice());
        orderAfterGoods.setRefundMoney(orderGoods.getNeedPayMoney());
        orderAfterGoods.setCreateTime(System.currentTimeMillis() / 1000);
        orderAfterGoods.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterGoodsMapper.insert(orderAfterGoods);

        // 售后日志
        logOrderAfterMapper.add(
                LogOrderAfterEnum.TYPE_USER.getCode(),
                userId,
                orderAfter.getId(),
                OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getMsg()
        );

        // 订单商品 更新状态为售后中 [1=无售后, 2=售后中, 3=售后完成]
        orderGoods.setAfterSale(OrderGoodsEnum.AFTER_STATUS_ING.getCode());
        orderGoodsMapper.updateById(orderGoods);

        OrderAfterCreateVo orderAfterCreateVo = new OrderAfterCreateVo();
        orderAfterCreateVo.setId(orderAfter.getId());
        return orderAfterCreateVo;
    }

    /**
     * 售后详情
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     * @return OrderAfterDetailVo
     */
    @Override
    public OrderAfterDetailVo detail(Integer userId, Integer id) {
        // 查询售后记录
        OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                .eq("id", id)
                .eq("user_id", userId)
                .last("limit 1"));

        Assert.notNull(orderAfter, "售后信息不存在");

        // 查询售后商品
        List<OrderAfterGoodsDetailVo> orderAfterGoodsList = orderAfterGoodsMapper.selectJoinList(OrderAfterGoodsDetailVo.class,
                new MPJQueryWrapper<OrderAfterGoods>()
                        .eq("t.order_after_id", orderAfter.getId())
                        .innerJoin("?_order_goods og ON og.id = t.order_goods_id".replace("?_", GlobalConfig.tablePrefix))
                        .select("t.id,t.order_id,t.order_goods_id,og.goods_id,og.goods_name,og.goods_image,og.goods_sku_value," +
                                "og.goods_price,og.goods_num,og.goods_money,og.need_pay_money,og.pay_money"));

        // 实付金额
        BigDecimal payMoney = BigDecimal.ZERO;
        for (OrderAfterGoodsDetailVo goodsDetailVo : orderAfterGoodsList) {
            goodsDetailVo.setGoodsImage(UrlUtils.toAbsoluteUrl(goodsDetailVo.getGoodsImage()));
            payMoney = payMoney.add(goodsDetailVo.getPayMoney());
        }

        // 组装数据
        OrderAfterDetailVo detailVo = new OrderAfterDetailVo();
        BeanUtils.copyProperties(orderAfter, detailVo);
        detailVo.setOrderGoodsId(orderAfterGoodsList.get(0).getOrderGoodsId());
        detailVo.setOrderAfterGoods(orderAfterGoodsList);
        detailVo.setCreateTime(TimeUtils.timestampToDate(orderAfter.getCreateTime()));
        detailVo.setPayMoney(payMoney);
        detailVo.setRefundTypeMsg(OrderAfterEnum.getRefundTypeMsg(detailVo.getRefundType()));
        detailVo.setAfterStatusMsg(OrderAfterEnum.getAfterStatusMsg(detailVo.getAfterStatus()));
        detailVo.setSubStatusMsg(OrderAfterEnum.geSubStatusMsg(detailVo.getSubStatus()));

        // 图片
        if (!orderAfter.getRefundImage().isEmpty()) {
            String[] imageList = orderAfter.getRefundImage().split(",");
            ArrayList<String> imagesLists = new ArrayList<>();
            for (String image : imageList) {
                imagesLists.add(UrlUtils.toAbsoluteUrl(image));
            }
            detailVo.setRefundImage(imagesLists);
        }

        // 退货地址
        Map<String, String> retreatConfig = ConfigUtils.get("retreat");
        String provinceId = retreatConfig.getOrDefault("province_id", "0");
        String cityId =  retreatConfig.getOrDefault("city_id", "0");
        String districtId = retreatConfig.getOrDefault("district_id", "0");

        String region = "";
        if (!provinceId.isEmpty() && !cityId.isEmpty() && !districtId.isEmpty()) {
            region  = getRegion(Integer.parseInt(provinceId), Integer.parseInt(cityId), Integer.parseInt(districtId));
        }
        detailVo.setRetreatConsignee(retreatConfig.getOrDefault("consignee", ""));
        detailVo.setRetreatMobile(retreatConfig.getOrDefault("mobile", ""));
        detailVo.setRetreatRegion(region);
        detailVo.setRetreatAddress(retreatConfig.getOrDefault("address", ""));

        // 操作按钮
        detailVo.setCancelBtn(__cancelBtn(orderAfter.getAfterStatus(), orderAfter.getSubStatus()));
        detailVo.setDeliveryBtn(__deliveryBtn(orderAfter.getAfterStatus(),orderAfter.getSubStatus(), orderAfter.getRefundType()));
        detailVo.setReapplyBtn(__reapplyBtn(orderAfter.getAfterStatus(), orderAfter.getSubStatus()));

        // 退款成功状态
        detailVo.setRefundSuccess(0);

        // 退款成功
        if (orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode())) {
            // 退款记录
            RefundRecord refundRecord = refundRecordMapper.selectOne(new QueryWrapper<RefundRecord>()
                    .eq("sn", orderAfter.getRefundSn())
                    .last("limit 1"));
            if (refundRecord != null && refundRecord.getRefundStatus().equals(RefundEnum.REFUND_SUCCESS.getCode())) {
                Order order =  orderMapper.selectById(orderAfter.getOrderId());
                detailVo.setRefundSuccess(1);
                switch (order.getPayWay()) {
                    case 1:
                        detailVo.setRefundPayWayMsg("退回账户余额");
                        break;
                    case 2:
                        detailVo.setRefundPayWayMsg("退回微信余额");
                        break;
                    case 3:
                        detailVo.setRefundPayWayMsg("退回支付宝余额");
                        break;
                }
            }
        }

        return detailVo;
    }

    /**
     * 撤销售后
     *
     * @author mjf
     * @param userId Integer
     * @param id Integer
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer userId, Integer id) {
        OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                .eq("id", id)
                .last("limit 1"));

        Assert.notNull(orderAfter, "售后信息不存在");

        // 校验售后状态
        if (orderAfter.getAfterStatus() != OrderAfterEnum.AFTER_STATUS_ING.getCode()) {
            throw new OperateException("不是售后中状态,不允许撤销申请");
        }

        // 校验售后子状态
        if (orderAfter.getSubStatus() != OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getCode()
                && orderAfter.getSubStatus() != OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode()) {
            throw new OperateException("售后处理中,不允许撤销申请");
        }

        // 更新订单商品售后状态 [1=无售后, 2=售后中, 3=售后完成]
        OrderGoods orderGoods = new OrderGoods();
        orderGoods.setAfterSale(OrderGoodsEnum.AFTER_STATUS_NO.getCode());
        orderGoodsMapper.update(orderGoods, new UpdateWrapper<OrderGoods>()
                .eq("order_id", orderAfter.getOrderId()));

        // 更新售后状态
        orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_CLOSE.getCode());
        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_CLOSE_BUYER_CANCEL.getCode());
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterMapper.updateById(orderAfter);

        // 售后日志
        logOrderAfterMapper.add(
                LogOrderAfterEnum.TYPE_USER.getCode(),
                userId,
                orderAfter.getId(),
                OrderAfterEnum.SUB_STATUS_CLOSE_BUYER_CANCEL.getMsg()
        );
    }

    /**
     * 买家退货
     *
     * @author mjf
     * @param userId Integer
     * @param deliveryValidate OrderAfterDeliveryValidate
     */
    @Override
    public void delivery(Integer userId, OrderAfterDeliveryValidate deliveryValidate) {
        OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                .eq("id", deliveryValidate.getAfterId())
                .last("limit 1"));

        Assert.notNull(orderAfter, "售后记录不存在");

        if (orderAfter.getSubStatus() != OrderAfterEnum.SUB_STATUS_ING_WAIT_BUYER_RETURN.getCode()) {
            throw new OperateException("当前售后状态不可进行退货操作");
        }

        ExpressCompany expressCompany = expressCompanyMapper.selectOne(new QueryWrapper<ExpressCompany>()
                .eq("id", deliveryValidate.getExpressId())
                .last("limit 1"));

        // 更新售后，记录日志
        orderAfter.setInvoiceNo(deliveryValidate.getInvoiceNo());
        orderAfter.setExpressId(deliveryValidate.getExpressId());
        orderAfter.setExpressName(expressCompany.getName());
        orderAfter.setExpressContact(deliveryValidate.getExpressContact());
        orderAfter.setExpressRemark(deliveryValidate.getExpressRemark());
        orderAfter.setExpressTime(System.currentTimeMillis() / 1000);
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getCode());
        orderAfterMapper.updateById(orderAfter);

        // 售后日志
        logOrderAfterMapper.add(
                LogOrderAfterEnum.TYPE_USER.getCode(),
                userId,
                orderAfter.getId(),
                OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getMsg()
        );
    }

    /**
     * 订单商品信息
     *
     * @author mjf
     * @param orderGoodsId Integer
     * @return OrderAfterInfoVo
     */
    @Override
    public OrderAfterInfoVo orderInfo(Integer userId, Integer orderGoodsId) {
        // 物流公司信息
        List<ExpressCompany> expressCompany = expressCompanyMapper.selectList(new QueryWrapper<ExpressCompany>()
                .eq("is_delete", 0)
                .orderByDesc(Arrays.asList("sort", "id")));

        List<OrderAfterExpressVo> expressVoList = new ArrayList<>();
        for (ExpressCompany express : expressCompany) {
            OrderAfterExpressVo expressVo = new OrderAfterExpressVo();
            expressVo.setId(express.getId());
            expressVo.setName(express.getName());
            expressVoList.add(expressVo);
        }

        // 售后原因
        List<OrderAfterReason> reasonList = orderAfterReasonMapper.selectList(new QueryWrapper<OrderAfterReason>()
                .select("id,type,content")
                .eq("is_show", 1)
                .eq("is_delete", 0)
                .orderByDesc(Arrays.asList("sort", "id")));

        List<String> onlyRefundReasonList = new ArrayList<>();
        List<String> refundAndReturnReason = new ArrayList<>();
        for (OrderAfterReason reason : reasonList) {
            if (reason.getType() == 1) {
                onlyRefundReasonList.add(reason.getContent());
            } else {
                refundAndReturnReason.add(reason.getContent());
            }
        }

        // 售后商品信息
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("id", orderGoodsId)
                .eq("user_id", userId)
                .last("limit 1"));

        Assert.notNull(orderGoods, "订单信息不存在");

        // 不可退运费
        BigDecimal expressMoney =  orderGoods.getExpressMoney();

        // 可退款金额
        BigDecimal refundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getCouponMoney());
        // 可退款金额(未发货需退回运费，已发货不退运费)
        Order order = orderMapper.selectById(orderGoods.getOrderId());
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) {
            refundMoney = refundMoney.add(orderGoods.getExpressMoney());
            expressMoney = BigDecimal.ZERO;
        }

        // 组装数据返回
        OrderAfterInfoVo orderAfterInfoVo = new OrderAfterInfoVo();
        orderAfterInfoVo.setOrderGoodsId(orderGoodsId);
        orderAfterInfoVo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
        orderAfterInfoVo.setGoodsId(orderGoods.getGoodsId());
        orderAfterInfoVo.setGoodsName(orderGoods.getGoodsName());
        orderAfterInfoVo.setGoodsSkuValue(orderGoods.getGoodsSkuValue());
        orderAfterInfoVo.setGoodsNum(orderGoods.getGoodsNum());
        orderAfterInfoVo.setExpressMoney(expressMoney);
        orderAfterInfoVo.setPayMoney(orderGoods.getPayMoney());
        orderAfterInfoVo.setRefundMoney(refundMoney);
        orderAfterInfoVo.setExpress(expressVoList);
        orderAfterInfoVo.setOnlyRefundReason(onlyRefundReasonList);
        orderAfterInfoVo.setRefundAndReturnReason(refundAndReturnReason);
        return orderAfterInfoVo;
    }

    /**
     * 校验能都申请售后
     *
     * @author mjf
     * @param userId Integer
     * @param orderAfterValidate OrderAfterCreateValidate
     */
    private void __checkAbleApplyOrderAfter(Integer userId,  OrderAfterCreateValidate orderAfterValidate) {
        Integer orderGoodsId = orderAfterValidate.getOrderGoodsId();
        BigDecimal refundMoney = orderAfterValidate.getRefundMoney();

        // 校验是否可以申请售后
        OrderGoods orderGoods = orderGoodsMapper.selectOne(new QueryWrapper<OrderGoods>()
                .eq("id", orderGoodsId)
                .eq("user_id", userId)
                .last("limit 1"));

        Assert.notNull(orderGoods, "订单商品信息不存在");

        Order order = orderMapper.selectOne(new QueryWrapper<Order>()
                .eq("id", orderGoods.getOrderId())
                .last("limit 1"));

        Assert.notNull(order, "订单信息不存在");

        // 可退款金额
        BigDecimal ableRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getCouponMoney());
        // 区分订单是否已发货
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) {
            if (refundMoney.compareTo(ableRefundMoney.add(orderGoods.getExpressMoney())) > 0) {
                throw new OperateException("退款金额不可大于商品金额");
            }
        } else {
            if (refundMoney.compareTo(ableRefundMoney) > 0) {
                throw new OperateException("退款金额不可大于商品金额");
            }
        }

        // 订单-已完成 是否开启售后和已过售后时间
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_COMPLETED.getCode()) {
            float afterSalesDay = Float.parseFloat(ConfigUtils.get("trade", "afterSalesDay", "-1"));
            // 售后已关闭
            if (afterSalesDay == -1) {
                throw new OperateException("系统已关闭售后维权");
            }
            // 确认收货后已过发起售后时间
            long afterSaleDeadline = (long) (afterSalesDay * 24 * 60 * 60) + order.getConfirmTime();
            if (TimeUtils.timestamp() > afterSaleDeadline) {
                throw new OperateException("订单已过售后时间，无法发起商品售后");
            }
        }

        // 订单-未支付,已取消,不可售后
        if (order.getOrderStatus() == OrderEnum.ORDER_STATUS_WAIT_PAY.getCode()
                || order.getOrderStatus() == OrderEnum.ORDER_STATUS_CANCEL.getCode()) {
            throw new OperateException("当前订单商品不可申请售后");
        }

        // 校验是否已有售后记录 售后中或售后成功的
        OrderAfterGoods orderAfterGoods = orderAfterGoodsMapper.selectOne(new QueryWrapper<OrderAfterGoods>()
                .eq("order_goods_id", orderGoods.getId())
                .orderByDesc("id")
                .last("limit 1"));

        if (orderAfterGoods != null) {
            // 重新申请
            OrderAfter orderAfter = orderAfterMapper.selectOne(new QueryWrapper<OrderAfter>()
                    .eq("order_id", orderAfterGoods.getOrderId())
                    .orderByDesc("id")
                    .last("limit 1"));

            // 重新申请售后时，售后状态需为售后中或已关闭
            if (orderAfter.getAfterStatus() == OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode()) {
                throw new OperateException("已有售后成功记录，不可申请");
            }

            // 重新申请时，将旧的售后记录状态更新为售后关闭
            orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_CLOSE.getCode());
            orderAfterMapper.updateById(orderAfter);
        }
    }

    /**
     * 售后列表-未申请
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param userId Integer
     * @return PageResult<OrderAfterListVo>
     */
    private PageResult<OrderAfterListVo> __waitApplyList(PageValidate pageValidate, Integer userId) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<OrderGoods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.innerJoin("?_order o ON o.id=t.order_id".replace("?_", GlobalConfig.tablePrefix))
                .selectAll(OrderGoods.class)
                .select("o.create_time as order_create_time, o.confirm_time as order_confirm_time, t.id as order_goods_id")
                .eq("t.after_sale", OrderGoodsEnum.AFTER_STATUS_NO.getCode())
                .eq("o.user_id", userId)
                .eq("o.is_delete", 0)
                .eq("o.pay_is", PaymentEnum.OK_PAID.getCode())
                .gt("o.order_status", OrderEnum.ORDER_STATUS_WAIT_PAY.getCode())
                .lt("o.order_status", OrderEnum.ORDER_STATUS_CANCEL.getCode())
                .orderByDesc("o.create_time");

        IPage<OrderAfterListVo> iPage = orderGoodsMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                OrderAfterListVo.class,
                mpjQueryWrapper);

        for (OrderAfterListVo listVo : iPage.getRecords()) {
            listVo.setGoodsImage(UrlUtils.toAbsoluteUrl(listVo.getGoodsImage()));
            // 申请售后按钮
            listVo.setApplyBtn(__applyBtn(listVo.getOrderConfirmTime(), listVo.getOrderCreateTime()));
            listVo.setCancelBtn(0);
            listVo.setDeliveryBtn(0);
            listVo.setReapplyBtn(0);
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 售后列表-售后记录
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param userId Integer
     * @param type String
     * @return PageResult<OrderAfterListVo>
     */
    private PageResult<OrderAfterListVo> __afterSaleList(PageValidate pageValidate, Integer userId, String type) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<OrderAfterGoods> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.innerJoin("?_order_after oa ON oa.id=t.order_after_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_order_goods og ON og.id=t.order_goods_id".replace("?_", GlobalConfig.tablePrefix))
                .select("t.order_after_id as after_id,og.goods_image,t.order_goods_id,og.goods_name,og.goods_sku_value,og.goods_price,og.goods_num," +
                        "oa.create_time as after_create_time, oa.refund_type,oa.after_status,oa.sub_status")
                .eq("oa.user_id", userId)
                .eq("oa.is_delete", 0)
                .orderByDesc("t.id");

        if (type.equals("ing")) {
            mpjQueryWrapper.eq("oa.after_status", OrderAfterEnum.AFTER_STATUS_ING.getCode());
        }

        IPage<OrderAfterListVo> iPage = orderAfterGoodsMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                OrderAfterListVo.class,
                mpjQueryWrapper);

        for (OrderAfterListVo listVo : iPage.getRecords()) {
            listVo.setRefundTypeMsg(OrderAfterEnum.getRefundTypeMsg(listVo.getRefundType()));
            listVo.setAfterStatusMsg(OrderAfterEnum.getAfterStatusMsg(listVo.getAfterStatus()));
            listVo.setSubStatusMsg(OrderAfterEnum.geSubStatusMsg(listVo.getSubStatus()));
            listVo.setAfterCreateTime(TimeUtils.timestampToDate(listVo.getAfterCreateTime()));
            listVo.setGoodsImage(UrlUtils.toAbsoluteUrl(listVo.getGoodsImage()));
            listVo.setApplyBtn(0);
            // 撤销按钮
            listVo.setCancelBtn(__cancelBtn(listVo.getAfterStatus(), listVo.getSubStatus()));
            // 填写单号按钮(买家退货)
            listVo.setDeliveryBtn(__deliveryBtn(listVo.getAfterStatus(), listVo.getSubStatus(), listVo.getRefundType()));
            // 重新申请按钮
            listVo.setReapplyBtn(__reapplyBtn(listVo.getAfterStatus(), listVo.getSubStatus()));
        }
        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }

    /**
     * 申请售后按钮
     *
     * @author mjf
     * @param orderConfirmTime Long
     * @param orderCreateTime Long
     * @return Integer
     */
    private Integer __applyBtn(Long orderConfirmTime, Long orderCreateTime) {

        // 买家售后维权时效: -1=关闭,否则开启(天) 此配置针对已确认收货订单有效
        float afterSalesDay = Float.parseFloat(ConfigUtils.get("trade", "afterSalesDay", "-1"));
        long ableAfterSalesTime = afterSalesDay != -1 ? (long) (afterSalesDay * 24 * 60 * 60) : 0;

        // 订单允许取消时长: -1=关闭,否则开启(分钟)
        float cancelUnshippedOrderTime = Float.parseFloat(ConfigUtils.get("trade", "cancelUnshippedOrderTime", "-1"));
        long ableCancelTime = cancelUnshippedOrderTime != -1 ? (long) (cancelUnshippedOrderTime * 60) : 0;

        // 申请售后按钮
        int btn = 0;

        if (orderConfirmTime > 0) {
            // 已确认收货
            if (afterSalesDay == -1) {
                return btn;
            }
            btn = 1;
            // 已过售后期的则置灰按钮
            if (TimeUtils.timestamp() > (orderConfirmTime + ableAfterSalesTime)) {
                btn = 2;
            }
        } else {
            // 已支付未确认收货订单 当前时间大于允许取消订单时间则 显示售后按钮
            if (TimeUtils.timestamp() > orderCreateTime + ableCancelTime) {
                btn = 1;
            }
        }

        return btn;
    }

    /**
     * 撤销售后按钮
     *
     * @author mjf
     * @param subStatus Integer
     * @return Integer
     */
    private Integer __cancelBtn(Integer afterStatus, Integer subStatus) {
        int btn = 0;

        if (!afterStatus.equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            return btn;
        }

        // 撤销按钮
        if (subStatus == OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode()
                || subStatus == OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getCode()
                || subStatus == OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT.getCode()) {
            btn = 1;
        }
        return btn;
    }

    /**
     * 重新申请售后
     *
     * @author mjf
     * @param afterStatus Integer
     * @param subStatus Integer
     * @return Integer
     */
    private Integer __reapplyBtn(Integer afterStatus, Integer subStatus) {
        int btn = 0;

        if (!afterStatus.equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            return btn;
        }

        // 重新申请按钮
        if (subStatus == OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getCode()
                || subStatus == OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT.getCode()) {
            btn = 1;
        }

        return btn;
    }

    /**
     * 填写单号按钮
     *
     * @author mjf
     * @param subStatus Integer
     * @return Integer
     */
    private Integer __deliveryBtn(Integer afterStatus, Integer subStatus, Integer refundType) {
        int btn = 0;

        if (!afterStatus.equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            return btn;
        }

        if (refundType.equals(OrderAfterEnum.METHOD_ONLY_REFUND.getCode())) {
            return btn;
        }

        if (subStatus == OrderAfterEnum.SUB_STATUS_ING_WAIT_BUYER_RETURN.getCode()) {
            btn = 1;
        }

        return btn;
    }

    /**
     * 获取省市区
     *
     * @author mjf
     * @param provinceId Integer
     * @param cityId Integer
     * @param districtId Integer
     * @return String
     */
    private String getRegion(Integer provinceId, Integer cityId, Integer districtId) {

        List<DevRegion> regionList = devRegionMapper.selectList(new QueryWrapper<DevRegion>()
                .in("id", Arrays.asList(provinceId, cityId, districtId)));

        String provinceName = "";
        String cityName = "";
        String districtName = "";

        for (DevRegion devRegion : regionList) {
            if (1 == devRegion.getLevel()) {
                provinceName = devRegion.getName();
            }
            if (2 == devRegion.getLevel()) {
                cityName = devRegion.getName();
            }
            if (3 == devRegion.getLevel()) {
                districtName = devRegion.getName();
            }
        }
        return provinceName + cityName + districtName;
    }

}
