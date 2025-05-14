<template>
    <popup
        ref="popupRef"
        title="发放优惠券"
        width="800px"
        :async="true"
        confirm-button-text="发放"
        @close="handleClose"
        @confirm="handleConfirm"
    >
        <!-- 表单-->
        <el-form :model="queryParams" :inline="true">
            <el-form-item>
                <el-input
                    class="w-[280px]"
                    v-model="queryParams.keyword"
                    placeholder="请输入优惠券名称"
                    clearable
                    @keyup.enter="resetPage"
                >
                    <template #append>
                        <el-button @click="resetPage" :icon="Search" />
                    </template>
                </el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="resetPage">查询</el-button>
                <el-button @click="resetParams">重置</el-button>
            </el-form-item>
        </el-form>

        <el-table
            size="large"
            height="420px"
            v-loading="pager.loading"
            :data="pager.lists"
            @selection-change="selectDataChange"
        >
            <el-table-column type="selection" width="55" />
            <el-table-column prop="name" label="优惠券名称" width="180" />
            <el-table-column prop="discountContent" label="优惠券内容" min-width="180" />
            <el-table-column prop="getTypeMsg" label="发放方式" min-width="180" />
        </el-table>

        <div class="flex justify-end mt-4">
            <pagination v-model="pager" @change="getLists" />
        </div>
    </popup>
</template>
<script lang="ts" setup>
import Popup from '@/components/popup/index.vue'
import { Search } from '@element-plus/icons-vue'
import { couponLists, couponSend } from '@/api/marketing/coupon'
import type { CouponListQueryParamsType, CouponSendFormType } from '@/api/marketing/coupon.d'
import { usePaging } from '@/hooks/usePaging'
import feedback from '@/utils/feedback'
const emit = defineEmits<{
    (event: 'update:show', value: boolean): void
    (event: 'confirm'): void
}>()
const props = defineProps({
    show: {
        type: Boolean,
        required: true
    },
    value: {
        type: Number,
        required: true
    }
})

const formData = reactive<CouponSendFormType>({
    userIds: [],
    number: 1,
    couponIds: []
})
const popupRef = shallowRef<InstanceType<typeof Popup>>()
const queryParams = reactive<CouponListQueryParamsType>({
    keyword: '',
    getType: 2,
    useGoodsType: '',
    type: 2
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: couponLists,
    params: queryParams
})

const selectDataChange = (value: any[]) => {
    formData.couponIds = value.map((item: any) => item.id)
}

const handleClose = () => {
    emit('update:show', false)
}

const handleConfirm = async () => {
    if (!formData.couponIds.length) {
        feedback.msgError('请选择优惠券')
        return
    }
    try {
        await couponSend(formData)
        feedback.msgSuccess('发放成功')
        emit('confirm')
        await handleClose()
    } catch (error) {
        console.log('发放优惠券报错', error)
    }
}

watch(
    () => props.show,
    (val) => {
        if (val) {
            popupRef.value?.open()
            formData.userIds = [props.value]
            getLists()
        } else {
            popupRef.value?.close()
        }
    }
)
</script>
