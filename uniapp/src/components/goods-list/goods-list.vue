<template>
    <!-- 2栏卡片--瀑布流 -->

    <u-waterfall :model-value="list" v-if="type === 'column-card'">
        <template #left="{ leftList }">
            <view
                class="flex justify-center mb-[20rpx] ml-[15rpx]"
                v-for="(item, index) in leftList"
                :key="index"
                @click="goPage(`/pages/goods_detail/goods_detail?id=${item.id}`)"
            >
                <goods-card
                    :name="item.name"
                    :image="item.image"
                    :price="item.price"
                    :imageStyle="{ width: '336rpx', height: '336rpx' }"
                    :containStyle="{ width: '336rpx', height: 'auto' }"
                    :contentStyle="{ height: 'auto', flex: '0' }"
                    :minPrice="item.lineationPrice"
                >
                    <template #name>
                        <view class="text-lg text-main line-2">
                            {{ item.name }}
                        </view>
                    </template>
                </goods-card>
            </view>
        </template>
        <template #right="{ rightList }">
            <view
                class="flex justify-center mb-[20rpx] mr-[15rpx]"
                v-for="(item, index) in rightList"
                :key="index"
                @click="goPage(`/pages/goods_detail/goods_detail?id=${item.id}`)"
            >
                <goods-card
                    :name="item.name"
                    :image="item.image"
                    :price="item.price"
                    :imageStyle="{ width: '336rpx', height: '336rpx' }"
                    :containStyle="{ width: '336rpx', height: 'auto' }"
                    :contentStyle="{ height: 'auto', flex: '0' }"
                    :minPrice="item.lineationPrice"
                >
                    <template #name>
                        <view class="text-lg text-main line-2">
                            {{ item.name }}
                        </view>
                    </template>
                </goods-card>
            </view>
        </template>
    </u-waterfall>
</template>

<script lang="ts" setup>
import { useRouter } from 'uniapp-router-next'
import type { PropType } from 'vue'

const props = defineProps({
    type: {
        type: String,
        default: 'column-card'
    },
    list: {
        type: Array as PropType<any[]>,
        default: []
    }
})
const router = useRouter()

const goPage = (url: string) => {
    router.navigateTo(url)
}
</script>

<style lang="scss" scoped>
.column-2-box {
    .column-2-item {
        margin-right: 20rpx;
        margin-bottom: 20rpx;
    }

    .column-2-item:nth-child(2n) {
        margin-right: 0;
    }
}

.hot-sales-num {
    background-color: rgba($color: #ed5349, $alpha: 0.1);
}
</style>
