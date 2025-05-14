<template>
    <view
        class="bg-white login min-h-full flex flex-col items-center px-[40rpx] pt-[80rpx] box-border"
        :style="$theme.pageStyle"
    >
        <view>
            <u-image :src="appStore.config.website.logo" mode="widthFix" height="160" width="160" />
        </view>

        <view class="w-full mt-[60rpx] pb-[60rpx]">
            <!-- #ifdef MP-WEIXIN || H5 -->
            <block v-if="!phoneLogin">
                <view class="mt-[80rpx]" v-if="isOpenOtherAuth && isWeixin">
                    <u-button
                        type="primary"
                        @click="wxLogin"
                        :customStyle="{ height: '100rpx' }"
                        hover-class="none"
                    >
                        用户一键登录
                    </u-button>
                </view>
                <!-- #endif -->
                <view class="mt-[40rpx]">
                    <u-button
                        @click="phoneLogin = !phoneLogin"
                        :customStyle="{ height: '100rpx' }"
                        hover-class="none"
                    >
                        手机号登录
                    </u-button>
                </view>
            </block>
            <block v-if="phoneLogin">
                <template
                    v-if="loginWay == LoginWayEnum.ACCOUNT && includeLoginWay(LoginWayEnum.ACCOUNT)"
                >
                    <view
                        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[80rpx]"
                    >
                        <u-input
                            class="flex-1"
                            v-model="formData.username"
                            :border="false"
                            placeholder="输入账号/手机号码"
                        />
                    </view>
                    <view
                        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
                    >
                        <u-input
                            class="flex-1"
                            v-model="formData.password"
                            type="password"
                            placeholder="请输入密码"
                            :border="false"
                        />
                        <navigator url="/packageA/pages/forget_pwd/forget_pwd" hover-class="none">
                            <view
                                class="border-l border-solid border-0 border-light pl-3 text-muted leading-4 ml-3"
                            >
                                忘记密码？
                            </view>
                        </navigator>
                    </view>
                </template>
                <template
                    v-if="loginWay == LoginWayEnum.MOBILE && includeLoginWay(LoginWayEnum.MOBILE)"
                >
                    <view
                        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[80rpx]"
                    >
                        <u-input
                            class="flex-1"
                            v-model="formData.mobile"
                            :border="false"
                            placeholder="请输入手机号码"
                        />
                    </view>
                    <view
                        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
                    >
                        <u-input
                            class="flex-1"
                            v-model="formData.code"
                            placeholder="请输入验证码"
                            :border="false"
                        />
                        <view
                            class="border-l border-solid border-0 border-light pl-3 leading-4 ml-3 w-[180rpx]"
                            @click="sendSms"
                        >
                            <u-verification-code
                                ref="uCodeRef"
                                :seconds="60"
                                @change="codeChange"
                                change-text="x秒"
                            />
                            <text :class="formData.mobile ? 'text-primary' : 'text-muted'">
                                {{ codeTips }}
                            </text>
                        </view>
                    </view>
                </template>
            </block>

            <view class="mt-[40rpx]" v-if="isOpenAgreement">
                <u-checkbox v-model="isCheckAgreement" shape="circle">
                    <view class="text-xs flex">
                        已阅读并同意
                        <view @click.stop>
                            <router-navigate
                                class="text-primary"
                                hover-class="none"
                                to="/packageA/pages/agreement/agreement?type=service"
                            >
                                《服务协议》
                            </router-navigate>
                        </view>

                        和
                        <view @click.stop>
                            <router-navigate
                                class="text-primary"
                                hover-class="none"
                                to="/packageA/pages/agreement/agreement?type=privacy"
                            >
                                《隐私协议》
                            </router-navigate>
                        </view>
                    </view>
                </u-checkbox>
            </view>
            <block v-if="phoneLogin">
                <view class="mt-[60rpx]">
                    <u-button
                        type="primary"
                        @click="handleLogin(formData.scene)"
                        :customStyle="{
                            height: '100rpx',
                            opacity: DisableStyle ? '1' : '0.5'
                        }"
                        hover-class="none"
                    >
                        登 录
                    </u-button>
                </view>

                <view class="text-content flex justify-between mt-[40rpx]">
                    <view
                        class="flex-1 flex"
                        v-if="
                            loginWay == LoginWayEnum.MOBILE && includeLoginWay(LoginWayEnum.ACCOUNT)
                        "
                    >
                        已有账号，使用
                        <view
                            @click="changeLoginWay(LoginTypeEnum.ACCOUNT, LoginWayEnum.ACCOUNT)"
                            class="text-primary"
                        >
                            密码登录
                        </view>
                    </view>
                    <view
                        class="flex-1 flex"
                        v-if="
                            loginWay == LoginWayEnum.ACCOUNT && includeLoginWay(LoginWayEnum.MOBILE)
                        "
                    >
                        已有账号，使用
                        <view
                            @click="changeLoginWay(LoginTypeEnum.MOBILE, LoginWayEnum.MOBILE)"
                            class="text-primary"
                        >
                            验证码登录
                        </view>
                    </view>

                    <router-navigate to="/pages/register/register" hover-class="none"
                        >注册账号</router-navigate
                    >
                </view>
            </block>
        </view>
        <!-- 协议弹框 -->
        <u-modal
            v-model="showModel"
            show-cancel-button
            :show-title="false"
            @confirm=";(isCheckAgreement = true), (showModel = false)"
            @cancel="showModel = false"
            :confirm-color="$theme.primaryColor"
        >
            <view class="text-center px-[70rpx] py-[60rpx]">
                <view> 请先阅读并同意 </view>
                <view class="flex justify-center">
                    <navigator data-theme="" url="/pages/agreement/agreement?type=service">
                        <view class="text-primary">《服务协议》</view>
                    </navigator>
                    和
                    <navigator url="/pages/agreement/agreement?type=privacy">
                        <view class="text-primary">《隐私协议》</view>
                    </navigator>
                </view>
            </view>
        </u-modal>
        <mplogin-popup
            v-model:show="showLoginPopup"
            :logo="websiteConfig.logo"
            :title="websiteConfig.name"
            @update="handleUpdateUser"
        />
    </view>
</template>

<script setup lang="ts">
import { mobileLogin, accountLogin, mnpLogin } from '@/api/account'
import { smsSend } from '@/api/app'
import { updateUser } from '@/api/user'
import { SMSEnum } from '@/enums/appEnums'
import { BACK_URL, SHARE_CODE } from '@/enums/cacheEnums'
import { useLockFn } from '@/hooks/useLockFn'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import cache from '@/utils/cache'
import { isWeixinClient } from '@/utils/client'
// #ifdef H5
import wechatOa from '@/utils/wechat'
// #endif
import { onLoad, onShow } from '@dcloudio/uni-app'
import { computed, reactive, ref, shallowRef, watch } from 'vue'
import { useRouter } from 'uniapp-router-next'

enum LoginTypeEnum {
    MOBILE = 'mobile',
    ACCOUNT = 'account'
}

enum LoginWayEnum {
    ACCOUNT = 1,
    MOBILE = 2
}

enum LoginAuthEnum {
    WX = 1,
    QQ = 2
}
const isWeixin = ref(true)
const showLoginPopup = ref(false)
// #ifdef H5
isWeixin.value = isWeixinClient()
// #endif

const userStore = useUserStore()
const appStore = useAppStore()
const phoneLogin = ref(false)
const showModel = ref(false)

const router = useRouter()
const websiteConfig = computed(() => appStore.getWebsiteConfig)
const uCodeRef = shallowRef()
const loginWay = ref<LoginWayEnum>()
const codeTips = ref('')
const isCheckAgreement = ref(false)
const loginData = ref<any>({})
const formData = reactive({
    scene: '',
    username: '',
    password: '',
    code: '',
    mobile: ''
})

const codeChange = (text: string) => {
    codeTips.value = text
}

const sendSms = async () => {
    if (!formData.mobile) return
    if (uCodeRef.value?.canGetCode) {
        await smsSend({
            scene: SMSEnum.LOGIN,
            mobile: formData.mobile
        })
        uni.$u.toast('发送成功')
        uCodeRef.value?.start()
    }
}
const DisableStyle = computed(() => {
    if (formData.scene == LoginTypeEnum.ACCOUNT && formData.username && formData.password) {
        console.log(formData.scene)

        return true
    } else if (formData.scene == LoginTypeEnum.MOBILE && formData.mobile && formData.code) {
        console.log(formData.scene)

        return true
    } else {
        return false
    }
})
const changeLoginWay = (type: LoginTypeEnum, way: LoginWayEnum) => {
    formData.scene = type
    loginWay.value = way
}

const includeLoginWay = (way: LoginWayEnum) => {
    return appStore.getLoginConfig.loginWay.includes(way)
}

const includeAuthWay = (way: LoginAuthEnum) => {
    return appStore.getLoginConfig.autoLoginAuth.includes(way)
}

const isOpenAgreement = computed(() => appStore.getLoginConfig.openAgreement == 1)

const isOpenOtherAuth = computed(() => appStore.getLoginConfig.openOtherAuth == 1)
const isForceBindMobile = computed(() => appStore.getLoginConfig.forceBindMobile == 1)

const loginFun = async (scene: LoginTypeEnum) => {
    try {
        if (!isCheckAgreement.value && isOpenAgreement.value) return (showModel.value = true)
        if (scene == LoginTypeEnum.ACCOUNT) {
            if (!formData.username) return uni.$u.toast('请输入账号/手机号码')
            if (!formData.password) return uni.$u.toast('请输入密码')
        }
        if (scene == LoginTypeEnum.MOBILE) {
            if (!formData.mobile) return uni.$u.toast('请输入手机号码')
            if (!formData.code) return uni.$u.toast('请输入验证码')
        }
        uni.showLoading({
            title: '请稍后...'
        })

        let data
        switch (scene) {
            case LoginTypeEnum.ACCOUNT:
                data = await accountLogin(formData)
                break
            case LoginTypeEnum.MOBILE:
                data = await mobileLogin(formData)

                break
        }
        if (data) {
            loginHandle(data)
        }
    } catch (error: any) {
        uni.hideLoading()
        uni.$u.toast(error)
    }
}

const loginHandle = async (data: any) => {
    const { token, isBindMobile } = data
    if (!isBindMobile && isForceBindMobile.value) {
        userStore.temToken = token
        router.navigateTo('/pages/bind_mobile/bind_mobile')
        uni.hideLoading()
        return
    }
    userStore.login(data.token)
    await userStore.getUser()
    uni.$u.toast('登录成功')
    uni.hideLoading()
    // #ifdef H5
    location.replace('/mobile' + (cache.get(BACK_URL) || '/'))
    cache.remove(BACK_URL)
    //#endif
    // #ifndef H5

    const pages = getCurrentPages()

    if (pages.length > 1) {
        const prevPage = pages[pages.length - 1]
        uni.navigateBack({
            delta: pages.length - 1,
            success: () => {
                // @ts-ignore
                const { onLoad, options } = prevPage
                // 刷新上一个页面
                onLoad && onLoad(options)
            }
        })
    } else if (cache.get(BACK_URL)) {
        uni.redirectTo({ url: cache.get(BACK_URL) })
    } else {
        router.reLaunch('/pages/index/index')
    }
    //#endif
}

const { lockFn: handleLogin } = useLockFn(loginFun)

const checkAgreement = async () => {
    if (!isCheckAgreement.value && isOpenAgreement.value)
        return Promise.reject('请勾选已阅读并同意《服务协议》和《隐私协议》')
}
const { lockFn: wxLogin } = useLockFn(async () => {
    try {
        // await checkAgreement();
        if (!isCheckAgreement.value && isOpenAgreement.value) {
            showModel.value = true
            return
        }
        // #ifdef MP-WEIXIN
        uni.showLoading({
            title: '请稍后...'
        })
        const { code }: any = await uni.login({
            provider: 'weixin'
        })
        const share_code = cache.get(SHARE_CODE) || ''
        const data = await mnpLogin({
            code,
            inviteCode: share_code
        })
        cache.remove(SHARE_CODE)
        loginData.value = data
        if (data.isNew) {
            uni.hideLoading()
            userStore.temToken = data.token
            showLoginPopup.value = true
            return
        }
        loginHandle(data)
        // #endif
        // #ifdef H5
        if (isWeixin.value) {
            wechatOa.getUrl()
        }
        // #endif
    } catch (error) {
        uni.$u.toast(error)
    }
})

const handleUpdateUser = async (value: any) => {
    await updateUser(value, { token: userStore.temToken })
    showLoginPopup.value = false
    loginHandle(loginData.value)
}

watch(
    () => appStore.getLoginConfig,
    (value) => {
        if (value.loginWay) {
            loginWay.value = value.loginWay[0]
            //@ts-ignore
            formData.scene = LoginTypeEnum[LoginWayEnum[loginWay.value]]
            console.log(loginWay.value)
            console.log(formData.scene)
        }
    },
    {
        immediate: true
    }
)

onShow(async () => {
    try {
        if (userStore.isLogin) {
            uni.showLoading({
                title: '请稍后...'
            })
            await userStore.getUser()
            uni.hideLoading()
            uni.navigateBack()
        }
    } catch (error: any) {
        uni.hideLoading()
    }
})

onLoad(async (options) => {
    if (userStore.isLogin) {
        // 已经登录 => 首页
        router.reLaunch('/pages/index/index')
        return
    }
    // #ifdef H5
    const { code } = options
    if (code) {
        uni.showLoading({
            title: '请稍后...'
        })

        try {
            const data = await wechatOa.authLogin(code)
            loginHandle(data)
        } catch (error: any) {
            uni.hideLoading()
            throw new Error(error)
        }
    }
    // #endif
})
</script>

<style lang="scss">
page {
    height: 100%;
}
</style>
