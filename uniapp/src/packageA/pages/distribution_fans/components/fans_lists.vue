<template>
    <z-paging ref="paging" v-model="dataList" @query="queryList" :fixed="false" height="100%" :auto="i == index"
        :data-key="i" auto-show-back-to-top :auto-clean-list-when-reload="false" :auto-scroll-to-top-when-reload="false">
        <view>
            <sortNavNew @change-sort="changeSort" :list="sortList"></sortNavNew>
        </view>

        <view class="px-[20rpx] bg-white">
            <view v-for="(item, index) in dataList" :key="index" class="py-[20rpx] flex itemUser">
                <image :src="item.avatar" class="w-[100rpx] h-[100rpx] rounded-full"></image>
                <view class="felx flex-col justify-between ml-[10rpx]">
                    <view class="text-xl">{{ item.nickname }}({{ item.sn }})</view>
                    <view class="text-xs text-[#9CABFF]">{{ item.mobile || '-' }}</view>
                    <view class="text-xs text-[#666666]">关注时间：{{ item.createTime }}</view>
                </view>
                <view class="flex flex-col justify-between text-xs flex-1 items-end">
                    <view>
                        <span class="text-[#FF2C3C]">{{ item.fans }}</span>
                        <span>人</span>
                    </view>
                    <view>{{ item.orderNum }}笔</view>
                    <view>{{ item.orderAmount }}元</view>
                </view>
            </view>
        </view>
    </z-paging>
</template>

<script setup lang="ts">
import sortNavNew from '@/components/sort-nav-new/index.vue'
import { getMyFansList } from '@/api/distribution'
import { nextTick, ref, watch } from 'vue'
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app'

const props = defineProps({
    type: {
        type: String,
        default: ''
    },
    keyWord: {
        type: String,
        default: ''
    },
    index: {
        type: Number,
        default: 0
    },
    i: {
        type: Number,
        default: 0
    }
})

const params = ref({
    type: props.type, //类型
    keyword: '', //昵称
    sortAmount: '',
    sortOrder: '',
    sortFans: ''
})

//排序状态列表
const sortList = ref([
    { label: '团队排序', name: 'team_' },
    { label: '金额排序', name: 'order_' },
    { label: '订单排序', name: 'num_' }
])
//分页组件ref
const paging: any = ref(null)
const dataList: any = ref([])

const changeSort = (value: any) => {
    switch (value.split('_')[0]) {
        case 'team':
            params.value.sortFans = value.split('_')[1]
            break
        case 'order':
            params.value.sortOrder = value.split('_')[1]
            break
        case 'num':
            params.value.sortAmount = value.split('_')[1]
            break
    }
    paging.value.reload()
}

const queryList = async (pageNo: any, pageSize: any) => {
    try {
        const { lists } = await getMyFansList({ pageNo, pageSize, ...params.value })
        paging.value.complete(lists)
    } catch (error) {
        paging.value.complete(false)
    }
}

watch(
    () => props.keyWord,
    async () => {
        if (props.index == props.i) {
            params.value.keyword = props.keyWord

            paging.value.reload()
        }
    },
    {
        immediate: false
    }
)
watch(
    () => props.index,
    async () => {
        if (props.index == props.i) {
            paging.value.reload()
        }
    },
    {
        immediate: false
    }
)
</script>

<style scoped lang="scss">
.itemUser {
    &:not(:last-child) {
        border-bottom: 1px solid #e5e5e5;
    }
}
</style>
