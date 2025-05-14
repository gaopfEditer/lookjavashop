<template>
  <view class="px-[30rpx] mb-[40rpx]" v-if="content?.enabled == 1">
    <view
      class="flex items-center bg-white p-[24rpx] rounded-t-lg"
      @click="toSeckill"
    >
      <view class="text-[34rpx] font-medium">{{ content?.title }}</view>
      <view class="text-[26rpx] text-[#999999] ml-[14rpx]">{{
        content?.subtitle
      }}</view>
      <view class="text-[24rpx] text-[#999999] ml-auto">更多 ></view>
    </view>
    <view class="rounded-b-lg">
      <vertical
        :content="content"
        :dataList="dataList"
        @toGoodsDetail="toGoodsDetial"
        v-if="content.sortType == '1'"
      >
      </vertical>
      <transverse
        :content="content"
        :dataList="dataList"
        @toGoodsDetail="toGoodsDetial"
        v-if="content.sortType == '2'"
      ></transverse>
      <plate
        :content="content"
        :dataList="dataList"
        @toGoodsDetail="toGoodsDetial"
        v-if="content.sortType == '3'"
      >
      </plate>
    </view>
  </view>
</template>

<script setup lang="ts">
import Vertical from "./components/vertical.vue";
import Transverse from "./components/transverse.vue";
import Plate from "./components/plate.vue";
import { seckillLists } from "@/api/marketing/seckill";
import { GoodsTypeEnum } from "@/enums/goodsEnums";
import { ref } from "vue";
import { useRouter } from "uniapp-router-next";
const router = useRouter();

const props = defineProps({
  content: {
    type: Array as any,
    default: () => [],
  },
});

const dataList: any = ref([1, 2]);

const getDataList = async () => {
  const res = await seckillLists();
  dataList.value = res.lists;
  console.log(dataList.value);
};

const toGoodsDetial = (row: any) => {
  try {
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
  } catch (error) {
    return;
  }
};

const toSeckill = () => {
  router.navigateTo("/packageA/pages/goods_seckill/goods_seckill");
};

getDataList();
</script>

<style scoped lang="scss"></style>
