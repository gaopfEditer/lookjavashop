<template>
    <view class="swiper-wrap">
        <swiper class="swiper" ref="swiper" :autoplay="true" :circular="circular" :interval="interval"
            :duration="duration" @change="changeSwiper">
            <block v-for="(item, index) in data" :key="index">
                <swiper-item @tap="previewSwiperItem(index)">
                    <view v-if="item['type'] === 'video'" class="video-wrap">
                        <l-video height="750rpx" :src="item.url" :poster="videoCover" />
                    </view>

                    <u-image width="750" height="750" :src="item.url" mode="aspectFit" />
                </swiper-item>
            </block>
        </swiper>

        <view class="dots text-sm text-white rounded-[60px]" v-if="showDots">
            {{ currentSwiperIndex + 1 }}/{{ data.length || 0 }}
        </view>
    </view>
</template>

<script lang="ts" setup>
import { ref, computed, unref } from "vue"

const props = defineProps({
    // 视频标题
    images: {
        type: Array,
        default: () => []
    },
    // 视频链接
    video: {
        type: [String, Boolean],
        default: false
    },

    // 视频封面
    videoCover: {
        type: [String, Boolean],
        default: false
    },

    // 衔接滑动，即播放到末尾后重新回到开头
    circular: {
        type: Boolean,
        default: true
    },

    // 自动切换时间间隔
    interval: {
        type: Number,
        default: 15 * 1000
    },

    // 滑动动画时长
    duration: {
        type: Number,
        default: 500
    }
})

const data = computed(() => {
    const images = props.images.map(item => ({ url: item, type: 'image' }))

    return props.video
        ? [{ url: props.video, type: 'video' }, ...images]
        : images
})

const showDots = computed(() => {
    return unref(data)[unref(currentSwiperIndex)]?.type !== 'video'
})

const currentSwiperIndex = ref<number>(0)


const changeSwiper = (e: any) => {
    currentSwiperIndex.value = e.detail.current
}

// 查看Swiper项
const previewSwiperItem = (current: number) => {
    if (unref(data)[current].type === 'video') return;

    // #ifdef MP-WEIXIN
    wx.previewMedia({
        current,
        sources: unref(data)
    })
    //#endif

    // #ifndef MP-WEIXIN
    uni.previewImage({ current, urls: props.images })
    //#endif

}
</script>

<style scoped>
.swiper-wrap {
    width: 100%;
    height: 750rpx;
    position: relative;
}

.swiper-wrap .swiper {
    width: 100%;
    height: 100%;
    position: relative;
}

.swiper-wrap .swiper .slide-image {
    width: 100%;
    height: 100%;
}

.swiper-wrap .dots {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    display: flex;
    height: 34rpx;
    padding: 0 15rpx;
    background-color: rgba(0, 0, 0, 0.5);
}

.swiper-wrap .video-wrap {
    width: 100%;
    height: 100%;
    position: relative;
    overflow: hidden;
}

.swiper-wrap .video {
    width: 100%;
    height: 100%;
}

.swiper-wrap .icon-play {
    width: 90rpx;
    height: 90rpx;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 999;
}
</style>