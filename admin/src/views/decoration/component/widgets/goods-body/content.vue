<template>
    <div class="goods-detail">
        <div class="m-[10px] bg-white rounded-lg p-[10px]">
            <div>
                <span class="text-[#999999]">选择</span> <span class="ml-[15px]">36 1件</span>
            </div>
            <div class="mt-[20px] flex">
                <span class="text-[#999999]">物流</span>
                <div class="ml-[15px]">
                    免费配送<br /><span class="text-[#999999]">配送至：北京 北京市 朝阳区</span>
                </div>
            </div>
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
