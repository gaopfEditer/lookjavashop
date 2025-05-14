<template>
    <view class="container" :style="$theme.pageStyle">
        <view class="success" v-if="passType === 0">
            <image
                src="/static/images/icon/icon_success.png"
                class="img"
                style="margin-top: 150rpx"
            ></image>
            <view class="my-4">注销完成</view>
            <view>你的所有信息都已清空</view>
            <view class="px-4 w-full mt-10" @click="userStore.logout()">
                <navigator
                    url="/pages/index/index"
                    open-type="switchTab"
                    hover-class="none"
                    class="bg-primary text-white flex justify-center leading-10 rounded-full"
                >
                    确定
                </navigator>
            </view>
        </view>
        <view style="padding: 0 42rpx" v-if="passType !== 0">
            <view class="fail">
                <image
                    src="/static/images/icon/icon_fail.png"
                    class="img mb-5"
                    style="margin-top: 50rpx"
                ></image>
                <view class="mb-5 tip">抱歉，无法注销</view>
                <view>很遗憾，由于以下原因，导致账号无法注销</view>
            </view>
            <view style="margin-top: 50rpx">
                <template v-for="item in data" :key="item.code">
                    <view class="fail-body">{{ item.title }}</view>
                    <view class="muted"
                        >{{ item.content }}</view
                    >
                </template>
            </view>

            <view class="btn w-full mt-10 fixed bottom-[env(safe-area-inset-bottom)] -ml-5 px-4">
                <navigator
                    url="/pages/user/user"
                    open-type="switchTab"
                    hover-class="none"
                    class="bg-primary text-white flex justify-center leading-10 rounded-full"
                >
                    返回个人中心
                </navigator>
            </view>
        </view>
    </view>
</template>
<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import { apiUserCancellation } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const data = ref({})
const passType = ref(-1)
onMounted(async () => {
    const res = await apiUserCancellation()
    data.value = res
	passType.value = res[0].code
})
</script>
<style lang="scss" scoped>
.container {
    height: 100vh;
    background-color: white;
    padding-bottom: 0;
    .img {
        width: 120rpx;
        height: 120rpx;
    }
    .success {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        font-size: 36rpx;
        .btn {
            height: 80rpx;
            line-height: 80rpx;
            color: white;
            font-size: 28rpx;
            position: fixed;
            bottom: env(safe-area-inset-bottom);
            bottom: constant(safe-area-inset-bottom);
            // #ifdef APP
            bottom: 30rpx;
            // #endif
            left: 42rpx;
            right: 42rpx;
        }
    }

    .fail {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        .tip {
            font-size: 36rpx;
        }
    }
    .fail-body {
        font-size: 32rpx;
        margin-top: 50rpx;
    }

    .btn {
        // #ifndef MP-WEIXIN
        bottom: 60rpx;
        // #endif
    }
}
</style>
