<template>
    <tabs :current="current" @change="handleChange" height="100" bar-width="60" :is-scroll="false"
        :active-color="$theme.primaryColor">
        <tab v-for="(item, index) in tabList" :key="index" :name="item.name">
            <account-log-list :changeObj="changeObj" :type="item.type" :index="current" :i="index"></account-log-list>
        </tab>
    </tabs>
</template>

<script setup lang="ts">
import AccountLogList from '@/packageA/pages/balance_detail/component/account-log-list.vue'
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const changeObj = ref<number>(0)
const tabList = ref([
    {
        name: '全部',
        type: 0
    },
    {
        name: '收入',
        type: 1
    },
    {
        name: '支出',
        type: 2
    }
])
const current = ref<number>(0)

const handleChange = (index: number) => {
    current.value = Number(index)
}

onLoad(({ type }: any) => {
    if (type == 1) {
        uni.setNavigationBarTitle({
            title: '账户明细'
        })
        changeObj.value = 0
    } else if (type == 2) {
        uni.setNavigationBarTitle({
            title: '佣金明细'
        })
        changeObj.value = 1
    }
})
</script>
