<template>
  <page-meta :page-style="$theme.pageStyle">
    <!-- #ifndef H5 -->
    <navigation-bar
      :front-color="$theme.navColor"
      :background-color="$theme.navBgColor"
    />
    <!-- #endif -->
  </page-meta>
  <div :style="$theme.pageStyle">
    <view class="px-[30rpx] py-[20rpx] bg-white">
      <u-search v-model="keyWord" placeholder="请输入用户账号/昵称"></u-search>
    </view>
    <view>
      <tabs
        :current="current"
        :isScroll="false"
        :active-color="$theme.primaryColor"
        @change="handlechange"
      >
        <tab v-for="(item, index) in tabsList" :key="index" :name="item.label">
          <view class="fansList">
            <fansLists
              :keyWord="keyWord"
              :type="item.type"
              :index="current"
              :i="index"
            ></fansLists>
          </view>
        </tab>
      </tabs>
    </view>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
import fansLists from "@/packageA/pages/distribution_fans/components/fans_lists.vue";

const current = ref(0);
const keyWord = ref("");

const tabsList = ref([
  { label: "全部粉丝", type: "all" },
  { label: "一级粉丝", type: "first" },
  { label: "二级粉丝", type: "second" },
]);
const handlechange = (index: any) => {
  current.value = index;
};
</script>

<style scoped lang="scss">
.fansList {
  height: calc(100vh - 104rpx - 80rpx - env(safe-area-inset-bottom));
}
</style>
