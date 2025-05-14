<template>
    <div class="storage">
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示：1.使用对象存储，需要将域名下目录的资源文件保留原来目录路径传输到对象存储空间。2.请勿随意切换存储方式，可能导致图片无法查看。3.需要在对象存储后台设置域名跨域，否则图片生成场景无法使用，例海报合成等。4.微信小程序官方后台的request合法域名和downloadFile合法域名设置为对象存储图片域名。"
                :closable="false"
                show-icon
            ></el-alert>
        </el-card>
        <el-card class="!border-none mt-4" shadow="never" v-loading="state.loading">
            <el-table size="large" :data="state.lists">
                <el-table-column label="储存方式" prop="name" min-width="120" />
                <el-table-column label="储存位置" prop="describe" min-width="160" />
                <el-table-column label="状态" min-width="80">
                    <template #default="{ row }">
                        <el-tag v-if="row.status == 1">开启</el-tag>
                        <el-tag type="danger" v-else>关闭</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" min-width="80" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['setting:storage:edit']"
                            type="primary"
                            link
                            @click="handleSet(row.alias)"
                        >
                            设置
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
        <edit-popup ref="editRef" @success="getLists" />
    </div>
</template>
<script lang="ts" setup name="storage">
import { storageLists } from '@/api/setting/storage'
import EditPopup from './edit.vue'
const editRef = shallowRef<InstanceType<typeof EditPopup>>()

// 列表数据
const state = reactive({
    loading: false,
    lists: []
})

// 获取存储引擎列表数据
const getLists = async () => {
    try {
        state.loading = true
        state.lists = await storageLists()
        state.loading = false
    } catch (error) {
        state.loading = false
    }
}

const handleSet = (alias: string) => {
    editRef.value?.open(alias)
}

getLists()
</script>
