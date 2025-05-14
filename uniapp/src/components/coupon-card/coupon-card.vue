<template>
  <view
    class="coupon-card flex rounded-[10rpx] overflow-hidden"
    @click="onClick"
    :style="$theme.pageStyle"
  >
    <!--    左侧    -->
    <view class="coupon-card__left w-[220rpx]" :style="mainStyle">
      <view>
        <price
          :content="data.money"
          color="inherit"
          main-size="60rpx"
          minor-size="34rpx"
        />
      </view>
      <view class="text-sm">
        {{ data.condition }}
      </view>
    </view>
    <!--    右侧    -->
    <view class="coupon-card__right flex-1">
      <view class="flex flex-1 px-[20rpx]">
        <view class="flex py-[14rpx] flex-col justify-center flex-1">
          <!--  优惠券名称  -->
          <view class="text-[#101010] text-lg font-medium">
            {{ data.name }}
          </view>
          <!--  使用日期  -->
          <view class="text-content text-xs mt-[10rpx] w-[300rpx]">
            {{ data.effectiveTime }}
          </view>
        </view>
        <view class="other">
          <slot name="other"></slot>
        </view>
      </view>
      <slot name="useScene">
        <view
          class="text-xs text-content px-[20rpx] h-[60rpx] py-[10rpx]"
          style="border-top: 1px solid #eaeaea"
        >
          {{ data.useScene }}
        </view>
      </slot>
    </view>
  </view>
</template>

<script lang="ts" setup>
const emit = defineEmits(["click"]);

const props = withDefaults(
  defineProps<{
    data: any;
    mainStyle?: any;
  }>(),
  {
    data: {
      condition: "--",
      effectiveTime: "-",
      id: 0,
      isAble: 1,
      isReceive: 0,
      money: 0,
      name: "-",
      useScene: "-",
    },
    mainStyle: {
      color: "#FFFFFF",
    },
  }
);

const onClick = () => {
  emit("click");
};
</script>

<style lang="scss" scoped>
.coupon-card {
  margin: 0 24rpx;
  margin-top: 20rpx;

  &__left {
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: center;
    background: linear-gradient(
      89.7deg,
      var(--theme-dark-color) 0%,
      var(--theme-color) 100%
    );
  }

  &__right {
    background: #ffffff;
    position: relative;

    .other {
      position: absolute;
      top: 50%;
      right: 20rpx;
      transform: translateY(-50%);
    }
  }
}
</style>
