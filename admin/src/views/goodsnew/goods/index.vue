<template>
  <div class="wrapper">
    <div class="content">商品列表</div>
  </div>
</template>

<script setup lang="ts">
import {
  ref,
  watch,
  computed,
  useTemplateRef,
  defineProps,
  onMounted,
} from "vue";
import type { GoodsListQueryParamsType } from "@/api/goods/goods.d";
import { usePaging } from "@/hooks/usePaging";
import { goodsNewLists } from "@/api/goods/goods";

const queryParams = reactive<GoodsListQueryParamsType>({
  name: "",
  code: "",
  categoryId: "",
  type: 0,
});
const { pager, getLists, resetPage, resetParams } = usePaging({
  fetchFun: goodsNewLists,
  params: queryParams,
});

onMounted(() => {
  getLists();
});
</script>

<style lang="scss" scoped>
.wrapper {
}
</style>