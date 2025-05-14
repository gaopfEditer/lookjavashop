<template>
  <view class="withdrawal-record" :style="$theme.pageStyle">
    <z-paging
      auto-show-back-to-top
      ref="paging"
      v-model="dataList"
      @query="queryList"
      :fixed="true"
      height="100%"
      default-page-size="20"
    >
      <RecordList :withdrawApplyLists="dataList" />
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { ref, shallowRef } from "vue";
import { userWithdrawList } from "@/api/wallet/withdraw";

import RecordList from "./component/record-list.vue";

const paging = shallowRef<any>(null);
const dataList = ref<any>([]);

const queryList = async (page_no: number, page_size: number) => {
  try {
    const { lists } = await userWithdrawList({
      page_no,
      page_size,
    });
    paging.value.complete(lists);
  } catch (e) {
    console.log("报错=>", e);
    //TODO handle the exception
    paging.value.complete(false);
  }
};
</script>

<style lang="scss" scoped></style>
