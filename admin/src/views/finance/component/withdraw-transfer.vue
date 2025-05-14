<template>
  <div class="edit-popup">
    <popup
      ref="popupRef"
      title="转账"
      :async="true"
      width="550px"
      @confirm="handleSubmit"
      @close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        label-width="110px"
        :rules="formRules"
      >
        <el-form-item label="转账状态" prop="status">
          <div>
            <el-radio-group v-model="formData.status">
              <el-radio :label="1" size="large">通过</el-radio>
              <el-radio :label="0" size="large">不通过</el-radio>
            </el-radio-group>

            <div class="form-tips">转账失败后，提现金额会全部退回商家账户</div>
          </div>
        </el-form-item>

        <el-form-item label="转账凭证">
          <material-picker v-model="formData.transferVoucher" :limit="1" />
        </el-form-item>

        <el-form-item label="转账说明" prop="content">
          <el-input
            v-model="formData.transferRemark"
            placeholder="请输入转账说明"
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 10 }"
            show-word-limit
            clearable
          />
        </el-form-item>
      </el-form>
    </popup>
  </div>
</template>
<script lang="ts" setup>
// import type { WithDrawTransferType } from "@/api/finance";
import { WithDrawTransfer } from "@/api/finance";
import { usePaging } from "@/hooks/usePaging";
import Popup from "@/components/popup/index.vue";
import type { FormInstance, FormRules } from "element-plus";
import feedback from "@/utils/feedback";

const emit = defineEmits(["success", "close"]);
const formRef = shallowRef<FormInstance>();
const popupRef = shallowRef<InstanceType<typeof Popup>>();

const formData = reactive<any>({
  id: "",
  auditRemark: "",
  status: 1,
  transferVoucher: "",
  transferRemark: "",
});

const formRules = reactive<FormRules>({
  status: [{ required: true, message: "请选择转账状态", trigger: ["blur"] }],
});

const handleSubmit = async () => {
  await formRef.value?.validate();
  await WithDrawTransfer(formData);
  popupRef.value?.close();
  emit("success");
};

const open = async (id: number) => {
  formData.id = id;
  popupRef.value?.open();
};

const handleClose = () => {
  emit("close");
};

defineExpose({ open });
</script>
