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
      <u-search
        v-model="keyWord"
        placeholder="请输入商品名称/订单编号/用户昵称"
      ></u-search>
    </view>
    <view>
      <tabs
        :current="current"
        :isScroll="false"
        :active-color="$theme.primaryColor"
        @change="handlechange"
      >
        <tab v-for="(item, index) in tabsList" :key="index" :name="item.label">
          <view class="orderList">
            <OrderList
              :keyWord="keyWord"
              :status="item.status"
              :current="current"
              :i="index"
            ></OrderList>
          </view>
        </tab>
      </tabs>
    </view>
  </div>
</template>
<script setup lang="ts">
import { ref } from "vue";
import OrderList from "./components/orderList.vue";

const current = ref(0);
const keyWord = ref("");

const tabsList: any = ref([
  { label: "全部", status: 0 },
  { label: "待返佣", status: 1 },
  { label: "已结算", status: 2 },
  { label: "已失效", status: 3 },
]);
const handlechange = (index: any) => {
  current.value = index;
};
</script>

<style scoped lang="scss">
.orderList {
  height: calc(100vh - 104rpx - 80rpx - env(safe-area-inset-bottom));
}
</style>
