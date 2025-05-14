<template>
    <u-tabbar
        v-model="current"
        v-bind="tabbarStyle"
        :list="tabbarList"
        @change="handleChange"
        :hide-tab-bar="true"
    ></u-tabbar>
</template>

<script lang="ts" setup>
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { navigateTo } from '@/utils/util'
import { computed, ref } from 'vue'
const current = ref()
const appStore = useAppStore()
const userStore = useUserStore()
const tabbarList = computed(() => {
    return appStore.getTabbarConfig.map((item: any) => {
        const link = JSON.parse(item.link)
        return {
            iconPath: item.unselected,
            selectedIconPath: item.selected,
            text: item.name,
            link,
            count: link.path == '/pages/shop_cart/shop_cart' ? userStore.shopCartCount : 0,
            pagePath: link.path
        }
    })
})

const showTabbar = computed(() => {
    const currentPages = getCurrentPages()
    const currentPage = currentPages[currentPages.length - 1]
    const current = tabbarList.value.findIndex((item: any) => {
        return item.pagePath === '/' + currentPage.route
    })
    return current >= 0
})
const nativeTabList = [
    '/pages/index/index',
    '/pages/category/category',
    '/pages/shop_cart/shop_cart',
    '/pages/user/user'
]
const tabbarStyle = computed(() => ({
    activeColor: appStore.getStyleConfig.selectedColor,
    inactiveColor: appStore.getStyleConfig.defaultColor
}))
const handleChange = (index: number) => {
    // const selectTab = tabbarList.value[index]

    // uni.switchTab({
    //     url: selectTab.link.path,
    //     fail() {
    //         navigateTo(selectTab.link, 'reLaunch')
    //     }
    // })
    const selectTab = tabbarList.value[index]
    if (nativeTabList.includes(selectTab.link.path)) {
        uni.switchTab({
            url: selectTab.link.path
        })
    } else {
        navigateTo(selectTab.link, 'reLaunch')
    }
}
</script>
