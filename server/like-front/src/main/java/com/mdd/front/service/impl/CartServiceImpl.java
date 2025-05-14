package com.mdd.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.github.yulichang.query.MPJQueryWrapper;
import com.mdd.front.validate.cart.CartAddValidate;
import com.mdd.front.validate.cart.CartChangeSkuValidate;
import com.mdd.front.validate.cart.CartSelectedValidate;
import com.mdd.front.vo.cart.CartCountVo;
import com.mdd.common.config.GlobalConfig;
import com.mdd.common.entity.cart.Cart;
import com.mdd.common.entity.goods.GoodsSku;
import com.mdd.common.enums.CartEnum;
import com.mdd.common.mapper.cart.CartMapper;
import com.mdd.common.mapper.goods.GoodsSkuMapper;
import com.mdd.common.util.TimeUtils;
import com.mdd.front.service.ICartService;
import com.mdd.front.validate.cart.CartChangeNumValidate;
import com.mdd.front.vo.cart.CartGoodsListVo;
import com.mdd.front.vo.cart.CartListVo;
import com.mdd.common.util.UrlUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl implements ICartService {

    @Resource
    CartMapper cartMapper;

    @Resource
    GoodsSkuMapper goodsSkuMapper;


    /**
     * 购物车列表
     *
     * @author cjhao
     * @return CartListVo
     */
    @Override
    public CartListVo list(Integer userId) {
        List<CartGoodsListVo> cartList = cartMapper.selectJoinList(
                CartGoodsListVo.class,
                new MPJQueryWrapper<Cart>()
                        .eq("t.is_delete", 0)
                        .eq("user_id", userId)
                        .eq("g.is_delete",0) // 修复购物车问题
                        .leftJoin("?_goods g ON g.id = t.goods_id".replace("?_", GlobalConfig.tablePrefix))
                        .leftJoin("?_goods_sku gs ON gs.id = t.goods_sku_id".replace("?_", GlobalConfig.tablePrefix))
                        .select("t.id,t.goods_id,t.goods_sku_id,t.num,t.selected,g.name,g.image,g.is_delete as goods_delete," +
                                "g.status as goods_status,g.spec_type,gs.sku_value_arr,gs.image as sku_image,gs.sku_value_arr,gs.price,gs.stock"));

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalNum = 0;
        for (CartGoodsListVo cart : cartList) {
            cart.setImage(UrlUtils.toAbsoluteUrl(cart.getImage()));
            // 商品状态
            cart.setCartStatus(CartEnum.CART_GOODS_NORMAL.getCode());
            // 商品下架
            if (cart.getGoodsStatus() == 0) {
                cart.setCartStatus(CartEnum.CART_GOODS_DOWN.getCode());
            }
            // 商品删除
            if (cart.getGoodsDelete() == 1) {
                cart.setCartStatus(CartEnum.CART_GOODS_DELETED.getCode());
            }
            // 商品规格不存在
            if (cart.getGoodsSkuId() == null || cart.getSkuValueArr() == null) {
                cart.setCartStatus(CartEnum.CART_ORDER_SKU_DELETED.getCode());
            } else {
                // 规格库存不足
                if (cart.getStock() < cart.getNum()) {
                    cart.setCartStatus(CartEnum.CART_ORDER_SKU_STOCK_LACK.getCode());
                }
            }
            // 非正常商品状态时重置为未选中
            if (cart.getCartStatus() != CartEnum.CART_GOODS_NORMAL.getCode()) {
                cart.setSelected(0);
            }
            // 统计金额
            if (cart.getSelected() == 1 && cart.getCartStatus() == CartEnum.CART_GOODS_NORMAL.getCode()) {
                BigDecimal  goodsPrice = cart.getPrice().multiply(BigDecimal.valueOf(cart.getNum()));
                totalPrice = totalPrice.add(goodsPrice);
                totalNum += cart.getNum();
            }
        }

        CartListVo cartListVo = new CartListVo();
        cartListVo.setGoods(cartList);
        cartListVo.setTotalPrice(totalPrice);
        cartListVo.setTotalNum(totalNum);
        return cartListVo;
    }

    /**
     * 加入购物车
     *
     * @author cjhao
     * @param addValidate CartAddValidate
     */
    @Override
    public void add(Integer userId, CartAddValidate addValidate) {
        Integer goodsSkuId = addValidate.getGoodsSkuId();
        Integer num = addValidate.getNum();
        Cart cart = cartMapper.selectOne(new QueryWrapper<Cart>()
                .eq("user_id", userId)
                .eq("goods_sku_id", goodsSkuId)
                .eq("is_delete", 0)
                .last("limit 1"));

        if(null == cart) {
            GoodsSku goodsSku = goodsSkuMapper.selectOne(new QueryWrapper<GoodsSku>()
                    .eq("id", goodsSkuId)
                    .last("limit 1"));

            Cart model = new Cart();
            model.setNum(num);
            model.setGoodsId(goodsSku.getGoodsId());
            model.setGoodsSkuId(goodsSkuId);
            model.setUserId(userId);
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setUpdateTime(System.currentTimeMillis() / 1000);
            cartMapper.insert(model);
        }else{
            cart.setNum(cart.getNum()+num);
            cartMapper.updateById(cart);
        }
    }

    /**
     * 修改选中状态
     *
     * @author mjf
     * @param cartSelectedValidate CartSelectedValidate
     */
    @Override
    public void select(Integer userId, CartSelectedValidate cartSelectedValidate) {
        List<Integer> ids = cartSelectedValidate.getIds();
        Integer isSelected = cartSelectedValidate.getIsSelected();
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        if (ids.size() > 0) {
            queryWrapper.in("id", ids);
        } else {
            queryWrapper.eq("id", -1);
        }
        queryWrapper.eq("user_id",userId)
                .eq("is_delete", 0);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);

        Assert.notEmpty(cartList, "购物车不存在!");

        for (Cart cart : cartList) {
            cart.setSelected(isSelected);
            cart.setUpdateTime(System.currentTimeMillis() / 1000);
            cartMapper.updateById(cart);
        }
    }

    /**
     * 修改购物车商品数量
     *
     * @author cjhao
     * @param userId Integer
     * @param changeNumValidate CartNumValidate
     */
    @Override
    public void changeNum(Integer userId, CartChangeNumValidate changeNumValidate) {
        Cart cart = cartMapper.selectOne(new QueryWrapper<Cart>()
                .eq("id", changeNumValidate.getId())
                .eq("user_id",userId)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(cart, "购物车不存在!");
        cart.setNum(changeNumValidate.getNum());
        cart.setUpdateTime(TimeUtils.timestamp());
        cartMapper.updateById(cart);
    }

    /**
     * 购物车变动规格
     *
     * @author mjf
     * @param changeSkuValidate CartChangeSkuValidate
     */
    @Override
    public void changeSku(Integer userId, CartChangeSkuValidate changeSkuValidate) {
        Cart cart = cartMapper.selectOne(new QueryWrapper<Cart>()
                .eq("id", changeSkuValidate.getId())
                .eq("user_id",userId)
                .eq("is_delete", 0)
                .last("limit 1"));
        Assert.notNull(cart, "购物车不存在!");
        cart.setNum(changeSkuValidate.getNum());
        cart.setGoodsSkuId(changeSkuValidate.getGoodsSkuId());
        cart.setUpdateTime(TimeUtils.timestamp());
        cartMapper.updateById(cart);
    }

    /**
     * 删除购物车
     *
     * @author cjhao
     * @param userId Integer
     * @param ids List<Integer>
     */
    @Override
    public void del(Integer userId, List<Integer> ids) {
        List<Cart> cartList = cartMapper.selectList(new QueryWrapper<Cart>()
                .in("id", ids)
                .eq("user_id",userId)
                .eq("is_delete", 0));

        Assert.notNull(cartList, "商品不存在!");
        for (Cart cart : cartList) {
            cart.setIsDelete(1);
            cart.setUpdateTime(System.currentTimeMillis() / 1000);
            cartMapper.updateById(cart);
        }
    }

    /**
     * 购物车数量
     *
     * @author mjf
     * @param userId Integer
     * @return CartCountVo
     */
    @Override
    public CartCountVo cartCount(Integer userId) {
        MPJQueryWrapper<Cart> mpjQueryWrapper = new MPJQueryWrapper<Cart>()
                .select("t.id")
                .innerJoin("?_goods g ON g.id=t.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .innerJoin("?_goods_sku gs ON gs.id=t.goods_sku_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("t.is_delete", 0)
                .eq("g.is_delete", 0)
                .eq("t.user_id", userId);
        Long count = cartMapper.selectCount(mpjQueryWrapper);
        CartCountVo vo = new CartCountVo();
        vo.setNum(count);
        return vo;
    }
}
