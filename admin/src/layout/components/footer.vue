<template>
  <footer class="layout-footer">
    <div>
      <!-- <div class="text-center p-2 text-xs text-tx-secondary max-w-[900px] mx-auto">
                <a
                    class="mx-3 hover:underline"
                    :href="item.link"
                    target="_blank"
                    v-for="item in bottomArr"
                    :key="item.name"
                >
                    {{ item.name }}
                </a>
            </div> -->
      <div
        class="text-center p-2 text-xs text-tx-secondary max-w-[900px] mx-auto"
      >
        <a
          class="mx-1 hover:underline"
          :href="item.link"
          target="_blank"
          v-for="item in copyright"
          :key="item.name"
        >
          {{ item.name }}
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import useAppStore from "@/stores/modules/app";

const bottomArr = [
  {
    link: "https://likeshop.cn",
    name: "官网",
  },
  {
    link: "https://www.likeshop.cn/doc",
    name: "文档",
  },
  {
    link: "https://gitee.com/likeshop_gitee/likeshop",
    name: "Gitee",
  },
];

const appStore = useAppStore();
const copyright = computed(() => appStore.config.copyright || []);
</script>
