<template>
    <video class="w-full" id="video" :src="getSource(src)" :poster="getPoster(poster)" :title="title" :style="{
        height: height
    }" :enable-progress-gesture="false" controls autoplay muted show-mute-btn></video>
</template>

<script lang="ts" setup>

const props = defineProps({
    // 视频标题
    title: {
        type: String,
    },
    // 视频地址
    src: {
        type: String
    },
    // 视频封面
    poster: {
        type: String
    },
    // 视频高度
    height: {
        type: String,
        default: '200px',
    }
});

const getSource = (item: string | any) => {
    if (typeof item === 'string') return item
    else uni.$u.toast('请按格式传递列表参数')
    return ''
}

// 
const getPoster = (item: string | any) => {
    return item ? item : ''
}

</script>