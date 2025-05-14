<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            :title="popupTitle"
            :async="true"
            width="550px"
            @confirm="handleSubmit"
            @close="handleClose"
        >
            <el-form ref="formRef" :model="formData" label-width="84px" :rules="formRules">
                <el-form-item label="分类图片">
                    <material-picker v-model="formData.image" size="80px" :limit="1" />
                </el-form-item>
                <el-form-item label="分类名称" prop="name">
                    <div class="w-80">
                        <el-input
                            v-model="formData.name"
                            placeholder="请输入分类名称"
                            :clearable="true"
                            maxlength="10"
                            :show-word-limit="true"
                        />
                    </div>
                </el-form-item>
                <el-form-item label="父级分类" prop="has_parent">
                    <el-radio-group v-model="formData.has_parent" class="ml-4">
                        <el-radio :label="0">无父级分类</el-radio>
                        <el-radio :label="1">有父级分类</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item prop="pid" v-if="formData.has_parent">
                    <el-cascader
                        class="w-80"
                        v-model="formData.pid"
                        :options="goodsCategoryList"
                        :props="props"
                        :clearable="true"
                    />
                </el-form-item>
                <el-form-item label="排序">
                    <div>
                        <div class="w-80">
                            <el-input
                                v-model="formData.sort"
                                placeholder="请输入排序"
                                :clearable="true"
                            />
                        </div>
                        <div class="form-tips">默认为0， 数值越大越排前</div>
                    </div>
                </el-form-item>
                <el-form-item label="是否显示" prop="isShow">
                    <el-switch v-model="formData.isShow" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>
        </popup>
    </div>
</template>
<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import type { GoodsCategoryFormType } from '@/api/goods/category.d'

import {
    goodsCategoryEdit,
    goodsCategoryAdd,
    goodsCategoryDetail,
    goodsCategoryLists
} from '@/api/goods/category'

import Popup from '@/components/popup/index.vue'

const emit = defineEmits(['success', 'close'])

const formRef = shallowRef<FormInstance>()
const popupRef = shallowRef<InstanceType<typeof Popup>>()

const mode = ref('add')
const formData = reactive<GoodsCategoryFormType>({
    id: '',
    name: '',
    sort: 0,
    has_parent: 0,
    pid: 0,
    isShow: 1,
    image: ''
})
const goodsCategoryList = ref([])
const props = reactive({
    multiple: false,
    checkStrictly: true,
    label: 'name',
    value: 'id',
    children: 'children',
    emitPath: false
})
const formRules = reactive<FormRules>({
    name: [{ required: true, message: '请输入分类名称', trigger: ['blur'] }],
    pid: [{ required: true, message: '请选择父级分类', trigger: ['blur'] }],
    has_parent: [{ required: true, message: '请选择父级分类', trigger: ['blur'] }],
    isShow: [{ required: true, message: '请选择是否显示', trigger: ['blur'] }]
})

const popupTitle = computed(() => {
    return mode.value == 'edit' ? '编辑分类' : '新增分类'
})

const handleSubmit = async () => {
    // 不是父级时pid为0，
    if (formData.has_parent == 0) {
        formData.pid = 0
    }
    await formRef.value?.validate()
    mode.value == 'edit' ? await goodsCategoryEdit(formData) : await goodsCategoryAdd(formData)
    popupRef.value?.close()
    emit('success')
}

const open = (type = 'add') => {
    mode.value = type
    popupRef.value?.open()
    getGoodsCategoryList()
}

const setFormData = (data: GoodsCategoryFormType) => {
    for (const key in formData) {
        if (data[key] != null && data[key] != undefined) {
            formData[key] = data[key]
        }
    }
}

const getGoodsCategoryList = async () => {
    try {
        const data = await goodsCategoryLists()
        // 禁止选择自己以及自己以下的
        data.forEach((item: any) => {
            if (formData.id == item.id) {
                item.disabled = true
            }
        })
        console.log(data)
        goodsCategoryList.value = data
    } catch (error) {
        console.log(error)
    }
}

const getDetail = async (row: Record<string, any>) => {
    const data = await goodsCategoryDetail({
        id: row.id
    })
    if (data.pid != 0) {
        formData.has_parent = 1
    }
    setFormData(data)
    await getGoodsCategoryList()
}

const handleClose = () => {
    emit('close')
}

defineExpose({
    open,
    getDetail
})
</script>
