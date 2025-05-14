<template>
    <view class="banner h-[312rpx] translate-y-0" v-if="content.data.length && content.enabled">
        <LSwiper :content="content" height="312" :circular="true" :effect3d="true" border-radius="14" bgColor="transparent"
            @change="handleChange"></LSwiper>
    </view>
</template>

<script setup lang="ts">
import LSwiper from '@/components/l-swiper/l-swiper.vue'
const emit = defineEmits(['change'])
defineProps({
    content: {
        type: Object,
        default: () => ({})
    },
    styles: {
        type: Object,
        default: () => ({})
    }
})

const handleChange = (index: number) => {
    emit('change', index)
}
</script>
