<template>
    <view class="comment-upon p-[20rpx]" :style="$theme.pageStyle">
        <!-- Main -->
        <view class="rounded-[10rpx] overflow-hidden">
            <!-- 商品信息 -->
            <goods-card shape="rectangle" class="flex-1" :image="goods?.goodsImage"
                :imageStyle="{ width: '180rpx', height: '180rpx' }" :name="goods?.goodsName"
                :containStyle="{ height: '220rpx', 'border-radius': '0' }">
                <view class="text-muted mt-[30rpx]">
                    {{ goods?.goodsSkuValue || '默认' }}
                </view>
            </goods-card>

            <!-- 商品评分 -->
            <view class="flex mt-[1px] py-[30rpx] px-[20rpx] bg-white">
                <text class="text-main text-base mr-[30rpx]">商品评分</text>
                <u-rate :count="5" v-model="formData.score" :min-count="1" inactive-icon="star-fill" size="34"></u-rate>
                <view class="ml-[30rpx] text-primary">
                    <text v-if="formData.score == 5">非常好</text>
                    <text v-if="formData.score == 4">好</text>
                    <text v-if="formData.score == 3">一般</text>
                    <text v-if="formData.score == 2">差</text>
                    <text v-if="formData.score == 1">非常差</text>
                </view>
            </view>

            <!-- 评价内容 -->
            <view class="p-[20rpx] mt-[1rpx] bg-white">
                <view class="content">
                    <u-input v-model="formData.content" type="textarea" placeholder="请输入评价内容" height="200" />
                </view>

                <view class="mt-[20rpx]">
                    <uploader v-model="formData.images" :maxUpload="6" image-fit="aspectFill" :deletable="true" />
                </view>
            </view>
        </view>

        <!-- Footer -->
        <view class="bg-white rounded-[10rpx] overflow-hidden p-[20rpx] mt-[20rpx]">
            <view class="font-medium">其它评价</view>
            <view class="flex mt-2">
                <text class="text-main text-base mr-[30rpx]">描述相符</text>
                <u-rate :count="5" v-model="formData.describeScore" :min-count="1" inactive-icon="star-fill"
                    size="34"></u-rate>
            </view>

            <view class="flex mt-2">
                <text class="text-main text-base mr-[30rpx]">服务态度</text>
                <u-rate :count="5" v-model="formData.serviceScore" :min-count="1" inactive-icon="star-fill"
                    size="34"></u-rate>
            </view>

            <view class="flex mt-2">
                <text class="text-main text-base mr-[30rpx]">配送服务</text>
                <u-rate :count="5" v-model="formData.logisticsScore" :min-count="1" inactive-icon="star-fill"
                    size="34"></u-rate>
            </view>
        </view>

        <!-- 提交 -->
        <view class="mt-[46rpx]">
            <button class="text-xl comment-btn full-primary bg-primary" @click="onSubmit">
                提交评价
            </button>
        </view>

        <!-- Component -->
        <page-status ref="pageRef"></page-status>
    </view>
</template>

<script lang="ts" setup>
import { ref, shallowRef, nextTick, unref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { commentAdd, orderGoodsDetail } from '@/api/order'
import GoodsAbnormalImage from '@/static/images/empty/goods.png'

interface CommentFormType {
    orderGoodsId: number // 订单商品id
    score: number // 分数 1~5
    content: string | number // 内容 最多255字符
    images: Array<string | null> // 图片 最多5张
    describeScore: number // 描述评分 1~5
    serviceScore: number // 服务评分 1~5
    logisticsScore: number // 物流评分 1~5
}

const pageRef = shallowRef()
const goods = reactive<any>({
    goodsId: '',
    goodsImage: '',
    goodsName: '',
    goodsSkuId: '',
    goodsSkuValue: '',
    id: '',
    userId: ''
})
const formData = reactive<CommentFormType>({
    orderGoodsId: 0,
    score: 0,
    content: '',
    images: [],
    describeScore: 0,
    serviceScore: 0,
    logisticsScore: 0
})

const onSubmit = async (): Promise<void> => {
    try {
        if (formData.score === 0) return uni.$u.toast('请选择评分')
        if (formData.describeScore === 0) return uni.$u.toast('请选择描述评分')
        if (formData.serviceScore === 0) return uni.$u.toast('请选择服务态度评分')
        if (formData.logisticsScore === 0) return uni.$u.toast('请选择配送服务评分')
        await commentAdd({ ...formData })
        uni.navigateBack()
    } catch (error) {
        console.log('提交评价: ', error)
    }
}

const initGoodsInfo = async (id: number): Promise<void> => {
    try {
        const data = await orderGoodsDetail({ id: id })
        Reflect.ownKeys(data).map((item: any) => {
            goods[item] = data[item]
        })
        unref(pageRef)?.close()
    } catch (error) {
        unref(pageRef)?.show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('获取订单商品详情接口', error)
    }
}

onLoad(async ({ id }: any) => {
    await nextTick()
    try {
        if (!id) {
            throw new Error('参数有误')
        }
        // 初始化获取订单商品信息
        await initGoodsInfo(id)
        formData.orderGoodsId = id
    } catch (error) {
        unref(pageRef).show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('发布评价', error)
    }
})
</script>

<style lang="scss" scoped>
.content {
    padding: 10rpx 24rpx;
    border-radius: 10rpx;
    background-color: $u-bg-color;
}

.comment-btn {
    border-radius: 60px;
    height: 84rpx;
    line-height: 84rpx;
}
</style>
