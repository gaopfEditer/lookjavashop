<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            title="拒绝售后"
            :async="true"
            width="550px"
            @confirm="handleSubmit"
            @close="handleClose"
        >
            <el-input
                v-model="formData.remarks"
                placeholder="请输入拒绝理由"
                type="textarea"
                class="mr-8"
                :autosize="{ minRows: 10, maxRows: 10 }"
                show-word-limit
                clearable
            />
        </popup>
    </div>
</template>
<script lang="ts" setup>
import { afterSaleRefuse } from '@/api/order/after_sale'
import Popup from '@/components/popup/index.vue'
import feedback from '@/utils/feedback'

const emit = defineEmits(['success', 'close'])
const popupRef = shallowRef<InstanceType<typeof Popup>>()

type AuditType = {
    id: string | number
    remarks: string
    [index: number | string]: number | string
}

const formData = reactive<AuditType>({
    id: '',
    remarks: ''
})

const handleSubmit = async () => {
    if (!formData.remarks) return feedback.msgError('请输入拒绝理由')
    await afterSaleRefuse({
        id: formData.id as number,
        remarks: formData.remarks
    })
    popupRef.value?.close()
    emit('success')
}

const open = (data: AuditType) => {
    Reflect.ownKeys(data).map((item: any) => {
        formData[item] = data[item]
    })
    popupRef.value?.open()
}

const handleClose = () => {
    emit('close')
}

defineExpose({
    open
})
</script>
