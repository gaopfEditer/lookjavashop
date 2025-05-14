<template>
  <div class="edit-popup">
    <popup
      ref="popupRef"
      title="提现详情"
      :async="false"
      width="550px"
      @close="handleClose"
    >
      <div>
        <el-row class="leading-[40px]">
          <el-col :span="12">
            <span class="mx-4">用户账号: </span>
            <span>{{ withdraw.username }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">提现金额: </span>
            <span>{{ withdraw.money }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">用户昵称: </span>
            <span>{{ withdraw.nickname }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">提现手续费: </span>
            <span>{{ withdraw.handlingFee }}</span>
          </el-col>
          <el-col :span="24">
            <span class="mx-4">手机号码: </span>
            <span>{{ "-" }}</span>
          </el-col>
          <el-col :span="24">
            <span class="mx-4">到账金额: </span>
            <span>{{ withdraw.leftMoney }}</span>
          </el-col>
          <el-col :span="24">
            <span class="mx-4">申请时间: </span>
            <span>{{ withdraw.createTime }}</span>
          </el-col>
          <el-col :span="24">
            <span class="mx-4">提现方式: </span>
            <span>{{ withdraw.typeMsg }}</span>
          </el-col>
        </el-row>
      </div>

      <!--  银行卡  -->
      <div class="mt-2" v-if="withdraw.type == 3">
        <div class="py-2 px-2 font-medium text-base">银行卡收款信息</div>
        <el-row class="leading-[40px]">
          <el-col :span="12">
            <span class="mx-4">银行卡账号: </span>
            <span>{{ withdraw.account }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">持卡人姓名: </span>
            <span>{{ withdraw.realName }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">提现银行: </span>
            <span>{{ withdraw.bank }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">银行支行称: </span>
            <span>{{ withdraw.subBank }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">提现备注: </span>
            <span>{{ withdraw.applyRemark }}</span>
          </el-col>
        </el-row>
      </div>

      <!--  微信收款  -->
      <div class="mt-2" v-if="withdraw.type == 4">
        <div class="py-2 px-2 font-medium text-base">微信收款信息</div>
        <el-row class="leading-[40px]">
          <el-col :span="12">
            <span class="mx-4">微信账号: </span>
            <span>{{ withdraw.account }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">真实姓名: </span>
            <span>{{ withdraw.realName }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">收款码: </span>
            <image-contain
              class="flex-none mr-2"
              v-if="withdraw.moneyQrCode"
              :src="withdraw.moneyQrCode"
              :width="44"
              :height="44"
              :preview-src-list="[withdraw.moneyQrCode]"
              :preview-teleported="true"
              fit="cover"
            />
          </el-col>
          <el-col :span="12">
            <span class="mx-4">提现备注: </span>
            <span>{{ withdraw.applyRemark }}</span>
          </el-col>
        </el-row>
      </div>

      <!--  支付宝收款  -->
      <div class="mt-2" v-if="withdraw.type == 5">
        <div class="py-2 px-2 font-medium text-base">支付宝收款信息</div>
        <el-row class="leading-[40px]">
          <el-col :span="12">
            <span class="mx-4">支付宝账号: </span>
            <span>{{ withdraw.account }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">真实姓名: </span>
            <span>{{ withdraw.realName }}</span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">收款码: </span>
            <image-contain
              class="flex-none mr-2"
              v-if="withdraw.moneyQrCode"
              :src="withdraw.moneyQrCode"
              :width="44"
              :height="44"
              :preview-src-list="[withdraw.moneyQrCode]"
              :preview-teleported="true"
              fit="cover"
            />
          </el-col>
          <el-col :span="12">
            <span class="mx-4">提现备注: </span>
            <span>{{ withdraw.applyRemark }}</span>
          </el-col>
        </el-row>
      </div>

      <div class="mt-2">
        <div class="py-2 px-2 font-medium text-base">提现信息</div>
        <el-row class="leading-[40px]">
          <el-col :span="12">
            <span class="mx-4">提现状态: </span>
            <span class="text-[#f69b19]" v-if="withdraw.status == 1">
              {{ withdraw.statusMsg }}
            </span>
            <span class="text-primary" v-if="withdraw.status == 2">
              {{ withdraw.statusMsg }}
            </span>
            <span class="text-[#59fa61]" v-if="withdraw.status == 3">
              {{ withdraw.statusMsg }}
            </span>
            <span class="text-[#f04f4f]" v-if="withdraw.status == 4">
              {{ withdraw.statusMsg }}
            </span>
          </el-col>
          <el-col :span="12">
            <span class="mx-4">审核说明: </span>
            <span>{{ withdraw.auditRemark || "-" }}</span>
          </el-col>
          <el-col :span="12" class="flex">
            <span class="mx-4">转账凭证: </span>
            <image-contain
              class="flex-none mr-2"
              v-if="withdraw.transferVoucher"
              :src="withdraw.transferVoucher"
              :width="44"
              :height="44"
              :preview-src-list="[withdraw.transferVoucher]"
              preview-teleported
              fit="cover"
            />
          </el-col>
          <el-col :span="12">
            <span class="mx-4">转账时间: </span>
            <span>{{ "-" }}</span>
          </el-col>
          <el-col :span="24">
            <span class="mx-4">转账说明: </span>
            <span>{{ withdraw.transferRemark || "-" }}</span>
          </el-col>
        </el-row>
      </div>
    </popup>
  </div>
</template>
<script lang="ts" setup>
import { getWithdrawDetail } from "@/api/finance";
import Popup from "@/components/popup/index.vue";
const emit = defineEmits(["success", "close"]);
import useAppStore from "@/stores/modules/app";
const popupRef = shallowRef<InstanceType<typeof Popup>>();
const mode = ref("add");
const { getImageUrl } = useAppStore();
const withdraw = ref<Record<string, any>>({
  //   id: 0,
  //   create_time: "",
  //   update_time: "",
  //   delete_time: "",
  //   sn: "",
  //   batch_no: "",
  //   type: 3,
  //   status: 2,
  //   money: "",
  //   real_money: "",
  //   service_money: "",
  //   bank_name: "",
  //   bank_sub_name: "",
  //   bank_account: "",
  //   bank_account_name: "",
  //   wechat_account: "",
  //   wechat_account_name: "",
  //   wechat_qr_code: "",
  //   wechat_pay_message: [],
  //   wechat_pay_result: [],
  //   wechat_pay_no: "",
  //   wechat_pay_time: "",
  //   alipay_account: "",
  //   alipay_account_name: "",
  //   alipay_qr_code: "",
  //   remark: "",
  //   audit_content: "",
  //   transfer_voucher: "",
  //   transfer_time: "",
  //   transfer_content: "",
  //   user: {
  //     id: "",
  //     sn: "",
  //     nickname: "",
  //     account: "",
  //     mobile: "",
  //   },
  //   status_text: "",
  //   type_text: "",
  //   can_audit: 0,
  //   can_transfer: 0,
});

const open = (id: number) => {
  getDetail({ id });
  popupRef.value?.open();
};

// const setFormData = (data: Record<string, any>) => {
//   for (const key in withdraw) {
//     if (data[key] != null && data[key] != undefined) {
//       withdraw[key] = data[key];
//     }
//   }
// };

const getDetail = async (row: Record<string, any>) => {
  const data: Record<string, any> = await getWithdrawDetail({
    id: row.id,
  });
  withdraw.value = data;
  //   setFormData(data)
};

const handleClose = () => {
  emit("close");
};

defineExpose({ open, getDetail });
</script>
