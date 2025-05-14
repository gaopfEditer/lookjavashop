<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="user-set" :style="$theme.pageStyle">
        <router-navigate :to="`/packageA/pages/user_data/user_data`">
            <view class="item flex bg-white mt-[20rpx]">
                <u-avatar :src="userInfo.avatar" shape="square" :size="100"></u-avatar>
                <view class="ml-[20rpx] flex flex-1 justify-between items-center">
                    <view>
                        <view class="mb-[15rpx] text-xl font-medium">{{ userInfo.nickname }}</view>
                        <view class="text-content text-xs">账号：{{ userInfo.username }}</view>
                    </view>
                    <u-icon name="arrow-right" color="#666"></u-icon>
                </view>
            </view>
        </router-navigate>
        <view
            class="item bg-white mt-[20rpx] btn-border flex flex-1 justify-between"
            @click="handlePwd"
        >
            <view class="">登录密码</view>
            <u-icon name="arrow-right" color="#666"></u-icon>
        </view>
        <!-- #ifdef MP-WEIXIN || H5 -->

        <view
            class="item bg-white flex flex-1 justify-between"
            v-if="isWeixin"
            @click="handleBindwx"
        >
            <view class="">绑定微信</view>
            <view class="flex justify-between">
                <view class="text-muted mr-[20rpx]">
                    {{ userInfo.isBindMnp ? '已绑定' : '未绑定' }}
                </view>
                <!-- <u-icon name="arrow-right" color="#666"></u-icon> -->
            </view>
        </view>
        <!-- #endif -->
        <router-navigate :to="`/packageA/pages/agreement/agreement?type=${AgreementEnum.PRIVACY}`">
            <view class="item bg-white mt-[20rpx] btn-border flex flex-1 justify-between">
                <view class="">隐私政策</view>
                <u-icon name="arrow-right" color="#666"></u-icon>
            </view>
        </router-navigate>
        <router-navigate :to="`/packageA/pages/agreement/agreement?type=${AgreementEnum.SERVICE}`">
            <view class="item bg-white btn-border flex flex-1 justify-between">
                <view class="">服务协议</view>
                <u-icon name="arrow-right" color="#666"></u-icon>
            </view>
        </router-navigate>
        <router-navigate to="/packageA/pages/as_us/as_us">
            <view class="item bg-white flex flex-1 justify-between">
                <view class="">关于我们</view>
                <view class="flex justify-between">
                    <view class="text-muted mr-[20rpx]">
                        {{ appStore.config.version }}
                    </view>
                    <u-icon name="arrow-right" color="#666"></u-icon>
                </view>
            </view>
        </router-navigate>
        <router-navigate :to="`/packageA/pages/cancel/cancel`">
            <view class="item bg-white btn-border flex flex-1 justify-between">
                <view class="">注销账号</view>
                <u-icon name="arrow-right" color="#666"></u-icon>
            </view>
        </router-navigate>

        <view class="mt-[60rpx] mx-[26rpx]">
            <u-button type="primary" shape="circle" @click="logoutHandle"> 退出登录 </u-button>
        </view>

        <u-action-sheet
            :list="list"
            v-model="show"
            @click="handleClick"
            :safe-area-inset-bottom="true"
        ></u-action-sheet>
    </view>
</template>

<script setup lang="ts">
import { logout, apiBindwx, apiOaBindwx } from '@/api/account'
import { getUserInfo } from '@/api/user'
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { AgreementEnum } from '@/enums/agreementEnums'
import { isWeixinClient, getClient } from '@/utils/client'
import { useRouter } from 'uniapp-router-next'

import { ClientEnum } from '@/enums/appEnums'
// #ifdef H5
import wechatOa from '@/utils/wechat'
// #endif
import cache from '@/utils/cache'
import { BACK_URL, SHARE_CODE } from '@/enums/cacheEnums'
import theme from '@/mixins/theme'
import { onLoad } from '@dcloudio/uni-app'
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const userInfo = ref({
    avatar: '',
    nickname: '',
    username: '',
    isBindMnp: '',
    isPassword: ''
})
const list = ref([
    {
        text: '修改密码'
    },
    {
        text: '忘记密码'
    }
])

const isWeixin = ref(true)
// #ifdef H5
isWeixin.value = isWeixinClient()
// #endif

const show = ref(false)

// 获取用户信息
const getUser = async () => {
    const res = await getUserInfo()
    userInfo.value = res
}

// 修改/忘记密码
const handleClick = (index: number) => {
    switch (index) {
        case 0:
            router.navigateTo('/packageA/pages/change_password/change_password')
            break
        case 1:
            router.navigateTo('/packageA/pages/forget_pwd/forget_pwd')
            break
    }
}

const handlePwd = () => {
    if (!userInfo.value.isPassword)
        return router.navigateTo('/packageA/pages/change_password/change_password?type=set')
    show.value = true
}

// 退出登录
const logoutHandle = () => {
    uni.showModal({
        content: '是否退出登录？',
        // confirmColor: "",
        success: async ({ cancel }) => {
            if (cancel) return
            await logout()
            userStore.logout()
            router.reLaunch('/pages/login/login')
        }
    })
}
const handleBindwx = async () => {
    uni.showLoading({
        title: '请稍后...'
    })
    try {
        if (getClient() == ClientEnum.MP_WEIXIN) {
            const { code }: any = await uni.login({
                provider: 'weixin'
            })
            const data = await apiBindwx({
                code
            })
            uni.$u.toast('绑定成功')
            getUser()
        } else if (getClient() == ClientEnum.OA_WEIXIN) {
            wechatOa.getUrl()
        }
    } catch (error: any) {
        uni.hideLoading()

        uni.$u.toast(error)
    } finally {
        uni.hideLoading()
    }
}

onShow(() => {
    getUser()
})

onLoad((e) => {
    //const options = router
    if (getClient() == ClientEnum.OA_WEIXIN) {
        //公众号绑定
        if (e.code != undefined) {
            uni.showLoading({
                title: '请稍后...'
            })
            apiOaBindwx({ code: e.code })
                .then((rep) => {
                    uni.$u.toast('绑定成功')
                    getUser()
                    //刷新当前Ui
                    uni.reLaunch({
                        url: '/pages/user/user'
                    })
                })
                .catch((error) => {
                    uni.hideLoading()
                    uni.$u.toast(error)
                    uni.reLaunch({
                        url: '/pages/user/user'
                    })
                })
                .finally((rep) => {
                    uni.hideLoading()
                })
        }
    }
})
</script>

<style lang="scss" scoped>
.user-set {
    .item {
        padding: 30rpx;
    }

    .btn-border {
        border-bottom: 2rpx solid #f8f8f8;
    }
}
</style>
