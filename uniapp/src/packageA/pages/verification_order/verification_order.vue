<template>
    <view class="" :style="$theme.pageStyle">
        <view class="card mx-[10rpx]">
            <view class="flex justify-between">
                <text class="text-xs"> 订单编号：{{orderInfo.orderSn}} </text>
                <text :style="{ color: $theme.primaryColor }" class="text-xs"> {{orderInfo.verificationStatusStr}} </text>
            </view>
			<view v-for="(item, index) in orderInfo?.orderGoodsList">
				<view class="mt-[30rpx] flex w-full" v-if="item.verificationStatus == 1">
					<u-image
						width="150rpx"
						height="150rpx"
						:src="item.goodsImage"
					></u-image>
					<view class="ml-[20rpx] flex-1 flex flex-col justify-between">
						<view>{{item.goodsName}}</view>
						<view class="text-xs text-muted flex">
							<view>
								<text> {{item.goodsSkuValue}} </text>
								<text class="ml-[30rpx]"> x{{item.goodsNum}} </text>
							</view>
						</view>
						<view class="text-xs">核销时间：{{orderInfo.verificationTimeStr}}</view>
					</view>
				</view>
			</view>
			<view class="text-xs mt-[20rpx]">
				<view> 姓名：{{orderInfo.addressContact}} </view>
				<view class="mt-[20rpx]"> 联系号码：{{orderInfo.addressMobile}} </view>
			</view>
        </view>
    </view>
</template>
<script lang="ts" setup>
import { ref, reactive, computed, unref, shallowRef, nextTick, watchEffect } from 'vue'
import { selffetchDetail } from "@/api/order"
import { onLoad, onUnload } from '@dcloudio/uni-app'
const id = ref()
const isCheckAgreement = ref(false)
const orderInfo = ref({})
const getSelffetchDetail = async() => {
	try {
		let ret = await selffetchDetail({id: id.value})
		orderInfo.value = ret
	} catch (err) {
		uni.navigateBack({
			delta: 1
		})
	}
}
onLoad(async (options: any) => {
    await nextTick()

    try {
        if (!options.id) {
            throw new Error('参数有误')
        }
		id.value = options.id
		getSelffetchDetail()
    } catch (error) {
		uni.navigateBack({
			delta: 1
		})
    }
})
</script>
<style lang="scss" scoped>
.card {
    margin-top: 20rpx;
    background-color: white;
    border-radius: 14rpx;
    padding: 20rpx;
}
.btn {
    height: 70rpx;
    margin-bottom: env(safe-area-inset-bottom);
    background: linear-gradient(90deg, var(--theme-dark-color) 0%, var(--theme-color) 100%);
    color: white;
    width: 100%;
}
</style>
