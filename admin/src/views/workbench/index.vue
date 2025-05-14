<template>
  <div class="workbench">
    <div class="md:flex">
      <el-card class="!border-none mb-4 md:mr-4 w-[400px]" shadow="never">
        <template #header>
          <span class="card-title">版本信息</span>
        </template>
        <div>
          <div class="flex leading-9">
            <div class="w-20 flex-none">网站名称</div>
            <span> {{ workbenchData.version.name }}</span>
          </div>
          <div class="flex leading-9">
            <div class="w-20 flex-none">当前版本</div>
            <span> {{ workbenchData.version.version }}</span>
          </div>
          <div class="flex leading-9" v-if="ShowDoc">
            <!-- <div class="w-20 felx-none">获取渠道</div> -->
            <div class="flex items-center">
              <div>
                <is-copyright />
              </div>
              <a
                :href="workbenchData.version.channel.website"
                target="_blank"
                class="ml-3"
              >
                <el-button type="success" size="small">官网</el-button>
              </a>
              <a
                class="ml-3"
                :href="workbenchData.version.channel.document"
                target="_blank"
              >
                <el-button type="danger" size="small">开发文档</el-button>
              </a>
            </div>
          </div>
        </div>
      </el-card>
      <el-card class="!border-none mb-4 flex-1" shadow="never">
        <template #header>
          <div>
            <span class="card-title">今日数据</span>
            <span class="text-tx-secondary text-xs ml-4">
              更新时间：{{ workbenchData.today.time }}
            </span>
          </div>
        </template>

        <div class="flex flex-wrap">
          <div class="w-1/2 md:w-1/4">
            <div class="leading-10">销售额(元)</div>
            <div class="text-6xl">{{ workbenchData.today.todaySales }}</div>
          </div>
          <div class="w-1/2 md:w-1/4">
            <div class="leading-10">订单量(笔)</div>
            <div class="text-6xl">{{ workbenchData.today.todayOrder }}</div>
          </div>
          <div class="w-1/2 md:w-1/4">
            <div class="leading-10">新增用户</div>
            <div class="text-6xl">{{ workbenchData.today.todayUsers }}</div>
          </div>
          <div class="w-1/2 md:w-1/4">
            <div class="leading-10">访问量(人)</div>
            <div class="text-6xl">{{ workbenchData.today.todayVisits }}</div>
          </div>
        </div>
      </el-card>
    </div>
    <div class="function lg:flex">
      <el-card class="!border-none flex-1 mb-4 md:mr-4" shadow="never">
        <template #header>
          <div>
            <span class="card-title">待办事项</span>
          </div>
        </template>

        <div class="flex flex-1">
          <div class="w-1/4 text-center">
            <div class="text-4xl">
              {{ workbenchData.stay.stayDeliver }}
            </div>
            <div class="leading-10">待发货</div>
          </div>
          <div class="w-1/4 text-center">
            <div class="text-4xl">
              {{ workbenchData.stay.stayRefund }}
            </div>
            <div class="leading-10">待退款</div>
          </div>
          <div class="w-1/4 text-center">
            <div class="text-4xl">
              {{ workbenchData.stay.stayReply }}
            </div>
            <div class="leading-10">待回复</div>
          </div>
          <div class="w-1/4 text-center">
            <div class="text-4xl">
              {{ workbenchData.stay.warningStock }}
            </div>
            <div class="leading-10">库存预警</div>
          </div>
        </div>
      </el-card>
      <el-card class="flex-1 !border-none mb-4" shadow="never">
        <template #header>
          <span>常用功能</span>
        </template>
        <div class="flex flex-wrap">
          <div
            v-for="item in workbenchData.menu"
            class="w-1/5 flex flex-col items-center"
            :key="item"
          >
            <router-link :to="item.url" class="mb-3 flex flex-col items-center">
              <img width="40" height="40" :src="item.image" />
              <div class="mt-2">{{ item.name }}</div>
            </router-link>
          </div>
        </div>
      </el-card>
    </div>
    <div class="md:flex">
      <el-card class="flex-1 !border-none md:mr-4 mb-4" shadow="never">
        <template #header>
          <span>销售额趋势图</span>
        </template>
        <div>
          <v-charts
            style="height: 350px"
            :option="workbenchData.salesOption"
            :autoresize="true"
          />
        </div>
      </el-card>
      <el-card class="flex-1 !border-none mb-4" shadow="never">
        <template #header>
          <span>访问量趋势图</span>
        </template>
        <div>
          <v-charts
            style="height: 350px"
            :option="workbenchData.visitorOption"
            :autoresize="true"
          />
        </div>
      </el-card>
    </div>

    <layout-footer />
  </div>
</template>

<script lang="ts" setup name="workbench">
import { getWorkbench } from "@/api/app";
import vCharts from "vue-echarts";
import menu_goods from "./image/menu_goods.png";
import menu_order from "./image/menu_order.png";
import menu_decoration from "./image/menu_decoration.png";
import menu_user from "./image/menu_user.png";
import menu_setting from "./image/menu_setting.png";
import LayoutFooter from "@/layout/components/footer.vue";
import { getWebsite } from "@/api/setting/website";

// 表单数据
const workbenchData: any = reactive({
  version: {
    version: "", // 版本号
    website: "", // 官网
    based: "",
    channel: {
      gitee: "",
      website: "",
    },
  },
  // 待办事项
  stay: {
    stayDeliver: 0,
    stayRefund: 0,
    stayReply: 0,
    warningStock: 0,
  },
  today: {}, // 今日数据
  menu: [
    {
      name: "发布商品",
      image: menu_goods,
      url: "/goods/lists",
    },
    {
      name: "订单查询",
      image: menu_order,
      url: "/order/order/list",
    },
    {
      name: "店铺装修",
      image: menu_decoration,
      url: "/decoration/pages",
    },
    {
      name: "用户管理",
      image: menu_user,
      url: "/consumer/lists",
    },
    {
      name: "网站设置",
      image: menu_setting,
      url: "/setting/website/information",
    },
  ], // 常用功能
  visitor: [], // 访问量
  article: [], // 文章阅读量

  salesOption: {
    xAxis: {
      type: "category",
      data: [0],
    },
    yAxis: {
      type: "value",
    },
    legend: {
      data: ["销售额"],
    },
    itemStyle: {
      // 点的颜色。
      color: "red",
    },
    tooltip: {
      trigger: "axis",
    },
    series: [
      {
        name: "销售额",
        data: [0],
        type: "line",
        smooth: true,
      },
    ],
  },

  visitorOption: {
    xAxis: {
      type: "category",
      data: [0],
    },
    yAxis: {
      type: "value",
    },
    legend: {
      data: ["访问量"],
    },
    itemStyle: {
      // 点的颜色。
      color: "red",
    },
    tooltip: {
      trigger: "axis",
    },
    series: [
      {
        name: "访问量",
        data: [0],
        type: "line",
        smooth: true,
      },
    ],
  },
});
const ShowDoc = ref<number>(0);
// 获取工作台主页数据
const getData = async () => {
  const res = await getWorkbench();
  /**
   * @description 控制文档信息显示
   */
  const { isShowDoc } = await getWebsite();
  ShowDoc.value = isShowDoc;
  workbenchData.version = res.version;
  workbenchData.today = res.today;
  workbenchData.stay = res.stay;
  workbenchData.visitor = res.visitor;

  // 清空echarts 数据
  workbenchData.visitorOption.xAxis.data = [];
  workbenchData.visitorOption.series[0].data = [];

  workbenchData.salesOption.xAxis.data = [];
  workbenchData.salesOption.series[0].data = [];

  // 写入从后台拿来的数据
  workbenchData.visitorOption.xAxis.data = res.visitor.date;
  workbenchData.visitorOption.series[0].data = res.visitor.visits;

  workbenchData.salesOption.xAxis.data = res.visitor.date;
  workbenchData.salesOption.series[0].data = res.visitor.salesVolume;
};

getData();
</script>

<style lang="scss" scoped></style>
