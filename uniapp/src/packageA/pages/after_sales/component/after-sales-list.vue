<template>
    <z-paging
        auto-show-back-to-top
        :auto="i == index"
        ref="paging"
        v-model="orderList"
        :data-key="i"
        @query="queryList"
        :fixed="false"
        height="100%"
        empty-view-text="暂无售后订单～"
        :empty-view-img="EmptyOrderImg"
        :empty-view-style="{ 'margin-top': '100px' }"
        :empty-view-center="false"
        :auto-clean-list-when-reload="false"
        :auto-scroll-to-top-when-reload="false"
        :empty-view-img-style="{ width: '360rpx', height: '360rpx' }"
    >
        <!-- 订单卡片 -->
        <block v-for="(item2) in orderList" :key="item2.id">
            <after-sales-card :data="item2" :isApply="type == 'apply'">
                <after-sales-footer
                    :after_sales_id="item2.afterId"
                    :order_goods_id="item2?.orderGoodsId"
                    :re_apply="item2.reapplyBtn"
                    :cancel="item2.cancelBtn"
                    :delivery="item2.deliveryBtn"
                    :apply="item2.applyBtn"
                    @refresh="queryList"
                />
            </after-sales-card>
        </block>
    </z-paging>
</template>

<script lang="ts" setup>
import { ref, shallowRef, watch } from 'vue'
import { afterSalesLists } from '@/api/order'
import AfterSalesCard from './after-sales-card.vue'
import { onShow } from '@dcloudio/uni-app';
import EmptyOrderImg from "../../../static/empty/order.png"
import AfterSalesFooter from '../../../component/after-sales-footer/after-sales-footer.vue'


const props = withDefaults(
    defineProps<{
        type?: any // 底部
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

watch(
    () => props.index,
    async (val) => {
		if (props.i == val) {
		    queryList()
		}
    },
    { immediate: false }
)

const queryList = async (pageNo = 1, pageSize = 10) => {
    try {
        const { lists } = await afterSalesLists({
            pageNo,
            pageSize,
            type: props.type
        })
        paging.value.complete(lists)
    } catch (e) {
        paging.value.complete(false)
    }
}

onShow(() => {
	if (props.i === 0 && props.index === 0) queryList()
})
</script>
 