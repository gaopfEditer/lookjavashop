<template>
    <div class="goods-lists">
        <el-card class="!border-none" shadow="never">
            <el-alert type="warning" :closable="false" show-icon>
                <div>
                    温馨提示：
                    1.审核会员的佣金提现申请；2.佣金提现支持微信、支付宝转账；3.提现失败会退回全部佣金
                </div>
            </el-alert>

            <el-form ref="formRef" class="mb-[-16px] mt-[16px]" :model="queryParams" :inline="true">
                <el-form-item label="用户信息">
                    <el-input class="w-[280px]" v-model="queryParams.keyword" placeholder="请输入用户信息" clearable
                        @keyup.enter="resetPage" />
                </el-form-item>
                <el-form-item label="变动类型">
                    <el-select class="w-[280px]" v-model="queryParams.type">
                        <el-option label="全部" value="" />
                        <el-option label="钱包余额" :value="1" />
                        <el-option label="微信零钱" :value="2" />
                        <el-option label="银行卡" :value="3" />
                        <el-option label="微信收款码" :value="4" />
                        <el-option label="支付宝收款码" :value="5" />
                    </el-select>
                </el-form-item>
                <el-form-item label="记录时间">
                    <daterange-picker v-model:startTime="queryParams.startTime" v-model:endTime="queryParams.endTime" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="!border-none mt-4" :body-style="{ 'padding-top': '10px' }" shadow="never">
            <!-- 选项卡 -->
            <el-tabs v-model="queryParams.status" @tab-change="changeTabs">
                <template v-for="(tabItem, tabIndex) in tabsParams.TabsEnumMap" :key="tabIndex">
                    <el-tab-pane :label="`${tabItem.label}(${pager.extend[tabItem?.type] || 0})`" :name="tabItem.name" />
                </template>
            </el-tabs>

            <!-- 表格 -->
            <el-table size="large" v-loading="pager.loading" :data="pager.lists" class="mt-4">
                <el-table-column label="提现单号" prop="sn" width="180" />
                <el-table-column label="用户信息" min-width="140">
                    <template #default="{ row }">
                        <el-popover placement="top" width="300px" trigger="hover">
                            <div>头像: <el-avatar :src="row.avatar" size="50"></el-avatar></div>
                            <div class="mt-[20px]">昵称: {{ row.nickname }}</div>
                            <div class="mt-[20px]">账号: {{ row.userSn || '-' }}</div>
                            <template #reference>
                                <el-tag>{{ row.nickname }}</el-tag>
                            </template>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column label="提现金额" prop="money" min-width="100" />
                <el-table-column label="提现手续费" prop="handlingFee" min-width="120" />
                <el-table-column label="到账金额" prop="leftMoney" min-width="100" />
                <el-table-column label="提现方式" prop="typeMsg" min-width="100" />
                <el-table-column label="提现状态" min-width="100">
                    <template #default="{ row }">
                        <el-tag type="warning" v-if="row.status == 1">{{ row.statusMsg }}</el-tag>
                        <el-tag type="primary" v-if="row.status == 2">{{ row.statusMsg }}</el-tag>
                        <el-tag type="success" v-if="row.status == 3">{{ row.statusMsg }}</el-tag>
                        <el-tag type="danger" v-if="row.status == 4">{{ row.statusMsg }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="提现备注" prop="applyRemark" min-width="120" />
                <el-table-column label="申请时间" prop="createTime" min-width="180" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button type="primary" link v-perms="['mall.shopWithdraw/detail']"
                            @click="handleWithDrawLog(row.id)">
                            详情
                        </el-button>

                        <el-button v-perms="['mall.shopWithdraw/audit']" v-if="row.status == 1" type="primary" link
                            @click="handleWithDrawAudit(row.id)">
                            审核
                        </el-button>

                        <el-button v-perms="['mall.shopWithdraw/transfer']" v-if="row.status == 2" type="primary" link
                            @click="handleWithDrawTransfer(row.id)">
                            转账
                        </el-button>
                        <el-button v-perms="['mall.shopWithdraw/transfer']" v-if="row.status == 2 && row.typeMsg == '微信零钱'"
                            type="primary" link @click="handleresult(row.id)">
                            查询结果
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>

        <!-- 提现详情 -->
        <withdraw-log v-if="withdrawLogShow" ref="withdrawLogRef" @close="withdrawLogShow = false"></withdraw-log>

        <!-- 审核弹窗 -->
        <withdraw-audit v-if="showAudit" ref="withdrawAuditRef" @close="showAudit = false"
            @success="getLists"></withdraw-audit>

        <!-- 审核弹窗 -->
        <withdraw-transfer v-if="showTransfer" ref="withdrawTransferRef" @close="showTransfer = false"
            @success="getLists"></withdraw-transfer>
    </div>
</template>
<script lang="ts" setup name="shopWithdrawLists">
import { usePaging } from '@/hooks/usePaging'
import { getRoutePath } from '@/router'
import { getWithdrawLists, withdrawquery } from '@/api/finance'
import feedback from '@/utils/feedback'

import WithdrawLog from './component/withdraw-log.vue'
import WithdrawAudit from './component/withdraw-audit.vue'
import WithdrawTransfer from './component/withdraw-transfer.vue'

const withdrawLogRef = shallowRef()
const withdrawAuditRef = shallowRef()
const withdrawTransferRef = shallowRef()
const withdrawLogShow = ref(false)
const showAudit = ref(false)
const showTransfer = ref(false)

// 分类组件配置数据
const props = reactive({
    multiple: false,
    checkStrictly: true,
    label: 'name',
    value: 'id',
    children: 'sons',
    emitPath: false
})

// Tab类型
enum TabsEnum {
    ALL = 'all',
    WAIT = 'wait',
    ING = 'ing',
    SUCCESS = 'success',
    FAIL = 'fail'
}

const tabsParams = reactive({
    TabsEnumMap: [
        {
            label: '全部',
            name: '',
            type: TabsEnum.ALL
        },
        {
            label: '待提现',
            name: '1',
            type: TabsEnum.WAIT
        },
        {
            label: '提现中',
            name: '2',
            type: TabsEnum.ING
        },
        {
            label: '提现成功',
            name: '3',
            type: TabsEnum.SUCCESS
        },
        {
            label: '提现失败',
            name: '4',
            type: TabsEnum.FAIL
        }
    ]
})

const queryParams = reactive({
    keyword: '',
    startTime: '',
    endTime: '',
    type: '',
    status: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: getWithdrawLists,
    params: queryParams
})

const changeTabs = (name: string) => {
    queryParams.status = name
    getLists()
}

// 显示提现
const handleWithDrawLog = async (id: number) => {
    withdrawLogShow.value = true
    await nextTick()
    withdrawLogRef.value.open(id)
}

// 显示提现
const handleWithDrawAudit = async (id: number) => {
    showAudit.value = true
    await nextTick()
    withdrawAuditRef.value.open(id)
}

// 显示提现
const handleWithDrawTransfer = async (id: number) => {
    showTransfer.value = true
    await nextTick()
    withdrawTransferRef.value.open(id)
}
const handleresult = async (id: number) => {
    await withdrawquery({ id })
    getLists()
}
onActivated(() => {
    getLists()
})

getLists()
</script>
