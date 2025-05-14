<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="h-full goods-orde pt-[20rpx]" :style="$theme.pageStyle">
        <view class="delivery-tabs px-[20rpx] mt-[20rpx]">
            <u-tabs
                :list="[
                    {
                        id: 1,
                        name: '快递发货',
                        sign: 'express'
                    },
                    {
                        id: 2,
                        name: '上门自提',
                        sign: 'store'
                    }
                ]"
                :is-scroll="false"
                :height="88"
                :active-color="$theme.primaryColor"
                :bar-style="{ top: '100%' }"
                :bar-width="100"
                v-model="delivertIndex"
                @change="changeDeliveryType"
            />
            <!-- <view class="color-bar"></view>
			<u-grid :border="false" :col="2" v-if="orderInfo.goods.length > 0">
				<u-grid-item v-for="(baseListItem,baseListIndex) in baseList" :key="baseListIndex"
					:class="delivertIndex == baseListIndex? 'curr-tabs-'+baseListIndex : 'curr-tabs'"
					@click="changeDeliveryType(baseListIndex)">
					
					<view class="left-color-image" v-show="delivertIndex == 0 && baseListIndex == 0" style="width: 100%; position: absolute;">
						<img :src="LeftSelected" style="width: 100%;" />
					</view>
					<view class="right-color-image" v-show="delivertIndex == 1 && baseListIndex == 1" style="width: 100%; position: absolute;">
						<img :src="RightSelected" style="width: 100%;" />
					</view>
					<text class="grid-text" style="z-index: 999;">{{baseListItem.name}}</text>
				</u-grid-item>
			</u-grid>-->
        </view>

        <!-- Header -->
        <navigator
            class="navigator-class"
            url="/packageA/pages/user_address/user_address?type=true"
            hover-class="none"
            v-if="delivertIndex == 0"
        >
            <address-card>
                <template v-if="!orderInfo?.address?.contact">
                    <view class="text-[#101010] text-lg"> 设置收货地址 </view>
                </template>
                <template v-else>
                    <view class="text-lg text-[#222] mb-[14rpx]">
                        <text class="font-medium">{{ orderInfo?.address.contact }}</text>
                        <text class="ml-[10rpx]">{{ orderInfo?.address.mobile }}</text>
                    </view>
                    <view class="text-base text-content">
                        {{ orderInfo?.address.addressDetail }}
                    </view>
                </template>
            </address-card>
        </navigator>
        <navigator
            class="navigator-class"
            url="/packageA/pages/pick_up/pick_up"
            hover-class="none"
            v-if="delivertIndex == 1"
        >
            <address-card>
                <template v-if="pickInfo.name == undefined">
                    <view class="text-[#101010] text-lg"> 请选择自提地址 </view>
                </template>
                <template v-else>
                    <view class="text-lg text-[#222] mb-[14rpx]">
                        <text class="font-medium">{{ pickInfo.name }}</text>
                    </view>
                    <view class="text-base text-content"> {{ pickInfo.detailedAddress }} </view>
                </template>
            </address-card>
        </navigator>
        <view class="bg-white p-[20rpx] mx-[20rpx]" v-if="delivertIndex == 1">
            <view class="flex justify-center items-center">
                <view> 姓名 </view>
                <u-input
                    class="flex-1"
                    v-model="pupcontact"
                    placeholder="请输入联系人姓名"
                    :border="false"
                    input-align="right"
                />
            </view>

            <view class="mt-[20rpx] flex justify-center items-center"
                >联系电话
                <u-input
                    class="flex-1"
                    v-model="pupmobile"
                    placeholder="请输入联系电话"
                    :border="false"
                    input-align="right"
                />
            </view>
        </view>

        <!-- Main -->
        <view class="m-[20rpx] rounded-[14rpx] bg-white overflow-hidden">
            <goods-card
                shape="rectangle"
                :image="item.image"
                :imageStyle="{ width: '170rpx', height: '170rpx' }"
                :name="item.goodsName"
                :containStyle="{ height: '220rpx', 'border-radius': '0' }"
                v-for="item in orderInfo.goods"
                :key="item.id"
                :goods="item"
            >
                <view>
                    <view class="text-muted mt-[6rpx]">
                        {{ item.skuValueArr }}
                    </view>
                    <!-- {{delivertIndex}} - {{item.isExpress}} -- {{item.isSelffetch}} -->
                    <view class="flex justify-between mt-[8rpx]" v-if="item.buyAble == true">
                        <!-- 普通商品价格 -->
                        <price
                            v-if="GoodsTypeEnum.ORDINARY == orderInfo.orderType"
                            :content="item.price"
                            main-size="32rpx"
                            minor-size="24rpx"
                        />
                        <!-- 秒杀商品价格 -->
                        <price
                            v-if="GoodsTypeEnum.SECKILL == orderInfo.orderType"
                            :content="item.price"
                            main-size="32rpx"
                            minor-size="24rpx"
                            prefix="秒杀价￥"
                        />

                        <text class="text-content"> x {{ item.num }} </text>
                    </view>
                    <view v-else class="flex justify-between mt-[8rpx] text-muted">
                        {{ item.buyAbleMsg.join(',') }}
                    </view>
                </view>
            </goods-card>
            <!--  配送方式 -->
            <!-- <view class="p-[24rpx] flex justify-between">
                <text class="text-[#333333] text-base">配送方式</text>
                <text class="text-[#101010] text-base">
                    {{ orderInfo.deliveryTypeDesc }}
                </text>
            </view> -->
            <!--  备注 -->
            <view class="p-[24rpx] flex w-full col-start">
                <text class="text-[#333333] mt-[6rpx] text-base">买家备注</text>
                <view class="flex-1 ml-2">
                    <u-input
                        v-model="orderParam.remark"
                        type="textarea"
                        placeholder="请输入备注"
                        height="80"
                    />
                </view>
            </view>
        </view>

        <view class="m-[20rpx] rounded-[14rpx] overflow-hidden bg-white">
            <view class="p-[24rpx] flex justify-between">
                <text class="text-[#333333] text-base">商品总额</text>
                <text class="text-[#101010] text-base">¥{{ orderInfo.goodsAmount }}</text>
            </view>
            <view class="p-[24rpx] flex justify-between" v-if="delivertIndex == 0">
                <text class="text-[#333333] text-base">运费</text>
                <text class="text-base text-[#101010]">¥{{ orderInfo.freightAmount }}</text>
            </view>

            <!-- 优惠券 -->
            <coupon-popup
                v-if="GoodsTypeEnum.ORDINARY == orderInfo.orderType"
                v-model:orderParam="orderParam"
                :couponAmount="orderInfo.couponAmount"
                @refresh="getOrderDetail"
            ></coupon-popup>
        </view>
        <view class="safe-bottom"></view>
        <!-- Footer -->
        <view class="flex items-center justify-end bg-white submit">
            <view class="mr-[30rpx]">
                <text class="text-lg">合计:</text>
                <price :content="orderInfo.orderAmount" mainSize="38rpx" minorSize="30rpx" />
            </view>

            <button
                class="submit-btn"
                @tap="onSubmitOrder"
                :style="buyGoodsNum == 0 ? 'background: #939393 !important' : ''"
            >
                提交订单
            </button>
        </view>

        <!-- Component 页面状态 -->
        <page-status ref="pageRef"></page-status>

        <!-- Component 支付 -->
        <payment
            v-if="payState.showPay || payState.showCheck"
            v-model:show="payState.showPay"
            v-model:show-check="payState.showCheck"
            :order-id="payState.orderId"
            :from="payState.from"
            :redirect="payState.redirect"
            @success="handlePayResult"
            @fail="handlePayResult"
        />
    </view>
</template>

<script lang="ts" setup>
import { reactive, shallowRef, unref, nextTick, watch, ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { orderSettle, orderSubmit } from '@/api/order'
import GoodsAbnormalImage from '@/static/images/empty/goods.png'
import RightSelected from '@/static/images/tabs/right_selected.png'
import LeftSelected from '@/static/images/tabs/left_selected.png'
import { useAddress } from '@/hooks/useAddress'
import { PayFromType } from '@/enums/appEnums'
import { useLockFn } from '@/hooks/useLockFn'
import { GoodsTypeEnum } from '@/enums/goodsEnums'
import { useRouter } from 'uniapp-router-next'
import { queryToArrObj, isEmpty, isValidPhone } from '@/utils/util'
import { useOrderPickup } from '@/stores/orderPickup'
import CouponPopup from '@/pages/goods_order/compound/coupon-popup.vue'
import { getSelffetchshopList } from '@/api/selffetchshop'
const baseList = ref([
    {
        id: 1,
        name: '快递发货',
        title: '快递发货',
        sign: 'express'
    },
    {
        id: 2,
        name: '上门自提',
        title: '上门自提',
        sign: 'store'
    }
])
const form = ref('')
const router = useRouter()
const { address } = useAddress()
const pageRef = shallowRef()
const latitude = ref(39.909)
const longitude = ref(116.39742)
const pupcontact = ref('')
const pupmobile = ref('')
const payState = reactive({
    orderId: '',
    from: '',
    showPay: false,
    showCheck: false,
    redirect: '/pages/goods_order/goods_order'
})
const lineBg = LeftSelected
const orderInfo = reactive<any>({
    // 商品信息
    goods: [],
    // 地址信息
    address: {},
    // 订单类型（商品类型
    orderType: '0',
    // 配送方式
    deliveryTypeDesc: '快递配送',
    // 运费
    freightAmount: 0,
    // 优惠金额
    couponAmount: 0,
    // 商品总额
    goodsAmount: 0,
    // 实付金额
    orderAmount: 0
})
const orderParam = reactive<any>({
    buyType: 'buyNow',
    couponListId: '',
    buyGoods: {
        goodsId: '',
        skuId: '',
        num: '',
        cartId: '',
        deliveryAble: false,
        buyAble: false
    },
    remark: '',
    addressId: 0,

    // 页面初始的参数
    param: null,

    //到店自提
    selffetchShopId: 0,
    deliveryType: 1,
    address: {}
})
const cpOrderParam = reactive<any>({
    buyType: 'buyNow',
    couponListId: '',
    buyGoods: {
        goodsId: '',
        skuId: '',
        num: '',
        cartId: '',
        deliveryAble: false,
        buyAble: false
    },
    remark: '',
    addressId: 0,

    // 页面初始的参数
    param: null,

    //到店自提
    selffetchShopId: 0,
    deliveryType: 1,
    address: {}
})

watch(
    () => address,
    (value) => {
        orderParam.addressId = value.id
        orderInfo.address = value
    },
    { deep: true }
)

const buyGoodsNum = computed(() => {
    // let ret = cpOrderParam?.buyGoods.filter(item => {
    // 	return item.deliveryAble == true
    // })
    const orderCp = JSON.parse(JSON.stringify(orderParam))

    if (orderCp?.buyGoods.length != undefined) {
        const ret = orderCp?.buyGoods.filter((item) => {
            return item.buyAble == true
        })
        return ret.length
    } else {
        if (orderCp?.buyGoods.buyAble == false) {
            return 0
        } else {
            return 1
        }
    }
})

// 到店自提
const pickInfo = computed(() => {
    if (useOrderPickup().config.id == undefined) {
        return {}
    } else {
        return useOrderPickup().config
    }
})

const getOrderDetail = async () => {
    try {
        const data = await orderSettle({ ...orderParam })
        Reflect.ownKeys(data).map((item: any) => {
            orderInfo[item] = data[item]
        })
        orderParam.addressId = data.address?.id
        const buyAbleIds = []
        //let buySkuIds = []
        orderInfo.goods.map((item) => {
            if (item.buyAble == true) {
                buyAbleIds.push(item.goodsId + '-' + item.goodsSkuId)
                //buySkuIds.push(item.goodsSkuId)
            }
        })
        orderParam.buyGoods.map((item) => {
            if (buyAbleIds.includes(item.goodsId + '-' + item.skuId)) {
                item.buyAble = true
            } else {
                item.buyAble = false
            }
        })

        if (pupcontact.value == '') {
            //orderInfo.address.pupcontact = data.lastAddressContact
            pupcontact.value = data.lastAddressContact
        }
        if (pupmobile.value == '') {
            pupmobile.value = data.lastAddressMobile
        }

        if (data.lastSelffetchShop != null) {
            useOrderPickup().config = data.lastSelffetchShop
        } else {
            //就近
            getLocation()
        }
        unref(pageRef).close()
    } catch (error) {
        unref(pageRef)?.show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('获取结算详情接口', error)
    }
}

const { isLock, lockFn: onSubmitOrder } = useLockFn(async () => {
    //console.log(buyGoodsNum.value, "value")
    if (buyGoodsNum.value == 0) {
        return uni.$u.toast('至少要有一个商品满足配送方式才可以下单')
    }
    if (orderParam.deliveryType == 2) {
        //到店自提
        if (isEmpty(pickInfo.value.id)) {
            return uni.$u.toast('请选择自提地址')
        }
        if (pupcontact.value == '') {
            return uni.$u.toast('请输入联系人姓名')
        }
        if (pupmobile.value == '' || pupmobile.value == null) {
            return uni.$u.toast('请输入联系电话')
        }
        if (isValidPhone(pupmobile.value) == false) {
            return uni.$u.toast('请输入正确的联系电话')
        }
        orderParam.selffetchShopId = pickInfo.value.id
        orderParam.address = {
            mobile: pupmobile.value,
            contact: pupcontact.value
        }
    } else {
        orderParam.selffetchShopId = 0
    }
    const modal: any = await uni.showModal({
        title: '温馨提示',
        content: '是否确认下单?',
        confirmColor: '#FF2C3C'
    })
    if (!modal.confirm) return
    try {
        cpOrderParam.value = JSON.parse(JSON.stringify(orderParam))
        cpOrderParam.value.buyGoods = cpOrderParam.value.buyGoods.filter((item) => {
            return item.buyAble == true
        })

        if (cpOrderParam.value.buyGoods.length == 0) {
            return uni.$u.toast('至少有一个商品才能下单')
        }

        const data = await orderSubmit({ ...cpOrderParam.value })
        payState.orderId = data.orderId
        payState.from = PayFromType.ORDER
        payState.showPay = true
        payState.redirect =
            payState.redirect +
            `?buyGoods=${orderParam.param}&order_id=${data.orderId}&from=${PayFromType.ORDER}&checkPay=true`
        // payState.redirect =
        //     payState.redirect + `?&order_id=${data.orderId}&from=${PayFromType.ORDER}&checkPay=true`
    } catch (error) {
        console.log('创建订单', error)
    }
})

const handlePayResult = async () => {
    payState.showPay = false
    payState.showCheck = false
    const param = JSON.stringify({
        order_id: payState.orderId,
        from: payState.from
    })
    setTimeout(() => {
        router.redirectTo({
            path: '/pages/payment_result/payment_result',
            query: {
                param: param
            }
        })
    }, 1000)
}
//切换配送方式
const delivertIndex = ref(0)
const changeDeliveryType = async (index: any) => {
    if (index == 0) {
        orderParam.deliveryType = 1
    } else {
        orderParam.deliveryType = 2
    }

    delivertIndex.value = index
    await getOrderDetail()
}

const getLocation = async () => {
    uni.getLocation({
        success: function (res) {
            longitude.value = Math.abs(res.longitude)
            latitude.value = Math.abs(res.latitude)
            getShopList()
        },
        fail: function (err) {
            return uni.$u.toast(err)
        },
        complete: function (e) {}
    })
}

const getShopList = async () => {
    const rep = await getSelffetchshopList({
        longitude: longitude.value,
        latitude: latitude.value
    })
    if (rep.lists.length > 0) {
        useOrderPickup().config = rep.lists[0]
        // rep.lists.map((item, index) => {

        // 	console.log(item, "itemitemitemitem")
        // })
    }
}

onLoad(async (options: any) => {
    await nextTick()
    //console.log(options)
    try {
        if (!options.buyGoods) {
            throw new Error('参数有误')
        }
        Reflect.ownKeys(orderParam).map((item: any) => {
            if (!options[item]) return
            orderParam[item] = options[item]
        })
        orderParam.param = options.buyGoods
        //console.log(options.buyGoods)

        orderParam.buyGoods = JSON.parse(options.buyGoods)

        await getOrderDetail()

        // h5支付用于弹起手动确认支付弹窗
        if (options?.checkPay) {
            payState.orderId = options.order_id
            payState.from = options.from
            payState.showCheck = true
            return
        }
    } catch (error) {
        unref(pageRef).show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('订单结算', error)
    }
})
</script>

<style lang="scss" scoped>
page {
    height: 100%;
}

.goods-order {
    padding-bottom: calc(100rpx + env(safe-area-inset-bottom));
}

.submit {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 100;
    height: calc(100rpx + env(safe-area-inset-bottom));
    padding: 0 24rpx env(safe-area-inset-bottom) 24rpx;

    &-btn {
        width: 220rpx;
        height: 74rpx;
        color: #ffffff;
        line-height: 74rpx;
        border-radius: 60px;
        background: linear-gradient(90deg, var(--theme-dark-color) 0%, var(--theme-color) 100%);
    }
}

// .delivery-tabs {
// 	padding-top: 30rpx;
// 	border-radius: 14rpx 14rpx 0 0;
// }

// .curr-tabs {
// 	//background-color: #c9c9c9 !important;
// 	z-index: 99;
// 	background: none !important;
// 	font-size: 22rpx;
// }

// .curr-tabs-0 {
// 	//background-image: url("/static/images/tabs/left_selected.png") !important;
// 	background: none  !important;
// 	background-size: 390rpx !important;
// 	background-repeat: no-repeat !important;
// 	background-position: center  !important;
// }

// .curr-tabs-1 {
// 	//background-image: url("/static/images/tabs/right_selected.png") !important;
// 	background: none  !important;
// 	background-size: 390rpx !important;
// 	background-repeat: no-repeat !important;
// 	background-position: center  !important;
// }
// .navigator-class {
// 	margin-top: -10rpx;
// }
// .address-box {
// 	border-radius: 0rpx;
// }
// .color-bar {
// 	width: 94.5%;
// 	height: 100rpx;
// 	background-color: #c9c9c9 !important;
// 	position: absolute;
// 	top: 50rpx;
// 	border-radius: 10rpx 10rpx 0 0;
// }
</style>
