<template>
    <div>
        <el-card class="!border-none" shadow="never">
            <div class="text-lg font-medium">分销功能</div>
            <div class="mt-4">
                <el-form label-width="120px">
                    <el-form-item label="佣金计算方式">
                        <div>
                            <el-radio-group v-model="formData.settlementTiming">
                                <el-radio :label="1">商品实际支付金额</el-radio>
                            </el-radio-group>
                            <div class="form-tips">根据商品实际给付金额和佣金规格计算佣金</div>
                        </div>
                    </el-form-item>
                    <el-form-item label="结算时机">
                        <div>
                            <el-radio>
                                <div class="flex">
                                    <div>订单完成后</div>
                                    <el-input
                                        v-model="formData.settlementTime"
                                        class="ml-2"
                                    ></el-input>
                                    <div class="ml-2">天</div>
                                </div>
                            </el-radio>
                            <div class="form-tips">根据商品实际给付金额和佣金规格计算佣金</div>
                        </div>
                    </el-form-item>
                </el-form>
            </div>
        </el-card>
        <FooterBtns>
            <el-button @click="submit" type="primary">保存</el-button>
        </FooterBtns>
    </div>
</template>

<script setup lang="ts">
import { getconfig, Saveconfig } from '@/api/distribution/distribution'

const formData = ref({
    settlementTiming: '', //佣金计算方式 0商品实际金额
    settlementTime: ''
})

//获取数据
const getData = async () => {
    formData.value = await getconfig()
}

const submit = async () => {
    await Saveconfig({ ...formData.value })
    getData()
}

onMounted(() => {
    getData()
})
</script>

<style scoped lang="scss"></style>
