<template>
    <el-table size="large" :data="goods">
        <el-table-column prop="goodsCode" label="商品货号" min-width="100" />
        <el-table-column prop="goods" label="商品信息" min-width="220">
            <template #default="{ row }">
                <div class="flex mb-[5px]">
                    <image-contain
                        class="flex-none"
                        :src="row.goodsImage"
                        :width="70"
                        :height="70"
                        :preview-src-list="[row.goodsImage]"
                        :preview-teleported="true"
                        fit="cover"
                    />
                    <div class="ml-[6px] text-xs overflow-hidden">
                        <div class="mt-2 goods-name">{{ row.goodsName }}</div>
                        <div class="text-tx-placeholder">{{ row?.goodsSkuValue }}</div>
                    </div>
                </div>
            </template>
        </el-table-column>
        <el-table-column label="单价/数量" min-width="100">
            <template #default="{ row }">
                <div>
                    <div>¥{{ row.goodsPrice }}</div>
                    <div>x <span>{{ row.goodsNum }}</span></div>
                </div>
            </template>
        </el-table-column>
<!--        <el-table-column label="运费" min-width="100">-->
<!--            <template #default="{ row }">-->
<!--                <div>¥{{ row.expressMoney }}</div>-->
<!--            </template>-->
<!--        </el-table-column>-->
        <el-table-column label="商品总价" min-width="100">
            <template #default="{ row }">
                <div>¥{{ row.goodsMoney }}</div>
            </template>
        </el-table-column>
        <el-table-column label="实付金额" min-width="100">
            <template #default="{ row }">
                <div>¥{{ row.payMoney }} </div>
            </template>
        </el-table-column>
        <el-table-column label="操作" min-width="100">
            <template #default="{ row }">
                <el-button type="primary" link>
                    <router-link
                        :to="{
                            path: getRoutePath('order:order:detail'),
                            query: {
                                id: row.orderId
                            }
                        }"
                    >
                        查看订单
                    </router-link>
                </el-button>
            </template>
        </el-table-column>
    </el-table>
</template>

<script lang="ts" setup>
import { getRoutePath } from '@/router'
import type { PropType } from 'vue'
defineProps({
    goods: {
        type: Array as PropType<any>,
        default: () => []
    }
})
</script>