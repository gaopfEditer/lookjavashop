<template>
    <view class="address-container">
        <view class="flex items-center address-box">
            <image class="h-[44rpx] w-[44rpx]" src="./images/icon_address.png" />
            <view class="flex-1 ml-[20rpx] mr-[50rpx]">
                <slot></slot>
            </view>
            <slot name="right">
                <u-icon name="arrow-right" color="#707070"></u-icon>
            </slot>
        </view>
    </view>
</template>

<style lang="scss" scoped>
.address-container {
    padding: 0 20rpx;

    .address-box {
        z-index: 1;
        position: relative;
        height: 200rpx;
        padding: 24rpx;
        border-radius: 0 0 14rpx 14rpx;
        background-color: #ffffff;
    }

    // .address-box::before {
    //     content: '';
    //     position: absolute;
    //     height: inherit;
    //     z-index: -1;
    //     top: -8rpx;
    //     left: 0;
    //     right: 0;
    //     border-radius: 14rpx;
    //     background-color: $u-type-primary;
    // }
    .address-box::after {
        content: '';
        position: absolute;
        height: inherit;
        z-index: -1;
        top: 0;
        left: 0;
        right: 0;
        border-radius: 14rpx;
        background-color: #ffffff;
    }
}
</style>
