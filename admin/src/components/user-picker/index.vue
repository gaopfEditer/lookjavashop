<template>
    <Popup title="选择用户" ref="popRef" width="auto" async @confirm="comfrim">
        <view>
            <el-form>
                <el-form-item label="搜索用户">
                    <el-input v-model="queryParams.keyword" class="w-[240px]" placeholder="请输入用户名称" @input="getLists">
                        <template #append>
                            <el-icon>
                                <Search />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
            </el-form>
            <div>
                <el-table ref="tableRef" size="large" v-loading="pager.loading" :data="pager.lists" class="mt-4"
                    height="400px" :row-key="getRowKey">
                    <el-table-column reserve-selection type="selection" width="55" />
                    <el-table-column label="用户账号" prop="username" min-width="120"></el-table-column>
                    <el-table-column label="用户昵称" min-width="180">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <el-image class="w-[48px] h-[48px] flex-none" :src="row.avatar"></el-image>
                                <div class="ml-2">{{ row.nickname }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="用户余额" min-width="120">
                        <template #default="{ row }">
                            <div>¥{{ row.money }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column label="手机号码" prop="mobile" min-width="120">
                        <template #default="{ row }">
                            {{ row.mobile || `-` }}
                        </template>
                    </el-table-column>
                    <el-table-column label="分销商" min-width="120">
                        <template #default>否</template>
                    </el-table-column>
                    <el-table-column label="注册时间" prop="createTime" min-width="180"></el-table-column>
                </el-table>
                <div class="flex justify-end mt-4">
                    <pagination v-model="pager" @change="getLists" />
                </div>
            </div>
        </view>
    </Popup>
</template>

<script setup lang="ts">
import { usePaging } from '@/hooks/usePaging'
import { getUserList } from '@/api/consumer'

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => {
            ;[]
        }
    }
})
const emit = defineEmits(['update:modelValue'])

const value = computed({
    get() {
        return props.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

//弹框ref
const popRef = shallowRef()
//表格ref
const tableRef = shallowRef()
//搜索参数
const queryParams = ref({
    keyword: '',
    isDistribution: 0
})
//选择表格选项
const selectTable = async () => {
    await tableRef.value.clearSelection()
    pager.lists.forEach((item: any) => {
        value.value.forEach((item1: any) => {
            if (item.id == item1.id) {
                tableRef.value.toggleRowSelection(item, true)
            }
        })
    })
}

//打开弹框
const open = async () => {
    await popRef.value.open()
    await selectTable()
    getLists()
}
//获取row唯一值
const getRowKey = (row: any) => {
    return row.id
}

//确定
const comfrim = () => {
    value.value = tableRef.value.getSelectionRows()
    popRef.value.close()
}

//分页组件
const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: getUserList,
    params: queryParams.value
    // size: 5
})

defineExpose({ open })
</script>

<style scoped lang="scss"></style>
