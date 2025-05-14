<template>
  <div>
    <el-card class="!border-none" shadow="never">
      <el-page-header :content="$route.meta.title" @back="$router.back()" />
    </el-card>
    <el-form label-width="120px">
      <el-card class="!border-none mt-4" shadow="never">
        <div class="font-medium text-xl">商品信息</div>
        <div class="mt-4">
          <el-form-item label="商品编号">
            <div>{{ detailData.code }}</div>
          </el-form-item>
          <el-form-item label="商品图片">
            <el-image :src="detailData.image" class="w-[58px] h-[58px]">
            </el-image>
          </el-form-item>
          <el-form-item label="商品名称">
            <div>{{ detailData.name }}</div>
          </el-form-item>
        </div>
      </el-card>
      <el-card class="!border-none mt-4" shadow="never">
        <div>
          <el-form-item label="分销状态">
            <div>
              <el-radio-group v-model="detailData.isDistribution">
                <el-radio :label="1">参与分销</el-radio>
                <el-radio :label="0">不参与分销</el-radio>
              </el-radio-group>
              <div class="form-tips">
                是否参与分销，选择不参与分销将不产生分销佣金
              </div>
            </div>
          </el-form-item>
          <el-form-item label="分销状态">
            <div class="flex-none">
              <el-radio-group
                v-model="detailData.rule"
                @change="distriTypeChange"
              >
                <el-radio :label="1">默认分销等级佣金规则</el-radio>
                <el-radio :label="2">单独设置佣金比例</el-radio>
                <!-- <el-radio :label="2">单独设置佣金</el-radio> -->
              </el-radio-group>
              <div class="mt-2 flex">
                <popover-input @confirm="setSelf" :disabled="!isSelectData">
                  <el-button :disabled="!isSelectData">设置自购佣金</el-button>
                </popover-input>
                <popover-input @confirm="setFirst" :disabled="!isSelectData">
                  <el-button class="ml-2" :disabled="!isSelectData"
                    >设置一级佣金</el-button
                  >
                </popover-input>
                <popover-input @confirm="setSecond" :disabled="!isSelectData">
                  <el-button class="ml-2" :disabled="!isSelectData"
                    >设置二级佣金</el-button
                  >
                </popover-input>
              </div>
              <el-table
                :data="formatTableData"
                ref="tableRef"
                class="mt-2"
                @selection-change="tableSelect"
              >
                <el-table-column type="selection" width="55" />
                <el-table-column label="分销等级" prop="name"></el-table-column>
                <el-table-column label="商品规格">
                  <template #default="{ row }">
                    <!-- <div>{{ row.sku_value_arr.join(',') || '默认' }}</div> -->
                    <div>{{ row.specValueStr }}</div>
                  </template>
                </el-table-column>
                <el-table-column
                  label="价格"
                  prop="sellPrice"
                ></el-table-column>
                <el-table-column :label="`自购佣金比例(%)`" min-width="180">
                  <template #default="{ row }">
                    <div>
                      <div class="flex items-center">
                        <el-input
                          class="w-[80px]"
                          type="number"
                          v-model="row.selfRatio"
                          :disabled="detailData.rule == 1"
                        ></el-input>
                        <div class="ml-2">
                          =
                          {{
                            ((row.sellPrice * row.selfRatio) / 100).toFixed(2)
                          }}元
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column :label="`一级佣金比例(%)`" min-width="180">
                  <template #default="{ row }">
                    <div>
                      <div class="flex items-center">
                        <el-input
                          class="w-[80px]"
                          type="number"
                          v-model="row.firstRatio"
                          :disabled="detailData.rule == 1"
                        ></el-input>
                        <div class="ml-2">
                          =
                          {{
                            ((row.sellPrice * row.firstRatio) / 100).toFixed(2)
                          }}元
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column :label="`二级佣金比例(%)`" min-width="180">
                  <template #default="{ row }">
                    <div class="flex items-center">
                      <div class="flex items-center">
                        <el-input
                          class="w-[80px]"
                          type="number"
                          v-model="row.secondRatio"
                          :disabled="detailData.rule == 1"
                        ></el-input>
                        <div class="ml-2">
                          =
                          {{
                            ((row.sellPrice * row.secondRatio) / 100).toFixed(
                              2
                            )
                          }}元
                        </div>
                      </div>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-form-item>
        </div>
      </el-card>
    </el-form>
    <footer-btns>
      <el-button type="primary" @click="submit">保存</el-button>
    </footer-btns>
  </div>
</template>
<script setup lang="ts">
import {
  getGoodsDetail,
  distributionSave,
} from "@/api/distribution/distribution";
import router from "@/router";
import feedback from "@/utils/feedback";

interface IDetailData {
  code: string;
  name: string;
  id: number;
  image: string;
  isDistribution: number;
  ratioData: any[];

  rule: number; //佣金规则 1-按分销等级比例分佣 2-单独设置分佣比例
}

const route = useRoute();
const id = route.query.id;
const formatTableData: any = ref([]);

//表格ref
const tableRef = shallowRef();
//是否选中数据
const isSelectData = ref();
//分销详情数据
const detailData = ref<IDetailData>({
  code: "",
  name: "",
  id: 0,
  image: "",
  isDistribution: 1,
  ratioData: [],
  rule: 1,
});
const newDistributionLevel: any = ref([]);

//获取详情数据
const getDetailData = async () => {
  detailData.value = await getGoodsDetail({ id });
};

//格式化表格数据
const formatTable = () => {
  formatTableData.value = [];
  detailData.value.ratioData.forEach((item: any, index) => {
    item.skuItems.forEach((item1: any, index1: any) => {
      //默认分销等级
      if (detailData.value.rule == 1) {
        const res: any = {};
        Object.keys(item1).map((key) => {
          res[key] = item1[key];
        });
        formatTableData.value.push({ ...res, name: item.name });
      } else {
        formatTableData.value.push({ ...item1, name: item.name });
      }
    });
  });
};
//变选项变化
const tableSelect = async (selectData: any) => {
  await nextTick();
  isSelectData.value = selectData.length;
};

//批量设置佣金
const setSelf = (setValue: any) => {
  tableRef.value.getSelectionRows().map((item: any) => {
    item.selfRatio = setValue;
  });
};
const setFirst = (setValue: any) => {
  tableRef.value.getSelectionRows().map((item: any) => {
    item.firstRatio = setValue;
  });
};
const setSecond = (setValue: any) => {
  tableRef.value.getSelectionRows().map((item: any) => {
    item.secondRatio = setValue;
  });
};

//佣金规则切换
const distriTypeChange = (value: any) => {
  nextTick(() => {
    formatTable();
  });
};

//保存
const submit = async () => {
  // console.log(formatTableData.value)

  // await feedback.msgSuccess('保存成功！')
  // router.back()
  // newDistributionLevel.value = detailData.value.distribution_level
  // newDistributionLevel.value.forEach((item: any, index: any) => {
  //     //清空
  //     item.distribution_list = []
  //     formatTableData.value.map((item1: any) => {
  //         if (item1.distribution_level_id == item.id) {
  //             item.distribution_list.push(item1)
  //         }
  //     })
  // })
  // await distributionSave({
  //     id: detailData.value.id,
  //     distribution_type: detailData.value.distribution_type,
  //     distribution_level: newDistributionLevel.value,
  //     is_distribution: detailData.value.is_distribution
  // })
  // await getDetailData()

  formatTableData.value.forEach((item: any) => {
    const itemObj = {
      levelId: item.levelId,
      skuId: item.id,
      selfRatio: item.selfRatio,
      firstRatio: item.firstRatio,
      secondRatio: item.secondRatio,
    };
    newDistributionLevel.value.push(itemObj);
  });
  await distributionSave({
    goodsId: detailData.value.id,
    isDistribution: detailData.value.isDistribution,
    rule: detailData.value.rule,
    ratioData: newDistributionLevel.value,
  });
  feedback.msgSuccess("保存成功！");
  router.back();
};

onMounted(async () => {
  await getDetailData();
  await formatTable();
});
</script>

<style scoped lang="scss">
:deep(.el-form-item__content) {
  display: block;
}
</style>
