<template>
    <view class="comment-list">
        <!-- Nav -->
        <navbar
            title="商品评价"
            :background="{ background: `${$theme.navBgColor}` }"
            :titleColor="`${$theme.navColor}`"
            :immersive="false"
        ></navbar>

        <!-- Tabs -->
        <tabs
            :current="comment.current"
            height="80"
            bar-width="60"
            :is-scroll="true"
            itemWidth="250"
            @change="(e) => (comment.current = e)"
            :active-color="$theme.primaryColor"
        >
            <tab
                v-for="(item, index) in tabList"
                :key="index"
                :name="item.name"
            >
                <view
                    class="px-[20rpx]"
                    :style="{
                        height: `calc(100vh - ${ comment.statusBarHeight + navbarHeight }px - 40px - env(safe-area-inset-bottom)`
                    }"
                >
                    <comment-list
                        :type="item.type"
                        :i="index"
                        :index="comment.current"
                        @extend="handleExtend"
                    ></comment-list>
                </view>
            </tab>
        </tabs>
    </view>
</template>

<script lang="ts" setup>
import { reactive, computed } from 'vue'
import CommentList from './component/comment-list.vue'

// 获取系统状态栏的高度
const systemInfo = uni.getSystemInfoSync()
const comment = reactive({
    current: 0,
    height: 0,
    statusBarHeight: systemInfo.statusBarHeight
})
const tabList = reactive<any>([
    {
        name: '待评价(0)',
        type: 0
    },
    {
        name: '已评价(0)',
        type: 1
    }
])

// 转换字符数值为真正的数值
const navbarHeight = computed(() => {
    // #ifdef APP-PLUS || H5
    return comment.height ? comment.height : 44
    // #endif
    // #ifdef MP
    const height = systemInfo.osName == 'ios' ? 44 : 48
    return comment.height ? comment.height : height
    // #endif
})

const handleExtend = ({ wait, finish }: any) => {
    tabList[0].name = `待评价(${wait})`
    tabList[1].name = `已评价(${finish})`
}
</script>

<style lang="scss" scoped>
page {
    height: 100%;
}

.comment-list {
    position: relative;

    &-content {
        height: 100%;
        // height: calc(100vh - 40px - env(safe-area-inset-bottom));
    }
}
</style>
