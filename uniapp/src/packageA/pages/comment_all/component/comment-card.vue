<template>
    <view class="contain px-[20rpx]">
        <view
            class="text-base mb-[20rpx] comment-content"
            v-for="sitem in data"
            :key="sitem.id"
        >
            <!-- Header -->
            <view class="flex justify-between">
                <!-- 用户信息 -->
                <view class="flex">
                    <u-image
                        width="74"
                        height="74"
                        mode="scaleToFill"
                        border-radius="50%"
                        :src="sitem.avatar"
                    />
                    <view class="ml-[10rpx]">
                        <view class="text-lg text-main">{{ sitem?.nickname || '用户昵称' }}</view>
                        <view class="text-xs text-muted">{{ sitem?.createTime }}</view>
                    </view>
                </view>
                <!-- 评分 -->
                <view class="text-sm text-muted">
                    <text>{{ sitem?.scoreMsg }}</text>
                    <u-rate
                        :current="sitem.goodsScore"
                        :disabled="true"
                    />
                </view>
            </view>
            <!-- 评价文本 -->
            <view class="mt-4 break-all text-main">{{ sitem.content }}</view>
            <!-- 评价图片 -->
            <view class="flex flex-wrap comment-images" v-if="sitem.imagesList">
                <u-image
                    class="mt-2 comment-images-item"
                    v-for="(imageItem, imageIndex) in sitem.imagesList"
                    :key="imageItem"
                    :src="imageItem"
                    width="200"
                    height="200"
                    mode="aspectFill"
                    @tap="previewImage(sitem.imagesList, imageIndex)"
                />
            </view>
            <!-- 商品规格 -->
            <view class="mt-[30rpx] text-muted">
                规格：{{ sitem?.goodsSkuValue }}
            </view>
            <!-- 商家回复 -->
            <view class="comment-reply" v-if="sitem.replyContent">
                <text class="mr-[20rpx] text-muted">商家回复:</text>
                <text>{{ sitem.replyContent || '谢谢亲亲的支持～' }}</text>
            </view>
        </view>
    </view>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
defineProps({
    data: {
        type: Array as PropType<any>,
        default: () => []
    }
})

// 查看评价图片
const previewImage = (images: string[], index: number) => {
    uni.previewImage({
        urls: images,
        current: index
    })
}
</script>

<style lang="scss" scoped>
.comment-content {
    padding: 24rpx;
    border-radius: 14rpx;
    background-color: #FFFFFF;

    .comment-images {
        margin-top: 20rpx;
        &-item {
            margin-right: 14rpx;
        }
        &-item:nth-child(3n) {
            margin-right: 0;
        }
    }

    .comment-reply {
        padding: 20rpx;
        margin-top: 30rpx;
        border-radius: 8px;
        background-color: #F6F6F6;
        word-break: break-all;
    }
}
</style>