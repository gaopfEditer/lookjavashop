<template>
    <div class="my-service">
        <div v-if="content.title" class="title px-[15px] py-[10px]">
            <div>{{ content.title }}</div>
        </div>
        <div v-if="content.style == 1" class="flex flex-wrap pt-[20px] pb-[10px]">
            <div v-for="(item, index) in content.data" :key="index" class="flex flex-col items-center w-1/5 mb-[15px]">
                <decoration-img width="26px" height="26px" :src="item.image" alt="" />
                <div class="mt-[7px]">{{ item.name }}</div>
            </div>
        </div>
        <div v-if="content.style == 2">
            <div v-for="(item, index) in content.data" :key="index"
                class="flex items-center border-b border-[#e5e5e5] h-[50px] px-[12px]">
                <decoration-img width="24px" height="24px" :src="item.image" alt="" />
                <div class="ml-[10px] flex-1">{{ item.name }}</div>
                <div>
                    <icon name="el-icon-ArrowRight" />
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type options from './options'
import DecorationImg from '../../decoration-img.vue'
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

<style lang="scss" scoped>
.my-service {
    margin: 10px 10px 0;
    background-color: #fff;
    border-radius: 7px;

    .title {
        border-bottom: 1px solid #e5e5e5;
        font-size: 16px;
        font-weight: 500;
    }
}
</style>
