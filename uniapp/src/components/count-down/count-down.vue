<template>
    <view class="text-white count-down">
        <u-count-down
            :timestamp="timestamp"
            format="DD:HH:mm:ss"
            @change="timeHandle"
        >
            <text class="pl-[20rpx] pr-[14rpx]" v-if="timer.days">{{ timer.days }}天</text>
            <text class="chunk">{{ timer.hours }}</text>
            <text class="px-[10rpx]">:</text>
            <text class="chunk">{{ timer.minutes }}</text>
            <text class="px-[10rpx]">:</text>
            <text class="chunk">{{ timer.seconds }}</text>
        </u-count-down>
    </view>
</template>


<script lang="ts" setup>
/**
 * @description 倒计时组件
 * @property {Number|String} timeEnd 结束时间戳 (必填项)
 * @example <count-down :time-end="endTime" />
 */

import {computed, reactive} from "vue";

const props = defineProps({
    // 结束时间戳(秒)
    timeEnd: {
        type:  [Number,String],
        default: ''
    }
});

const timer = reactive({
    days: 0,
    hours: 20,
    minutes: 36,
    seconds: 35
})

// 剩余--时间戳
const timestamp = computed(() => {
    const nowTimestamp = new Date().getTime().toString().substr(0, 10)
    return (props.timeEnd - nowTimestamp) * 1000
})
const timeHandle = (time: any) => {
    timer.days = time.days
    timer.hours = (time.hours <= 9 ? '0' + time.hours : time.hours)
    timer.minutes = (time.minutes <= 9 ? '0' + time.minutes : time.minutes)
    timer.seconds = (time.seconds <= 9 ? '0' + time.seconds : time.seconds)
}
</script>


<style lang="scss" scoped>
.count-down {
    .chunk {
        color: $u-type-primary;
        border-radius: 6rpx;
        display: inline-block;
        width: 50rpx;
        height: 44rpx;
        line-height: 44rpx;
        text-align: center;
        background-color: var(--color-theme-light-color-light-9);
    }
}
</style>
