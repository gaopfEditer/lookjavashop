<template>
    <div class="goods-detail">
        <img src="./images/goods.png" class="" />
        <div class="mx-[10px] mt-[10px] bg-white rounded-lg p-[10px]">
            <span class="text-[red] text-[18px]">￥31起</span>
            <span class="text-[#999999] line-through text-[12px]">￥69.9起</span>
            <div class="mt-[10px]">vivo新款只能手机大屏幕5g手机大电池手机</div>
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
