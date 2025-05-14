<template>
	<page-meta :page-style="$theme.pageStyle">
		<!-- #ifndef H5 -->
		<navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
		<!-- #endif -->
	</page-meta>
	<view :style="$theme.variable">
		<navbar title="订单详情" :background="{ background: `rgba(256,256,256, 1)` }" :titleColor="`rgba(0,0,0,1)`"
			:immersive="false"></navbar>

		<view class="order_detail">
			<!-- Header -->
			<view class="p-[30rpx] pb-0 text-white">
				<view class="text-xl">{{ order.orderStatusMsg }}</view>

				<view class="mt-[10rpx] mb-[10rpx]">
					<!-- 待付款 -->
					<template v-if="timeStamp && order.orderStatus === 1">
						<view class="flex" v-if="timeStamp > 0">
							<text class="mr-[10rpx]">支付剩余</text>
							<u-count-down :timestamp="timeStamp" format="mm分ss秒" @end="timeStamp = 0" color="#2073F4"
								class="text-base font-normal text-white" />
							<text class="ml-[10rpx]">后关闭</text>
						</view>
					</template>
					<!-- 待发货 -->
					<text v-if="order.orderStatus === 2"> 您的商品正在打包中，请耐心等待… </text>
					<!-- 待收货 -->
					<text v-if="order.orderStatus === 3 && order.deliveryType != 2"> 您的商品已经在路上了，请耐心等待… </text>
					<text v-if="order.orderStatus === 3 && order.deliveryType == 2"> 请前往指定门店取货</text>

					<!-- 已完成 -->
					<text v-if="order.orderStatus === 4"> 商品已签收，感谢再次购买！ </text>
				</view>
			</view>

			<address-card v-if="order.deliveryType != 2">
				<view class="text-lg text-[#222] mb-[14rpx]">
					<text class="font-medium">
						{{ order?.addressContact }}
					</text>
					<text class="ml-[10rpx]">
						{{ order?.addressMobile }}
					</text>
				</view>
				<view class="text-base text-content">
					{{ order?.addressContent }}
				</view>
				<template #right></template>
			</address-card>
			<view class="address-card bg-white mx-[20rpx] rounded-md" v-if="order.deliveryType == 2">
				<view @click="copy(order?.selffetchShop.shopAddress)">
					<u-icon name="man-add" size="35" />
					<text class="ml-[20rpx]">{{order?.selffetchShop.contact}}</text>
					<text class="ml-[20rpx]">{{order?.selffetchShop.mobile}}</text>
				</view>
				<view class="mt-[20rpx]">
					<u-icon name="tags" size="35" />
					<text class="ml-[20rpx]">{{order?.selffetchShop.name}}</text>
				</view>
				<view class="flex justify-between">
					<view>
						<view class="mt-[15rpx] text-muted text-xs"> 门店地址：{{order?.selffetchShop.shopAddress}} </view>
						<view class="mt-[15rpx] text-muted text-xs"> 营业时间：{{order?.selffetchShop.weekdaysStr}}
							{{formatTime(order?.selffetchShop.businessStartTime)}} -
							{{formatTime(order?.selffetchShop.businessEndTime)}}
						</view>
					</view>
					<view>
						<u-image width="100rpx" height="100rpx" :src="order?.selffetchShop.image"
							@click="previewShopLogo(order?.selffetchShop.image)">
						</u-image>
					</view>
				</view>
				<view class="mt-[50rpx] flex justify-between">
					<view @click="handleshowQR">
						<u-icon name="scan" />
						<text class="ml-[5rpx]">出示核销码</text>
					</view>
					<view @click="makePhoneCall">
						<u-icon name="phone" />
						<text class="ml-[5rpx]">联系自提点</text>
					</view>
					<view @click="handleShopLocation">
						<u-icon name="map" />
						<text class="ml-[5rpx]">导航自提点</text>
					</view>
				</view>
			</view>
			<!-- 提货码QR -->
			<u-popup v-model="showQR" mode="bottom" safe-area-inset-bottom border-radius="20">
				<view class="">
					<view class="flex flex-col justify-center items-center p-[30rpx]">
						<view class="">核销码</view>
						<text class="mt-[30rpx]">请将二维码展示给店员或者提供数字核销码</text>
						<view class="mt-[40rpx]">
							<uqrcode ref="uqrcode" canvas-id="qrcode" :value="order.pickupCode"
								:options="{ margin: 10 }"></uqrcode>
						</view>
						<view class="mt-[30rpx]">{{order.pickupCode}}</view>
					</view>
				</view>
			</u-popup>
			<!-- Main -->
			<!-- 自提门店地图 -->
			<u-popup v-model="showMap" :round="10" mode="center" safe-area-inset-bottom>
				<view style="width:100%; height: 100%">
					<map style="width: 100%; height: 600rpx" :latitude="latitude" :longitude="longitude"
						:markers="markers"></map>
				</view>
			</u-popup>
			<view class="rounded-[14rpx] pb-[20rpx] mt-[20rpx] mx-[20rpx] overflow-hidden bg-white">
				<view v-for="item in order.orderGoodsList" :key="item.id"
					@click.stop="goPage(`/pages/goods_detail/goods_detail?id=${item.goodsId}`)">
					<goods-card shape="rectangle" class="flex-1" :image="item.goodsImage"
						:imageStyle="{ width: '180rpx', height: '180rpx' }" :name="item.goodsName" :containStyle="{
                            height: '220rpx',
                            'border-radius': '0'
                        }">
						<view class="flex justify-between mt-[8rpx]">
							<price :content="item.goodsPrice" main-size="32rpx" minor-size="28rpx" fontWeight="500" />
							<text class="text-main">x {{ item.goodsNum }}</text>
						</view>
					</goods-card>
					<view class="flex justify-end mr-4">
						<button v-if="!item.afterSalesMsg && item.afterSalesBtn" class="apply-btn plain-gray"
							@click.stop="
                                goPage(`/packageA/pages/apply_refund/apply_refund?id=${item.id}`)
                            ">
							申请售后
						</button>
						<template v-else>
							<view class="text-[#fd8d1c]">{{ item.afterSalesMsg }}</view>
						</template>
					</view>
				</view>
			</view>

			<!-- 商品总结 -->
			<view class="text-base card">
				<view class="flex justify-between">
					<view class="text-content">商品金额</view>
					<view class="text-[#101010]">¥{{ order.goodsMoney }}</view>
				</view>
				<view class="flex justify-between">
					<view class="text-content">运费金额</view>
					<view class="text-[#101010]">¥{{ order.expressMoney }}</view>
				</view>
				<view class="flex justify-between" v-if="order.couponMoney">
					<view class="text-content">优惠券</view>
					<view class="text-primary">-¥{{ order.couponMoney }}</view>
				</view>
				<view class="text-right">
					<text>实付金额：</text>
					<price :content="order?.needPayMoney" main-size="34rpx" minor-size="26rpx" fontWeight="500" />
				</view>
			</view>

			<!-- 订单信息 -->
			<view class="card text-md">
				<view class="flex col-start" v-if="order?.userRemark">
					<view class="text-content">订单备注</view>
					<view style="word-break: break-all; white-space: pre-wrap"
						class="ml-[40rpx] flex-1 text-[#101010] whitespace-pre">
						{{ order?.userRemark }}
					</view>
				</view>
				<view class="flex items-center">
					<view class="text-content">订单编号</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]"> {{ order.orderSn }}</view>
					<button class="text-xs copy-btn text-content" @click="copy(order.orderSn)">
						复制
					</button>
				</view>
				<view class="flex">
					<view class="text-content">订单类型</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ orderTypeMsg(order.orderType) }}
					</view>
				</view>
				<view class="flex">
					<view class="text-content">配送方式</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ order?.deliveryTypeStr }}
					</view>
				</view>
				<view class="flex">
					<view class="text-content">支付方式</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ getPayWayDesc(order.payWay) || '-' }}
					</view>
				</view>
				<view class="flex">
					<view class="text-content">下单时间</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ order.createTime }}
					</view>
				</view>
				<view class="flex">
					<view class="text-content">付款时间</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ order.payTime }}
					</view>
				</view>
				<view class="flex" v-if="order.deliveryType != 2">
					<view class="text-content">发货时间</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ order.expressTime }}
					</view>
				</view>
				<view class="flex" v-if="order.confirmTime">
					<view class="text-content">成交时间</view>
					<view class="ml-[40rpx] flex-1 text-[#101010]">
						{{ order.confirmTime }}
					</view>
				</view>
			</view>
		</view>

		<!-- Footer -->
		<view class="flex justify-end footer" v-if="
                order.confirmBtn ||
                order.cancelBtn ||
                order.payBtn ||
                order.deleteBtn ||
                order.logisticsBtn
            ">
			<view class="footer--wrap">
				<order-footer :orderId="order?.id" :confirm="order.confirmBtn" :cancel="order.cancelBtn"
					:evaluate="order.commentBtn" :payWay="order.payWay" :orderSn="order.orderSn" :pay="order.payBtn"
					:del="order.deleteBtn" :logistics="order.logisticsBtn" :order="order" @refresh="initOrderDetail" />
			</view>
		</view>
	</view>

	<!-- Component -->
	<page-status ref="pageRef"></page-status>

	<!-- Component 支付 -->
	<payment v-if="payState.showPay || payState.showCheck" v-model:show="payState.showPay"
		v-model:show-check="payState.showCheck" :order-id="payState.orderId" :from="payState.from"
		:redirect="payState.redirect" @success="handlePayResult" />


</template>

<script lang="ts" setup>
	import { useCopy } from '@/hooks/useCopy'
	import { onLoad, onUnload } from '@dcloudio/uni-app'
	import { orderDetail } from '@/api/order'
	import GoodsAbnormalImage from '@/static/images/empty/goods.png'
	import OrderFooter from '../../component/order-footer/order-footer.vue'
	// import tkiQrcode from "@/components/tki-qrcode/tki-qrcode.vue"
	import { ref, unref, shallowRef, nextTick, reactive, watchEffect, computed } from 'vue'
	import { GoodsTypeEnum } from '@/enums/goodsEnums'
	import { useRouter } from 'uniapp-router-next'
	import { formatTime } from "@/utils/util"
	import IconMarker from '@/static/images/icon/icon_marker.png'
	const router = useRouter()
	const showMap = ref(false)

	const { copy } = useCopy()
	const pageRef = shallowRef()
	const showQR = ref(false)
	const payState = reactive({
		orderId: '',
		from: '',
		showPay: false,
		showCheck: false,
		redirect: '/packageA/pages/order_detail/order_detail'
	})

	const markers = ref([{
		id: 0,
		latitude: 39.916527,
		longitude: 116.397128,
		title: "北京天安门",
		iconPath: IconMarker, // 标记图标的本地路径
		width: 50, // 标记图标的宽度
		height: 50 // 标记图标的高度
	}])
	const latitude = ref(39.909)
	const longitude = ref(116.39742)

	const order = reactive<any>({
		id: '',
		orderSn: '',
		payWay: 0,
		cancelTime: 0,
		orderType: 1,
		orderStatus: '',
		orderStatusMsg: '',
		addressContact: '',
		addressContent: '',
		addressMobile: '',
		orderGoodsList: [],
		order_amount: '',
		needPayMoney: '',
		goodsMoney: '',
		createTime: '',
		payBtn: 0,
		cancelBtn: 0,
		confirmBtn: 0,
		deleteBtn: 0,
		logisticsBtn: 0,
		refundBtn: 0,
		selffetchShop: {},
		pickupCode: ''
	})
	const handleshowQR = () => {
		let status = [2, 3, 4]
		if (status.includes(order.orderStatus) == false) {
			return uni.$u.toast("付款后可查看核销码")
		} else {
			showQR.value = true
		}
	}
	const timeStamp = ref<number | null>(0)

	const orderTypeMsg = computed(() => {
		return (orderType : number) => {
			switch (orderType) {
				case GoodsTypeEnum.ORDINARY:
					return '普通订单'
				case GoodsTypeEnum.SECKILL:
					return '秒杀订单'
				case GoodsTypeEnum.TEAM:
					return '拼团订单'
			}
		}
	})

	const getPayWayDesc = computed(() => {
		return (type : number) => {
			switch (type) {
				case 0:
					return '未知支付'
				case 1:
					return '余额支付'
				case 2:
					return '微信支付'
				case 3:
					return '支付宝支付'
			}
		}
	})

	// 初始化订单详情
	const initOrderDetail = async () : Promise<void> => {
		try {
			const data = await orderDetail({ id: order.id })
			Reflect.ownKeys(data).map((item : any) => {
				order[item] = data[item]
			})
			timeStamp.value = Number(order.cancelTime) * 1000
			unref(pageRef)?.close()
		} catch (error) {
			unref(pageRef)?.show({
				text: error,
				src: GoodsAbnormalImage
			})
			console.log('获取订单详情接口', error)
		}
	}

	// 页面跳转
	const goPage = (url : string) => {
		router.navigateTo(url)
	}

	uni.$on('payment', (param : any) => {
		payState.orderId = param.order_id
		payState.from = param.from
		payState.showPay = true
		payState.redirect =
			payState.redirect +
			`?id=${order.id}&order_id=${param.order_id}&from=${param.from}&checkPay=true`
	})

	const handlePayResult = async () => {
		payState.showPay = false
		payState.showCheck = false
		await initOrderDetail()
		// uni.redirectTo({
		//     url: `/packageA/pages/order_detail/order_detail?id=${order.id}`
		// })
	}

	const makePhoneCall = async () => {
		uni.makePhoneCall({
			phoneNumber: order?.selffetchShop.mobile
		});
	}


	// 查看店铺logo项
	const previewShopLogo = (current) => {
		uni.previewImage({
			urls: [current],
		})
	}

	const handleShopLocation = async () => {
		//显示店铺地址
		longitude.value = Math.abs(order.selffetchShop.longitude)
		latitude.value = Math.abs(order.selffetchShop.latitude)
		markers.value[0].latitude = latitude.value
		markers.value[0].longitude = longitude.value
		markers.value[0].title = order.selffetchShop.name
		
		uni.openLocation({
			latitude: latitude.value,
			longitude: longitude.value,
			name: order.selffetchShop.name,
			address: order.selffetchShop.shopAddress,
			success: function () {
				console.log('success');
			}
		});

		//showMap.value = true
	}

	onLoad(async (options : any) => {
		await nextTick()

		try {
			if (!options.id) {
				throw new Error('参数有误')
			}
			order.id = Number(options.id)
			// 初始化订单信息
			await initOrderDetail()

			// h5支付用于弹起手动确认支付弹窗
			if (options?.checkPay) {
				payState.orderId = options.order_id
				payState.from = options.from
				payState.showCheck = true
				// return
			}
		} catch (error) {
			unref(pageRef).show({
				text: error,
				src: GoodsAbnormalImage
			})
			console.log('订单结算', error)
		}
	})

	onUnload(() => {
		uni.$off(['payment'])
		payState.showPay = false
	})
</script>

<style lang="scss">
	.order_detail {
		height: 100%;
		// padding: 30rpx 24rpx;
		padding-bottom: 200rpx;
		background: linear-gradient(to bottom, $u-type-primary 200rpx, transparent 0);

		.card {
			padding: 20rpx;
			border-radius: 14rpx;
			line-height: 60rpx;
			margin: 20rpx 20rpx 0 20rpx;
			background-color: #ffffff;
		}

		.apply-btn {
			font-size: 24rpx;
			height: 52rpx;
			padding: 0 20rpx;
			line-height: 52rpx;
			border-radius: 60px;
		}

		.copy-btn {
			width: 100rpx;
			height: 40rpx;
			padding: 0rpx 14rpx;
			border-radius: 60px;
			line-height: 40rpx;
			border: 1px solid #d7d7d7;
		}
	}

	.footer {
		left: 0;
		bottom: 0;
		width: 100%;
		position: fixed;
		padding: 0 20rpx;
		background-color: #ffffff;
		box-shadow: 2rpx 2rpx 22rpx rgba($color: #000000, $alpha: 0.2);

		&--wrap {
			padding-bottom: env(safe-area-inset-bottom);
		}
	}

	.address-card {
		padding: 30rpx;
	}
</style>