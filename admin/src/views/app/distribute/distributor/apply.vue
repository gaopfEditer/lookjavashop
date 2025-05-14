<template>
    <el-card class="!border-none" shadow="never">
        <el-alert type="warning" title="用户申请审核通过即可成为分销会员；审核拒绝后可再次发起申请！" :closable="false" show-icon />
        <el-form ref="formRef" class="mb-[-16px] mt-4" :model="queryParams" :inline="true">
            <el-form-item label="用户信息">
                <el-input class="w-[280px]" v-model="queryParams.searchUserKeyword" placeholder="请输入用户ID/昵称/手机号码"
                    clearable />
            </el-form-item>
            <el-form-item label="邀请人">
                <el-input class="w-[280px]" v-model="queryParams.searchInviterKeyword" placeholder="请输入邀请人ID/昵称/手机号码"
                    clearable />
            </el-form-item>
            <el-form-item label="申请时间">
                <daterange-picker v-model:startTime="queryParams.startTime" v-model:endTime="queryParams.endTime" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="resetPage">查询</el-button>
                <el-button @click="resetParams">重置</el-button>
            </el-form-item>
        </el-form>
    </el-card>
    <el-card class="!border-none mt-4" shadow="never">
        <el-tabs v-model="queryParams.status" @tab-change="changeTabs">
            <el-tab-pane :label="`全部(${pager.extend.all || 0})`" name="0" />
            <el-tab-pane :label="`待审核(${pager.extend.wait || 0})`" name="1" />
            <el-tab-pane :label="`审核通过(${pager.extend.pass || 0})`" name="2" />
            <el-tab-pane :label="`审核失败(${pager.extend.refuse || 0})`" name="3" />
        </el-tabs>
        <div>
            <el-table ref="tableRef" size="large" v-loading="pager.loading" :data="pager.lists" class="mt-4">
                <el-table-column label="用户信息" min-width="230">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <el-image class="w-[47px] h-[47px]" :src="row.avatar"></el-image>
                            <div class="ml-2">{{ row.nickname }}</div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="真实姓名" prop="realName" min-width="180"></el-table-column>
                <el-table-column label="手机号码" prop="mobile" min-width="180"></el-table-column>
                <el-table-column label="上级邀请人" prop="inviterName" min-width="180">
                    <template #default="{ row }">
                        {{ row.inviterName || '-' }}
                    </template>
                </el-table-column>

                <el-table-column label="审核状态" prop="statusMsg" min-width="180">
                    <template #default="{ row }">
                        <el-tag type="success" v-if="row.status == 2">
                            {{ row.statusMsg || '-' }}</el-tag>
                        <el-tag type="warning" v-if="row.status == 1">
                            {{ row.statusMsg || '-' }}</el-tag>
                        <el-tag type="danger" v-if="row.status == 3">
                            {{ row.statusMsg || '-' }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="申请时间" prop="createTime" min-width="180"></el-table-column>
                <el-table-column label="操作" min-width="180">
                    <template #default="{ row }">
                        <div>
                            <el-button type="primary" @click="openDetail(row.id, 1)" link>详情</el-button>
                            <el-button type="primary" v-if="row.status == 1" link
                                @click="openDetail(row.id, 2)">审核</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </div>
    </el-card>
    <ApplyPop @close="getLists" v-if="popShow" ref="popRef"></ApplyPop>
</template>

<script setup lang="ts">
import { usePaging } from '@/hooks/usePaging'
import { applyList } from '@/api/distribution/distributor'
import ApplyPop from './components/applyPop.vue'
const queryParams = ref({
    searchUserKeyword: '', //搜索名称
    searchInviterKeyword: '', //审核状态 0-待审核 1-通过 2-拒绝
    status: '0',
    startTime: '',
    endTime: ''
})
//弹框显示/隐藏
const popShow = ref(false)

//弹框ref
const popRef = shallowRef()

//分页组件
const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: applyList,
    params: queryParams.value
})

//打开弹框
const openDetail = async (id: number, type: number) => {
    popShow.value = true
    await nextTick()
    popRef.value.open({ id, type })
}

const changeTabs = async () => {
    await nextTick()
    getLists()
}

onMounted(() => {
    getLists()
})
</script>

<style scoped lang="scss"></style>
