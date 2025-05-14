<template>
    <div class="goods-detail">
        <div class="flex justify-around bg-white text-[#999999]">
            <span>库存：313</span>
            <span>销量：0</span>
            <span>浏览量：23</span>
        </div>
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
