<template>
    <div class="goods-category-list">
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示：1.用户可以根据商品分类搜索商品，请正确创建分类；2.点击分类名称前符号，显示该商品分类的下级分类。"
                :closable="false"
                :show-icon="true"
            />
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <div class="flex mb-4">
                <el-button type="primary" @click="handleAdd" v-perms="['goods:goodsCategory:add']">
                    <template #icon>
                        <icon name="el-icon-Plus" />
                    </template>
                    添加分类
                </el-button>
                <el-button :plain="true" type="primary" @click="toggleRowExpansion">
                    {{ !expand.length ? '展开' : '收起' }}
                </el-button>
            </div>
            <el-table
                size="large"
                v-loading="pageState.loading"
                :data="pageState.list"
                row-key="id"
                :expand-row-keys="expand"
                :tree-props="{ children: 'children' }"
            >
                <el-table-column width="55" />
                <el-table-column property="name" label="分类名称" max-width="266" />
                <el-table-column label="分类图片" min-width="100">
                    <template #default="{ row }">
                        <image-contain
                            v-if="row.image"
                            :src="row.image"
                            :width="60"
                            :height="45"
                            :preview-src-list="[row.image]"
                            :preview-teleported="true"
                            fit="contain"
                        />
                        <image-contain
                            v-else
                            :src="NullImage"
                            :width="60"
                            :height="45"
                            preview-teleported
                            fit="contain"
                        />
                    </template>
                </el-table-column>
                <el-table-column property="isShow" label="状态" max-width="220">
                    <template #default="{ row }">
                        <el-switch
                            v-perms="['goods:goodsCategory:change']"
                            v-model="row.isShow"
                            :active-value="1"
                            :inactive-value="0"
                            @change="changeStatus($event, row.id)"
                        />
                    </template>
                </el-table-column>
                <el-table-column label="商品数量" prop="goodsNum" min-width="100" />
                <el-table-column label="排序" prop="sort" min-width="100" />
                <el-table-column label="创建时间" prop="createTime" min-width="120" />
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['goods:goodsCategory/edit']"
                            type="primary"
                            :link="true"
                            @click="handleEdit(row)"
                        >
                            编辑
                        </el-button>
                        <el-button
                            v-perms="['goods:goodsCategory/del']"
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
<script lang="ts" setup>
import { goodsCategoryDel, goodsCategoryChange, goodsCategoryLists } from '@/api/goods/category'
import feedback from '@/utils/feedback'
import EditPopup from './edit.vue'
import NullImage from '@/assets/images/null_image.png'

const editRef = shallowRef<InstanceType<typeof EditPopup>>()
const showEdit = ref<boolean>(false)
const pageState = reactive({
    loading: true,
    list: [] as any
})
const expand = ref<string[]>([])

const getLists = async () => {
    try {
        const data = await goodsCategoryLists()
        pageState.list = data
    } catch (error) {
        console.log('获取分类列表失败', error)
    }
    pageState.loading = false
}

const requestPagination = () => {
    getLists()
    expand.value = []
}

// 二级展开收起，不想写递归
const toggleRowExpansion = () => {
    if (!expand.value.length) {
        for (let i = 0; i < pageState.list.length; i++) {
            const list = pageState.list[i]
            const len = list.children?.length
            if (len) {
                for (let j = 0; j < len; j++) {
                    const item = list.children[j]
                    expand.value = [...expand.value, item.id + '']
                }
            }
            expand.value = [...expand.value, list.id + '']
        }
    } else {
        expand.value = []
    }
}

const handleAdd = async () => {
    showEdit.value = true
    await nextTick()
    editRef.value?.open('add')
}

const changeStatus = async ($event: number, id: number) => {
    try {
        await goodsCategoryChange({ id })
        await getLists()
    } catch (error) {
        console.log('切换分类状态失败', error)
    }
}

const handleEdit = async (data: any) => {
    showEdit.value = true
    await nextTick()
    editRef.value?.open('edit')
    editRef.value?.getDetail(data)
}

const handleDelete = async (id: number) => {
    try {
        await feedback.confirm('确定要删除当前分类吗？')
        await goodsCategoryDel({ id })
        await getLists()
    } catch (error) {
        console.log('删除分类失败', error)
    }
}

onMounted(() => {
    requestPagination()
})
</script>
