<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="after-sale-list">
        <tabs :current="current" @change="handleChange" height="80" bar-width="60" :is-scroll="false" v-if="current != -1"
            :active-color="$theme.primaryColor">
            <tab v-for="(item, index) in tabList" :key="index" :name="item.name">
                <view class="afterSaleList">
                    <after-sales-list :type="item.type" :i="index" :index="current"></after-sales-list>
                </view>
            </tab>
        </tabs>
    </view>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import AfterSalesList from './component/after-sales-list.vue'

const tabList = reactive<any>([
    {
        name: '售后申请',
        type: 'apply'
    },
    {
        name: '处理中',
        type: 'ing'
    },
    {
        name: '售后记录',
        type: 'log'
    }
])
const current = ref<number>(0)

const handleChange = (index: number) => {
    current.value = Number(index)
}

onLoad(({ type }: any) => {
    current.value = Number(type) + 1 || 0
})
</script>

<style lang="scss">
.afterSaleList {
    height: calc(100vh - 40px - env(safe-area-inset-bottom));
}
</style>
