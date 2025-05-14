<template>
    <!-- 物流信息 -->
    <div class="my-4 text-xl font-medium">物流信息</div>
    <div class="leading-9">
        <div>快递公司: {{ order.expressCompanyName }}</div>
        <div>发货单号: {{ order.expressNo }}</div>
        <div>发货时间: {{ order.expressConfirmTime }}</div>
    </div>

    <!-- 物流轨迹 -->
    <div class="my-4 text-xl font-medium">物流轨迹</div>
    <el-table size="large" v-if="order.logistics.length" :data="order.logistics">
        <el-table-column prop="time" label="日期" width="220" />
        <el-table-column prop="content" label="轨迹" min-width="220" />
    </el-table>
    <div v-else class="flex nr weight-500 m-t-60 row-center">
        {{ order.logistics_error || '找不到该物流轨迹！' }}
    </div>
</template>

<script lang="ts" setup>
import type { PropType } from "vue"
defineProps({
    order: {
        type: Object as PropType<any>,
        default: () => {}
    }
})
</script>