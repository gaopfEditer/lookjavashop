package com.mdd.admin.service.impl;

import com.alipay.api.response.AlipayTradeRefundResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IOrderAfterService;
import com.mdd.admin.service.IOrderManageService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.order.OrderAfterSearchValidate;
import com.mdd.admin.vo.order.OrderAfterDetailVo;
import com.mdd.admin.vo.order.OrderAfterListedVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.RefundLog;
import com.mdd.common.entity.RefundRecord;
import com.mdd.common.entity.goods.Goods;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.entity.log.LogOrder;
import com.mdd.common.entity.log.LogOrderAfter;
import com.mdd.common.entity.order.Order;
import com.mdd.common.entity.order.OrderAfter;
import com.mdd.common.entity.order.OrderAfterGoods;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.entity.system.SystemAuthAdmin;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.*;
import com.mdd.common.exception.OperateException;
import com.mdd.common.mapper.RefundLogMapper;
import com.mdd.common.mapper.RefundRecordMapper;
import com.mdd.common.mapper.goods.GoodsMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.mapper.log.LogMoneyMapper;
import com.mdd.common.mapper.log.LogOrderAfterMapper;
import com.mdd.common.mapper.log.LogOrderMapper;
import com.mdd.common.mapper.order.OrderAfterGoodsMapper;
import com.mdd.common.mapper.order.OrderAfterMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.order.OrderMapper;
import com.mdd.common.mapper.system.SystemAuthAdminMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.plugin.alipay.AliPayDriver;
import com.mdd.common.plugin.alipay.request.AliPayRefundRequest;
import com.mdd.common.plugin.notice.NoticeDriver;
import com.mdd.common.plugin.notice.vo.NoticeSmsVo;
import com.mdd.common.plugin.wechat.WxPayDriver;
import com.mdd.common.plugin.wechat.request.RefundRequestV3;
import com.mdd.common.util.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
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
    GoodsMapper goodsMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;
    @Resource
    OrderAfterMapper orderAfterMapper;

    @Resource
    OrderAfterGoodsMapper orderAfterGoodsMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    OrderMapper orderMapper;

    @Resource
    LogOrderMapper logOrderMapper;

    @Resource
    LogOrderAfterMapper logOrderAfterMapper;

    @Resource
    RefundRecordMapper refundRecordMapper;

    @Resource
    RefundLogMapper refundLogMapper;

    @Resource
    LogMoneyMapper logMoneyMapper;

    @Resource
    SystemAuthAdminMapper systemAuthAdminMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    DataSourceTransactionManager transactionManager ;

    @Resource
    TransactionDefinition transactionDefinition;
    @Resource
    IOrderManageService iOrderManageService;

    /**
     * 售后列表
     *
     * @author fzr
     * @param pageValidate 分页参数
     * @param searchValidate 搜索参数
     * @return PageResult<OrderAfterListedVo>
     */
    @Override
    public PageResult<OrderAfterListedVo> list(PageValidate pageValidate, OrderAfterSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();

        MPJQueryWrapper<OrderAfter> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper
                .leftJoin("?_order O ON t.order_id=O.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_user U ON t.user_id=U.id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.is_delete", 0)
                .orderByDesc("t.id");

        // 搜索条件
        if (StringUtils.isNotNull(searchValidate.getType())) {
            searchValidate.setType(searchValidate.getType().equals(0) ? null : searchValidate.getType());
        }
        orderAfterMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "like:afterSn@t.after_sn:str",
                "like:orderSn@O.order_sn:str",
                "like:nickname@U.nickname:str",
                "=:refundType@t.refund_type:int",
                "=:afterType@t.after_type:int",
                "=:subStatus@t.sub_status:int",
                "=:orderStatus@O.order_status:int",
                "datetime:startTime-endTime@t.create_time:long"
        });

        // 商品搜索
        if (StringUtils.isNotEmpty(searchValidate.getGoodsName())) {
            String goodsName = searchValidate.getGoodsName();
            List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(
                    new QueryWrapper<OrderGoods>()
                            .select("id")
                            .like("goods_name", goodsName));

            List<Integer> orderGoodsIds = new LinkedList<>();
            for (OrderGoods orderGoods : orderGoodsList) {
                orderGoodsIds.add(orderGoods.getId());
            }

            List<Integer> afterGoodsIds = new LinkedList<>();
            orderGoodsIds.add(0);
            afterGoodsIds.add(0);

            List<OrderAfterGoods> afterGoodsList = orderAfterGoodsMapper.selectList(
                    new QueryWrapper<OrderAfterGoods>()
                            .select("order_after_id")
                            .in("order_goods_id", orderGoodsIds));

            for (OrderAfterGoods orderAfterGoods : afterGoodsList) {
                afterGoodsIds.add(orderAfterGoods.getOrderAfterId());
            }

            mpjQueryWrapper.in("t.id", afterGoodsIds);
        }

        // 数据统计
        Map<String, Object> extend = new LinkedHashMap<>();
        extend.put("afterSubStatus", OrderAfterEnum.getAfterSubStatusList());
        Map<String, Object> statistics = new LinkedHashMap<>();
        MPJQueryWrapper<OrderAfter> allMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<OrderAfter> ingMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<OrderAfter> successMpj = mpjQueryWrapper.clone();
        MPJQueryWrapper<OrderAfter> closeMpj = mpjQueryWrapper.clone();
        statistics.put("all", orderAfterMapper.selectCount(allMpj.select("t.id")));
        statistics.put("ing", orderAfterMapper.selectCount(ingMpj.select("t.id").eq("t.after_status", 1)));
        statistics.put("success", orderAfterMapper.selectCount(successMpj.select("t.id").eq("t.after_status", 2)));
        statistics.put("close", orderAfterMapper.selectCount(closeMpj.select("t.id").eq("t.after_status", 3)));
        extend.put("statistics", statistics);

        if (StringUtils.isNotNull(searchValidate.getType())) {
            mpjQueryWrapper.eq("t.after_status", searchValidate.getType());
        }

        mpjQueryWrapper.selectAll(OrderAfter.class)
                .select("U.nickname,U.avatar,U.sn as user_sn,O.order_sn,O.pay_money");

        // 发起查询
        IPage<OrderAfterListedVo> iPage = orderAfterMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                OrderAfterListedVo.class,
                mpjQueryWrapper);

        // 处理数据
        for (OrderAfterListedVo vo : iPage.getRecords()) {
            // 查询售后商品
            MPJQueryWrapper<OrderAfterGoods> goodsMPJQueryWrapper = new MPJQueryWrapper<>();
            goodsMPJQueryWrapper
                    .select("t.order_goods_id,OG.goods_name,OG.goods_image,OG.goods_num,OG.goods_sku_value,OG.goods_price,OG.pay_money")
                    .eq("t.order_after_id", vo.getId())
                    .leftJoin("?_order_goods OG ON t.order_goods_id=OG.id".replace("?_", GlobalConfig.tablePrefix));

            int refundNum = 0;
            List<Map<String, Object>> goodsList = new LinkedList<>();
            List<Map<String, Object>> afterGoods = orderAfterGoodsMapper.selectMaps(goodsMPJQueryWrapper);
            for (Map<String, Object> map : afterGoods) {
                Map<String, Object> goodsInfo = new LinkedHashMap<>();
                goodsInfo.put("orderGoodsId", map.get("order_goods_id"));
                goodsInfo.put("goodsName", map.get("goods_name"));
                goodsInfo.put("goodsImage", UrlUtils.toAbsoluteUrl(map.get("goods_image").toString()));
                goodsInfo.put("goodsNum", map.get("goods_num"));
                goodsInfo.put("goodsPrice", map.get("goods_price"));
                goodsInfo.put("goodsSkuValue", map.get("goods_sku_value"));
                goodsInfo.put("payMoney", map.get("pay_money"));
                refundNum += Integer.parseInt(map.get("goods_num").toString());
                goodsList.add(goodsInfo);
            }

            // 处理售后数据
            vo.setGoodsInfo(goodsList);
            vo.setRefundNum(refundNum);
            vo.setAvatar(UrlUtils.toAbsoluteUrl(vo.getAvatar()));
            vo.setAfterTypeMsg(OrderAfterEnum.getAfterTypeMsg(vo.getAfterType()));
            vo.setRefundTypeMsg(OrderAfterEnum.getRefundTypeMsg(vo.getRefundType()));
            vo.setAfterStatusMsg(OrderAfterEnum.getAfterStatusMsg(vo.getAfterStatus()));
            vo.setCreateTime(TimeUtils.timestampToDate(vo.getCreateTime()));
            vo.setSubStatusMsg(OrderAfterEnum.geSubStatusMsg(vo.getSubStatus()));
            vo.setAfterBtn(this.handleAfterBtn(vo.getSubStatus(), vo.getRefundType()));
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords(), extend);
    }

    /**
     * 售后详情
     *
     * @author fzr
     * @param id 售后ID
     * @return OrderAfterDetailVo
     */
    @Override
    public OrderAfterDetailVo detail(Integer id) {
        // 售后信息
        OrderAfter orderAfter = orderAfterMapper.selectById(id);
        Assert.notNull(orderAfter, "售后记录不存在");

        List<String> refundImage = new LinkedList<>();
        List<String> images = ListUtils.stringToListAsStr(orderAfter.getRefundImage(), ",");
        for (String s : images) {
            refundImage.add(UrlUtils.toAbsoluteUrl(s));
        }

        // 用户信息
        User userInfo = userMapper.selectById(orderAfter.getUserId());

        // 订单信息
        Order order = orderMapper.selectById(orderAfter.getOrderId());

        // 售后商品
        MPJQueryWrapper<OrderAfterGoods> goodsMPJQueryWrapper = new MPJQueryWrapper<>();
        goodsMPJQueryWrapper.selectAll(OrderAfterGoods.class)
                .select("OG.goods_name,OG.goods_code,OG.goods_image,OG.goods_num,OG.goods_money," +
                        "OG.goods_price,OG.pay_money,OG.goods_sku_value,O.express_money")
                .eq("t.order_after_id", id)
                .leftJoin("?_order O ON t.order_id=O.id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_order_goods OG ON t.order_goods_id=OG.id".replace("?_", GlobalConfig.tablePrefix));

        List<Map<String, Object>> afterGoodsList = new LinkedList<>();
        List<Map<String, Object>> afterGoods = orderAfterGoodsMapper.selectJoinMaps(goodsMPJQueryWrapper);
        for (Map<String, Object> item : afterGoods) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("orderId", item.get("order_id"));
            map.put("goodsId", item.get("goods_id"));
            map.put("goodsName", item.get("goods_name"));
            map.put("goodsCode", item.get("goods_code"));
            map.put("goodsSkuValue", item.get("goods_sku_value"));
            map.put("goodsImage", UrlUtils.toAbsoluteUrl(item.get("goods_image").toString()));
            map.put("goodsNum", item.get("goods_num"));
            map.put("goodsMoney", item.get("goods_money"));
            map.put("goodsPrice", item.get("goods_price"));
            map.put("expressMoney", item.get("express_money"));
            map.put("payMoney", item.get("pay_money"));
            afterGoodsList.add(map);
        }

        // 售后日志
        List<Map<String, Object>> logOrderList = new LinkedList<>();
        List<LogOrderAfter> orderAfterLogList = logOrderAfterMapper.selectList(new QueryWrapper<LogOrderAfter>().eq("order_after_id", orderAfter.getId()));
        // 售后日志操作人名称
        for (LogOrderAfter orderAfterLog : orderAfterLogList) {
            String uname = "系统";
            switch (orderAfterLog.getType()) {
                case 2:
                    SystemAuthAdmin systemAuthAdmin = systemAuthAdminMapper.selectById(orderAfterLog.getOperatorId());
                    uname = StringUtils.isNotNull(systemAuthAdmin) ? systemAuthAdmin.getNickname() : "-";
                    break;
                case 3:
                    User user = userMapper.selectById(orderAfterLog.getOperatorId());
                    uname = user.getNickname();
                    break;
            }

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("operatorUser", uname);
            m.put("content", orderAfterLog.getContent());
            m.put("create_time", TimeUtils.timestampToDate(orderAfterLog.getCreateTime()));
            logOrderList.add(m);
        }

        // 返回数据
        OrderAfterDetailVo vo = new OrderAfterDetailVo();
        vo.setId(orderAfter.getId());
        vo.setOrderSn(order.getOrderSn());
        vo.setUserId(userInfo.getId());
        vo.setNickname(userInfo.getNickname());
        vo.setAfterSn(orderAfter.getAfterSn());
        vo.setRefundRemark(orderAfter.getRefundRemark());
        vo.setAfterStatus(orderAfter.getAfterStatus());
        vo.setSubStatus(orderAfter.getSubStatus());
        vo.setAfterStatusMsg(OrderAfterEnum.getAfterStatusMsg(orderAfter.getAfterStatus()));
        vo.setSubStatusMsg(OrderAfterEnum.geSubStatusMsg(orderAfter.getSubStatus()));
        vo.setRefundTypeMsg(OrderAfterEnum.getRefundTypeMsg(orderAfter.getRefundType()));
        vo.setAfterTypeMsg(OrderAfterEnum.getAfterTypeMsg(orderAfter.getAfterType()));
        vo.setRefundMoney(orderAfter.getRefundMoney());
        vo.setRefundReason(orderAfter.getRefundReason());
        vo.setHandleRemark(orderAfter.getHandleRemark());
        vo.setCreateTime(TimeUtils.timestampToDate(orderAfter.getCreateTime()));
        vo.setRefundReason(orderAfter.getRefundReason());
        vo.setRefundImage(refundImage);
        vo.setAfterBtn(this.handleAfterBtn(orderAfter.getSubStatus(), orderAfter.getRefundType()));
        vo.setRefundGoodsList(afterGoodsList);
        vo.setLogOrderList(logOrderList);
        return vo;
    }

    /**
     * 卖家同意售后
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     * @param remarks 备注
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void agree(Integer adminId, Integer id, String remarks) {
        OrderAfter orderAfter = orderAfterMapper.selectById(id);

        if (StringUtils.isNull(orderAfter)) {
            throw new OperateException("售后记录不存在");
        }

        if (!orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            throw new OperateException("不在售后中状态,不能进行同意售后操作");
        }

        if (!orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode())) {
            throw new OperateException("不是等待卖家同意状态,不能进行同意售后操作");
        }

        Integer roleSeller = LogOrderAfterEnum.TYPE_ADMIN.getCode();
        switch (orderAfter.getRefundType()) {
            case 1: // 仅退款
                orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND.getCode());
                logOrderAfterMapper.add(roleSeller, adminId, id, "卖家已同意,等待退款");
                break;
            case 2: // 退货退款
                orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_WAIT_BUYER_RETURN.getCode());
                logOrderAfterMapper.add(roleSeller, adminId, id, "卖家已同意售后,等待买家退货");
                break;
        }

        orderAfter.setHandleId(adminId);
        orderAfter.setHandleRemark(remarks);
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterMapper.updateById(orderAfter);
    }

    /**
     * 卖家拒绝售后
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param remarks 备注
     * @param id 售后ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuse(Integer adminId, Integer id, String remarks) {
        OrderAfter orderAfter = orderAfterMapper.selectById(id);

        if (StringUtils.isNull(orderAfter)) {
            throw new OperateException("售后记录不存在");
        }

        if (!orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            throw new OperateException("不在售后中状态,不能进行拒绝售后操作");
        }

        if (!orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode())) {
            throw new OperateException("不是等待卖家同意状态,不能进行拒绝售后操作");
        }

        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_APPLY.getCode());
        orderAfter.setHandleId(adminId);
        orderAfter.setHandleRemark(remarks);
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterMapper.updateById(orderAfter);

        List<OrderAfterGoods> orderAfterGoods = orderAfterGoodsMapper.selectList(
                new QueryWrapper<OrderAfterGoods>()
                    .eq("order_after_id", orderAfter.getId()));

        for (OrderAfterGoods afterGoods : orderAfterGoods) {
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setAfterSale(4);
            orderGoods.setUpdateTime(System.currentTimeMillis() / 1000);
            orderGoodsMapper.update(orderGoods, new QueryWrapper<OrderGoods>().eq("id", afterGoods.getOrderGoodsId()));
        }

        Integer roleSeller = LogOrderAfterEnum.TYPE_ADMIN.getCode();
        logOrderAfterMapper.add(roleSeller, adminId, id, "卖家拒绝售后");

        // 通知
        Order order = orderMapper.selectById(orderAfter.getOrderId());
        this.afterSaleNotice(order, orderAfter, NoticeEnum.REFUND_REFUSE_NOTICE.getCode());
    }

    /**
     * 卖家拒绝收货
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuseGoods(Integer adminId, Integer id) {
        OrderAfter orderAfter = orderAfterMapper.selectById(id);

        if (StringUtils.isNull(orderAfter)) {
            throw new OperateException("售后记录不存在");
        }

        if (!orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            throw new OperateException("不是售后中状态，不能进行拒绝收货操作");
        }

        if (!orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getCode())) {
            throw new OperateException("不是等待卖家收货状态，不允许进行拒绝收货操作");
        }

        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_CLOSE_SELLER_REFUSE_RECEIPT.getCode());
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterMapper.updateById(orderAfter);

        List<OrderAfterGoods> orderAfterGoods = orderAfterGoodsMapper.selectList(
                new QueryWrapper<OrderAfterGoods>()
                        .eq("order_after_id", orderAfter.getId()));

        for (OrderAfterGoods afterGoods : orderAfterGoods) {
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setUpdateTime(System.currentTimeMillis() / 1000);
            orderGoods.setAfterSale(4);
            orderGoodsMapper.update(orderGoods, new QueryWrapper<OrderGoods>().eq("id", afterGoods.getOrderGoodsId()));
        }

        Integer roleSeller = LogOrderAfterEnum.TYPE_ADMIN.getCode();
        logOrderAfterMapper.add(roleSeller, adminId, id, "卖家拒绝收货, 售后失败");
    }

    /**
     * 卖家确认收货
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmGoods(Integer adminId, Integer id) {
        OrderAfter orderAfter = orderAfterMapper.selectById(id);

        if (StringUtils.isNull(orderAfter)) {
            throw new OperateException("售后记录不存在");
        }

        if (!orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            throw new OperateException("不是售后中状态，不能进行确认收货操作");
        }

        if (!orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getCode())) {
            throw new OperateException("不是等待卖家收货状态，不允许进行确认收货操作");
        }

        orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND.getCode());
        orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
        orderAfterMapper.updateById(orderAfter);

        Integer roleSeller = LogOrderAfterEnum.TYPE_ADMIN.getCode();
        logOrderAfterMapper.add(roleSeller, adminId, id, "卖家确认收货，等待卖家处理");
    }

    /**
     * 卖家确认退款
     *
     * @author fzr
     * @param adminId 管理员ID
     * @param id 售后ID
     */
    @Override
    public void confirmRefund(Integer adminId, Integer id) {
        OrderAfter orderAfter = orderAfterMapper.selectById(id);

        if (StringUtils.isNull(orderAfter)) {
            throw new OperateException("售后记录不存在");
        }

        if (!orderAfter.getAfterStatus().equals(OrderAfterEnum.AFTER_STATUS_ING.getCode())) {
            throw new OperateException("不是售后中状态，不能进行确认退款操作");
        }

        if (!orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND.getCode())
            && !orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND.getCode())
            && !orderAfter.getSubStatus().equals(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode())) {
            throw new OperateException("不是等待卖家退款状态，不允许进行确认退款操作");
        }

        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        RefundRecord refundRecord = null;
        RefundLog refundLog = null;
        try {
            // 写入售后日志
            Integer roleSeller = LogOrderAfterEnum.TYPE_ADMIN.getCode();
            logOrderAfterMapper.add(roleSeller, adminId, id, "卖家已确认退款，售后退款中");

            // 查询订单信息
            Order order = orderMapper.selectById(orderAfter.getOrderId());

            // 生成退款记录
            String refundSn = refundRecordMapper.randMakeOrderSn("sn");
            refundRecord = new RefundRecord();
            refundRecord.setSn(refundSn);
            refundRecord.setOrderSn(order.getOrderSn());
            refundRecord.setUserId(order.getUserId());
            refundRecord.setOrderId(order.getId());
            refundRecord.setOrderType(RefundEnum.getOrderType(RefundEnum.ORDER_TYPE_ORDER.getCode()));
            refundRecord.setOrderAmount(order.getPayMoney());
            refundRecord.setRefundAmount(orderAfter.getRefundMoney());
            refundRecord.setRefundType(RefundEnum.TYPE_ADMIN.getCode());
            refundRecord.setTransactionId(order.getTransactionId());
            refundRecord.setRefundWay(order.getPayWay());
            refundRecord.setCreateTime(System.currentTimeMillis() / 1000);
            refundRecordMapper.insert(refundRecord);

            // 生成退款日志
            refundLog = new RefundLog();
            refundLog.setSn(refundLogMapper.randMakeOrderSn("sn"));
            refundLog.setRecordId(refundRecord.getId());
            refundLog.setUserId(order.getUserId());
            refundLog.setHandleId(adminId);
            refundLog.setOrderAmount(order.getPayMoney());
            refundLog.setRefundAmount(refundRecord.getRefundAmount());
            refundLog.setRefundStatus(RefundEnum.REFUND_ING.getCode());
            refundLog.setUpdateTime(System.currentTimeMillis() / 1000);
            refundLog.setCreateTime(System.currentTimeMillis() / 1000);
            refundLogMapper.insert(refundLog);

            // 发起退款请求
            switch (order.getPayWay()) {
                case 1: // 余额
                    User user = userMapper.selectById(order.getUserId());
                    user.setMoney(user.getMoney().add(orderAfter.getRefundMoney()));
                    user.setUpdateTime(System.currentTimeMillis() / 1000);
                    userMapper.updateById(user);

                    logMoneyMapper.add(
                            user.getId(),
                            LogMoneyEnum.BNW_INC_AFTER_SALE.getCode(), // 修复售后退款显示类型错误
                            orderAfter.getRefundMoney(), // 修复退款错误
                            order.getId(),
                            order.getOrderSn(),
                            "售后退还余额",
                            null
                    );

                    break;
                case 2: // 微信
                    RefundRequestV3 requestV3 = new RefundRequestV3();
                    requestV3.setTransactionId(order.getTransactionId());
                    requestV3.setOutTradeNo(order.getOrderSn());
                    requestV3.setOutRefundNo(refundSn);
                    requestV3.setTotalAmount(AmountUtil.yuan2Fen(order.getPayMoney().toString()));
                    requestV3.setRefundAmount(AmountUtil.yuan2Fen(orderAfter.getRefundMoney().toString()));
                    WxPayDriver.refund(requestV3);
                    break;
                case 3:
                    AliPayRefundRequest request = new AliPayRefundRequest();
                    request.setTransactionId(order.getTransactionId());
                    request.setOutTradeNo(order.getOrderSn());
                    request.setOutRefundNo(refundSn);
                    request.setTotalAmount(order.getPayMoney());
                    request.setRefundAmount(orderAfter.getRefundMoney());
                    AliPayDriver.refund(request);
                    break;
            }

            // 退款记录更新
            refundRecord.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
            refundRecordMapper.updateById(refundRecord);

            // 退款日志更新
            refundLog.setRefundStatus(RefundEnum.REFUND_SUCCESS.getCode());
            refundLogMapper.updateById(refundLog);

            // 变更成功状态
            orderAfter.setRefundSn(refundSn);
            orderAfter.setAfterStatus(OrderAfterEnum.AFTER_STATUS_SUCCESS.getCode());
            orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_SUCCESS.getCode());
            orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
            orderAfterMapper.updateById(orderAfter);

            // 更新售后状态
            List<OrderAfterGoods> orderAfterGoods = orderAfterGoodsMapper.selectList(new QueryWrapper<OrderAfterGoods>().eq("order_after_id", orderAfter.getId()));
            for (OrderAfterGoods oag : orderAfterGoods) {
                OrderGoods orderGoods = new OrderGoods();
                orderGoods.setAfterSale(3);
                orderGoodsMapper.update(orderGoods, new QueryWrapper<OrderGoods>().eq("id", oag.getOrderGoodsId()));
            }

            // 验证是否还有所有商品都完成售后
             Long orderGoodsCount = orderGoodsMapper.selectCount(new QueryWrapper<OrderGoods>()
                    .eq("order_id", order.getId())
                    .in("after_sale", Arrays.asList(1, 2, 4)));

            // 退还库存
            int returnInventory = Integer.parseInt(ConfigUtils.get("trade","returnInventory", "0"));
            if (returnInventory == 1) {
                this.__returnStock(id, order.getId());
            }

            if (orderGoodsCount <= 0) {
                order.setOrderStatus(OrderEnum.ORDER_STATUS_CANCEL.getCode());
                order.setUpdateTime(System.currentTimeMillis() / 1000);
                orderMapper.updateById(order);
            }

            //如果是自提订单，且已经全部自提完毕，则联动更新自提状态
            if (order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode())) {
               if (iOrderManageService.checkPickupAll(order.getId()).equals(true)) {    //如果已经全部自提了
                   order.setConfirmTime(System.currentTimeMillis() / 1000);
                   order.setOrderStatus(OrderEnum.ORDER_STATUS_COMPLETED.getCode());
                   order.setVerificationStatus(OrderGoodsEnum.VERIFYSTATUS_TRUE.getCode());
                   orderMapper.updateById(order);
               }
            }

            // 提交操作事务
            transactionManager.commit(transactionStatus);

            // 通知
            this.afterSaleNotice(order, orderAfter, NoticeEnum.REFUND_SUCCESS_NOTICE.getCode());

        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);

            // 退款失败(退款中)
            orderAfter.setSubStatus(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode());
            orderAfter.setUpdateTime(System.currentTimeMillis() / 1000);
            orderAfterMapper.updateById(orderAfter);

            // 退款日志(标记失败)
            if (StringUtils.isNotNull(refundLog)) {
                refundLog.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                refundLog.setUpdateTime(System.currentTimeMillis() / 1000);
                refundLogMapper.updateById(refundLog);
            }

            // 退款记录(标记失败)
            if (StringUtils.isNotNull(refundRecord)) {
                refundRecord.setRefundStatus(RefundEnum.REFUND_ERROR.getCode());
                refundRecord.setUpdateTime(System.currentTimeMillis() / 1000);
                refundRecordMapper.updateById(refundRecord);
            }

            throw new OperateException(e.getMessage());
        }
    }

    /**
     * 处理售后退款按钮
     *
     * @author fzr
     * @param subStatus 子状态
     * @param refundType 退款类型: [1=仅退款 2=退货退款]
     */
    private Map<String, Boolean> handleAfterBtn(Integer subStatus, Integer refundType) {
        boolean agreeBtn = false;
        boolean refuseBtn = false;
        boolean refuseGoodsBtn = false;
        boolean confirmGoodsBtn = false;
        boolean confirmRefundBtn = false;

        // 买家申请售后,待商家同意
        if (subStatus.equals(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_AGREE.getCode())) {
            agreeBtn = true;  // 同意售后
            refuseBtn = true; // 拒绝售后
        }

        // 买家已退货,待商家处理
        if (subStatus.equals(OrderAfterEnum.SUB_STATUS_ING_WAIT_SELLER_HANDLE.getCode())) {
            refuseGoodsBtn = true;  // 拒绝收货
            confirmGoodsBtn = true; // 同意收货
        }

        // 仅退款类型: 同意退款按钮
        if (subStatus.equals(OrderAfterEnum.SUB_STATUS_ING_SELLER_AGREE_WAIT_REFUND.getCode()) &&
            refundType.equals(OrderAfterEnum.METHOD_ONLY_REFUND.getCode())) {
            confirmRefundBtn = true;
        }

        // 确认退款按钮
        // 商家确认收货,等待退款
        if (subStatus.equals(OrderAfterEnum.SUB_STATUS_ING_SELLER_REFUND_ING.getCode()) ||
            subStatus.equals(OrderAfterEnum.SUB_STATUS_ING_SELLER_RECEIPT_WAIT_REFUND.getCode())) {
            confirmRefundBtn = true;
        }

        Map<String, Boolean> map = new LinkedHashMap<>();
        map.put("agreeBtn", agreeBtn);                 // 同意售后
        map.put("refuseBtn", refuseBtn);               // 拒绝售后
        map.put("refuseGoodsBtn", refuseGoodsBtn);     // 拒绝收货
        map.put("confirmGoodsBtn", confirmGoodsBtn);   // 确认收货
        map.put("confirmRefundBtn", confirmRefundBtn); // 确认退款
        return map;
    }

    /**
     * 售后通知
     *
     * @author mjf
     * @param order Order
     * @param orderAfter OrderAfter
     * @param scene Integer
     */
    private void afterSaleNotice(Order order, OrderAfter orderAfter, Integer scene) {
        // 发货通知
        try {
            NoticeSmsVo UserParams = new NoticeSmsVo()
                    .setScene(scene)
                    .setMobile(order.getAddressMobile())
                    .setExpire(900)
                    .setParams(new String[]{
                            "user_name:" + order.getAddressContact(),
                            "after_sale_sn:" + orderAfter.getAfterSn()
                    });
            NoticeDriver.handle(UserParams);
        } catch (Exception ignored) {}
    }

    /**
     * 回滚库存
     */
    private void __returnStock(Integer id, Integer orderId) {
        Order order = orderMapper.selectById(orderId);
        List<OrderAfterGoods> orderAfterGoods = orderAfterGoodsMapper.selectList(new QueryWrapper<OrderAfterGoods>()
                .eq("order_after_id", id)
                .eq("order_id", orderId)
        );

        if (orderAfterGoods.size() > 0 && (order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_WAIT_DELIVER.getCode()) || ( order.getDeliveryType().equals(OrderEnum.DELIVERY_TYPE_PICK.getCode()) && order.getOrderStatus().equals(OrderEnum.ORDER_STATUS_PICKUP_DELIVER.getCode())))) {
            for ( OrderAfterGoods item : orderAfterGoods ) {
                // 退回商品规格库存
                goodsSkuMapper.update(null, new UpdateWrapper<GoodsSku>()
                        .eq("id", item.getGoodsSkuId())
                        .setSql("stock = stock + " + item.getGoodsNum()));
                // 退回商品总库存
                goodsMapper.update(null, new UpdateWrapper<Goods>()
                        .eq("id", item.getGoodsId())
                        .setSql("total_stock = total_stock+" + item.getGoodsNum()));
            }
        }
    }
}
