<template>
	<view class="" :style="$theme.pageStyle">
		<tabs :current="current" @change="handleChange" height="80" bar-width="60" :is-scroll="false"
			:active-color="$theme.primaryColor">
			<tab v-for="(item, index) in [
                    { name: '待核销', type: 0 },
                    { name: '已核销', type: 0 }
                ]" :key="index" :name="item.name">
				<scroll-view :scroll-top="0" scroll-y="true" class="scroll-Y scroll-view" @scroll="onScroll">
					<view class="card mx-[10rpx]" v-for="(item, index) in datalist" @click="handleItemClick(item)">
						<view class="flex justify-between">
							<text class="text-xs"> 订单编号：{{item.orderSn}} </text>
							<text :style="{ color: $theme.primaryColor }" class="text-xs">
								{{item.verificationStatusStr}}
							</text>
						</view>
						<view  v-for="(subItem, subIndex) in item.orderGoodsList">
							<view class="mt-[30rpx] flex w-full" v-if="current == 0 || ( current == 1 && subItem.verificationStatus == 1)">
									<u-image width="150rpx" height="150rpx" :src="subItem.goodsImage"></u-image>
									<view class="ml-[20rpx] flex-1">
										<view>{{subItem.goodsName}} - {{}}</view>
										<view class="text-xs text-muted mt-[20rpx] flex justify-between">
											<text> {{subItem.goodsSkuValue}} </text>
											<text> x{{subItem.goodsNum}} </text>
										</view>
									</view>
							</view>
						</view>
					</view>
					
					<!-- 购物车空状态 -->
					<view
					    class="text-center mb-[20rpx]"
					    style="padding: 80rpx 0 50rpx"
					    v-show="datalist.length == 0"
					>
					    <image
					        class="w-[300rpx]"
					        src="/static/images/empty/goods.png"
					        mode="widthFix"
					    ></image>
					    <view class="text-[#999] mb-[30rpx]">暂无记录</view> 
					</view>
				</scroll-view>
			</tab>
		</tabs>

		<view class="flex mx-[20rpx] btn-footer">
			 <!-- #ifdef MP -->
				<button class="btn" @click="scanQRcode">扫码核销</button>
			 <!-- #endif -->
			 <!-- #ifdef MP -->
			<button class="btn ml-[20rpx]" @click="openCodeInput">输入核销码</button>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<button class="btn h5-button" @click="openCodeInput">输入核销码</button>
			<!-- #endif -->
		</view>
	</view>
	<u-modal ref="uModalInput" v-model="showInputCode" show-cancel-button :confirm-color="$theme.primaryColor"
		confirm-text="确定" @confirm="inputOrderCode" title="手动核销">
		<view class="slot-content row-center" style="padding: 40rpx">
			<u-input v-model="code" :border="true" placeholder="请输入核销码" style="width: 100%" />
		</view>
	</u-modal>
</template>
<script lang="ts" setup>
	import { ref, reactive, computed } from 'vue'
	import { selffetchOrderLists, pickupCodeVerify } from "@/api/order"
	import { onShow, onHide, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
	import { isEmpty } from "@/utils/util"
	const systemInfo = uni.getSystemInfoSync()
	const showInputCode = ref(false)
	const current = ref(0)
	const code = ref('')
	const query = ref({
		verificationStatus: 0,
		pageNo: 1,
		pageSize: 20
	})
	const datalist = ref([])
	const loadStatus = ref(false)
	const finishStatus = ref(false)
	const handleChange = (index : any) => {
		current.value = index
		if (index == 0) {
			query.value.verificationStatus = 0
			query.value.pageNo = 1
		} else if (index == 1) {
			query.value.verificationStatus = 1
			query.value.pageNo = 1
		}
		finishStatus.value = false
		datalist.value = []
		getSelffetchOrderLists()

	}

	const inputOrderCode = async () => {
		if (code.value == "") {
			showInputCode.value = true
			uni.$u.toast("请输入核销码");
			return false
		} else {
			try {
				let ret = await pickupCodeVerify({
					pickupCode: code.value
				})
				if (ret.verificationStatus == 1) { //跳转到详细
					uni.navigateTo({
						url: '/packageA/pages/verification_order/verification_order?id=' + ret.id
					})
					return
				} else if (ret.verificationStatus == 0) { //跳转到核销页
					uni.navigateTo({
						url: '/packageA/pages/verification_detial/verification_detial?id=' + ret.id
					})
					return
				}
				//console.log(ret, isEmpty(code.value))
			} catch(err) {
				return false
			}
		}
	}
	const getSelffetchOrderLists = async () => {
		if (loadStatus.value == true) {
			return
		}
		if (finishStatus.value == true) {
			return uni.$u.toast("已经到底了");
		}
		loadStatus.value = true
		let ret = await selffetchOrderLists(query.value);
		if (ret.lists.length > 0) {
			ret.lists.map(item => {
				datalist.value.push(item)
			})
		} else if (ret.lists.length == 0) {
			finishStatus.value = true
		}
		loadStatus.value = false
	}
	const handleItemClick = async (item) => {
		if (item.verificationStatus == 1) { //跳转到详细
			uni.navigateTo({
				url: '/packageA/pages/verification_order/verification_order?id=' + item.id
			})
			return
		} else if (item.verificationStatus == 0) { //跳转到核销页
			uni.navigateTo({
				url: '/packageA/pages/verification_detial/verification_detial?id=' + item.id
			})
			return
		}
		return uni.$u.toast("类型错误");
	}
	const onScroll = async (event) => {
		const scrollView = event.detail;
		// 当滚动到底部时，scrollTop + 视图高度 >= 滚动视图内容高度
		//console.log(scrollView.scrollTop + scrollView.scrollHeight, scrollView.height)
		if (scrollView.scrollTop + scrollView.scrollHeight >= scrollView.height) {
			// 这里编写滚动到底部后的操作
		}
	}
	const scanQRcode = async (event) => {
		uni.scanCode({
			success: function (res) {
				code.value = res.result
				inputOrderCode()
				code.value = ''
			}
		});
	}
	const openCodeInput = async () => {
		showInputCode.value = true
		
	}

	onPullDownRefresh(async () => {
		query.value.pageNo = 1
		datalist.value = []
		finishStatus.value = false
		await getSelffetchOrderLists()
		uni.stopPullDownRefresh();
	})

	onReachBottom(async () => {
		query.value.pageNo++
		await getSelffetchOrderLists()
		//console.log("onReachBottomonReachBottom")
	})


	getSelffetchOrderLists()
</script>
<style lang="scss" scoped>
	.scroll-Y {
		height: calc(100vh - 160rpx - env(safe-area-inset-bottom));

		.card {
			margin-top: 20rpx;
			background-color: white;
			border-radius: 14rpx;
			padding: 20rpx;
		}
	}

	.scroll-view {
		height: 100%;
	}

	.btn {
		height: 70rpx;
		margin-bottom: env(safe-area-inset-bottom);
		background: linear-gradient(90deg, var(--theme-dark-color) 0%, var(--theme-color) 100%);
		color: white;
		width: 50%;
	}

	.btn-footer {
		bottom: 0px;
		position: fixed;
		width: 100%;
	}
	.h5-button {
		width: 710rpx
	}
</style>