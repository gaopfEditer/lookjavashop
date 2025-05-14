<template>
    <div class="coupon-lists">
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示：1.优惠券在发放时间内只要未关闭未删除符合条件就能领取；2.优惠券已结束不能继续领取，已发放的优惠券在用券时间内能可继续使用；3.已结束的优惠券允许后台删除，优惠券已删除不能继续领取已发放的优惠券不能继续使用"
                :closable="false"
                show-icon
            />

            <el-form ref="formRef" class="mb-[-16px] mt-[16px]" :model="queryParams" :inline="true">
                <el-form-item label="优惠券搜索">
                    <el-input
                        class="w-[280px]"
                        v-model="queryParams.keyword"
                        placeholder="请输入优惠券名称"
                        :clearable="true"
                        @keyup.enter="resetPage"
                    />
                </el-form-item>
                <el-form-item label="领取方式">
                    <el-select class="w-[280px]" v-model="queryParams.getType">
                        <el-option label="全部" value />
                        <el-option label="用户领取" :value="1" />
                        <el-option label="系统发放" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="使用范围">
                    <el-select class="w-[280px]" v-model="queryParams.useGoodsType">
                        <el-option label="全部" value />
                        <el-option label="全场通用" :value="1" />
                        <el-option label="指定商品可用" :value="2" />
                        <el-option label="指定商品不可用" :value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="!border-none mt-4" :body-style="{ 'padding-top': '10px' }" shadow="never">
            <!-- 选项卡 -->
            <el-tabs v-model="queryParams.type" @tab-change="changeTabs">
                <template v-for="(tabItem, tabIndex) in tabsParams.TabsEnumMap" :key="tabIndex">
                    <el-tab-pane
                        :label="`${tabItem.label}(${pager.extend[tabItem.type] || 0})`"
                        :name="tabItem.name"
                    />
                </template>
            </el-tabs>
            <el-button type="primary">
                <router-link
                    :to="{
                        path: getRoutePath('marketing:coupon:detail')
                    }"
                >
                    + 新增优惠券
                </router-link>
            </el-button>

            <!-- 表格 -->
            <el-table size="large" class="mt-4" v-loading="pager.loading" :data="pager.lists">
                <el-table-column label="优惠券名称" min-width="190">
                    <template #default="{ row }">
                        <div class="text-[#101010]">{{ row.name }}</div>
                        <div class="text-[#999]">券ID：{{ row.sn }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="券内容" prop="discountContent" min-width="190" />
                <el-table-column label="领取方式" min-width="120">
                    <template #default="{ row }">
                        <div v-if="row.getTypeMsg == '买家领取'">
                            {{ row.getTypeMsg }}
                        </div>
                        <div v-else class="text-success">
                            {{ row.getTypeMsg }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="领券时间" prop="getTimeMsg" min-width="200">
                    <!--                    <template #default="{ row }">-->
                    <!--                        <div>{{ row.useTimeMsg }}</div>-->
                    <!--                        <div>{{ row.endTime }}</div>-->
                    <!--                    </template>-->
                </el-table-column>
                <el-table-column label="发放数量" prop="surplusNumber" min-width="100" />
                <el-table-column label="已使用/领取" min-width="120">
                    <template #default="{ row }">
                        <span>{{ row.useNumber }}</span
                        >/
                        <span>{{ row.receiveNumber }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="使用率" prop="usageRate" min-width="100" />
                <el-table-column label="优惠券状态" min-width="120">
                    <template #default="{ row }">
                        <el-tag v-if="row?.status == 1" type="danger">未开始</el-tag>
                        <el-tag v-if="row?.status == 2" type="success">进行中</el-tag>
                        <el-tag v-if="row?.status == 3" type="info">已结束</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" prop="createTime" min-width="180" />
                <el-table-column label="操作" width="240" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['marketing:coupon:detail']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('marketing:coupon:detail'),
                                    query: {
                                        id: row.id,
                                        read_only: 'detail'
                                    }
                                }"
                            >
                                详情
                            </router-link>
                        </el-button>

                        <el-button
                            v-if="row.status != 3 && row.status != 1"
                            v-perms="['marketing:coupon:edit']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('marketing:coupon:detail'),
                                    query: {
                                        id: row.id,
                                        read_only: true
                                    }
                                }"
                            >
                                编辑
                            </router-link>
                        </el-button>

                        <el-button
                            v-if="row.status == 1"
                            v-perms="['marketing:coupon:edit']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('marketing:coupon:detail'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                编辑
                            </router-link>
                        </el-button>

                        <el-button
                            v-if="row.getTypeMsg == '系统发放' && row.status != 3"
                            v-perms="['marketing:coupon:info']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('marketing:coupon:info'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                发券
                            </router-link>
                        </el-button>

                        <el-button
                            v-if="row.status == 1"
                            v-perms="['marketing:coupon:start']"
                            type="primary"
                            :link="true"
                            @click="handleStart(row.id, row.name)"
                        >
                            开始
                        </el-button>

                        <el-button
                            v-if="row.status == 2"
                            v-perms="['marketing:coupon:end']"
                            type="primary"
                            :link="true"
                            @click="handleEnd(row.id, row.name)"
                        >
                            结束
                        </el-button>

                        <el-button
                            v-if="row.status != 2"
                            v-perms="['marketing:coupon:del']"
                            type="danger"
                            :link="true"
                            @click="handleDelete(row.id, row.name)"
                        >
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>
    </div>
</template>
<script lang="ts" setup name="CouponLists">
import { usePaging } from '@/hooks/usePaging'
import { getRoutePath } from '@/router'
import { couponLists, couponStart, couponEnd, couponDel } from '@/api/marketing/coupon'
import type { CouponListQueryParamsType } from '@/api/marketing/coupon.d'
import feedback from '@/utils/feedback'

// Tab类型
enum TabsEnum {
    ALL = 'all',
    WAIT = 'wait',
    NOT = 'not',
    CONDUCT = 'conduct',
    END = 'end'
}

const tabsParams = reactive({
    TabsEnumMap: [
        {
            label: '全部',
            name: '',
            type: TabsEnum.ALL
        },
        {
            label: '未开始',
            name: '1',
            type: TabsEnum.NOT
        },
        {
            label: '进行中',
            name: '2',
            type: TabsEnum.CONDUCT
        },
        {
            label: '已结束',
            name: '3',
            type: TabsEnum.END
        }
    ]
})

const queryParams = reactive<CouponListQueryParamsType>({
    keyword: '',
    getType: '',
    useGoodsType: '',
    type: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: couponLists,
    params: queryParams
})

const changeTabs = (name: string) => {
    queryParams.type = name
    getLists()
}

const handleStart = async (id: number, name: string) => {
    try {
        await feedback.confirm(`确定开始发放(${name})？，请谨慎操作`, '开始发放')
        await couponStart({ id })
        getLists()
    } catch (error) {
        console.log('开启优惠券失败=>', error)
    }
}

const handleEnd = async (id: number, name: string) => {
    try {
        await feedback.confirm(
            `确定结束发放(${name})？结束发放的优惠券不能开始新的领取，请谨慎操作`,
            '结束发放'
        )
        await couponEnd({ id })
        getLists()
    } catch (error) {
        console.log('结束优惠券失败=>', error)
    }
}

const handleDelete = async (id: number, name: string) => {
    try {
        await feedback.confirm(`确定删除(${name})活动吗？`)
        await couponDel({ id })
        getLists()
    } catch (error) {
        console.log('删除优惠券失败=>', error)
    }
}

onActivated(() => {
    console.log('优惠券123')

    getLists()
})

onMounted(() => {
    console.log('优惠456')

    getLists()
})
</script>
