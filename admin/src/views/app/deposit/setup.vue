<template>
    <div class="deposit-setup">
        <el-form ref="formRef" :rules="rules" :model="formData" label-width="140px">
            <el-card shadow="never" class="!border-none" :body-style="{ 'padding-top': '0' }">
                <el-tabs v-model="activeName">
                    <el-tab-pane label="充值设置" :name="1"></el-tab-pane>
                </el-tabs>

                <el-form-item label="充值功能" prop="openRecharge">
                    <div>
                        <el-radio-group v-model="formData.openRecharge" class="ml-4">
                            <el-radio :label="1">开启</el-radio>
                            <el-radio :label="0">关闭</el-radio>
                        </el-radio-group>
                        <div class="form-tips">
                            关闭或开启充值功能，关闭后不显示商城充值功能进入接口
                        </div>
                    </div>
                </el-form-item>

                <el-form-item label="最低充值金额">
                    <div>
                        <el-input v-model="formData.minRechargeMoney" placeholder="请输入" />
                        <div class="form-tips">
                            最低充值金额要求，不填或填0表示不限制最低充值金额
                        </div>
                    </div>
                </el-form-item>
            </el-card>
        </el-form>

        <footer-btns v-perms="['marketing:recharge:save']">
            <el-button type="primary" @click="handleSubmit">保存</el-button>
        </footer-btns>
    </div>
</template>

<script lang="ts" setup name="Deposit">
import type { DepositFormType } from '@/api/app/deposit.d'
import { getDeposit, setDeposit } from '@/api/app/deposit'
import type { FormInstance, FormRules } from 'element-plus'

const activeName = ref(1)
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<DepositFormType>({
    openRecharge: 1,
    minRechargeMoney: 0
})

// 表单验证
const rules = reactive<FormRules>({
    openRecharge: [{ required: true, trigger: 'blur' }]
})

// 获取登录注册数据
const getData = async () => {
    try {
        const data = await getDeposit()
        for (const key in formData) {
            //@ts-ignore
            formData[key] = data[key]
        }
    } catch (error) {
        console.log('获取=>', error)
    }
}

// 保存登录注册数据
const handleSubmit = async () => {
    await formRef.value?.validate()
    try {
        await setDeposit(formData)
        await getData()
    } catch (error) {
        console.log('保存=>', error)
    }
}

getData()
</script>
