<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="container">
        <!-- Header -->
        <view class="bg-white header">
            <view class="flex items-center justify-between text-white card">
                <view class="pl-[30rpx]">
                    <text class="text-xs">我的余额</text>
                    <!-- 我的余额 -->
                    <view class="text-[76rpx] pt-[20rpx]">{{ wallet.userMoney || '0.00' }}</view>
                </view>
                <!-- 充值按钮 -->
                <navigator url="/packageA/pages/user_charge/user_charge" hover-class="none">
                    <view v-if="wallet.openRecharge" class="text-lg font-medium charge-btn text-primary">去充值</view>
                </navigator>
            </view>
        </view>

        <!-- Main -->
        <tabs :current="current" @change="handleChange" height="100" bar-width="60" :is-scroll="false"
            :active-color="$theme.primaryColor">
            <tab v-for="(item, index) in tabList" :key="index" :name="item.name">
                <account-log-list :type="item.type" :index="current" :i="index"></account-log-list>
            </tab>
        </tabs>
    </view>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { rechargeConfig } from '@/api/recharge'
import { onLoad, onShow } from '@dcloudio/uni-app'
import AccountLogList from './component/account-log-list.vue'

const wallet = reactive<any>({
    userMoney: '',
    openRecharge: 0,
    minRechargeMoney: 0
})
const tabList = ref([
    {
        name: '全部',
        type: 0
    },
    {
        name: '收入',
        type: 1
    },
    {
        name: '支出',
        type: 2
    }
])
const current = ref<number>(0)

const initWallet = async (): Promise<void> => {
    try {
        const data = await rechargeConfig()
        Reflect.ownKeys(data).map((item: any) => {
            wallet[item] = data[item]
        })
    } catch (e) {
        console.log('初始化钱包请求=>', e)
    }
}

const handleChange = (index: number) => {
    current.value = Number(index)
}

onShow(() => {
    initWallet()
})
</script>

<style lang="scss">
.container {
    display: flex;
    height: 100vh;
    overflow: hidden;
    flex-direction: column;
}

.header {
    padding: 48rpx 30rpx 0 30rpx;

    .card {
        height: 240rpx;
        margin-bottom: 12rpx;
        border-radius: 14rpx;
        background: linear-gradient(90deg, var(--theme-color) 0, var(--theme-dark-color) 100%);

        // 充值按钮
        .charge-btn {
            height: 72rpx;
            width: 180rpx;
            line-height: 72rpx;
            text-align: center;
            color: $u-type-primary;
            border-radius: 37rpx 0 0 37rpx;
            background: rgba($color: #ffffff, $alpha: 0.8);
        }
    }
}
</style>
