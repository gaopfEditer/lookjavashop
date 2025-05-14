<template>
  <view
    v-if="content?.isShow"
    class="rounded-[14rpx] overflow-hidden bg-white m-[20rpx] pb-[18rpx] px-[30rpx] py-[26rpx] flex justify-between"
  >
    <view>
      <span>佣金</span>
      <span class="text-[red] ml-[30rpx]"
        >{{ content.percent.toFixed(2) }}%</span
      >
      <span class="ml-[10rpx] text-[#999]"
        >(预计{{ content.earnings.toFixed(2) }})</span
      >
    </view>
    <view class="flex" @click="handleshare">
      <!-- <button>立即分享</button> -->
      <span class="mr-[5rpx]">立即分享</span>
      <span class="ml-[5rpx]"
        ><u-icon name="arrow-right" color="#707070"></u-icon
      ></span>
    </view>
  </view>
</template>
<script setup lang="ts">
const emit = defineEmits(["update:modelValue"]);

const props = defineProps({
  modelValue: {
    type: Boolean,
  },
  content: {
    type: Object,
  },
});
const handleshare = () => {
  emit("update:modelValue", true);
};
</script>
