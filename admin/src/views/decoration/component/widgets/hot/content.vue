<template>
    <div class="hot mx-[10px] my-[10px] bg-white rounded-lg overflow-hidden">
        <div
            class="hot-header flex justify-between text-white items-center py-[12px] px-[10px] mb-[8px]"
            :style="{
                backgroundImage:
                    content.bg_type == 1 ? `` : `url(${appStore.getImageUrl(content.bg_url)})`
            }"
        >
            <div>
                <div class="text-lg font-medium">{{ content.title }}</div>
                <div class="mt-[6px]">{{ content.sub_title }}</div>
            </div>
            <div class="more-btn flex items-center rounded-2xl text-xs px-[8px]">
                <span class="mt-[2px]">更多</span>
                <el-icon>
                    <ArrowRight />
                </el-icon>
            </div>
        </div>
        <!-- <div class="hot-header flex justify-between text-white items-center py-[12px] px-[10px] mb-[8px]" :style={} v-else>
            <div>
                <div class="text-lg font-medium">{{ content.title }}</div>
                <div class="mt-[6px]">{{ content.sub_title }}</div>
            </div>
            <div class="more-btn flex items-center rounded-2xl text-xs px-[8px]">
                <span class="mt-[2px]">更多</span>
                <el-icon>
                    <ArrowRight />
                </el-icon>
            </div>
        </div> -->

        <div v-if="content.type == 1">
            <div
                class="goods-list flex mb-[10px] p-[10px]"
                v-for="(item, index) in goodsList"
                :key="index"
            >
                <div class="w-[120px] h-[120px] relative">
                    <img
                        :src="imageArr[index]"
                        v-if="index <= 3"
                        class="w-[24px] h-[30px] absolute left-0 top-0 z-10"
                        alt="排名"
                    />
                    <image-contain
                        :src="item.image"
                        :width="100"
                        :height="100"
                        preview-teleported
                        fit="contain"
                    />
                </div>
                <div class="ml-[10px]">
                    <div class="line-2 w-[210px] h-[42px]">{{ item.name }}</div>
                    <div class="flex justify-between items-end">
                        <div class="mt-[5px]">
                            <div class="buy-num-btn text-xs px-[10px] py-[2px] rounded-2xl">
                                已经有 {{ item.salesNum }}人购买
                            </div>
                            <div class="text-xs mt-[5px]">
                                <span class="text-[#FF2C3C]"
                                    >¥<span class="font-medium text-lg">{{
                                        item.minPrice
                                    }}</span></span
                                >
                                <span class="line-through text-[#999] ml-[4px]"
                                    >¥{{ item.maxPrice }}</span
                                >
                            </div>
                        </div>
                        <div
                            class="buy-btn text-sm text-white px-[15px] py-[5px] rounded-full h-[28px]"
                        >
                            立即抢
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div v-if="content.type == 2" class="flex overflow-x-scroll">
            <div class="goods-list p-[10px]" v-for="(item, index) in goodsList" :key="index">
                <div class="w-[120px] h-[120px] relative">
                    <img
                        :src="imageArr[index]"
                        v-if="index <= 3"
                        class="w-[24px] h-[30px] absolute left-0 top-0 z-10"
                        alt="排名"
                    />
                    <image-contain
                        :src="item.image"
                        :width="100"
                        :height="100"
                        preview-teleported
                        fit="contain"
                    />
                </div>
                <div class="">
                    <div class="line-2 h-[42px]">{{ item.name }}</div>
                    <div class="flex justify-between items-end">
                        <div class="mt-[5px]">
                            <div class="text-xs mt-[5px]">
                                <span class="text-[#FF2C3C]"
                                    >¥<span class="font-medium text-lg">{{
                                        item.minPrice
                                    }}</span></span
                                >
                                <span class="line-through text-[#999] ml-[4px]"
                                    >¥{{ item.maxPrice }}</span
                                >
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import type { PropType } from 'vue'
import type options from './options'
import { decorateGoodsLists } from '@/api/decoration'
import No1 from './images/No.1.png'
import No2 from './images/No.2.png'
import No3 from './images/No.3.png'
import No4 from './images/No.4.png'
import useAppStore from '@/stores/modules/app'

const goodsList = ref<any>([])
const imageArr = [No1, No2, No3, No4]
const appStore = useAppStore()

const getData = async () => {
    const data = await decorateGoodsLists({
        type: 'hot'
    })
    goodsList.value = data
}
getData()

type OptionsType = ReturnType<typeof options>
const props = defineProps({
    content: {
        type: Object as PropType<OptionsType['content']>,
        default: () => ({})
    },
    styles: {
        type: Object as PropType<OptionsType['styles']>,
        default: () => ({})
    }
})
</script>

<style lang="scss" scoped>
.hot {
    &-header {
        background: url(./images/hot_bg.png) no-repeat;
        background-size: 340px 80px;
    }

    .more-btn {
        background-color: rgba($color: #ffffff, $alpha: 0.4);
    }

    .buy-num-btn {
        color: #f79c0c;
        background-color: rgba($color: #f79c0c, $alpha: 0.1);
    }

    .buy-btn {
        background: linear-gradient(90deg, #f95f2f 0%, #ff2c3c 100%);
    }
}
</style>
