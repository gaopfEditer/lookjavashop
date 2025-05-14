<template>
    <view class="goods-detail" :style="$theme.pageStyle">
        <!-- 导航栏 -->
        <navbar
            title="商品详情"
            :background="{ background: `rgba(256,256,256,${goods.percent})` }"
            :titleColor="`rgba(0,0,0,${goods.percent})`"
            :immersive="true"
        ></navbar>

        <!-- 轮播图 -->
        <goods-swiper
            :images="goods.goodsImage"
            :video="goods.video"
            :video-cover="goods.videoCover"
        />

        <!-- 商品内容 -->
        <view class="rounded-[14rpx] overflow-hidden bg-white m-[20rpx]">
            <!-- 限时秒杀 -->
            <activity-view :activity="activity" v-if="GoodsTypeEnum.SECKILL == goodsPayload.type">
                <template #price>
                    <price
                        :content="activity.minSeckillPrice"
                        main-size="46rpx"
                        minor-size="28rpx"
                        prefix="秒杀价￥"
                        color="#FFFFFF"
                    />
                </template>
            </activity-view>

            <view class="p-[20rpx]">
                <!-- 价格 -->
                <view class="pb-[16rpx] flex justify-between items-center">
                    <view>
                        <price
                            :content="goods.sellPrice"
                            main-size="46rpx"
                            minor-size="28rpx"
                            :suffix="goods.specType == 2 ? '起' : ''"
                        />
                        <price
                            class="pl-[12rpx]"
                            mainSize="28rpx"
                            :line-through="true"
                            :content="goods.checkedSku.linePrice"
                            color="#999"
                            :suffix="goods.specType == 2 ? '起' : ''"
                            v-show="Number(goods.checkedSku.linePrice)"
                        />
                    </view>

                    <view class="text-center ml-[30rpx]" @click="showShare = true">
                        <image
                            src="@/static/images/goods/icon_share.png"
                            class="w-[36rpx] h-[36rpx]"
                        />
                    </view>
                </view>

                <!-- 商品领券 -->
                <coupon-com
                    :goods-id="goods.id"
                    v-if="goodsPayload.type === GoodsTypeEnum.ORDINARY"
                ></coupon-com>

                <!-- 商品名称与分享 -->
                <view class="flex items-start justify-between">
                    <view class="flex-1 text-lg font-medium">{{ goods.name }}</view>
                    <!-- <view class="text-center ml-[30rpx]" @click="showShare = true">
            <image
              src="@/static/images/goods/icon_share.png"
              class="w-[36rpx] h-[36rpx]"
            />
            <view class="text-sm text-[#707070]">分享</view>
          </view> -->
                </view>

                <!-- 商品简介 -->
                <!-- <view
                    v-if="goods.description"
                    class="pt-[18rpx] pb-[30rpx] text-sm text-[#666]"
                >
                    {{ goods.description }}
                </view> -->

                <!-- 商品销售数据 -->
                <view
                    class="flex justify-between text-xs text-[#999] py-[10px]"
                    v-if="goods?.decorate[1]?.content.show_conmment"
                >
                    <text v-if="goods?.decorate[1]?.content.show_sku_stock == 1">
                        库存： {{ goods.totalStock }}
                    </text>
                    <text v-if="goods?.decorate[1]?.content.show_goods_sales">
                        销量： {{ activity.salesVolume || goods.salesNum }}
                    </text>
                    <text v-if="goods?.decorate[1]?.content.show_goods_visit">
                        浏览量： {{ activity.browseVolume || goods.clickNum }}
                    </text>
                </view>
            </view>
        </view>
        <!-- 佣金 -->
        <Earning
            v-model="showShare"
            :content="goods.earningsData"
            v-if="goodsPayload.type == GoodsTypeEnum.ORDINARY"
        ></Earning>
        <!-- 配送规格信息 -->
        <view class="rounded-[14rpx] overflow-hidden bg-white m-[20rpx] pb-[18rpx]">
            <!-- 规格 -->
            <view
                class="flex justify-between text-base px-[30rpx] py-[26rpx]"
                @click="buttonsHandle(confirmSpecButtons)"
            >
                <view class="flex-1 flex">
                    <text class="text-muted pr-4">选择</text>
                    <view class="flex-1 truncate pr-4 text-black">
                        <text>{{ goods.checkedSku.skuValueArr || '默认规格' }}</text>
                        <text class="ml-3">{{ goods.checkedSku.number }}件</text>
                    </view>
                </view>
                <u-icon name="arrow-right" color="#707070"></u-icon>
            </view>

            <!-- 配送区域 -->
            <RegionalDistribution
                :defaultFreight="goods.defaultFreight"
                :defaultAddress="goods.defaultAddress"
                @refresh="getDetail"
            ></RegionalDistribution>
        </view>

        <!-- 装修DIY -->

        <!-- 商品评论 -->
        <template v-if="goods.decorate[3]?.content.show_evaluate == 1">
            <reviews :comment="goods.goodsComment" :goodsId="goods.id" />
        </template>
        <!-- 猜你喜欢 -->
        <template v-if="goods.decorate[4]?.content.enabled == 1">
            <goods-recommend :content="goods.goods_like" />
        </template>

        <!-- 商品详情 -->
        <view class="break-all rich__text px-[24rpx]">
            <view class="goods-detail">
                <view class="goods-detail-content">商品详情</view>
            </view>
            <u-parse class="rich__text" :html="goods.content"></u-parse>
        </view>

        <!-- 规格 -->
        <GoodsSpec
            ref="goodsSpecRef"
            :defaultInfo="{
                image: goods.goodsImage[0],
                price: goods.sellPrice,
                stock: goods.totalStock,
                unit: goods.unit_name
            }"
            :buttons="goods.buttons"
            :spec-list="goods.specValue"
            :spec-map="goods.specValueList"
            :activity-spec="activity.goodsSku"
            priceKey="price"
            @change="changeGoodsSpec"
            @buttonsHandle="buttonsHandle"
        />

        <!-- 提交导航栏 -->
        <SubmitBar
            :goods="goods"
            :goodsType="goodsPayload.type"
            :buttons="goods.buttonsGroup"
            @refresh="getDetail"
            @buttonsHandle="buttonsHandle"
        ></SubmitBar>

        <!-- 分享 -->
        <SharePopup
            v-model="showShare"
            :share-id="goods.id"
            pagePath="pages/goods_detail/goods_detail"
            :payload="goodsPayload"
            :options="{
                name: goods.name,
                image: goods.goodsImage[0],
                price: goods.sellPrice,
                linePrice: goods.linePrice
            }"
        />

        <!-- 页面状态 -->
        <PageStatus ref="pageRef"></PageStatus>

        <!-- 返回顶部按钮 -->
        <u-back-top
            :scroll-top="goods.scrollTop"
            :top="1000"
            :customStyle="{
                backgroundColor: '#FFF',
                color: '#000',
                boxShadow: '0px 3px 6px rgba(0, 0, 0, 0.1)'
            }"
        >
        </u-back-top>
    </view>
</template>

<script lang="ts" setup>
import { nextTick, reactive, ref, shallowRef, unref } from 'vue'
import { onLoad, onPageScroll, onShareAppMessage } from '@dcloudio/uni-app'
import { getGoodsDetail, getGoodsLimitLists } from '@/api/goods'
import { getDecorate } from '@/api/shop'
import { seckillDetaill } from '@/api/marketing/seckill'
import { strToParams } from '@/utils/util'
import { GoodsTypeEnum } from '@/enums/goodsEnums'
import { SubmitEventEnum, changeSpecButtons, confirmSpecButtons } from '@/pages/goods_detail/config'
import { useSubmitEffect } from '@/pages/goods_detail/useSubmitEffect'
import { useUserStore } from '@/stores/user'

import GoodsAbnormalImage from '@/static/images/empty/goods.png'
import GoodsSwiper from '@/components/goods-swiper/goods-swiper.vue'
import ActivityView from '@/pages/goods_detail/component/activity-view.vue'
import CouponCom from '@/pages/goods_detail/component/coupon-popup.vue'
import GoodsSpec from '@/components/goods-spec/goods-spec.vue'
import RegionalDistribution from '@/pages/goods_detail/component/regional-distribution.vue'
import Reviews from '@/pages/goods_detail/component/reviews.vue'
import GoodsRecommend from '@/pages/goods_detail/component/goodsrecom.vue'
import SubmitBar from '@/pages/goods_detail/component/submit-bar.vue'
import SharePopup from '@/components/share-popup/share-popup.vue'
import PageStatus from '@/components/page-status/page-status.vue'
import Earning from '@/pages/goods_detail/component/earning.vue'

const pageRef = shallowRef()
const goodsSpecRef = shallowRef()
const showShare = ref<boolean>(false)
// 商品详情相关的参数
const goods = reactive<any>({
    id: -1,
    // 顶部导航栏
    percent: '',
    // 返回顶部按钮的当前滚动条数值
    scrollTo: 0,
    // 轮播图
    goodsImage: [],
    // 已选择的商品sku
    checkedSku: {},
    // 配送区域
    defaultAddress: {
        addressId: 0,
        provinceName: '北京',
        cityName: '北京市',
        districtName: '朝阳区'
    },
    // 配送费
    defaultFreight: 0,
    // 是否收藏
    isCollect: 0,
    // 当前选中的底部按钮
    buttons: [],
    // 底部提交按钮组
    buttonsGroup: {},
    // 商品所属分类
    categoryId: [],
    // 装修数据
    decorate: [],
    // 评论数据
    goodsComment: [],
    // 猜你喜欢列表
    goods_like: [],
    //佣金
    earningsData: {}
})

// 商品承载
const goodsPayload = ref<any>({
    type: GoodsTypeEnum.ORDINARY,
    activityId: ''
})
// 活动
const activity = reactive<any>({
    goodsId: '',
    startTime: '',
    minSeckillPrice: '',
    goodsSku: [],
    checkedSku: {},
    browseVolume: '',
    salesVolume: ''
})

const getDetail = async () => {
    try {
        const data = await getGoodsDetail({ id: goods.id })
        Reflect.ownKeys(data).map((item: any) => {
            goods[item] = data[item]
        })
        unref(pageRef).close()
    } catch (error) {
        unref(pageRef)?.show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('获取商品详情接口', error)
    }
}

const getGoodsDecorate = async () => {
    try {
        const data = await getDecorate({ id: 5 })
        goods.decorate = JSON.parse(data.pages)
        if (!goods.decorate[4].content.enabled) return
        const good_num = goods.decorate[4].content.goods_num
        console.log('获取猜你喜欢列表')

        goods.goods_like = await getGoodsLimitLists({
            type: 'like',
            limit: good_num,
            categoryId: goods.categoryId.toString()
        })
    } catch (error) {
        console.log('猜你喜欢列表获取失败', error)
    }
}

const getActivityFun = async () => {
    try {
        let resultData: any = {}
        switch (goodsPayload.value.type * 1) {
            // 秒杀商品
            case GoodsTypeEnum.SECKILL:
                resultData = await seckillDetaill({
                    id: goodsPayload.value.activityId
                })
                break
        }
        Reflect.ownKeys(resultData).map((item: any) => {
            activity[item] = resultData[item]
        })
    } catch (error) {
        console.log('活动请求失败', error)
    }
}

const onGoodsSpecOpen = ($event: any, event: string) => {
    goodsSpecRef.value?.handleOpen({ ...$event })
    goods.buttons = [goods.buttonsGroup[event] ?? $event]
}

const changeGoodsSpec = (options: any) => {
    console.log(options)
    goods.checkedSku = options.spec
    activity.checkedSku = options.activitySpec
}

// 处理按钮组事件
const buttonsHandle = async (options: any) => {
    switch (options.event) {
        case SubmitEventEnum.CONFIRM:
            onGoodsSpecOpen(confirmSpecButtons, options.event)
            break
        case SubmitEventEnum.CART:
            onGoodsSpecOpen(null, options.event)
            break
        case SubmitEventEnum.BUY:
            onGoodsSpecOpen(null, options.event)
            break
        case SubmitEventEnum.BUY_NOW:
            onGoodsSpecOpen({ priceKey: 'seckillPrice' }, options.event)
            break
        case SubmitEventEnum.CART_HANDLE:
            const { cartHandle } = useSubmitEffect()
            await cartHandle(options)
            await getDetail()
            break
        case SubmitEventEnum.BUY_HANDLE:
            const { buyHandle } = useSubmitEffect()
            await buyHandle(options, goods.id)
            break
        case SubmitEventEnum.BUY_NOW_HANDLE:
            const { buyNowHandle } = useSubmitEffect()
            await buyNowHandle(options, goods.id, activity.seckillId)
            break
    }
}

onLoad(async ({ scene, id, payload = { type: GoodsTypeEnum.ORDINARY } }: any) => {
    await nextTick()
    try {
        if (!scene) {
            goods.id = id
            goodsPayload.value = payload.type ? payload : JSON.parse(payload)
        } else {
            const param = strToParams(decodeURIComponent(scene))
            console.log('param', param)
            goods.id = param.id
            // 承载商品拓展类型(t = goodsType, aid = activityId,
            goodsPayload.value.type = param.t || GoodsTypeEnum.ORDINARY
            goodsPayload.value.activityId = param.aid || ''
        }
        if (!goods.id) {
            throw new Error('请传入商品ID')
        }
        // 不是普通商品时
        if (goodsPayload.value.type != GoodsTypeEnum.ORDINARY) {
            await getActivityFun()
        }
        goods.buttonsGroup = (changeSpecButtons as any)[goodsPayload.value.type]
        await getDetail()
        await getGoodsDecorate()
    } catch (error) {
        unref(pageRef).show({
            text: error,
            src: GoodsAbnormalImage
        })
        console.log('商品详情报错', error)
    }
})

onPageScroll(({ scrollTop }: any) => {
    const top = uni.upx2px(100)
    goods.percent = scrollTop / top > 1 ? 1 : scrollTop / top
    goods.scrollTop = scrollTop
})

onShareAppMessage(() => {
    const userStore = useUserStore()
    return {
        title: goods.name,
        path: `/pages/goods_detail/goods_detail?id=${goods.id}&payload=${JSON.stringify(
            goodsPayload.value
        )}&share_code=${userStore?.userInfo?.code || ''}`,
        imageUrl: goods.goodsImage[0]
    }
})
</script>

<style lang="scss">
.goods-detail {
    // 此样式是在海报中的弹窗生效，share-popup
    // 因为小程序的无能，所以只能写在页面中
    ::v-deep .u-mode-center-box {
        background: none !important;
    }

    ::v-deep .u-drawer-top {
        background: none !important;
    }

    // 此样式是在详情富文本生效，mp-html
    ::v-deep ._img {
        display: block;
    }

    padding-bottom: calc(100rpx + 20rpx + env(safe-area-inset-bottom));

    .goods-detail {
        padding: 30rpx 0;
        text-align: center;

        &-content {
            display: inline-flex;
            align-items: center;
            font-weight: bold;
            font-size: 32rpx;
            color: #333;

            &::before {
                content: '';
                display: inline-block;
                width: 98rpx;
                height: 1px;
                margin: 0 20rpx;
                background: #dcdfe6;
            }

            &::after {
                content: '';
                display: inline-block;
                width: 98rpx;
                height: 1px;
                margin: 0 20rpx;
                background: #dcdfe6;
            }
        }
    }
}
</style>
