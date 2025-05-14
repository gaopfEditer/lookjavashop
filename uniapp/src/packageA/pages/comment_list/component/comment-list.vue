<template>
    <z-paging
        auto-show-back-to-top
        :auto="i == index"
        ref="paging"
        v-model="commentList"
        :data-key="i"
        @query="queryList"
        :fixed="false"
        height="100%"
        empty-view-text="暂无内容～"
        :empty-view-img="EmptyCommentImg"
        :empty-view-style="{ 'margin-top': '100px' }"
        :empty-view-center="false"
        :auto-clean-list-when-reload="false"
        :auto-scroll-to-top-when-reload="false"
        :empty-view-img-style="{ width: '360rpx', height: '360rpx' }"
    >
        <!-- 待评价 -->
        <template v-if="type == 0">
            <has-card :data="commentList"></has-card>
        </template>
        <!-- 已评价 -->
        <template v-else>
            <no-card :data="commentList"></no-card>
        </template>
    </z-paging>
</template>

<script lang="ts" setup>
import { commentLists } from '@/api/order'
import { ref, shallowRef, watch, nextTick, unref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import NoCard from './no-card.vue'
import HasCard from './has-card.vue'
import EmptyCommentImg from '../../../static/empty/comment.png'

const emit = defineEmits(['extend'])

const props = withDefaults(
    defineProps<{
        type?: number // 底部
        i: number
        index: number
    }>(),
    {
        type: 1,
        i: 0,
        index: 0
    }
)

const commentList: any = ref([])
const paging = shallowRef()
const isFirst = ref<boolean>(true)

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
        const { lists, extend } = await commentLists({
            pageNo,
            pageSize,
            isComment: props.type
        })
        emit('extend', extend)
        paging.value.complete(lists)
    } catch (e) {
        paging.value.complete(false)
    }
}

onShow(() => {
    // 刷新当前的
    if (props.i == 1 && props.index == 1) {
        paging.value?.reload()
    }
    if (props.i == 0 && props.index == 0) {
        paging.value?.reload()
    }
})
</script>

