<template>
    <el-card shadow="never" class="!border-none px-[20px]">
        <div class="text-xl font-medium">配色方案</div>
        <div class="mt-[20px] flex">
            <div
                class="py-[18px] px-[20px] flex items-center text-[14px] shadow rounded-lg mr-[20px]"
                @click="handleSeclect(index)"
                :class="{ actived: formData.themeColorId == index }"
                v-for="(item, index) in styleLists"
                :key="index"
            >
                <div
                    class="rounded-full h-[34px] w-[34px]"
                    :style="`background: linear-gradient(to right, ${item.color1}, ${item.color2})`"
                ></div>
                <div class="ml-[14px]">{{ item.name }}</div>
            </div>
        </div>
        <div class="text-xl font-medium mt-[55px]">DIY样式设置</div>
        <el-form label-width="140px" class="mt-[20px]">
            <el-form-item label="导航顶部文字颜色">
                <div>
                    <el-radio-group v-model="formData.topTextColor">
                        <el-radio label="white" size="large">白色</el-radio>
                        <el-radio label="black" size="large">黑色</el-radio>
                    </el-radio-group>
                </div>
            </el-form-item>
            <el-form-item label="导航顶部背景颜色">
                <div class="flex">
                    <color-picker
                        v-model="formData.navigationBarColor"
                        :defaultColor="styleLists[formData.themeColorId].color1"
                    ></color-picker>
                    <span class="text-[#999999] ml-4">
                        顶部文字颜色与导航背景颜色切勿设置同一种
                    </span>
                </div>
            </el-form-item>
            <el-form-item label="自定义主题色">
                <div class="flex">
                    <color-picker
                        v-model="formData.main_color"
                        :defaultColor="styleLists[formData.themeColorId].color1"
                    ></color-picker>
                </div>
            </el-form-item>
            <el-form-item label="自定义辅助深色主题" v-if="formData.themeColorId == 7">
                <div class="flex">
                    <color-picker
                        v-model="formData.deep_color"
                        :defaultColor="styleLists[formData.themeColorId].deep"
                    ></color-picker>
                </div>
            </el-form-item>
            <el-form-item label="自定义辅助淡色主题" v-if="formData.themeColorId == 7">
                <div class="flex">
                    <color-picker
                        v-model="formData.undeep_color"
                        :defaultColor="styleLists[formData.themeColorId].undeep"
                    ></color-picker>
                </div>
            </el-form-item>
            <el-form-item label="自定义辅助禁用主题" v-if="formData.themeColorId == 7">
                <div class="flex">
                    <color-picker
                        v-model="formData.enabled_color"
                        :defaultColor="styleLists[formData.themeColorId].enabled_color"
                    ></color-picker>
                </div>
            </el-form-item>
        </el-form>

        <div class="flex mt-[50px]" v-if="formData.themeColorId != 7">
            <img class="theme-preview" :src="getAssetsFile(`${theme}_home.png`)" />
            <img class="theme-preview" :src="getAssetsFile(`${theme}_goods.png`)" />
            <img class="theme-preview" :src="getAssetsFile(`${theme}_order.png`)" />
            <img class="theme-preview" :src="getAssetsFile(`${theme}_user.png`)" />
        </div>
    </el-card>
    <footer-btns class="mt-4" :fixed="true" v-perms="['decorate:pages:save']">
        <el-button type="primary" @click="setData">保存</el-button>
    </footer-btns>
</template>
<script setup lang="ts">
import { getDecoratePages, setDecoratePages } from '@/api/decoration'
import feedback from '@/utils/feedback'
const theme = ref('')
//配色
const styleLists = [
    {
        id: 1,
        name: '经典红',
        color1: '#FF2C3C',
        color2: '#FF2E3C',
        deep: '#EF1D2D',
        undeep: '#FFE9EB',
        enabled_color: '#FF959D'
    },
    {
        id: 2,
        name: '商务紫',
        color1: '#8E2DE2',
        color2: '#8555BF',
        deep: '#8E2DE2',
        undeep: '#F8F0FF',
        enabled_color: '#C695F0'
    },
    {
        id: 3,
        name: '活力橙',
        color1: '#F7971E',
        color2: '#FF6E31',
        deep: '#FFD200',
        undeep: '#FFF4E8',
        enabled_color: '#FBCB8E'
    },
    {
        id: 4,
        name: '美妆色',
        color1: '#FD498F',
        color2: '#FD478F',
        deep: '#FA444D',
        undeep: '#FEECF4',
        enabled_color: '#FCA1A5'
    },
    {
        id: 5,
        name: '科技蓝',
        color1: '#2481FF',
        color2: '#034373',
        deep: '#4AD3FF',
        undeep: '#E9F2FD',
        enabled_color: '#91C0FF'
    },
    {
        id: 6,
        name: '生鲜绿',
        color1: '#2EC840',
        color2: '#0CA451',
        deep: '#3DE650',
        undeep: '#E9FAEB',
        enabled_color: '#96E39F'
    },
    {
        id: 7,
        name: '土豪金',
        color1: '#E0A356',
        color2: '#E1C073',
        deep: '#EBC389',
        undeep: '#FBF7F1',
        enabled_color: '#EFD1AA'
    },
    {
        id: 8,
        name: '自定义',
        color1: '#FFFFFF',
        color2: '#FFFFFF',
        deep: '#FFFFFF',
        undeep: '#FFFFFF',
        enabled_color: '#FFFFFF'
    }
]
//选择的风格
const handleSeclect = (index: number) => {
    formData.value.themeColorId = index
    getThemename(index)

    formData.value.navigationBarColor = styleLists[index].color1
    formData.value.main_color = styleLists[index].color1
    formData.value.deep_color = styleLists[index].deep
    formData.value.undeep_color = styleLists[index].undeep
    formData.value.enabled_color = styleLists[index].enabled_color
}
const formData = ref<any>({
    themeColorId: 0,
    topTextColor: 'black',
    navigationBarColor: '',
    main_color: '',
    deep_color: '',
    undeep_color: '',
    enabled_color: ''
})

const setData = async () => {
    try {
        await setDecoratePages({
            id: 9,
            type: 9,
            name: '商城风格',
            pageData: JSON.stringify(formData.value)
        })
        await getData()
        feedback.msgSuccess('保存成功')
    } catch (error: any) {
        feedback.msgError(error)
    }
}
const getData = async () => {
    const { pageData } = await getDecoratePages({ id: 9 })
    formData.value = JSON.parse(pageData)

    getThemename(formData.value.themeColorId)
}
getData()
const getAssetsFile = (url: string) => {
    return new URL(`./image/theme/${url}`, import.meta.url).href
}
const getThemename = (id: number) => {
    switch (id) {
        case 0:
            theme.value = 'red_theme'
            break
        case 1:
            theme.value = 'purple_theme'
            break
        case 2:
            theme.value = 'orange_theme'
            break
        case 3:
            theme.value = 'pink_theme'
            break
        case 4:
            theme.value = 'blue_theme'
            break
        case 5:
            theme.value = 'green_theme'
            break
        case 6:
            theme.value = 'gold_theme'
            break
    }
}
</script>
<style lang="scss" scoped>
.actived {
    border: 3px solid rgba(64, 115, 250, 1);
}

.theme-preview {
    width: 280px;
    // height: 498px;
    margin-right: 40px;
    box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.05);
}
</style>
