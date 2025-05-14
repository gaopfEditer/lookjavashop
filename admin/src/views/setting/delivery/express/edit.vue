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
            <el-form
                ref="formRef"
                :model="formData"
                label-width="120px"
                :rules="formRules"
            >
                <el-form-item label="快递名称" prop="name">
                    <div class="w-80">
                        <el-input
                            v-model="formData.name"
                            placeholder="请输入快递名称"
                            :clearable="true"
                        />
                    </div>
                </el-form-item>
                <el-form-item label="图标">
                    <material-picker
                        v-model="formData.image"
                        size="80px"
                        :limit="1"
                    />
                </el-form-item>
                <el-form-item label="快递编码">
                    <div class="w-80">
                        <el-input
                            v-model="formData.codeKd"
                            placeholder="请输入快递编码"
                            :clearable="true"
                        />
                    </div>
                </el-form-item>
                <el-form-item label="快递100编码">
                    <div class="w-80">
                        <el-input
                            v-model="formData.codeKd100"
                            placeholder="请输入快递100编码"
                            :clearable="true"
                        />
                        <div class="form-tips">快递公司在快递100平台的编码，用于快递查询跟踪</div>
                    </div>
                </el-form-item>
                <el-form-item label="快递鸟编码">
                    <div class="w-80">
                        <el-input
                            v-model="formData.codeKdniao"
                            placeholder="请输入快递鸟编码"
                            :clearable="true"
                        />
                        <div class="form-tips">快递公司在快递鸟平台的编码，用于快递查询跟踪</div>
                    </div>
                </el-form-item>
                <el-form-item label="排序" prop="sort">
                    <div class="w-80">
                        <el-input
                            v-model="formData.sort"
                            placeholder="请输入排序"
                            :clearable="true"
                        />
                        <div class="form-tips">默认为0， 数值越大越排前</div>
                    </div>
                </el-form-item>
            </el-form>
        </popup>
    </div>
</template>
<script lang="ts" setup name="expressEdit">
import type { ExpressFormType } from '@/api/setting/delivery'
import {
    expressEdit,
    expressAdd,
    expressDetail
} from '@/api/setting/delivery'
import Popup from '@/components/popup/index.vue'
import type { FormInstance, FormRules } from 'element-plus'

const emit = defineEmits(['success', 'close'])
const formRef = shallowRef<FormInstance>()
const popupRef = shallowRef<InstanceType<typeof Popup>>()
const mode = ref('add');
const popupTitle = computed(() => {
    return mode.value == 'edit' ? '编辑快递公司' : '新增快递公司'
})

const formData = reactive<ExpressFormType>({
    id: '',
    name: '',
    sort: 0,
    image: '',
    codeKd: '',
    codeKd100: '',
    codeKdniao: ''
})

const formRules = reactive<FormRules>({
    name: [
        {
            required: true,
            message: '请输入快递名称',
            trigger: ['blur']
        }
    ]
})

const handleSubmit = async () => {
    await formRef.value?.validate()
    mode.value == 'edit'
        ? await expressEdit(formData)
        : await expressAdd(formData)
    popupRef.value?.close()
    emit('success')
};

const open = (type = 'add') => {
    mode.value = type
    popupRef.value?.open()
};

const setFormData = (data: ExpressFormType) => {
    for (const key in formData) {
        if (data[key] != null && data[key] != undefined) {
            formData[key] = data[key]
        }
    }
};

const getDetail = async (row: Record<string, any>) => {
    const data: ExpressFormType = await expressDetail({
        id: row.id
    })
    setFormData(data)
}

const handleClose = () => {
    emit('close')
}

defineExpose({
    open,
    setFormData,
    getDetail
})
</script>
