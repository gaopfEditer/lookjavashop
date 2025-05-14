<template>
    <view class="flex items-center justify-center">
        <view @click="changeStatus(itemData?.name)">{{ itemData?.label }}</view>
        <sort-icon :status="status" :active-color="$theme.primaryColor"></sort-icon>
    </view>
</template>

<script setup lang="ts">
import sortIcon from './sort-icon.vue'
import { ref } from 'vue'
import theme from '@/hooks/useTheme'

const emit = defineEmits(['changeSort'])
const props = defineProps({
    itemData: {
        type: Object,
        default: {} as any
    }
})

const status = ref('null')

const changeStatus = (name: string) => {
    status.value = status.value == 'asc' ? 'desc' : 'asc'
    const formateName = name + status.value
    emit('changeSort', formateName)
}
</script>

<style scoped lang="scss"></style>
