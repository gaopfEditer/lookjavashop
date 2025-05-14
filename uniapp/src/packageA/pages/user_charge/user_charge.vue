<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="charge">
        <!-- Header -->
        <view class="flex justify-between py-[30rpx] text-xs">
            <view>
                <text class="text-muted">当前可用余额</text>
                <text class="text-lg text-primary"> ¥{{ wallet.userMoney || '-' }}</text>
            </view>
            <view class="text-main" @click="goPage('/packageA/pages/user_charge_record/user_charge_record')">
                充值记录
            </view>
        </view>

        <view class="charge-content">
            <view class="text-[36rpx] text-content font-medium">充值金额</view>
            <view class="input">
                <!-- 充值输入 -->
                <input type="text" @input="handleCheckMoney" v-model="money" placeholder="0.00" />
            </view>
        </view>

        <!-- 充值按钮 -->
        <view class="charge-btn">
            <u-button :loading="isLock" type="primary" shape="circle" @click="rechargeLock">
                立即充值
            </u-button>
        </view>

        <!-- Component -->
        <payment v-if="payState.showPay" v-model:show="payState.showPay" v-model:show-check="payState.showCheck"
            :order-id="payState.orderId" :from="payState.from" :redirect="payState.redirect" @success="handlePaySuccess" />
    </view>
</template>

<script lang="ts" setup>
import { ref, reactive, nextTick } from 'vue'
import { onShow, onLoad } from '@dcloudio/uni-app'
import { recharge, rechargeConfig } from '@/api/recharge'
import { useLockFn } from '@/hooks/useLockFn'
import { PayFromType } from '@/enums/appEnums'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const payState = reactive({
    orderId: '',
    from: 'recharge',
    showPay: false,
    showCheck: false,
    redirect: '/packageA/pages/user_charge/user_charge'
})

const wallet = reactive<any>({
    userMoney: '',
    minRechargeMoney: 0
})
const money = ref<string>('')

/**
 * @return { void }
 * @description 获取钱包数据
 */
const initWalletData = async (): Promise<void> => {
    try {
        const data = await rechargeConfig()
        Reflect.ownKeys(data).map((item: any) => {
            wallet[item] = data[item]
        })
    } catch (e) {
        console.log('初始化钱包请求=>', e)
    }
}

/**
 * @return { void }
 * @description 处理金额
 */
const handleCheckMoney = async ({ detail }: any) => {
    let val = detail.value.replace(/(^\s*)|(\s*$)/g, '')
    if (!val) {
        money.value = ''
        return
    }
    const reg = /[^\d.]/g
    // 只能是数字和小数点，不能是其他输入
    val = val.replace(reg, '')
    // 保证第一位只能是数字，不能是点
    val = val.replace(/^\./g, '')
    // 小数只能出现1位
    val = val.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.')
    // 小数点后面保留2位
    val = val.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
    await nextTick()
    money.value = val
}

const { isLock, lockFn: rechargeLock } = useLockFn(async () => {
    const minNum = wallet.minRechargeMoney
    if (!money.value) return uni.$u.toast('请输入充值金额')
    if (minNum == 0 && Number(money.value) == minNum) {
        return uni.$u.toast(`充值金额必须大于0`)
    }
    if (Number(money.value) < minNum) return uni.$u.toast(`最低充值金额${minNum}`)
    const data = await recharge({
        orderAmount: money.value
    })
    payState.orderId = data.orderId
    payState.from = PayFromType.USERRECHARGE
    payState.showPay = true
})

const handlePaySuccess = async () => {
    payState.showPay = false
    payState.showCheck = false
    const param = JSON.stringify({
        order_id: payState.orderId,
        from: payState.from
    })
    router.navigateTo(`/pages/payment_result/payment_result?param=${param}`)
}

/**
 * @param { url } 跳转链接
 * @return { void }
 * @description 跳转页面
 */
const goPage = (url: string) => {
    router.navigateTo(url)
}

onLoad((options: any) => {
    // h5支付用于弹起手动确认支付弹窗
    if (options?.checkPay) {
        payState.orderId = options.id
        payState.from = options.from
        payState.showCheck = true
    }
})

onShow(() => {
    initWalletData()
})
</script>

<style lang="scss">
.charge {
    padding: 0 30rpx;

    // 充值内容
    .charge-content {
        width: 100%;
        height: 320rpx;
        padding: 040rpx;
        border-radius: 20rpx;
        background-color: #ffffff;

        .input {
            padding: 24rpx 0;
            font-size: 66rpx;
            border-bottom: 1rpx solid #e5e5e5;

            input {
                padding-left: 10rpx;
                font-size: 66rpx;
                height: 80rpx;
            }
        }
    }

    // 充值按钮
    .charge-btn {
        padding: 40rpx 0 60rpx 0;

        button {
            height: 84rpx;
            line-height: 84rpx;
            border-radius: 60px;
            // background: linear-gradient(79.04deg, #f95f2f 0%, #ff2c3c 100%);
        }
    }

    .pay-popup {
        .content {
            padding: 40rpx;
            padding-bottom: 0;
            text-align: center;
            width: 560rpx;
            border-radius: 20rpx;
        }

        .img-icon {
            width: 168rpx;
            height: 118rpx;
            display: inline-block;
        }
    }
}
</style>
