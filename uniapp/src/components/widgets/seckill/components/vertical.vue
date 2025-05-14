<template>
    <view class="overflow-hidden rounded-b-[14rpx]">
        <view
            class="bg-white flex px-[20rpx] pb-[20rpx]"
            v-for="item in content?.showNum"
            :key="item"
            @click="toGoodsDetail(dataList[item - 1])"
        >
            <u-image
                :src="dataList[item - 1]?.goodsImage"
                width="180"
                height="180"
                border-radius="14rpx 0 0 14rpx"
            ></u-image>
            <view class="px-[20rpx] flex flex-col justify-between flex-1">
                <view class="line-2 text-main">
                    {{ dataList[item - 1]?.goodsName }}
                </view>
                <view class="text-[22rpx] text-info mt-[10rpx]">
                    已抢{{ dataList[item - 1]?.salesVolume }}件
                </view>
                <view class="flex-1 flex items-end justify-between">
                    <view :class="{ 'opacity-0': content.showPrice == '2' }">
                        <Price
                            :content="dataList[item - 1]?.seckillPrice"
                            main-size="38rpx"
                            minor-size="26rpx"
                            font-weight="500"
                        ></Price>
                    </view>
                    <view class="rounded-full px-[30rpx] py-[10rpx] bg-primary text-white">去抢购</view>
                </view>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import Price from '@/components/price/price.vue'

const emit = defineEmits(['toGoodsDetail'])
const props = defineProps({
    content: {
        type: Array as any,
        default: () => []
    },
    dataList: {
        type: Array as any,
        default: () => []
    }
})

const toGoodsDetail = (row: any) => {
    emit('toGoodsDetail', row)
}
</script>

<style scoped lang="scss"></style>
