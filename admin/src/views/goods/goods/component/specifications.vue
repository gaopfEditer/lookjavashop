<template>
    <div class="ml-[80px] pr-8">
        <div class="flex m-4">
            <el-button type="primary" @click="addSpecItem">添加规格项</el-button>
        </div>

        <template v-for="(item, index) in specItem" :key="index">
            <del-wrap @close="handleDelSpecItem(index)">
                <div class="flex p-[16px] ml-4 mt-[16px] spec-item">
                    <div class="flex-none mr-[10px]">
                        <div class="mt-2">规格名</div>
                        <div class="mt-6">规格值</div>
                    </div>
                    <div class="spec-item__content">
                        <el-form-item
                            label-width="0"
                            :prop="`specValue.${index}.name`"
                            :rules="[
                                {
                                    required: true,
                                    message: '请输入规格名称',
                                    trigger: 'blur'
                                }
                            ]"
                        >
                            <el-input
                                v-model="item.name"
                                style="width: 240px"
                                maxlength="20"
                                :show-word-limit="true"
                            >
                            </el-input>
                            <el-checkbox
                                class="ml-4"
                                :false-label="0"
                                :true-label="1"
                                v-model="item.hasImage"
                                @change="addImage(index, $event)"
                                >规格图片</el-checkbox
                            >
                        </el-form-item>
                        <div class="flex flex-wrap col-top">
                            <el-form-item
                                v-for="(subItem, subIndex) in item.specList"
                                :key="subIndex"
                                label-width="0"
                                :prop="`specValue.${index}.specList.${subIndex}.value`"
                                :rules="[
                                    {
                                        required: true,
                                        message: '请输入规格值',
                                        trigger: 'blur'
                                    }
                                ]"
                                class="mr-2"
                            >
                                <del-wrap @close="removeSpecValue(index, subIndex)">
                                    <el-input
                                        class="w-40"
                                        v-model="subItem.value"
                                        maxlength="20"
                                        show-word-limit
                                        @blur="checkValue(index, subIndex)"
                                    ></el-input>
                                </del-wrap>
                                <div v-if="item.hasImage">
                                    <material-picker
                                        class="mt-4"
                                        :limit="1"
                                        size="60px"
                                        v-model="subItem.image"
                                    >
                                    </material-picker>
                                </div>
                            </el-form-item>
                            <el-button class="ml-2" @click="addSpecValue(index)">
                                + 添加规格值
                            </el-button>
                        </div>
                    </div>
                </div>
            </del-wrap>
        </template>
    </div>

    <el-form-item label="规格明细" class="mt-8">
        <div class="flex pl-[10px] mb-4">
            <popover-input
                class="mr-2"
                :disabled="disabledBatchBtn"
                @confirm="batchSetting($event, 'price')"
            >
                <el-button :disabled="disabledBatchBtn">设置价格</el-button>
            </popover-input>
            <popover-input
                class="mr-2"
                :disabled="disabledBatchBtn"
                @confirm="batchSetting($event, 'linePrice')"
            >
                <el-button :disabled="disabledBatchBtn">设置划线价</el-button>
            </popover-input>
            <popover-input
                class="mr-2"
                :disabled="disabledBatchBtn"
                @confirm="batchSetting($event, 'marketPrice')"
            >
                <el-button :disabled="disabledBatchBtn">设置成本价</el-button>
            </popover-input>
            <popover-input
                class="mr-2"
                :disabled="disabledBatchBtn"
                @confirm="batchSetting($event, 'stock')"
            >
                <el-button :disabled="disabledBatchBtn">设置库存</el-button>
            </popover-input>
            <popover-input
                class="mr-2"
                :disabled="disabledBatchBtn"
                @confirm="batchSetting($event, 'volume')"
            >
                <el-button :disabled="disabledBatchBtn">设置体积</el-button>
            </popover-input>
            <popover-input
                class="mr-2"
                :disabled="disabledBatchBtn"
                @confirm="batchSetting($event, 'weight')"
            >
                <el-button :disabled="disabledBatchBtn">设置重量</el-button>
            </popover-input>
            <popover-input :disabled="disabledBatchBtn" @confirm="batchSetting($event, 'code')">
                <el-button :disabled="disabledBatchBtn">设置条码</el-button>
            </popover-input>
        </div>

        <el-table
            class="pl-[10px]"
            :data="specParams.tableData"
            max-height="600"
            :row-height="75"
            tooltip-effect="dark"
            :border="false"
            :big-data-checkbox="true"
            @selection-change="selectDataChange"
        >
            <el-table-column type="selection" width="55" />
            <el-table-column
                v-for="(item, index) in specItem"
                :key="index"
                :label="item.name"
                min-width="140"
                :show-overflow-tooltip="true"
            >
                <template #default="{ row }">
                    {{ row.skuValueArr.split(',')[index] }}
                </template>
            </el-table-column>
            <el-table-column label="规格图片" min-width="90">
                <template #default="{ row, $index }">
                    <del-wrap @close="removeSpecImage($index)" v-if="row.image">
                        <el-image
                            style="width: 50px; height: 50px"
                            :src="row.image"
                            @click="addSpecImage($index)"
                        >
                        </el-image>
                    </del-wrap>
                    <div
                        class="flex items-center justify-center spec-image"
                        @click="addSpecImage($index)"
                        v-else
                    >
                        <el-icon><Plus /></el-icon>
                    </div>
                </template>
            </el-table-column>
            <el-table-column min-width="100">
                <template #header> <span class="text-error">*</span> 价格 </template>
                <template #default="{ row, $index }">
                    <el-form-item
                        label-width="0"
                        :prop="`specValueList.${$index}.price`"
                        :rules="[
                            {
                                required: true,
                                message: '请输入规格价格',
                                trigger: 'blur'
                            }
                        ]"
                    >
                        <el-input class="spec-input" type="number" v-model="row.price"></el-input>
                    </el-form-item>
                </template>
            </el-table-column>
            <el-table-column min-width="100">
                <template #header> 划线价</template>
                <template #default="{ row }">
                    <el-input class="spec-input" type="number" v-model="row.linePrice"></el-input>
                </template>
            </el-table-column>
            <el-table-column label="成本价" min-width="100">
                <template #default="{ row }">
                    <el-input class="spec-input" type="number" v-model="row.marketPrice"></el-input>
                </template>
            </el-table-column>
            <el-table-column min-width="100">
                <template #header> <span class="text-error">*</span> 库存 </template>
                <template #default="{ row, $index }">
                    <el-form-item
                        label-width="0"
                        :prop="`specValueList.${$index}.stock`"
                        :rules="[
                            {
                                required: true,
                                message: '请输入规格库存',
                                trigger: 'blur'
                            }
                        ]"
                    >
                        <el-input class="spec-input" type="number" v-model="row.stock"></el-input>
                    </el-form-item>
                </template>
            </el-table-column>
            <el-table-column min-width="100">
                <template #header> 体积</template>
                <template #default="{ row }">
                    <el-input class="spec-input" type="number" v-model="row.volume"></el-input>
                </template>
            </el-table-column>
            <el-table-column label="重量" min-width="100">
                <template #header> 重量</template>
                <template #default="{ row }">
                    <el-input class="spec-input" type="number" v-model="row.weight"></el-input>
                </template>
            </el-table-column>
            <el-table-column label="条码" min-width="100">
                <template #default="{ row }">
                    <el-input class="spec-input" type="number" v-model="row.code"></el-input>
                </template>
            </el-table-column>
        </el-table>
    </el-form-item>

    <!--  资源选择组件  -->
    <material-picker ref="materialRef" :hiddenUpload="true" @change="changeSpecImage" />
</template>

<script lang="ts" setup>
import feedback from '@/utils/feedback'
import { flatten } from '@/utils/util'
import { useDebounceFn } from '@vueuse/core'

import type { SkuItemList, SkuNameList } from '@/api/goods/goods.d'

const props = withDefaults(
    defineProps<{
        modelValue: any
    }>(),
    {
        modelValue: {}
    }
)

const materialRef = shallowRef()
const specParams = reactive<any>({
    tableData: [],
    selectData: [],
    tableDataIndex: 0 as number
})

const disabledBatchBtn = computed(() => !specParams.selectData.length)

const specItem = computed(() => props.modelValue.specValue || [])
const specSubItem = computed(() => props.modelValue.specValueList || [])

// 新增规格项
const addSpecItem = () => {
    props.modelValue.specValue.push({
        hasImage: 0,
        id: '',
        name: '',
        specList: [
            {
                value: '',
                image: ''
            }
        ]
    })
}

// 删除规格项
const handleDelSpecItem = (index: number) => {
    const len = props.modelValue.specValue.length
    if (len <= 1) return feedback.msgError('至少一个规格项')
    props.modelValue.specValue.splice(index, 1)
}

// 新增规格值
const addSpecValue = (index: number) => {
    props.modelValue.specValue[index].specList.push({
        // id: "",
        value: '',
        image: ''
    })
}

// 删除规格值
const removeSpecValue = (index: number, subIndex: number) => {
    props.modelValue.specValue[index].specList.splice(subIndex, 1)
}

const addImage = (i: number, v: any) => {
    const skuItem: SkuNameList[] = props.modelValue.specValue
    const skuSubItem: SkuItemList[] = props.modelValue.specValueList
    skuItem.forEach((item, index: number) => {
        item.hasImage = 0
        if (i == index) {
            item.hasImage = v
        }
    })
    skuSubItem.forEach((sitem) => {
        sitem.image = ''
    })
    specParams.tableData.forEach((item: { image: string }) => {
        item.image = ''
    })
}

// 娇艳规格值
const checkValue = (index: number, subIndex: number) => {
    const skuItem = props.modelValue?.specValue[index]
    const value = skuItem?.specList[subIndex].value
    const res = skuItem?.specList.filter(
        (item: { value: string }) => item.value == value && value != '' && value.length != 0
    )
    const lessTops = res.length >= 2
    if (lessTops) {
        feedback.msgWarning('已存在相同规格值')
        skuItem.specList[subIndex].value = ''
    }
}

const selectDataChange = (value: SkuItemList[]) => {
    specParams.selectData = value.map((item) => item.ids)
}

const batchSetting = (value: string, fields: string | never) => {
    specParams.tableData.forEach((item: { [x: string]: string; ids: any }) => {
        if (specParams.selectData.includes(item.ids)) {
            item[fields] != undefined && (item[fields] = value)
        }
    })
}

//设置字段名称
const setFields = (prev: any, next: any) => {
    let valueArr = [prev, next]
    valueArr = valueArr.filter((item) => item.value !== undefined)
    const ids = flatten(valueArr.map((item) => item.ids)).join()
    const value = flatten(valueArr.map((item) => item.value))
    return {
        id: prev.id ? prev.id : '',
        ids: ids,
        value,
        skuValueArr: value.toString(),
        image: prev.image ? prev.image : next.image,
        price: prev.price ? prev.price : '',
        linePrice: prev.linePrice ? prev.linePrice : '',
        marketPrice: prev.marketPrice ? prev.marketPrice : '',
        stock: prev.stock ? prev.stock : '',
        volume: prev.volume ? prev.volume : '',
        weight: prev.weight ? prev.weight : '',
        code: prev.code ? prev.code : ''
    }
}

// 通过规格项和规格值得到一个表格data
const getTableData = (arr: any[]) => {
    arr = JSON.parse(JSON.stringify(arr))
    return arr.reduce(
        (prev, next) => {
            const newArr = []
            for (let i = 0; i < prev.length; i++) {
                if (!next.length) {
                    newArr.push(setFields(prev[i], {}))
                }
                for (let j = 0; j < next.length; j++) {
                    next[j].ids = j
                    newArr.push(setFields(prev[i], next[j]))
                }
            }
            return newArr
        },
        [{}]
    )
}

const setTableData = useDebounceFn(() => {
    const skuNameList = props.modelValue.specValue
    const tableData = specParams.tableData
    const specList = skuNameList.map((item: SkuNameList) => item.specList)
    const newData = getTableData(specList)
    const rawData = JSON.parse(JSON.stringify(tableData))
    const rawObject: any = {}
    rawData.forEach((item: any) => {
        if (item.skuValueArr !== undefined) {
            rawObject[item.skuValueArr] = item
        }
    })

    specParams.tableData = newData.map((item: any) =>
        rawObject[item.skuValueArr]
            ? {
                  ...rawObject[item.skuValueArr],
                  value: item.value,
                  ids: item.ids,
                  image: item.image || rawObject[item.skuValueArr].image
              }
            : item
    )
}, 10)

watch(
    () => specItem.value,
    () => {
        setTableData()
    },
    { deep: true, immediate: true }
)

watch(
    () => specSubItem.value,
    (value) => {
        specParams.tableData = value
    },
    { deep: true, immediate: true }
)

watch(
    () => specParams.tableData,
    (value) => {
        props.modelValue.specValueList = value
    },
    { deep: false, immediate: true }
)

const addSpecImage = (index: number) => {
    specParams.tableDataIndex = index
    materialRef.value?.showPopup()
}
const changeSpecImage = (value: string) => {
    specParams.tableData[specParams.tableDataIndex].image = value
}
const removeSpecImage = (index: number) => {
    specParams.tableData[index].image = ''
}
</script>

<style>
.spec-item {
    transition: all 1s;
    background-color: var(--el-color-primary-light-9);
}

.spec-image {
    width: 50px;
    height: 50px;
    cursor: pointer;
    border: 1px dashed #e5e5e5;
}
</style>
