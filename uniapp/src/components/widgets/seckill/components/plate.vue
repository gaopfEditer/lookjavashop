<template>
    <view class="px-[12rpx] pb-[12rpx] bg-white">
        <view
            class="grid grid-rows-2 grid-cols-2 grid-flow-col gap-1 h-[350rpx]"
            v-for="item in 1"
            :key="item"
        >
            <view
                class="rounded-lg row-span-2 flex flex-col card p-[12rpx]"
                @click="toGoodsDetail(dataList[1 + item * 3 - 4])"
            >
                <template v-if="dataList[1 + item * 3 - 4]">
                    <view class="truncate font-medium">{{
                        dataList[1 + item * 3 - 4]?.goodsName
                    }}</view>

                    <Price
                        v-if="content.showPrice != '2'"
                        class="text-price"
                        color="inherit"
                        :content="dataList[1 + item * 3 - 4]?.seckillPrice"
                    ></Price>

                    <text class="bg-btn text-white rounded-full py-[4rpx] px-[18rpx] self-start"
                        >去抢购</text
                    >
                    <view class="flex-1 flex items-end justify-end">
                        <u-image
                            :src="dataList[1 + item * 3 - 4]?.goodsImage"
                            width="190"
                            height="190"
                            border-radius="10"
                        ></u-image>
                    </view>
                </template>
            </view>
            <view
                class="rounded-lg card p-[12rpx]"
                @click="toGoodsDetail(dataList[2 + item * 3 - 4])"
            >
                <template v-if="dataList[2 + item * 3 - 4]">
                    <view class="truncate font-medium">{{
                        dataList[2 + item * 3 - 4]?.goodsName
                    }}</view>
                    <view class="flex">
                        <view class="flex flex-col">
                            <Price
                                v-if="content.showPrice != '2'"
                                class="text-price"
                                color="inherit"
                                :content="dataList[2 + item * 3 - 4]?.seckillPrice"
                            ></Price>
                            <text
                                class="bg-btn text-white rounded-full py-[4rpx] px-[18rpx] self-start"
                                >去抢购</text
                            >
                        </view>
                        <view class="flex-1 flex items-end justify-end mt-[10rpx]">
                            <u-image
                                :src="dataList[2 + item * 3 - 4]?.goodsImage"
                                width="100"
                                height="100"
                                border-radius="10"
                            ></u-image>
                        </view>
                    </view>
                </template>
            </view>
            <view
                class="rounded-lg card p-[12rpx]"
                @click="toGoodsDetail(dataList[3 + item * 3 - 4])"
            >
                <template v-if="dataList[3 + item * 3 - 4]">
                    <view class="truncate font-medium">{{
                        dataList[3 + item * 3 - 4]?.goodsName
                    }}</view>
                    <view class="flex">
                        <view class="flex flex-col">
                            <Price
                                v-if="content.showPrice != '2'"
                                class="text-price"
                                color="inherit"
                                :content="dataList[3 + item * 3 - 4]?.seckillPrice"
                            ></Price>
                            <text
                                class="bg-btn text-white rounded-full py-[4rpx] px-[18rpx] self-start"
                                >去抢购</text
                            >
                        </view>
                        <view class="flex-1 flex items-end justify-end mt-[10rpx]">
                            <u-image
                                :src="dataList[3 + item * 3 - 4]?.goodsImage"
                                width="100"
                                height="100"
                                border-radius="10"
                            ></u-image>
                        </view>
                    </view>
                </template>
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

<style scoped lang="scss">
.card {
    &:nth-child(1) {
        background-size: contain;
        background: url('@/components/widgets/seckill/image/bg-red.png') no-repeat center;
        .text-price {
            color: #FF2C3C;
        }
        .bg-btn {
            background-color: #FF2C3C;
        }
    }
    &:nth-child(2) {
        background-size: contain;
        background: url('@/components/widgets/seckill/image/bg-green.png') no-repeat center;
        .text-price {
            color: #2EC840;
        }
        .bg-btn {
            background-color: #2EC840;
        }
    }
    &:nth-child(3) {
        background-size: contain;
        background: url('@/components/widgets/seckill/image/bg-yellow.png') no-repeat center;
        .text-price {
            color: #FF6636;
        }
        .bg-btn {
            background-color: #FF6636;
        }
    }
}
</style>
