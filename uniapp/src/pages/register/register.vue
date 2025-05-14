<template>
  <view
    class="register bg-white min-h-full flex flex-col items-center px-[40rpx] pt-[40rpx] box-border"
    :style="$theme.pageStyle"
  >
    <view class="w-full">
      <view class="text-2xl font-medium mb-[60rpx]">注册新账号</view>
      <view
        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex"
      >
        <u-input
          class="flex-1"
          v-model="formData.username"
          :border="false"
          placeholder="请输入账号"
        />
      </view>
      <view
        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
      >
        <u-input
          class="flex-1"
          type="password"
          v-model="formData.password"
          placeholder="请输入密码"
          :border="false"
        />
      </view>
      <view
        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
      >
        <u-input
          class="flex-1"
          type="password"
          v-model="formData.password2"
          placeholder="请确认密码"
          :border="false"
        />
      </view>

      <view class="mt-[40rpx]" v-if="isOpenAgreement">
        <u-checkbox v-model="isCheckAgreement" shape="circle">
          <view class="text-xs flex">
            已阅读并同意
            <view @click.stop>
              <navigator
                class="text-primary"
                hover-class="none"
                url="/packageA/pages/agreement/agreement?type=service"
              >
                《服务协议》
              </navigator>
            </view>

            和
            <view @click.stop>
              <navigator
                class="text-primary"
                hover-class="none"
                url="/packageA/pages/agreement/agreement?type=privacy"
              >
                《隐私协议》
              </navigator>
            </view>
          </view>
        </u-checkbox>
      </view>
      <view class="mt-[60rpx]">
        <u-button
          type="primary"
          hover-class="none"
          @click="accountRegister"
          :customStyle="{
            height: '100rpx',
            opacity:
              formData.username && formData.password && formData.password2
                ? '1'
                : '0.5',
          }"
        >
          注册
        </u-button>
      </view>
    </view>
    <!-- 协议弹框 -->
    <u-modal
      v-model="showModel"
      show-cancel-button
      :show-title="false"
      @confirm="(isCheckAgreement = true), (showModel = false)"
      @cancel="showModel = false"
      :confirm-color="$theme.primaryColor"
    >
      <view class="text-center px-[70rpx] py-[60rpx]">
        <view> 请先阅读并同意 </view>
        <view class="flex justify-center">
          <navigator
            data-theme=""
            url="/pages/agreement/agreement?type=service"
          >
            <view class="text-primary">《服务协议》</view>
          </navigator>
          和
          <navigator url="/pages/agreement/agreement?type=privacy">
            <view class="text-primary">《隐私协议》</view>
          </navigator>
        </view>
      </view>
    </u-modal>
  </view>
</template>

<script setup lang="ts">
import { register } from "@/api/account";
import { useAppStore } from "@/stores/app";
import { computed, reactive, ref } from "vue";
import cache from "@/utils/cache";
import { SHARE_CODE } from "@/enums/cacheEnums";

const isCheckAgreement = ref(false);
const appStore = useAppStore();
const isOpenAgreement = computed(
  () => appStore.getLoginConfig.openAgreement == 1
);
const formData = reactive({
  username: "",
  password: "",
  password2: "",
});
const showModel = ref(false);

const accountRegister = async () => {
  if (!formData.username) return uni.$u.toast("请输入账号");
  if (!formData.password) return uni.$u.toast("请输入密码");
  if (!formData.password2) return uni.$u.toast("请输入确认密码");
  if (!isCheckAgreement.value && isOpenAgreement.value)
    return (showModel.value = true);
  if (formData.password != formData.password2)
    return uni.$u.toast("两次输入的密码不一致");

  const share_code = cache.get(SHARE_CODE) || "";
  await register({
    ...formData,
    inviteCode: share_code,
  });
  cache.remove(SHARE_CODE);
  uni.$u.toast("注册成功");
  uni.navigateBack();
};
</script>

<style lang="scss">
page {
  height: 100%;
}
</style>
