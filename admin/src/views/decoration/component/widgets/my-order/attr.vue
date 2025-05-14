<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="标题名称">
                <el-input class="w-[400px]" v-model="content.title" />
            </el-form-item>
            <el-form-item label="菜单设置">
                <div class="w-full" v-for="(item, index) in content.data" :key="item">
                    <div>{{ item.name }}</div>
                    <div class="bg-fill-light max-w-[400px] p-4">
                        <div class="w-full flex">
                            <div class="mr-3">菜单图标</div>
                            <material-picker v-model="content.data[index].image" size="60px"
                                exclude-domain></material-picker>
                        </div>
                        <div class="w-full flex mt-[18px]">
                            <div class="mr-3">菜单名称</div>
                            <el-input v-model="content.data[index].name" class="w-[200px] mr-3"></el-input>
                            <el-checkbox v-model="content.data[index].show" :true-label="1"
                                :false-label="0"></el-checkbox><span class="mr-3">显示</span>
                        </div>
                    </div>
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type options from './options'
type OptionsType = ReturnType<typeof options>
defineProps({
    content: {
        type: Object as PropType<OptionsType['content']>,
        default: () => ({})
    },
    styles: {
        type: Object as PropType<OptionsType['styles']>,
        default: () => ({})
    }
})
</script>

<style lang="scss" scoped></style>
