<template>
    <div class="goods-detail">
        <div class="flex justify-between bg-white mx-[10px] p-[10px]">
            <div>用户评价</div>
            <div class="text-[red]">查看全部</div>
        </div>

        <div class="text-[#999999] text-center leading-[100px] bg-white">暂无任何评论</div>
    </div>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
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
</script>
