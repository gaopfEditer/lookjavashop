<!-- 网站信息 -->
<template>
  <div class="website-information">
    <el-form ref="formRef" :rules="rules" :model="formData" label-width="120px">
      <el-card shadow="never" class="!border-none">
        <div class="text-xl font-medium mb-[20px]">后台设置</div>
        <el-form-item label="网站名称" prop="name">
          <div class="w-80">
            <el-input
              v-model="formData.name"
              placeholder="请输入网站名称"
              maxlength="30"
              show-word-limit
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item label="网站图标" prop="favicon">
          <div>
            <material-picker v-model="formData.favicon" :limit="1" />
            <div class="form-tips">
              建议尺寸：100*100像素，支持jpg，jpeg，png格式
            </div>
          </div>
        </el-form-item>
        <el-form-item label="网站logo" prop="logo">
          <div>
            <material-picker v-model="formData.logo" :limit="1" />
            <div class="form-tips">
              建议尺寸：200*200像素，支持jpg，jpeg，png格式
            </div>
          </div>
        </el-form-item>
        <el-form-item label="登录页广告图" prop="backdrop">
          <div>
            <material-picker v-model="formData.backdrop" :limit="1" />
            <div class="form-tips">
              建议尺寸：400*400像素，支持jpg，jpeg，png格式
            </div>
          </div>
        </el-form-item>
        <el-form-item label="文档信息">
          <div>
            <el-radio-group v-model="formData.isShowDoc">
              <el-radio :label="1">显示</el-radio>
              <el-radio :label="0">隐藏</el-radio>
            </el-radio-group>
            <div class="form-tips">
              默认开启，控制工作台商城信息的按钮是否显示
            </div>
          </div>
        </el-form-item>
      </el-card>
      <el-card shadow="never" class="!border-none mt-4">
        <div class="text-xl font-medium mb-[20px]">前台设置</div>
        <el-form-item label="商城名称" prop="shopName">
          <div class="w-80">
            <el-input
              v-model="formData.shopName"
              placeholder="请输入店铺/商城名称"
              maxlength="30"
              show-word-limit
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item label="商城LOGO" prop="shopLogo">
          <div>
            <material-picker v-model="formData.shopLogo" :limit="1" />
            <div class="form-tips">
              建议尺寸：100*100px，支持jpg，jpeg，png格式
            </div>
          </div>
        </el-form-item>
        <el-form-item label="经营状态" prop="shopStatus">
          <div>
            <el-switch
              v-model="formData.shopStatus"
              :active-value="1"
              :inactive-value="0"
            ></el-switch>
            <div class="form-tips">
              默认开启，商城状态为关闭时，将不对外提供服务，请谨慎操作
            </div>
          </div>
        </el-form-item>
      </el-card>
      <el-card shadow="never" class="!border-none mt-4">
        <div class="text-xl font-medium mb-[20px]">联系方式</div>
        <el-form-item label="联系人名称" prop="contactNickname">
          <div class="w-80">
            <el-input
              v-model="formData.contactNickname"
              placeholder="请输入联系人名称"
              maxlength="10"
              show-word-limit
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item label="联系人电话" prop="contactMobile">
          <div class="w-81">
            <el-input
              v-model="formData.contactMobile"
              placeholder="请输入联系人电话"
              maxlength="11"
              show-word-limit
            ></el-input>
            <div class="form-tips">
              商城联系人手机号：有订单产生时，可发送短信至联系人手机
            </div>
          </div>
        </el-form-item>
      </el-card>

      <el-card shadow="never" class="!border-none mt-4">
        <div class="text-xl font-medium mb-[20px]">退货地址</div>
        <el-form-item label="收货人名称" prop="retreatConsignee">
          <div class="w-80">
            <el-input
              v-model="formData.retreatConsignee"
              placeholder="请输入收货人名称"
              maxlength="10"
              show-word-limit
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item label="手机号码" prop="retreatMobile">
          <div class="w-80">
            <el-input
              v-model="formData.retreatMobile"
              placeholder="请输入手机号码"
              maxlength="11"
              show-word-limit
            ></el-input>
          </div>
        </el-form-item>
        <el-form-item label="地区" prop="retreatProvinceId">
          <div class="w-80">
            <area-select
              v-model:province="formData.retreatProvinceId"
              v-model:city="formData.retreatCityId"
              v-model:district="formData.retreatDistrictId"
            />
          </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="retreatAddress">
          <div class="w-80">
            <el-input
              v-model="formData.retreatAddress"
              placeholder="请输入"
              type="textarea"
              :autosize="{ minRows: 4, maxRows: 4 }"
              maxlength="64"
              :show-word-limit="true"
              :clearable="true"
            />
          </div>
        </el-form-item>
      </el-card>
      <!--            <el-card shadow="never" class="!border-none mt-4">-->
      <!--                <div class="text-xl font-medium mb-[20px]">PC端设置</div>-->
      <!--                <el-form-item label="PC端LOGO" prop="pcLogo">-->
      <!--                    <div>-->
      <!--                        <material-picker v-model="formData.pcLogo" :limit="1" />-->
      <!--                        <div class="form-tips">建议尺寸：120*28px，支持jpg，jpeg，png格式</div>-->
      <!--                    </div>-->
      <!--                </el-form-item>-->
      <!--                <el-form-item label="网站标题" prop="pcTitle">-->
      <!--                    <div class="w-80">-->
      <!--                        <el-input-->
      <!--                            v-model.trim="formData.pcTitle"-->
      <!--                            placeholder="请输入PC端网站标题"-->
      <!--                            maxlength="30"-->
      <!--                            show-word-limit-->
      <!--                        />-->
      <!--                    </div>-->
      <!--                </el-form-item>-->
      <!--                <el-form-item label="网站图标" prop="pcIco">-->
      <!--                    <div>-->
      <!--                        <material-picker v-model="formData.pcIco" :limit="1" />-->
      <!--                        <div class="form-tips">建议尺寸：100*100像素，支持jpg，jpeg，png格式</div>-->
      <!--                    </div>-->
      <!--                </el-form-item>-->
      <!--                <el-form-item label="网站描述" prop="pcDesc">-->
      <!--                    <div class="w-80">-->
      <!--                        <el-input v-model.trim="formData.pcDesc" placeholder="请输入PC端网站描述" />-->
      <!--                    </div>-->
      <!--                </el-form-item>-->
      <!--                <el-form-item label="网站关键词" prop="pcKeywords">-->
      <!--                    <div class="w-80">-->
      <!--                        <el-input-->
      <!--                            v-model.trim="formData.pcKeywords"-->
      <!--                            placeholder="请输入PC端网站关键词"-->
      <!--                        />-->
      <!--                    </div>-->
      <!--                </el-form-item>-->
      <!--            </el-card>-->
    </el-form>
    <footer-btns v-perms="['setting:website:save']">
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </footer-btns>
  </div>
</template>

<script lang="ts" setup name="webInformation">
import { getWebsite, setWebsite } from "@/api/setting/website";
import useAppStore from "@/stores/modules/app";
import feedback from "@/utils/feedback";
import type { FormInstance } from "element-plus";
const formRef = ref<FormInstance>();
const { getConfig } = useAppStore();
// 表单数据
const formData = reactive({
  name: "", // 网站名称
  favicon: "", // 网站图标
  logo: "", // 网站logo
  backdrop: "", // 登录页广告图
  shopName: "",
  shopLogo: "",
  contactNickname: "",
  contactMobile: "",
  retreatConsignee: "",
  retreatMobile: "",
  retreatRegion: "",
  retreatProvinceId: "",
  retreatCityId: "",
  retreatDistrictId: "",
  retreatAddress: "",
  pcDesc: "",
  pcIco: "",
  pcKeywords: "",
  pcLogo: "",
  pcTitle: "",
  isShowDoc: "",
  shopStatus: "",
});

// 表单验证
const rules = {
  name: [
    {
      required: true,
      message: "请输入网站名称",
      trigger: ["blur"],
    },
  ],
  favicon: [
    {
      required: true,
      message: "请选择网站图标",
      trigger: ["change"],
    },
  ],
  logo: [
    {
      required: true,
      message: "请选择网站logo",
      trigger: ["change"],
    },
  ],
  backdrop: [
    {
      required: true,
      message: "请选择登录页广告图",
      trigger: ["change"],
    },
  ],
  shopName: [
    {
      required: true,
      message: "请输入店铺/商城名称",
      trigger: ["blur"],
    },
  ],
  shopLogo: [
    {
      required: true,
      message: "请选择商城LOGO",
      trigger: ["change"],
    },
  ],
  contactNickname: [
    {
      required: true,
      message: "请输入联系人名称",
      trigger: ["change"],
    },
  ],
  contactMobile: [
    {
      required: true,
      message: "请输入联系人电话",
      trigger: ["change"],
    },
  ],
  retreatConsignee: [
    {
      required: true,
      message: "请输入收货人名称",
      trigger: ["change"],
    },
  ],
  retreatMobile: [
    {
      required: true,
      message: "请输入收货人电话",
      trigger: ["change"],
    },
  ],
  retreatProvinceId: [
    {
      required: true,
      message: "请选择收货人地区",
      trigger: ["change"],
    },
  ],
  retreatRegion: [
    {
      required: true,
      message: "请选择收货人地区",
      trigger: ["change"],
    },
  ],
  retreatAddress: [
    {
      required: true,
      message: "请选择收货人地址",
      trigger: ["change"],
    },
  ],
  pcLogo: [
    {
      required: true,
      message: "请选择PC端LOGO",
      trigger: ["change"],
    },
  ],
  pcTitle: [
    {
      required: true,
      message: "请输入PC端网站标题",
      trigger: ["blur"],
    },
  ],
  pcIco: [
    {
      required: true,
      message: "请选择PC端网站图标",
      trigger: ["change"],
    },
  ],
  shopStatus: [
    {
      required: true,
      message: "请选择经营状态",
      trigger: ["change"],
    },
  ],
};

// 获取备案信息
const getData = async () => {
  const data = await getWebsite();
  for (const key in formData) {
    //@ts-ignore
    formData[key] = data[key];
  }
};

// 设置备案信息
const handleSubmit = async () => {
  await formRef.value?.validate();
  await setWebsite(formData);
  feedback.msgSuccess("操作成功");
  getConfig();
  getData();
};

getData();
</script>

<style lang="scss" scoped></style>
