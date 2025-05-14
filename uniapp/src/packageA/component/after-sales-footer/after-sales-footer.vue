<template>
    <view class="flex m-[20rpx] justify-end items-center" @click.stop>

        <view class="flex">    
            
            <!-- 申请售后 -->
            <template v-if="apply">
                <button
                    :disabled="apply == 2"
                    class="btn plain-primary"
                    @click="goPage(`/packageA/pages/apply_refund/apply_refund?id=${order_goods_id}`)"
                >
                    申请售后
                </button>
            </template>

            <!-- 撤销申请 -->
            <template v-if="cancel">
                <button
                    class="btn plain-gray"
                    @click="handleOrderCancel(after_sales_id)"
                >
                    撤销申请
                </button>
            </template>

            <!-- 重新申请 -->
            <template v-if="re_apply">
                <button
                    class="btn plain-primary"
                    @click="goPage(`/packageA/pages/apply_refund/apply_refund?id=${order_goods_id}`)"
                >
                    重新申请
                </button>
            </template>

            <!-- 填写单号 -->
            <template v-if="delivery">
                <button
                    class="btn plain-primary"
                    @click="goPage(`/packageA/pages/after_sales_delivery/after_sales_delivery?id=${after_sales_id}&orderGoodsId=${order_goods_id}`)"
                >
                    填写单号
                </button>
            </template>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { afterSalesCancel } from '@/api/order'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const emit = defineEmits(['refresh'])

const props = withDefaults(
    defineProps<{
        after_sales_id?: number   // 售后id
        order_goods_id?: number   // 订单商品id
        cancel?: number           // 取消申请售后
        re_apply?: number         // 重新申请售后
        apply: boolean            // 申请售后
        delivery: number          // 填写快递单号
    }>(),
    {
        after_sales_id: 0,
        order_goods_id: 0,
        cancel: 0,
        re_apply: 0,
        apply: true,
        delivery: 0
    }
)

// 页面跳转
const goPage = (url: string) => {
    router.navigateTo(url)
}

// 撤销申请
const handleOrderCancel = async (id?: number): Promise<void> => {
    const modelRes: any = await uni.showModal({
        title: '温馨提示',
        content: '确认撤销申请售后吗？'
    })
    if (modelRes.cancel) return
    await afterSalesCancel({ id: id })
    emit('refresh')
}
</script>

<style lang="scss" scoped>
.btn {
    font-size: 28rpx;
    height: 62rpx;
    padding: 0 30rpx;
    line-height: 62rpx;
    border-radius: 60px;
    margin-left: 20rpx;
}
.btn[disabled] {
    border-radius: 60px;
    border: none;
}
</style>
 