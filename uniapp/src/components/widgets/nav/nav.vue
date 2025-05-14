<template>
    <div class="mx-[30rpx] mt-[20rpx]">
        <swiper
            class="pt-[30rpx] bg-white rounded-lg"
            :style="{
                height: navList[0].length > 5 ? '336rpx' : '168rpx'
            }"
            :indicator-dots="navList.length > 1"
            :autoplay="false"
        >
            <swiper-item v-for="(sItem, sIndex) in navList" :key="sIndex">
                <view class="nav" v-if="navList.length && content.enabled">
                    <view class="flex flex-wrap nav-item">
                        <view
                            v-for="(item, index) in sItem"
                            :key="index"
                            class="flex flex-col items-center w-1/5 mb-[30rpx]"
                            @click="handleClick(item.link)"
                        >
                            <u-image width="82" height="82" :src="getImageUrl(item.image)" alt="" />
                            <view class="mt-[14rpx] text-xs">{{ item.name }}</view>
                        </view>
                    </view>
                </view>
            </swiper-item>
        </swiper>
    </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useAppStore } from '@/stores/app'
import { navigateTo, sliceArray } from '@/utils/util'

const props = defineProps({
    content: {
        type: Object,
        default: () => ({})
    },
    styles: {
        type: Object,
        default: () => ({})
    }
})

const handleClick = (link: any) => {
    navigateTo(link)
}
const { getImageUrl } = useAppStore()

const navList = ref<Record<string, any>>([])

watch(
    () => props.content.data,
    (val) => {
        navList.value = sliceArray(val, 10)
    },
    { deep: true, immediate: true }
)
</script>
