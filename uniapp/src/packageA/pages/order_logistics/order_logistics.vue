<template>
    <view class="order-logistics">
        <view class="express">
            <!-- 商品图片 -->
            <view class="w-[160rpx] h-[160rpx] relative">
                <u-image
                    width="100%"
                    height="100%"
                    mode="scaleToFill"
                    :src="logistics?.goodsImage"
                />
                <view class="absolute w-full text-xs text-center bottom-1">
                    <text
                        class="rounded-[20px] bg-black bg-opacity-60 text-white px-[20rpx] py-[5rpx]"
                        >共{{ logistics.orderGoodsNum }}件商品</text
                    >
                </view>
            </view>
            <!-- 快递信息 -->
            <view class="flex flex-col ml-[20rpx]">
                <text class="text-lg font-medium">{{ logistics.orderStatusMsg }}</text>
                <text class="text-sm mt-[10rpx]"
                    >物流公司：{{ logistics.expressName || '--' }}</text
                >
                <view class="flex text-sm mt-[10rpx]">
                    <text>快递单号：{{ logistics.expressNo || '--' }}</text>
                    <template v-if="logistics.expressNo">
                        <view
                            class="ml-[30rpx] text-primary"
                            @click.stop="copy(logistics.expressNo)"
                            >复制</view
                        >
                    </template>
                </view>
            </view>
        </view>

        <view class="logistics mt-[20rpx]">
            <u-time-line>
                <!-- 收货地址 -->
                <u-time-line-item>
                    <template #node>
                        <image class="w-[40rpx] h-[40rpx]" :src="Order0" />
                    </template>
                    <template #content>
                        <view class="node-title">
                            <text class="text-[#222]">{{ logistics.contact }}</text>
                            <text class="text-[#222] ml-[20rpx]">
                                {{ logistics.mobile }}
                            </text>
                        </view>
                        <view class="node-desc">{{ logistics.address }}</view>
                    </template>
                </u-time-line-item>

                <!-- 交易完成 -->
                <u-time-line-item v-if="logistics.orderStatus === 4">
                    <template #node>
                        <image class="w-[40rpx] h-[40rpx]" :src="Order4" />
                    </template>
                    <template #content>
                        <view class="node-title">{{ '交易完成' }}</view>
                        <view class="node-desc">{{ '订单交易完成' }}</view>
                        <view class="node-time">{{ logistics.confirmTime }}</view>
                    </template>
                </u-time-line-item>

                <!-- S 运输 -->
                <u-time-line-item v-if="logistics.track">
                    <template #node>
                        <image class="w-[40rpx] h-[40rpx]" :src="Order3" />
                    </template>
                    <template #content>
                        <view class="node-title">{{ '运输中' }}</view>
                    </template>
                </u-time-line-item>
                <block v-for="(item, index) in logistics.track" :key="index">
                    <u-time-line-item>
                        <template #content>
                            <view class="node-desc">{{ item.content }}</view>
                            <view class="node-time">{{ item.time }}</view>
                        </template>
                    </u-time-line-item>
                </block>
                <!-- E 运输 -->

                <!-- 发货 -->
                <u-time-line-item v-if="logistics.orderStatus >= 2">
                    <template #node>
                        <image class="w-[40rpx] h-[40rpx]" :src="Order2" />
                    </template>
                    <template #content>
                        <view class="node-title">{{ '已发货' }}</view>
                        <view class="node-desc">{{ '商品已出库' }}</view>
                        <view class="node-time">{{ logistics.expressTime }}</view>
                    </template>
                </u-time-line-item>

                <!-- 下单 -->
                <u-time-line-item>
                    <template #node>
                        <image class="w-[40rpx] h-[40rpx]" :src="Order1" />
                    </template>
                    <template #content>
                        <view class="node-title">{{ '已下单' }}</view>
                        <view class="node-desc">{{ '订单提交成功' }}</view>
                        <view class="node-time">{{ logistics.createTime }}</view>
                    </template>
                </u-time-line-item>
            </u-time-line>
        </view>

        <!-- Component -->
        <page-status ref="pageRef"></page-status>
    </view>
</template>

<script lang="ts" setup>
import { orderLogistics } from '@/api/order'
import { useCopy } from '@/hooks/useCopy'
import { reactive, shallowRef, nextTick, unref } from 'vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import GoodsAbnormalImage from '@/static/images/empty/goods.png'
import Order0 from '../../static/logistics/order_0.png'
import Order1 from '../../static/logistics/order_1.png'
import Order2 from '../../static/logistics/order_2.png'
import Order3 from '../../static/logistics/order_3.png'
import Order4 from '../../static/logistics/order_4.png'

const pageRef = shallowRef()
const { copy } = useCopy()
const logistics = reactive<any>({
    orderId: '', // 订单ID
    contact: '', // 收货人名称
    address: '', // 收货人地址
    mobile: '', // 收货人手机号
    createTime: '', // 下单时间
    expressTime: '', // 发货时间（商品出库）
    express_confirm_time: '', // 完成时间
    expressNo: '', // 快递单号
    expressName: '', // 快递公司名称
    goodsImage: '', // 商品图片
    orderGoodsNum: 0, // 商品数量
    orderStatus: 0, // 订单状态
    orderStatusMsg: '', // 状态描述文字
    track: [] // 运输中
})

const initLogisticsData = async () => {
    try {
        const data = await orderLogistics({
            id: logistics.orderId
        })
        Reflect.ownKeys(data).map((item: any) => {
            logistics[item] = data[item]
        })
        unref(pageRef).close()
    } catch (error) {
        console.log(error)
        unref(pageRef)?.show({
            text: error,
            src: GoodsAbnormalImage
        })
    }
}

onLoad(async ({ id }) => {
    await nextTick()
    try {
        if (!id) throw new Error('参数错误')
        logistics.orderId = id
        await initLogisticsData()
    } catch (error) {
        console.log(error)
        unref(pageRef).show({
            text: error,
            src: GoodsAbnormalImage
        })
    }
})
</script>

<style lang="scss" scoped>
.order-logistics {
    padding: 20rpx;
}

.express {
    display: flex;
    align-items: center;
    height: 200rpx;
    padding: 20rpx 24rpx;
    border-radius: 14rpx;
    background-color: #ffffff;
}

.logistics {
    border-radius: 14rpx;
    background-color: #ffffff;
    padding: 30rpx 24rpx 30rpx calc(24rpx + 40rpx / 2);

    .node-title {
        font-weight: 500;
        font-size: 28rpx;
        color: $u-main-color;
    }

    .node-desc {
        margin-bottom: 6rpx;
        font-size: 26rpx;
        color: $u-content-color;
    }

    .node-time {
        color: $u-tips-color;
        font-size: 26rpx;
    }
}
</style>
