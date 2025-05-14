<template>
    <view class="refund-reason">
        <u-popup
            id="reason"
            v-model="refund_reason.showPop"
            border-radius="14"
            mode="bottom"
            :closeable="true"
        >
            <view class="p-[30rpx] text-lg text-center">
                {{ refund_type === 1 ? '退款' : '退货退款' }}原因
            </view>
            <scroll-view style="height: 800rpx" :scroll-y="true">
                <view
                    v-for="(item, index) in reason_data"
                    :key="index"
                    class="
                        px-[24rpx]
                        py-[20rpx]
                        flex
                        justify-between
                        text-[#101010]
                    "
                    @click="onSelect(item)"
                >
                    <text
                        :class="{
                            'text-primary': item === refund_reason.value
                        }"
                    >
                        {{ item }}
                    </text>
                    <image
                        v-show="item === refund_reason.value"
                        class="w-[34rpx] h-[34rpx]"
                        src="@/static/images/icon/icon_select.png"
                    />
                    <image
                        v-show="item !== refund_reason.value"
                        class="w-[34rpx] h-[34rpx]"
                        src="@/static/images/icon/icon_null_select.png"
                    />
                </view>
            </scroll-view>
        </u-popup>
    </view>
</template>

<script lang="ts" setup>
import { reactive, watchEffect } from 'vue'

const emit = defineEmits(['update:modelValue'])
const props = withDefaults(
    defineProps<{
        modelValue: any
        reason_data: any
        refund_type: number
    }>(),
    {
        modelValue: 0,
        reason_data: [],
        refund_type: 1
    }
)

const refund_reason = reactive({
    value: '',
    showPop: false
})

watchEffect(() => {
    const value = props.modelValue
    const curValue = refund_reason.value
    if (value != curValue) {
        refund_reason.value = value
    }
})

const handleOpen = () => {
    refund_reason.showPop = true
}

const handleClose = () => {
    refund_reason.showPop = false
}

const onSelect = (value: string) => {
    handleClose()
    refund_reason.value = value
    emit('update:modelValue', value)
}

defineExpose({ handleOpen })
</script>