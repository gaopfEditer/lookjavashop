<template>
    <div>
        <el-card class="!border-none" shadow="never">
            <el-alert
                type="warning"
                title="温馨提示：用户评价的列表，管理员可以回复用户的评价，允许修改已回复过的内容；设置该条评论是否要在前端隐藏或显示。"
                :closable="false"
                show-icon
            />

            <el-form
                ref="formRef"
                class="mb-[-16px] mt-[16px]"
                :model="queryParams"
                :inline="true"
            >
                <el-form-item label="评价信息">
                    <el-input
                        v-model="queryParams.keyword"
                        placeholder="请输入"
                        class="w-[320px]"
                        @keyup.enter="resetPage"
                    >
                        <template #prepend>
                            <el-select
                                v-model="queryParams.searchType"
                                placeholder="选择"
                                style="width: 100px"
                            >
                                <el-option label="商品名称" value="goodsName" />
                                <el-option label="用户编号" value="userSn" />
                                <el-option label="用户昵称" value="nickname" />
                            </el-select>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="评价等级">
                    <el-select class="w-[280px]" v-model="queryParams.level">
                        <el-option label="全部" :value="0" />
                        <el-option label="好评" :value="1" />
                        <el-option label="中评" :value="2" />
                        <el-option label="差评" :value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="评价状态">
                    <el-select class="w-[280px]" v-model="queryParams.status">
                        <el-option label="全部" :value="0" />
                        <el-option label="显示" :value="1" />
                        <el-option label="隐藏" :value="2" />
                    </el-select>
                </el-form-item>
                <el-form-item label="评价时间">
                    <daterange-picker
                        value-format="x"
                        :second="true"
                        v-model:startTime="queryParams.startTime"
                        v-model:endTime="queryParams.endTime"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage"
                        >查询</el-button
                    >
                    <el-button @click="resetParams">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card
            class="!border-none mt-4"
            :body-style="{ 'padding-top': '10px' }"
            shadow="never"
        >
            <!-- 选项卡 -->
            <el-tabs
                v-model="queryParams.type"
                class="demo-tabs"
                @tab-change="changeTabs"
            >
                <template
                    v-for="(tabsItem, tabsIndex) in tabsParams.TabsEnumMap"
                    :key="tabsIndex"
                >
                    <el-tab-pane
                        :label="`${tabsItem.label}(${pager.extend[tabsItem.type] || 0 })`"
                        :name="tabsItem.name"
                    />
                </template>
            </el-tabs>

            <!-- 表格 -->
            <el-table
                size="large"
                v-loading="pager.loading"
                :data="pager.lists"
            >
                <el-table-column label="评价商品" min-width="180">
                    <template #default="{ row }">
                        <div class="flex">
                            <el-image
                                fit="cover"
                                :src="row.goodsImage"
                                class="flex-none w-[58px] h-[58px]"
                            />
                            <div class="ml-4 overflow-hidden">
                                <el-tooltip
                                    effect="dark"
                                    placement="top"
                                    :content="row.goodsName"
                                >
                                    <div class="text-base line-2">
                                        {{ row.goodsName }}
                                    </div>
                                </el-tooltip>
                                <div class="form-tips">{{ row?.goodsSkuValue }}</div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="买家信息" min-width="130">
                    <template #default="{ row }">
                        <div>{{ row.nickname }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="评价等级" width="135">
                    <template #default="{ row }">
                        <div>{{ row.scoreMsg }} </div>
                        <el-rate v-model="row.goodsScore" disabled size="small" />
                    </template>
                </el-table-column>
                <el-table-column label="评价内容" min-width="240">
                    <template #default="{ row }">
                        <div class="comment-content">
                            {{ row.content || '-' }}
                        </div>
                        <div class="mt-2" v-if="row.images[0]">
                            <div
                                v-for="(item, index) in row.images"
                                :key="index"
                                class="inline mr-2"
                            >
                                <el-image
                                    v-show="item"
                                    style="width: 60px; height: 60px"
                                    :src="item"
                                    :preview-src-list="[item]"
                                    :hide-on-click-modal="true"
                                    :preview-teleported="true"
                                    :fit="'cover'"
                                ></el-image>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="回复内容" min-width="120">
                    <template #default="{ row }">
                        <div>{{ row.replyContent || '-' }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="状态" min-width="100">
                    <template #default="{ row }">
                        <el-switch
                            v-perms="['goods:comment:change']"
                            v-model="row.isShow"
                            :active-value="1"
                            :inactive-value="0"
                            @change="changeStatus($event, row.id)"
                        />
                    </template>
                </el-table-column>
                <el-table-column label="回复状态" min-width="90">
                    <template #default="{ row }">
                        <el-tag v-if="row?.isReply == 1" type="success" >已回复</el-tag>
                        <el-tag type="error" v-else>待回复</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="评价时间" prop="createTime" min-width="180" />
                <el-table-column label="操作" width="220" fixed="right">
                    <template #default="{ row }">
                        <el-button
                            v-perms="['goods:comment:reply']"
                            type="primary"
                            link
                            @click="handleEdit(row)"
                        >
                            {{ row.isReply ? '修改回复' : '回复' }}
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>

        <edit-popup
            v-if="showEdit"
            ref="editRef"
            @success="getLists"
            @close="showEdit = false"
        />
    </div>
</template>
<script lang="ts" setup name="CommentLists">
import { usePaging } from '@/hooks/usePaging'
import { commentLists, commentChange } from '@/api/goods/comment'
import type { CommentListsQueryParamsType } from '@/api/goods/comment.d'

import EditPopup from './edit.vue'
const editRef = shallowRef<InstanceType<typeof EditPopup>>()
const showEdit = ref(false)

// Tab类型
enum TabsEnum {
    ALL = 'all',
    WAIT_REPLY = 'waitReply',
    REPLIED = 'replied'
}

const tabsParams = reactive({
    TabsEnumMap: [
        {
            label: '全部评价',
            name: 0,
            type: TabsEnum.ALL
        },
        {
            label: '待回复评价',
            name: 1,
            type: TabsEnum.WAIT_REPLY
        },
        {
            label: '已回复',
            name: 2,
            type: TabsEnum.REPLIED
        }
    ]
})

const queryParams = reactive<CommentListsQueryParamsType>({
    type: 0,
    keyword: '',
    searchType: 'goodsName',
    level: 0,
    status: 0,
    startTime: '',
    endTime: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: commentLists,
    params: queryParams
})

const changeTabs = (name: number) => {
    queryParams.type = name
    getLists()
}

const changeStatus = async (show: boolean, id: number) => {
    try {
        await commentChange({ id })
        getLists()
    } catch (error) {
        getLists()
        console.log(error)
    }
}

const handleEdit = async (data: any) => {
    showEdit.value = true
    await nextTick()
    editRef.value?.open(data.id, data.replyContent)
}

getLists()
</script>
