<template>
    <view
        v-for="(sitem, sindex) in data"
        :key="sindex"
        class="rounded-[10rpx] bg-white overflow-hidden p-[20rpx] mt-[20rpx] no-card"
        @click="goGoodsDetail(sitem.goodsId)"
    >

        <!-- Header -->
        <view class="flex justify-between no-card-header">
            <view class="text-muted font-xs">
                {{ sitem.comment.createTime || "-" }}
            </view>
            <view class="flex items-center">
                <view class="text-xs text-muted">
                    <text v-if="sitem.comment.goodsScore == 5">非常好</text>
                    <text v-if="sitem.comment.goodsScore == 4">好</text>
                    <text v-if="sitem.comment.goodsScore == 3">一般</text>
                    <text v-if="sitem.comment.goodsScore == 2">差</text>
                    <text v-if="sitem.comment.goodsScore == 1">非常差</text>
                </view>
                <u-rate
                    :count="5"
                    v-model="sitem.comment.goodsScore"
                    size="28"
                    inactive-icon="star-fill"
                ></u-rate>
            </view>
        </view>

        <!-- Main -->
        <view class="mt-2 no-card-main">
            <!-- Contemt -->
            <view class="break-all content">{{ sitem.comment.content }}</view>

            <!-- Image -->
            <view class="flex flex-wrap mt-2">
                <block v-for="(sitem3, index3) in sitem.comment.images" :key="index3">
                    <view
                        class="mt-2"
                        :class="{ 'mr-[14rpx]': (index3 + 1) % 4 != 0 }"
                        @click.stop="previewImage(sitem.comment.images, index3)"
                    >
                        <u-image :src="sitem3" width="155" height="155"></u-image>
                    </view>
                </block>
            </view>

            <!-- Goods -->
            <view class="flex mt-2 bg-page">
                <goods-card
                    shape="rectangle"
                    class="flex-1"
                    :image="sitem?.goodsImage"
                    :imageStyle="{ width: '180rpx', height: '180rpx' }"
                    :name="sitem?.goodsName"
                    :containStyle="{
                        background: 'none',
                        height: '220rpx',
                        'border-radius': '10rpx'
                    }"
                >
                    <view class="text-muted mt-[30rpx]">
                        {{ nsitem?.goodsSkuValue || "默认" }}
                    </view>
                </goods-card>
            </view>

            <!-- 商家回复 -->
            <view class="reply" v-if="sitem.comment.replyContent">
                <text class="mr-[20rpx] text-muted">商家回复:</text>
                <text>{{ sitem.comment.replyContent || '谢谢亲亲的支持～' }}</text>
            </view>
        </view>
    </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()
defineProps({
    data: {
        type: Array as PropType<any>,
        default: () => []
    }
})

// 去商品详情
const goGoodsDetail = (id: number) => {
    router.navigateTo(`/pages/goods_detail/goods_detail?id=${id}`)
}

// 查看评价图片
const previewImage = (images: string[], index: number) => {
    uni.previewImage({
        current: index,
        urls: images,
    });
};
</script>

<style lang="scss" scoped>
    .no-card {
        &-header {
            padding-bottom: 16rpx;
            border-bottom: 1px solid $u-border-color;
        }

        .reply {
            padding: 20rpx;
            margin-top: 20rpx;
            border-radius: 8px;
            background-color: #F6F6F6;
            word-break: break-all;
        }
    }
</style>