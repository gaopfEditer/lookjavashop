<template>
    <div>
        <el-form label-width="70px">
            <el-form-item label="标题名称">
                <el-input class="w-[400px]" v-model="content.title" />
            </el-form-item>
            <el-form-item label="菜单设置">
                <div class="flex-1">
                    <AddSub :max="5" :min="5" v-model="content.data" />
                </div>
            </el-form-item>
        </el-form>
    </div>
</template>
<script lang="ts" setup>
import type { PropType } from 'vue'
import type options from './options'
import AddSub from '../../add-sub.vue'
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

<style lang="scss" scoped>

</style>
