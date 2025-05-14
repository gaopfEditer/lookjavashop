<template>
    <div class="freight-lists">
        <el-card
            class="!border-none"
            :body-style="{ 'padding-bottom': '0' }"
            shadow="never"
        >
            <el-page-header
                class="mb-4"
                :content="$route.meta.title"
                @back="$router.back()"
            />

            <el-form
                ref="formRef"
                :model="express.queryParams"
                :inline="true"
            >
                <el-form-item label="快递名称">
                    <el-input
                        class="w-[280px]"
                        v-model="express.queryParams.name"
                        placeholder="请输入快递名称"
                        clearable
                        @keyup.enter="resetPage" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="getLists">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <el-button
                type="primary"
                v-perms="['deliver:express:detail']"
                @click="handleAdd"
            >
                新增快递公司
            </el-button>

            <el-table
                class="mt-4"
                size="large"
                v-loading="express.loading"
                :data="express.lists"
                row-key="id"
            >
                <el-table-column label="快递图标" min-width="120">
                    <template #default="{ row }">
                        <image-contain
                            :src="row.image"
                            :width="40"
                            :height="40"
                            :preview-src-list="[row.image]"
                            :preview-teleported="true"
                            fit="contain"
                        />
                    </template>
                </el-table-column>
                <el-table-column property="name" label="快递公司" min-width="120" />
                <el-table-column property="codeKd" label="快递编码" min-width="120" />
                <el-table-column property="codeKd100" label="快递100编码" min-width="120" />
                <el-table-column property="codeKdniao" label="快递鸟编号" min-width="120" />
                <el-table-column property="sort" label="排序" min-width="120" />
                <el-table-column label="操作" width="280" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['deliver:express:detail']"
                            type="primary"
                            :link="true"
                            @click="handleEdit(row)"
                        >
                            编辑
                        </el-button>
                        <el-button
                            v-perms="['deliver:express:del']"
                            type="danger"
                            :link="true"
                            @click="handleDelete(row.id)"
                        >
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <edit-popup v-if="showEdit" ref="editRef" @success="getLists" @close="showEdit = false" />
    </div>
</template>
<script lang="ts" setup name="expressLists">
import { expressLists, expressDel } from '@/api/setting/delivery'
import { usePaging } from '@/hooks/usePaging'
import feedback from '@/utils/feedback'
import EditPopup from './edit.vue'

const editRef = shallowRef<InstanceType<typeof EditPopup>>()
const showEdit = ref<boolean>(false)

const express = reactive({
    queryParams: {
        name: ''
    },
    loading: true,
    lists: []
})

const getLists = async () => {
    express.loading = true
    try {
        const data = await expressLists(express.queryParams)
        express.lists = data
    } catch (error) {
        console.log('请求快递公司列表报错', error)
    }
    express.loading = false
}

const resetParams = () => {
    express.queryParams.name = ''
    getLists()
}

const handleAdd = async () => {
    showEdit.value = true
    await nextTick()
    editRef.value?.open('add')
}

const handleEdit = async (data: any) => {
    showEdit.value = true
    await nextTick()
    editRef.value?.open('edit')
    editRef.value?.getDetail(data)
}

const handleDelete = async (id: number) => {
    await feedback.confirm('确定要删除当前快递公司吗？')
    await expressDel({ id })
    await getLists()
}

getLists()
</script>
