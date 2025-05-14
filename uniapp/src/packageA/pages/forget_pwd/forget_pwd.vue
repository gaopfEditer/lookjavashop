<template>
  <view
    class="register bg-white min-h-full flex flex-col items-center px-[40rpx] pt-[100rpx] box-border"
    :style="$theme.pageStyle"
  >
    <view class="w-full">
      <view class="text-2xl font-medium mb-[60rpx]">忘记登录密码</view>
      <view
        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
      >
        <u-input
          class="flex-1"
          v-model="formData.mobile"
          :border="false"
          placeholder="请输入手机号码"
        />
      </view>
      <view
        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
      >
        <u-input
          class="flex-1"
          v-model="formData.code"
          placeholder="请输入验证码"
          :border="false"
        />
        <view
          class="border-l border-solid border-0 border-light pl-3 text-muted leading-4 ml-3 w-[180rpx]"
          @click="sendSms"
        >
          <u-verification-code
            ref="uCodeRef"
            :seconds="60"
            @change="codeChange"
            change-text="x秒"
          />
          <text :class="formData.mobile ? 'text-primary' : 'text-muted'">
            {{ codeTips }}
          </text>
        </view>
      </view>
      <view
        class="px-[18rpx] border border-solid border-lightc border-light rounded-[10rpx] h-[100rpx] items-center flex mt-[40rpx]"
      >
        <u-input
          class="flex-1"
          type="password"
          v-model="formData.password"
          placeholder="输入新密码"
          :border="false"
        />
      </view>
      <view class="mt-[60rpx]">
        <u-button
          type="primary"
          hover-class="none"
          @click="handleConfirm"
          :customStyle="{
            height: '100rpx',
            opacity:
              formData.mobile && formData.password && formData.code
                ? '1'
                : '0.5',
          }"
        >
          立即重置密码
        </u-button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { forgotPassword } from "@/api/account";
import { smsSend } from "@/api/app";
import { SMSEnum } from "@/enums/appEnums";
import { reactive, ref, shallowRef } from "vue";

const uCodeRef = shallowRef();
const codeTips = ref("");
const formData = reactive({
  mobile: "",
  code: "",
  password: "",
  //   password2: "",
});

const codeChange = (text: string) => {
  codeTips.value = text;
};

const sendSms = async () => {
  if (!formData.mobile) return;
  if (uCodeRef.value?.canGetCode) {
    await smsSend({
      scene: SMSEnum.FIND_PASSWORD,
      mobile: formData.mobile,
    });
    uni.$u.toast("发送成功");
    uCodeRef.value?.start();
  }
};

const handleConfirm = async () => {
  if (!formData.mobile) return uni.$u.toast("请输入手机号码");
  if (!formData.password) return uni.$u.toast("请输入密码");
  //   if (!formData.password2) return uni.$u.toast("请输入确认密码");
  //   if (formData.password != formData.password2)
  //     return uni.$u.toast("两次输入的密码不一致");
  await forgotPassword(formData);
  uni.$u.toast("操作成功");
  uni.navigateBack();
};
</script>

<style lang="scss">
page {
  height: 100%;
}
</style>
