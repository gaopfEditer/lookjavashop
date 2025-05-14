<template>
    <!-- 收货信息 -->
    <div class="my-4 text-xl font-medium">收货信息</div>
    <div class="my-[30px] leading-9">
        <div>收货人: {{ order.addressContact }}</div>
        <div>手机号码: {{ order.addressMobile }}</div>
        <div class="flex">
            <div class="mr-1">收货地址:</div>
            <div class="flex-1">{{ order.addressContent }}</div>
        </div>
    </div>

    <!-- 物流配送 -->
    <div class="my-4 text-xl font-medium">物流配送</div>
    <el-form ref="formRef" :model="formData" label-width="84px" :rules="formRules" inline>
        <div>
            <el-form-item label="发货方式" prop="sendType">
                <el-radio-group v-model="formData.sendType">
                    <el-radio :label="1" size="large">快递配送</el-radio>
                    <el-radio :label="2" size="large">无需物流</el-radio>
                </el-radio-group>
            </el-form-item>
        </div>
        <el-form-item
            label="物流公司"
            prop="expressId"
            style="margin-right: 0"
            v-if="formData.sendType === 1"
        >
            <div class="md:flex-1 phone:block sm:flex">
                <el-select
                    v-model="formData.expressId"
                    placeholder="选择"
                    class="phone:mb-4 phone:w-full sm:w-[150px] md:w-[150px]"
                >
                    <el-option
                        v-for="sitem in delivery"
                        :key="sitem.id"
                        :label="sitem.name"
                        :value="sitem.id"
                    />
                </el-select>
            </div>
        </el-form-item>
        <el-form-item prop="invoiceNo" v-if="formData.sendType === 1">
            <el-input
                v-model.trim="formData.invoiceNo"
                placeholder="请输入"
                class="w-[320px] phone:mb-4 sm:ml-0 phone:ml-[84px]"
            >
            </el-input>
        </el-form-item>
    </el-form>
</template>

<script lang="ts" setup>
import { orderDelivery } from '@/api/order/order'
import { expressLists } from '@/api/setting/delivery'
import type { DeliveryGoodsFormType } from '@/api/order/order.d'
import type { FormInstance, FormRules } from 'element-plus'
import feedback from '@/utils/feedback'

const emit = defineEmits(['success'])

const props = defineProps({
    order: {
        type: Object as PropType<any>,
        default: () => {}
    }
})

const formRef = shallowRef<FormInstance>()

// 快递公司列表
const delivery = ref<any>([])

// 发货表单
const formData = reactive<DeliveryGoodsFormType>({
    orderId: props.order.id,
    sendType: 1,
    invoiceNo: '',
    expressId: ''
})

// 发货规则
const formRules = reactive<FormRules>({
    sendType: [{ required: true, message: '请选择物流方式', trigger: ['blur'] }],
    expressId: [{ required: true, message: '请选择物快递公司', trigger: ['change'] }],
    invoiceNo: [{ required: true, message: '请输入快递单号', trigger: ['change'] }]
})

const onSubmit = async () => {
    await formRef.value?.validate()
    // if (formData.sendType === 1) {
    //     await feedback.confirm(
    //         `仅允许修改一次发货信息，请确认快递信息：${getDeliveryNameById(formData.expressId)} ${
    //             formData.invoiceNo
    //         }`
    //     )
    // } else {
    //     await feedback.confirm('仅允许修改一次发货信息，是否修改为无需快递？')
    // }
    await orderDelivery(formData)
    await formRef.value?.resetFields()
    await feedback.msgSuccess('操作成功')
    emit('success')
    return true
}

const getLists = async () => {
    try {
        const list = await expressLists()
        delivery.value = list
    } catch (error) {
        console.log('获取快递公司失败', error)
    }
}

// function getDeliveryNameById(expressId: string | number): string | null {
//     const foundDelivery = delivery.value.find(
//         (item: Record<string, string | number>) => item.id === expressId
//     )
//     return foundDelivery ? foundDelivery.name : null
// }

getLists()
defineExpose({ onSubmit })
</script>
