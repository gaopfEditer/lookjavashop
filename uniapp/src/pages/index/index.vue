<template>
    <!-- #ifndef H5 -->
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <!-- #endif -->
    <view class="index-page">
        <!-- #ifdef H5 -->
        <page-meta :page-style="$theme.pageStyle"></page-meta>
        <!-- #endif -->
        <view class="index">
            <view class="index__header">
                <!-- 背景图片 -->
                <view
                    class="index__header__banner_bg"
                    :style="{
                        backgroundImage: `url(${getBannerBg})`
                    }"
                >
                </view>
                <!-- 锚点（视图经过锚点以后就触发 -->
                <view class="index__anchor"></view>
                <!-- 顶部导航 -->
                <u-sticky :enable="true" bg-color="transparent" :offset-top="0" :h5-nav-height="0">
                    <view
                        :style="{
                            marginBottom: '30rpx',
                            transition: 'all 1s',
                            background: state.trigger ? '#FFFFFF' : 'transparent'
                        }"
                    >
                        <!-- 搜索栏 -->
                        <w-search bg-color="transparent"></w-search>
                    </view>
                </u-sticky>
                <!-- 轮播图 -->
                <w-banner
                    v-if="getBannerData?.content"
                    :content="getBannerData?.content"
                    :styles="getBannerData?.styles"
                    @change="
                        (i:any) => {
                            state.bannerIndex = i;
                        }
                    "
                />
            </view>

            <view class="index__main">
                <!-- 装修 -->
                <view v-for="(item, index) in state.pages" :key="index">
                    <template v-if="item.name == 'nav'">
                        <w-nav class="pt-2" :content="item.content" :styles="item.styles" />
                    </template>
                    <template v-if="item.name == 'notice'">
                        <w-notice
                            :article="state.article"
                            :styles="item.styles"
                            :content="item.content"
                        />
                    </template>
                    <template v-if="item.name == 'seckill'">
                        <w-seckill :content="item.content" :styles="item.styles" />
                    </template>
                    <!-- 热门商品 -->
                    <template v-if="state.hot_list.length && item.name == 'hot'">
                        <w-hot-goods :content="state.hot_list" :icontent="item.content" />
                    </template>
                    <!-- 新品推荐 -->
                    <template v-if="state.new_list.length && item.name == 'new'">
                        <w-new-goods :content="state.new_list" :icontent="item.content" />
                    </template>
                </view>
                <!-- #ifdef H5 -->
                <view class="record_number">
                    <view v-for="item in copyright" :key="item.link">
                        <a style="color: #495770; text-decoration: none" :href="item.link">
                            {{ item.name }}
                        </a>
                    </view>
                </view>
                <!-- #endif -->
            </view>

            <!-- 返回顶部按钮 -->
            <u-back-top
                :scroll-top="state.trigger ? 1 : 0"
                :top="0"
                :z-index="999"
                :customStyle="{
                    backgroundColor: '#FFF',
                    color: '#000',
                    boxShadow: '0px 3px 6px rgba(0, 0, 0, 0.1)'
                }"
            >
            </u-back-top>

            <!-- 骨袈屏 -->
            <skeleton v-if="state.loading"></skeleton>

            <!-- 底部导航栏 -->
            <tabbar />
        </view>
    </view>
</template>

<script setup lang="ts">
import { getIndex } from '@/api/shop'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import { nextTick, ref, reactive, computed, getCurrentInstance } from 'vue'
import { onShow, onHide, onPullDownRefresh } from '@dcloudio/uni-app'
import { getGoodsLists } from '@/api/goods'

import Skeleton from './component/skeleton.vue'

const { getShopCartCount } = useUserStore()
const { getImageUrl } = useAppStore()
const appStore = useAppStore()

//@ts-ignore
const ctx = getCurrentInstance()
const copyright = computed(() => appStore.getCopyright)

const state = reactive<{
    loading: boolean
    trigger: boolean
    pages: any[]
    article: any[]
    hot_list: any[]
    new_list: any[]
    bannerIndex: number
}>({
    loading: true,
    trigger: false,
    pages: [],
    article: [],
    hot_list: [],
    new_list: [],
    bannerIndex: 0
})

const _observer = ref<any>()

const getBannerData = computed(() => state.pages.find((item) => item.name == 'banner') || {})
const getBannerBg = computed(() => {
    const i = state.bannerIndex
    const url = getBannerData.value?.content?.data[i]?.image ?? ''
    return url.indexOf('http') == -1 ? getImageUrl(url) : url
})

const getIndexData = async () => {
    try {
        const hotData = await getGoodsLists({ type: 'hot', sort: 'salesDesc' })
        const data = await getIndex()
        state.hot_list = hotData.lists
        state.pages = JSON.parse(data?.pages)
        state.article = data.article ?? []
        // state.hot_list = data.hot_goods ?? [];
        state.new_list = data.new_goods ?? []
    } catch (error) {
        console.log('首页接口报错', error)
    }
    await nextTick()
    state.loading = false
    uni.stopPullDownRefresh()
}

// 监听函数
const observerFunc = (res: any) => {
    console.log('触发监听', res)
    if (res.intersectionRatio > 0) {
        state.trigger = false
    } else {
        state.trigger = true
    }
}

onPullDownRefresh(async () => {
    await getIndexData()
    await getShopCartCount()
})

onShow(async () => {
    _observer.value = uni.createIntersectionObserver(ctx)
    await getIndexData()
    await getShopCartCount()
    await nextTick()
    try {
        _observer.value
            .relativeToViewport({
                bottom: 10
            })
            .observe('.index__anchor', observerFunc)
    } catch (error) {
        console.log(error)
    }
})

onHide(() => {
    _observer.value.disconnect()
})
</script>

<style lang="scss">
.index {
    &__anchor {
        position: absolute;
        left: 0;
        width: 100vw;
        height: 10px;
    }

    &__header {
        overflow: hidden;
        position: relative;
        padding-bottom: 30rpx;
        width: 100%;

        &__banner_bg {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-size: 100% 100%;
            -webkit-filter: blur(30px);
            filter: blur(30px);
        }
    }
}
.record_number {
    text-align: center;
    padding: 30rpx 30rpx 150rpx 30rpx;
    font-size: 24rpx;
}
</style>
