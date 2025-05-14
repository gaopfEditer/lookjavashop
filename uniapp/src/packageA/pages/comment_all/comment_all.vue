<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="comment-all">
        <!-- Header -->
        <view class="comment-all-header">
            <view
                v-for="(item, index) in tabList"
                :key="index"
                class="nav-item"
                @click="handleChangeNav(index)"
                :class="{ 'nav-item-active': index == comment.current }"
            >
                {{ item.name }}
            </view>
        </view>

        <!-- Main -->
        <view class="comment-all-main">
            <z-paging
                auto-show-back-to-top
                ref="paging"
                :auto="false"
                v-model="comment.lists"
                @query="queryList"
                :fixed="false"
                height="100%"
                empty-view-text="暂无商品评论～"
                :empty-view-img="EmptyCommentImg"
                :empty-view-style="{ 'margin-top': '100px' }"
                :empty-view-center="false"
                :auto-clean-list-when-reload="false"
                :auto-scroll-to-top-when-reload="false"
                :empty-view-img-style="{ width: '360rpx', height: '360rpx' }"
            >
                <comment-card :data="comment.lists"></comment-card>
            </z-paging>
        </view>

        <!-- Component -->
        <page-status ref="pageRef"></page-status>
    </view>
</template>

<script lang="ts" setup>
import { shallowRef, reactive, nextTick, unref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import CommentCard from './component/comment-card.vue'
import { commentGoodsLists } from '@/api/order'
import EmptyCommentImg from '../../static/empty/comment.png'

const pageRef = shallowRef()
const paging = shallowRef()
const comment = reactive({
    id: '',
    current: 0,
    lists: []
})
const tabList = reactive<any>([
    {
        name: '全部(0)',
        type: ''
    },
    {
        name: '晒图(0)',
        type: 1
    }
])

const handleChangeNav = (i: number) => {
    if (comment.current == i) return
    comment.current = i
    paging.value.reload()
}

const handleExtend = ({ all, has_image }: any) => {
    tabList[0].name = `全部(${all || 0})`
    tabList[1].name = `晒图(${has_image || 0})`
}

const queryList = async (pageNo: any, pageSize: any) => {
    try {
        const { lists, extend } = await commentGoodsLists({
            pageNo,
            pageSize,
            goodsId: comment.id,
            hasImage: tabList[comment.current].type
        })
        handleExtend(extend)
        paging.value.complete(lists)
    } catch (e) {
        paging.value.complete(false)
    }
    unref(pageRef).close()
};

onLoad(async ({ id }: any) => {
    await nextTick()
    try {
        if (!id) {
            throw new Error('参数有误')
        }
        comment.id = id
        paging.value?.reload()
    } catch (error) {
        unref(pageRef).show({
            text: error,
            src: EmptyCommentImg
        })
        console.log('全部评价', error)
    }
});
</script>

<style lang="scss" scoped>
.comment-all {
    &-header {
        padding: 20rpx;
        .nav-item {
            display: inline-block;
            border-radius: 60px;
            padding: 10rpx 30rpx;
            background: #eeeeee;
            margin-right: 20rpx;
        }
        .nav-item-active {
            color: #FFFFFF;
            background: $u-type-primary;
        }
    }
    &-main {
        height: calc(100vh - 40px - env(safe-area-inset-bottom));
    }
}
</style>
