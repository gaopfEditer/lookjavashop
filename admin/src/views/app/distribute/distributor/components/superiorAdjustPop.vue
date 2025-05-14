<template>
  <Popup
    ref="popRef"
    title="上级分销商调整"
    width="700px"
    @confirm="confirm"
    async
    @close="emit('close')"
  >
    <div>
      <el-form label-width="120px">
        <el-form-item label="用户信息">
          <div>{{ data?.nickname }}</div>
        </el-form-item>
        <el-form-item label="当前分销商">
          <div>{{ data?.firstLeaderName || "系统" }}</div>
        </el-form-item>
        <el-form-item label="调整方式" class="is-required">
          <div>
            <el-radio-group v-model="adjustType">
              <el-radio label="assign">指定分销商</el-radio>
              <el-radio label="system">设置分销商为系统</el-radio>
            </el-radio-group>
            <div class="form-tips">
              设置“设置分销商为系统”时，用户的上级分销商会默认为系统
            </div>
          </div>
        </el-form-item>
        <el-form-item
          label="选择分销商"
          class="is-required"
          v-if="adjustType == 'assign'"
        >
          <div class="w-full flex-none flex flex-col">
            <div>
              <el-button @click="selectUser" type="primary" link
                >选择用户</el-button
              >
              <div class="form-tips">选择新的分销商</div>
            </div>
            <div class="w-full">
              <el-table :data="isPickerUser">
                <el-table-column label="ID" prop="id"></el-table-column>
                <el-table-column label="用户昵称" min-width="180">
                  <template #default="{ row }">
                    <div class="flex items-center">
                      <el-image
                        class="w-[48px] h-[48px] flex-none"
                        :src="row.avatar"
                      ></el-image>
                      <div class="ml-2">{{ row?.nickname }}</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="上级推荐人" min-width="120">
                  <template #default="{ row }">
                    {{ row.firstLeaderName || "系统" }}
                  </template>
                </el-table-column>
                <el-table-column label="分销状态">
                  <template #default="{ row }">
                    <div>
                      {{ row.isFreeze == 0 ? "正常" : "冻结" }}
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
    <DistributorPicker
      ref="UserPickerRef"
      v-model="isPickerUser"
    ></DistributorPicker>
  </Popup>
</template>

<script setup lang="ts">
import { adjustLeader } from "@/api/distribution/distributor";

const emit = defineEmits(["close"]);

//用户数据
const id = ref();
const data: any = ref({});
//选中用户
const isPickerUser: any = ref([]);
//调整方式
const adjustType: any = ref("assign");
//弹框ref
const popRef = shallowRef();
//用户选择器ref
const UserPickerRef = shallowRef();

//选择分销商
const selectUser = () => {
  console.log(UserPickerRef.value);

  UserPickerRef.value.open();
};

//确定
const confirm = async () => {
  if (adjustType.value == "system") {
    await adjustLeader({
      type: adjustType.value,
      userId: id.value,
      firstId: 0,
    });
  } else {
    await adjustLeader({
      type: adjustType.value,
      userId: id.value,
      firstId: isPickerUser.value[0].id,
    });
  }
  popRef.value.close();
};

//打开弹框
const open = async (option: any) => {
  await nextTick();
  popRef.value.open();
  data.value = option;
  id.value = option.id;
};

defineExpose({ open });
</script>
<style scoped lang="scss"></style>
