<template>
  <page-meta :page-style="$theme.pageStyle">
    <!-- #ifndef H5 -->
    <navigation-bar
      :front-color="$theme.navColor"
      :background-color="$theme.navBgColor"
    />
    <!-- #endif -->
  </page-meta>
  <view class="user-wallet p-[20rpx]" :style="$theme.pageStyle">
    <!-- Header -->
    <view class="p-[24rpx] bg-white header rounded-[14rpx]">
      <view
        class="bg-primary-linear rounded-[14rpx] text-white text-center py-[50rpx]"
      >
        <view>
          <view class="pb-[10rpx] text-base">我的余额</view>
          <!-- 总资产 -->
          <view class="text-[56rpx] font-medium">
            {{
              Number(wallet.userMoney + wallet.userEarnings).toFixed(2) ||
              "0.00"
            }}
          </view>
        </view>

        <view class="flex justify-around pt-[20rpx]">
          <view>
            <view class="pb-[10rpx] text-base">可用余额(元)</view>
            <!-- 余额 -->
            <view class="text-xl font-medium">
              {{ Number(wallet.userMoney).toFixed(2) || "0.00" }}
            </view>
          </view>
          <view>
            <view class="pb-[10rpx] text-base">可提现佣金(元)</view>
            <!-- 佣金 -->
            <view class="text-xl font-medium">
              {{ Number(wallet.userEarnings).toFixed(2) || "0.00" }}
            </view>
          </view>
        </view>
      </view>

      <view class="flex w-full mt-[24rpx]">
        <view
          class="w-[50%] mx-[10rpx]"
          @click="goPage('/packageA/pages/user_charge/user_charge')"
        >
          <button class="flex-1 bg-[#ECECEC] text-black">去充值</button>
        </view>
        <view
          class="flex-1 mx-[10rpx]"
          @click="goPage('/packageA/pages/withdraw_deposit/withdraw_deposit')"
        >
          <button class="bg-primary-linear flex-1 text-white">提现</button>
        </view>
      </view>
    </view>

    <!-- Main -->
    <view
      class="flex flex-wrap mt-[20rpx] text-center bg-white rounded-[14rpx] p-[20rpx]"
    >
      <view
        class="w-1/3 py-[20rpx]"
        @click="goPage(`/packageA/pages/balance_detail/balance_detail?type=1`)"
      >
        <image
          class="w-[54rpx] h-[54rpx]"
          src="@/packageA/static/wallet/icon_balance_detail.png"
        />
        <view class="text">账户明细</view>
      </view>
      <view
        class="w-1/3 py-[20rpx]"
        @click="goPage(`/packageA/pages/balance_detail/balance_detail?type=2`)"
      >
        <image
          class="w-[54rpx] h-[54rpx]"
          src="@/packageA/static/wallet/icon_commission_detail.png"
        />
        <view class="text">佣金明细</view>
      </view>
      <view
        class="w-1/3 py-[20rpx]"
        @click="goPage('/packageA/pages/user_charge_record/user_charge_record')"
      >
        <image
          class="w-[54rpx] h-[54rpx]"
          src="@/packageA/static/wallet/icon_charge_record.png"
        />
        <view class="text">充值记录</view>
      </view>
      <view
        class="w-1/3 py-[20rpx]"
        @click="goPage('/packageA/pages/withdraw_record/withdraw_record')"
      >
        <image
          class="w-[54rpx] h-[54rpx]"
          src="@/packageA/static/wallet/icon_withdraw_record.png"
        />
        <view class="text">提现记录</view>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { rechargeConfig } from "@/api/recharge";
import { useRouter } from "uniapp-router-next";

import { onShow } from "@dcloudio/uni-app";

const router = useRouter();
const wallet = reactive<any>({
  userMoney: "",
  all: "",
  distribution_money: "",
  openRecharge: 0,
  minRechargeMoney: 0,
});

const initWallet = async (): Promise<void> => {
  try {
    const data = await rechargeConfig();
    Reflect.ownKeys(data).map((item: any) => {
      wallet[item] = data[item];
    });
  } catch (e) {
    console.log("初始化钱包请求=>", e);
  }
};

const goPage = (url: string) => {
  router.navigateTo(url);
};

onShow(() => {
  initWallet();
});
</script>

<style lang="scss"></style>
