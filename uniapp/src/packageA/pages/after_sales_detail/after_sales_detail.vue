<template>
    <view class="after_sales_detail">
        <!-- Header -->
        <view class="px-[40rpx] h-[160rpx] bg-[#555555] text-white">
            <view class="text-xl font-medium pt-[24rpx]">
                {{ after_sales.afterStatusMsg }}
            </view>
            <view class="text-base pt-[10rpx]">
                {{ after_sales.subStatusMsg }}
            </view>
        </view>
        <view class="px-[40rpx] h-[130rpx] bg-white text-muted">
            <view class="text-xs pt-[24rpx]">
                如果商家拒绝，您可重新发起申请
            </view>
            <view class="text-xs pt-[10rpx]">
                售后成功之后，预计1-3个工作日到账，请注意查收
            </view>
        </view>

        <!-- Main -->
        <view
            class="rounded-[14rpx] mt-[20rpx] mx-[20rpx] overflow-hidden"

        >
            <view
                v-for="(item, index) in after_sales.orderAfterGoods"
                :key="index"
                @click="goGoodsDetail(item.goodsId)"
            >
                <goods-card
                    shape="rectangle"
                    class="flex-1"
                    :image="item.goodsImage"
                    :imageStyle="{ width: '180rpx', height: '180rpx' }"
                    :name="item.goodsName"
                    :containStyle="{ height: '220rpx', 'bafter_sales-radius': '0' }"
                >
                    <view class="text-muted mt-[6rpx]">
                        {{ item.goodsSkuValue }}
                    </view>
                    <view class="flex justify-between mt-[8rpx]">
                        <price
                            :content="item.goodsPrice"
                            main-size="32rpx"
                            minor-size="28rpx"
                            fontWeight="500"
                            color="#333333"
                        />
                        <text class="text-main ">
                            x {{ item.goodsNum }}
                        </text>
                    </view>
                </goods-card>
            </view>
        </view>

        <!-- 退货地址 -->
        <view
            v-if="after_sales.deliveryBtn"
            class="rounded-[14rpx] mt-[20rpx] mx-[20rpx] p-[30rpx] overflow-hidden bg-white"
        >
            <view class="">
                <text class="text-sm font-medium text-[#101010]">退货地址</text>
                <text
                    class="ml-4 text-xs text-muted bg-[#F8F8F8] px-[10rpx] py-[4rpx]"
                    @click="copy(retreatAddress)"
                >
                    复制
                </text>
            </view>
            <view class="mt-[14rpx]">
                <text class="text-sm">收件人</text>
                <text class="ml-4 text-xs">
                    {{ after_sales.retreatConsignee }}，{{ after_sales.retreatMobile }}
                </text>
            </view>
            <view class="mt-[14rpx]">
                <text class="text-sm">地址：</text>
                <text class="ml-4 text-xs">
                    {{ after_sales.retreatRegion }}
                    {{ after_sales.retreatAddress }}
                </text>
            </view>
        </view>

        <!-- 售后成功 -->
        <view
            v-if="after_sales.refundSuccess"
            class="rounded-[14rpx] mt-[20rpx] mx-[20rpx] p-[30rpx] overflow-hidden bg-white"
        >
            <view class="text-error flex justify-between">
                <text class="text-sm">已退款金额</text>
                <text class="ml-4 text-base">¥{{ after_sales.refundMoney }}</text>
            </view>
            <view class="text-content flex justify-between mt-[14rpx]">
                <text class="text-error text-sm">{{ after_sales.refundPayWayMsg }}</text>
                <text class="ml-4 text-xs">¥{{ after_sales.refundMoney }}</text>
            </view>
        </view>

        <!-- 拒绝 -->
        <view
            v-if="after_sales.handleRemark"
            class="rounded-[14rpx] mt-[20rpx] mx-[20rpx] p-[30rpx] overflow-hidden bg-white"
        >
            <text class="text-error text-base">拒绝原因</text>
            <text class="ml-4 text-base">{{ after_sales.handleRemark }}</text>
        </view>

        <!-- 详情信息 -->
        <view class="text-sm leading-8 rounded-[14rpx] mt-[20rpx] mx-[20rpx] py-[20rpx] px-[30rpx] overflow-hidden bg-white">
            <view class="flex">
                <view class="text-content">退款方式</view>
                <view class="ml-[40rpx] flex-1 text-[#101010]">
                    {{ after_sales?.refundTypeMsg || '-' }}
                </view>
            </view>
            <view class="flex">
                <view class="text-content">退款原因</view>
                <view class="ml-[40rpx] flex-1 text-[#101010]">
                    {{ after_sales?.refundReason || '-' }}
                </view>
            </view>
            <view class="flex">
                <view class="text-content">实付金额</view>
                <view class="ml-[40rpx] flex-1 text-[#101010]">
                    ¥{{ after_sales?.payMoney || '-' }}
                </view>
            </view>
            <view class="flex">
                <view class="text-error">退款金额</view>
                <view class="ml-[40rpx] flex-1 text-[#101010]">
                    ¥{{ after_sales?.refundMoney || '-' }}
                </view>
            </view>
            <view class="flex">
                <view class="text-content">退款编号</view>
                <view class="ml-[40rpx] flex-1 text-[#101010]">
                    {{ after_sales?.afterSn || '-' }}
                </view>
            </view>
            <view class="flex">
                <view class="text-content">申请时间</view>
                <view class="ml-[40rpx] flex-1 text-[#101010]">
                    {{ after_sales?.createTime || '-' }}
                </view>
            </view>
        </view>
    </view>

    <!-- Footer -->
    <view class="flex justify-end footer">
        <view class="footer--wrap">
            <after-sales-footer
                class="w-full"
                :after_sales_id="after_sales.id"
                :order_goods_id="after_sales?.orderGoodsId"
                :re_apply="after_sales.reapplyBtn"
                :cancel="after_sales.cancelBtn"
                :delivery="after_sales.deliveryBtn"
                :apply="false"
                @refresh="refresh"
            />
        </view>
    </view>

    <!-- Component -->
    <page-status ref="pageRef"></page-status>
</template>

<script lang="ts" setup>
import { useCopy } from '@/hooks/useCopy'
import { onLoad } from '@dcloudio/uni-app'
import { afterSalesDetail } from '@/api/order'
import EmptyOrderImg from "../../static/empty/order.png"
import AfterSalesFooter from '../../component/after-sales-footer/after-sales-footer.vue'
import {unref, shallowRef, nextTick, reactive, computed} from 'vue'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const { copy } = useCopy()
const pageRef = shallowRef()
const after_sales = reactive<any>({
    afterSn: '',
    afterStatus: 1,
    afterStatusMsg: '',
    afterType: 2,
    cancelBtn: 1,
    confirmTime: 0,
    createTime: '',
    deliveryBtn: 1,
    refundSuccess: 0,
    refundPayWayMsg: 0,
    refundMone: 0,
    expressName: '',
    expressRemark: '',
    expressTime: 0,
    handleRemark: '',
    payMoney: '',
    id: 1,
    invoiceNo: '',
    orderAfterGoods: [],
    orderGoodsId: '',
    orderId: '',
    reapplyBtn: '',
    refundTypeMsg: '',
    refundImage: [],
    refundMoney: 0,
    refundReason: '',
    refundRemark: '',
    refundSn: '',
    refundType: 1,
    retreatAddress: '',
    retreatConsignee: '',
    retreatMobile: '',
    retreatRegion: '',
    subStatus: 11,
    subStatusMsg: '',
    userId: 2
})

const retreatAddress = computed(() => {
    return ` 收货人：${after_sales.retreatConsignee}\n 手机号码：${after_sales.retreatMobile}\n 退货地址:${after_sales.refundReason}${after_sales.retreatAddress}`
})

// 刷新订单
const refresh = () => {
    uni.navigateBack()
}

// 初始化订单详情
const initAfterSalesDetail = async (): Promise<void> => {
    try {
        const data = await afterSalesDetail({ id: after_sales.id })
        Reflect.ownKeys(data).map((item: any) => {
            after_sales[item] = data[item]
        })
        unref(pageRef)?.close()
    } catch (error) {
        unref(pageRef)?.show({
            text: error,
            src: EmptyOrderImg
        })
        console.log('获取订单详情接口', error)
    }
}

// 查看商品详情
const goGoodsDetail = (id: number) => {
    router.navigateTo(`/pages/goods_detail/goods_detail?id=${id}`)
}

onLoad(async ({ id }: any) => {
    await nextTick()
    try {
        if (!id) {
            throw new Error('参数有误')
        }
        after_sales.id = Number(id)
        // 初始化售后详情信息
        await initAfterSalesDetail()
    } catch (error) {
        unref(pageRef).show({
            text: error,
            src: EmptyOrderImg
        })
        console.log('售后详情', error)
    }
})
</script>

<style lang="scss">
.after_sales_detail {
    height: 100%;
}
.footer {
    left: 0;
    bottom: 0;
    width: 100%;
    position: fixed;
    padding: 0 20rpx;
    background-color: #ffffff;
    box-shadow: 2rpx 2rpx 22rpx rgba($color: #000000, $alpha: 0.2);

    &--wrap {
        padding-bottom: env(safe-area-inset-bottom);
    }
}
</style>
 