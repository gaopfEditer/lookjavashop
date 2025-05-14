<template>
    <view v-for="(sitem, sindex) in data" :key="sindex" class="rounded-[10rpx] overflow-hidden bg-white mt-[20rpx]"
        :style="$theme.pageStyle">
        <goods-card shape="rectangle" class="flex-1" :image="sitem?.goodsImage"
            :imageStyle="{ width: '180rpx', height: '180rpx' }" :name="sitem?.goodsName"
            :containStyle="{ height: '220rpx', 'border-radius': '0' }">
            <view class="text-muted mt-[30rpx]">{{ sitem?.goodsSkuValue || '默认' }}</view>
        </goods-card>
        <view class="pr-[20rpx] pb-[22rpx] flex justify-end">
            <navigator hover-class="none" :url="`/packageA/pages/comment_upon/comment_upon?id=${sitem.id}`">
                <button class="text-base comment-btn bg-primary">立即评价</button>
            </navigator>
        </view>
    </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
defineProps({
    data: {
        type: Array as PropType<any>,
        default: () => []
    }
})
</script>

<style lang="scss" scoped>
.comment-btn {
    height: 58rpx;
    line-height: 58rpx;
    border-radius: 60px;
    // color: $u-type-primary;
    color: white;
    border: 1px solid $u-type-primary;
}
</style>
