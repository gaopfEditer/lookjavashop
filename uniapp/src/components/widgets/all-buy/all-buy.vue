<template>
    <!-- 热门商品 -->
    <z-paging
        ref="paging"
        v-model="state.lists"
        use-page-scroll
        :refresher-enabled="false"
        :auto-clean-list-when-reload="false"
        :auto-scroll-to-top-when-reload="false"
        @query="queryList"
        v-if="content.enabled"
    >
        <view class="recommend-goods">
            <view class="recommend-goods-title flex">
                <view class="w-[44rpx] h-[44rpx]">
                    <image class="w-full h-full" src="@/static/images/user/icon_good.png" />
                </view>
                <text class="ml-[20rpx] text-main">大家都在买</text>
            </view>
            <goods-list :list="goods_lists" />
        </view>
    </z-paging>
</template>
<script setup lang="ts">
import { getGoodsLists } from '@/api/goods'
import { shallowRef, ref, reactive, computed } from 'vue'
const props = defineProps({
    content: {
        type: Object,
        default: () => ({})
    },
    styles: {
        type: Object,
        default: () => ({})
    }
})
const paging = shallowRef()
const state = reactive({
    lists: []
})
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
const goods_lists = computed(() => {
    return state.lists.filter((i: any, index: number) => {
        return index < props.content.goods_num
    })
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
        text-align: center;
    }
}
</style>
