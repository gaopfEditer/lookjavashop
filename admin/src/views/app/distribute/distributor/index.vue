<template>
    <el-card class="!border-none" shadow="never">
        <!-- <el-alert type="warning" title="设置商品的分销佣金比例" :closable="false" show-icon /> -->
        <el-form ref="formRef" class="mb-[-16px] mt-4" :model="queryParams" :inline="true">
            <el-form-item label="用户信息">
                <el-input
                    class="w-[280px]"
                    v-model="queryParams.keyword"
                    placeholder="请输入昵称/编号"
                    clearable
                />
            </el-form-item>
            <el-form-item label="分销等级">
                <el-select class="w-[280px]" v-model="queryParams.levelId">
                    <el-option label="全部" value />
                    <el-option
                        v-for="(item, index) in dropDownList"
                        :key="index"
                        :label="item.name"
                        :value="item.id"
                    ></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="分销状态">
                <el-select class="w-[280px]" v-model="queryParams.isFreeze">
                    <el-option label="全部" value />
                    <el-option label="正常" value="0" />
                    <el-option label="冻结" value="1" />
                </el-select>
            </el-form-item>
            <el-form-item label="成为分销商时间">
                <daterange-picker
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
        <el-button type="primary" @click="addDistributor">开通分销商</el-button>
        <div>
            <el-table
                ref="tableRef"
                size="large"
                v-loading="pager.loading"
                :data="pager.lists"
                class="mt-4"
            >
                <el-table-column label="用户信息" min-width="220">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <el-image
                                class="w-[47px] h-[47px] flex-none"
                                :src="row.avatar"
                            ></el-image>
                            <div class="ml-2">
                                {{ row.nickname }}
                                <span v-if="row.isClose" class="ml-2 text-xs text-danger"
                                    >(已注销)</span
                                >
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="用户等级" min-width="90">
                    <template #default="{ row }">
                        {{ row?.levelName || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="已入账佣金"
                    min-width="90"
                    prop="receivedEarnings"
                ></el-table-column>
                <el-table-column label="待结算佣金" min-width="90">
                    <template #default="{ row }">
                        {{ row.waitEarnings || 0 }}
                    </template>
                </el-table-column>
                <el-table-column label="上级推荐人" min-width="90">
                    <template #default="{ row }">
                        {{ row.firstLeaderName || '系统' }}
                    </template>
                </el-table-column>
                <el-table-column label="分销状态" min-width="90">
                    <template #default="{ row }">
                        {{ row.isFreeze == 0 ? '正常' : '冻结' }}
                    </template>
                </el-table-column>
                <el-table-column
                    label="成为分销商时间"
                    min-width="90"
                    prop="distributionTime"
                ></el-table-column>
                <el-table-column label="操作" min-width="180">
                    <template #default="{ row }">
                        <div>
                            <el-button type="primary" @click="toDetial(row.id)" link
                                >详情</el-button
                            >
                            <el-button
                                :disabled="row.isClose"
                                type="primary"
                                link
                                @click="adjustLevel(row)"
                                >等级调整</el-button
                            >
                            <el-button
                                type="primary"
                                v-if="row.isFreeze == 0"
                                link
                                :disabled="row.isClose"
                                @click="changeStatus(row, 0)"
                                >冻结资格</el-button
                            >
                            <el-button
                                type="primary"
                                v-if="row.isFreeze == 1"
                                link
                                :disabled="row.isClose"
                                @click="changeStatus(row, 1)"
                                >恢复资格</el-button
                            >
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="flex justify-end mt-4">
            <pagination v-model="pager" @change="getLists" />
        </div>
    </el-card>
    <addDistributorPopVue
        v-if="showPop"
        ref="popRef"
        @close="showPop = false"
        @confirm="getLists"
    ></addDistributorPopVue>
    <AdjustLevelPop
        ref="adjustLevelPop"
        @close="showPop = false"
        @confirm="getLists"
        v-if="showPop"
    ></AdjustLevelPop>
</template>

<script setup lang="ts">
import { usePaging } from '@/hooks/usePaging'
import {
    getDistributorList,
    closeDistributionStatus,
    getOtherLists
} from '@/api/distribution/distributor'
import addDistributorPopVue from './components/addDistributorPop.vue'
import feedback from '@/utils/feedback'
import AdjustLevelPop from './components/adjustLevelPop.vue'
import router from '@/router'

//弹框ref
const popRef = shallowRef()
const adjustLevelPop = shallowRef()
const showPop = ref(false)
//搜索参数
const queryParams = ref({
    keyword: '', //关键字搜索
    levelId: '', //分销等级id搜索
    isFreeze: '', //分销状态 1-正常 0-冻结
    startTime: '', //开始时间
    endTime: '' //结束时间
})
//下拉列表
const dropDownList: any = ref([])

//分页组件
const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: getDistributorList,
    params: queryParams.value
})
//获取下拉列表
const getDropDownList = async () => {
    const { lists } = await getOtherLists()
    dropDownList.value = lists
}
//开通分销商
const addDistributor = async () => {
    showPop.value = true
    await nextTick()
    popRef.value.open()
}
//修改分销商状态
const changeStatus = async (row: any, distribution_status: any) => {
    if (distribution_status == 0) {
        await feedback.customConfirm(
            '确定冻结：',
            '？请谨慎处理',
            row.nickname,
            'color: red ',
            '冻结会员'
        )
    }
    if (distribution_status == 1) {
        await feedback.customConfirm(
            '确定恢复：',
            '？请谨慎处理',
            row.nickname,
            ' color: red ',
            '恢复会员'
        )
    }
    await closeDistributionStatus({ id: row.id })

    getLists()
}

//等级调整
const adjustLevel = async (row: any) => {
    showPop.value = true
    await nextTick()
    await adjustLevelPop.value.open(row)
}

//跳转至详情页
const toDetial = (id: any) => {
    router.push(`/app/distribution/distributor/detail?id=${id}`)
}

onMounted(async () => {
    await getLists()
    await getDropDownList()
})
</script>

<style scoped lang="scss"></style>
