<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <z-paging ref="paging" v-model="goodsCollectData" @query="queryList" :fixed="false" height="100%"
        :use-page-scroll="true" empty-view-text="暂无收藏商品～" :empty-view-img="EmptyCollectImg"
        :empty-view-style="{ 'margin-top': '100px' }" :empty-view-center="false" :auto-clean-list-when-reload="false"
        :auto-scroll-to-top-when-reload="false" :empty-view-img-style="{ width: '360rpx', height: '360rpx' }">
        <u-swipe-action :show="item.show" :index="index" v-for="(item, index) in goodsCollectData" :key="item.id"
            :options="options" btn-width="120" @click="handleCollect">
            <goods-card shape="rectangle" :image="item.image" :imageStyle="{ width: '180rpx', height: '180rpx' }"
                :name="item.name" :containStyle="{ height: '220rpx', 'border-radius': '0' }">
                <view class="flex justify-between mt-[30rpx]">
                    <price :content="item.price" main-size="32rpx" minor-size="28rpx" fontWeight="500" />
                    <router-navigate :to="`/pages/goods_detail/goods_detail?id=${item.id}`">
                        <view class="buy-btn text-sm text-white px-[30rpx] py-[10rpx] rounded-full h-[56rpx]">去购买</view>
                    </router-navigate>
                </view>
            </goods-card>
        </u-swipe-action>
    </z-paging>
</template>

<script lang="ts" setup>
import { ref, reactive, shallowRef } from 'vue'
import EmptyCollectImg from '../../static/empty/collect.png'
import { getGoodsCollectLists, goodsCollect } from '@/api/goods'
import GoodsCard from '@/components/goods-card/goods-card.vue'
import Price from '@/components/price/price.vue'

const paging = shallowRef()
const options = reactive([
    {
        text: '取消收藏',
        style: {
            color: '#FFFFFF',
            backgroundColor: '#FF2C3C'
        }
    }
])
const goodsCollectData: any = ref([])

const queryList = async (pageNo: number, pageSize: number) => {
    const { lists } = await getGoodsCollectLists({
        pageNo: pageNo,
        pageSize: pageSize
    })
    lists.forEach((item: any) => {
        item.show = false
    })
    goodsCollectData.value = lists
    paging.value.complete(lists)
}

const handleCollect = async (index: number): Promise<void> => {
    try {
        const goods_id: number = goodsCollectData.value[index].id
        await goodsCollect({ id: goods_id })
        uni.$u.toast('已取消收藏')
        paging.value.reload()
    } catch (err) {
        //TODO handle the exception
        console.log('取消收藏报错=>', err)
    }
}
</script>

<style scoped>
.buy-btn {
    background: linear-gradient(90deg, #f95f2f 0%, #ff2c3c 100%);
}
</style>
