<template>
    <view class="card" @click.stop="goPage" :style="$theme.variable">
        <!-- Header -->
        <view class="flex justify-between text-sm card--header">
            <view class="text-main flex items-center">
                <text
                    v-if="orderInfo.orderType != GoodsTypeEnum.ORDINARY"
                    class="order-tag text-xs text-white"
                >
                    {{ orderTypeMsg(orderInfo.orderType) }}
                </text>
                <text>订单编号：{{ orderInfo.orderSn }}</text>
            </view>
            <view class="text-primary">{{ orderInfo.orderStatusMsg }}</view>
        </view>

        <!-- Main -->
        <view class="card--main">
            <goods-card
                shape="rectangle"
                class="flex-1"
                :image="item.goodsImage"
                :imageStyle="{ width: '180rpx', height: '180rpx' }"
                :name="item.goodsName"
                :containStyle="{ height: '220rpx', 'border-radius': '0' }"
                v-for="item in orderInfo.orderGoodsList"
                :key="item.id"
            >
                <view class="text-muted mt-[6rpx]">{{ item.goodsSkuValue }}</view>
                <view class="flex justify-between mt-[8rpx]">
                    <price
                        :content="item.goodsPrice"
                        main-size="32rpx"
                        minor-size="28rpx"
                        fontWeight="500"
                    />
                    <text class="text-main">x {{ item.goodsNum }}</text>
                </view>
            </goods-card>
            <view class="text-right px-[24rpx] py-[18rpx]">
                <text> 共{{ orderInfo?.goodsNum }}件商品，应付款： </text>
                <price
                    :content="orderInfo.needPayMoney"
                    main-size="30rpx"
                    minor-size="30rpx"
                    fontWeight="500"
                    color="#333333"
                />
            </view>
        </view>

        <!-- Footer Slot -->
        <view class="flex">
            <slot></slot>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { GoodsTypeEnum } from '@/enums/goodsEnums'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const props = withDefaults(
    defineProps<{
        orderInfo?: any
    }>(),
    {
        orderInfo: {}
    }
)

const orderTypeMsg = computed(() => {
    return (orderType: number) => {
        switch (orderType) {
            case GoodsTypeEnum.SECKILL:
                return '秒杀'
            case GoodsTypeEnum.TEAM:
                return '拼团'
        }
    }
})

const goPage = () => {
    router.navigateTo(`/packageA/pages/order_detail/order_detail?id=${props.orderInfo.id}`)
}
</script>

<style lang="scss" scoped>
.card {
    border-radius: 14rpx;
    background-color: #ffffff;
    margin: 20rpx 20rpx 0 20rpx;

    &--header {
        padding: 24rpx 30rpx;
        //border-bottom: 1px solid $u-border-color;
        .order-tag {
            border-radius: 6rpx;
            padding: 3rpx 10rpx;
            margin-right: 10rpx;
            background-color: $u-type-primary;
        }
    }

    &--main {
        color: #555555;
    }

    &--footer {
        // padding: 20rpx 30rpx;
        font-size: 26rpx;
        border-bottom: 1px solid $u-border-color;
    }
}
</style>
