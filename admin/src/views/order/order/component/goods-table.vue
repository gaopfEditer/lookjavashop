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
                    <div class="ml-[6px] text-base overflow-hidden">
                        <div class="goods-name"> {{ row.goodsName }} </div>
                        <div class="form-tips">{{ row.goodsSkuValue }}</div>
                    </div>
                </div>
            </template>
        </el-table-column>
        <el-table-column prop="goodsPrice" label="单价(元)" min-width="70" v-if="showPrice" />
        <el-table-column prop="goodsNum" label="数量(件)" min-width="70" />
        <el-table-column label="商品总价" min-width="100" v-if="showPrice">
            <template #default="{ row }">
                <div>{{ row.goodsMoney }}</div>
            </template>
        </el-table-column>
        <el-table-column label="实付金额" min-width="100" v-if="showPrice">
            <template #default="{ row }">
                <div>¥{{ row.needPayMoney }} </div>
                <div class="form-tips">
                    (含运费：{{ row.expressMoney }})
                </div>
                <div class="form-tips">
                    (含优惠：{{ row.couponMoney }})
                </div>
            </template>
        </el-table-column>
        <el-table-column label="售后状态" min-width="100">
            <template #default="{ row }">
                <div v-if="row.afterSale == 1">{{ row.afterMsg || row.afterSaleMsg }}</div>
                <div v-else class="text-[#fab428]">{{ row.afterMsg || row.afterSaleMsg }}</div>
            </template>
        </el-table-column>
    </el-table>
</template>

<script lang="ts" setup>
import type { PropType } from "vue"
defineProps({
    goods: {
        type: Array as PropType<any>,
        default: () => []
    },
    showPrice: {
        type: Boolean,
        default: false
    }
})
</script>