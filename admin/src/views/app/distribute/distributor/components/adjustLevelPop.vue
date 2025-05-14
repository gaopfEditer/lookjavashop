<template>
    <Popup ref="popRef" title="分销调整等级" width="400px" @confirm="confirm" @close="emit('close')" async>
        <div>
            <el-form label-width="140px">
                <el-form-item label="用户信息">
                    <div>
                        {{ data?.nickname }}
                    </div>
                </el-form-item>
                <el-form-item label="当前分销等级">
                    <div>
                        {{ data.levelName || '-' }}
                    </div>
                </el-form-item>
                <el-form-item label="调整后分销等级" class="is-required">
                    <div>
                        <el-select v-model="adjustVal">
                            <el-option v-for="(item, index) in dropDownList" :key="index" :label="item.name"
                                :value="item.id"></el-option>
                        </el-select>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </Popup>
</template>

<script setup lang="ts">
import { getOtherLists, levelAdjust } from '@/api/distribution/distributor'
import feedback from '@/utils/feedback'

const emit = defineEmits(['confirm', 'close'])

//弹框ref
const popRef = shallowRef()
//用户数据
const data: any = ref({})
//调整后等级
const adjustVal = ref()

//下拉列表
const dropDownList: any = ref([])

//获取下拉列表
const getDropDownList = async () => {
    const { lists } = await getOtherLists()
    dropDownList.value = lists
}

//确定
const confirm = async () => {
    if (!adjustVal.value) return feedback.msgError('请选择调整后的分销等级')
    await levelAdjust({ userId: data.value.id, levelId: adjustVal.value })
    emit('confirm')
    popRef.value.close()
}

const open = async (row: any) => {
    data.value = row
    console.log(row)
    popRef.value.open()
    await getDropDownList()
}

defineExpose({ open })
</script>

<style scoped lang="scss"></style>
