<template>
    <div class="goods-lists">
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示：1.新增商品时可选统一规格或者多规格，满足商品不同销售属性场景；2.商品销售状态分为销售中且库存足够时才可下单购买。"
                :closable="false"
                show-icon
            />

            <el-form ref="formRef" class="mb-[-16px] mt-[16px]" :model="queryParams" :inline="true">
                <el-form-item label="商品货号">
                    <el-input
                        class="w-[280px]"
                        v-model="queryParams.code"
                        placeholder="请输入商品货号"
                        :clearable="true"
                        @keyup.enter="resetPage"
                    />
                </el-form-item>
                <el-form-item label="商品名称">
                    <el-input
                        class="w-[280px]"
                        v-model="queryParams.name"
                        placeholder="请输入名称"
                        :clearable="true"
                        @keyup.enter="resetPage"
                    />
                </el-form-item>
                <el-form-item label="商品分类">
                    <el-cascader
                        class="w-[280px]"
                        v-model="queryParams.categoryId"
                        :options="goodsCategoryList"
                        :props="props"
                        :clearable="true"
                        :filterable="true"
                    />
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

            <!-- 按钮组操作 -->
            <div>
                <el-button type="primary" v-perms="['goods:product:add']">
                    <router-link
                        :to="{
                            path: getRoutePath('goods:product:detail')
                        }"
                    >
                        + 发布商品
                    </router-link>
                </el-button>

                <el-button
                    v-perms="['goods:product:batchUpper']"
                    type="default"
                    :plain="true"
                    :disabled="!multipleSelection.length"
                    @click="handleBatchUpper"
                >
                    批量上架
                </el-button>

                <el-button
                    v-perms="['goods:product:batchLower']"
                    type="default"
                    :plain="true"
                    :disabled="!multipleSelection.length"
                    @click="handleBatchLower"
                >
                    批量下架
                </el-button>

                <el-button
                    v-perms="['goods:product:batchDelete']"
                    type="default"
                    :plain="true"
                    :disabled="!multipleSelection.length"
                    @click="handleBatchDel"
                >
                    批量删除
                </el-button>
            </div>

            <!-- 表格 -->
            <el-table
                size="large"
                class="mt-4"
                v-loading="pager.loading"
                :data="pager.lists"
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" />
                <el-table-column
                    label="商品货号"
                    prop="code"
                    width="140"
                    :show-overflow-tooltip="true"
                />
                <el-table-column label="商品信息" min-width="300">
                    <template #default="{ row }">
                        <div class="flex">
                            <el-image
                                fit="cover"
                                :src="row.image"
                                class="flex-none w-[58px] h-[58px]"
                            />
                            <div class="ml-4 overflow-hidden">
                                <el-tooltip effect="dark" :content="row.name" placement="top">
                                    <div class="text-base line-2">
                                        {{ row.name }}
                                    </div>
                                </el-tooltip>
                                <el-tag class="mr-2" checked v-if="row.specType == 2"
                                    >多规格</el-tag
                                >
                                <el-tag type="danger" checked v-if="row.isActivity == 1"
                                    >秒杀</el-tag
                                >
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="价格" prop="price" min-width="160">
                    <template #default="{ row }"> ¥{{ row.sellPrice }} </template>
                </el-table-column>
                <el-table-column label="库存" prop="totalStock" min-width="90" />
                <el-table-column label="销量" prop="salesNum" min-width="90" />
                <el-table-column label="排序" prop="sort" min-width="90">
                    <template #default="{ row }">
                        <popover-input
                            :value="row.sort"
                            class="ml-[10px]"
                            :limit="32"
                            @confirm="handleSort($event, row)"
                        >
                            {{ row.sort }}
                            <el-button type="primary" link v-perms="['goods:product:sort']">
                                <icon name="el-icon-EditPen" />
                            </el-button>
                        </popover-input>
                    </template>
                </el-table-column>
                <el-table-column label="销售状态" min-width="90">
                    <template #default="{ row }">
                        <el-tag v-if="row?.status == 1" type="success">销售中</el-tag>
                        <el-tag type="info" v-else>仓库中</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" prop="createTime" min-width="180" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button v-perms="['goods:product:detail']" type="primary" :link="true">
                            <router-link
                                :to="{
                                    path: getRoutePath('goods:product:detail'),
                                    query: {
                                        id: row.id,
                                        read_only: 1
                                    }
                                }"
                            >
                                详情
                            </router-link>
                        </el-button>

                        <el-button
                            v-perms="['goods:product:detail', 'goods:product:edit']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('goods:product:detail'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                编辑
                            </router-link>
                        </el-button>

                        <el-button
                            v-perms="['goods:product:change']"
                            type="primary"
                            :link="true"
                            @click="handleStatus(row.status ? 0 : 1, row.id)"
                        >
                            {{ row.status === 0 ? '上架' : '下架' }}
                        </el-button>

                        <el-button
                            v-perms="['goods:product:del']"
                            type="danger"
                            :link="true"
                            @click="handleDelete(row.id)"
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
<script lang="ts" setup name="GoodsLists">
import { usePaging } from '@/hooks/usePaging'
import { getRoutePath } from '@/router'
import {
    goodsLists,
    goodsChange,
    goodsDel,
    goodsSort,
    goodsBatchDelete,
    goodsBatchUpper,
    goodsBatchLower
} from '@/api/goods/goods'
import { goodsCategoryLists } from '@/api/goods/category'
import type { GoodsListQueryParamsType } from '@/api/goods/goods.d'
import feedback from '@/utils/feedback'

// 分类组件配置数据
const props = reactive({
    multiple: false,
    checkStrictly: true,
    label: 'name',
    value: 'id',
    children: 'children',
    emitPath: false
})

// Tab类型
enum TabsEnum {
    ALL_COUNT = 'allNum',
    SALES_COUNT = 'salesNum',
    WARNING_COUNT = 'warningNum',
    STORAGE_COUNT = 'storageNum'
}

const tabsParams = reactive({
    TabsEnumMap: [
        {
            label: '全部',
            name: 0,
            type: TabsEnum.ALL_COUNT
        },
        {
            label: '销售中',
            name: 1,
            type: TabsEnum.SALES_COUNT
        },
        {
            label: '库存预警',
            name: 2,
            type: TabsEnum.WARNING_COUNT
        },
        {
            label: '仓库中',
            name: 3,
            type: TabsEnum.STORAGE_COUNT
        }
    ]
})

const queryParams = reactive<GoodsListQueryParamsType>({
    name: '',
    code: '',
    categoryId: '',
    type: 0
})
const multipleSelection = ref<any[]>([])
const goodsCategoryList = ref([])

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: goodsLists,
    params: queryParams
})

const changeTabs = (name: string) => {
    queryParams.type = name
    getLists()
}

const handleSelectionChange = (val: any[]) => {
    multipleSelection.value = val
}

const handleBatchUpper = async () => {
    try {
        await feedback.confirm(`确定要批量上架选中的商品吗？`)
        const ids: number[] = multipleSelection.value.map((item) => item.id)
        await goodsBatchUpper({ ids })
        feedback.msgSuccess('操作成功')
        await getLists()
    } catch (error) {
        console.log('批量上架=>', error)
    }
}

const handleBatchLower = async () => {
    try {
        await feedback.confirm(`确定要批量下架选中的商品吗？`)
        const ids: number[] = multipleSelection.value.map((item) => item.id)
        await goodsBatchLower({ ids })
        feedback.msgSuccess('操作成功')
        await getLists()
    } catch (error) {
        console.log('批量下架=>', error)
    }
}

const handleBatchDel = async () => {
    try {
        await feedback.confirm(`确定要批量删除选中的商品吗？`)
        const ids: number[] = multipleSelection.value.map((item) => item.id)
        await goodsBatchDelete({ ids })
        feedback.msgSuccess('操作成功')
        await getLists()
    } catch (error) {
        console.log('批量删除=>', error)
    }
}

const handleStatus = async (status: number, id: number) => {
    try {
        await feedback.confirm(`确定要${status ? '上架吗' : '下架吗'}？`)
        await goodsChange({ id })
        feedback.msgSuccess('操作成功')
        await getLists()
    } catch (error) {
        console.log('上下架商品失败=>', error)
    }
}

const handleSort = async (sort: number, row: any) => {
    try {
        await goodsSort({ id: row.id, sort: sort })
        feedback.msgSuccess('操作成功')
        await getLists()
    } catch (error) {
        console.log('排序商品失败=>', error)
    }
}

const handleDelete = async (id: number) => {
    try {
        await feedback.confirm('确定要删除该商品吗？')
        await goodsDel({ id })
        feedback.msgSuccess('操作成功')
        getLists()
    } catch (error) {
        console.log('删除商品失败=>', error)
    }
}

const getGoodsCategoryList = async () => {
    try {
        const data = await goodsCategoryLists()
        goodsCategoryList.value = data
    } catch (error) {
        console.log(error)
    }
}

onActivated(() => {
    getLists()
})

onMounted(() => {
    getLists()
    getGoodsCategoryList()
})
</script>
