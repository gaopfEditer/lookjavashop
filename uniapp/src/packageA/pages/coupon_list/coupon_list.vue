<template>
    <view class="coupon-list">
        <!-- Tabs -->
        <tabs :current="coupon.current" height="80" bar-width="60" :is-scroll="true" itemWidth="250"
            @change="(e) => (coupon.current = e)" :active-color="$theme.primaryColor">
            <tab v-for="(item, index) in tabList" :key="index" :name="item.name">
                <view :style="{
                    height: `calc(100vh - 40px)`
                }">
                    <coupon-list :type="item.type" :i="index" :index="coupon.current" @extend="handleExtend"></coupon-list>
                </view>
            </tab>
        </tabs>
    </view>
</template>

<script lang="ts" setup>
import { reactive, computed } from 'vue'
import CouponList from './component/coupon-list.vue'

const coupon = reactive({
    current: 0
})
const tabList = reactive<any>([
    {
        name: '可使用(0)',
        type: 0
    },
    {
        name: '已使用(0)',
        type: 1
    },
    {
        name: '已失效(0)',
        type: 2
    }
])

const handleExtend = ({ normal, used, invalid }: any) => {
    tabList[0].name = `可使用(${normal})`
    tabList[1].name = `已使用(${used})`
    tabList[2].name = `已失效(${invalid})`
}
</script>
