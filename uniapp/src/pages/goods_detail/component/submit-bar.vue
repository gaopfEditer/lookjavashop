<template>
    <!-- 底部--商品状态栏 -->
    <view class="status-bar text-center text-white p-[24rpx]" v-if="goods.status === 0">
        <text>抱歉，该商品已下架~</text>
    </view>
    <!-- 底部-----提交栏 -->
    <view class="submit-bar">
        <view class="icon-group text-xs text-[#999]">
            <navigator class="icon-item" url="/packageA/pages/customer_service/customer_service">
                <u-icon name="kefu-ermai" size="42" />
                <text>客服</text>
            </navigator>
            <view class="icon-item" :class="{ 'text-primary': goods.isCollect }" @tap="handleCollect">
                <template v-if="goods.isCollect">
                    <u-icon name="heart-fill" size="42" />
                </template>
                <template v-else>
                    <u-icon name="heart" size="42" />
                </template>
                <text>收藏</text>
            </view>
            <navigator v-if="goodsType == GoodsTypeEnum.ORDINARY" class="icon-item" @click="gotoShopCart">
                <u-badge :count="userStore.shopCartCount" :offset="[-10, -10]"></u-badge>
                <u-icon name="shopping-cart" size="42" />
                <text>购物车</text>
            </navigator>
        </view>
        <view class="button-group">
            <view class="button-item bg-primary" :class="{ 'button-join': buttonsLen == 2 }"
                v-for="(item, index) in buttons" :key="index" :style="[item.style]" @click="onButtonHandle(item.event)">
                {{ item.text }}
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { GoodsTypeEnum } from '@/enums/goodsEnums'
import { goodsCollect } from '@/api/goods'
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const emit = defineEmits(['refresh', 'buttonsHandle'])
const props = withDefaults(
    defineProps<{
        goods?: any
        goodsType: string
        buttons: any[]
    }>(),
    {
        goods: {},
        goodsType: GoodsTypeEnum.ORDINARY,
        buttons: []
    }
)

const buttonsLen = computed(() => Reflect.ownKeys(props.buttons).length)

const handleCollect = async () => {
    try {
        await goodsCollect({ id: props.goods.id })
        await emit('refresh')
    } catch (error) {
        console.log('商品收藏', error)
    }
}

const onButtonHandle = (event: string | any) => {
    emit('buttonsHandle', {
        event: event
    })
}

const gotoShopCart = () => {
	uni.switchTab({
		url: '/pages/shop_cart/shop_cart'
	})
}
</script>

<style lang="scss" scoped>
.status-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: calc(100rpx + env(safe-area-inset-bottom));
    background-color: rgba($color: #000000, $alpha: 0.6);
}

.submit-bar {
    position: fixed;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    align-items: center;
    height: calc(100rpx + env(safe-area-inset-bottom));
    padding: 0 24rpx env(safe-area-inset-bottom) 24rpx;
    box-sizing: 1px 0 4px rgba(#000, 0.2);
    background-color: #ffffff;

    .icon-group {
        display: flex;
        justify-content: space-around;
        //min-width: 268rpx;

        .icon-item {
            width: 90rpx;
            position: relative;
            display: flex;
            flex-direction: column;
            align-items: center;
        }
    }

    .button-group {
        flex: 1;
        display: flex;
        justify-content: space-between;

        // 链接按钮
        .button-join:first-child {
            border-radius: 60px 0 0 60px;
        }

        .button-join:last-child {
            margin-left: 0;
            border-radius: 0 60px 60px 0;
        }

        .button-item {
            flex: 1;
            box-sizing: border-box;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 74rpx;
            margin-left: 14rpx;
            border-radius: 60px;
            color: #ffffff;
            border: none;
        }
    }
}
</style>
