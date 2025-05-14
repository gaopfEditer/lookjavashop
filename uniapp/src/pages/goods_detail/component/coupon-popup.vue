<template>
    <template v-if="couponList.length">
        <view
            class="coupons-view text-primary rounded-[6rpx] text-sm p-[12rpx] mb-4 flex justify-between"
            @click.stop="showCoupon = true"
        >
            <view>{{ viewPortList }}</view>
            <view class="flex items-center">
                <text class="mr-1">领券</text>
                <u-icon name="arrow-right" size="22rpx" class="text-primary"></u-icon>
            </view>
        </view>

        <u-popup
            v-model="showCoupon"
            mode="bottom"
            :duration="50"
            border-radius="14"
            :closeable="false"
            :safeAreaInsetBottom="true"
        >
            <view class="coupons">
                <view class="coupons-header p-[30rpx] flex justify-between">
                    <text class="text-[#101010] text-lg">领券</text>
                    <u-icon name="close" color="#666666" @tap="showCoupon = false" />
                </view>
                <view class="coupons-main">
                    <scroll-view :scroll-y="true" style="height: 700rpx; touch-action: none">
                        <coupon-card v-for="item in couponList" :key="item.id" :data="item">
                            <template #other>
                                <view class="w-[120rpx] flex items-end">
                                    <!--  已领取  -->
                                    <image
                                        v-if="
                                            (item.isReceive && !item.isAble) ||
                                            received.includes(item.id)
                                        "
                                        class="w-[120rpx] h-[124rpx]"
                                        src="@/static/images/icon/home_img_receive.png"
                                    >
                                    </image>
                                    <!--  领取  -->
                                    <button
                                        v-if="item.isAble && !received.includes(item.id)"
                                        class="bg-primary rounded-[60px] text-white h-[52rpx] leading-[52rpx] mb-[14rpx]"
                                        @click.stop="onReceive(item.id)"
                                    >
                                        领取
                                    </button>
                                </view>
                            </template>
                        </coupon-card>
                    </scroll-view>
                </view>
            </view>
        </u-popup>
    </template>
</template>

<script lang="ts" setup>
import { goodsCoupon, receiveCoupon } from '@/api/marketing/coupon'
import { computed, onMounted, ref } from 'vue'
import CouponCard from '@/components/coupon-card/coupon-card.vue'

const props = withDefaults(
    defineProps<{
        goodsId?: number | string
    }>(),
    {
        goodsId: ''
    }
)

// 已领取（只能点击一次
const received = ref<any[]>([])
const couponList = ref<any[]>([])
const showCoupon = ref<boolean>(false)

const viewPortList = computed(() => {
    const COUNT = 2
    const data = couponList.value
    const len = data.length
    if (len > COUNT) {
        return data
            .slice(0, 2)
            .map((item) => formatValue(item.condition, item.money))
            .join(' | ')
    } else {
        return data.map((item) => formatValue(item.condition, item.money)).join(' | ')
    }
})

const formatValue = (val: string, money: number) => {
    if (val == '无门槛') return val + '减' + money + '元'
    return val
}

const getCouponList = async () => {
    try {
        const data: any[] = await goodsCoupon({
            goodsId: props.goodsId
        })
        couponList.value = data
    } catch (error) {
        console.log('请求商品优惠券失败', error)
    }
}

// 领取优惠券
const onReceive = async (id: number) => {
    try {
        await receiveCoupon({ id })
        received.value.push(id)
        uni.$u.toast('领取成功')
        await getCouponList()
    } catch (error) {
        console.log(error)
    }
}

onMounted(() => {
    getCouponList()
})
</script>

<style lang="scss" scoped>
.coupons-view {
    background: var(--theme-light-color);
}
.coupons {
    &-main {
        background: $u-bg-color;
    }
}
</style>
