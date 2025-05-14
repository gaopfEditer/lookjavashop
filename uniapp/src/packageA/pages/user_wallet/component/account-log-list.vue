<template>
    <view class="main">
        <z-paging
            auto-show-back-to-top
            :auto="i == index"
            ref="paging"
            v-model="accountLogList"
            :data-key="i"
            :fixed="false"
            height="100%"
            :auto-clean-list-when-reload="false"
            :auto-scroll-to-top-when-reload="false"
            @query="queryList"
        >
            <view class="pt-2.5">
                <view
                    v-for="item in accountLogList"
                    :key="item.id"
                    class="bg-white border-solid border-b border-0 border-light px-[26rpx] py-[24rpx]"
                >
                    <view class="flex justify-between">
                        <view class="mr-2">{{ item.tips }}</view>
                        <view
                            class="text-lg"
                            :class="{
                                'text-primary': item.action == 1
                            }"
                        >
                            {{ item.action == 1 ? '+' : '-' }}
                            {{ item.orderAmount }}
                        </view>
                    </view>
                    <view class="text-sm text-muted mr-1">{{ item.createTime }}</view>
                </view>
            </view>
        </z-paging>
    </view>
</template>

<script lang="ts" setup>
import { ref, shallowRef, watch, nextTick, unref } from 'vue'
import { accountLog } from '@/api/user'

const props = withDefaults(
    defineProps<{
        type?: any
        i: number
        index: number
    }>(),
    {
        type: 1,
        i: 0,
        index: 0
    }
)

const isFirst = ref<boolean>(true)
const accountLogList: any = ref([])
// 下拉组件的Ref
const paging = shallowRef()

watch(
    () => props.index,
    async () => {
        await nextTick()
        if (props.i == props.index && unref(isFirst)) {
            isFirst.value = false
            paging.value?.reload()
        }
    },
    { immediate: true }
)

const queryList = async (pageNo: any, pageSize: any) => {
    try {
        const { lists } = await accountLog({
            pageNo,
            pageSize,
            type: props.type
        })

        paging.value.complete(lists)
    } catch (e) {
        console.log('下拉加载', e)
        paging.value.complete(false)
    }
}
</script>

<style lang="scss" scoped>
.main {
    height: calc(100vh - 200px - env(safe-area-inset-bottom));
    padding-top: 20rpx;
}
</style>