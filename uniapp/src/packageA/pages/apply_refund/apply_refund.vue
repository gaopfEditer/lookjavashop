<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="apply-refund p-[20rpx] pb-[100px]" :class="{ 'pb-[480px]': applyRefundInfo.focus_refundRemark }">
        <!-- 商品信息 -->
        <view class="rounded-[10rpx] overflow-hidden bg-white">
            <goods-card shape="rectangle" class="flex-1" :image="applyRefundInfo?.goodsImage"
                :imageStyle="{ width: '180rpx', height: '180rpx' }" :name="applyRefundInfo?.goodsName"
                :containStyle="{ height: '220rpx', 'border-radius': '0' }">
                <view class="text-muted mt-[30rpx]">
                    {{ applyRefundInfo.goodsSkuValue }}
                </view>
            </goods-card>
        </view>

        <!-- 选择退款类型 -->
        <view v-show="formData.refundType == 0" class="bg-white rounded-[10rpx] mt-[24rpx]">
            <!-- 仅退款 -->
            <button class="flex justify-between items-center px-[30rpx] py-[20rpx]" @click.stop="formData.refundType = 1">
                <view class="text-left">
                    <view class="font-medium text-lg h-[60rpx] leading-[60rpx]">仅退款</view>
                    <view class="text-xs text-muted">
                        未收到货，与卖家协商同意无需退货只需退款
                    </view>
                </view>
                <u-icon name="arrow-right" size="26"></u-icon>
            </button>
            <!-- 退货退款 -->
            <button class="flex justify-between items-center p-[30rpx] py-[20rpx]" @click.stop="formData.refundType = 2">
                <view class="text-left">
                    <view class="font-medium text-lg h-[60rpx] leading-[60rpx]">退货退款</view>
                    <view class="text-xs text-muted"> 已收到货，需退还收到的实物 </view>
                </view>
                <u-icon name="arrow-right" size="26"></u-icon>
            </button>
        </view>

        <view v-show="formData.refundType != 0">
            <!-- 金额信息 -->
            <view class="rounded-[10rpx] overflow-hidden bg-white mt-[20rpx]">
                <!-- 实付金额 -->
                <view class="flex justify-between p-[30rpx] text-main border_b">
                    <view>实付金额</view>
                    <view>¥{{ applyRefundInfo.payMoney }}</view>
                </view>

                <!-- 申请金额 -->
                <view class="p-[30rpx]">
                    <view>
                        <text>申请金额</text>
                        <text class="text-muted">
                            (不可退金额：运费¥{{ applyRefundInfo.expressMoney }})
                        </text>
                    </view>
                    <view class="flex items-center flex-1 text-base">
                        <view class="text-primary">¥</view>
                        <input v-model="formData.refundMoney" class="flex-1 p-[8rpx] text-primary text-xl"
                            @input="handleRefundMoney" @blur="applyRefundInfo.show_change_price = 0"
                            @focus="handleScrollTop(0, 'show_change_price')" />
                        <view v-if="!applyRefundInfo.is_show_change_price">
                            <u-icon name="edit-pen" size="28rpx"></u-icon>
                            <text class="text-content ml-[10rpx]">修改金额</text>
                        </view>
                    </view>
                </view>
            </view>

            <!-- 售后信息 -->
            <view class="rounded-[10rpx] overflow-hidden bg-white mt-[20rpx]">
                <!-- 退款方式 -->
                <view class="flex justify-between p-[30rpx] text-main border_b" @click.stop="changeRefundType">
                    <view>退款方式</view>
                    <view class="flex items-center">
                        <text>
                            {{ formData.refundType === 1 ? '仅退款' : '退货退款' }}
                        </text>
                        <u-icon name="arrow-right" size="24rpx"></u-icon>
                    </view>
                </view>

                <!-- 数量 -->
                <view class="flex justify-between p-[30rpx] text-main border_b">
                    <view>数量</view>
                    <view>{{ applyRefundInfo.goodsNum }}</view>
                </view>

                <!-- 退款原因 -->
                <view class="flex p-[30rpx] text-main border_b" @click="refundReasonChoice">
                    <view class="mr-[30rpx]">退款原因</view>
                    <view class="flex-1">
                        <text v-if="formData.refundReason != -1" class="text-main">
                            {{ formData.refundReason }}
                        </text>
                        <text v-else class="text-muted">请选择</text>
                    </view>
                    <u-icon name="arrow-down"></u-icon>
                </view>

                <!-- 退款说明 -->
                <view class="flex p-[20rpx] text-main">
                    <view class="mr-[30rpx] pt-[10rpx]">退款说明</view>
                    <textarea class="bg-page flex-1 rounded-[10rpx] h-[172rpx] px-[20rpx] py-[10rpx]"
                        placeholder="请描述申请售后的具体原因" v-model="formData.refundRemark" name="textarea" maxlength="100"
                        @blur="applyRefundInfo.focus_refundRemark = 0"
                        @focus="handleScrollTop(400, 'focus_refundRemark')"></textarea>
                </view>
            </view>

            <!-- 退款凭证 -->
            <view class="rounded-[10rpx] overflow-hidden px-[30rpx] bg-white mt-[20rpx]">
                <!-- 退款凭证 -->
                <view class="flex pt-[30rpx] pb-[20rpx]">
                    <view class="text-main">退款凭证</view>
                    <text class="text-muted">（选填，最多上传3张）</text>
                </view>

                <!-- 上传图片 -->
                <view class="pb-[30rpx]">
                    <uploader v-model="formData.refundImage" :maxUpload="3" :deletable="true" image-fit="aspectFill" />
                </view>
            </view>
        </view>

        <!-- 提交按钮 -->
        <view class="mt-[46rpx]" v-show="formData.refundType != 0">
            <button class="text-xl apply-btn full-primary" @click="onSubmit" :loading="loading">
                申请退款
            </button>
        </view>

        <!-- Component -->
        <page-status ref="pageRef"></page-status>

        <!-- 退款原因 -->
        <refund-reason ref="refundReasonRef" v-model="formData.refundReason" :reason_data="refundReason"
            :refund_type="formData.refundType"></refund-reason>
    </view>
</template>

<script lang="ts" setup>
import type { ApplyRefundType } from '@/api/order'
import { afterSaleGoodsDetail, afterSalesAdd } from '@/api/order'
import { reactive, shallowRef, nextTick, unref, computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import EmptyOrderImg from '../../static/empty/order.png'
import RefundReason from './component/refund-reason.vue'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const pageRef = shallowRef()
const refundReasonRef = shallowRef()
const loading = ref(false)

// 展示数据
const applyRefundInfo = reactive<any>({
    orderGoodsId: '', // 订单ID
    goodsId: '', // 商品ID
    goodsImage: '', // 商品图片
    goodsPrice: '', // 商品金额
    goodsSkuValue: [], // 商品规格
    goodsName: '', // 商品名称
    express_money: '', // 运费金额
    can_refundMoney: '', // 最大可退款金额
    payMoney: '', // 实际付款金额
    goodsNum: '', // 视频数量
    status_text: '', // 状态描述文字
    onlyRefundReason: [], // 仅退款的原因
    refundAndReturnReason: [], // 退货退款的原因
    show_change_price: 0, // 是否显示修改金额文案
    focus_refundRemark: 0 // 是否聚焦了退款说明
})
// 需要提交的表单数据
const formData = reactive<ApplyRefundType>({
    orderGoodsId: 0,
    refundRemark: '',
    refundImage: [],
    refundMoney: 0,
    refundReason: '',
    refundType: 0
})

// 获取退款/退货退款原因
const refundReason = computed(() => {
    if (formData.refundType === 1) {
        return applyRefundInfo.onlyRefundReason
    } else {
        return applyRefundInfo.refundAndReturnReason
    }
})

// 处理申请金额输入限制哦
const handleRefundMoney = async (e: any) => {
    let val = e.target.value.replace(/(^\s*)|(\s*$)/g, '')
    if (!val) {
        return (formData.refundMoney = 0)
    }
    // 只能是数字和小数点，不能是其他输入
    const reg = /[^\d.]/g
    val = val.replace(reg, '')
    // 保证第一位只能是数字，不能是点
    val = val.replace(/^\./g, '')
    // 小数只能出现1位
    val = val.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.')
    // 小数点后面保留2位
    val = val.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
    await nextTick()
    formData.refundMoney = val
}

// 切换退款方式
const changeRefundType = () => {
    formData.refundType = 0
    formData.refundReason = ''
}

// 选择退款原因
const refundReasonChoice = () => {
    refundReasonRef.value?.handleOpen()
}

const handleScrollTop = async (top: number, item: string) => {
    applyRefundInfo[item] = 1
    await nextTick()
    uni.pageScrollTo({
        duration: 450,
        scrollTop: top
    })
}

const initApplyRefundData = async () => {
    try {
        const data = await afterSaleGoodsDetail({
            orderGoodsId: applyRefundInfo.orderGoodsId
        })
        Reflect.ownKeys(data).map((item: any) => {
            applyRefundInfo[item] = data[item]
        })
        formData.refundMoney = applyRefundInfo.refundMoney
        formData.orderGoodsId = applyRefundInfo.orderGoodsId
        unref(pageRef)?.close()
    } catch (error) {
        console.log(error)
        unref(pageRef)?.show({
            text: error,
            src: EmptyOrderImg
        })
    }
}

const onSubmit = async () => {
	if (loading.value == true) {
		return
	}
	if (!formData.refundReason || !formData.refundMoney) {
		return uni.$u.toast('退款理由或退款金额不能为空')
	}
	
	loading.value = true
    try {
        const { id }: any = await afterSalesAdd(formData)
		loading.value = false
        router.redirectTo(`/packageA/pages/after_sales_detail/after_sales_detail?id=${id}`)
    } catch (error) {
		loading.value = false
        console.log('售后申请失败', error)
    }
}

onLoad(async ({ id }) => {
    await nextTick()
    try {
        if (!id) throw new Error('参数错误')
        applyRefundInfo.orderGoodsId = id
        await initApplyRefundData()
    } catch (error) {
        console.log(error)
        unref(pageRef).show({
            text: error,
            src: EmptyOrderImg
        })
    }
})
</script>

<style lang="scss" scoped>
.border_b {
    border-bottom: 1px solid $u-bg-color;
}

.apply-btn {
    background-color: var(--theme-color) !important;
    border-radius: 60px;
    height: 84rpx;
    line-height: 84rpx;
}

// 禁用时样式
button[disabled] {
    color: #ffffff;
    background-color: rgba($color: $u-type-primary, $alpha: 0.6);
}
</style>
