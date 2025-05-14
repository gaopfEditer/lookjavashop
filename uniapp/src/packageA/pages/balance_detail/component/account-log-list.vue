<template>
  <view class="main">
    <z-paging
      auto-show-back-to-top
      :auto="i == index"
      ref="paging"
      v-model="accountLogList"
      :data-key="i"
      @query="queryList"
      :fixed="false"
      height="100%"
      default-page-size="20"
    >
      <!-- 订单卡片 -->
      <view class="list">
        <view
          v-for="item in accountLogList"
          :key="item.id"
          class="bg-white item"
        >
          <view class="flex justify-between text-lg">
            <view class="normal mb-[10rpx]">{{
              item?.change_type_text || item.tips
            }}</view>
            <view
              class="text-2xl text-primary"
              v-if="item?.account_status === 1 || item?.action == 1"
              >+{{ item.change_amount || item.orderAmount }}</view
            >
            <view class="text-2xl text-main text-[red]" v-else
              >-{{ item.change_amount || item.orderAmount }}
            </view>
          </view>
          <view class="text-xs text-muted">{{
            item.create_time || item.createTime
          }}</view>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { ref, shallowRef, watch, nextTick, unref } from "vue";
import { apiAccountLogLists } from "@/api/user";
import { accountLog } from "@/api/user";

const props = withDefaults(
  defineProps<{
    changeObj: number;
    type?: any;
    i: number;
    index: number;
  }>(),
  {
    changeObj: 0,
    type: 1,
    i: 0,
    index: 0,
  }
);

const isFirst = ref<boolean>(true);
const accountLogList: any = ref([]);
// 下拉组件的Ref
const paging = shallowRef();

watch(
  () => props.index,
  async () => {
    await nextTick();
    if (props.i == props.index && unref(isFirst)) {
      isFirst.value = false;
      paging.value?.reload();
    }
  },
  { immediate: true }
);

const queryList = async (page_no: any, page_size: any) => {
  try {
    if (props.changeObj == 0) {
      const { lists } = await accountLog({
        page_no,
        page_size,
        type: props.type,
      });
      paging.value.complete(lists);
    } else {
      const { lists } = await apiAccountLogLists({
        page_no,
        page_size,
        type: props.type,
      });
      paging.value.complete(lists);
    }
  } catch (e) {
    console.log("下拉加载", e);
    paging.value.complete(false);
  }
};
</script>

<style lang="scss" scoped>
.main {
  height: calc(100vh - 46px - env(safe-area-inset-bottom));
  padding-top: 20rpx;

  .list {
    .item {
      padding: 20rpx 30rpx;

      &:not(:last-of-type) {
        border-bottom: 1rpx solid #e5e4e5;
      }
    }
  }
}
</style>
