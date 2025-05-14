<template>
  <div class="activity_list mt-[30px]">
    <div class="flex flex-wrap items-center">
      选择商品分类
      <div class="ml-4 flex-1 min-w-[100px]">
        <el-cascader
          class="w-[280px]"
          ref="cascaderRef"
          :model-value="modelValue.query?.id"
          :options="cateList"
          :props="props"
          clearable
          filterable
          @change="handleSelest"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import type { PropType } from "vue";
import { LinkTypeEnum, type Link } from ".";
import { goodsCategoryLists } from "@/api/goods/category";

defineProps({
  modelValue: {
    type: Object as PropType<Link>,
    default: () => ({}),
  },
});
const emit = defineEmits<{
  (event: "update:modelValue", value: Link): void;
}>();

// 分类组件配置数据
const props = reactive({
  multiple: false,
  checkStrictly: true,
  label: "name",
  value: "id",
  children: "children",
  emitPath: false,
});
const cascaderRef = shallowRef();
const cateList = ref([]);

const getData = async () => {
  try {
    const data: any = await goodsCategoryLists();
    cateList.value = data;
  } catch (error) {}
};

const handleSelest = (value: string) => {
  const label = cascaderRef.value.getCheckedNodes()[0].text;
  emit("update:modelValue", {
    path: "/pages/search/search",
    name: label,
    query: {
      name: label,
      id: value,
    },
    type: LinkTypeEnum.CATEGORY_LIST,
  });
};

getData();
</script>
