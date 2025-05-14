package com.mdd.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.admin.service.IDistributionOrderService;
import com.mdd.admin.validate.commons.PageValidate;
import com.mdd.admin.validate.distribution.*;
import com.mdd.admin.vo.distribution.*;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.core.PageResult;
import com.mdd.common.entity.distribution.DistributionOrder;
import com.mdd.common.entity.order.OrderGoods;
import com.mdd.common.entity.user.User;
import com.mdd.common.enums.DistributionEnum;
import com.mdd.common.mapper.distribution.DistributionOrderMapper;
import com.mdd.common.mapper.order.OrderGoodsMapper;
import com.mdd.common.mapper.user.UserMapper;
import com.mdd.common.util.StringUtils;
import com.mdd.common.util.TimeUtils;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * 分销订单服务实现类
 */
@Service
public class DistributionOrderServiceImpl implements IDistributionOrderService {

    @Resource
    UserMapper userMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    DistributionOrderMapper distributionOrderMapper;

    /**
     * 分销订单列表
     *
     * @author mjf
     * @param pageValidate PageValidate
     * @param searchValidate DistributionOrderSearchValidate
     * @return PageResult<DistributionOrderListedVo>
     */
    @Override
    public PageResult<DistributionOrderListedVo> list(PageValidate pageValidate, DistributionOrderSearchValidate searchValidate) {
        Integer pageNo = pageValidate.getPageNo();
        Integer pageSize = pageValidate.getPageSize();
        String orderSn = searchValidate.getOrderSn();
        String goodsKeyword = searchValidate.getGoodsKeyword();
        String buyerKeyword = searchValidate.getBuyerKeyword();
        String distributionKeyword = searchValidate.getDistributionKeyword();

        MPJQueryWrapper<DistributionOrder> mpjQueryWrapper = new MPJQueryWrapper<>();
        mpjQueryWrapper.leftJoin("?_order O ON O.id=t.order_id".replace("?_", GlobalConfig.tablePrefix));
        mpjQueryWrapper.eq("t.is_delete", 0);

        // 订单号
        if (StringUtils.isNotBlank(orderSn)) {
            mpjQueryWrapper.like("O.order_sn", orderSn);
        }

        // 商品搜索
        if (StringUtils.isNotBlank(goodsKeyword)) {
            List<OrderGoods> orderGoodsList = orderGoodsMapper.selectList(new QueryWrapper<OrderGoods>().nested(wq->wq
                    .like("goods_code", goodsKeyword).or()
                    .like("goods_name", goodsKeyword)));

            List<Integer> orderGoodsIds = new LinkedList<>();
            for (OrderGoods orderGoods : orderGoodsList) {
                orderGoodsIds.add(orderGoods.getId());
            }
            if (orderGoodsIds.size() > 0) {
                mpjQueryWrapper.in("t.order_goods_id", orderGoodsIds);
            } else {
                mpjQueryWrapper.in("t.order_goods_id", 0);
            }
        }

        // 买家信息
        if (StringUtils.isNotBlank(buyerKeyword)) {
            List<User> userList = userMapper.selectList(new QueryWrapper<User>().nested(wq->wq
                    .like("sn", buyerKeyword).or()
                    .like("nickname", buyerKeyword).or()
                    .like("mobile", buyerKeyword)));

            List<Integer> userListIds = new LinkedList<>();
            for (User user : userList) {
                userListIds.add(user.getId());
            }
            if (userListIds.size() > 0) {
                mpjQueryWrapper.in("O.user_Id", userListIds);
            } else {
                mpjQueryWrapper.in("O.user_Id", 0);
            }
        }

        // 分销商信息
        if (StringUtils.isNotBlank(distributionKeyword)) {
            List<User> distributionStoreList = userMapper.selectList(new QueryWrapper<User>().nested(wq->wq
                    .like("sn", distributionKeyword).or()
                    .like("nickname", distributionKeyword).or()
                    .like("mobile", distributionKeyword)));

            List<Integer> distributionStoreIds = new LinkedList<>();
            for (User user : distributionStoreList) {
                distributionStoreIds.add(user.getId());
            }
            if (distributionStoreIds.size() > 0) {
                mpjQueryWrapper.in("t.user_id", distributionStoreIds);
            } else {
                mpjQueryWrapper.in("t.user_id", 0);
            }
        }

        // 其它搜索
        distributionOrderMapper.setSearch(mpjQueryWrapper, searchValidate, new String[]{
                "=:status@t.status:int",
                "datetime:startTime-endTime@O.create_time:str",
        });

        mpjQueryWrapper.selectAll(DistributionOrder.class)
                .orderByDesc("t.id")
                .select("t.user_id as distribution_store_id, t.sku_id as goods_sku_id," +
                        "O.order_sn, O.user_id as buyer_id, O.create_time as order_create_time");

        IPage<DistributionOrderListedVo> iPage = distributionOrderMapper.selectJoinPage(
                new Page<>(pageNo, pageSize),
                DistributionOrderListedVo.class,
                mpjQueryWrapper);

        for (DistributionOrderListedVo vo : iPage.getRecords()) {
            vo.setStatusMsg(DistributionEnum.getOrderStatusMsg(vo.getStatus()));
            vo.setOrderCreateTime(TimeUtils.timestampToDate(vo.getOrderCreateTime()));
            // 结算时间
            if (Long.parseLong(vo.getSettleTime()) > 0) {
                vo.setSettleTime(TimeUtils.timestampToDate(vo.getSettleTime()));
            } else {
                vo.setSettleTime("-");
            }
            vo.setLevel(DistributionEnum.getOrderLevelMsg(Integer.valueOf(vo.getLevel())));
            // 分销商
            User distributionStore = userMapper.selectById(vo.getDistributionStoreId());
            if (null != distributionStore){
                vo.setDistributionStore(distributionStore.getNickname());
            }

            // 买家
            User buyer = userMapper.selectById(vo.getBuyerId());
            if (null != buyer){
                vo.setBuyerSn(buyer.getSn());
                vo.setBuyerUsername(buyer.getUsername());
                vo.setBuyerNickname(buyer.getNickname());
                vo.setBuyerAvatar(UrlUtils.toAbsoluteUrl(buyer.getAvatar()));
            }
            // 商品
            OrderGoods orderGoods = orderGoodsMapper.selectById(vo.getOrderGoodsId());
            if (null != orderGoods){
                vo.setGoodsName(orderGoods.getGoodsName());
                vo.setGoodsSkuStr(orderGoods.getGoodsSkuValue());
                vo.setGoodsImage(UrlUtils.toAbsoluteUrl(orderGoods.getGoodsImage()));
                vo.setGoodsPrice(orderGoods.getGoodsPrice());
                vo.setGoodNum(orderGoods.getGoodsNum());
                vo.setPayMoney(orderGoods.getPayMoney());
            }
        }

        return PageResult.iPageHandle(iPage.getTotal(), iPage.getCurrent(), iPage.getSize(), iPage.getRecords());
    }
}
