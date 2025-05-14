<template>
  <el-card class="!border-none" shadow="never">
    <div class="font-medium">提现设置</div>
    <div class="mt-[20px]">
      <el-form label-width="120px" :model="formData">
        <el-form-item label="提现方法">
          <div>
            <el-checkbox-group v-model="formData.withdrawWay">
              <el-checkbox :label="1">账户余额（默认）</el-checkbox>
              <el-checkbox :label="2">微信零钱</el-checkbox>
              <el-checkbox :label="3">银行卡</el-checkbox>
              <el-checkbox :label="4">微信收款码</el-checkbox>
              <el-checkbox :label="5">支付宝收款码</el-checkbox>
            </el-checkbox-group>
            <div class="form-tips">默认需要保留一种提现方法</div>
          </div>
        </el-form-item>
        <el-form-item label="微信零钱接口">
          <div>
            <el-radio-group v-model="formData.transferWay" class="ml-4">
              <el-radio :label="1">商家转账到零钱</el-radio>
            </el-radio-group>
            <div class="form-tips">
              选择商家转账到零钱时，运营账户必须有钱才能提现
            </div>
          </div>
        </el-form-item>
        <el-form-item label="最低提现金额">
          <div>
            <el-input class="w-[240px]" v-model="formData.minMoney">
              <template #append>元</template>
            </el-input>
            <div class="form-tips">
              用户提现需满足最低提现金额，才能提交提现申请
            </div>
          </div>
        </el-form-item>
        <el-form-item label="最高提现金额">
          <div>
            <el-input class="w-[240px]" v-model="formData.maxMoney">
              <template #append>元</template>
            </el-input>
            <div class="form-tips">用户提现允许的最高提现金额</div>
          </div>
        </el-form-item>
        <el-form-item label="提现手续费">
          <div>
            <el-input class="w-[240px]" v-model="formData.serviceCharge">
              <template #append>%</template>
            </el-input>
            <div class="form-tips">用户提现时收取的手续费占比</div>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
  <FooterBtns>
    <el-button type="primary" @click="handleSave">保存</el-button>
  </FooterBtns>
</template>

<script setup lang="ts">
import { withdraw, savewithdraw } from "@/api/setting/user";
const formData = ref<any>({
  withdrawWay: [],
  transferWay: "",
  minMoney: "",
  maxMoney: "",
  serviceCharge: "",
});
const getData = async () => {
  formData.value = await withdraw();
};
getData();
const handleSave = async () => {
  formData.value.withdrawWay = formData.value.withdrawWay.join(",");

  await savewithdraw(formData.value);
  getData();
};
</script>

<style lang="scss" scoped></style>
