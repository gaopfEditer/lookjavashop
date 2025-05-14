<template>
	<view class="safe-area-inset-bottom" :style="$theme.pageStyle">
		<view class="card mx-[10rpx]">
			<view class="flex justify-between">
				<text class="text-xs"> 订单编号：{{orderInfo.orderSn}} </text>
				<text :style="{ color: $theme.primaryColor }" class="text-xs"> {{orderInfo.verificationStatusStr}}
				</text>
			</view>

			<!-- <u-checkbox-group v-model="checkboxValue" placement="column" @change="checkboxChange"> -->
			<view class="mt-[30rpx] flex w-full" v-for="(item, index) in unverifiedList"
				@click.stop="handleSelect(item)">
				<u-image width="150rpx" height="150rpx" :src="item.goodsImage"></u-image>
				<view class="ml-[20rpx] flex-1 flex flex-col justify-between">
					<view>{{item.goodsName}}</view>
					<view class="text-xs text-muted flex justify-between">
						<view>
							<text> {{item.goodsSkuValue}} </text>
							<text> x{{item.goodsNum}} </text>
						</view>
						<l-checkbox :checked="item.selected" label="" :true-label="1" :false-label="0"
							:active-color="$theme.primaryColor" v-if="item.disabled == false" />
						<!-- <u-checkbox  :key="index" :label="item.id" :name="item.id"> </u-checkbox> -->
					</view>
					<view :class="item.afterSalesMsg == '' ? 'text-xs' : 'text-xs text-orange'">{{item.afterSalesMsg || "无售后"}}</view>
				</view>
			</view>
			<!-- </u-checkbox-group> -->
			<view class="text-xs mt-[20rpx]">
				<view> 姓名：{{orderInfo.addressContact}} </view>
				<view class="mt-[20rpx]"> 联系号码：{{orderInfo.addressMobile}} </view>
			</view>
		</view>


		<view class="flex mx-[20rpx] mt-[50rpx]" @click="submit" v-if="unverifiedList.length > 0">
			<button class="btn">核销</button>
		</view>
		<view v-if="verifiedList.length > 0">
			<view class="card mx-[10rpx]" style="margin-top: 30rpx">
				<view class="text-center">核销记录</view>
				<view class="mt-[30rpx] flex w-full" v-for="(item, index) in verifiedList">
					<u-image width="150rpx" height="150rpx" :src="item.goodsImage"></u-image>
					<view class="ml-[20rpx] flex-1">
						<view>{{item.goodsName}}</view>
						<view class="text-xs text-muted mt-[20rpx] flex justify-between">
							<text> {{item.goodsSkuValue}} </text>
							<text> x{{item.goodsNum}} </text>
						</view>
						<view class="text-xs mt-[20rpx] flex justify-between">
							<text> 核销时间：{{item.verificationTimeStr}} </text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>
<script lang="ts" setup>
	import { ref, reactive, computed, unref, shallowRef, nextTick, watchEffect } from 'vue'
	import { selffetchDetail, selffetchVerify } from "@/api/order"
	import { onLoad, onUnload } from '@dcloudio/uni-app'
	const isCheckAgreement = ref(false)
	const id = ref()
	const orderInfo = ref({})
	const verifiedList = ref([]) //已核销列表
	const unverifiedList = ref([]) //待核销列表
	const checkboxValue = ref([])
	const getSelffetchDetail = async () => {
		try {
			let ret = await selffetchDetail({ id: id.value })
			orderInfo.value = ret
			verifiedList.value = []
			unverifiedList.value = []
			orderInfo.value.orderGoodsList.map(item => {
				if (item.verificationStatus == 0) { //待核销
					if (!(item.afterSalesStatus == 1 || item.afterSalesStatus == 2)) {
						item.selected = true
						item.disabled = false
					} else {
						item.selected = false
						item.disabled = true
					}
					unverifiedList.value.push(item)
				} else if (item.verificationStatus == 1) {
					verifiedList.value.push(item)
				}
			})
		} catch (err) {
			//console.log(err)
			uni.navigateBack({
				delta: 1
			})
		}
	}

	const handleSelect = async (item) => {
		if (item.afterSalesStatus == 1 || item.afterSalesStatus == 2) { //售后中，成功的记录不能被核销
			return 
		}
		item.selected = !item.selected
	}
	const submit = async () => {
		let selectedList = []
		unverifiedList.value.map(item => {
			if (item.selected == true) {
				selectedList.push({
					id: item.id,
					afterSale: item.afterSalesBtn == 0 ? 1 : 0,
					verificationStatus: item.verificationStatus
				})
			}
		})
		if (selectedList.length == 0) {
			return uni.$u.toast('请选择至少一项核销')
		}
		const modelRes: any = await uni.showModal({
		    title: '温馨提示',
		    content: '确认核销商品吗？'
		})
		if (modelRes.cancel) return
		
		try {
			let ret = await selffetchVerify({
				id : orderInfo.value.id,
				items: selectedList
			});
			uni.$u.toast('核销成功')
			getSelffetchDetail()
		} catch (err) {
			return uni.$u.toast(err)
		}
		
	}
	onLoad(async (options : any) => {
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
	.text-orange {
		color : red;
	}
</style>