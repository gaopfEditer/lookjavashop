<template>
    <div>
        <!-- Header Start -->
        <el-card shadow="never" class="!border-none">
            <el-page-header content="邀请列表" @back="$router.back()" />
        </el-card>
        <!-- Header End -->
        <!-- body -->
        <el-card shadow="never" class="!border-none mt-[10px]">
            <div class="text-xl font-medium">用户信息</div>
            <el-form ref="formRef" class="ls-form mt-4" :model="pager">
                <el-form-item label="用户信息:"> {{ route.query.name || '-' }} </el-form-item>
                <el-form-item label="邀请人数:"> {{ route.query.num || '-' }} </el-form-item>
            </el-form>
        </el-card>
        <el-card shadow="never" class="!border-none mt-[10px]">
            <div class="text-xl font-medium">邀请列表</div>
            <el-form ref="formRef" class="mt-4" :model="queryParams" :inline="true">
                <el-form-item label="用户信息">
                    <el-input class="w-[400px]" v-model="queryParams.nickname" :placeholder="`请输入${type}`" clearable
                        v-if="type == '用户昵称'">
                        <template #prepend>
                            <el-select class="w-[150px]" v-model="type">
                                <el-option value="用户账号" label="用户账号"></el-option>
                                <el-option value="用户昵称" label="用户昵称"></el-option>
                            </el-select>
                        </template>
                    </el-input>
                    <el-input class="w-[400px]" v-model="queryParams.username" :placeholder="`请输入${type}`" clearable
                        v-if="type == '用户账号'">
                        <template #prepend>
                            <el-select class="w-[150px]" v-model="type">
                                <el-option value="用户账号" label="用户账号"></el-option>
                                <el-option value="用户昵称" label="用户昵称"></el-option>
                            </el-select>
                        </template>
                    </el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
            <!-- <el-tabs v-model="activeTab" @tab-change="handleTabChange">
                <el-tab-pane v-for="(item, index) in tabLists" :label="`${item.name}`" :name="index" :key="index"> -->
            <el-table size="large" v-loading="pager.loading" :data="pager.lists">
                <el-table-column label="用户账号" prop="username" min-width="190" />
                <el-table-column label="用户昵称" prop="nickname" min-width="190" />

                <el-table-column label="用户头像" min-width="190">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <el-avatar :src="row.avatar" :size="50" class="mr-2" />
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="手机号码" prop="mobile" min-width="190" />
                <el-table-column label="钱包余额" prop="money" min-width="190" />
                <el-table-column label="消费金额" prop="totalAmount" min-width="190" />
                <el-table-column label="最近登录时间" prop="lastLoginTime" min-width="190" />
                <el-table-column label="注册时间" prop="createTime" min-width="190" />
            </el-table>
            <!-- </el-tab-pane>
            </el-tabs> -->
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>
    </div>
</template>
<script setup lang="ts">
import { inviterList } from '@/api/user'
import { usePaging } from '@/hooks/usePaging'

const route = useRoute()
const queryParams = reactive({
    userId: Number(route.query.id),
    username: '',
    sn: '',
    nickname: '',
    type: ''
})
const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: inviterList,
    params: queryParams
})
getLists()
// const activeTab = ref(0)
// const tabLists = ref([
//     {
//         name: '全部',
//         type: '',
//         numKey: 'all_num'
//     },
//     {
//         name: '下一级人数',
//         type: 1,
//         numKey: 'first_num'
//     },
//     {
//         name: '下二级人数',
//         type: 2,
//         numKey: 'second_num'
//     }
// ])
// const handleTabChange = (index: any) => {
//     queryParams.type = tabLists.value[index].type as string
//     resetPage()
// }
const type = ref('用户账号')
watch(
    () => type.value,
    () => {
        queryParams.nickname = ''
        queryParams.username = ''
    }
)
</script>
