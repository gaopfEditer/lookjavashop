<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <!-- Mian -->
    <u-form :model="inputExpressInfo" ref="formRef" :error-type="['message', 'toast']">
        <view class="card">
            <u-form-item prop="region_content">
                <view class="flex items-center" style="width: 100%" @click="selectPupop = true">
                    <view class="label"> 物流公司 </view>
                    <view
                        class="flex-1 text-lg"
                        :class="{
                            'text-[#c0c0c0]': !expressName,
                            'text-main': expressName
                        }"
                    >
                        {{ expressName || '请选择物流公司' }}
                    </view>
                    <u-icon name="arrow-right" size="22" color="#888888"></u-icon>
                </view>
            </u-form-item>
            <u-form-item prop="contact">
                <view class="inline-block label"> 快递单号 </view>
                <u-input v-model="inputExpressInfo.invoiceNo" class="w-[540rpx]" placeholder="请输入快递单号" />
            </u-form-item>
            <u-form-item prop="info">
                <view class="flex w-full col-start">
                    <view class="label"> 备注说明 </view>
                    <view class="flex-1 pt-[6rpx]">
                        <u-input v-model="inputExpressInfo.expressRemark" type="textarea" placeholder="选填" height="124" />
                    </view>
                </view>
            </u-form-item>
        </view>
        <!-- 联系方式 -->
        <view class="card">
            <u-form-item prop="mobile">
                <view class="inline-block label"> 联系方式 </view>
                <u-input v-model="inputExpressInfo.expressContact" class="w-[540rpx]" placeholder="请输入联系方式" />
            </u-form-item>
        </view>
    </u-form>

    <!-- Footer -->
    <view class="flex flex-1 m-[20rpx] pt-[30rpx]">
        <button class="text-white btn bg-primary" @click="onSubmit">提交</button>
    </view>

    <!-- 选择组件 -->
    <u-select
        v-model="selectPupop"
        value-name="id"
        label-name="name"
        :list="expressLists"
        :confirm-color="$theme.primaryColor"
        @confirm="confirmExpress"
    ></u-select>
</template>

<script lang="ts" setup>
import { onLoad, onReady } from '@dcloudio/uni-app'
import { ref, reactive } from 'vue'
import { afterSalesDelivery, afterSaleGoodsDetail } from '@/api/order'
import type { SalesDeliveryFormType } from '@/api/order'

const expressLists = ref<any>([])
const expressName = ref<string>('')
const inputExpressInfo = reactive<SalesDeliveryFormType>({
    afterId: '',
    expressId: '',
    invoiceNo: '',
    expressRemark: '',
    expressContact: ''
})
const formRef = ref()
const selectPupop = ref<boolean | null>(false)
const rules = reactive<object>({
    afterId: [{ required: true, message: '请选择快递公司', trigger: ['change', 'blur'] }],
    invoiceNo: [{ required: true, message: '请填写快递单号', trigger: ['change', 'blur'] }],
    expressContact: [
        { required: true, message: '请输入手机号码', trigger: ['change', 'blur'] },
        {
            pattern: /^1[3-9]\d{9}$/,
            transform(value: any) {
                return String(value)
            },
            message: '请输入正确的手机号'
        }
    ]
})

const getOrderGoodsInfo = async (id: number) => {
    try {
        const { express } = await afterSaleGoodsDetail({ orderGoodsId: id })
        expressLists.value = express
    } catch (error) {
        console.log('获取订单商品失败', error)
    }
}

const onSubmit = () => {
    formRef.value.validate(async (valid: boolean) => {
        if (!valid) return false
        await afterSalesDelivery({ ...inputExpressInfo })
        setTimeout(() => {
            uni.navigateBack()
        }, 300)
    })
}

const confirmExpress = (event: any) => {
    const val = event[0]
    expressName.value = val.label
    inputExpressInfo.expressId = val.value
}

onLoad(({ id, orderGoodsId }: any) => {
    inputExpressInfo.afterId = id
    getOrderGoodsInfo(orderGoodsId)
})

onReady(() => {
    formRef.value?.setRules(rules)
})
</script>
<style lang="scss">
.card {
    margin: 20rpx;
    margin-bottom: 0;
    padding: 0 24rpx;
    border-radius: 14rpx;
    background-color: #ffffff;

    ::v-deep .u-border-bottom:after {
        border-bottom-width: 0;
    }

    .label {
        width: 130rpx;
        color: #222222;
        font-size: 28rpx;
        margin-right: 30rpx;
        line-height: 70rpx;
    }
}

.btn {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    font-size: 30rpx;
    border-radius: 60rpx;
}
</style>
