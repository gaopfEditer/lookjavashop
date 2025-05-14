<template>
    <view class="cancel" :style="$theme.pageStyle">
        <view class="text-[32rpx] text-[#000]">
            为保证你的账号安全，在你提交的注销申请生效前，需同时满足以下条件：
        </view>
        <view class="my-8">
            <view class="text-[32rpx] text-[#000] mb-2"> 1.账号处于安全状态</view>
            <view class="muted" style="font-size: 28rpx"> 账号当前为有效账号，非冻结状态。</view>
        </view>
        <view>
            <view class="text-[32rpx] text-[#000] mb-2"> 2、账号内财产已结清，交易已完成</view>
            <view class="muted" style="font-size: 28rpx">
                账号下所有关联业务的资产及预期收益（包括现金、余额、佣金、积分、优惠券）均已结清、退款、清空或自愿放弃，所有交易已完成或自愿放弃。</view
            >
        </view>
        <view
            class="w-full fixed bottom-[env(safe-area-inset-bottom)] -ml-5 px-4"
            @click="showPopup = true"
        >
            <button class="btn bg-primary text-white flex justify-center leading-10">
                申请注销
            </button>
        </view>

        <u-popup
            v-model="showPopup"
            mode="bottom"
            border-radius="14"
            :closeable="false"
            :safe-area-inset-bottom="true"
            mask-close-able
            height="400px"
        >
            <view class="popup_header">
                <text>提示</text>
                <u-icon name="close" color="#666666" @tap="showPopup = false" />
            </view>
            <view class="popup_body">
                <view
                    >注销后，
                    <text class="tip"> 你的账号信息将永久清空无法恢复 </text>
                </view>
                <view class="popup_body_container">
                    <view>·你的个人相关数据将会被清空且无法恢复</view>
                    <view class="mt-2.5">·你账号内剩余的余额、资产也将无法恢复</view>
                </view>
                <view class="mt-7 flex items-center">
                    <u-checkbox v-model="isAgree" shape="circle">
                        <view class="text-sm flex" @click.stop="isAgree = !isAgree">
                            我已阅读并同意

                            <router-navigate
                                class="text-primary"
                                hover-class="none"
                                to="/packageA/pages/agreement/agreement?type=close"
                            >
                                《用户注销协议》
                            </router-navigate>
                        </view>
                    </u-checkbox>
                </view>
                <view class="flex justify-between mt-7 btn">
                    <button class="btn_cancel" @click="showPopup = false">取消</button>
                    <button
                        class="btn_comfirm"
                        :class="{ '!bg-primary': isAgree }"
                        @click="handleCancel"
                    >
                        <text> 确认注销 </text>
                    </button>
                </view>
            </view>
        </u-popup>
    </view>
</template>
<script lang="ts" setup>
import { useRouter } from 'uniapp-router-next'
import { ref } from 'vue'

const router = useRouter()
const showPopup = ref(false)
const isAgree = ref(false)

const handleCancel = async () => {
    if (!isAgree.value) return uni.$u.toast('请先阅读并同意《用户注销协议》')
    router.navigateTo({
        path: '/packageA/pages/cancel_result/cancel_result',
        query: {
            pass: 'success'
        }
    })
}
</script>
<style lang="scss" scoped>
a {
    text-decoration: none;
}
.cancel {
    background-color: #fff;
    height: 100vh;
    padding: 42rpx;
    font-size: 32rpx;
    padding-bottom: 0;
    .btn {
        // #ifndef MP-WEIXIN
        bottom: 60rpx;
        // #endif
    }
    .popup_header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 100rpx;
        padding: 0 30rpx;
        font-weight: 500;
        font-size: 30rpx;
        background-color: #ffffff;
    }
    .popup_body {
        padding: 42rpx;
        font-size: 30rpx;
        .tip {
            font-weight: 500;
        }
        .popup_body_container {
            margin-top: 30rpx;
            background-color: #f7f7f7;
            padding: 42rpx;
        }
        .btn_comfirm {
            border-radius: 50rpx;
            width: 300rpx;
            height: 80rpx;
            line-height: 80rpx;
            color: white;
            background-color: #bfbfbf;
        }
        .btn_cancel {
            border-radius: 50rpx;
            width: 300rpx;
            height: 80rpx;
            line-height: 80rpx;
            border: 1px solid rgba(230, 230, 230, 1);
        }
    }
}
</style>
