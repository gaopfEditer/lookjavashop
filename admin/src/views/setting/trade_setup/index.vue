<template>
  <div class="trade-setup">
    <el-card shadow="never" class="!border-none mb-4">
      <el-alert
        type="warning"
        title="温馨提示：设置库存、订单自动取消、完成时间等信息。"
        :closable="false"
        :show-icon="true"
      />
    </el-card>

    <el-form ref="formRef" :model="formData" label-width="140px">
      <el-card shadow="never" class="!border-none">
        <div class="font-medium mb-7">订单设置</div>

        <el-form-item label="系统自动取消订单">
          <div>
            <el-radio-group
              v-model="formData.isCancelUnpaidOrderTime"
              class="ml-4"
              @change="
                (value) => {
                  if (value == 1 && formData.cancelUnpaidOrderTime == -1) {
                    formData.cancelUnpaidOrderTime = 0;
                  }
                }
              "
            >
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
            <div class="w-60">
              <el-input
                v-model="formData.cancelUnpaidOrderTime"
                v-show="formData.isCancelUnpaidOrderTime"
                placeholder="请输入"
              >
                <template #append>分钟</template>
              </el-input>
            </div>
            <div class="form-tips">未付款订单多久时间后自动关闭</div>
          </div>
        </el-form-item>

        <el-form-item label="订单允许取消时长">
          <div>
            <el-radio-group
              v-model="formData.isCancelUnshippedOrderTime"
              class="ml-4"
              @change="
                (value) => {
                  if (value == 1 && formData.cancelUnshippedOrderTime == -1) {
                    formData.cancelUnshippedOrderTime = '';
                  }
                }
              "
            >
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
            <div class="w-60">
              <el-input
                v-model="formData.cancelUnshippedOrderTime"
                v-show="formData.isCancelUnshippedOrderTime"
                placeholder="请输入"
              >
                <template #append>分钟</template>
              </el-input>
            </div>
            <div class="form-tips">
              已支付但未发货/未提货的订单多久时间内允许用户自行取消
            </div>
          </div>
        </el-form-item>
        <!-- <el-form-item label="未发货申请退款">
                    <div>
                        <el-radio-group class="ml-4"  v-model="formData.beforeExpressRefund">
                            <el-radio :label="1">自动退款</el-radio>
                            <el-radio :label="0">审核后退款</el-radio>
                        </el-radio-group>
                        <div class="form-tips">
                            自动退款：用户在未发货/提货前提交的售后退款无需商家审核，自动退款成功 <br />
                            审核后退款：用户提交售后退款都需要商家进行审核才能进行退款
                        </div>
                    </div>
                </el-form-item> -->

        <el-form-item label="订单自动完成时长">
          <div>
            <el-radio-group
              v-model="formData.isAutoConfirmReceiptDay"
              class="ml-4"
              @change="
                (value) => {
                  if (value == 1 && formData.autoConfirmReceiptDay == -1) {
                    formData.autoConfirmReceiptDay = 0;
                  }
                }
              "
            >
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
            <div class="w-60">
              <el-input
                v-model="formData.autoConfirmReceiptDay"
                v-show="formData.isAutoConfirmReceiptDay"
                placeholder="请输入"
              >
                <template #append>天</template>
              </el-input>
            </div>
            <div class="form-tips">订单已发货多久时间后自动收货完成订单</div>
          </div>
        </el-form-item>

        <el-form-item label="买家售后维权时效">
          <div>
            <el-radio-group
              v-model="formData.isAfterSalesDay"
              class="ml-4"
              @change="
                (value) => {
                  if (value == 1 && formData.afterSalesDay == -1) {
                    formData.afterSalesDay = 0;
                  }
                }
              "
            >
              <el-radio :label="1">开启</el-radio>
              <el-radio :label="0">关闭</el-radio>
            </el-radio-group>
            <div class="w-60">
              <el-input
                v-model="formData.afterSalesDay"
                v-show="formData.isAfterSalesDay"
                placeholder="请输入"
              >
                <template #append>天</template>
              </el-input>
            </div>
            <div class="form-tips">
              订单<span class="text-[red]">确认收货之后</span
              >多久时间内可以进行售后维权
            </div>
          </div>
        </el-form-item>
      </el-card>

      <el-card shadow="never" class="!border-none mt-4">
        <div class="font-medium mb-7">库存设置</div>

        <el-form-item label="库存占用时机">
          <div>
            <el-radio-group v-model="formData.inventoryOccupancy" class="ml-4">
              <el-radio :label="1">订单提交占用库存</el-radio>
            </el-radio-group>
            <div class="form-tips">
              订单提交占用库存，库存不足则无法提交订单
            </div>
          </div>
        </el-form-item>

        <el-form-item label="取消订单退回库存">
          <div>
            <el-radio-group v-model="formData.returnInventory" class="ml-4">
              <el-radio :label="1">退回库存</el-radio>
              <el-radio :label="0">无需退回库存</el-radio>
            </el-radio-group>
            <div class="form-tips">
              订单提交占用库存，库存不足则无法提交订单
            </div>
          </div>
        </el-form-item>
      </el-card>
      <el-card shadow="never" class="!border-none mt-4">
        <div class="font-medium mb-7">其他设置</div>

        <el-form-item label="取消订单返回优惠券">
          <div>
            <el-radio-group class="ml-4" v-model="formData.returnCoupon">
              <el-radio :label="1">需要退还优惠券</el-radio>
              <el-radio :label="0">无需退还优惠券</el-radio>
            </el-radio-group>
            <div class="form-tips">
              待付款、待发货订单取消时，需要退还优惠券。注意优惠券受发放条件限制可能退还失败。
            </div>
          </div>
        </el-form-item>
      </el-card>
    </el-form>

    <footer-btns v-perms="['mall.transaction/edit']">
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </footer-btns>
  </div>
</template>

<script lang="ts" setup name="TradeSetup">
import type { TradeFormType } from "@/api/setting/trade";
import { getTradeConfig, setTradeConfig } from "@/api/setting/trade";
import type { FormInstance, FormRules } from "element-plus";
const formRef = ref<FormInstance>();

// 表单数据
const formData = reactive<TradeFormType>({
  isCancelUnpaidOrderTime: 1,
  cancelUnpaidOrderTime: -1,
  isCancelUnshippedOrderTime: 1,
  cancelUnshippedOrderTime: -1,
  isAutoConfirmReceiptDay: 1,
  autoConfirmReceiptDay: -1,
  isAfterSalesDay: 1,
  afterSalesDay: -1,
  inventoryOccupancy: 1,
  returnInventory: 1,
  returnCoupon: 1,
  beforeExpressRefund: 0
});

// 获取交易设置
const getData = async () => {
  try {
    const data = await getTradeConfig();
    for (const key in formData) {
      if (data[key] == undefined) continue;
      formData[key] = data[key];
    }
    if (data.cancelUnpaidOrderTime == -1) {
      formData.isCancelUnpaidOrderTime = 0;
    }
    if (data.cancelUnshippedOrderTime == -1) {
      formData.isCancelUnshippedOrderTime = 0;
    }
    if (data.autoConfirmReceiptDay == -1) {
      formData.isAutoConfirmReceiptDay = 0;
    }
    if (data.afterSalesDay == -1) {
      formData.isAfterSalesDay = 0;
    }
  } catch (error) {
    console.log("获取=>", error);
  }
};

// 保存交易设置数据
const handleSubmit = async () => {
  try {
    if (formData.isCancelUnpaidOrderTime == 0) {
      formData.cancelUnpaidOrderTime = -1;
    }
    if (formData.isCancelUnshippedOrderTime == 0) {
      formData.cancelUnshippedOrderTime = -1;
    }
    if (formData.isAutoConfirmReceiptDay == 0) {
      formData.autoConfirmReceiptDay = -1;
    }
    if (formData.isAfterSalesDay == 0) {
      formData.afterSalesDay = -1;
    }
    await setTradeConfig(formData);
    await getData();
  } catch (error) {
    console.log("保存=>", error);
  }
};

getData();
</script>
