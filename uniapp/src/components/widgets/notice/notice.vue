<template>
    <view class="mx-[30rpx] notice">
        <router-navigate to="/packageA/pages/news/news" hover-class="none" v-if="content?.enabled == 1">
            <view class="my-[20rpx] bg-white px-[20rpx] py-[16rpx] rounded-lg overflow-hidden">
                <view class="flex">
                    <image class="flex-none h-[40rpx] mb-[2px]" mode="heightFix" src="./images/icon_notice.png"
                           v-if="content.icon == 1" />
                    <image class="flex-none h-[40rpx] mb-[2px]" mode="heightFix" :src="getImageUrl(content?.iconUrl)" v-else />
                    <view class="flex items-center">
                        <view class="mx-[6px] text-[#f1f1f1]"> |</view>
                        <swiper class="w-[500rpx] h-[40rpx]" :autoplay="true" :interval="5000" :duration="500" :vertical="true"
                                :circular="true">
                            <swiper-item class="flex items-center" v-for="(item, index) in article" :key="index">
                                <view class="truncate w-[500rpx] text-xs ml-[4px]">
                                    {{ item.title }}
                                </view>
                            </swiper-item>
                        </swiper>
                    </view>
                </view>
            </view>
        </router-navigate>
    </view>
</template>

<script setup lang="ts">
import { getDecorate } from '@/api/shop'
import { useAppStore } from '@/stores/app'
const { getImageUrl } = useAppStore()
const props = defineProps({
    article: {
        type: Object,
        default: () => ({})
    },
    styles: {
        type: Object,
        default: () => ({})
    },
    content: {
        type: Object,
        default: () => ({})
    }
})
</script>

<style></style>
