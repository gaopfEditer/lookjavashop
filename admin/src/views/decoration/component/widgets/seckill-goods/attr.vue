<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="是否启用">
                <div>
                    <el-radio-group v-model="content.enabled">
                        <el-radio :label="1">自动获取</el-radio>
                    </el-radio-group>
                    <div
                        class="w-[300px] text-primary px-[12px] pt-[8px] pb-[14px] leading-5 rounded-[3px] bg-primary-light-9">
                        根据系统算法，优先显示销量高且排序在前的商品。优先级为：销量>浏览量>创建时间来排序
                    </div>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type options from './options'
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
