<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <!-- <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
 -->

        <u-sticky offset-top="0" h5-nav-height="0" bg-color="transparent">
            <u-navbar
                :is-back="false"
                title="分类"
                :title-bold="false"
                :is-fixed="false"
                :border-bottom="false"
                :background="{ background: `${$theme.navBgColor}` }"
                :title-color="$theme.navColor"
            ></u-navbar>
        </u-sticky>
        <!-- #endif -->
    </page-meta>
    <view class="category-pages">
        <navigator
            url="/pages/search/search"
            class="search px-[24rpx] py-[14rpx] bg-white"
            hover-class="none"
        >
            <u-search
                placeholder="请输入关键词搜索"
                :disabled="true"
                :show-action="false"
            ></u-search>
        </navigator>

        <w-category class="category" />

        <tabbar />
    </view>
</template>

<script lang="ts" setup>
import { useUserStore } from '@/stores/user'
import { goodsCategory } from '@/api/goods'
import { getDecorate } from '@/api/shop'
import { onShow } from '@dcloudio/uni-app'
import { useDataEffect } from '@/components/widgets/category/useDataEffect'

const { getShopCartCount } = useUserStore()
const { list, page } = useDataEffect()

const getCategory = async () => {
    try {
        const category = await goodsCategory()
        const decorate = await getDecorate({ id: 4 })
        list.value = category
        const data = JSON.parse(decorate.pages)
        page.value = data[0].content
    } catch (error) {
        console.log(error)
    }
}

onShow(() => {
    getCategory()
    getShopCartCount()
})
</script>

<style lang="scss">
/*根元素需要有固定的高度*/
page {
    height: 100%;
    box-sizing: border-box;
    // 支付宝小程序,钉钉小程序需添加绝对定位,否则height:100%失效: https://opendocs.alipay.com/mini/framework/acss#%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98
    /* #ifdef MP-ALIPAY || MP-DINGTALK*/
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;

    /* #endif */
    /*需给父元素设置height:100%*/
    .category-pages {
        position: fixed;
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        background-repeat: no-repeat;
        background-size: 100% auto;

        /* 中间 */
        .category {
            flex: 1;
            min-width: 0;
            min-height: 0;
            /* 需给flex:1的元素加上最小高,否则内容超过会溢出容器 (如:小程序Android真机) */
            display: flex;
            flex-direction: column;
        }
    }
}
</style>
