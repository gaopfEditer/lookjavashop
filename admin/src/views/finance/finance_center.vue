<template>
    <div class="finance-center">
        <el-card shadow="never" class="!border-none">
            <template #header>
                <span class="text-[#101010] font-medium text-xl">经营状况</span>
            </template>

            <el-row class="mt-4">
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="CourseTotalAmount"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">累计营业额(元)</span>
                            <el-tooltip
                                effect="dark"
                                content="成交订单的金额，无论订单是否取消，只要支付过就算营业额"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.totalOrderAmount }}</div>
                    </div>
                </el-col>
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="CourseTotalNum"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">累计成交订单(笔)</span>
                            <el-tooltip
                                effect="dark"
                                content="已付款就算成交订单数"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.totalOrderNum }}</div>
                    </div>
                </el-col>
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="RefundOrderNum"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">已退款订单金额(元)</span>
                            <el-tooltip
                                effect="dark"
                                content="售后退款成功金额(取消订单已退款金额+售后已退款金额)"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.totalRefundAmount }}</div>
                    </div>
                </el-col>
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="RefundOrderAmount"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">待退款订单金额(元)</span>
                            <el-tooltip
                                effect="dark"
                                content="退款记录中的退款失败和退款中的金额"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.waitRefundAmount }}</div>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <el-card shadow="never" class="!border-none mt-4">
            <template #header>
                <span class="text-[#101010] font-medium text-xl">用户概况</span>
            </template>

            <el-row class="mt-4">
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="TotalChargeAmount"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">用户充值金额(元)</span>
                            <el-tooltip
                                effect="dark"
                                content="所有用户累计充值的金额"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.userRechargeAmount }}</div>
                    </div>
                </el-col>
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="UserTotalAmount"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">用户可用余额(元)</span>
                            <el-tooltip
                                effect="dark"
                                content="所有用户当前余额所剩的余额"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.userUsableMoney }}</div>
                    </div>
                </el-col>
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="UserTotalPeople"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">用户已用余额(元)</span>
                            <el-tooltip
                                effect="dark"
                                content="所有用户总共使用的余额金额"
                                placement="top-start"
                            >
                                <el-icon size="16px">
                                    <Warning />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-4xl black">{{ finance.userUsedMoney }}</div>
                    </div>
                </el-col>
                <el-col :span="6" class="flex">
                    <image-contain
                        class="flex-none"
                        :src="TotalChargeNum"
                        :width="60"
                        :height="60"
                        fit="contain"
                    />
                    <div class="ml-[5px] leading-7">
                        <div class="flex items-center text-base text-[#666]">
                            <span class="mr-2">累计充值人数/人次</span>
                        </div>
                        <div class="text-4xl black">{{ finance.totalRechargeNum }}</div>
                    </div>
                </el-col>
            </el-row>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { financeCenterCount } from '@/api/finance'
import CourseTotalAmount from '@/assets/images/finance/course_total_amount.png'
import CourseTotalNum from '@/assets/images/finance/course_order_total_num.png'
import RefundOrderNum from '@/assets/images/finance/refund_order_num.png'
import RefundOrderAmount from '@/assets/images/finance/refund_order_amount.png'
import TotalChargeAmount from '@/assets/images/finance/total_charge_amount.png'
import UserTotalAmount from '@/assets/images/finance/user_total_amount.png'
import UserTotalPeople from '@/assets/images/finance/user_total_peopel.png'
import TotalChargeNum from '@/assets/images/finance/total_charge_num.png'

const finance = reactive({
    totalOrderNum: 0,
    totalOrderAmount: 0,
    totalRefundAmount: 0,
    waitRefundAmount: 0,
    userUsableMoney: 0,
    userRechargeAmount: 0,
    totalRechargeNum: 0,
    userUsedMoney: 0
})

const getData = async () => {
    try {
        const data = await financeCenterCount()
        Object.keys(data).forEach((key) => {
            //@ts-ignore
            finance[key] = data[key]
        })
    } catch (error) {
        console.log('获取财务中心失败', error)
    }
}

getData()
</script>
