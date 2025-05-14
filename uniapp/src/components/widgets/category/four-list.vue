<template>
    <z-paging
        auto-show-back-to-top
        :auto="i == index"
        ref="paging"
        v-model="goodsList"
        :data-key="i"
        @query="queryList"
        :fixed="false"
        :auto-clean-list-when-reload="false"
        :auto-scroll-to-top-when-reload="false"
    >
        <view
            class="flex flex-wrap mx-[20rpx] my-[10rpx] col-top bg-white p-[20rpx]"
            v-if="list[current]?.children"
        >
            <view
                class="text-center item"
                :style="{ width: `${100 / 5}%` }"
                v-for="(item, index) in list[current]?.children"
                :key="index"
                @click="changeSubCate(index)"
            >
                <view class="flex flex-col col-center mb-[10rpx]">
                    <view class="flex justify-center bg-white">
                        <u-image
                            :class="{ avatar: subCurrent == index }"
                            mode="aspectFit"
                            width="88rpx"
                            height="88rpx"
                            borderRadius="50%"
                            :src="item.image"
                        />
                    </view>
                    <view
                        class="mt-[10rpx] text-xs px-[10rpx]"
                        :class="{ 'text-primary': subCurrent == index }"
                        >{{ item.name }}</view
                    >
                </view>
            </view>
        </view>
        <view class="mx-[20rpx] my-[20rpx]">
            <l-swiper height="300" v-if="i == index && page.enabled" :content="page" mode="none" />
        </view>
        <view class="mx-[20rpx] my-[20rpx]">
            <sort-nav class="mt-[20rpx]" v-model:active="sort" @change="changeNav">
                <view class="flex items-center" @click="changeGoodsType">
                    <text class="mr-[40rpx]">|</text>
                    <image
                        class="w-[32rpx] h-[32rpx]"
                        v-show="cardType != 'square'"
                        src="@/static/images/icon/icon_grid.png"
                    />
                    <image
                        class="w-[32rpx] h-[32rpx]"
                        v-show="cardType == 'square'"
                        src="@/static/images/icon/icon_list_dat.png"
                    />
                </view>
            </sort-nav>
        </view>

        <view
            class="mx-[20rpx]"
            :class="{ 'flex flex-wrap justify-between': cardType == 'square' }"
        >
            <view class="mb-[20rpx]" v-for="item in goodsList" :key="item.id">
                <router-navigate
                    :to="`/pages/goods_detail/goods_detail?id=${item.id}`"
                    open-type="navigate"
                    hover-class="none"
                >
                    <goods-card
                        :shape="cardType"
                        :name="item.name"
                        :image="item.image"
                        :price="item.price"
                        :minPrice="item.lineationPrice"
                    />
                </router-navigate>
            </view>
        </view>
    </z-paging>
</template>

<script lang="ts" setup>
import { ref, watch, nextTick, shallowRef, unref } from 'vue'
import { getGoodsLists } from '@/api/goods'
import { useDataEffect } from './useDataEffect'

const { list, page, current } = useDataEffect()

const emit = defineEmits<{
    (event: 'update:cardType', value: string): void
}>()

const props = withDefaults(
    defineProps<{
        cid: number
        i: number
        index: number
        cardType: string
    }>(),
    {
        cid: 0,
        cardType: 'square'
    }
)

const paging = shallowRef<any>(null)
const goodsList = ref<any>([])
const isFirst = ref<boolean>(true)
const sort = ref<string>('')
const subCurrent = ref<number>(0)

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

const changeGoodsType = () => {
    emit('update:cardType', props.cardType == 'square' ? 'rectangle' : 'square')
}

const changeNav = () => {
    paging.value.reload()
}

const changeSubCate = (index: number) => {
    subCurrent.value = index
    paging.value.reload()
}

const queryList = async (pageNo: number, pageSize: number) => {
    const children = unref(list)[unref(current)]?.children
    const cateId = children ? children[unref(subCurrent)].id : unref(list)[unref(current)].id
    try {
        const { lists } = await getGoodsLists({
            pageNo: pageNo,
            sort: sort.value,
            pageSize: pageSize,
            categoryId: cateId
        })
        paging.value.complete(lists)
    } catch (error) {
        console.log(error)
        paging.value.complete(false)
    }
}
</script>

<style lang="scss" scoped>
.avatar {
    border: 2px solid $u-type-primary;
    border-radius: 50%;
}
</style>
