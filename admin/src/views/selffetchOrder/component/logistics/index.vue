<template>
    <div class="logistics-popup">
        <popup
            ref="popupRef"
            :title="popupTitle"
            :async="true"
            width="70vw"
            :confirmButtonText="popupConfirmText"
            @confirm="handleSubmit"
            @close="$emit('close')"
        >
            <div v-loading="loading">
                <!-- 商品信息 -->
                <div class="my-4 text-xl font-medium">商品信息</div>
                <!-- Component -->
                <goods-table :goods="order.orderGoodsList"></goods-table>

                <!-- 去发货 -->
                <template v-if="mode == 'send'">
                    <LogisticsDelivery
                        v-if="showDelivery"
                        :order="order"
                        ref="deliveryRef"
                        @success="handleSuccess"
                    ></LogisticsDelivery>
                </template>

                <!-- 物流信息 -->
                <template v-else>
                    <LogisticsInfo :order="order"></LogisticsInfo>
                </template>
            </div>
        </popup>
    </div>
</template>
<script lang="ts" setup>
import { orderDetail, orderLogistics } from '@/api/order/order'

import Popup from '@/components/popup/index.vue'
import GoodsTable from '../goods-table.vue'
import LogisticsInfo from './info.vue'
import LogisticsDelivery from './delivery.vue'

const emit = defineEmits(['success', 'close'])

const loading = ref(true)
const showDelivery = ref(true)
const deliveryRef = shallowRef()

const popupRef = shallowRef<InstanceType<typeof Popup>>()
const mode = ref('send')
const order = reactive<any>({
    id: '',
    orderGoodsList: [],
    // 发货
    addressContact: '',
    addressContent: '',
    addressMobile: '',

    // 查询物流
    logistics: [],
    invoiceNo: '',
    expressCompanyName: '',
    expressNo: '',
    expressConfirmTime: '',
    logistics_error: ''
})

const popupTitle = computed(() => {
    return mode.value == 'send' ? '发货' : '查看物流'
})
const popupConfirmText = computed(() => {
    return mode.value == 'send' ? '发货' : '确认'
})

const handleSubmit = async () => {
    if (mode.value != 'send') {
        popupRef.value?.close()
        return
    }
    showDelivery.value = true
    await nextTick()
    await deliveryRef.value?.onSubmit()
    popupRef.value?.close()
}

const open = async (type = 'send', id: number) => {
    order.id = id
    mode.value = type
    if (mode.value == 'send') {
        await getDetail()
    } else {
        await getLogistics()
    }
    loading.value = false
    popupRef.value?.open()
}

const getDetail = async () => {
    try {
        const data: any = await orderDetail({
            id: order.id as number
        })
        Reflect.ownKeys(order).map((item: any) => {
            if (!data[item]) return
            order[item] = data[item]
        })
    } catch (error) {
        console.log('获取订单详情', error)
    }
}

const getLogistics = async () => {
    try {
        const logistics: any = await orderLogistics({
            id: order.id as number
        })
        order.orderGoodsList = logistics.goods
        order.logistics = logistics.trajectory.track
        order.logistics_error = logistics.trajectory.trackError
        order.expressCompanyName = logistics.express.expressName
        order.expressNo = logistics.express.expressNo
        order.expressConfirmTime = logistics.express.expressTime
    } catch (error) {
        console.log('查看物流', error)
    }
}

const handleSuccess = () => {
    showDelivery.value = false
    emit('success')
    emit('close')
}

defineExpose({ open })
</script>
