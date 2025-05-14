<template>
    <div class="new-list mx-[10px] my-[10px]">
        <div class="flex items-center mx-[10px] my-[15px] text-[17px] font-medium">新品推荐</div>

        <div class="new-box flex flex-wrap justify-between mb-[10px]" v-if="content.type == 1">
            <div class="new-item rounded-lg overflow-hidden bg-white w-[164px]" v-for="(item, index) in goodsList"
                :key="index">
                <image-contain :src="item.image" :width="164" :height="164" preview-teleported fit="contain" />
                <div class="ml-[10px]">
                    <div class="line-2 h-[40px] pt-[5px] leading-[18px]">{{ item.name }}</div>
                    <div class="mt-[5px] mb-[10px] text-xs">
                        <span class="text-[#FF2C3C]">¥<span class="font-medium text-lg">{{ item.minPrice }}</span></span>
                        <span class="line-through text-[#999] ml-[4px]">¥{{ item.maxPrice }}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="flex overflow-x-scroll" v-else>
            <div class="new-item rounded-lg bg-white w-[164px]" v-for="(item, index) in goodsList" :key="index">
                <image-contain :src="item.image" :width="130" :height="130" preview-teleported fit="contain" />
                <div class="ml-[10px]">
                    <div class="line-2 h-[40px] pt-[5px] leading-[18px]">{{ item.name }}</div>
                    <div class="mt-[5px] mb-[10px] text-xs">
                        <span class="text-[#FF2C3C]">¥<span class="font-medium text-lg">{{ item.minPrice }}</span></span>
                        <span class="line-through text-[#999] ml-[4px]">¥{{ item.maxPrice }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { decorateGoodsLists } from '@/api/decoration'
import type options from './options'
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

const goodsList = ref<any>([])

const getData = async () => {
    const data = await decorateGoodsLists({
        type: 'new'
    })
    goodsList.value = data
}
getData()
</script>

<style lang="scss" scoped>
.new-box {
    .new-item {
        margin-right: 10px;
        margin-bottom: 10px;
    }

    .new-item:nth-child(2n) {
        margin-right: 0;
    }
}
</style>
