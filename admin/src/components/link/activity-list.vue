<template>
  <div class="activity_list mt-[30px]">
    <div class="flex flex-wrap items-center">
      选择热销分类
      <div class="ml-4 flex-1 min-w-[100px]">
        <el-select
          :model-value="modelValue.query?.id"
          @change="handleSelest"
          placeholder="选择"
        >
          <template v-for="(cateItem, cateIndex) in cateList" :key="cateIndex">
            <el-option :label="cateItem.name" :value="cateItem.id" />
          </template>
        </el-select>
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

const cateList = ref<any>([]);

const getData = async () => {
  try {
    const data: any = await goodsCategoryLists();
    cateList.value = data;
  } catch (error) {}
};

const handleSelest = (value: string) => {
  emit("update:modelValue", {
    path: "/packageA/pages/activity_list/activity_list",
    name: cateList.value.filter((item: any) => item.id == value)[0].name,
    query: {
      id: value,
      sort: "hot",
    },
    type: LinkTypeEnum.ACTIVITY_LIST,
  });
};

getData();
</script>
