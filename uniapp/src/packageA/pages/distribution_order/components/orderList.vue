<template>
    <z-paging ref="paging" v-model="dataList" @query="queryList" :fixed="false" height="100%" :auto="i == current">
        <view class="px-[20rpx]">
            <view class="bg-white rounded mt-[20rpx]" v-for="(item, index) in dataList" :key="index">
                <view class="p-[20rpx] flex justify-between" style="border-bottom: 1px solid #e3e3e3">
                    <view class="text-[#555555]">订单编号：{{ item.orderSn }}</view>
                    <view class="text-[#FF2C3C]">{{ item.statusMsg }}</view>
                </view>
                <view class="flex p-[20rpx]" style="border-bottom: 1px solid #e3e3e3">
                    <image class="w-[180rpx] h-[180rpx] rounded-lg" :src="item.goodsImage"></image>
                    <view class="w-[330rpx] truncate ml-[20rpx]">{{ item.goodsName }}</view>
                    <view class="flex flex-col justify-between flex-1 items-end">
                        <view class="text-xs text-[#999]">¥{{ item.goodsPrice }}</view>
                        <view class="text-xs text-[#999]">x{{ item.goodsNum }}</view>
                    </view>
                </view>
                <view class="p-[20rpx] flex justify-between">
                    <view class="flex flex-col justify-around">
                        <view class="flex items-center">
                            <image class="h-[70rpx] w-[70rpx] rounded-full" :src="item.avatar">
                            </image>
                            <view class="ml-[13rpx]">{{ item.nickname }}</view>
                        </view>
                        <view class="text-sm text-[#999]">{{ item.createTime }}</view>
                    </view>
                    <view class="flex flex-col justify-around">
                        <view class="text-xs text-[#666]">合计实付：¥{{ item.payMoney }}</view>
                        <view>
                            <span class="text-xs text-[#666]">预估收益：</span>
                            <span class="text-base text-[#F95F2F]">¥{{ item.earnings }}</span>
                        </view>
                    </view>
                </view>
            </view>
        </view>
    </z-paging>
</template>

<script setup lang="ts">
import { getOrderList } from '@/api/distribution'
import { useAppStore } from '@/stores/app'
import { ref, watch } from 'vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'

const props = defineProps({
    status: {
        type: String,
        default: ''
    },
    keyWord: {
        type: String,
        default: ''
    },
    current: {
        type: Number,
        default: 0
    },
    i: {
        type: Number,
        default: 0
    }
})
//搜索参数
const params = ref({
    status: props.status, //状态 0-待结算 1-已结算 2-已失效
    keyword: ''
})

const appStore = useAppStore()

const paging: any = ref(null)
const dataList: any = ref([])
const queryList = async (pageNo: any, pageSize: any) => {
    try {
        const { lists } = await getOrderList({ pageNo, pageSize, ...params.value })
        paging.value.complete(lists)
    } catch (error) {
        paging.value.complete(false)
    }
}

watch(
    () => props.keyWord,
    () => {
        if (props.current == props.i) {
            params.value.keyword = props.keyWord
            paging.value.reload()
        }
    }
)
watch(
    () => props.current,
    async () => {
        if (props.current == props.i) {
            paging.value.reload()
        }
    },
    {
        immediate: false
    }
)
</script>

<style scoped lang="scss"></style>
