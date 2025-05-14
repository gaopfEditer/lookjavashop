<template>
    <div class="order-detail">
        <el-form class="demo-form-inline">
            <el-card class="!border-none" shadow="never">
                <el-page-header :content="$route.meta.title" @back="handleRouteBack" />
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="bg-[#f7f7f7] rounded p-[20px]">
                    <div class="flex items-center ">
                        <icon
                            v-if="after_sale.afterStatus != 3 && after_sale.afterStatus != 2"
                            :size="34"
                            color="#fab428"
                            name="local-icon-time_circle_fill"
                        >
                        </icon>
                        <el-icon v-if="after_sale.afterStatus == 2" size="34px" color="#0aa91e">
                            <SuccessFilled />
                        </el-icon>
                        <el-icon v-else-if="after_sale.afterStatus == 3" size="34px" color="#ff3c3d">
                            <CircleCloseFilled />
                        </el-icon>
                        <span class="text-[20px] font-medium ml-4">
                            {{ after_sale.afterStatusMsg }}
                        </span>
                        <span
                            class="ml-4 text-[#888888]"
                        >
                            {{ after_sale.subStatusMsg }}
                        </span>
                    </div>

                    <div class="text-error mt-2 ml-[50px]" v-if="after_sale.handleRemark">
                        拒绝原因：{{ after_sale.handleRemark }}
                    </div>
                </div>

                <div class="flex mt-[24px]">
                    <div class="w-2/5">
                        <el-form-item label="退款状态">
                            {{ after_sale.afterStatusMsg || '-' }}
                        </el-form-item>
                        <el-form-item label="售后单号">
                            {{ after_sale.afterSn }}
                        </el-form-item>
                        <el-form-item label="售后类型">
                            {{ after_sale.afterTypeMsg || '-' }}
                        </el-form-item>
                        <el-form-item label="退款原因">
                            {{ after_sale.refundReason || '-' }}
                        </el-form-item>
                        <el-form-item label="退款说明">
                            <div class="break-all">
                                {{ after_sale.refundRemark || '-' }}
                            </div>
                        </el-form-item>
                    </div>
                    <div class="w-3/5">
                        <el-form-item label="订单编号">
                            {{ after_sale.orderSn }}
                        </el-form-item>
                        <el-form-item label="退款方式">
                            {{ after_sale.refundTypeMsg }}
                        </el-form-item>
                        <el-form-item label="退款金额">
                            <span class="text-[#fd3e47]">
                                ¥{{ after_sale.refundMoney }}
                            </span>
                            <el-tooltip
                                class="box-item"
                                effect="dark"
                                content="未发货的订单，用户申请售后可以退回运费；已经发货的订单，用户申请售后不退回运费"
                                placement="top-start"
                            >
                                <el-icon size="18px" class="ml-1 text-[#ccc]">
                                    <QuestionFilled/>
                                </el-icon>
                            </el-tooltip>
                        </el-form-item>
                        <el-form-item label="申请时间">
                            {{ after_sale.createTime }}
                        </el-form-item>
                        <el-form-item label="退款凭证">
                            <template v-for="Img in after_sale.refundImage" :key="Img">
                                <image-contain
                                    v-if="Img.length"
                                    class="flex-none mr-2"
                                    :src="Img"
                                    :width="44"
                                    :height="44"
                                    :preview-src-list="[...after_sale.refundImage]"
                                    :preview-teleported="true"
                                    fit="cover"
                                />
                            </template>
                            {{ after_sale.refundImage[0] == '' ? '-' : ''}}
                        </el-form-item>
                    </div>
                </div>

                <div class="pt-4" style="border-top: 1px solid #eeeeee">
                    <el-button
                        v-perms="['order:after:agree']"
                        v-if="after_sale.afterBtn.agreeBtn"
                        type="primary"
                        @click="handleMore('agree')"
                    >
                        同意售后
                    </el-button>

                    <el-button
                        v-perms="['order:after:refuse']"
                        v-if="after_sale.afterBtn.refuseBtn"
                        type="danger"
                        @click="handleMore('refuse')"
                    >
                        拒绝售后
                    </el-button>

                    <el-button
                        v-perms="['order:after:confirmGoods']"
                        v-if="after_sale.afterBtn.confirmGoodsBtn"
                        type="primary"
                        @click="handleMore('confirmGoods')"
                    >
                        确认收货
                    </el-button>

                    <el-button
                        v-perms="['order:after:refuseGoods']"
                        v-if="after_sale.afterBtn.refuseGoodsBtn"
                        type="danger"
                        @click="handleMore('refuseGoods')"
                    >
                        拒绝收货
                    </el-button>

                    <el-button
                        v-perms="['order:after:agreeRefund']"
                        v-if="after_sale.afterBtn.agreeRefundBtn"
                        type="primary"
                        @click="handleMore('agreeRefund')"
                    >
                        同意退款
                    </el-button>

                    <el-button
                        v-perms="['order:after:refuseRefund']"
                        v-if="after_sale.afterBtn.refuseRefundBtn"
                        type="danger"
                        @click="handleMore('refuseRefund')"
                    >
                        拒绝退款
                    </el-button>

                    <el-button
                        v-perms="['order:after:confirmRefund']"
                        v-if="after_sale.afterBtn.confirmRefundBtn"
                        type="primary"
                        @click="handleMore('refund')"
                    >
                        确认退款
                    </el-button>
                </div>
            </el-card>
            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    买家信息
                </div>

                <div class="flex">
                    <div class="w-2/5">
                        <el-form-item label="买家信息">
                            <router-link
                                class="text-primary"
                                :to="`/consumer/detail?id=${after_sale.userId}`"
                            >
                                {{ after_sale.nickname }}
                            </router-link>
                        </el-form-item>
                    </div>
                </div>
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    退款商品
                </div>
                <!-- 商品表格 -->
                <goods-table :goods="after_sale.refundGoodsList"></goods-table>
            </el-card>

            <el-card class="!border-none mt-4" shadow="never">
                <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                    订单日志
                </div>
                <!-- 操作日志表哥 -->
                <el-table size="large" :data="after_sale.logOrderList">
                    <el-table-column prop="operatorUser" label="操作人" min-width="100" />
                    <el-table-column prop="content" label="操作内容" min-width="100" />
                    <el-table-column prop="create_time" label="记录日期" min-width="100" />
                </el-table>
            </el-card>
        </el-form>

        <!-- Component -->
        <!-- 审核售后 -->
        <refuse ref="refuseRef" @success="getDetails"></refuse>
        <!-- 确认退款 -->
        <refund ref="refundRef" @success="getDetails"></refund>
    </div>
</template>

<script lang="ts" setup name="AfterSaleDetail">
import {
    afterSaleDetail,
    afterSaleAgree,
    afterSaleConfirmGoods,
    afterSaleRefuseGoods,
    afterSaleAgreeRefund,
    afterSaleRefuseRefund
} from '@/api/order/after_sale'
import feedback from '@/utils/feedback'

import Refuse from './component/refuse.vue'
import Refund from './component/refund.vue'
import GoodsTable from './component/goods-table.vue'
import useMultipleTabs from "@/hooks/useMultipleTabs";

const { removeTab } = useMultipleTabs()

const route = useRoute()
const router = useRouter()
const refuseRef = shallowRef()
const refundRef = shallowRef()
const after_sale = reactive<any>({
    afterBtn: {
        agreeBtn: false,
        refuseBtn: false,
        refuseGoodsBtn: false,
        confirmGoodsBtn: false,
        agreeRefundBtn: false,
        refuseRefundBtn: false,
        confirmRefundBtn: false
    },
    id: '',
    userId: '',
    orderSn: '',
    afterSn: '',
    nickname: '',
    afterStatus: '',
    afterStatusMsg: '',
    handleRemark: '',
    subStatus: '',
    subStatusMsg: '',
    refundTypeMsg: '',
    afterTypeMsg: '',
    refundMoney: 0,
    refundReason: '',
    createTime: '',
    refundRemark: '',
    refundImage: [],
    refundGoodsList: [],
    logOrderList: []
})

const getDetails = async () => {
    const data = await afterSaleDetail({
        //@ts-ignore
        id: route.query.id
    })
    Reflect.ownKeys(after_sale).map((item: any) => {
        after_sale[item] = data[item]
    })
}

const handleMore = (event: string) => {
    switch (event) {
        // 同意退款
        case 'agree':
            handleAgree(after_sale.id)
            break
        // 拒绝退款
        case 'refuse':
            const refuse = {
                id: after_sale.id,
                remarks: ''
            }
            refuseRef.value.open(refuse)
            break
        // 确认收货
        case 'confirmGoods':
            handleConfirmGoods(after_sale.id)
            break
        // 拒绝收货
        case 'refuseGoods':
            handleRefuseGoods(after_sale.id)
            break
        // 同意退款
        case 'agreeRefund':
            handleAgreeRefund(after_sale.id)
            break
        // 拒绝退款
        case 'refuseRefund':
            handleRefuseRefund(after_sale.id)
            break
        // 确认退款
        case 'refund':
            const refund = {
                id: after_sale.id,
                type_text: after_sale.refundTypeMsg,
                refund_money: after_sale.refundMoney
            }
            refundRef.value.open(refund)
            break
    }
}

const handleAgree = async (id: number) => {
    try {
        await feedback.confirm('确认要同意该售后订单吗？')
        await afterSaleAgree({ id })
        await getDetails()
    } catch (error) {
        console.log('同意售后失败=>', error)
    }
}

const handleConfirmGoods = async (id: number) => {
    try {
        await feedback.confirm('确认收到该售后订单的货物吗？')
        await afterSaleConfirmGoods({ id })
        await getDetails()
    } catch (error) {
        console.log('确认收货失败=>', error)
    }
}

const handleRefuseGoods = async (id: number) => {
    try {
        await feedback.confirm('确认拒绝该售后订单的货物吗？')
        await afterSaleRefuseGoods({ id })
        await getDetails()
    } catch (error) {
        console.log('拒绝收货失败=>', error)
    }
}


const handleAgreeRefund = async (id: number) => {
    try {
        await feedback.confirm('确认同意退款该售后订单吗？')
        await afterSaleAgreeRefund({ id })
        await getDetails()
    } catch (error) {
        console.log('同意退款失败=>', error)
    }
}

const handleRefuseRefund = async (id: number) => {
    try {
        await feedback.confirm('确认拒绝退款该售后订单吗？')
        await afterSaleRefuseRefund({ id })
        await getDetails()
        await handleRouteBack()
    } catch (error) {
        console.log('同意退款失败=>', error)
    }
}

const handleRouteBack = () => {
    removeTab()
    router.back()
}

getDetails()
</script>
