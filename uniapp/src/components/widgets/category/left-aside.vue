<template>
    <scroll-view scroll-y="true" class="h-full w-[220rpx] bg-white" scroll-with-animation="true" v-if="list.length">
        <template v-for="(item, index) in list">
            <view class="text-center text-sm relative px-[20rpx] py-[26rpx]" :class="{'active text-primary': index == current}"
                @click="changeActive(index)">
                <text class="name">{{ item.name }}</text>
                <view v-if="index == current" class="active-line bg-primary"></view>
            </view>
        </template>
    </scroll-view>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import { useDataEffect } from "./useDataEffect";

const { list, current, changeActive } = useDataEffect()
</script>

<style>
.active {
    font-weight: bold;
}

.active-line {
    position: absolute;
    width: 6rpx;
    height: 30rpx;
    left: 4rpx;
    top: 50%;
    transform: translateY(-50%);
}
</style>