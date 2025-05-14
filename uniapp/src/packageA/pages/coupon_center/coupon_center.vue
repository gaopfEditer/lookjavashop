<!-- 领券中心 -->
<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="coupon-list" :style="$theme.variable">
        <z-paging
            ref="paging"
            v-model="coupon.lists"
            @query="queryList"
            :use-page-scroll="true"
            safe-area-inset-bottom
            :refresher-enabled="true"
            :auto-clean-list-when-reload="false"
            :auto-scroll-to-top-when-reload="false"
        >
            <l-swiper
                height="300"
                v-if="coupon.enabled"
                :content="coupon.pages[0]?.content"
                mode="none"
            />

            <view class="lists-wrap pt-[22rpx]">
                <coupon-card v-for="item in coupon.lists" :key="item.id" :data="item">
                    <template #other>
                        <view class="w-[120rpx] flex items-end">
                            <!--  已领取  -->
                            <image
                                v-if="
                                    (item.isReceive && !item.isAble) ||
                                    coupon.received.includes(item.id)
                                "
                                class="w-[120rpx] h-[124rpx]"
                                src="@/static/images/icon/home_img_receive.png"
                            >
                            </image>
                            <!--  领取  -->
                            <button
                                v-if="item.isAble && !coupon.received.includes(item.id)"
                                class="bg-primary rounded-[60px] text-white h-[52rpx] leading-[52rpx] mb-[14rpx]"
                                @click.stop="onReceive(item.id)"
                            >
                                领取
                            </button>
                        </view>
                    </template>
                </coupon-card>
            </view>
        </z-paging>
    </view>
</template>

<script lang="ts" setup>
import { couponLists, receiveCoupon } from '@/api/marketing/coupon'
import { getDecorate } from '@/api/shop'
import { onLoad, onReachBottom, onPageScroll } from '@dcloudio/uni-app'
import { reactive, shallowRef } from 'vue'
import CouponCard from '@/components/coupon-card/coupon-card.vue'

const paging = shallowRef()
const coupon = reactive<any>({
    lists: [],
    pages: [],
    received: [],
    enabled: 1
})

// 领取优惠券
const onReceive = async (id: number) => {
    try {
        await receiveCoupon({ id })
        coupon.received.push(id)
        uni.$u.toast('领取成功')
        await paging.value.reload()
    } catch (error) {
        console.log(error)
    }
}

const getData = async () => {
    try {
        const data = await getDecorate({ id: 8 })
        coupon.pages = JSON.parse(data.pages)
        coupon.enabled = coupon.pages[0]?.content?.enabled
    } catch (error) {
        console.log('获取装修错误', error)
    }
}

const queryList = async (pageNo: number, pageSize: number) => {
    try {
        const { lists } = await couponLists({
            pageNo: pageNo,
            pageSize: pageSize
        })
        paging.value.complete(lists)
    } catch (e) {
        console.log('报错=>', e)
        //TODO handle the exception
        paging.value.complete(false)
    }
}

onReachBottom(() => {
    paging.value.pageReachBottom()
})

onPageScroll(({ scrollTop }: any) => {
    paging.value?.updatePageScrollTop(scrollTop)
})

onLoad(() => {
    getData()
})
</script>
