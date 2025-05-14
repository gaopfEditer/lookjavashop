<template>
    <div class="user-info flex items-center px-[25px]" :style="{
        backgroundImage:
            content.background == 1 ? `` : `url(${appStore.getImageUrl(content.backgroundurl)})`
    }">
        <img src="./images/default_avatar.png" class="w-[60px] h-[60px]" alt="" />
        <div class="text-white text-[18px] ml-[10px]">未登录</div>
    </div>
</template>
<script lang="ts" setup>
import useAppStore from '@/stores/modules/app'
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
const appStore = useAppStore()
</script>

<style lang="scss" scoped>
.user-info {
    background: url(./images/my_topbg.png);
    height: 115px;
    background-position: bottom;
    background-size: 100% auto;
}
</style>
