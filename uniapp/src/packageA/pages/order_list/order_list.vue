<template>
    <view class="order-list" :style="$theme.pageStyle">
        <!-- 导航栏 -->
        <navbar
            title="订单列表"
            :background="{ background: `${$theme.navBgColor}` }"
            :titleColor="`${$theme.navColor}`"
            :immersive="false"
        ></navbar>

        <tabs
            :current="current"
            @change="handleChange"
            height="80"
            bar-width="60"
            :is-scroll="false"
            v-if="current != -1"
            :active-color="$theme.primaryColor"
        >
            <tab v-for="(item, index) in tabList" :key="index" :name="item.name">
                <view
                    class="orderList"
                    :style="{
                        height: `calc(100vh - ${
                            navbarParam.statusBarHeight + navbarHeight
                        }px - 40px - env(safe-area-inset-bottom)`
                    }"
                    v-if="isLogin"
                >
                    <order-list :type="item.type" :i="index" :index="current"></order-list>
                </view>
                <button v-else class="btn bg-primary" @click="toLogin">立即登录</button>
            </tab>
        </tabs>

        <!-- Component 支付 -->
        <payment
            v-if="payState.showPay || payState.showCheck"
            v-model:show="payState.showPay"
            v-model:show-check="payState.showCheck"
            :order-id="payState.orderId"
            :from="payState.from"
            :redirect="payState.redirect"
            @success="handlePayResult"
        />
    </view>
</template>

<script lang="ts" setup>
import { ref, reactive, nextTick, shallowRef, unref, computed } from 'vue'
import { onLoad, onShow, onUnload, onHide } from '@dcloudio/uni-app'
import OrderList from './component/order-list.vue'
import navbar from '@/components/navbar/navbar.vue'
import { useRouter } from 'uniapp-router-next'
import { storeToRefs } from 'pinia'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const router = useRouter()
const { isLogin } = storeToRefs(userStore)

const systemInfo = uni.getSystemInfoSync()
const payState = reactive({
    orderId: '',
    from: '',
    showPay: false,
    showCheck: false,
    redirect: '/packageA/pages/order_list/order_list'
})

const tabList = reactive<any>([
    {
        name: '全部',
        type: 0
    },
    {
        name: '待付款',
        type: 1
    },
    {
        name: '待发货',
        type: 2
    },
    {
        name: '待收货',
        type: 3
    },
    {
        name: '已完成',
        type: 4
    },
    {
        name: '已取消',
        type: 5
    }
])
const current = ref<number>(-1)

const navbarParam = reactive({
    height: 0,
    statusBarHeight: systemInfo.statusBarHeight
})

// 转换字符数值为真正的数值
const navbarHeight = computed(() => {
    // #ifdef APP-PLUS || H5
    return navbarParam.height ? navbarParam.height : 44
    // #endif
    // #ifdef MP
    const height = systemInfo.osName == 'ios' ? 44 : 48
    return navbarParam.height ? navbarParam.height : height
    // #endif
})

const handleChange = (index: number) => {
    current.value = Number(index)
}

const handlePayResult = async () => {
    payState.showPay = false
    payState.showCheck = false
    router.redirectTo(`/packageA/pages/order_list/order_list`)
}
const toLogin = () => {
	router.redirectTo(`/pages/login/login`)
}

onLoad((options: any) => {
    current.value = Number(options.type) + 1 || 0

    // h5支付用于弹起手动确认支付弹窗
    if (options?.checkPay) {
        payState.orderId = options.order_id
        payState.from = options.from
        payState.showCheck = true
        return
    }
})

onShow(() => {
    uni.$on('payment', (param: any) => {
        payState.orderId = param.order_id
        payState.from = param.from
        payState.showPay = true
        payState.redirect =
            payState.redirect + `?order_id=${param.order_id}&from=${param.from}&checkPay=true`
    })
})

onUnload(() => {
    uni.$off(['payment'])
    payState.showPay = false
})

onHide(() => {
    uni.$off(['payment'])
})
</script>

<style lang="scss">
.orderList {
    height: calc(100vh - 40px - env(safe-area-inset-bottom));
}
.btn {
    height: 100rpx;
    color: white;
    width: 200rpx;
    line-height: 100rpx;
    margin-left: auto;
    margin-right: auto;
    margin-top: 100rpx;
    border-radius: 20rpx;
}
</style>
