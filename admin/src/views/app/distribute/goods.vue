<template>
  <el-card class="!border-none" shadow="never">
    <el-alert
      type="warning"
      title="设置商品的分销佣金比例"
      :closable="false"
      show-icon
    />
    <el-form
      ref="formRef"
      class="mb-[-16px] mt-4"
      :model="queryParams"
      :inline="true"
    >
      <el-form-item label="商品信息">
        <el-input
          class="w-[280px]"
          v-model="queryParams.keyword"
          placeholder="请输入商品名称/编号"
          clearable
        />
      </el-form-item>
      <el-form-item label="商品分类">
        <el-cascader
          class="w-[280px]"
          v-model="queryParams.categoryId"
          :options="goodsCategoryList"
          :props="props"
          :clearable="true"
          :filterable="true"
        />
      </el-form-item>
      <el-form-item label="分销状态">
        <el-select class="w-[280px]" v-model="queryParams.isDistribution">
          <el-option label="全部" value />
          <el-option label="参与" value="1" />
          <el-option label="未参与" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="resetPage">查询</el-button>
        <el-button @click="resetParams">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
  <el-card class="!border-none mt-4" shadow="never">
    <el-button type="primary" @click="batchChangeStauts(1)">参与分销</el-button>
    <el-button @click="batchChangeStauts(0)">取消分销</el-button>
    <div>
      <el-table
        ref="tableRef"
        size="large"
        v-loading="pager.loading"
        :data="pager.lists"
        class="mt-4"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="商品名称" min-width="180">
          <template #default="{ row }">
            <div class="flex items-center">
              <el-image
                :src="row.image"
                class="w-[64px] ht-[64px] rounded-lg flex-none"
              ></el-image>
              <div class="ml-2">
                <div>
                  {{ row.name }}
                </div>
                <el-tag checked v-if="row.specType == 2">多规格</el-tag>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格" min-width="140">
          <template #default="{ row }">
            <div v-if="row.minPrice == row.maxPrice">
              {{ row.minPrice }}
            </div>
            <div v-else>{{ row.minPrice }}~{{ row.maxPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column label="商品状态" min-width="140" prop="goodsStatus">
        </el-table-column>
        <el-table-column label="分销状态" min-width="140">
          <template #default="{ row }">
            <el-tag v-if="row.distributionStatus == '参与'" type="success"
              >参与</el-tag
            >
            <el-tag v-else type="info">未参与</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="180">
          <template #default="{ row }">
            <div>
              <RouterLink :to="`/app/distribution/goodsDetail?id=${row.id}`">
                <el-button type="primary" link>设置佣金</el-button>
              </RouterLink>
              <el-button
                type="primary"
                v-if="row.distributionStatus == '参与'"
                link
                @click="changStatus(0, row.name, row.id)"
                >取消分销</el-button
              >
              <el-button
                type="primary"
                v-else
                link
                @click="changStatus(1, row.name, row.id)"
                >参与分销</el-button
              >
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="flex justify-end mt-4">
        <pagination v-model="pager" @change="getLists" />
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { usePaging } from "@/hooks/usePaging";
import {
  getDistributionGoods,
  postChangStatus,
} from "@/api/distribution/distribution";
import { goodsCategoryLists } from "@/api/goods/category";
import feedback from "@/utils/feedback";

//搜索参数
const queryParams: any = ref({
  categoryId: "", //分类id
  keyword: "", //商品名称/id/编号
  status: "", //商品状态：0-仓库中 1-销售中 2-售罄
  isDistribution: "", //分销状态 0-未参与 1-参与
});

// 分类组件配置数据
const props = reactive({
  multiple: false,
  checkStrictly: true,
  label: "name",
  value: "id",
  children: "children",
  emitPath: false,
});
//分类下拉列表
const goodsCategoryList = ref([]);

//表格ref
const tableRef = shallowRef();

//分页组件
const { pager, getLists, resetPage, resetParams } = usePaging({
  fetchFun: getDistributionGoods,
  params: queryParams.value,
});

//获取分类下拉列表
const getGoodsCategoryList = async () => {
  try {
    const data = await goodsCategoryLists();
    goodsCategoryList.value = data;
  } catch (error) {
    console.log(error);
  }
};

//修改分销状态
const changStatus = async (stauts: any, name: string, id: number) => {
  //status 0-取消分销 1-参与分销
  if (stauts == 0) {
    await feedback.customConfirm(
      "确认该商品：\t",
      "\t取消分销吗？请谨慎操作",
      name,
      "color:red",
      "取消分销"
    );
  }
  if (stauts == 1) {
    await feedback.customConfirm(
      "确认该商品：\t",
      "\t参与分销吗？请谨慎操作",
      name,
      "color:red",
      "参与分销"
    );
  }
  await postChangStatus({ ids: [id], isDistribution: stauts });
  getLists();
};

//批量修改分销状态
const batchChangeStauts = async (status: any) => {
  const ids = tableRef.value.getSelectionRows().map((item: any) => {
    return item.id;
  });
  if (ids.length == 0) {
    feedback.msgError("没有选择商品！");
    return;
  }
  switch (status) {
    case 0:
      await feedback.confirm("确定将已选商品取消分销？");
      break;

    case 1:
      await feedback.confirm("确定将已选商品参与分销？");
      break;
    default:
      break;
  }
  await postChangStatus({ ids: ids, isDistribution: status });
  getLists();
};

onMounted(() => {
  getLists();
  getGoodsCategoryList();
});
</script>

<style scoped lang="scss"></style>
