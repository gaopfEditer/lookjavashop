<template>
    <div class="order-detail">
        <el-form class="demo-form-inline">
            <el-card class="!border-none" shadow="never">
                <el-page-header :content="$route.meta.title" @back="handleRouteBack" />
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    订单信息
                </div>

                <div class="flex items-center bg-[#f7f7f7] rounded p-[20px]">
                    <icon
                        v-if="order.orderStatus == 3 || order.orderStatus == 2"
                        :size="34"
                        color="#fab428"
                        name="local-icon-time_circle_fill"
                    >
                    </icon>
                    <el-icon v-if="order.orderStatus == 4" size="34px" color="#0aa91e">
                        <SuccessFilled />
                    </el-icon>
                    <el-icon v-else-if="order.orderStatus == 5" size="34px" color="#ff3c3d">
                        <CircleCloseFilled />
                    </el-icon>
                    <span class="text-[20px] font-medium ml-4">
                        {{ order.orderStatusMsg }}
                    </span>
                </div>

                <div class="flex mt-4">
                    <div class="w-2/5">
                        <el-form-item label="订单编号">
                            {{ order.orderSn }}
                        </el-form-item>
                        <el-form-item label="订单来源">
                            {{ order.orderSourceMsg || '-' }}
                        </el-form-item>
                        <el-form-item label="下单时间">
                            {{ order.createTime }}
                        </el-form-item>
                        <el-form-item label="完成时间">
                            {{ order.confirmTime || '-' }}
                        </el-form-item>
                        <el-form-item label="买家留言">
                            {{ order?.userRemark || '-' }}
                        </el-form-item>
                    </div>
                    <div class="w-3/5">
                        <el-form-item label="支付状态">
                            <span v-if="order.payIs">已支付</span>
                            <span v-else class="text-error">未支付</span>
                        </el-form-item>
                        <el-form-item label="支付方式">
                            {{ order.payWayMsg || '-' }}
                        </el-form-item>
                        <el-form-item label="买家信息">
                            <router-link
                                class="text-primary"
                                :to="`/consumer/detail?id=${order.userId}`"
                            >
                                {{ order.nickname }}
                            </router-link>
                        </el-form-item>
                        <el-form-item label="支付时间">
                            {{ order.payTime || '-' }}
                        </el-form-item>
                        <!-- <el-form-item label="退款状态">
                            {{ '-' }}
                        </el-form-item> -->
                        <el-form-item label="商家备注">
                            <div class="break-all">
                                {{ order.shopRemark }}
                            </div>
                        </el-form-item>
                    </div>
                </div>

                <div class="pt-4" style="border-top: 1px solid #eeeeee">
                    <el-button
                        v-perms="['order:order:cancel']"
                        v-if="order.cancelBtn && order.orderStatus != 3"
                        type="danger"
                        @click="handleMore('cancel')"
                    >
                        取消订单
                    </el-button>

                    <el-button
                        v-perms="['order:order:takeDelivery']"
                        v-if="order.confirmBtn"
                        type="primary"
                        @click="handleMore('confirm')"
                    >
                        确认收货
                    </el-button>

                    <el-button
                        v-perms="['order:order:sendDelivery']"
                        v-if="order.deliverBtn"
                        type="primary"
                        @click="handleMore('send')"
                    >
                        去发货
                    </el-button>

                    <el-button
                        v-perms="['order:order:logistics']"
                        v-if="order.logisticsBtn"
                        type="primary"
                        @click="handleMore('delivery')"
                    >
                        查看物流
                    </el-button>

                    <el-button
                        v-perms="['order:order:remarks']"
                        type="default"
                        @click="handleMore('remark')"
                    >
                        商家备注
                    </el-button>
                </div>
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    收货及物流信息
                </div>

                <div class="flex">
                    <div class="w-2/5">
                        <el-form-item label="配送方式">
                            {{ order.deliveryTypeMsg || '-' }}
                        </el-form-item>
                        <el-form-item label="收货人">
                            {{ order.addressContact || '-' }}
                        </el-form-item>
                        <el-form-item label="手机号码">
                            {{ order.addressMobile || '-' }}
                        </el-form-item>
                        <el-form-item label="收货地址">
                            {{ order.addressContent || '-' }}
                        </el-form-item>
                    </div>
                    <div class="w-3/5">
                        <el-form-item label="发货状态">
                            {{ order.expressIsMsg || '-' }}
                        </el-form-item>
                        <el-form-item label="物流公司">
                            {{
                                order.expressCompanyName == null && order.orderStatus > 2
                                    ? '无需物流'
                                    : order.expressCompanyName || '-'
                            }}
                        </el-form-item>
                        <el-form-item label="物流单号">
                            {{ order.expressNo == null ? '无需物流' : order.expressNo || '-' }}
                        </el-form-item>
                        <el-form-item label="发货时间">
                            {{ order.expressTime || '-' }}
                        </el-form-item>
                    </div>
                </div>
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    商品信息
                </div>
                <!-- 商品表格 -->
                <goods-table :goods="order.orderGoodsList" :showPrice="true"></goods-table>
                <!-- 商品总结 -->
                <div class="w-[200px] ml-auto">
                    <div class="flex justify-between mt-4">
                        <span>商品总额: </span>
                        <span>¥{{ order.goodsMoney }}</span>
                    </div>
                    <div class="flex justify-between mt-4">
                        <span>运费金额: </span>
                        <span>¥{{ order.expressMoney }}</span>
                    </div>
                    <div class="flex justify-between mt-4">
                        <span>优惠金额: </span>
                        <span>-¥{{ order.couponMoney }}</span>
                    </div>
                    <div class="flex justify-between mt-4">
                        <span>实际支付:</span>
                        <span class="text-xl text-error">¥{{ order.payMoney }}</span>
                    </div>
                </div>
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    订单日志
                </div>
                <!-- 操作日志表哥 -->
                <el-table size="large" :data="order.orderLogList">
                    <el-table-column prop="operatorName" label="操作人" min-width="100" />
                    <el-table-column prop="content" label="操作内容" min-width="100" />
                    <el-table-column prop="createTime" label="记录日期" min-width="100" />
                </el-table>
            </el-card>
        </el-form>

        <!-- Component -->
        <!-- 订单备注 -->
        <remark ref="remarkRef" @success="getDetails"></remark>
        <!-- 发货 ｜｜ 查看物流 -->
        <logistics ref="logisticsRef" @success="getDetails"></logistics>
    </div>
</template>

<script lang="ts" setup name="OrderDetail">
import { orderDetail, orderCancel, orderTakeDelivery } from '@/api/order/order'
import feedback from '@/utils/feedback'

import Remark from './component/remark.vue'
import Logistics from './component/logistics/index.vue'
import GoodsTable from './component/goods-table.vue'
import useMultipleTabs from '@/hooks/useMultipleTabs'

const { removeTab } = useMultipleTabs()

const route = useRoute()
const router = useRouter()
const remarkRef = shallowRef()
const logisticsRef = shallowRef()
const order = reactive<any>({
    id: '',
    createTime: '',
    orderSn: '',
    status: 4,
    payIs: 1,
    payWayMsg: '',
    payTime: '',
    addressContact: '',
    addressMobile: '',
    addressContent: '',
    deliveryTypeMsg: '',
    expressMoney: '',
    expressIsMsg: '',
    expressTime: '',
    expressNo: '',
    expressCompanyName: '',
    expressConfirmTime: '',
    userRemark: '',
    shopRemark: '',
    orderSourceMsg: '',
    pay_way_text: '',
    orderGoodsList: [],
    orderLogList: [],
    cancelBtn: 0,
    orderStatusMsg: '已取消',
    deliverBtn: 0,
    logisticsBtn: 0,
    confirmBtn: 0,
    confirmTime: ''
})

const getDetails = async () => {
    const data = await orderDetail({
        //@ts-ignore
        id: route.query.id
    })
    Reflect.ownKeys(data).map((item: any) => {
        order[item] = data[item]
    })
}

const handleMore = (event: string) => {
    switch (event) {
        case 'cancel':
            handleCancel(order.id)
            break
        case 'confirm':
            handleConfirm(order.id)
            break
        case 'remark':
            remarkRef.value.open(order.id, order.shopRemark)
            break
        case 'send':
            logisticsRef.value.open('send', order.id)
            break
        case 'delivery':
            logisticsRef.value.open('delivery', order.id)
            break
    }
}

const handleCancel = async (id: number) => {
    try {
        await feedback.confirm('确定要取消订单吗？')
        await orderCancel({ id })
        await getDetails()
    } catch (error) {
        console.log('取消失败=>', error)
    }
}

const handleConfirm = async (id: number) => {
    try {
        await feedback.confirm('确认用户已收到货吗？')
        await orderTakeDelivery({ id })
        await getDetails()
    } catch (error) {
        console.log('确认失败=>', error)
    }
}

const handleRouteBack = () => {
    removeTab()
    router.back()
}

getDetails()
</script>
