<template>
    <el-form-item label="配送方式" prop="goods_send_mode">
        <div class="w-80">
                <el-checkbox :label="1" v-model="formData.isExpress" :true-label="1" :false-label="0">快递配送</el-checkbox>
                <el-checkbox :label="1" v-model="formData.isSelffetch" :true-label="1" :false-label="0">到店自提</el-checkbox>

            <div class="form-tips">必须选择一种配送方式</div>
        </div>
    </el-form-item>
    <el-form-item label="运费设置" prop="expressType">
        <div class="w-80">
            <el-radio-group v-model="formData.expressType">
                <el-radio :label="1">包邮</el-radio>
                <el-radio :label="2">运费模版</el-radio>
            </el-radio-group>
        </div>
    </el-form-item>
    <el-form-item prop="expressTemplateId" v-if="formData.expressType === 2">
        <div class="w-80">
            <el-select
                v-model="formData.expressTemplateId"
                placeholder="请选择运费模版"
            >
                <el-option
                    v-for="item in expressList"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                />
            </el-select>
        </div>
    </el-form-item>
</template>

<script lang="ts" setup>
import { useVModels } from '@vueuse/core'
import { freightLists } from '@/api/setting/delivery'

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

type List = {
    id: number
    name: string
}

const expressList = ref<List[]>([])

const getLists = async () => {
    try {
        const data = await freightLists()
        expressList.value = data;
    } catch (error) {
        console.log(error)
    }
}

getLists()
</script>
