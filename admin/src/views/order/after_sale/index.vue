<template>
    <div class="after-sales-lists">
        <el-card class="!border-none" shadow="never">

            <!-- Header -->
            <el-form
                ref="formRef"
                class="mb-[-16px] mt-[16px]"
                :model="queryParams"
                :inline="true"
            >
                <el-form-item label="售后单号">
                    <el-input
                        v-model="queryParams.afterSn"
                        placeholder="请输入"
                        class="w-[320px]"
                        @keyup.enter="resetPage"
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="订单编号">
                    <el-input
                        v-model="queryParams.orderSn"
                        placeholder="请输入"
                        class="w-[320px]"
                        @keyup.enter="resetPage"
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="用户昵称">
                    <el-input
                        v-model="queryParams.nickname"
                        placeholder="请输入"
                        class="w-[280px]"
                        @keyup.enter="resetPage"
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="退款方式">
                    <el-select class="w-[280px]" v-model="queryParams.refundType">
                        <el-option label="全部" value="" />
                        <el-option label="仅退款" value="1" />
                        <el-option label="退货退款" value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="售后类型">
                    <el-select class="w-[280px]" v-model="queryParams.afterType">
                        <el-option label="全部" value="" />
                        <el-option label="整单售后" value="1" />
                        <el-option label="部分商品售后" value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="售后状态">
                    <el-select class="w-[280px]" v-model="queryParams.subStatus">
                        <el-option label="全部" value="" />
                        <el-option
                            v-for="(item, index, key) in pager.extend.afterSubStatus"
                            :key="key"
                            :label="item"
                            :value="index"
                        />

                    </el-select>
                </el-form-item>
                <el-form-item label="订单状态">
                    <el-select class="w-[280px]" v-model="queryParams.orderStatus">
                        <el-option label="全部" value="" />
                        <el-option label="待付款" value="1" />
                        <el-option label="待发货" value="2" />
                        <el-option label="待收货" value="3" />
                        <el-option label="已完成" value="4" />
                        <el-option label="已取消" value="5" />
                    </el-select>
                </el-form-item>
                <el-form-item label="申请时间">
                    <daterange-picker
                        value-format="x"
                        :second="true"
                        v-model:startTime="queryParams.startTime"
                        v-model:endTime="queryParams.endTime"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- Main -->
        <el-card class="!border-none mt-4" :body-style="{ 'padding-top': '10px' }" shadow="never">
            <!-- 选项卡 -->
            <el-tabs
                v-model="queryParams.type"
                class="demo-tabs"
                @tab-change="changeTabs"
            >
                <template v-for="(tabsItem, tabsIndex) in tabsParams.TabsEnumMap" :key="tabsIndex">
                    <el-tab-pane
                        :label="`${tabsItem.label}(${getTabsTitle(tabsItem.type) || 0 })`"
                        :name="tabsItem.name"
                    />
                </template>
            </el-tabs>

            <!-- 表格 -->
            <el-table
                size="large"
                v-loading="pager.loading"
                :data="pager.lists"
            >
                <el-table-column prop="afterSn" label="售后单号" width="245" />
                <el-table-column label="买家昵称" min-width="160">
                    <template #default="{ row }">
                        <el-popover placement="top" width="300px" trigger="hover">
                            <div class="flex items-center">
                                <span class="mr-4">头像: </span>
                                <el-avatar :size="50" :src="row?.avatar" />
                            </div>
                            <div class="mt-[20px]">
                                <span class="mr-4"> 昵称: </span>
                                <span>{{ row.nickname }}</span>
                            </div>
                            <div class="mt-[20px]">
                                <span class="mr-4">编号: </span>
                                <span>{{ row.userSn }}</span>
                            </div>
                            <template #reference>
                                <el-tag>{{ row.nickname }}</el-tag>
                            </template>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column label="商品信息" min-width="220">
                    <template #default="{ row }">
                        <router-link :to="`/goods/lists`">
                            <div
                                class="flex mb-[5px]"
                                v-for="(item,index) in row.goodsInfo"
                                :key="index"
                            >
                                <image-contain
                                    class="flex-none"
                                    :src="item.goodsImage"
                                    :width="70"
                                    :height="70"
                                    :preview-src-list="[item.goodsImage]"
                                    :preview-teleported="true"
                                    fit="cover"
                                />
                                <div class="ml-[6px] text-xs overflow-hidden">
                                    <el-tooltip
                                        effect="dark"
                                        :content="item.goodsName"
                                        placement="top"
                                    >
                                        <div class="text-base truncate goods-name">
                                            {{ item.goodsName }}
                                        </div>
                                    </el-tooltip>
                                    <div class="text-tx-placeholder">
                                        {{ item.goodsSkuValue }}
                                    </div>
                                    <div class="black">
                                        ¥{{ item.goodsPrice }}
                                    </div>
                                </div>
                            </div>
                        </router-link>
                    </template>
                </el-table-column>
                <el-table-column label="数量" min-width="100">
                    <template #default="{ row }">
                        {{ row.refundNum }}
                    </template>
                </el-table-column>
                <el-table-column label="退款金额" min-width="90">
                    <template #default="{ row }">
                        ¥{{ row.refundMoney }}
                    </template>
                </el-table-column>
                <el-table-column prop="refundTypeMsg" label="退款方式" min-width="90" />
                <el-table-column prop="afterTypeMsg" label="售后类型" min-width="90" />
                <el-table-column label="售后状态" min-width="200">
                    <template #default="{ row }">
                        <div>{{ row.afterStatusMsg }}</div>
                        <div class="mt-2">{{ row.subStatusMsg }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="申请时间" min-width="180" />
                <el-table-column label="操作" width="280" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['order:after:detail']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('order:after:detail'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                售后详情
                            </router-link>
                        </el-button>

                        <el-button
                            v-perms="['order:after:agree']"
                            v-if="row.afterBtn.agreeBtn"
                            type="primary"
                            :link="true"
                            @click="handleMore('agree', row)"
                        >
                            同意售后
                        </el-button>

                        <el-button
                            v-perms="['order:after:refuse']"
                            v-if="row.afterBtn.refuseBtn"
                            type="danger"
                            :link="true"
                            @click="handleMore('refuse', row)"
                        >
                            拒绝售后
                        </el-button>

                        <el-button
                            v-perms="['order:after:confirmGoods']"
                            v-if="row.afterBtn.confirmGoodsBtn"
                            type="primary"
                            :link="true"
                            @click="handleMore('confirmGoods', row)"
                        >
                            确认收货
                        </el-button>

                        <el-button
                            v-perms="['order:after:refuseGoods']"
                            v-if="row.afterBtn.refuseGoodsBtn"
                            type="danger"
                            :link="true"
                            @click="handleMore('refuseGoods', row)"
                        >
                            拒绝收货
                        </el-button>

                        <el-button
                            v-perms="['order:after:agreeRefund']"
                            v-if="row.afterBtn.agreeRefundBtn"
                            type="primary"
                            :link="true"
                            @click="handleMore('agreeRefund', row)"
                        >
                            同意退款
                        </el-button>

                        <el-button
                            v-perms="['order:after:refuseRefund']"
                            v-if="row.afterBtn.refuseRefundBtn"
                            type="danger"
                            :link="true"
                            @click="handleMore('refuseRefund', row)"
                        >
                            拒绝退款
                        </el-button>

                        <el-button
                            v-perms="['order:after:confirmRefund']"
                            v-if="row.afterBtn.confirmRefundBtn"
                            type="primary"
                            :link="true"
                            @click="handleMore('refund', row)"
                        >
                            确认退款
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>

        <!-- Component -->
        <!-- 审核售后 -->
        <refuse ref="refuseRef" @success="getLists"></refuse>
        <!-- 确认退款 -->
        <refund ref="refundRef" @success="getLists"></refund>
    </div>
</template>
<script lang="ts" setup name="afterSaleLists">
import { usePaging } from '@/hooks/usePaging'
import { getRoutePath } from '@/router'
import type { AfterSaleQueryParamsType } from '@/api/order/after_sale.d'
import {
    afterSaleLists,
    afterSaleAgree,
    afterSaleConfirmGoods,
    afterSaleRefuseGoods,
    afterSaleAgreeRefund,
    afterSaleRefuseRefund
} from '@/api/order/after_sale'
import feedback from "@/utils/feedback";

import Refund from "@/views/order/after_sale/component/refund.vue";
import Refuse from "@/views/order/after_sale/component/refuse.vue";

// Tab类型
enum TabsEnum {
    ALL = 'all',
    APPLY = 'ing',
    FAIL = 'close',
    SUCCESS = 'success'
}

const tabsParams = reactive({
    TabsEnumMap: [
        {
            label: '全部',
            name: 0,
            type: TabsEnum.ALL
        },
        {
            label: '售后中',
            name: 1,
            type: TabsEnum.APPLY
        },
        {
            label: '售后成功',
            name: 2,
            type: TabsEnum.SUCCESS
        },
        {
            label: '售后失败',
            name: 3,
            type: TabsEnum.FAIL
        }
    ]
})

const refuseRef = shallowRef()
const refundRef = shallowRef()
const queryParams = reactive<AfterSaleQueryParamsType>({
    afterSn: '',
    orderSn: '',
    nickname: '',
    refundType: '',
    afterType: '',
    subStatus: '',
    orderStatus: '',
    type: 0,
    startTime: '',
    endTime: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: afterSaleLists,
    params: queryParams
})

const getTabsTitle = computed(() => {
    return (val: number) => {
        try {
            if (!pager?.extend?.statistics || !val) return 0
            return pager?.extend?.statistics[val]
        } catch (error) {
            console.log(error)
        }
    }
})

const changeTabs = (name: number) => {
    queryParams.type = name
    getLists()
}


const handleMore = (event: string, after_sale: any) => {
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
        await getLists()
    } catch (error) {
        console.log('同意售后失败=>', error)
    }
}

const handleConfirmGoods = async (id: number) => {
    try {
        await feedback.confirm('确认收到该售后订单的货物吗？')
        await afterSaleConfirmGoods({ id })
        await getLists()
    } catch (error) {
        console.log('确认收货失败=>', error)
    }
}

const handleRefuseGoods = async (id: number) => {
    try {
        await feedback.confirm('确认拒绝该售后订单的货物吗？')
        await afterSaleRefuseGoods({ id })
        await getLists()
    } catch (error) {
        console.log('拒绝收货失败=>', error)
    }
}


const handleAgreeRefund = async (id: number) => {
    try {
        await feedback.confirm('确认同意退款该售后订单吗？')
        await afterSaleAgreeRefund({ id })
        await getLists()
    } catch (error) {
        console.log('同意退款失败=>', error)
    }
}

const handleRefuseRefund = async (id: number) => {
    try {
        await feedback.confirm('确认拒绝退款该售后订单吗？')
        await afterSaleRefuseRefund({ id })
        await getLists()
    } catch (error) {
        console.log('同意退款失败=>', error)
    }
}

onActivated(() => {
    getLists()
})

getLists()
</script>