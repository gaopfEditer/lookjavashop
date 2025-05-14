<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <!-- <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
 -->

        <u-sticky offset-top="0" h5-nav-height="0" bg-color="transparent">
            <u-navbar
                :is-back="false"
                title="购物车"
                :title-bold="false"
                :is-fixed="false"
                :border-bottom="false"
                :background="{ background: `${$theme.navBgColor}` }"
                :title-color="$theme.navColor"
            ></u-navbar>
        </u-sticky>
        <!-- #endif -->
    </page-meta>
    <view class="shopping-cart">
        <!-- 购物车列表 -->
        <view class="p-[20rpx] w-full" v-if="cart.goods.length">
            <cart-lists :lists="cart.goods" @update="getShoppingCart"></cart-lists>
        </view>

        <!-- 购物车空状态 -->
        <view
            class="text-center mb-[20rpx]"
            style="padding: 80rpx 0 50rpx"
            v-show="isLogin && cart.goods.length == 0"
        >
            <image
                class="w-[300rpx]"
                src="/static/images/empty/shopping_cart.png"
                mode="widthFix"
            ></image>
            <view class="text-[#999] mb-[30rpx]">购物车空空如也~</view>
            <router-navigate
                class="rounded-[60px] btn text-primary flex justify-center"
                to="/pages/index/index"
            >
                <text>去逛逛</text>
            </router-navigate>
        </view>

        <!-- 未登录状态 -->
        <view v-if="!isLogin" class="text-center mb-[20rpx]" style="padding: 80rpx 0 50rpx">
            <image
                class="w-[300rpx]"
                src="/static/images/empty/shopping_cart.png"
                mode="widthFix"
            ></image>
            <view class="text-muted mb-[30rpx]">登录后才能查看购物车哦</view>
            <router-navigate
                class="rounded-[60px] btn text-primary flex justify-center"
                to="/pages/login/login"
            >
                <text>去登录</text>
            </router-navigate>
        </view>

        <!-- 热门商品 -->
        <z-paging
            ref="paging"
            v-model="cart.hot_lists"
            use-page-scroll
            :refresher-enabled="false"
            :auto-clean-list-when-reload="false"
            :auto-scroll-to-top-when-reload="false"
            @query="queryList"
        >
            <view class="hot-goods">
                <view
                    class="flex items-center mx-[30rpx] py-[26rpx] mt-[20rpx] text-2xl font-medium text-[#101010]"
                >
                    热门商品
                </view>
                <goods-list :list="cart.hot_lists" />
            </view>
        </z-paging>

        <!-- 去结算底部 -->
        <view class="flex items-center bg-white footer" v-show="cart.goods.length">
            <view class="flex items-center" @click="handleAllSelect">
                <l-checkbox
                    :checked="allSelect"
                    label=""
                    :true-label="1"
                    :false-label="0"
                    :active-color="$theme.primaryColor"
                />
                <text class="text-main text-md mx-[10rpx]">全选</text>
            </view>
            <view class="text-primary text-md" @tap="handleShowDel">删除</view>
            <view class="flex justify-end items-center flex-1 mr-[20rpx]">
                <view class="text-main text-md">合计：</view>
                <price :content="cart.totalPrice" main-size="32rpx" minor-size="24rpx" />
            </view>
            <button class="text-white right-btn br60" @tap="goToConfirm">去结算</button>
        </view>

        <!-- 底部Tabbar -->
        <tabbar />

        <!-- 公用组件 删除 -->
        <u-modal
            v-model="cart.showDelTips"
            :show-cancel-button="true"
            confirmText="狠心删除"
            :show-title="false"
            @confirm="handleDel"
            confirmColor="#FF2C3C"
        >
            <view class="flex-col items-center text-center h-[230rpx] w-full pt-[40rpx]">
                <image class="w-[52rpx] h-[52rpx]" src="@/static/images/icon/icon_warning.png" />
                <view class="my-[30rpx] text-center">确认删除选中商品吗？</view>
            </view>
        </u-modal>

        <!-- 骨袈屏 -->
        <skeleton v-if="cart.loading"></skeleton>
    </view>
</template>

<script lang="ts" setup>
import { getGoodsLists, shoppingCartLists, shoppingCartDel, shoppingCartSelect } from '@/api/goods'
import { reactive, shallowRef, unref, computed, nextTick } from 'vue'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/stores/user'
import { onShow } from '@dcloudio/uni-app'
import CartLists from './component/cart-lists.vue'
import Skeleton from './component/skeleton.vue'
import { useRouter } from 'uniapp-router-next'
import { objectToQuery } from '@/utils/util'
const router = useRouter()

const userStore = useUserStore()
const { getShopCartCount } = useUserStore()
const { isLogin } = storeToRefs(userStore)

type ShoppingCartItem = {
    cartStatus: number
    goods: any
    goodsId: number
    goodsSkuId: number
    price: string
    id: number
    num: number
    selected: number
    sku: any
    show?: boolean
}

type ShoppingCartParam = {
    loading: boolean
    goods: ShoppingCartItem[]
    hot_lists: any[]
    totalNum: number
    totalPrice: number
    showDelTips: boolean
}

const paging = shallowRef()
const cart = reactive<ShoppingCartParam>({
    loading: true,
    goods: [],
    hot_lists: [],
    totalNum: 0,
    totalPrice: 0,
    showDelTips: false
})

// 全部选中或不选中
const allSelect = computed(() => {
    const lists = cart.goods.filter((item: ShoppingCartItem) => item.cartStatus == 0)
    return lists.every((item: ShoppingCartItem) => item.selected)
})
// 有一个选中或多个
const isSelect = computed(() => {
    const lists = cart.goods
    return lists.some((item: ShoppingCartItem) => item.selected)
})

const getShoppingCart = async () => {
    try {
        const { goods, totalPrice, totalNum }: ShoppingCartParam = await shoppingCartLists()
        goods.forEach((item: ShoppingCartItem) => {
            item.show = false
        })
        cart.goods = goods
        cart.totalNum = totalNum
        cart.totalPrice = totalPrice
    } catch (error) {
        console.log('获取购物车列表失败', error)
    }
    await nextTick()
    cart.loading = false
}

const queryList = async (pageNo: number, pageSize: number) => {
    try {
        const { lists } = await getGoodsLists({
            type: 'hot',
            pageNo: pageNo,
            pageSize: pageSize
        })
        paging.value.complete(lists)
    } catch (e) {
        console.log('报错=>', e)
        //TODO handle the exception
        paging.value.complete(false)
    }
}

// 全选操作
const handleAllSelect = async () => {
    try {
        const ids = cart.goods.filter((item) => !item.cartStatus).map((item) => item.id)
        await shoppingCartSelect({ ids: ids, isSelected: allSelect.value ? 0 : 1 })
        // 改变后重新获取购物车
        await getShoppingCart()
    } catch (error) {
        console.log('全选', error)
    }
}

const handleShowDel = () => {
    // 没有选中的话就返回
    if (!unref(isSelect)) return
    cart.showDelTips = true
}

const handleDel = async () => {
    try {
        const ids: number[] = cart.goods
            .filter((item) => !item.cartStatus && item.selected)
            .map((item) => item.id)
        await shoppingCartDel({ ids: ids })
        // 改变后重新获取购物车
        await getShoppingCart()
        //
        await getShopCartCount()
    } catch (error) {
        console.log('全选', error)
    }
}

// 去结算
const goToConfirm = () => {
    const buyGoods = cart.goods
        .filter((item: ShoppingCartItem) => item.selected)
        .map((item: ShoppingCartItem) => {
            return {
                cartId: item.id,
                num: item.num,
                skuId: item.goodsSkuId,
                goodsId: item.goodsId
            }
        })
    router.navigateTo({
        path: '/pages/goods_order/goods_order',
        query: {
            buyGoods: JSON.stringify(buyGoods),
            buyType: 'cart'
        }
    })
}

onShow(() => {
    getShoppingCart()
    getShopCartCount()
})
</script>

<style lang="scss" scoped>
.shopping-cart {
    padding-bottom: 150rpx;

    ::v-deep .u-icon-minus,
    ::v-deep .u-icon-plus {
        width: 58rpx;
        height: 54rpx;
        border-radius: 2rpx !important;
    }

    ::v-deep .u-number-input {
        font-size: 26rpx !important;
    }

    ::v-deep .u-icon-disabled {
        color: #999999 !important;
    }

    .btn {
        border: 1px solid $u-type-primary;
        width: 184rpx;
        padding: 8rpx 24rpx;
        margin-left: auto;
        margin-right: auto;
    }

    .footer {
        position: fixed;
        padding: 0 24rpx;
        width: 100%;
        height: 100rpx;
        box-shadow: 0px 0px 12px rgba(0, 0, 0, 0.1);
        margin-bottom: 100rpx;
        bottom: calc(env(safe-area-inset-bottom));
        box-sizing: border-box;
        z-index: 20;

        .right-btn {
            height: 68rpx;
            border-radius: 60px;
            background: linear-gradient(90deg, var(--theme-dark-color) 0%, var(--theme-color) 100%);
        }
    }
}
</style>
