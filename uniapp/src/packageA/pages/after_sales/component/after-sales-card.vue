<template>

    <view 
        @click="toAfterSalesDetail"
        class="rounded-[10rpx] overflow-hidden bg-white mt-[20rpx] mx-[20rpx]"
    >
        <!-- 头部 -->
        <view class="flex justify-between p-[20rpx] tetx-sm border-0 border-b border-solid border-[#e5e5e5]" v-if="!isApply">
            <view class="text-muted">申请时间: {{ data?.afterCreateTime }}</view>
            <view class="text-primary"> {{ data.refundTypeMsg }}</view>
        </view>

        <!-- 商品 -->
        <goods-card
            shape="rectangle"
            class="flex-1"
            :image="data?.goodsImage"
            :imageStyle="{ width: '180rpx', height: '180rpx' }"
            :name="data?.goodsName"
            :containStyle="{ height: '220rpx', 'border-radius': '0' }"
        >
            <view class="flex justify-between mt-[8rpx]">
                <price
                    :content="data.goodsPrice"
                    main-size="32rpx"
                    minor-size="28rpx"
                    fontWeight="500"
                    color="#101010"
                />
                <text class="text-main">x {{ data.goodsNum }}</text>
            </view>
        </goods-card>

        <!-- 申请信息 -->
        <view
            class="px-[30rpx] py-[20rpx] mx-[20rpx] mb-[20rpx] rounded-[10rpx] bg-page text-base text-main"
            v-if="!isApply"
        >
            <text class="font-medium">{{ data.afterStatusMsg }}</text>
            <text class="ml-[30rpx]">{{ data.subStatusMsg }}</text>
        </view>

        <!-- 底部 -->
        <view class="after-sales-footer" @click.stop>
            <slot></slot>
        </view>
    </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()
const props = defineProps({
    data: {
        type: Array as PropType<any>,
        default: () => []
    },
    isApply: {
        type: Boolean,
        default: false
    }
})

const toAfterSalesDetail = () => {
    if (props.isApply) return
    router.navigateTo(`/packageA/pages/after_sales_detail/after_sales_detail?id=${props.data.afterId}`)
}
</script>

<style lang="scss" scoped>
.-btn {
    height: 58rpx;
    line-height: 58rpx;
    border-radius: 60px;
    color: $u-type-primary;
    border: 1px solid $u-type-primary;
}
</style>