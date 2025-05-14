<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            title="核销记录"
            :async="true"
            width="60%"
            :confirmButtonText="'确定'"
            @confirm="handleClose"
        >
            <el-form
                ref="formRef"
                :model="formData"
                label-width="125px"
                :rules="formRules"
            >
                <el-card class="!border-none mt-4" shadow="never">
                    <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                        提货人信息
                    </div>
                    <div class="pb-4 mb-4 text-lg font-medium" >
                            <el-row>
                                <el-col :span="12">
                                    <el-form-item label="姓名">
                                        {{order.addressContact}}
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="联系号码">
                                        {{order.addressMobile}}
                                    </el-form-item>
                                </el-col>
                            </el-row>
                    </div>
                </el-card>
                <el-card class="!border-none mt-4" shadow="never">
                    <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                        核销信息
                    </div>
                    <div class="pb-4 mb-4 text-lg font-medium" >
                            <el-row>
                                <el-col :span="12">
                                    <el-form-item label="自提门店">
                                        {{order.selffetchShopName}} 
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="门店电话">
                                        {{order.selffetchShopMobile}}
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row>
                                <el-col :span="12">
                                    <el-form-item label="核销员">
                                        {{order.verificationByStr}} 
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="核销时间">
                                        {{order.verificationTimeStr}} 
                                    </el-form-item>
                                </el-col>
                            </el-row>
                    </div>
                </el-card>
                <el-card class="!border-none" shadow="never">
                    <div class="pb-4 mb-4 text-lg font-medium" style="border-bottom: 1px solid #eeeeee">
                        商品信息
                    </div>

                    <div>
                        <!-- 商品表格 -->
                        <goods-table-log :goods="verifiedGoods" :showPrice="true" @change="changeGoods"></goods-table-log>
                    </div>
                </el-card>
            </el-form>
        </popup>
    </div>
</template>
<script lang="ts" setup>
import { orderSelffetchVerify } from "@/api/selffetchorder/selffetchorder";
import Popup from "@/components/popup/index.vue";
import type { FormInstance, FormRules } from "element-plus";
import { orderDetail } from '@/api/order/selffetch_order'
import GoodsTableLog from './goods-table-log.vue'
import feedback from '@/utils/feedback'

const emit = defineEmits(["success", "close"]);
const formRef = shallowRef<FormInstance>();
const popupRef = shallowRef<InstanceType<typeof Popup>>();

type RemarkType = {
    id: string | number;
    orderGoodsList: Array;
}

const formData = reactive<RemarkType>({
    id: '', 
    orderGoodsList: []
});

const formRules = reactive<FormRules>({
});
const order = reactive<any>({
    id: '',
    createTime: '',
    orderSn: '',
    status: 4,
    payIs: 1,
    payWayMsg: '',
    payTime: '',
    addressContact: '',
    addressMobile: '',
    addressContent: '',
    deliveryTypeMsg: '',
    expressMoney: '',
    expressIsMsg: '',
    expressTime: '',
    expressNo: '',
    expressCompanyName: '',
    expressConfirmTime: '',
    userRemark: '',
    shopRemark: '',
    orderSourceMsg: '',
    pay_way_text: '',
    orderGoodsList: [],
    orderLogList: [],
    cancelBtn: 0,
    orderStatusMsg: '已取消',
    deliverBtn: 0,
    logisticsBtn: 0,
    confirmBtn: 0,
    confirmTime: ''
})
const verfyGoods = ref([])

const getDetails = async () => {
    const data = await orderDetail({
        //@ts-ignore
        id: formData.id
    })
    Reflect.ownKeys(data).map((item: any) => {
        order[item] = data[item]
    })
}

const verifiedGoods =  computed(() => {
    let ret = order.orderGoodsList.filter(item => {
        return item.verificationStatus == 1
    })
    return ret;
})


const handleSubmit = async () => {
    if (verfyGoods.value.length == 0) {
        feedback.msgError('请选择至少一个商品核销')
        return
    }
    await formRef.value?.validate();
    let ret = await orderSelffetchVerify({
        id: formData.id as number,
        items: verfyGoods.value
    })
    //console.log(ret, "retretretret")
    //popupRef.value?.close();
    emit("success");
};

const open = (id: number, item : Array) => {
    formData.id = id;
    popupRef.value?.open();
    getDetails()
};

const handleClose = () => {
    emit("close");
};

const changeGoods = async(e) => {
    verfyGoods.value = e 
}

defineExpose({
    open
});
</script>

<style lang="scss" scoped>
.after-status {

}
</style>
