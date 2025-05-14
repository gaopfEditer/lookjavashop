<template>
  <div class="goods-lists">
    <el-form ref="formRef" :model="queryParams" :inline="true">
      <el-form-item label="商品名称">
        <el-input
          class="w-[280px]"
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="resetPage"
        >
          <template #append>
            <el-button @click="resetPage" :icon="Search" />
          </template>
        </el-input>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table
      size="large"
      v-loading="pager.loading"
      :data="pager.lists"
      height="270px"
      @row-click="handleSelectItem"
    >
      <el-table-column label="选择" min-width="60">
        <template #default="{ row }">
          <div class="flex row-center">
            <el-checkbox
              :model-value="getSelectItem(row.id)"
              @change="handleSelectItem(row)"
              size="large"
            ></el-checkbox>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品信息" min-width="300">
        <template #default="{ row }">
          <div class="flex">
            <el-image
              fit="cover"
              :src="row.image"
              class="flex-none w-[58px] h-[58px]"
            />
            <div class="ml-4 overflow-hidden">
              <el-tooltip effect="dark" :content="row.name" placement="top">
                <div class="text-base line-2">
                  {{ row.name }}
                </div>
              </el-tooltip>
              <el-tag checked v-if="row.specType == 2">多规格</el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="价格" prop="price" min-width="100">
        <template #default="{ row }"> ¥{{ row.sellPrice }} </template>
      </el-table-column>
      <el-table-column label="库存" prop="totalStock" min-width="90" />
    </el-table>

    <div class="flex justify-end mt-4">
      <pagination v-model="pager" @change="getLists" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { PropType } from "vue";
import { LinkTypeEnum, type Link } from ".";
import { usePaging } from "@/hooks/usePaging";
import { goodsLists } from "@/api/goods/goods";
// import type { GoodsList } from "@/api/goods/goods";
import { Search } from "@element-plus/icons-vue";

//TODO TODO
const props = defineProps({
  modelValue: {
    type: Object as PropType<any>,
    default: () => ({}),
  },
});
const emit = defineEmits<{
  (event: "update:modelValue", value: any): void;
}>();

const selectData = ref<any>({
  id: "",
  name: "",
});

const queryParams = reactive<any>({
  name: "",
});

const { pager, getLists, resetPage, resetParams } = usePaging({
  fetchFun: goodsLists,
  params: queryParams,
});

const getSelectItem = (id: number) => {
  return id == Number(selectData.value.id);
};
/**
 * @description 选择
 */
const handleSelectItem = (event: any) => {
  selectData.value = {
    id: event.id,
    name: event.name,
  };

  emit("update:modelValue", {
    type: "goods",
    list: selectData.value,
  });
};
watch(
  () => props.modelValue,
  (value) => {
    if (value.type != "goods") {
      return (selectData.value = {
        id: "",
        name: "",
      });
    }
    selectData.value = value.list;
  },
  {
    immediate: true,
  }
);
getLists();
</script>

<style lang="scss" scoped>
:deep(.el-input-group__append) {
  .el-button {
    margin: 0 0;
  }
}
</style>
