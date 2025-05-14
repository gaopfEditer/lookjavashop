<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="用户评价">
                <div>
                    <el-radio-group v-model="content.show_evaluate">
                        <el-radio :label="1">显示</el-radio>
                        <el-radio :label="0">隐藏</el-radio>
                    </el-radio-group>
                    <div class="form-tips">
                        开启或停用用户评论模块是否在商品详情里显示，默认开启
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
