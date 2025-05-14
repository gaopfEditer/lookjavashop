<template>
    <u-popup
        v-model="showPay"
        mode="bottom"
        safe-area-inset-bottom
        :mask-close-able="false"
        border-radius="14"
        closeable
        @close="handleClose"
        z-index="101"
    >
        <view class="h-[900rpx]">
            <view class="flex flex-col w-full h-full payment">
                <view class="header py-[50rpx] flex flex-col items-center">
                    <price
                        :content="payData?.orderAmount"
                        mainSize="44rpx"
                        minorSize="40rpx"
                        fontWeight="500"
                        color="#333"
                    >
                    </price>

                    <template v-if="payData?.cancelTime">
                        <view class="count-down flex items-center rounded-[30px] text-xs">
                            <text>支付剩余时间</text>
                            <u-count-down
                                :timestamp="getCountDown"
                                format="mm:ss"
                                :font-size="22"
                            />
                        </view>
                    </template>
                </view>
                <view class="main flex-1 mx-[20rpx]">
                    <pay-way-list
                        v-model="payWay"
                        :user_money="payData.userMoney"
                        :lists="payData.list"
                    />
                </view>

                <view class="submit-btn p-[20rpx]">
                    <u-button @click="handlePay" shape="circle" type="primary" :loading="isLock">
                        立即支付
                    </u-button>
                </view>
            </view>
        </view>

        <page-status v-if="showPay" :pageContainer="{ height: '900rpx' }" ref="pageRef" />
    </u-popup>

    <u-popup
        class="pay-popup"
        v-model="showCheckPay"
        round
        mode="center"
        borderRadius="10"
        :maskCloseAble="false"
        z-index="101"
        @close="handleClose"
    >
        <view class="content bg-white w-[560rpx] p-[40rpx]">
            <view class="text-2xl font-medium text-center"> 支付确认 </view>
            <view class="pt-[30rpx] pb-[40rpx]">
                <view> 请在微信内完成支付，如果您已支付成功，请点击`已完成支付`按钮 </view>
            </view>
            <view class="flex justify-center">
                <view class="flex justify-center flex-1">
                    <u-button
                        shape="circle"
                        type="primary"
                        plain
                        size="medium"
                        hover-class="none"
                        :customStyle="{ width: '70%' }"
                        @click="queryPayResult(false)"
                    >
                        重新支付
                    </u-button>
                </view>
                <view class="flex justify-center flex-1">
                    <u-button
                        shape="circle"
                        type="primary"
                        size="medium"
                        hover-class="none"
                        :customStyle="{ width: '70%' }"
                        @click="queryPayResult()"
                    >
                        已完成支付
                    </u-button>
                </view>
            </view>
        </view>

        <page-status v-if="showCheckPay" :pageContainer="{ height: '0rpx' }" ref="pageRef" />
    </u-popup>
</template>

<script lang="ts" setup>
import { pay, PayWayEnum } from '@/utils/pay'
import { getPayWay, prepay, getPayStatus } from '@/api/pay'
import type { PayParamFormType } from '@/api/pay'
import { computed, ref, watch, shallowRef, unref, nextTick } from 'vue'
import { useLockFn } from '@/hooks/useLockFn'
import { series } from '@/utils/util'
import { ClientEnum, PayStatusEnum } from '@/enums/appEnums'
import { useUserStore } from '@/stores/user'
import { client } from '@/utils/client'
import GoodsAbnormalImage from '@/static/images/empty/goods.png'
import { useRouter } from 'uniapp-router-next'

import PayWayList from './component/pay-way-list.vue'

/*
页面参数 orderId：订单id，from：订单来源
*/

const props = defineProps({
    show: {
        type: Boolean,
        required: true
    },
    showCheck: {
        type: Boolean
    },
    // 订单id
    orderId: {
        type: Number,
        required: true
    },
    //订单来源
    from: {
        type: String,
        required: true
    },
    //h5微信支付回跳路径，一般为拉起支付的页面路径
    redirect: {
        type: String
    }
})

const emit = defineEmits(['update:showCheck', 'update:show', 'close', 'success', 'fail'])

const router = useRouter()
const payWay = ref()
const pageRef = shallowRef()
const payData = ref<any>({
    orderAmount: '',
    cancelTime: 0,
    userMoney: 0,
    list: []
})

// 倒计时
const getCountDown = computed(() => {
    return payData.value?.cancelTime * 1000
})

const showCheckPay = computed({
    get() {
        return props.showCheck
    },
    set(value) {
        // unref(pageRef).close()
        // pageRef.value.close()
        emit('update:showCheck', value)
    }
})

const showPay = computed({
    get() {
        return props.show
    },
    set(value) {
        emit('update:show', value)
    }
})

const handleClose = () => {
    showPay.value = false
    // emit('close')
    handlePayResult(PayStatusEnum.FAIL)
}
const getPayData = async () => {
    await nextTick()
    try {
        payData.value = await getPayWay({
            orderId: props.orderId,
            from: props.from
        })
        unref(pageRef).close()
        const checkPay =
            payData.value.list.find((item: any) => item.isDefault) || payData.value.list[0]
        payWay.value = checkPay?.id
    } catch (error) {
        unref(pageRef)?.show({
            text: error,
            src: GoodsAbnormalImage
        })
    }
}

const userStore = useUserStore()

const payment = (() => {
    // 查询是否绑定微信
    const checkIsBindWx = async () => {
        if (
            userStore.userInfo.isBindWechat == 0 &&
            [ClientEnum.OA_WEIXIN, ClientEnum.MP_WEIXIN].includes(client) &&
            payWay.value == PayWayEnum.WECHAT
        ) {
            const res: any = await uni.showModal({
                title: '温馨提示',
                content: '当前账号未绑定微信，无法完成支付',
                confirmText: '去绑定'
            })
            if (res.confirm) {
                router.navigateTo('/packageA/pages/user_set/user_set')
            }
            return Promise.reject()
        }
    }

    // 调用预支付
    const prepayTask = async () => {
        uni.showLoading({
            title: '正在支付中'
        })
        //根据不同环境做不同的跳转
        let envRedirect =
            uni.getSystemInfoSync().hostName == 'WeChat'
                ? props.redirect
                : `${location.origin}/mobile${props.redirect}`
        let redirectUrl = payWay.value == PayWayEnum.WECHAT ? props.redirect : envRedirect
        const data = await prepay({
            orderId: props.orderId,
            scene: props.from,
            payWay: payWay.value,
            // redirectUrl: props.redirect
            redirectUrl: redirectUrl
        })
        return data
    }

    //拉起支付
    const payTask = async (data: any) => {
        try {
            const res = await pay.payment(payWay.value, data)
            return res
        } catch (error) {
            return Promise.reject(error)
        }
    }
    return series(checkIsBindWx, prepayTask, payTask)
})()

const { isLock, lockFn: handlePay } = useLockFn(async () => {
    try {
        const res: PayStatusEnum = await payment()
        handlePayResult(res)
        // uni.hideLoading()
    } catch (error) {
        // uni.hideLoading()
        if (error == undefined || error == '余额不足') return
        handlePayResult(PayStatusEnum.FAIL)
        console.log(error)
    }
})

const handlePayResult = (status: PayStatusEnum) => {
    switch (status) {
        case PayStatusEnum.SUCCESS:
            emit('success')
            break
        case PayStatusEnum.FAIL:
            emit('fail')
            break
    }
}

const queryPayResult = async (confirm = true) => {
    const res = await getPayStatus({
        orderId: props.orderId,
        from: props.from
    })

    if (res.success == 0) {
        if (confirm == true) {
            uni.$u.toast('您的订单还未支付，请重新支付')
        }
        showPay.value = true
        // handlePayResult(PayStatusEnum.FAIL)
    } else {
        if (confirm == false) {
            uni.$u.toast('您的订单已经支付，请勿重新支付')
        }
        handlePayResult(PayStatusEnum.SUCCESS)
    }
    showCheckPay.value = false
}

watch(
    () => props.show,
    async (value) => {
        if (value) {
            if (!props.orderId) {
                await nextTick()
                unref(pageRef)?.show({
                    text: '订单信息错误，无法查询到订单信息',
                    src: GoodsAbnormalImage
                })
                return
            }
            getPayData()
        }
    },
    {
        immediate: true
    }
)
</script>

<style lang="scss">
.payway-lists {
    .payway-item {
        border-bottom: 1px solid;
        @apply border-page;
    }
}

.count-down {
    color: #666666;
    padding: 10rpx 30rpx 10rpx 30rpx;
    background-color: #ffffff;
}
</style>
