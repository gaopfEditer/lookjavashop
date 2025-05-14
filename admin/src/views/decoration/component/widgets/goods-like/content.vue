<template>
    <div class="goods-like h-[258px]">
        <img
            src="./images/goods_like.png"
            class="absolute top-0 left-0 z-10 w-full"
            alt="排名"
        />
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
