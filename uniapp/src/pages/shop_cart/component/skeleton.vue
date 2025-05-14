<template>
    <view class="shopping-skeleton">
        <view class="py-[40px] flex flex-col items-center">
            <view class="base-bg w-[300rpx] h-[300rpx] rounded-[50%]"></view>
            <view class="base-bg w-[250rpx] h-[30rpx] mt-[30rpx]"></view>
            <view class="base-bg w-[200rpx] h-[60rpx] mt-[30rpx] rounded-[60px]"></view>
        </view>

        <view class="base-bg w-[180rpx] h-[50rpx] ml-[24rpx]"></view>

        <view class="goods-skt mt-[40rpx] pl-[24rpx] flex flex-wrap">
            <view v-for="item in 4" :key="item" class="w-[336rpx] mr-[24rpx] mb-[24rpx]">
                <view class="h-[336rpx] base-bg base-br"></view>
                <view class="w-[336rpx] bg-white py-[30rpx] px-[12rpx]">
                    <view class="w-full h-[50rpx] base-bg base-br"></view>
                    <view class="flex items-center mt-[24rpx]">
                        <view class="w-[100rpx] h-[40rpx] base-bg"></view>
                        <view class="w-[70rpx] h-[30rpx] base-bg ml-[24rpx]"></view>
                    </view>
                </view>
            </view>
        </view>

        <view class="footer-skt flex">
            <view class="w-[25%] pt-[20rpx] flex flex-col items-center" v-for="item in 4" :key="item">
                <view class="w-[44rpx] h-[44rpx] base-bg base-br"></view>
                <view class="w-[44rpx] h-[20rpx] mt-[10rpx] base-bg base-b"></view>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {}
    }
}
</script>

<style lang="scss" scoped>
@keyframes twinkle {
    0% {
        opacity: 1;
    }

    50% {
        opacity: 0.5;
    }

    100% {
        opacity: 1;
    }
}

.base-bg {
    background: #ededed;
    animation: twinkle 2s ease infinite forwards;
}

.base-margin {
    margin: 0 20rpx 20rpx;
}

.base-br {
    border-radius: 8rpx;
}

.shopping-skeleton {
    position: fixed;
    top: 0;
    left: 0;
    z-index: 99999;
    opacity: 1;
    width: 100vw;
    height: 100vh;
    background: $u-bg-color;
    box-sizing: border-box;
    transition: all 0.2s ease;

    .footer-skt {
        position: fixed;
        bottom: 0;
        width: 100vw;
        height: 100rpx;
        background: #fff;
        padding-bottom: calc(env(safe-area-inset-bottom));
        box-sizing: content-box;
    }
}
</style>
