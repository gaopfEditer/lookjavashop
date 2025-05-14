<template>
  <div class="shadow mx-[30px] pages-preview">
    <el-scrollbar>
      <draggable
        class="draggable"
        v-model="fileList"
        @change="changeMaterialSort"
        animation="300"
        item-key="id"
      >
        <template v-slot:item="{ element, index }">
          <div
            :key="element.id"
            class="relative"
            :class="{
              'cursor-pointer': !element?.disabled,
            }"
            @click="handleClick(element, index)"
          >
            <div
              class="absolute w-full h-full z-[100] border-dashed"
              :class="{
                select: index == modelValue,
                'border-[#dcdfe6] border-2': !element?.disabled,
              }"
            ></div>
            <slot>
              <component
                :is="widgets[element?.name]?.content"
                :content="element.content"
                :styles="element.styles"
                :key="element.id"
              />
            </slot></div
        ></template>
      </draggable>
    </el-scrollbar>
  </div>
</template>
<script lang="ts" setup>
import widgets from "../widgets";
import type { PropType } from "vue";
import Draggable from "vuedraggable";

const props = defineProps({
  pageData: {
    type: Array as PropType<any[]>,
    default: () => [],
  },
  modelValue: {
    type: Number,
    default: 0,
  },
});

const emit = defineEmits<{
  (event: "update:modelValue", value: number): void;
  (event: "changeIndex", value: object): void;
}>();

const handleClick = (widget: any, index: number) => {
  if (widget.disabled) return;
  emit("update:modelValue", index);
};

const fileList = computed(()=>{
    return props.pageData
});
const changeMaterialSort = (ret: any) => {
  emit("changeIndex", ret);
  console.log(77777723456, ret);
};
</script>

<style lang="scss" scoped>
.pages-preview {
  background-color: #f8f8f8;
  width: 360px;
  height: 615px;
  color: #333;
  .select {
    @apply border-primary border-solid;
  }
}
</style>
