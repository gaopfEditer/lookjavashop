<template>
    <z-paging
        ref="paging"
        v-model="dataList"
        @query="queryList"
        :show-loading-more-when-reload="true"
    >
        <view class="pt-2.5">
            <view
                v-for="item in dataList"
                :key="item.id"
                class="bg-white border-solid border-b border-0 border-light px-[26rpx] py-[24rpx]"
            >
                <view class="flex justify-between">
                    <view class="mr-2">{{ item.tips }}</view>
                    <view class="text-lg text-primary"> +{{ item.orderAmount }} </view>
                </view>
                <view class="text-sm text-muted mr-1">{{ item.createTime }}</view>
            </view>
        </view>
    </z-paging>
</template>

<script lang="ts" setup>
import { ref, shallowRef } from 'vue'
import { rechargeRecord } from '@/api/recharge'

const paging = shallowRef()
const dataList = ref<any[]>([])

const queryList = async (pageNo: any, pageSize: any) => {
    try {
        const { lists } = await rechargeRecord({
            pageNo,
            pageSize
        })
        paging.value.complete(lists)
    } catch (e) {
        console.log('下拉加载', e)
        paging.value.complete(false)
    }
}
</script>