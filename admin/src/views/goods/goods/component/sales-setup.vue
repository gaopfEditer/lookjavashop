<template>
    <el-form-item
        label="库存预警"
        prop="stockWarning"
        :rules="[{ pattern: /^[0-9|^\\.]/, message: '数值不能为负数或字母' }]"
    >
        <div class="w-80">
            <el-input
                v-model.number="formData.stockWarning"
                placeholder="请输入库存预警"
                :clearable="true"
            />
            <div class="form-tips">
                设置最低库存预警值，不填或填0表示不做库存预警
            </div>
        </div>
    </el-form-item>
    <el-form-item
        label="虚拟销量"
        prop="virtualSalesNum"
        :rules="[{ pattern: /^[0-9|^\\.]/, message: '数值不能为负数或字母' }]"
    >
        <div class="w-80">
            <el-input
                v-model.number="formData.virtualSalesNum"
                placeholder="请输入虚拟销量"
                :clearable="true"
            />
        </div>
    </el-form-item>
    <el-form-item
        label="虚拟浏览量"
        prop="virtualClickNum"
        :rules="[{ pattern: /^[0-9|^\\.]/, message: '数值不能为负数或字母' }]"
    >
        <div class="w-80">
            <el-input
                v-model.number="formData.virtualClickNum"
                placeholder="请输入虚拟浏览量"
                :clearable="true"
            />
        </div>
    </el-form-item>
    <el-form-item
        label="排序"
        prop="sort"
        :rules="[{ pattern: /^[0-9|^\\.]/, message: '数值不能为负数或字母' }]"
    >
        <div class="w-80">
            <el-input
                v-model.number="formData.sort"
                placeholder=""
                :clearable="true"
            />
            <div class="form-tips">默认为0，数值越大越排前</div>
        </div>
    </el-form-item>
    <el-form-item label="销售状态" prop="status">
        <div class="w-80">
            <el-radio-group v-model="formData.status" class="ml-4">
                <el-radio :label="1">上架销售</el-radio>
                <el-radio :label="0">放入仓库</el-radio>
            </el-radio-group>
        </div>
    </el-form-item>
</template>

<script lang="ts" setup>
import { useVModels } from '@vueuse/core'

const props = withDefaults(
    defineProps<{
        modelValue?: any
    }>(),
    {
        modelValue: {}
    }
);

const emit = defineEmits(['update:modelValue'])
const { modelValue: formData } = useVModels(props, emit)
</script>
