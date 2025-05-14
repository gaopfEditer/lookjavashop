<template>
    <view class="flex h-full">
        <left-aside></left-aside>

        <view class="mt-[20rpx] w-full">
            <z-paging
                ref="paging"
                v-model="goodsList"
                @query="getData"
                :fixed="false"
                height="100%"
                :auto-clean-list-when-reload="false"
                :auto-scroll-to-top-when-reload="false"
            >
                <view class="mx-[20rpx]">
                    <l-swiper
                        class="mb-[20rpx]"
                        height="200"
                        v-if="page.enabled"
                        :content="page"
                        mode="none"
                    />
                    <view>
                        <sort-nav v-model:active="sort" @change="changeNav"></sort-nav>

                        <view class="mt-[20rpx]" v-for="item in goodsList" :key="item.id">
                            <router-navigate
                                :to="`/pages/goods_detail/goods_detail?id=${item.id}`"
                                open-type="navigate"
                                hover-class="none"
                            >
                                <goods-card
                                    shape="rectangle"
                                    :name="item.name"
                                    :image="item.image"
                                >
                                    <view class="mt-2">
                                        <view class="mb-2 text-muted" v-if="item.lineationPrice > 0">
                                            原价¥{{ item.lineationPrice }}
                                        </view>
                                        <price
                                            :content="item?.price"
                                            main-size="32rpx"
                                            minor-size="24rpx"
                                            fontWeight="500"
                                        />
                                    </view>
                                </goods-card>
                            </router-navigate>
                        </view>
                    </view>
                </view>
            </z-paging>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { ref, watch, shallowRef } from 'vue';
import leftAside from './left-aside.vue';
import { getGoodsLists } from "@/api/goods"

import { useDataEffect } from "./useDataEffect";
const { list, page, current } = useDataEffect()

const paging = shallowRef()
const goodsList = ref<any>([])
const sort = ref<string>('');

const changeNav = () => {
    paging.value.reload()
}

const getData = async (pageNo: number, pageSize: number) => {
    try {
        const { lists } = await getGoodsLists({
            pageNo: pageNo,
            sort: sort.value,
            pageSize: pageSize,
            categoryId: list.value[current.value].id
        })
        paging.value.complete(lists)
    } catch (error) {
        console.log(error)
        paging.value.complete(false)
    }
}

watch(
    () => current,
    (val) => {
        sort.value = '';
        paging.value.reload()
        console.log(val.value)
    },
    {
        deep: true
    }
)
</script>