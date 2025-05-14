<template>
    <view
        :class="[
            'goods',
            { 'goods--square': shape === 'square' },
            { 'goods--rectangle': shape === 'rectangle' }
        ]"
        :style="containStyle"
    >
        <!-- 商品图片 -->
        <view class="goods-images" :style="imageStyle">
            <u-image width="100%" height="100%" :src="image" mode="scaleToFill" />
            <view class="goods-images-tips" v-if="goods?.buyAble == false">
                <span class="goods-images-text"> 不可购买 </span>
            </view>
        </view>
        <!-- 商品信息 -->
        <view class="goods-content" :style="contentStyle">
            <!-- 名称 -->
            <slot name="name">
                <view class="text-lg text-[#333] line-2">
                    {{ name }}
                </view>
            </slot>
            <slot>
                <!-- 价格 -->
                <view class="goods-other">
                    <view class="flex items-center">
                        <price
                            :content="price"
                            fontWeight="500"
                            mainSize="38rpx"
                            minorSize="24rpx"
                            class="mr-2"
                        />
                        <price
                            :content="minPrice"
                            v-if="minPrice != '0.00'"
                            mainSize="24rpx"
                            minorSize="24rpx"
                            color="#999999"
                            lineThrough
                        />
                    </view>
                </view>
            </slot>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
/**
 * @description 商品卡片
 * @property {String} shape 形状：square-正方形; rectangle-长方形 (默认: square)
 * @property {String} image 商品图片
 * @property {String} name 商品名称
 * @property {String|Number} price 商品价格
 * @property {String|Number|Boolean} minPrice 商品划线价格 (默认：false)
 * @property {String} imageStyle 图片样式
 * @property {String} containStyle 卡片样式
 * @example <goods-card shape="rectangle" name="Muze" price="100" minPrice="120" />
 */
defineProps({
    // square -正方形 | rectangle -长方形
    shape: {
        type: String,
        default: 'square'
    },
    // 商品图片
    image: {
        type: String,
        default: ''
    },
    // 商品名称
    name: {
        type: String,
        default: ''
    },
    // 价格
    price: {
        type: [String, Number],
        default: 0
    },
    // 划线价
    minPrice: {
        type: [String, Number, Boolean],
        default: false
    },

    // 卡片样式
    containStyle: {
        type: Object,
        default: () => {}
    },

    // 图片样式
    imageStyle: {
        type: Object,
        default: () => {}
    },

    // 内容样式
    contentStyle: {
        type: Object,
        default: () => {}
    },

    goods: {
        type: Object,
        default: () => {}
    }
})
</script>

<style lang="scss" scoped>
.goods {
    display: flex;
    border-radius: 7px;
    background-color: #ffffff;
    overflow: hidden;

    &--square {
        flex-direction: column;
        width: 347rpx;
        height: 510rpx;

        .goods-images {
            width: 345rpx;
            height: 345rpx;
            position: relative;
        }

        .goods-content {
            padding: 14rpx;
        }
    }

    &--rectangle {
        flex-direction: row;
        align-items: center;
        height: 250rpx;
        padding: 20rpx;

        .goods-images {
            box-sizing: border-box;
            width: calc(250rpx - 2 * 20rpx);
            height: calc(250rpx - 2 * 20rpx);
            // border-radius: 7px;
            overflow: hidden;
            position: relative;
        }

        .goods-content {
            margin-left: 20rpx;
        }
    }

    &-content {
        box-sizing: border-box;
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        height: 100%;

        .goods-other {
            flex: 1;
            display: flex;
            flex-direction: column;
            // justify-content: flex-end;
        }
    }
}

.goods-images-tips {
    position: absolute;
    top: 0;
    width: 100%;
    height: 100%;
    text-align: center;
    line-height: 6;
    z-index: 9;
    background-color: rgba(0, 0, 0, 0.5);
}
.goods-images-text {
    padding: 10rpx;
    border-radius: 5rpx;
    color: white;
}
</style>
