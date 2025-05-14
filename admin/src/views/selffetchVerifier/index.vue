<template>
    <div class="index-lists">
        <el-card class="!border-none" shadow="never">
            <el-form ref="formRef" class="mb-[-16px]" :model="queryParams" :inline="true">
                <el-form-item label="核销员">
                        <el-input v-model="queryParams.name" placeholder="请输入核销员" />
                    </el-form-item>
                    <!-- 核销门店 -->
                    <el-form-item label="核销门店">
                        <selffetchPickerOne @change="changeSelffetchShop"></selffetchPickerOne>
                        <!-- <el-input v-model="queryParams.shopName" placeholder="请输入核销门店" /> -->
                    </el-form-item>
                    <!-- 核销员状态 -->
                    <el-form-item label="核销员状态" class="m-l-24">
                        <el-select v-model="queryParams.status" placeholder="请选择状态">
                            <el-option label="全部" value=""></el-option>
                            <el-option label="启用" :value="1"></el-option>
                            <el-option label="停用" :value="0"></el-option>
                        </el-select>
                    </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <div>
                <el-button v-perms="['selffetchVerifier:add']" type="primary" @click="handleAdd()">
                    <template #icon>
                        <icon name="el-icon-Plus" />
                    </template>
                    新增
                </el-button>
            </div>
            <el-table
                class="mt-4"
                size="large"
                v-loading="pager.loading"
                :data="pager.lists"
            >
                <el-table-column prop="sn" label="核销员编号" min-width="100" />
                <el-table-column prop="name" label="核销员姓名" min-width="100" />
                <el-table-column prop="nickname" label="用户名称" min-width="180">
                    <template  #default="{ row }">
                        <div class="flex">
                            <el-image :src="row.avatar" style="width: 40px; height: 40px" fit="fill" />
                            <span class="m-l-10 nickname-class">{{ row.nickname }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="selffetchShopName" label="门店名称" min-width="180" />
                <el-table-column prop="status" label="核销员状态" min-width="120">
                    <template  #default="{ row }">
                        <el-switch
                            v-model="row.status"
                            :active-value="1"
                            :inactive-value="0"
                            @change="changeSwitchStatus($event, row)"
                        />
                    </template>
                </el-table-column>
                <el-table-column sortable prop="createTime" label="创建时间" width="180" />
                <el-table-column label="操作" width="120" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['selffetchVerifier:edit']"
                            type="primary"
                            link
                            @click="handleEdit(row)"
                        >
                            编辑
                        </el-button>
                        <el-button
                            v-perms="['selffetchVerifier:del']"
                            type="danger"
                            link
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
        <edit-popup
            v-if="showEdit"
            ref="editRef"
            @success="getLists"
            @close="showEdit = false"
        />
    </div>
</template>
<script lang="ts" setup name="selffetchVerifier">
import { selffetchVerifierDelete, selffetchVerifierLists, apiSelffetchVerifierStatus } from '@/api/selffetchVerifier'
import { usePaging } from '@/hooks/usePaging'
import feedback from '@/utils/feedback'
import EditPopup from './edit.vue'
const editRef = shallowRef<InstanceType<typeof EditPopup>>()
const showEdit = ref(false)
const queryParams = reactive({
    selffetchShopId: '',
    userId: '',
    sn: '',
    name: '',
    mobile: '',
    status: '',
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: selffetchVerifierLists,
    params: queryParams
})



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
    await feedback.confirm('确定要删除？')
    await selffetchVerifierDelete({ id })
    feedback.msgSuccess('删除成功')
    getLists()
}

// 更改状态
const changeSwitchStatus = async (value: 0 | 1, data: any) => {
    apiSelffetchVerifierStatus({
            id: data.id,
            status: value
        }).catch(err => {
            getLists()
        })
}

const changeSelffetchShop = (e) => {
    queryParams.selffetchShopId = e.id
}

getLists()
</script>

<style lang="scss" scoped>
.nickname-class {
    margin-top: 8px;
}
</style>

