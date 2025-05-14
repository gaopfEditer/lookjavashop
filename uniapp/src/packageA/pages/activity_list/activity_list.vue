<!-- 一些活动榜单 -->
<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="activity-list">
        <z-paging ref="paging" v-model="activity.lists" @query="queryList" use-page-scroll safe-area-inset-bottom
            :refresher-enabled="false" :auto-clean-list-when-reload="false" :auto-scroll-to-top-when-reload="false">
            <l-swiper height="300" v-if="activity.enabled" :content="activity.pages[0]?.content" mode="none" />

            <view class="lists-wrap pt-[22rpx]">
                <!-- 热门商品列表 -->
                <div class="mx-[30rpx] mb-[20rpx]">
                    <router-navigate
                        class="rounded-lg flex items-center overflow-hidden bg-white mb-[20rpx] px-[20rpx] py-[10rpx]"
                        v-for="(item, index) in activity.lists" :key="index"
                        :to="`/pages/goods_detail/goods_detail?id=${item.id}`">
                        <u-image :src="item.image" :width="180" :height="180" fit="contain" />
                        <div class="m-[20rpx]">
                            <div class="line-2 w-[440rpx] pt-[10rpx] text-base leading-[38rpx]">
                                {{ item.name }}
                            </div>
                            <div class="inline-block pr-2 text-xs rounded-full hot-sales-num">
                                <image src="@/static/images/icon/icon_hot.png" class="w-[40rpx] h-[40rpx] align-middle" />
                                <text class="text-[#FF2C3C] ml-2">销量 {{ item.salesNum }}</text>
                            </div>
                            <div class="mt-[10rpx] text-xs">
                                <price :content="item.price" fontWeight="500" mainSize="34rpx" minorSize="26rpx"
                                    class="mr-2" />
                                <price :content="item.lineationPrice" v-if="item.lineationPrice != '0.00'" mainSize="24rpx"
                                    minorSize="24rpx" color="#999999" :lineThrough="true" />
                            </div>
                        </div>
                    </router-navigate>
                </div>
            </view>
        </z-paging>
    </view>
</template>

<script lang="ts" setup>
import { getGoodsLists } from '@/api/goods'
import { getDecorate } from '@/api/shop'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { reactive, shallowRef, unref, computed } from 'vue'
const paging = shallowRef()
const activity = reactive<any>({
    lists: [],
    pages: [],
    enabled: 1,
    sort: '',
    catetoryId: ''
})

const getData = async () => {
    try {
        const data = await getDecorate({ id: 6 })
        activity.pages = JSON.parse(data.pages)
        activity.enabled = activity.pages[0]?.content?.enabled
    } catch (error) {
        console.log('获取装修错误', error)
    }
}

const queryList = async (pageNo: number, pageSize: number) => {
    try {
        const { lists } = await getGoodsLists({
            type: activity.sort,
            pageNo: pageNo,
            pageSize: pageSize,
            catetoryId: activity.catetoryId
        })
        paging.value.complete(lists)
    } catch (e) {
        console.log('报错=>', e)
        //TODO handle the exception
        paging.value.complete(false)
    }
}

onReachBottom(() => {
    paging.value.pageReachBottom()
})

onLoad(({ sort, id }: any) => {
    activity.sort = sort
    activity.catetoryId = id || ''
    switch (sort) {
        case 'hot':
            uni.setNavigationBarTitle({
                title: '热门商品'
            })
            break
        case 'new':
            uni.setNavigationBarTitle({
                title: '新品推荐'
            })
            break
    }
    getData()
})
</script>
