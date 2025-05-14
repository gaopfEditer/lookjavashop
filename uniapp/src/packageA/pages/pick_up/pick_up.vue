<template>
    <view class="container" :style="$theme.pageStyle">
        <map style="width: 100%; height: 300px" :latitude="latitude" :longitude="longitude" :markers="markers"></map>
        <view>
            <scroll-view :scroll-top="0" scroll-y="true" class="scroll-Y">
				<u-radio-group v-model="value" @change="radioGroupChange" style="width: 100%">
                <view v-for="(items, index) in shopList" :key="index" class="card mx-[20rpx] flex items-center" >
                    <u-image
                        width="120rpx"
                        height="120rpx"
                        :src="items.image"
                    ></u-image>
                    <view class="ml-[20rpx] flex flex-col justify-between" >
                        <view class="mb-[10rpx]" ><span>{{items.name}} </span>
                            <span
                                class="ml-[10rpx] text-xs bg-primary px-[10rpx] text-white rounded"
								v-if="index == 0"
                                >距离最近</span>
                        </view>
                        <view class="text-muted text-xs mb-[10rpx]"> {{items.detailedAddress}} </view>
                        <view class="text-xs mb-[10rpx]">
                            <span class="text-[#4073FA]"> {{items.distanceStr}}</span>
                            <span class="mx-[10rpx]"> | </span>
                            <span class="text-muted" @click="showMap(items)"> 查看地图 </span></view
                        >
                    </view>
                    <view style="margin-left: auto; position: absolute; right: 0;">
                        <u-radio :label="items.id" :name="items.id"  :active-color="$theme.primaryColor"></u-radio>
                    </view>
                </view>
				</u-radio-group>
            </scroll-view>
        </view>
        <view class="mx-[20rpx]">
            <button class="btn" @click="submit">确认自提点</button>
        </view>
    </view>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { getSelffetchshopList } from "@/api/selffetchshop"
import IconSuccess from '@/static/images/icon/icon_marker.png'
import { kilometers, isEmpty } from "@/utils/util"
import { useOrderPickup } from '@/stores/orderPickup'
const latitude = ref(39.909)
const longitude = ref(116.39742)
const value = ref()
const shopList = ref([])
const markers = ref([{
          id: 0,
          latitude: 39.916527,
          longitude: 116.397128,
          title: "北京天安门",
          iconPath: IconSuccess, // 标记图标的本地路径
          width: 50, // 标记图标的宽度
          height: 50 // 标记图标的高度
        }])

const getLocation = async() => {
	//await getShopList()
	uni.getLocation({
		success: function (res) {
			longitude.value = Math.abs(res.longitude)
			latitude.value = Math.abs(res.latitude)
			markers.value[0].latitude = latitude.value
			markers.value[0].longitude = longitude.value
			markers.value[0].title = "当前位置"
			getShopList()
		},
		fail: function (err) {
			return uni.$u.toast(err)
		},
		complete: function (e) {
			//console.log(e, "completecompletecomplete")
		}
		
	});
}
const getShopList = async() => {
	let rep = await getSelffetchshopList({
		longitude: longitude.value,
		latitude: latitude.value
	})
	shopList.value = rep.lists
	if (shopList.value.length > 0) {
		shopList.value.map((item, index) => {
			if (index == 0) {
				value.value = item.id
			}
			item.distanceStr = kilometers(item.distance)
		})
	}
	
	//自动选择第一个
	showMap(shopList.value[0])
}

const showMap = async(item:any) => {
	longitude.value = item.longitude
	latitude.value = item.latitude
	markers.value[0].latitude = latitude.value
	markers.value[0].longitude = longitude.value 
	markers.value[0].title = item.name
	//console.log(markers.value)
}
const radioGroupChange = async() => {
	//console.log(value.value, "radioGroupChange")
}
const submit = async() => {
	if (isEmpty(value.value)) {
		return uni.$u.toast("请选择自提点");
	}
	
	//保存在全局变量
 	let shopFilter = shopList.value.filter(item => {
		return item.id == value.value
	})
	useOrderPickup().config = shopFilter[0]
	uni.navigateBack({
		delta: 1
	});
}
getLocation()
</script>
<style scoped lang="scss">
.container {
    .scroll-Y {
        height: calc(100vh - 300px - 75rpx - env(safe-area-inset-bottom));
        .card {
            margin-top: 20rpx;
            background-color: white;
            border-radius: 14rpx;
            padding: 20rpx;
			width: 100%;
        }
    }

    .btn {
        height: 70rpx;
        margin-bottom: env(safe-area-inset-bottom);
        background: linear-gradient(90deg, var(--theme-dark-color) 0%, var(--theme-color) 100%);
        color: white;
    }
}
</style>
