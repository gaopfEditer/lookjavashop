<template>
    <div>
        <el-card class="!border-none" shadow="never">
            <el-form ref="formRef" class="mb-[-16px]" :model="queryParams" :inline="true">
                <el-form-item label="用户信息">
                    <el-input
                        class="w-[280px]"
                        v-model="queryParams.keyword"
                        placeholder="用户编号/昵称/手机号码"
                        clearable
                        @keyup.enter="resetPage"
                    />
                </el-form-item>
                <el-form-item label="注册时间">
                    <daterange-picker
                        v-model:startTime="queryParams.startTime"
                        v-model:endTime="queryParams.endTime"
                    />
                </el-form-item>
                <el-form-item label="注册来源">
                    <el-select class="w-[280px]" v-model="queryParams.channel">
                        <el-option
                            v-for="(item, key) in ClientMap"
                            :key="key"
                            :label="item"
                            :value="key"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <el-table size="large" v-loading="pager.loading" :data="pager.lists">
                <el-table-column label="用户编号" prop="sn" min-width="120" />
                <el-table-column label="头像" min-width="140">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <el-avatar class="flex-shrink-0" :src="row.avatar" :size="50" />
                            <div v-if="row.isClose" class="ml-2 text-xs text-danger">(已注销)</div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="昵称" prop="nickname" min-width="120" />
                <el-table-column label="账号" prop="username" min-width="120" />
                <el-table-column label="手机号码" prop="mobile" min-width="120" />
                <el-table-column label="性别" prop="sex" min-width="100" />
                <el-table-column label="注册来源" prop="channel" min-width="100" />
                <el-table-column label="注册时间" prop="createTime" min-width="180" />
                <el-table-column label="操作" width="180" fixed="right">
                    <template #default="{ row }">
                        <el-button v-perms="['user:detail']" type="primary" link>
                            <router-link
                                :to="{
                                    path: getRoutePath('user:detail'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                详情
                            </router-link>
                        </el-button>

                        <el-dropdown @command="handleMore($event, row)" v-if="!row.isClose">
                            <el-button type="primary" link class="mt-[2px] mx-2">
                                <span>更多</span>
                                <el-icon class="el-icon--right">
                                    <arrow-down />
                                </el-icon>
                            </el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="wallet"> 调整余额 </el-dropdown-item>
                                    <el-dropdown-item command="coupon">
                                        发放优惠券
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>

        <!--    余额调整    -->
        <account-adjust
            v-model:show="adjustState.show"
            :value="adjustState.value"
            @confirm="handleConfirmAdjust"
        />
        <!--    优惠券发放    -->
        <send-coupon v-model:show="couponState.show" :value="couponState.value"></send-coupon>
    </div>
</template>
<script lang="ts" setup name="consumerLists">
import { usePaging } from '@/hooks/usePaging'
import { getRoutePath } from '@/router'
import { adjustMoney, getUserList } from '@/api/consumer'
import { ClientMap } from '@/enums/appEnums'
import feedback from '@/utils/feedback'

import AccountAdjust from '@/views/consumer/components/account-adjust.vue'
import SendCoupon from '@/views/consumer/components/send-coupon.vue'

const queryParams = reactive({
    keyword: '',
    channel: '',
    startTime: '',
    endTime: ''
})

const adjustState = reactive({
    show: false,
    value: '',
    userId: ''
})
const couponState = reactive({
    show: false,
    value: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: getUserList,
    params: queryParams
})

const handleMore = async (event: string, row: any) => {
    switch (event) {
        case 'wallet':
            adjustState.show = true
            adjustState.value = row.money
            adjustState.userId = row.id
            break
        case 'coupon':
            couponState.show = true
            couponState.value = row.id
            break
    }
}

const handleConfirmAdjust = async (value: any) => {
    await adjustMoney({ userId: adjustState.userId, ...value })
    feedback.msgSuccess('调整成功')
    adjustState.show = false
    await getLists()
}

onActivated(() => {
    getLists()
})

getLists()
</script>
