<template>
    <div class="notice mx-[10px] my-[10px] bg-white px-[14px] py-[8px] rounded-lg">
        <div class="flex">
            <img class="flex-none" style="height: 19px" src="./images/icon_notice.png" v-if="content.icon == 1" />
            <img class="flex-none" style="height: 19px" :src="getImageUrl(content.iconUrl)" v-else />

            <div class="flex">
                <div class="mx-[6px] text-[#f1f1f1]">|</div>
                <div class="truncate ml-[4px]">公告标题</div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type options from './options'
import useAppStore from '@/stores/modules/app'
const { getImageUrl } = useAppStore()
type OptionsType = ReturnType<typeof options>
defineProps({
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
