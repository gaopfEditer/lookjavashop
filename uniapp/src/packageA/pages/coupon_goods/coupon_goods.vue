<template>
    <view class="coupon-goods">
        <!-- 搜索框 -->
        <view class="px-[24rpx] py-[14rpx] bg-white">
            <u-search v-model="search.keyword" placeholder="请输入要搜索的商品" height="72" @search="handleSearch"
                @custom="handleSearch" @clear="search.searching = false" @focus="search.searching = false"></u-search>
        </view>
        <view>
            <view class="coupon-goods-title">
                <view class="content">
                    {{ search.couponName }}
                </view>
            </view>
            <sort-nav class="mt-[20rpx]" v-model:active="search.sort" @change="changeNav">
                <view class="flex items-center" @click="
                    search.goodsCardType =
                    search.goodsCardType == 'square' ? 'rectangle' : 'square'
                    ">
                    <view class="w-[1rpx] h-[34rpx] mr-[60rpx] bg-[#5d5d5d]"></view>

                    <image class="w-[32rpx] h-[32rpx]" v-show="search.goodsCardType != 'square'"
                        src="@/static/images/icon/icon_grid.png" />
                    <image class="w-[32rpx] h-[32rpx]" v-show="search.goodsCardType == 'square'"
                        src="@/static/images/icon/icon_list_dat.png" />
                </view>
            </sort-nav>
        </view>

        <!-- 搜索 -->
        <view class="coupon-goods-content">
            <z-paging ref="paging" v-model="search.result" @query="queryList" :fixed="false" height="100%"
                empty-view-text="暂无搜索商品～" :empty-view-img="EmptySearchImg" :empty-view-style="{ 'margin-top': '100px' }"
                :auto-clean-list-when-reload="false" :auto-scroll-to-top-when-reload="false" :empty-view-center="false"
                :empty-view-img-style="{
                    width: '360rpx',
                    height: '360rpx'
                }">
                <view class="pt-[20rpx]" :class="{
                    'flex flex-wrap justify-between mx-[20rpx]':
                        search.goodsCardType == 'square'
                }">
                    <view class="mb-[20rpx]" v-for="item in search.result" :key="item.id" @click="goToGoodsDetail(item.id)">
                        <goods-card :shape="search.goodsCardType" :name="item.name" :image="item.image" :price="item.price"
                            :minPrice="item.lineationPrice">
                            <view class="flex items-center">
                                <price :content="item.price" fontWeight="500" mainSize="38rpx" minorSize="24rpx"
                                    class="mr-2" />
                                <price :content="item.lineationPrice" v-if="item.lineationPrice != '0'" mainSize="24rpx"
                                    minorSize="24rpx" color="#999999" lineThrough />
                            </view>
                        </goods-card>
                    </view>
                </view>
            </z-paging>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { reactive, shallowRef } from 'vue'
import { getGoodsLists } from '@/api/goods'
import { onLoad } from '@dcloudio/uni-app'
import EmptySearchImg from '@/static/images/empty/search.png'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

interface SearchType {
    result: any
    searching: boolean
    keyword: string
    goodsCardType: string
    sort: string
    couponId: string | number
    couponName: string
}

const search = reactive<SearchType>({
    result: [],
    searching: false,
    keyword: '',
    goodsCardType: 'square',
    sort: '',
    couponId: '',
    couponName: '无门槛'
})
const paging = shallowRef()

onLoad((options: any) => {
    const payload = JSON.parse(options.payload)
    search.couponId = payload.id || ''
    if (payload.type == 2) {
        uni.setNavigationBarTitle({
            title: '指定可用商品'
        })
    } else if (options.type == 3) {
        uni.setNavigationBarTitle({
            title: '指定不可用商品'
        })
    }
})

const handleSearch = (value: string) => {
    search.keyword = value
    paging.value.reload()
    search.searching = true
}

const changeNav = () => {
    paging.value.reload()
}

const queryList = async (pageNo: number, pageSize: number) => {
    try {
        const { lists, extend } = await getGoodsLists({
            couponId: search.couponId,
            keyword: search.keyword,
            sort: search.sort,
            pageNo: pageNo,
            pageSize: pageSize
        })
        paging.value.complete(lists)
        search.couponName = extend.couponCondition
    } catch (error) {
        console.log('报错=>', error)
        //TODO handle the exception
        paging.value.complete(false)
    }
}

const goToGoodsDetail = (id: number) => {
    router.navigateTo(`/pages/goods_detail/goods_detail?id=${id}`)
}
</script>

<style lang="scss" scoped>
.coupon-goods {
    &-title {
        padding: 24rpx 30rpx;
        background-color: #ffffff;

        .content {
            border-radius: 10rpx;
            color: $u-type-primary;
            text-align: center;
            padding: 16rpx 0;
            background: rgba($u-type-primary, 0.1);
        }
    }

    &-content {
        height: calc(100vh - 46px - env(safe-area-inset-bottom));
    }
}
</style>
