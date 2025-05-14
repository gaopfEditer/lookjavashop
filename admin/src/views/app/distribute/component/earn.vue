<template>
  <el-card class="!border-none mt-4" shadow="never">
    <div class="font-medium">分销商收入排行榜</div>
    <div class="mt-4">
      <el-table :data="pager.lists">
        <el-table-column label="排行">
          <template #default="{ $index }">
            <div>{{ $index + 1 }}</div>
          </template>
        </el-table-column>
        <el-table-column label="用户信息" prop="nickname">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-image class="w-[45px] h-[45px]" :src="row.avatar"></el-image>
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
    <div class="flex justify-end mt-4">
      <pagination
        v-model="pager"
        @change="getLists"
        layout="total, prev, pager, next "
      />
    </div>
  </el-card>
</template>
<script setup lang="ts">
import { usePaging } from "@/hooks/usePaging";
import { surveytopEarnings } from "@/api/distribution/distribution";
const queryParams: any = ref({});
//分页组件
const { pager, getLists, resetPage, resetParams } = usePaging({
  fetchFun: surveytopEarnings,
  params: queryParams.value,
  page: 1,
  size: 5,
});
getLists();
</script>
