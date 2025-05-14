<template>
    <view class="p-[24rpx] flex items-center justify-between" @click.stop="showCoupon = true">
        <text class="text-[#333333] text-base">优惠券</text>
        <view class="flex items-center">
            <text class="text-base text-primary mr-[14rpx]">
                <text v-if="couponAmount">-¥{{ couponAmount }}</text>
                <text v-else>{{ coupon.ableUseCount }}张可用</text>
            </text>
            <u-icon name="arrow-right" size="24rpx"></u-icon>
        </view>
    </view>

    <u-popup
        v-model="showCoupon"
        mode="bottom"
        :duration="200"
        border-radius="14"
        :closeable="false"
        :safeAreaInsetBottom="true"
    >
        <view class="coupons">
            <view class="coupons-header p-[30rpx] flex justify-between">
                <text class="text-[#101010] text-lg">优惠券</text>
                <u-icon name="close" color="#666666" @tap="showCoupon = false" />
            </view>
            <view class="coupons-main">
                <!--  Tabs选项卡  -->
                <u-tabs
                    v-if="showCoupon"
                    bar-width="80"
                    :list="tabsList.list"
                    :current="tabsList.current"
                    :active-color="$theme.primaryColor"
                    @change="(v) => (tabsList.current = v)"
                ></u-tabs>

                <!--  优惠券内容  -->
                <scroll-view
                    :scroll-y="true"
                    class="mb-[24rpx]"
                    style="height: 700rpx; touch-action: none"
                >
                    <!--  可以使用的优惠券  -->
                    <view v-show="tabsList.current === 0">
                        <coupon-card
                            v-for="item in coupon.ableUse"
                            :key="item.id"
                            :data="item"
                            @click="onSelectCoupon(item.couponListId)"
                        >
                            <template #other>
                                <view class="w-[120rpx] flex justify-end items-end pb-[14rpx]">
                                    <l-checkbox
                                        :checked="item.couponListId == orderParam.couponListId"
                                        label=""
                                        :true-label="1"
                                        :false-label="0"
                                        :active-color="$theme.primaryColor"
                                    />
                                </view>
                            </template>
                        </coupon-card>
                        <u-empty
                            v-if="!coupon.ableUse.length"
                            text="暂无优惠券"
                            mode="coupon"
                            :icon-size="180"
                            margin-top="200"
                        />
                    </view>
                    <!--  不可用的优惠券  -->
                    <view v-show="tabsList.current === 1">
                        <coupon-card
                            v-for="item in coupon.unableUse"
                            :key="item.id"
                            :data="item"
                            :mainStyle="{
                                color: '#333333',
                                background: '#E6E6E8'
                            }"
                        >
                            <template #useScene>
                                <text class="unableUse">未满足使用条件</text>
                            </template>
                        </coupon-card>
                        <u-empty
                            v-if="!coupon.unableUse.length"
                            text="暂无优惠券"
                            mode="coupon"
                            :icon-size="180"
                            margin-top="200"
                        />
                    </view>
                </scroll-view>

                <!--  操作按钮  -->
                <button class="full-primary mx-[30rpx] h-[82rpx] rounded-[60px] leading-[82rpx]"
                    @click.stop="onConfirmCoupon">
                    确认
                </button>
            </view>
        </view>
    </u-popup>
</template>

<script lang="ts" setup>
import { orderCoupon } from '@/api/marketing/coupon'
import { computed, onMounted, reactive, ref } from 'vue'
import CouponCard from '@/components/coupon-card/coupon-card.vue'

const emit = defineEmits(['update:orderParam', 'refresh'])

const props = withDefaults(
    defineProps<{
        orderParam?: any
        couponAmount: number
    }>(),
    {
        orderParam: {},
        couponAmount: 0
    }
)

const tabsList = reactive({
    current: 0,
    list: [
        {
            name: '可使用优惠券(0)'
        },
        {
            name: '不可用优惠券(0)'
        }
    ]
})

const coupon = reactive<any>({
    ableUseCount: 0,
    unableUseCount: 0,
    ableUse: [],
    unableUse: []
})
const showCoupon = ref<boolean>(false)

const formData = computed({
    get: () => {
        return props.orderParam
    },
    set: (value) => {
        emit('update:orderParam', value)
    }
})

const onSelectCoupon = (id: number) => {
    if (formData.value.couponListId == id) {
        formData.value.couponListId = ''
    } else {
        formData.value.couponListId = id
    }
}

const onConfirmCoupon = () => {
    emit('refresh')
    showCoupon.value = false
}

const getCouponList = async () => {
    try {
        const data: any = await orderCoupon({
            ...formData.value
        })
        Reflect.ownKeys(data).map((item: any) => {
            coupon[item] = data[item]
        })
        tabsList.list[0].name = `可使用优惠券(${data.ableUseCount || 0})`
        tabsList.list[1].name = `不可用优惠券(${data.unableUseCount || 0})`
    } catch (error) {
        console.log('请求商品优惠券失败', error)
    }
}

onMounted(() => {
    getCouponList()
})
</script>

<style lang="scss" scoped>
.coupons {
    &-main {
        background: $u-bg-color;
        padding-bottom: 10rpx;
    }

    .unableUse {
        margin-left: 16rpx;
        font-size: 22rpx;
        border-radius: 60px;
        padding: 6rpx 24rpx;
        color: #f79c0c;
        background-color: #fff3e0;
    }
}
</style>
