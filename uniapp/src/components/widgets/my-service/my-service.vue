<template>
  <div class="my-service bg-white mx-[20rpx] mt-[20rpx] rounded-lg">
    <div
      v-if="content.title"
      class="title px-[30rpx] py-[20rpx] font-medium text-xl border-light border-solid border-0 border-b"
    >
      <div>{{ content.title }}</div>
    </div>
    <div v-if="content?.style == 1" class="flex flex-wrap pt-[40rpx] pb-[20rpx]">
      <div
        v-for="(item, index) in content.data"
        :key="index"
        @click="handleClick(item.link)"
        class="flex flex-col items-center w-1/4 mb-[15px]"
        v-show="handleShow(item.name)"
      >
        <u-image width="52" height="52" :src="getImageUrl(item.image)" alt="" />
        <div class="mt-[7px]">{{ item.name }}</div>
      </div>
    </div>
    <div v-if="content?.style == 2">
      <div
        v-for="(item, index) in content.data"
        :key="index"
        class="flex items-center"
        @click="handleClick(item.link)"
      >
        <div
          class="flex justify-between w-full items-center border-light border-solid border-0 border-b h-[100rpx] px-[24rpx]"
          v-if="handleShow(item.name)"
        >
          <u-image
            width="48"
            height="48"
            :src="getImageUrl(item.image)"
            alt=""
          />
          <div class="ml-[20rpx] flex-1">{{ item.name }}</div>
          <div class="text-muted">
            <u-icon name="arrow-right" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
import { useAppStore } from "@/stores/app";
import { navigateTo } from "@/utils/util";
import { distributionconfig } from "@/api/user";
import { ref } from "vue";
const props = defineProps({
  content: {
    type: Object,
    default: () => ({}),
  },
  styles: {
    type: Object,
    default: () => ({}),
  },
  user: {
    type: Object,
    default: () => ({}),
  },
});
const { getImageUrl } = useAppStore();
const handleClick = (link: any) => {
	console.log(props.content, link)
  navigateTo(link);
};
const distributionOpen = ref(1);
const Condition = ref(1);
const getData = async () => {
  const { open, openCondition } = await distributionconfig();
  distributionOpen.value = open;
  Condition.value = openCondition;
};
getData();
//TODO根据后台显示分销入口逻辑
const handleShow = (name: string) => {
  switch (name) {
    case "邀请海报":
      if (distributionOpen.value == 1) {
        if (Condition.value == 3) {
          if (props.user.isDistribution == 1) {
            return true;
          }
          return false;
        }
        if (props.user.isDistribution == 0) {
          return false;
        }
        return true;
      }
      break;
    case "分销推广":
      if (distributionOpen.value == 1) {
        if (Condition.value == 3) {
          if (props.user.isDistribution == 1) {
            return true;
          }
          return false;
        }
        return true;
      }
      break;
	case "核销订单":
		if ( props.user.isSelffetchVerifier == 1) {
			return true;
		} else {
			return false
		}
		break;
    default:
      return true;
      break;
  }
};
</script>

<style lang="scss"></style>
