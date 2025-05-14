<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            :title="popupTitle"
            :async="true"
            width="750px"
            :clickModalClose="true"
            @confirm="handleSubmit"
            @close="handleClose"
        >
            <el-form ref="formRef" :model="formData" label-width="124px" :rules="formRules">
                
                <el-form-item label="核销员名称" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入核销员名称" />
                </el-form-item>
                <el-form-item label="关联用户" prop="userId">
                    <el-button @click="openPickerUser">{{userIdTips}}</el-button>
                    <!-- <el-input v-model="formData.userId" placeholder="请输入核销员名称" /> -->
                </el-form-item>
                <el-form-item label="关联自提门店" prop="selffetchShopId">
                    <selffetchPickerOne @change="changeSelffetchShop" :title="selffetchShopName" :isPublic="1"></selffetchPickerOne>
                    <!-- <el-input v-model="formData.selffetchShopId" placeholder="请输入核销员名称" /> -->
                </el-form-item>
                <el-form-item label="联系电话" prop="mobile">
                    <el-input class="ls-input" v-model="formData.mobile" show-word-limit placeholder="请输入联系电话" />
                </el-form-item>
                <el-form-item label="核销员状态">
                    <el-radio-group v-model="formData.status">
                        <el-radio :label="0">停用</el-radio>
                        <el-radio :label="1">启用</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </popup>

        <UserPickerOne ref="userPickerPop" v-model="isPickUpUser"></UserPickerOne>
    </div>
</template>
<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import {  selffetchVerifierEdit, selffetchVerifierAdd, selffetchVerifierDetail } from '@/api/selffetchVerifier'
import Popup from '@/components/popup/index.vue'
import feedback from '@/utils/feedback'
import type { PropType } from 'vue'
// import selffetchPickerOne from "@/components/selffetchPickerOne/index.vue"
defineProps({
    dictData: {
        type: Object as PropType<Record<string, any[]>>,
        default: () => ({})
    }
})
const isPickUpUser = ref({})
const emit = defineEmits(['success', 'close'])
const formRef = shallowRef<FormInstance>()
const popupRef = shallowRef<InstanceType<typeof Popup>>()
const mode = ref('add')
const userPickerPop = shallowRef()
const popupTitle = computed(() => {
    return mode.value == 'edit' ? '编辑核销员' : '新增核销员'
})
const selffetchShopName = ref(undefined)

const formData = reactive({
    id: '',
    selffetchShopId: '',
    userId: '',
    sn: '',
    name: '',
    mobile: '',
    status: 1,
})

const formRules = {
    name: [{ required: true, message: '请输入核销员名称', trigger: 'blur' }],
    userId: [{ required: true, message: '请选择用户', trigger: 'blur' }],
    selffetchShopId: [{ required: true, message: '请选择自提门店', trigger: 'blur' }],
    mobile: [{ required: true, message: '请输入手机号码', trigger: 'blur' }]
}


//watch
watch(
    () => isPickUpUser.value,
    (value, oldValue) => {
        formData.userId = value?.id
        formData.nickname = value?.nickname 
    }, {
        immediate: true,
    }
);

//computed
const userIdTips = computed(() => {
    if (formData.nickname) {
        return formData.nickname
    }
    return '请选择核销员'
})


//function
//打开选择用户弹框
const openPickerUser = () => {
    userPickerPop.value.open()
}
const handleSubmit = async () => {
    await formRef.value?.validate()
    const data: any = { ...formData }
    mode.value == 'edit' ? await selffetchVerifierEdit(data) : await selffetchVerifierAdd(data)
    popupRef.value?.close()
    feedback.msgSuccess('操作成功')
    emit('success')
}

const open = (type = 'add') => {
    mode.value = type
    popupRef.value?.open()
}

const setFormData = async (data: Record<string, any>) => {
    for (const key in formData) {
        if (data[key] != null && data[key] != undefined) {
            //@ts-ignore
            formData[key] = data[key]
        }
    }
    selffetchShopName.value = data.selffetchShopName
}

const getDetail = async (row: Record<string, any>) => {
    const data = await selffetchVerifierDetail({
        id: row.id
    })
    setFormData(data)
}

const handleClose = () => {
    emit('close')
}

const changeSelffetchShop = (e) => {
    formData.selffetchShopId = e.id
}

defineExpose({
    open,
    setFormData,
    getDetail
})
</script>
