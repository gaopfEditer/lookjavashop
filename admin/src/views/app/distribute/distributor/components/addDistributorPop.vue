<template>
    <Popup ref="popRef" title="开通分销商" width="700px" @close="closePop" async @confirm="confirm">
        <el-form ref="formRef">
            <el-form-item label="用户信息" class="is-required">
                <div class="flex-none w-full">
                    <el-button type="primary" @click="openPickerUser">选择用户</el-button>
                    <el-table class="mt-4" v-if="isPickUpUser.length != 0" :data="isPickUpUser">
                        <el-table-column label="用户编码" prop="sn"></el-table-column>
                        <el-table-column label="用户昵称" prop="nickname" min-width="120">
                            <template #default="{ row }">
                                <div class="flex items-center">
                                    <el-image class="w-[47px] h-47px flex-none" :src="row.avatar"></el-image>
                                    <div class="ml-2">{{ row.nickname }}</div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="分销商">
                            <template #default="{ row }">
                                <div>{{ row.is_distribution == 0 ? '否' : '是' }}</div>
                            </template>
                        </el-table-column>
                        <el-table-column label="手机号码" prop="mobile"></el-table-column>
                        <el-table-column label="操作">
                            <template #default="{ row }">
                                <el-button type="primary" @click="delUser(row)" link>移除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-form-item>
            <el-form-item label="分销等级" class="is-required">
                <el-select v-model="level">
                    <el-option v-for="(item, index) in dropDownList" :key="index" :label="item.name"
                        :value="item.id"></el-option>
                </el-select>
            </el-form-item>
        </el-form>
        <UserPicker ref="userPickerPop" v-model="isPickUpUser"></UserPicker>
    </Popup>
</template>

<script setup lang="ts">
import { getOtherLists, addDistributor } from '@/api/distribution/distributor'
import feedback from '@/utils/feedback'
const emit = defineEmits(['close', 'confirm'])
const formRef = shallowRef()
//弹框ref
const popRef = shallowRef()
const userPickerPop = shallowRef()
//已选择用户
const isPickUpUser = ref([])
//分销等级
const level = ref()

//下拉列表
const dropDownList: any = ref([])

//打开弹框
const open = () => {
    popRef.value.open()
    getDropDownList()
}

//打开选择用户弹框
const openPickerUser = () => {
    userPickerPop.value.open()
}

//关闭窗口
const closePop = () => {
    emit('close')
}

//确定
const confirm = async () => {
    if (!level.value) return feedback.msgError('请选择分销等级')
    if (!isPickUpUser.value.length) return feedback.msgError('请选择分销用户')
    const ids = isPickUpUser.value.map((item: any) => item.id)
    await addDistributor({ ids, levelId: level.value })
    emit('confirm')
    popRef.value.close()
}

//移除用户
const delUser = (row: any) => {
    const res = isPickUpUser.value.findIndex((item: any) => item.id == row.id)
    isPickUpUser.value.splice(res, 1)
}

//获取下拉列表
const getDropDownList = async () => {
    const { lists } = await getOtherLists()
    dropDownList.value = lists
}

defineExpose({ open })
</script>

<style scoped lang="scss"></style>
