<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="是否启用">
                <el-radio-group v-model="content.enabled">
                    <el-radio :label="1">开启</el-radio>
                    <el-radio :label="0">停用</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="标题">
                <el-radio-group v-model="content.title">
                    <el-input
                        class="w-[230px]"
                        v-model="content.title"
                        show-word-limit
                        maxlength="10"
                    ></el-input>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="副标题">
                <el-input
                    class="w-[230px]"
                    v-model="content.sub_title"
                    show-word-limit
                    maxlength="15"
                ></el-input>
            </el-form-item>
            <el-form-item label="背景">
                <div>
                    <el-radio-group v-model="content.bg_type">
                        <el-radio :label="1">系统默认</el-radio>
                        <el-radio :label="0">自定义</el-radio>
                    </el-radio-group>
                    <template v-if="content.bg_type == 0">
                        <material-picker v-model="content.bg_url" exclude-domain />
                        <div class="form-tips">
                            <!-- 建议图片尺寸：60 x 20；图片格式：Gif、jpg、png、jpeg -->
                        </div>
                    </template>
                </div>
            </el-form-item>
            <el-form-item label="显示数量">
                <div class="w-[400px]">
                    <div class="flex">
                        <el-slider
                            class="w-[250px]"
                            v-model="content.num"
                            :max="10"
                            :min="1"
                            show-input
                        />
                    </div>
                    <div class="p-2 bg-primary-light-9 rounded text-primary mt-2">
                        <div>
                            根据系统算法，优先显示销量高且排序在前的商品。优先级为：销量>浏览量>创建时间来排序
                        </div>
                    </div>
                </div>
            </el-form-item>
            <el-form-item label="排序方式">
                <div>
                    <el-radio-group v-model="content.type">
                        <el-radio :label="1" size="medium" border>列表模式</el-radio>
                        <el-radio :label="2" size="medium" border>横向滑动</el-radio>
                    </el-radio-group>
                </div>
            </el-form-item>
            <!-- <el-form-item label="秒杀价格">
                <el-radio-group v-model="content.seckill">
                    <el-radio :label="1">显示</el-radio>
                    <el-radio :label="0">隐藏</el-radio>
                </el-radio-group>
            </el-form-item> -->
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
