<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="是否启用">
                <el-radio-group v-model="content.enabled">
                    <el-radio :label="1">开启</el-radio>
                    <el-radio :label="0">停用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="资讯图标">
                <div>
                    <el-radio-group v-model="content.icon">
                        <el-radio :label="1">系统默认</el-radio>
                        <el-radio :label="0">自定义</el-radio>
                    </el-radio-group>
                    <template v-if="content.icon == 0">
                        <material-picker v-model="content.iconUrl" exclude-domain />
                        <div class="form-tips">
                            建议图片尺寸：60 x 20；图片格式：Gif、jpg、png、jpeg
                        </div>
                    </template>
                </div>
            </el-form-item>
            <el-form-item label="最新标签">
                <el-radio-group v-model="content.label">
                    <el-radio :label="1">显示</el-radio>
                    <el-radio :label="0">隐藏</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="显示数量">
                <div>
                    <div class="pb-[30px] flex">
                        <el-slider class="mr-[10px]" style="width: 200px" v-model="content.num" :min="1" :max="5">
                        </el-slider>
                        <el-input-number style="width: 90px" v-model="content.num" controls-position="right" :min="1"
                            :max="5"></el-input-number>
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

<style lang="scss" scoped></style>
