<template>
  <div>
    <el-card class="!border-none" shadow="never">
      <div class="font-medium">佣金概览</div>
      <div class="mt-[30px] grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="flex flex-col justify-center items-center">
          <div class="text-sm">累计已入账佣金</div>
          <div class="text-6xl">{{ data.totalEarnings }}</div>
        </div>
        <div class="flex flex-col justify-center items-center">
          <div class="text-sm">今日入账佣金</div>
          <div class="text-6xl">{{ data.todayEarnings }}</div>
        </div>
        <div class="flex flex-col justify-center items-center">
          <div class="text-sm">待结算佣金</div>
          <div class="text-6xl">{{ data.totalWaitEarnings }}</div>
        </div>
        <div class="flex flex-col justify-center items-center">
          <div class="text-sm">今日新增待结算佣金</div>
          <div class="text-6xl">{{ data.todayWaitEarnings }}</div>
        </div>
      </div>
    </el-card>
    <el-card class="!border-none mt-4" shadow="never">
      <div class="font-medium">分销概览</div>
      <div class="mt-[30px] grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div class="flex flex-col justify-center items-center">
          <div class="text-sm">分销商</div>
          <div class="text-6xl">{{ data.distributionStore }}</div>
        </div>
        <div class="flex flex-col justify-center items-center">
          <div class="text-sm">分销商占比</div>
          <div class="text-6xl">{{ data.distributionRatio }}%</div>
        </div>
      </div>
    </el-card>
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <Earn></Earn>
      <Fans></Fans>
      <!-- <el-card class="!border-none mt-4" shadow="never"> -->
      <!-- <div class="font-medium">分销商收入排行榜</div>
        <div class="mt-4">
          <el-table :data="earnData.lists">
            <el-table-column label="排行">
              <template #default="{ $index }">
                <div>{{ $index + 1 }}</div>
              </template>
            </el-table-column>
            <el-table-column label="用户信息" prop="nickname">
              <template #default="{ row }">
                <div class="flex items-center">
                  <el-image
                    class="w-[45px] h-[45px]"
                    :src="row.avatar"
                  ></el-image>
                  <div class="ml-2">{{ row.nickname }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              label="已入账佣金"
              prop="totalEarnings"
            ></el-table-column>
          </el-table>
        </div>
      </el-card> -->
      <!-- <el-card class="!border-none mt-4" shadow="never">
        <div class="font-medium">分销商上下级人数排行</div>
        <div class="mt-4">
          <el-table :data="fansData.lists">
            <el-table-column label="排行">
              <template #default="{ $index }">
                <div>{{ $index + 1 }}</div>
              </template>
            </el-table-column>
            <el-table-column label="用户信息" prop="nickname">
              <template #default="{ row }">
                <div class="flex items-center">
                  <el-image
                    class="w-[45px] h-[45px]"
                    :src="row.avatar"
                  ></el-image>
                  <div class="ml-2">{{ row.nickname }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              label="下级人数"
              prop="totalFans"
            ></el-table-column>
          </el-table>
        </div>
      </el-card> -->
    </div>
  </div>
</template>
<script setup lang="ts">
import Earn from "./component/earn.vue";
import Fans from "./component/fans.vue";
import {
  getsurveydata,
  surveytopEarnings,
  surveytopFans,
} from "@/api/distribution/distribution";
const data: any = ref({});
const earnData: any = ref({});
const fansData: any = ref({});
const getdata = async () => {
  data.value = await getsurveydata();
  //   earnData.value = await surveytopEarnings();
  //   fansData.value = await surveytopFans();
};

getdata();
</script>

<style scoped lang="scss"></style>
