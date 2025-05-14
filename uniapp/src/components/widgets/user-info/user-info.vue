<template>
  <view
    class="user-info flex px-[50rpx] justify-between py-[50rpx]"
    :style="{
      backgroundImage:
        content.background == 1
          ? ``
          : `url(${getImageUrl(content.backgroundurl)})`,
    }"
  >
    <view
      v-if="isLogin"
      class="flex items-center"
      @click="navigateTo('/packageA/pages/user_data/user_data')"
    >
      <u-avatar :src="user.avatar" :size="120"></u-avatar>
      <view class="text-white ml-[20rpx]">
        <view class="text-2xl">{{ user.nickname }}</view>
        <view class="text-xs mt-[18rpx]" @click.stop="copy(user.username)">
          账号：{{ user.username }}
        </view>
      </view>
    </view>
    <router-navigate
      v-else
      hover-class="none"
      to="/pages/login/login"
    >
      <view class="flex items-center">
          <u-avatar
              src="/static/images/user/default_avatar.png"
              :size="120"
          ></u-avatar>
          <view class="text-white text-3xl ml-[20rpx]">未登录</view>
      </view>
    </router-navigate>
    <router-navigate
      v-if="isLogin"
      hover-class="none"
      to="/packageA/pages/user_set/user_set"
    >
      <u-icon name="setting" color="#fff" :size="48"></u-icon>
    </router-navigate>
  </view>
</template>
<script lang="ts" setup>
import { useCopy } from "@/hooks/useCopy";
import { useAppStore } from "@/stores/app";
const { getImageUrl } = useAppStore();

import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const props = defineProps({
  content: {
    type: Object,
    default: () => ({}),
  },
  styles: {
    type: Object,
    default: () => ({}),
  },
  user: {
    type: Object,
    default: () => ({}),
  },
  isLogin: {
    type: Boolean,
  },
});
const { copy } = useCopy();
const navigateTo = (url: string) => {
  router.navigateTo(url);
};
</script>

<style lang="scss" scoped>
.user-info {
  // background: url("../../../static/images/user/my_topbg.png");
  height: 115px;
  background-position: bottom;
  background-size: 100% auto;
  position: relative;
  background: linear-gradient(
    90deg,
    var(--theme-color) 0,
    var(--theme-dark-color) 100%
  );

  // &::after {
  //     content: '';
  //     // background-color: aqua;

  //     position: absolute;
  //     top: 0;
  //     left: 0;
  //     width: 100%;
  //     height: 100%;
  //     z-index: -1;
  // }
}
</style>
