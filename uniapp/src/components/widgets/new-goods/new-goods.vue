<template>
    <view
        class="flex items-center mx-[30rpx] py-[26rpx] mt-[20rpx] text-2xl font-medium text-[#101010]"
    >
        <text class="mr-2">{{ icontent.title }}</text>
        <text class="text-[#999] text-sm">{{ icontent.sub_title }}</text>
    </view>
    <!-- 瀑布模式 -->
    <template v-if="icontent.type == 1">
        <!-- #ifdef H5 -->
        <view class="">
            <goods-list :list="goodslists" />
        </view>
        <!-- #endif -->
        <!-- #ifndef H5 -->

        <goods-list :list="goodslists" />
        <!-- #endif -->
    </template>
    <!-- 横向模式 -->
    <template v-if="icontent.type == 2">
        <view class="mx-[30rpx]">
            <scroll-view
                class="nowrap"
                :scroll-x="true"
                :enhanced="true"
                :show-scrollbar="false"
                :enable-passive="true"
                :scroll-with-animation="false"
            >
                <view
                    class="w-[208rpx] mr-[14rpx] inline-block"
                    v-for="(item, index) in goodslists"
                    :key="item"
                >
                    <router-navigate
                        :to="`/pages/goods_detail/goods_detail?id=${item.id}`"
                        hover-class="none"
                    >
                        <template v-if="index < icontent.num">
                            <image :src="item?.image" class="w-[208rpx] h-[208rpx]"></image>
                            <view class="mt-[10rpx] truncate">{{ item?.name }}</view>
                            <view>
                                <price
                                    :content="item.price"
                                    fontWeight="500"
                                    mainSize="38rpx"
                                    minorSize="24rpx"
                                    class="mr-2"
                                />
                                <price
                                    :content="item.lineationPrice"
                                    v-if="item.lineationPrice != '0.00'"
                                    mainSize="24rpx"
                                    minorSize="24rpx"
                                    color="#999999"
                                    lineThrough
                                />
                            </view>
                        </template>
                    </router-navigate>
                </view>
            </scroll-view>
        </view>
    </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps({
    content: {
        type: Object,
        default: () => ({})
    },
    icontent: {
        type: Object,
        default: () => ({})
    }
})
const goodslists = computed(() => {
    return props.content.filter((i: any, index: number) => {
        return index < props.icontent.num
    })
})
</script>
<style lang="scss" scoped>
.nowrap {
    white-space: nowrap;
    width: 100%;
}
.safe-bottom {
    margin-bottom: 120rpx;
    width: 100%;
    display: block;
}
</style>
