<template>
    <view class="reviews bg-white mx-[20rpx] rounded-[14rpx]">
        <router-navigate
            hover-class="none"
            :to="`/packageA/pages/comment_all/comment_all?id=${goodsId}`"
        >
            <view class="flex justify-between reviews-header">
                <view>
                    <text class="text-lg text-[#101010]">用户评价</text>
                </view>
                <view>
                    <u-icon name="arrow-right" color="#707070" size="26rpx" />
                </view>
            </view>
        </router-navigate>
        <view class="reviews-content">
            <template v-if="comment.id">
                <view class="flex justify-between">
                    <view class="flex items-center">
                        <u-image
                            width="70"
                            height="70"
                            mode="scaleToFill"
                            border-radius="50%"
                            :src="comment?.user?.avatar"
                        />
                        <text class="ml-[20rpx]">{{ comment?.user?.nickname || '用户昵称' }}</text>
                    </view>
                    <view class="text-muted">{{ comment.create_time }}</view>
                </view>
                <view class="muted xs mt-[10rpx]">
                    <text>{{ comment?.order_goods?.spec_value_str?.join(',') }}</text>
                </view>
                <view class="mt-2 break-all">{{ comment.content }}</view>
                <view class="flex reviews-images" v-if="comment.images.length">
                    <u-image
                        class="mt-2"
                        v-for="(item, index) in comment.images"
                        :key="item"
                        :src="item"
                        width="155"
                        height="155"
                        mode="scaleToFill"
                        :class="{ 'mr-2': (index + 1) % 3 != 0 }"
                        @tap="previewImage(comment.images, index)"
                    />
                </view>
            </template>
            <view v-else class="text-[#999] text-center py-[26rpx] text-sm"> 暂无任何评论 </view>
        </view>
    </view>
</template>

<script lang="ts" setup>
defineProps({
    comment: {
        type: Object,
        default: () => ({})
    },
    goodsId: {
        type: [Number, String]
    }
})

// 查看评价图片
const previewImage = (images: string[], index: number) => {
    // 预览图片
    uni.previewImage({
        urls: images,
        current: index
    })
}
</script>

<style lang="scss" scoped>
.reviews {
    margin-top: 20rpx;

    &-header {
        padding: 24rpx;
    }

    &-content {
        padding: 24rpx;
        border-top: 1px solid $u-border-color;

        .evaluate-images {
            display: flex;
            flex-wrap: wrap;
        }
    }

    &-rate {
        color: $u-type-primary;
    }
}
</style>
