<template>
    <div class="seckill">
        <div class="flex p-3 items-end">
            <div class="text-xl font-medium">{{ content.title }}</div>
            <div class="ml-1 text-info text-xs">{{ content.subtitle }}</div>
            <div class="ml-auto text-info text-xs">更多 ></div>
        </div>
        <div class="rounded-lg bg-white p-2">
            <vertical :data-list="dataList" :content="content" v-if="content.sortType == '2'"></vertical>
            <transverse :data-list="dataList" :content="content" v-if="content.sortType == '1'">
            </transverse>
            <plate :data-list="dataList" :content="content" v-if="content.sortType == '3'"></plate>
        </div>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type options from './options'
import vertical from './components/vertical.vue'
import transverse from './components/transverse.vue'
import plate from './components/plate.vue'
import { decorateSeckillLists } from '@/api/decoration'

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

const dataList = ref<any[]>([])

const getDataList = async () => {
    dataList.value = await decorateSeckillLists()
}

getDataList()
</script>

<style lang="scss" scoped></style>
