<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            title="回复评价"
            :async="true"
            width="550px"
            @confirm="handleSubmit"
            @close="handleClose"
        >
            <el-form
                ref="formRef"
                :model="formData"
                label-width="84px"
                :rules="formRules"
            >
                <el-form-item label="回复内容" prop="content">
                    <!-- <div> -->
                    <el-input
                        v-model="formData.content"
                        placeholder="请输入回复内容"
                        type="textarea"
                        class="mr-8"
                        :autosize="{ minRows: 10, maxRows: 10 }"
                        show-word-limit
                        clearable
                    />
                    <!-- </div> -->
                </el-form-item>
            </el-form>
        </popup>
    </div>
</template>
<script lang="ts" setup>
import { commentReply } from '@/api/goods/comment'
import Popup from '@/components/popup/index.vue'
import type { FormInstance, FormRules } from 'element-plus'
const emit = defineEmits(['success', 'close'])
const formRef = shallowRef<FormInstance>()
const popupRef = shallowRef<InstanceType<typeof Popup>>()

interface CommentReplyFormType {
    id: number
    content: string
}

const formData = reactive<CommentReplyFormType>({
    id: -1,
    content: ''
})

const formRules = reactive<FormRules>({
    content: [
        {
            required: true,
            message: '请输入回复内容',
            trigger: ['blur']
        }
    ]
})

const handleSubmit = async () => {
    await formRef.value?.validate()
    await commentReply(formData)
    popupRef.value?.close()
    emit('success')
}

const open = (id: number, content: string) => {
    formData.id = id
    formData.content = content
    popupRef.value?.open()
}

const handleClose = () => {
    emit('close')
}

defineExpose({ open })
</script>
