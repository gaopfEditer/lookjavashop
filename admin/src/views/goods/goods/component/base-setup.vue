<template>
    <el-form-item label="商品货号" prop="code">
        <div class="w-80">
            <el-input v-model="formData.code" placeholder="请输入商品货号" type="text" :show-word-limit="true" :clearable="true" />
        </div>
    </el-form-item>
    <el-form-item label="商品名称" prop="name">
        <div class="w-80">
            <el-input v-model="formData.name" placeholder="请输入商品名称" type="textarea" :autosize="{ minRows: 2, maxRows: 2 }"
                maxlength="64" :show-word-limit="true" :clearable="true" />
        </div>
    </el-form-item>
    <!-- <el-form-item label="商品描述">
        <div class="w-80">
            <el-input
                v-model="formData.description"
                placeholder="请输入商品描述"
                type="textarea"
                :autosize="{ minRows: 3, maxRows: 5 }"
                :show-word-limit="true"
                :clearable="true"
            />
        </div>
    </el-form-item> -->
    <el-form-item label="商品分类" prop="categoryId">
        <div class="flex">
            <el-cascader class="w-80" v-model="formData.categoryId" :options="goodsCategoryList" :props="cateOpt"
                :clearable="true" :filterable="true" />

            <div class="ml-4">
                <a href="/goods/goodsCategory" target="_blank">
                    <el-button type="primary" :link="true" size="small">新建分类</el-button>
                </a>
                <el-button type="primary" :link="true" size="small" @click="getGoodsCategoryLists()">
                    |&nbsp; 刷新
                </el-button>
            </div>
        </div>
    </el-form-item>
    <el-form-item label="商品轮播图" prop="goodsImage">
        <div>
            <material-picker v-model="formData.goodsImage" :limit="9" />

            <div class="form-tips">
                建议尺寸：800*800,可拖拽改变图片顺序，默认首张图为主图，最多可上传9张
            </div>
        </div>
    </el-form-item>
    <el-form-item label="添加视频">
        <div class="w-80">
            <el-switch v-model="formData.videoStatus" :active-value="1" :inactive-value="0" />
        </div>
    </el-form-item>
    <template v-if="formData.videoStatus">
        <el-form-item label="视频来源" prop="videoSource">
            <div class="w-80">
                <el-radio-group v-model="formData.videoSource">
                    <el-radio :label="1">素材库</el-radio>
                    <el-radio :label="2">网络链接</el-radio>
                </el-radio-group>
            </div>
        </el-form-item>
        <el-form-item label="视频封面">
            <div class="w-80">
                <material-picker v-model="formData.videoCover" :limit="1" />
            </div>
        </el-form-item>
        <el-form-item label="选择视频" prop="video" v-if="formData.videoSource === 1">
            <div class="w-80">
                <material-picker v-model="formData.video" type="video" :limit="1" />
            </div>
        </el-form-item>
        <el-form-item label="视频链接" prop="video" v-if="formData.videoSource === 2">
            <div class="w-80">
                <el-input v-model="formData.video" placeholder="请输入或粘贴视频链接" :show-word-limit="true" :clearable="true" />
            </div>
        </el-form-item>
    </template>
</template>

<script lang="ts" setup>
import { useVModels } from '@vueuse/core'
import { goodsCategoryLists } from '@/api/goods/category'

const props = withDefaults(
    defineProps<{
        modelValue?: any
    }>(),
    {
        modelValue: {}
    }
)

const emit = defineEmits(['update:modelValue'])
const { modelValue: formData } = useVModels(props, emit)

const goodsCategoryList = ref([])

// 分类组件配置数据
const cateOpt = reactive({
    multiple: true,
    checkStrictly: true,
    label: 'name',
    value: 'id',
    children: 'children',
    emitPath: false
})

const getGoodsCategoryLists = async () => {
    try {
        const data = await goodsCategoryLists()
        goodsCategoryList.value = data
    } catch (error) {
        console.log(error)
    }
}

getGoodsCategoryLists()
</script>
