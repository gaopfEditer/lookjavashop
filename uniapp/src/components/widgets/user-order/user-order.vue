<template>
    <view class="user-order bg-white mx-[20rpx] mt-[20rpx] rounded-lg">
        <view v-if="content.title" class="title px-[30rpx] py-[20rpx] text-xl border-light border-solid border-0 border-b">
            <view class="flex items-center justify-between">
                <view class="text-[#222] font-medium">{{ content.title }}</view>
                <view class="flex items-center text-xs text-[#888]" @click="toOrderLists">
                    <text class="mr-[10rpx]">查看全部</text>
                    <u-icon name="arrow-right" size="22rpx" color="#888" />
                </view>
            </view>
        </view>
        <view class="flex pt-[20px] pb-[10px]">
            <view v-for="(item, index) in content.data" :key="index" :class="{ 'flex-1': item.show }"
                @click="handleClick(index)">
                <view class="order-menu-item flex flex-col items-center mb-[15px]" v-if="item.show == 1">
                    <!-- 待付款 -->
                    <text class="badge" v-if="userInfo?.waitPayCount && index == 0">
                        {{ userInfo?.waitPayCount }}
                    </text>
                    <!-- 待发货 -->
                    <text class="badge" v-if="userInfo?.waitDeliveryCount && index == 1">
                        {{ userInfo?.waitDeliveryCount }}
                    </text>
                    <!-- 待收货 -->
                    <text class="badge" v-if="userInfo?.waitConfirmCount && index == 2">
                        {{ userInfo?.waitConfirmCount }}
                    </text>
                    <!--商品评论 -->
                    <text class="badge" v-if="userInfo?.waitCommentCount && index == 3">
                        {{ userInfo?.waitCommentCount }}
                    </text>
                    <!-- 售后退款 -->
                    <text class="badge" v-if="userInfo?.waitAfterSaleCount && index == 4">
                        {{ userInfo?.waitAfterSaleCount }}
                    </text>
                    <u-image width="52" height="52" :src="getImageUrl(item.image)" alt="" />
                    <view class="mt-[7rpx]">{{ item.name }}</view>
                </view>
            </view>
        </view>
    </view>
</template>
<script lang="ts" setup>
import { useAppStore } from '@/stores/app'
import { navigateTo } from '@/utils/util'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const props = defineProps({
    content: {
        type: Object,
        default: () => ({})
    },
    styles: {
        type: Object,
        default: () => ({})
    },
    userInfo: {
        type: Object,
        default: () => ({})
    }
})
console.log(props.userInfo)
const { getImageUrl } = useAppStore()
const handleClick = (index: any) => {
    switch (index) {
        case 0:
            router.navigateTo('/packageA/pages/order_list/order_list?type=0')
            break
        case 1:
            router.navigateTo('/packageA/pages/order_list/order_list?type=1')
            break
        case 2:
            router.navigateTo('/packageA/pages/order_list/order_list?type=2')
            break
        case 3:
            router.navigateTo('/packageA/pages/comment_list/comment_list')
            break
        case 4:
            router.navigateTo('/packageA/pages/after_sales/after_sales')
            break
    }
}

// 点击查看跳转至订单详情页
const toOrderLists = () => {
    router.navigateTo('/packageA/pages/order_list/order_list')
}

</script>

<style lang="scss">
.order-menu-item {
    flex: auto;
    text-align: center;
    position: relative;

    image {
        width: 60rpx;
        height: 60rpx;
    }

    &--text {
        color: #f36161;
        font-size: 30rpx;
    }
}

// 徽章
.badge {
    width: 34rpx;
    height: 34rpx;
    text-align: center;
    line-height: 34rpx;
    top: -10rpx;
    right: 25rpx;
    z-index: 10;
    color: #ffffff !important;
    border-radius: 50%;
    position: absolute;
    font-size: 24rpx !important;
    background-color: #f36161;
}
</style>
