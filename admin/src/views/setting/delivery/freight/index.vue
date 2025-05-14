<template>
    <div class="freight-lists">
        <el-card class="!border-none" shadow="never">
            <el-page-header
                class="mb-4"
                :content="$route.meta.title"
                @back="$router.back()"
            />

            <el-alert
                type="warning"
                title="温馨提示：设置快递配送的运费模板；需要开启快递发货的配送方式，运费模板才能生效"
                :closable="false"
                :show-icon="true"
            />
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <el-button
                type="primary"
                v-perms="['deliver:tpl:detail']"
            >
                <router-link
                    :to="{
                        path: getRoutePath('deliver:tpl:detail')
                    }"
                >
                    + 新增
                </router-link>
            </el-button>

            <el-table
                class="mt-4"
                size="large"
                v-loading="state.loading"
                :data="state.lists"
                row-key="id"
            >
                <el-table-column property="name" label="模版名称" min-width="120" />
                <el-table-column property="typeDesc" label="计费方式" min-width="120" />
                <el-table-column property="remark" label="备注" min-width="120" />
                <el-table-column property="createTime" label="创建时间" min-width="120" />
                <el-table-column label="操作" width="280" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['deliver:tpl:detail']"
                            type="primary"
                            :link="true"
                        >
                            <router-link
                                :to="{
                                    path: getRoutePath('deliver:tpl:detail'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                编辑
                            </router-link>
                        </el-button>
                        <el-button
                            v-perms="['deliver:tpl:del']"
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
<script lang="ts" setup>
import { freightLists, freightDel } from '@/api/setting/delivery'
import { getRoutePath } from '@/router'
import feedback from '@/utils/feedback'

const state = reactive({
    loading: true,
    lists: []
})

const getLists = async () => {
    state.loading = true
    try {
        const data = await freightLists()
        state.lists = data
    } catch (error) {
        console.log('请求配送模板列表失败', error)
    }
    state.loading = false
}

const handleDelete = async (id: number) => {
    try {
        await feedback.confirm('确定要删除？')
        await freightDel({ id })
        await getLists()
    } catch (error) {
        console.log('删除运费模版失败=>', error)
    }
}

getLists()
</script>
