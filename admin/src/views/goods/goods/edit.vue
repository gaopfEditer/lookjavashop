<template>
    <div class="goods__edit">
        <el-card class="!border-none" shadow="never">
            <el-page-header :content="$route.meta.title" @back="handleRouteBack" />
        </el-card>
        <el-card class="mt-4 !border-none" shadow="never" :body-style="{ 'padding-top': '10px' }">
            <el-form
                ref="formRef"
                :model="formData"
                label-width="120px"
                :rules="rules"
                :disabled="read_only"
            >
                <!-- 选项卡 -->
                <el-tabs v-model="tabsParams.active" class="demo-tabs">
                    <template
                        v-for="(tabsItem, tabsIndex) in tabsParams.TabsEnumMap"
                        :key="tabsIndex"
                    >
                        <el-tab-pane :label="tabsItem.label" :name="tabsItem.type">
                            <component :is="tabsItem.comp" :modelValue="formData"></component>
                        </el-tab-pane>
                    </template>
                </el-tabs>
            </el-form>
        </el-card>
        <footer-btns v-perms="['goods:product:add', 'goods:product:edit']">
            <el-button type="primary" @click="handleSave" :disabled="read_only">保存</el-button>
        </footer-btns>
    </div>
</template>

<script lang="ts" setup name="GoodsEdit">
import type { FormInstance, FormRules } from 'element-plus'
import { getGoodsDetail, goodsEdit, goodsAdd } from '@/api/goods/goods'
import type { GoodsFormParamsType } from '@/api/goods/goods.d'
import useMultipleTabs from '@/hooks/useMultipleTabs'
import BaseSetup from './component/base-setup.vue'
import PriceStock from './component/price-stock.vue'
import ExpressSetup from './component/express-setup.vue'
import GoodsDetail from './component/goods-detail.vue'
import SalesSetup from './component/sales-setup.vue'
import feedback from '@/utils/feedback'

const route = useRoute()
const router = useRouter()

// Tab类型
enum TabsEnum {
    BASE_SETUP = 'base_setup',
    PRICE_STOCK = 'price_stock',
    EXPRESS_SETUP = 'express_setup',
    GOODS_DETAIL = 'goods_detail',
    SALES_SETUP = 'sales_setup'
}
const read_only: any = route.query.read_only
const tabsParams = reactive({
    active: TabsEnum.BASE_SETUP,
    TabsEnumMap: [
        {
            label: '基础设置',
            comp: markRaw(BaseSetup),
            type: TabsEnum.BASE_SETUP
        },
        {
            label: '价格/库存',
            comp: markRaw(PriceStock),
            type: TabsEnum.PRICE_STOCK
        },
        {
            label: '物流设置',
            comp: markRaw(ExpressSetup),
            type: TabsEnum.EXPRESS_SETUP
        },
        {
            label: '商品详情',
            comp: markRaw(GoodsDetail),
            type: TabsEnum.GOODS_DETAIL
        },
        {
            label: '销售设置',
            comp: markRaw(SalesSetup),
            type: TabsEnum.SALES_SETUP
        }
    ]
})

const formData = reactive<GoodsFormParamsType>({
    id: '',
    name: '',
    code: '',
    description: '',
    categoryId: [],
    goodsImage: [],
    videoStatus: 0,
    videoSource: 1,
    videoCover: '',
    video: '',
    specType: 1,
    specValue: [
        {
            name: '',
            hasImage: 0,
            specList: [
                {
                    value: '',
                    image: ''
                }
            ]
        }
    ],
    specValueList: [
        {
            id: '',
            image: '',
            ids: '',
            skuValueIds: '',
            skuValueArr: '',
            price: '',
            linePrice: '',
            marketPrice: '',
            stock: '',
            weight: 0,
            volume: '',
            code: ''
        }
    ],
    expressType: 1,
    expressTemplateId: '',
    content: '',
    stockWarning: '',
    virtualSalesNum: '',
    virtualClickNum: '',
    sort: 0,
    status: 1,
    isExpress: 1,
    isSelffetch: 1
})

const { removeTab } = useMultipleTabs()
const formRef = shallowRef<FormInstance>()
const rules = reactive<FormRules>({
    code: [{ required: true, message: '请输入商品货号', trigger: 'change' }],
    name: [{ required: true, message: '请输入商品名称', trigger: 'change' }],
    categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
    goodsImage: [{ required: true, message: '请选择商品轮播图', trigger: 'change' }],
    video: [{ required: true, message: '请输选择视频', trigger: 'change' }],
    specType: [{ required: true, message: '请选择规格类型', trigger: 'change' }],
    expressType: [{ required: true, message: '请选择运费设置', trigger: 'change' }],
    expressTemplateId: [{ required: true, message: '请选择运费模版', trigger: 'change' }],
    content: [{ required: true, message: '请输商品详情', trigger: 'change' }],
    status: [{ required: true, message: '请选择销售状态', trigger: 'change' }]
})

const getDetails = async () => {
    const data = await getGoodsDetail({
        id: route.query.id as any
    })
    Object.keys(formData).forEach((key) => {
        //@ts-ignore
        formData[key] = data[key]
    })
}

const handleSave = async () => {
    try {
        await formRef.value?.validate((valid: boolean, requiredFields: any) => {
            // 基础设置必填字段
            const basicFields = ['code', 'name', 'categoryId', 'goodsImage', 'video']
            // 价格库存必填字段
            const skuFields = ['specType', 'price', 'stock']
            // 物流设置必填字段
            const expressFields = ['expressType', 'expressTemplateId']
            // 商品详情必填字段
            const contentFields = ['content']
            // 销售设置必填字段
            const salesFields = ['']
            console.log(requiredFields, 111)

            for (const key in requiredFields) {
                console.log(key, 222)

                if (basicFields.includes(key)) tabsParams.active = TabsEnum.BASE_SETUP
                else if (skuFields.includes(key)) tabsParams.active = TabsEnum.PRICE_STOCK
                else if (expressFields.includes(key)) tabsParams.active = TabsEnum.EXPRESS_SETUP
                else if (contentFields.includes(key)) tabsParams.active = TabsEnum.GOODS_DETAIL
                else if (salesFields.includes(key)) tabsParams.active = TabsEnum.SALES_SETUP
                feedback.msgError(requiredFields[key][0].message)
                throw Error('请填写必填字段')
            }
        })

        if (route.query.id) {
            await goodsEdit(formData)
        } else {
            await goodsAdd(formData)
        }
        handleRouteBack()
        feedback.msgSuccess('操作成功')
    } catch (error) {
        console.log('保存商品报错', error)
    }
}

const handleRouteBack = () => {
    removeTab()
    router.back()
}

onMounted(() => {
    route.query.id && getDetails()
})
</script>
