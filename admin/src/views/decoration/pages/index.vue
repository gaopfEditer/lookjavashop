<template>
    <div class="decoration-pages min-w-[1100px]">
        <el-card shadow="never" class="!border-none flex-1 flex" :body-style="{ flex: 1 }">
            <div class="flex h-full items-start">
                <Menu v-model="activeMenu" :menus="menus" />
                <preview v-model="selectWidgetIndex" :pageData="getPageData" @changeIndex="changeIndex"/>
                <attr-setting class="flex-1" :widget="getSelectWidget" />
            </div>
        </el-card>
        <footer-btns class="mt-4" :fixed="false" v-perms="['decorate:pages:save']">
            <el-button type="primary" @click="setData">保存</el-button>
        </footer-btns>
    </div>
</template>
<script lang="ts" setup name="decorationPages">
import Menu from '../component/pages/menu.vue'
import Preview from '../component/pages/preview.vue'
import AttrSetting from '../component/pages/attr-setting.vue'
import widgets from '../component/widgets'
import { getDecoratePages, setDecoratePages } from '@/api/decoration'
import feedback from '@/utils/feedback'
import { getNonDuplicateID } from '@/utils/util'
enum pagesTypeEnum {
    HOME = '1',
    CATEGORY = '4',
    GOODSDETAIL = '5',
    HOT = '6',
    USER = '2',
    SERVICE = '3',
    SECKILL = '7',
    COUPON = '8'
}

const generatePageData = (widgetNames: string[]) => {
    return widgetNames.map((widgetName) => {
        const options = {
            id: getNonDuplicateID(),
            ...(widgets[widgetName]?.options() || {})
        }
        return options
    })
}

const menus: Record<
    string,
    {
        id: number
        name: string
        pageData: any[]
    }
> = reactive({
    [pagesTypeEnum.HOME]: {
        id: 1,
        type: 1,
        name: '商城首页',
        pageData: generatePageData(['search', 'banner', 'nav', 'notice', 'seckill', 'hot', 'new'])
    },
    [pagesTypeEnum.CATEGORY]: {
        id: 4,
        type: 4,
        name: '分类页面',
        pageData: generatePageData(['category'])
    },
    [pagesTypeEnum.GOODSDETAIL]: {
        id: 5,
        type: 5,
        name: '商品详情',
        pageData: generatePageData([
            'goods-header',
            'goods-detail',
            'goods-body',
            'goods-evaluate',
            'goods-like'
        ])
    },
    [pagesTypeEnum.HOT]: {
        id: 6,
        type: 6,
        name: '热门商品',
        pageData: generatePageData(['hot-banner', 'hot-goods'])
    },
    [pagesTypeEnum.SECKILL]: {
        id: 7,
        type: 7,
        name: '限时秒杀',
        pageData: generatePageData(['seckill-banner', 'seckill-goods'])
    },
    [pagesTypeEnum.COUPON]: {
        id: 8,
        type: 8,
        name: '领券中心',
        pageData: generatePageData(['banner', 'coupon-list'])
    },
    [pagesTypeEnum.USER]: {
        id: 2,
        type: 2,
        name: '个人中心',
        pageData: generatePageData([
            'user-info',
            'my-order',
            'my-service',
            'user-banner',
            'all-buy'
        ])
    },
    [pagesTypeEnum.SERVICE]: {
        id: 3,
        type: 3,
        name: '客服设置',
        pageData: generatePageData(['customer-service'])
    }
})

const activeMenu = ref('1')
const selectWidgetIndex = ref(-1)
const getPageData = computed(() => {
    return menus[activeMenu.value]?.pageData ?? []
})
const getSelectWidget = computed(() => {
    return menus[activeMenu.value]?.pageData[selectWidgetIndex.value] ?? ''
})

const getData = async () => {
    const data = await getDecoratePages({ id: activeMenu.value })
    menus[String(data.id)].pageData = JSON.parse(data.pageData)
    console.log('pageData', JSON.parse(data.pageData))
}

const setData = async () => {
    await setDecoratePages({
        ...menus[activeMenu.value],
        pageData: JSON.stringify(menus[activeMenu.value].pageData)
    })
    await getData()
    feedback.msgSuccess('保存成功')
}
watch(
    activeMenu,
    () => {
        selectWidgetIndex.value = getPageData.value.findIndex((item) => !item.disabled)
        getData()
    },
    {
        immediate: true
    }
)
const changeIndex = (move)=>{
    if(move&&move.moved&&menus[activeMenu.value]?.pageData){
        const moved = move.moved
        const [oldNode] = menus[activeMenu.value]?.pageData.splice(moved.oldIndex, 1)
        menus[activeMenu.value]?.pageData.splice(moved.newIndex, 0, oldNode)
    }
}
</script>
<style lang="scss" scoped>
.decoration-pages {
    min-height: calc(100vh - var(--navbar-height) - 80px);
    @apply flex flex-col;
}
</style>
