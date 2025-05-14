<template>
    <u-navbar
        :border-bottom="false"
        :is-fixed="false"
        custom-class="home-bg"
        :is-back="false"
        :navbarStyle="{
            background: bgColor
        }"
        :background="{
            background: bgColor
        }"
    >
        <view class="flex-1 px-[24rpx]">
            <router-navigate
                to="/pages/search/search"
                class="search"
                hover-class="none"
            >
                <u-search
                    placeholder="请输入关键词搜索"
                    disabled
                    :show-action="false"
                ></u-search>
            </router-navigate>
        </view>
    </u-navbar>
</template>

<script setup lang="ts">
const props = withDefaults(
    defineProps<{
        bgColor?: string //
    }>(),
    {
        bgColor: '#FFFFFF'
    }
)
</script>

<style></style>
