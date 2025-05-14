<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <u-sticky offset-top="0" h5-nav-height="0" bg-color="transparent">
            <u-navbar
                :is-back="false"
                title="个人中心"
                :title-bold="false"
                :is-fixed="false"
                :border-bottom="false"
                :background="{ background: `${$theme.navBgColor}` }"
                :title-color="$theme.navColor"
            ></u-navbar>
        </u-sticky>
        <!-- #endif -->
    </page-meta>
    <view class="user">
        <view v-for="(item, index) in state.pages" :key="index">
            <template v-if="item.name == 'user-info'">
                <w-user-info
                    :content="item.content"
                    :styles="item.styles"
                    :user="userInfo"
                    :is-login="isLogin"
                />
            </template>
            <template v-if="item.name == 'my-order'">
                <w-user-order :content="item.content" :userInfo="userInfo" :styles="item.styles" />
            </template>
            <template v-if="item.name == 'my-service'">
                <w-my-service :content="item.content" :styles="item.styles" :user="userInfo" />
            </template>
            <template v-if="item.name == 'user-banner'">
                <w-user-banner :content="item.content" :styles="item.styles" />
            </template>
            <template v-if="item.name == 'all-buy'">
                <w-all-buy :content="item.content" :styles="item.styles" />
            </template>
        </view>

        <!-- 地步导航 -->
        <tabbar />

        <!-- 骨袈屏 -->
        <skeleton v-if="state.loading"></skeleton>
    </view>
</template>

<script setup lang="ts">
import { getDecorate } from '@/api/shop'
import { getGoodsLists } from '@/api/goods'
import { useUserStore } from '@/stores/user'
import { onShow } from '@dcloudio/uni-app'
import { storeToRefs } from 'pinia'
import { nextTick, reactive, shallowRef } from 'vue'

import Skeleton from '@/pages/user/component/skeleton.vue'

const userStore = useUserStore()
const { userInfo, isLogin } = storeToRefs(userStore)

const paging = shallowRef()
const state = reactive<{
    loading: boolean
    pages: any[]
    goods_list: any[]
}>({
    loading: true,
    pages: [],
    goods_list: []
})

const getData = async () => {
    try {
        const data = await getDecorate({ id: 2 })
        state.pages = JSON.parse(data.pages)
    } catch (error) {
        console.log(error)
    }
    await nextTick()
    state.loading = false
}

const queryList = async (pageNo: number) => {
    try {
        const { lists } = await getGoodsLists({
            type: 'hot',
            pageNo: pageNo,
            pageSize: 20
        })
        paging.value.complete(lists)
    } catch (e) {
        console.log('报错=>', e)
        //TODO handle the exception
        paging.value.complete(false)
    }
}

onShow(async () => {
    await getData()
    await userStore.getUser()
    await userStore.getShopCartCount()
})
</script>

<style lang="scss" scoped>
.recommend-goods {
    &-title {
        margin-top: 20rpx;
        padding: 26rpx 0;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 34rpx;
        color: #ff612f;
        padding: 30 rpx 0 10 rpx 0;
        text-align: center;
    }
}
</style>
