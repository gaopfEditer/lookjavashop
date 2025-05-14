<template>
    <view
        class="hot mx-[30rpx] my-[20rpx] bg-white rounded-[20rpx] overflow-hidden"
        v-if="icontent.enabled == 1"
    >
        <view
            class="hot-header flex justify-between text-white items-center py-[26rpx] px-[20rpx] mb-2"
            :style="{
                        backgroundImage:
                          icontent.bg_type == 1
                            ? `url(${getImageUrl('api/static/hot_bg.png')})`
                            : `url(${getImageUrl(icontent.bg_url)})`,

            }"
        >
            <view class="">
                <view class="text-lg font-medium">{{ icontent.title }}</view>
                <view class="mt-[12rpx] text-sm">{{ icontent.sub_title }}</view>
            </view>
            <router-navigate
                to="/packageA/pages/activity_list/activity_list?sort=hot"
                hover-class="none"
            >
                <view class="more-btn flex items-center rounded-2xl text-xs px-[20rpx] py-[6rpx]">
                    <span class="mt-[4rpx]">更多</span>
                    <u-icon name="arrow-right" size="22" color="#FFFFFF"></u-icon>
                </view>
            </router-navigate>
        </view>
        <!-- 纵向模式 -->
        <template v-if="icontent.type == 1">
            <block v-for="(item, index) in content" :key="index">
                <navigator
                    class="goods-list flex p-[10px]"
                    :url="`/pages/goods_detail/goods_detail?id=${item.id}`"
                    hover-class="none"
                    v-if="index < icontent.num"
                >
                    <view class="w-[180rpx] h-[180rpx] relative">
                        <image
                            :src="imageArr[index]"
                            v-if="index <= 3"
                            class="w-[24px] h-[30px] absolute left-0 top-0 z-10"
                            alt="排名"
                        />
                        <u-image
                            :src="item?.image"
                            :width="180"
                            :height="180"
                            :preview-teleported="true"
                            fit="contain"
                        />
                    </view>
                    <view class="ml-[10rpx] flex-1">
                        <view class="line-2 w-[450rpx] min-h-[80rpx]">{{ item?.name }}</view>
                        <view class="flex items-end justify-between">
                            <view class="mt-[5rpx]">
                                <view class="buy-num-btn text-xs px-[10rpx] py-[2rpx] rounded-2xl">
                                    已经有 {{ item?.salesNum }}人购买
                                </view>
                                <view class="text-xs mt-[5rpx]">
                                    <price
                                        :content="item.price"
                                        fontWeight="500"
                                        mainSize="38rpx"
                                        minorSize="24rpx"
                                        class="mr-2"
                                    />
                                    <price
                                        :content="item.lineationPrice"
                                        v-if="item.lineationPrice != '0.00'"
                                        mainSize="24rpx"
                                        minorSize="24rpx"
                                        color="#999999"
                                        lineThrough
                                    />
                                </view>
                            </view>
                            <view
                                class="buy-btn bg-primary text-sm text-white px-[30rpx] py-[10rpx] rounded-full h-[56rpx]"
                            >
                                立即抢
                            </view>
                        </view>
                    </view>
                </navigator>
            </block>
        </template>
        <!-- 横向模式 -->
        <template v-if="icontent.type == 2">
            <scroll-view
                class="nowrap px-[14rpx] pb-[20rpx] box-border"
                :scroll-x="true"
                :enhanced="true"
                :show-scrollbar="false"
                :enable-passive="true"
                :scroll-with-animation="false"
            >
                <view
                    v-for="(item, index) in content"
                    :key="item"
                    class="w-[208rpx] mr-[14rpx] inline-block"
                >
                    <router-navigate
                        v-if="index < icontent.num"
                        :to="`/pages/goods_detail/goods_detail?id=${item.id}`"
                        hover-class="none"
                    >
                        <u-image
                            :src="item?.image"
                            width="208"
                            height="208"
                            border-radius="10"
                        ></u-image>
                        <view class="mt-[10rpx] truncate">{{ item?.name }}</view>
                        <view>
                            <price
                                :content="item.price"
                                fontWeight="500"
                                mainSize="38rpx"
                                minorSize="24rpx"
                                class="mr-2"
                            />
                            <price
                                :content="item.lineationPrice"
                                v-if="item.lineationPrice != '0.00'"
                                mainSize="24rpx"
                                minorSize="24rpx"
                                color="#999999"
                                lineThrough
                            />
                        </view>
                    </router-navigate>
                </view>
            </scroll-view>
        </template>
    </view>
</template>

<script lang="ts" setup>
import No1 from './images/No.1.png'
import No2 from './images/No.2.png'
import No3 from './images/No.3.png'
import No4 from './images/No.4.png'
import { useAppStore } from '@/stores/app'
const { getImageUrl } = useAppStore()
const props = defineProps({
    content: {
        type: Object,
        default: () => ({})
    },
    icontent: {
        type: Object,
        default: () => ({})
    }
})

const imageArr = [No1, No2, No3, No4]
</script>

<style lang="scss" scoped>
.hot {
    &-header {
        // background: url(./images/hot_bg.png) no-repeat;
        background-size: 100% 100%;
    }

    .more-btn {
        background-color: rgba($color: #ffffff, $alpha: 0.4);
    }

    .buy-num-btn {
        color: #f79c0c;
        background-color: rgba($color: #f79c0c, $alpha: 0.1);
    }

    // .buy-btn {
    //     background: linear-gradient(90deg, #f95f2f 0%, #ff2c3c 100%);
    // }
    .nowrap {
        white-space: nowrap;
        width: 100%;
    }
}
</style>
