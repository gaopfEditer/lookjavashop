<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="h-full payment-result p-[24rpx]" :style="$theme.variable">
        <view class="rounded-[14rpx] bg-white px-[24rpx] py-[20rpx]">
            <!-- Header -->
            <view class="text-center py-[60rpx]">
                <!-- 支付状态图片 -->
                <image class="w-[100rpx] h-[100rpx]" :src="paymentStatus['image']" />
                <!-- 支付状态文字 -->
                <view class="text-[#101010] font-medium text-2xl">
                    {{ paymentStatus['text'] }}
                </view>
                <view class="text-3xl font-medium mt-[20rpx]">
                    <price :content="orderInfo.orderAmount" main-size="56rpx" minor-size="34rpx" color="#101010" />
                </view>
            </view>
            <!-- Main -->
            <view class="text-main text-base leading-[70rpx]">
                <view class="flex justify-between">
                    <text>订单编号</text>
                    <text>{{ orderInfo.orderSn }}</text>
                </view>
                <view class="flex justify-between">
                    <text>付款时间</text>
                    <text>{{ orderInfo.payStatus ? orderInfo.payTime : '-' }}</text>
                </view>
                <view class="flex justify-between">
                    <text>支付方式</text>
                    <template v-if="orderInfo.payStatus">
                        <text>{{ orderInfo.payWay || '-' }}</text>
                    </template>
                    <template v-else>
                        <text>未支付</text>
                    </template>
                </view>
            </view>
        </view>

        <!-- Footer -->
        <view class="pt-[40rpx]">
            <template v-if="payForm.from === PayFromType.ORDER">
                <button class="text-white btn bg-primary" @click="goToOrderList">查看订单</button>
            </template>

            <template v-if="payForm.from === PayFromType.USERRECHARGE">
                <button class="text-white btn bg-primary" @click="goToRecharge">继续充值</button>
            </template>

            <navigator url="/pages/index/index" hover-class="none" open-type="reLaunch">
                <button class="btn bg-white text-main mt-[30rpx]">返回首页</button>
            </navigator>
        </view>

        <!-- Component -->
        <page-status ref="pageRef"></page-status>
    </view>
</template>

<script lang="ts" setup>
import { onLoad } from '@dcloudio/uni-app'
import { getPayStatus } from '@/api/pay'
import { PayFromType } from '@/enums/appEnums'
import { reactive, shallowRef, unref, nextTick, computed } from 'vue'
import GoodsAbnormalImage from '@/static/images/empty/goods.png'
import IconWait from '@/static/images/icon/icon_wait.png'
import IconSuccess from '@/static/images/icon/icon_pay_success.png'
import { useRouter } from 'uniapp-router-next'

const router = useRouter()
const pageRef = shallowRef()
const payForm = reactive({
    from: '',
    orderId: ''
})
const orderInfo = reactive<any>({
    order: {}
})
const paymentStatus = computed(() => {
    const status = !!orderInfo.payStatus
    return mapStatus[status ? 'succeed' : 'waiting']
})

const mapStatus = {
    succeed: {
        text: '支付成功',
        image: IconSuccess
    },
    waiting: {
        text: '等待支付',
        image: IconWait
    }
}

const getPayResult = async () => {
    try {
        const data = await getPayStatus({ ...payForm })
        Object.assign(orderInfo, data)
        unref(pageRef).close()
    } catch (error) {
        console.log('请求支付结果')
        unref(pageRef).show({
            text: error,
            src: GoodsAbnormalImage
        })
    }
}

const goToOrderList = () => {
    router.redirectTo('/packageA/pages/order_list/order_list')
}

const goToRecharge = () => {
    router.redirectTo('/packageA/pages/user_charge/user_charge')
}

onLoad(async ({ param }: any) => {
    await nextTick()
    try {
        param = JSON.parse(param)
        if (!param.from && !param.order_id) {
            throw new Error('参数有误')
        }
        payForm['from'] = param['from']
        payForm['orderId'] = param['order_id']
        await getPayResult()
    } catch (error) {
        unref(pageRef).show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('初始化支付页面', error)
    }
})
</script>

<style lang="scss" scoped>
page {
    height: 100%;
}

.btn {
    width: 100%;
    height: 84rpx;
    line-height: 84rpx;
    border-radius: 60px;
}
</style>
