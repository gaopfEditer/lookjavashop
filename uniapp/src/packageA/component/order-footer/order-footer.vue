<template>
    <view
        class="flex m-[20rpx] justify-between items-center"
        v-if="cancel || evaluate || logistics || pay || cancel || confirm"
        @click.stop
        :style="$theme.variable"
    >
        <!-- Left Tips -->
        <view class="text-primary">
            <template v-if="timeStamp && status == 1">
                <!-- <view class="flex" v-if="timeStamp > 0">
                    <text class="ml-[10rpx]">支付剩余</text>
                    <u-count-down
                        :timestamp="timeStamp"
                        format="mm分ss秒"
                        @end="timeStamp = 0"
                        color="#2073F4"
                        class="text-base font-normal text-primary"
                    />
                </view> -->
            </template>
        </view>

        <view class="flex">
            <!-- 取消订单 -->
            <template v-if="cancel">
                <button
                    class="btn plain-gray"
                    @click="handleOrderCancel(orderId)"
                    style="color: black"
                >
                    取消订单
                </button>
            </template>

            <!-- 去评价 -->
            <template v-if="evaluate">
                <button
                    class="btn plain-gray"
                    @click="goPage('/packageA/pages/comment_list/comment_list')"
					style="color: black"
                >
                    去评价
                </button>
            </template>

            <!-- 查看物流 -->
            <template v-if="logistics && order.deliveryType != 2">
                <button
                    class="btn plain-gray"
                    @click="goPage(`/packageA/pages/order_logistics/order_logistics?id=${orderId}`)"
					style="color: black"
                >
                    查看物流
                </button>
            </template>

            <template v-if="del">
                <button class="btn plain-gray" @click="handleOrderDel(orderId)" style="color: black">删除订单</button>
            </template>

            <!-- 申请售后 -->
            <template v-if="apply_refund">
                <button
                    class="btn plain-gray"
                    @click="goPage(`/packageA/pages/apply_refund/apply_refund?id=${orderId}`)"
					style="color: black"
                >
                    申请售后
                </button>
            </template>

            <!-- 确认订单 -->
            <template v-if="confirm && order.deliveryType != 2">
                <button class="btn plain-primary" @click="handleOrderConfirm(orderId)">
                    确认收货
                </button>
            </template>

            <!-- 去支付 -->
            <template v-if="pay">
                <button class="btn full-primary" @click="handlePayment">去支付</button>
            </template>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { ref, watchEffect } from 'vue'
import { PayFromType } from '@/enums/appEnums'
import { apiOrderCancel, apiOrderDel, apiOrderConfirm, apiGetMchId } from '@/api/order'
import { useRouter } from 'uniapp-router-next'
import { onShow } from '@dcloudio/uni-app'
const router = useRouter()

const emit = defineEmits(['refresh'])

const props = withDefaults(
    defineProps<{
        status: number // 订单状态
        orderId?: number // ID
        cancel?: number // 取消订单按钮
        evaluate?: number // 评价按钮
        confirm?: number
        logistics: number
        pay?: number // 支付按钮
        del?: number
        cancel_time?: number
        apply_refund?: number
        payWay?: number
        orderSn?: string // 订单编号
		order?: Object
    }>(),
    {
        status: 0,
        orderId: 0,
        cancel: 0,
        evaluate: 0,
        confirm: 0,
        logistics: 0,
        pay: 0,
        del: 0,
        cancel_time: 0,
        apply_refund: 0,
		order: {}
    }
)

const timeStamp = ref<number | null>(0)

watchEffect(() => {
    timeStamp.value = props.cancel_time * 1000
})

// 页面跳转
const goPage = (url: string) => {
    router.navigateTo(url)
}

// 取消订单
const handleOrderCancel = async (id: number): Promise<void> => {
    const modelRes: any = await uni.showModal({
        title: '温馨提示',
        content: '确认取消该订单吗？'
    })
    if (modelRes.cancel) return
    await apiOrderCancel({ id: id })
    emit('refresh')
}

// 支付
const handlePayment = () => {
    const param = {
        order_id: props.orderId,
        from: PayFromType.ORDER
    }
    uni.$emit('payment', param)
}

//删除订单
const handleOrderDel = async (id: number) => {
    const modelRes: any = await uni.showModal({
        title: '温馨提示',
        content: '是否确认删除订单！'
    })
    if (modelRes.cancel) return
    await apiOrderDel({ id: id })
    await emit('refresh')
}

// 确认订单
const handleOrderConfirm = async (id: number): Promise<void> => {
    // const res = await apiGetMchId({payWay: props.payWay})
    // console.log('res', res)
    //    //拉起确认收货组件
    //    if (wx.openBusinessView) {
    //        console.log('...',props.orderSn,res.mch_id)

    //        wx.openBusinessView({
    //            businessType: 'weappOrderConfirm',
    //            extraData: {
    //                merchant_id: res.mch_id,
    // 			merchant_trade_no: props.orderSn
    //            },
    //            success: async (res) => {
    //                //dosomething
    //                console.log('res', res)
    //            },
    //            fail(err) {
    // 			console.log('err', err)
    //                //dosomething
    //            },
    //            complete() {
    //                //dosomething
    //            }
    //        })
    //    } else {
    //        //引导用户升级微信版本
    //        console.log('请升级版本')
    //    }
    const modelRes: any = await uni.showModal({
        title: '温馨提示',
        content: '确认已收货该商品吗？'
    })
    if (modelRes.cancel) return
    await apiOrderConfirm({ id: id })
    emit('refresh')
}

onShow((options) => {
    console.log('options', options)
})
</script>

<style lang="scss" scoped>
.btn {
    font-size: 28rpx;
    height: 62rpx;
    padding: 0 30rpx;
    line-height: 62rpx;
    border-radius: 60px;
    margin-left: 20rpx;
    margin-top: 0;
}
</style>
