<template>
    <div class="freight-edit">
        <el-card class="!border-none" shadow="never">
            <el-page-header :content="$route.meta.title" @back="$router.back()" />
        </el-card>
        <el-card class="!border-none mt-4" shadow="never">
            <el-form
                ref="formRef"
                :model="formData"
                :rules="rules"
                label-width="120px"
            >
                <el-form-item label="模板名称" prop="name">
                    <div class="w-80">
                        <el-input
                            v-model="formData.name"
                            placeholder="请输入模板名称"
                        ></el-input>
                    </div>
                </el-form-item>
                <el-form-item label="计费方式" prop="type">
                    <div>
                        <el-radio-group v-model="formData.type" :disabled="formData.id != ''">
                            <el-radio :label="0">件数计费</el-radio>
                            <el-radio :label="1">体积计费</el-radio>
                            <el-radio :label="2">重量计费</el-radio>
                        </el-radio-group>
                        <div class="form-tips">保存后计费方式不能更改</div>
                    </div>
                </el-form-item>
                <el-form-item label="配送区费用" prop="firstNum">
                    <el-table :data="tableData" style="width: 100%" size="mini">
                        <el-table-column min-width="100" :label="`首${getTableTitle}`">
                            <template #default="{ row }">
                                <el-input
                                    class="pt-4 pb-4"
                                    style="width: 90%"
                                    type="number"
                                    v-model="formData.firstNum"
                                >
                                </el-input>
                            </template>
                        </el-table-column>
                        <el-table-column min-width="100" label="运费（元）">
                            <template #default="{ row }">
                                <el-input
                                    class="pt-4 pb-4"
                                    style="width: 90%"
                                    type="number"
                                    v-model="formData.firstPrice"
                                >
                                </el-input>
                            </template>
                        </el-table-column>
                        <el-table-column min-width="100" :label="`续${getTableTitle}`">
                            <template #default="{ row }">
                                <el-input
                                    class="pt-4 pb-4"
                                    style="width: 90%"
                                    type="number"
                                    v-model="formData.continueNum"
                                >
                                </el-input>
                            </template>
                        </el-table-column>
                        <el-table-column min-width="100" label="续费（元）">
                            <template #default="{ row }">
                                <el-input
                                    class="pt-4 pb-4"
                                    style="width: 90%"
                                    type="number"
                                    v-model="formData.continuePrice"
                                ></el-input>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input
                        style="width: 600px"
                        v-model="formData.remark"
                        rows="8"
                        type="textarea"
                        placeholder="请输入备注内容"
                    ></el-input>
                </el-form-item>
            </el-form>
        </el-card>

        <footer-btns>
            <el-button type="primary" @click="handleSave">保存</el-button>
        </footer-btns>
    </div>
</template>

<script lang="ts" setup name="freightEdit">
import type { FormInstance, FormRules } from 'element-plus'
import type { FreightFormType } from '@/api/setting/delivery'
import { freightDetail, freightEdit, freightAdd } from '@/api/setting/delivery'
import useMultipleTabs from '@/hooks/useMultipleTabs'

const route = useRoute()
const router = useRouter()
const formData = reactive<FreightFormType>({
    id: '',
    name: '',
    type: 1,
    remark: '',
    firstNum: '',       // 是	示例值：2说明：首 件/重/体积
    firstPrice: '',     // 是	示例值：3说明：首 件/重/体积 运费
    continueNum: '',    // 是	示例值：4说明：续 件/重/体积
    continuePrice: ''   // 是	示例值：5说明：续 件/重/体积 运费
})

const tableData = computed(() => {
    return [{
        firstNum: formData.firstNum,
        firstPrice: formData.firstPrice,
        continueNum: formData.continueNum,
        continuePrice: formData.continuePrice
    }]
})

const { removeTab } = useMultipleTabs()
const formRef = shallowRef<FormInstance>()
const rules = reactive<FormRules>({
    name: [{ required: true, message: '模版名称', trigger: 'blur' }],
    type: [
        { required: true, message: '请选择计费方式', trigger: 'blur' },
    ],
    firstNum: [{ required: true, message: '请选择配送区费用', trigger: 'blur' }]
})

const getTableTitle = computed(() => {
    const { type } = formData
    switch (type) {
        case 0:
            return '件（件）'
        case 1:
            return '体积（m³）'
        case 2:
            return '重（Kg）'
    }
})

const getDetails = async () => {
    const data: FreightFormType = await freightDetail({
        id: route.query.id
    })
    Object.keys(formData).forEach((key) => {
        formData[key] = data[key]
    })
}


const handleSave = async () => {
    await formRef.value?.validate()
    if (route.query.id) {
        await freightEdit(formData)
    } else {
        await freightAdd(formData)
    }
    removeTab()
    router.back()
}

route.query.id && getDetails()
</script>
