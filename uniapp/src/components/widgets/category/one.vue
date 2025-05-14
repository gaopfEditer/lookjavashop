<template>
    <view class="flex h-full">
        <left-aside></left-aside>

        <view class="mx-[20rpx] my-[20rpx] w-full">
            <l-swiper height="200" v-if="page.enabled" :content="page" mode="none" />

            <view class="mt-[20rpx]">
                <view class="bg-white rounded-[10rpx] mb-[20rpx]" v-for="(item, index) in list[current]?.children"
                    :key="index">
                    <router-navigate :to="`/pages/search/search?id=${item.id}&name=${item.name}`"
                        class="search px-[24rpx] bg-white" hover-class="none">
                        <view class="flex p-[20rpx] justify-between">
                            <text class="bold sm">{{ item.name }}</text>
                            <u-icon name="arrow-right"></u-icon>
                        </view>
                    </router-navigate>

                    <view class="flex flex-wrap px-[20rpx] py-[10rpx] col-top" v-if="item?.children">
                        <view class="text-center item" :style="{ width: `${100 / 3}%` }"
                            v-for="(sitem, sindex) in item.children" :key="sindex">
                            <router-navigate :to="`/pages/search/search?id=${sitem.id}&name=${sitem.name}`">
                                <view class="flex-col col-center mb-[20rpx]">
                                    <view class="bg-white">
                                        <u-image mode="aspectFit" width="150rpx" height="150rpx" :src="sitem.image" />
                                    </view>
                                    <view class="mt-[10rpx] text-xs px-[10rpx]">{{ sitem.name }}</view>
                                </view>
                            </router-navigate>
                        </view>
                    </view>
                </view>
            </view>
        </view>
    </view>
</template>

<script lang="ts" setup>
import leftAside from './left-aside.vue';
import { useDataEffect } from "./useDataEffect";
const { list, page, current } = useDataEffect()
</script>