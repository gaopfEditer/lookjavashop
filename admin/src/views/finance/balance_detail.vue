<template>
    <div class="balance-detail">
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示： 会员账户余额变动记录"
                :closable="false"
                show-icon
            />

            <el-form ref="formRef" class="mb-[-16px] mt-[16px]" :model="queryParams" :inline="true">
                <el-form-item label="用户信息">
                    <el-input
                        v-model="queryParams.keyword"
                        placeholder="请输入用户信息"
                        class="w-[320px]"
                        @keyup.enter="resetPage"
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="变动类型">
                    <el-select class="w-[280px]" v-model="queryParams.type">
                        <el-option
                            v-for="(item, key) in pager.extend.changeType"
                            :key="key"
                            :label="item"
                            :value="key"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="记录时间">
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
        <el-card class="!border-none mt-4" shadow="never">
            <!-- 表格 -->
            <el-table size="large" v-loading="pager.loading" :data="pager.lists">
                <el-table-column label="用户编号" min-width="100">
                    <template #default="{ row }">
                        {{ row.userSn || '-' }}
                    </template>
                </el-table-column>
                <el-table-column label="用户昵称" min-width="220" show-overflow-tooltip>
                    <template #default="{ row }">
                        <div class="flex items-center mb-[5px]">
                            <image-contain
                                class="flex-none"
                                :src="row.avatar"
                                :width="70"
                                :height="70"
                                :preview-src-list="[row.avatar]"
                                preview-teleported
                                fit="contain"
                            />

                            <div class="ml-[6px]">
                                {{ row.nickname }}
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="变动金额" min-width="120">
                    <template #default="{ row }">
                        <span v-if="row.action == 2"
                            >-{{ row.changeAmount }}</span
                        >
                        <span class="text-error" v-else>+{{ row.changeAmount }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="leftAmount" label="剩余金额" min-width="100" />
                <el-table-column prop="changeType" label="变动类型" min-width="140" />
                <el-table-column prop="sourceSn" label="来源单号" min-width="180" />
                <el-table-column prop="createTime" label="记录时间" min-width="180" />
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>
    </div>
</template>
<script lang="ts" setup name="consumerLists">
import { balanceLists } from '@/api/finance'
import { usePaging } from '@/hooks/usePaging'

const queryParams = reactive({
    keyword: '',
    type: '',
    startTime: '',
    endTime: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: balanceLists,
    params: queryParams
})

getLists()
</script>
