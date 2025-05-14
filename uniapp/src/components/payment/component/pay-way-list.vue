<template>
    <view @click="onSelect(item.id)" v-for="(item, index) in lists" :key="index"
        class="px-[20rpx] flex justify-between items-center h-[120rpx] mb-[2rpx] bg-white">
        <image class="w-[48rpx] h-[48rpx]" :src="item.icon" />
        <view class="flex-1 mx-[20rpx]">
            <view class="text-base text-main">{{ item.name }}</view>
            <view class="text-muted text-xs mt-[6rpx]" v-if="item.id === 1">
                可用余额：{{ user_money }}
            </view>
        </view>
        <l-checkbox
            label=""
            :checked="modelValue == item.id"
            :true-label="1"
            :false-label="0"
            :active-color="$theme.primaryColor"
        />
    </view>
</template>

<script lang="ts" setup>
const emit = defineEmits<{
    (event: 'update:modelValue', value: number): void
}>()

withDefaults(
    defineProps<{
        lists: any
        user_money: string | number
        modelValue: any
    }>(),
    {
        lists: [],
        user_money: 0,
        modelValue: 1
    }
)

const onSelect = (way: number) => {
    emit('update:modelValue', way)
}
</script>
