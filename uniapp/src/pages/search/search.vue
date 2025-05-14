<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="search">
        <!-- 搜索框 -->
        <view class="px-[24rpx] py-[14rpx] bg-white">
            <u-search v-model="search.keyword" placeholder="请输入关键词搜索" height="72" @search="handleSearch"
                @custom="handleSearch" @clear="search.searching = false" @focus="search.searching = false"></u-search>
        </view>

        <!-- 搜索 -->
        <view class="search-content">
            <!--  -->
            <suggest v-show="!search.searching" @search="handleSearch" @clear="handleClear" :hot_search="search.hot_search"
                :his_search="search.his_search"></suggest>

            <!--  -->
            <view class="search-content-s" v-show="search.searching">
                <sort-nav class="mt-[20rpx]" v-model:active="search.sort" @change="changeNav">
                    <view class="flex items-center" @click="
                        search.goodsCardType =
                        search.goodsCardType == 'square' ? 'rectangle' : 'square'
                        ">
                        <text class="mr-[40rpx]">|</text>
                        <!-- <u-icon name="grid" size="28"></u-icon> -->
                        <image class="w-[32rpx] h-[32rpx]" v-show="search.goodsCardType != 'square'"
                            src="@/static/images/icon/icon_grid.png" />
                        <image class="w-[32rpx] h-[32rpx]" v-show="search.goodsCardType == 'square'"
                            src="@/static/images/icon/icon_list_dat.png" />
                    </view>
                </sort-nav>

                <z-paging ref="paging" v-model="search.result" @query="queryList" :fixed="false" height="100%"
                    empty-view-text="暂无搜索内容～" :empty-view-img="EmptySearchImg" :empty-view-style="{ 'margin-top': '100px' }"
                    :auto-clean-list-when-reload="false" :auto-scroll-to-top-when-reload="false" :empty-view-center="false"
                    :empty-view-img-style="{
                        width: '360rpx',
                        height: '360rpx'
                    }">
                    <view class="mx-[20rpx] pt-[20rpx]" :class="{
                        'flex flex-wrap justify-between': search.goodsCardType == 'square'
                    }">
                        <view class="mb-[20rpx]" v-for="item in search.result" :key="item.id"
                            @click="goToGoodsDetail(item.id)">
                            <goods-card :shape="search.goodsCardType" :name="item.name" :image="item.image"
                                :price="item.price" :minPrice="item.linePrice" />
                        </view>
                    </view>
                </z-paging>
            </view>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { reactive, shallowRef } from 'vue'
import Suggest from './component/suggest.vue'
import { HISTORY } from '@/enums/cacheEnums'
import { getHotSearch } from '@/api/shop'
import cache from '@/utils/cache'
import { getGoodsLists } from '@/api/goods'
import { onLoad } from '@dcloudio/uni-app'
import EmptySearchImg from '/static/images/empty/search.png'
import { useRouter } from 'uniapp-router-next'

interface SearchType {
    hot_search: {
        data: any[]
        status: number
    }
    his_search: string[]
    result: any
    searching: boolean
    keyword: string
    goodsCardType: string
    sort: string
    category_id: string | number
}

const search = reactive<SearchType>({
    hot_search: {
        data: [],
        status: 1
    },
    his_search: [],
    result: [],
    searching: false,
    keyword: '',
    goodsCardType: 'square',
    sort: '',
    category_id: ''
})
const paging = shallowRef()
const router = useRouter()

onLoad((options: any) => {
    search.category_id = options.id || ''
    if (search.category_id) {
        search.searching = true
        uni.setNavigationBarTitle({
            title: options.name
        })
    }
})

const handleSearch = (value: string) => {
    search.keyword = value
    if (search.keyword) {
        if (!search.his_search.includes(search.keyword)) {
            search.his_search.unshift(search.keyword)
            cache.set(HISTORY, search.his_search)
        }
    }
    paging.value.reload()
    search.searching = true
}

const changeNav = () => {
    paging.value.reload()
}

const getHotSearchFunc = async () => {
    try {
        search.hot_search = await getHotSearch()
    } catch (e) {
        //TODO handle the exception
        console.log('获取热门搜索失败=>', e)
    }
}

const handleClear = async (): Promise<void> => {
    const resModel: any = await uni.showModal({
        title: '温馨提示',
        content: '是否清空历史记录？'
    })
    if (resModel.confirm) {
        cache.set(HISTORY, '')
        search.his_search = []
    }
}

const queryList = async (pageNo: number, pageSize: number) => {
    try {
        const { lists } = await getGoodsLists({
            categoryId: search.category_id,
            keyword: search.keyword,
            sort: search.sort,
            pageNo: pageNo,
            pageSize: pageSize
        })
		if (lists.length == 0) {
			paging.value.complete([])
			paging.value.updateCache()
		} else {
			paging.value.complete(lists)
		}
    } catch (error) {
        console.log('报错=>', error)
        //TODO handle the exception
        paging.value.complete(false)
    }
}

const goToGoodsDetail = (id: number) => {
    router.navigateTo(`/pages/goods_detail/goods_detail?id=${id}`)
}

getHotSearchFunc()
search.his_search = cache.get(HISTORY) || []
</script>

<style lang="scss" scoped>
.search {
    &-content {
        height: calc(100vh - 46px - env(safe-area-inset-bottom));

        &-s {
            height: 100%;
        }
    }
}
</style>
