<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="商品数据">
                <div>
                    <el-radio-group v-model="content.show_conmment">
                        <el-radio :label="1">显示</el-radio>
                        <el-radio :label="0">隐藏</el-radio>
                    </el-radio-group>
                    <div class="form-tips">如隐藏，前端详情页则不显示用户评价模块</div>
                </div>
            </el-form-item>
            <el-form-item label="商品库存">
                <div>
                    <el-radio-group v-model="content.show_sku_stock">
                        <el-radio :label="1">开启</el-radio>
                        <el-radio :label="0">关闭</el-radio>
                    </el-radio-group>
                    <div class="form-tips">如果开启，则商品详情显示库存</div>
                </div>
            </el-form-item>
            <el-form-item label="商品销量">
                <div>
                    <el-radio-group v-model="content.show_goods_sales">
                        <el-radio :label="1">开启</el-radio>
                        <el-radio :label="0">关闭</el-radio>
                    </el-radio-group>
                    <div class="form-tips">如果开启，则商品详情显示销量</div>
                </div>
            </el-form-item>
            <el-form-item label="浏览量">
                <div>
                    <el-radio-group v-model="content.show_goods_visit">
                        <el-radio :label="1">开启</el-radio>
                        <el-radio :label="0">关闭</el-radio>
                    </el-radio-group>
                    <div class="form-tips">如果开启，则商品详情显示浏览量</div>
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
