<!-- 限时秒杀 -->
<template>
  <page-meta :page-style="$theme.pageStyle">
    <!-- #ifndef H5 -->
    <navigation-bar
      :front-color="$theme.navColor"
      :background-color="$theme.navBgColor"
    />
    <!-- #endif -->
  </page-meta>
  <view class="seckill-list" :style="$theme.pageStyle">
    <z-paging
      ref="paging"
      v-model="seckill.lists"
      @query="queryList"
      :use-page-scroll="true"
      safe-area-inset-bottom
      :refresher-enabled="true"
      :auto-clean-list-when-reload="false"
      :auto-scroll-to-top-when-reload="false"
    >
      <l-swiper
        height="300"
        v-if="seckill.enabled"
        :content="seckill.pages[0]?.content"
        mode="none"
      />

      <view class="lists-wrap pt-[22rpx]">
        <view
          class="mb-[20rpx] mx-[24rpx]"
          v-for="item in seckill.lists"
          :key="item.seckillId"
        >
          <goods-card
            shape="rectangle"
            :image="item.goodsImage"
            :imageStyle="{ width: '180rpx', height: '180rpx' }"
            :name="item.goodsName"
            :containStyle="{ height: '250rpx', 'border-radius': '0' }"
          >
            <view class="text-xs mt-[10rpx]">
              <view class="sales-volume">
                <span class="sales-text"> 已抢 {{ item.salesVolume }} 件 </span>
              </view>
            </view>
            <view class="flex justify-between mt-[10rpx]">
              <price
                :content="item.seckillPrice"
                main-size="34rpx"
                minor-size="26rpx"
                fontWeight="500"
              />
              <button
                class="buy-btn bg-primary-linear text-white"
                @click="toGoodsSeckillDetail(item)"
              >
                立即抢购
              </button>
            </view>
          </goods-card>
        </view>
      </view>
    </z-paging>
  </view>
</template>

<script lang="ts" setup>
import { seckillLists } from "@/api/marketing/seckill";
import { getDecorate } from "@/api/shop";
import { onLoad, onReachBottom, onPageScroll } from "@dcloudio/uni-app";
import { reactive, shallowRef } from "vue";
import { GoodsTypeEnum } from "@/enums/goodsEnums";
import Price from "@/components/price/price.vue";
import GoodsCard from "@/components/goods-card/goods-card.vue";
import { useRouter } from "uniapp-router-next";
const router = useRouter();

const paging = shallowRef();
const seckill = reactive<any>({
  lists: [],
  pages: [],
  enabled: 1,
});

const toGoodsSeckillDetail = (row: any) => {
  const payload: any = {
    type: GoodsTypeEnum.SECKILL,
    activityId: row.seckillGId,
  };
  router.navigateTo({
    path: "/pages/goods_detail/goods_detail",
    query: {
      id: row.goodsId,
      payload: JSON.stringify(payload),
    },
  });
};

const getData = async () => {
  try {
    const data = await getDecorate({ id: 7 });
    seckill.pages = JSON.parse(data.pages);
    seckill.enabled = seckill.pages[0]?.content?.enabled;
  } catch (error) {
    console.log("获取装修错误", error);
  }
};

const queryList = async (pageNo: number, pageSize: number) => {
  try {
    const { lists } = await seckillLists({
      pageNo: pageNo,
      pageSize: pageSize,
    });
    paging.value.complete(lists);
  } catch (e) {
    console.log("报错=>", e);
    //TODO handle the exception
    paging.value.complete(false);
  }
};

onReachBottom(() => {
  paging.value.pageReachBottom();
});

onPageScroll(({ scrollTop }: any) => {
  paging.value?.updatePageScrollTop(scrollTop);
});

onLoad(() => {
  getData();
});
</script>

<style lang="scss" scoped>
.sales-volume {
  display: inline-block;
  padding: 4rpx 20rpx;
  border-radius: 20px;
  background: $u-type-primary-light;
  color: $u-type-primary;
  //   opacity: 0.1;
}

.buy-btn {
  //width: 160rpx;
  height: 58rpx;
  line-height: 58rpx;
  border-radius: 40px;
  font-size: 26rpx;
  // background: linear-gradient(90deg, #f95f2f 0%, #ff2c3c 100%);
}
</style>
