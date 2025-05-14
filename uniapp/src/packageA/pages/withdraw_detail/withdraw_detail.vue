<template>
  <view class="withdraw-detail" :style="$theme.pageStyle">
    <view class="bg-white withdraw-detail-item rounded-[14rpx]">
      <view class="whether-pass" v-if="formData.status == 0">
        <u-icon
          name="clock-fill"
          size="140"
          :color="$theme.primaryColor"
        ></u-icon>
        <view class="mt-[12rpx]">{{ formData.statusMsg }}</view>
      </view>

      <view class="whether-pass" v-if="formData.status == 1">
        <u-icon
          name="clock-fill"
          size="140"
          :color="$theme.primaryColor"
        ></u-icon>
        <view class="mt-[12rpx]">{{ formData.statusMsg }}</view>
      </view>

      <view class="whether-pass" v-if="formData.status == 3">
        <u-icon
          name="checkmark-circle-fill"
          size="140"
          :color="$theme.primaryColor"
        ></u-icon>
        <view class="mt-[12rpx]">{{ formData.statusMsg }}</view>
      </view>

      <view class="whether-pass" v-if="formData.status == 4">
        <u-icon name="close-circle-fill" size="140" color="#ff2c3c"></u-icon>
        <view class="mt-[12rpx]">{{ formData.statusMsg }}</view>
      </view>

      <view class="withdrawal-money">
        <view class="withdrawal-money-icon"> ¥ </view>
        <view class="withdrawal-money-text"> {{ formData.money }} </view>
      </view>

      <view class="ml-[30rpx] mt-[20rpx] mr-[30rpx]">
        <view class="flex justify-between withdrawal-content">
          <view>提现单号</view>
          <view>{{ formData.sn }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>申请时间</view>
          <view>{{ formData.createTime }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>提现至</view>
          <view>{{ formData.typeMsg }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>服务费</view>
          <view>{{ formData.handlingFee }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>实际到账</view>
          <view>{{ formData.leftMoney }}</view>
        </view>
      </view>

      <!-- 银行卡提现 -->
      <view
        class="mx-[30rpx] pt-[20rpx]"
        style="border-top: 1rpx solid #e5e5e5"
        v-if="formData.type == 3"
      >
        <view class="flex justify-between withdrawal-content">
          <view>银行卡账号</view>
          <view>{{ formData.account }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>持卡人姓名</view>
          <view>{{ formData.realName }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>提现银行</view>
          <view>{{ formData.bank }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>银行支行</view>
          <view>{{ formData.subBank }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>备注说明</view>
          <view>{{ formData.applyRemark }}</view>
        </view>
      </view>

      <!-- 支付宝收款码提现 -->
      <view
        class="mx-[30rpx] pt-[20rpx]"
        style="border-top: 1rpx solid #e5e5e5"
        v-if="formData.type == 5"
      >
        <view class="flex justify-between withdrawal-content">
          <view>支付宝账号</view>
          <view>{{ formData.account }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>真实姓名</view>
          <view>{{ formData.realName }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>支付宝收款码</view>
          <u-image
            height="160"
            width="160"
            :src="formData.moneyQrCode"
            @click="showImage([formData.moneyQrCode])"
          >
          </u-image>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>备注说明</view>
          <view>{{ formData.applyRemark }}</view>
        </view>
      </view>

      <!-- 微信收款码提现 -->
      <view
        class="mx-[30rpx] pt-[20rpx]"
        style="border-top: 1rpx solid #e5e5e5"
        v-if="formData.type == 4"
      >
        <view class="flex justify-between withdrawal-content">
          <view>微信账号</view>
          <view>{{ formData.account }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>真实姓名</view>
          <view>{{ formData.realName }}</view>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>微信收款码</view>
          <u-image
            height="160"
            width="160"
            :src="formData.moneyQrCode"
            @click="showImage([formData.moneyQrCode])"
          >
          </u-image>
        </view>
        <view class="flex justify-between withdrawal-content">
          <view>备注说明</view>
          <view>{{ formData.applyRemark }}</view>
        </view>
      </view>

      <!-- 转账凭证 -->
      <view
        class="mx-[30rpx] pt-[20rpx]"
        style="border-top: 1rpx solid #e5e5e5"
        v-if="formData.status == 3 || formData.status == 4"
      >
        <view class="flex justify-between withdrawal-content">
          <view>转账凭证</view>
          <u-image
            height="160"
            width="160"
            :src="formData.transferVoucher"
            v-if="formData.transferVoucher"
            @click="showImage([formData.transferVoucher])"
          >
          </u-image>
        </view>

        <view class="flex justify-between withdrawal-content">
          <view>转账说明</view>
          <view>{{ formData.transferRemark || "-" }}</view>
        </view>
      </view>
    </view>

    <view class="check-withdrawal-record">
      <button
        class="bg-primary h-[88rpx] leading-[88rpx] rounded-[30px] text-white"
        @click="toRecord"
      >
        查看历史提现记录
      </button>

      <view class="mt-[20rpx]">
        <button
          class="plain-primary h-[88rpx] leading-[88rpx] rounded-[30px] tohome"
          @click="toHome"
        >
          返回首页
        </button>
      </view>
    </view>

    <view class="review-success-tips"
      >* 审核成功后约72小时内到账，请留意账户明细</view
    >

    <!-- 页面状态 -->
    <PageStatus ref="pageRef"></PageStatus>
  </view>
</template>

<script lang="ts" setup>
import { nextTick, reactive, ref, shallowRef, unref } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { userWithdrawDetail } from "@/api/wallet/withdraw";
import PageStatus from "@/components/page-status/page-status.vue";
import { useRouter } from 'uniapp-router-next'

const router = useRouter()
const pageRef = shallowRef();
const withdrawId = ref<number>(0);

const formData = ref<any>({
  //   type: "", // 提现类型：1-钱包余额；2-微信零钱；3-银行卡;4-微信收款码;5-支付宝收款码
  //   typeMsg: "", // 提现类型描述
  //   status: "", // 提现状态:0-待提现1-提现中2-提现成功3-提现失败
  //   statusMSg: "", // 提现状态描述
  //   money: "", // 是 提现金额
  //   bank: "", // 否 type=3时需要 银行
  //   subBank: "", // 否 type=3时需要 支行
  //   bank_account: "", // 否 type=3时需要 账号
  //   bank_account_name: "", // 否 type=3时需要 持卡人姓名
  //   wechat_account: "", // 否 type=4时需要 微信账号
  //   wechat_account_name: "", // 否 type=4时需要 微信真实姓名
  //   wechat_qr_code: [], // 否 type=4时需要 微信二维码
  //   alipay_account: "", // 否 type=5时需要 支付宝账号
  //   alipay_account_name: "", // 否 type=5时需要 支付宝真实姓名
  //   alipay_qr_code: [], // 否 type=5时需要 支付宝收款码
  //   remark: "", // 否 提现备注
});

// 获取提现申请详情
const getWithdrawDetail = async () => {
  try {
    const data = await userWithdrawDetail({
      id: withdrawId.value,
    });
    // Reflect.ownKeys(data).map((item: any) => {
    //   formData[item] = data[item];
    // });
    formData.value = data;
    unref(pageRef).close();
  } catch (error) {
    unref(pageRef).show({
      text: error,
      mode: "order",
    });
    console.log("获取提现详情", error);
  }
};

const toRecord = () => {
    router.redirectTo("/packageA/pages/withdraw_record/withdraw_record")
};

const toHome = () => {
    router.reLaunch("/pages/index/index");
};

const showImage = (list: any) => {
  uni.previewImage({
    urls: list,
    current: 1,
  });
};

onLoad(async (options: any) => {
  await nextTick();
  try {
    if (!options?.id) {
      throw new Error("请传入详情ID");
    }
    withdrawId.value = options?.id || "";
    await getWithdrawDetail();
  } catch (error) {
    unref(pageRef).show({
      text: error,
      mode: "order",
    });
    console.log("商品详情报错", error);
  }
});
</script>

<style lang="scss" scoped>
.withdraw-detail {
  padding: 20rpx;
  padding-bottom: 100px;

  .withdraw-detail-item {
    .whether-pass {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      font-size: 34rpx;
      font-weight: 500;
      padding-top: 40rpx;

      image {
        width: 100rpx;
        height: 100rpx;
      }
    }

    .withdrawal-money {
      display: flex;
      justify-content: center;
      align-items: center;
      color: #ff2c3c;
      margin-top: 20rpx;
      padding-bottom: 30rpx;

      .withdrawal-money-icon {
        font-size: 30rpx;
      }

      .withdrawal-money-text {
        font-size: 46rpx;
      }
    }

    .withdrawal-content {
      padding-bottom: 24rpx;
    }
  }

  .check-withdrawal-record {
    padding: 40rpx 0rpx 40rpx 0;
    margin: 0 30rpx;
  }

  .review-success-tips {
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 24rpx;
    color: #999;
  }
}
button::after {
  border: none;
}
.tohome {
  background-color: white !important;
}
</style>
