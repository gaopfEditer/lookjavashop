<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            title="物流接口"
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
                <el-form-item label="选择类型">
                    <el-radio-group v-model="formData.engine">
                        <el-radio label="kdniao">快递鸟</el-radio>
                        <el-radio label="kd100">快递100</el-radio>
                    </el-radio-group>
                </el-form-item>
                <template v-if="formData.engine == 'kdniao'">
                    <el-form-item label="快递鸟套餐">
                        <el-radio-group v-model="formData.kdniao.requestType">
                            <el-radio label="free">免费</el-radio>
                            <el-radio label="charge">收费</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="EBussiness ID">
                        <el-input
                            v-model="formData.kdniao.customer"
                            placeholder=""
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="APPKEY">
                        <el-input
                            v-model="formData.kdniao.key"
                            placeholder=""
                        ></el-input>
                    </el-form-item>
                </template>
                <template v-else>
<!--                    <el-form-item label="接口类型">-->
<!--                        <el-radio-group-->
<!--                            v-model="formData.kd100.interface_type"-->
<!--                        >-->
<!--                            <el-radio label="free">免费版</el-radio>-->
<!--                            <el-radio label="limited_free">限量免费</el-radio>-->
<!--                            <el-radio label="enterprise">企业接口</el-radio>-->
<!--                        </el-radio-group>-->
<!--                    </el-form-item>-->
                    <el-form-item label="CUSTOMER">
                        <el-input
                            v-model="formData.kd100.customer"
                            placeholder=""
                        ></el-input>
                    </el-form-item>
                    <el-form-item label="APPKEY">
                        <el-input
                            v-model="formData.kd100.key"
                            placeholder=""
                        ></el-input>
                    </el-form-item>
                </template>
            </el-form>
        </popup>
    </div>
</template>
<script lang="ts" setup>
import type { LogisticsFormType } from '@/api/setting/delivery'
import { getLogistics, logisticsSet } from '@/api/setting/delivery'
import Popup from '@/components/popup/index.vue'
import type { FormInstance, FormRules } from 'element-plus'

const emit = defineEmits(['success', 'close'])
const formRef = shallowRef<FormInstance>()
const popupRef = shallowRef<InstanceType<typeof Popup>>()
const mode = ref<string>('add')

const formData = reactive<LogisticsFormType>({
    kdniao: {
        requestType: 'free',
        key: '',
        customer: ''
    },
    kd100: {
        key: '',
        customer: ''
    },
    engine: 'kd100'
});

const formRules = reactive<FormRules>({
    name: [
        {
            required: true,
            message: '请输入分类名称',
            trigger: ['blur']
        }
    ]
})

const handleSubmit = async () => {
    await formRef.value?.validate()
    await logisticsSet(formData)
    popupRef.value?.close()
    emit('success')
}

const open = (type = 'add') => {
    getDetail()
    mode.value = type
    popupRef.value?.open()
}

const setFormData = (data: LogisticsFormType) => {
    for (const key in formData) {
        if (data[key] != null && data[key] != undefined) {
            formData[key] = data[key]
        }
    }
}

const getDetail = async () => {
    const data = await getLogistics()
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
