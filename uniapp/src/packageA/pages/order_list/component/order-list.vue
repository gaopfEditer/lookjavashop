<template>
    <z-paging auto-show-back-to-top :auto="i == index" ref="paging" v-model="orderList" :data-key="i" @query="queryList"
        :fixed="false" height="100%" empty-view-text="暂无订单内容～" :empty-view-img="EmptyOrderImg"
        :empty-view-style="{ 'margin-top': '100px' }" :empty-view-center="false" :auto-clean-list-when-reload="true"
        :auto-scroll-to-top-when-reload="false" :empty-view-img-style="{ width: '360rpx', height: '360rpx' }">
        <block v-for="item2 in orderList" :key="item2.id">
            <!-- 订单卡片 -->
            <order-card :orderInfo="item2">
                <order-footer class="w-full" :orderId="item2?.id" :confirm="item2?.confirmBtn" :cancel="item2?.cancelBtn"
                    :evaluate="item2?.commentBtn" :pay="item2?.payBtn" :del="0" :cancel_time="item2?.cancelTime"
                    :payWay="item2?.payWay" :orderSn="item2?.orderSn" :status="item2.orderStatus" :logistics="item2?.logisticsBtn" :order="item2" @refresh="orderListRefresh" />
            </order-card>
        </block>
    </z-paging>
</template>

<script lang="ts" setup>
import { ref, shallowRef, watch, nextTick, unref } from 'vue'
import { orderLists } from '@/api/order'
import OrderCard from './order-card.vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'
import OrderFooter from '../../../component/order-footer/order-footer.vue'
import EmptyOrderImg from '../../../static/empty/order.png'

const props = withDefaults(
    defineProps<{
        type?: number | string // 底部
        i: number
        index: number
    }>(),
    {
        type: 1,
        i: 0,
        index: 0
    }
)

const orderList: any = ref([])
const paging = shallowRef()
const isFirst = ref<boolean>(true)

watch(
    () => props.index,
    async () => {
        if (props.i == props.index && unref(isFirst)) {
            isFirst.value = false
            orderListRefresh()
        }
    },
    { immediate: false }
)

// 订单刷新
const orderListRefresh = () => {
    paging.value?.reload()
}

const queryList = async (pageNo: any, pageSize: any) => {
    try {
        const { lists } = await orderLists({
            pageNo,
            pageSize,
            status: props.type
        })
        paging.value.complete(lists)
    } catch (e) {
        paging.value.complete(false)
    }
}

onShow(() => {
    isFirst.value = true
    if (props.i == props.index) {
        paging.value?.reload()
    }
})
</script>
