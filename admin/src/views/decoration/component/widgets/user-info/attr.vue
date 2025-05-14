<template>
    <div>
        <el-form-item label="背景设置">
            <div>
                <el-radio-group v-model="content.background">
                    <el-radio :label="1">跟随系统风格</el-radio>
                    <el-radio :label="2">背景图片</el-radio>
                </el-radio-group>
                <div>
                    <material-picker v-model="content.backgroundurl" exclude-domain
                        v-if="content.background == 2"></material-picker>
                    <div class="form-tips">建议图片尺寸：760 x 500；图片格式：jpg、png、jpeg</div>
                </div>
            </div>
        </el-form-item>
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

<style lang="scss" scoped></style>
