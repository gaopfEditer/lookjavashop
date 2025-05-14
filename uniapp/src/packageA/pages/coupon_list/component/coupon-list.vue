<template>
  <z-paging
    auto-show-back-to-top
    :auto="i == index"
    ref="paging"
    v-model="couponList"
    :data-key="i"
    @query="queryList"
    :fixed="false"
    height="100%"
    empty-view-text="暂无内容～"
    :empty-view-img="EmptyCommentImg"
    :empty-view-style="{ 'margin-top': '100px' }"
    :empty-view-center="false"
    :auto-clean-list-when-reload="false"
    :auto-scroll-to-top-when-reload="false"
    :empty-view-img-style="{ width: '360rpx', height: '360rpx' }"
  >
    <!-- 可使用 -->
    <template v-if="type == 0">
      <coupon-card v-for="item in couponList" :key="item.id" :data="item">
        <template #other>
          <view class="w-[120rpx] flex items-end">
            <!--  领取  -->
            <button
              class="plain-primary use-btn"
              @click.stop="toGoodsCoupon(item)"
            >
              去使用
            </button>
          </view>
        </template>
      </coupon-card>
    </template>
    <!-- 已使用 -->
    <template v-if="type == 1">
      <coupon-card
        v-for="item in couponList"
        :key="item.id"
        :data="item"
        :mainStyle="{
          color: '#333',
          background: '#FFF',
        }"
      >
        <template #other></template>
      </coupon-card>
    </template>
    <!-- 已失效 -->
    <template v-if="type == 2">
      <coupon-card
        v-for="item in couponList"
        :key="item.id"
        :data="item"
        :mainStyle="{
          color: '#333',
          background: '#FFF',
        }"
      >
        <template #other></template>
      </coupon-card>
    </template>
  </z-paging>
</template>

<script lang="ts" setup>
import { myCouponLists } from "@/api/marketing/coupon";
import { ref, shallowRef, watch, nextTick, unref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import EmptyCommentImg from "../../../static/empty/comment.png";
import CouponCard from "@/components/coupon-card/coupon-card.vue";
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const emit = defineEmits(["extend"]);

const props = withDefaults(
  defineProps<{
    type?: number; // 底部
    i: number;
    index: number;
  }>(),
  {
    type: 1,
    i: 0,
    index: 0,
  }
);

const couponList: any = ref([]);
const paging = shallowRef();
const isFirst = ref<boolean>(true);

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

const toGoodsCoupon = (row: any) => {
  // 全场通用的商品就直接回到首页
  if (row.useGoodsType == 1) {
    router.reLaunch( "/pages/index/index");
    return;
  }
  const payload = {
    id: row.id,
    type: row.useGoodsType,
  };
  // 指定商品可用｜不可用的则跳转
  router.navigateTo(`/packageA/pages/coupon_goods/coupon_goods?payload=${JSON.stringify(
      payload
    )}`)
};

const queryList = async (pageNo: any, pageSize: any) => {
  try {
    const { lists, extend } = await myCouponLists({
      pageNo,
      pageSize,
      status: props.type,
    });
    emit("extend", extend);
    paging.value.complete(lists);
  } catch (e) {
    paging.value.complete(false);
  }
};

onShow(() => {
  // 刷新当前的
  if (props.i == 2 && props.index == 2) {
    paging.value?.reload();
  }
  if (props.i == 1 && props.index == 1) {
    paging.value?.reload();
  }
  if (props.i == 0 && props.index == 0) {
    paging.value?.reload();
  }
});
</script>

<style lang="scss" scoped>
.use-btn {
  padding: 0 16rpx;
  margin-bottom: 14rpx;
  border-radius: 60px;
  height: 52rpx;
  line-height: 52rpx;
  font-size: 26rpx;
  border: none !important;
}
</style>
