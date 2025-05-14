<template>
    <view class="flex justify-around text-sm bg-white rounded-[10rpx] leading-[44rpx] py-[22rpx]">
        <view :class="{ 'text-primary font-medium': active == '' }" @click="hanldeNavSelect(1)"
            >综合</view
        >
        <view
            class="flex"
            :class="{ 'text-primary font-medium': active == 'price' }"
            @click="hanldeNavSelect(2)"
        >
            <text>价格</text>
            <sort-icon
                :status="
                    active == 'priceAsc' || active == 'priceDesc'
                        ? navParam.price == 'priceDesc'
                            ? 'desc'
                            : 'asc'
                        : ''
                "
            />
        </view>
        <view
            class="flex"
            :class="{ 'text-primary font-medium': active == 'sale' }"
            @click="hanldeNavSelect(3)"
        >
            <text>销量</text>
            <sort-icon
                :status="
                    active == 'salesAsc' || active == 'salesDesc'
                        ? navParam.sales == 'salesDesc'
                            ? 'desc'
                            : 'asc'
                        : ''
                "
            />
        </view>
        <slot></slot>
    </view>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import SortIcon from './sort-icon.vue'

const emit = defineEmits<{
    (event: 'change', value: string): void
    (event: 'update:active', value: string): void
}>()

const props = withDefaults(
    defineProps<{
        active: string
    }>(),
    {
        active: ''
    }
)

const navParam = reactive({
    price: 'priceAsc',
    sales: 'salesAsc'
})

const hanldeNavSelect = (type: number) => {
    let value = ''
    switch (type) {
        case 1:
            value = ''
            emit('update:active', '')
            break
        case 2:
            navParam.price = navParam.price == 'priceDesc' ? 'priceAsc' : 'priceDesc'
            value = navParam.price
            emit('update:active', value)
            break
        case 3:
            navParam.sales = navParam.sales == 'salesDesc' ? 'salesAsc' : 'salesDesc'
            value = navParam.sales
            emit('update:active', value)
            break
    }
    emit('change', value)
}
</script>
