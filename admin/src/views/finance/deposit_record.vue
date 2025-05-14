<template>
    <div class="balance-detail">
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示：用户充值记录"
                :closable="false"
                show-icon
            ></el-alert>
            <el-form ref="formRef" class="mb-[-16px] mt-[16px]" :model="queryParams" :inline="true">
                <el-form-item label="充值单号">
                    <el-input
                        class="w-[280px]"
                        v-model="queryParams.sn"
                        placeholder="请输入充值单号"
                        clearable
                        @keyup.enter="resetPage"
                    />
                </el-form-item>
                <el-form-item label="用户信息">
                    <el-input
                        class="w-[280px]"
                        v-model="queryParams.keyword"
                        placeholder="请输入用户编号/昵称/手机号"
                        clearable
                        @keyup.enter="resetPage"
                    />
                </el-form-item>
                <el-form-item label="支付方式">
                    <el-select class="w-[280px]" v-model="queryParams.payWay">
                        <el-option label="全部" value />
                        <el-option label="微信支付" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="支付状态">
                    <el-select class="w-[280px]" v-model="queryParams.payStatus">
                        <el-option label="全部" value />
                        <el-option label="未支付" :value="0" />
                        <el-option label="已支付" :value="1" />
                    </el-select>
                </el-form-item>
                <el-form-item label="下单时间">
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
                    <export-data
                        class="ml-2.5"
                        :fetch-fun="rechargeLists"
                        :params="queryParams"
                        :page-size="pager.size"
                    />
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <!-- 表格 -->
            <el-table size="large" v-loading="pager.loading" :data="pager.lists">
                <el-table-column prop="id" label="ID" min-width="60" />
                <el-table-column label="用户信息" min-width="220" show-overflow-tooltip>
                    <template #default="{ row }">
                        <div class="flex items-center mb-[5px]">
                            <image-contain
                                class="flex-none"
                                :src="row.avatar"
                                :width="70"
                                :height="70"
                                :preview-src-list="[row.avatar]"
                                :preview-teleported="true"
                                fit="contain"
                            />

                            <div class="ml-[6px]">
                                {{ row.nickname }}
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="orderSn" label="充值单号" min-width="180" />
                <el-table-column prop="orderAmount" label="充值金额" min-width="100" />
                <el-table-column prop="payWay" label="支付方式" min-width="100" />
                <el-table-column label="支付状态" min-width="100">
                    <template #default="{ row }">
                        <span v-if="row.payStatus">已支付</span>
                        <span v-else class="text-error">未支付</span>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="提交时间" min-width="180" />
                <el-table-column prop="payTime" label="支付时间" min-width="180" />
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>
    </div>
</template>
<script lang="ts" setup name="consumerLists">
import { usePaging } from '@/hooks/usePaging'
import { depositLists } from '@/api/finance'

const queryParams = reactive({
    sn: '',
    keyword: '',
    payStatus: '',
    payWay: '',
    startTime: '',
    endTime: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: depositLists,
    params: queryParams
})

getLists()
</script>
