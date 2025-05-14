<template>
    <view class="flex justify-between py-[20rpx] bg-white">
        <view v-for="(item, index) in list" :key="index" class="flex-1">
            <item @change-sort="changeSort" :item-data="item"></item>
        </view>
    </view>
</template>

<script setup lang="ts">
/**
 * @description 排序升降序
 * @property {Object} list 列表:{label:'团队排序',name:'team_'},{ label: '金额排序', name: 'order_money_' }
 */
import { ref } from 'vue'
import item from './item.vue'

const emit = defineEmits(['changeSort'])

const props = defineProps({
    list: {
        type: Object,
        default: [] as any
    }
})

const changeSort = (value: string) => {
    emit('changeSort', value)
}
</script>

<style scoped lang="scss"></style>
