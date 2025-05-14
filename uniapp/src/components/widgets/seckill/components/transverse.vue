<template>
    <view class="bg-white px-[20rpx] pb-[20rpx] rounded-b-[14rpx]">
        <scroll-view
            class="whitespace-nowrap"
            :scroll-x="true"
            :enhanced="true"
            :show-scrollbar="false"
            :enable-passive="true"
            :scroll-with-animation="false"
        >
            <view class="flex">
                <view class="w-[208rpx] flex-none mr-[14rpx]" v-for="item in content?.showNum" :key="item"
                    @click="toGoodsDetail(dataList[item - 1])">
					<template v-if="dataList[item - 1]">
                    <u-image
                        :src="dataList[item - 1]?.goodsImage"
                        width="100%"
                        height="208"
                        border-radius="14rpx 14rpx 0 0"
                    ></u-image>
                    <view class="mt-[10rpx] truncate text-main">
                        {{ dataList[item - 1]?.goodsName }}
                    </view>
                    <view class="mt-[10rpx] flex items-center">
                        <text class="px-[7rpx] py-[3rpx] rounded-[4rpx] bg-primary text-white text-[22rpx]">
                            抢
                        </text>
                        <view
                            class="ml-[10rpx]"
                            :class="{ 'opacity-0': content.showPrice == '2' }"
                        >
                            <Price
                                :content="dataList[item - 1]?.seckillPrice"
                                main-size="28rpx"
                                minor-size="22rpx"
                            ></Price>
                        </view>
                    </view>
					</template>
				</view>
            </view>
        </scroll-view>
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
