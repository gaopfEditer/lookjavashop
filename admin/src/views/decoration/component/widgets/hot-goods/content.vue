<template>
    <div class="hot-goods mx-[10px] my-[10px]">

        <div
            class="goods-list flex mb-[10px] p-[10px] bg-white rounded-lg overflow-hidden"
            v-for="(item, index) in goodsList"
        >
            <image-contain
                :src="item.image"
                :width="90"
                :height="90"
                :preview-teleported="true"
                fit="contain"
            />
            <div class="ml-[10px]">
                <div class="line-2 w-[210px] h-[40px]">{{ item.name }}</div>
                <div class="mt-[5px]">
                    <div class="hot-sales-num pr-2 rounded-full inline-block text-xs">
                        <img
                            src="./images/icon_hot.png"
                            class="w-[20px] h-[20px] inline-block"
                            alt="热度"
                        >
                        <span class="text-[#FF2C3C] ml-2">
                            销量 {{ item.salesNum }}
                        </span>
                    </div>
                    <div class="text-xs mt-[5px]">
                        <span class="text-[#FF2C3C]">
                            ¥<span class="font-medium text-lg">
                                {{ item.minPrice }}
                            </span>
                        </span>
                        <span class="line-through text-[#999] ml-[4px]">
                            ¥{{ item.maxPrice }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { decorateGoodsLists } from '@/api/decoration'

const goodsList = ref([])

const getData = async () => {
    const data = await decorateGoodsLists({ type: 'hot' })
    goodsList.value = data
}
getData()
</script>

<style lang="scss" scoped>
.hot-goods {
    .hot-sales-num {

        background-color: rgba($color: #ed5349, $alpha: 0.1);
    }
}
</style>