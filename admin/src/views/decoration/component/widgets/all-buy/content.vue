<template>
    <div class="goods-like p-[10px]">
        <div class="text-center text-[#999999]">大家都在买</div>
        <div class="flex mt-[10px]">
            <img src="./images/good1.png" class="w-1/2 h-[150px]mr-[10px]" />
            <img src="./images/good2.png" class="w-1/2 h-[150px]" />
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
