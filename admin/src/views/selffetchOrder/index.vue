<template>
    <div>
        <el-card class="!border-none" shadow="never">

            <!-- Header -->
            <el-form ref="formRef" class="mb-[-16px] mt-[16px]" :model="queryParams" :inline="true">
                <el-form-item label="订单搜索">
                    <el-input
                        v-model="queryParams.searchKeyword"
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
                                <el-option label="订单编号" value="1" />
                                <el-option label="商品编号" value="2" />
                                <el-option label="商品名称" value="3" />
                            </el-select>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="买家信息">
                    <el-input
                        v-model="queryParams.searchUserKeyword"
                        placeholder="请输入"
                        class="w-[320px]"
                        @keyup.enter="resetPage"
                    >
                        <template #prepend>
                            <el-select
                                v-model="queryParams.userSearchType"
                                placeholder="选择"
                                style="width: 115px"
                            >
                                <el-option label="用户编号" value="1" />
                                <el-option label="用户昵称" value="2" />
                                <el-option label="提货人姓名" value="3" />
                                <el-option label="提货人手机" value="4" />
                            </el-select>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item label="核销码">
                    <el-input
                        v-model="queryParams.pickupCode"
                        placeholder="请输入核销码"
                        class="w-[320px]"
                        @keyup.enter="resetPage"
                    >
                    </el-input>
                </el-form-item>
                <el-form-item label="自提门店">
                    <selffetchPickerOne @change="changeSelffetchShop"></selffetchPickerOne>
                </el-form-item>
                <el-form-item label="订单状态">
                    <el-select class="w-[280px]" v-model="queryParams.orderStatus" clearable>
                        <el-option label="待付款" value="1" />
                        <!-- <el-option label="待发货" value="2" /> -->
                        <el-option label="待收货" value="3" />
                        <el-option label="已完成" value="4" />
                        <el-option label="已关闭" value="5" />
                    </el-select>
                </el-form-item>
                <!--<el-form-item label="订单来源">
                    <el-select class="w-[280px]" v-model="queryParams.orderSource">
                        <el-option
                            v-for="(item, key) in ClientMap"
                            :key="key"
                            :label="item"
                            :value="key"
                        />
                    </el-select>
                </el-form-item>
                 <el-form-item label="支付方式">
                    <el-select class="w-[280px]" v-model="queryParams.payWay">
                        <el-option label="全部" value=" " />
                        <el-option label="未知" value="0" />
                        <el-option label="余额" value="1" />
                        <el-option label="微信" value="2" />
                        <el-option label="支付宝" value="3" />
                    </el-select>
                </el-form-item>
                <el-form-item label="支付状态">
                    <el-select class="w-[280px]" v-model="queryParams.payStatus">
                        <el-option label="全部" value=" " />
                        <el-option label="未支付" value="0" />
                        <el-option label="已支付" value="1" />
                    </el-select>
                </el-form-item> -->
                <el-form-item label="时间类型">
                    <el-select
                        v-model="queryParams.searchTimeType"
                        placeholder="全部"
                        style="width: 115px"
                    >
                        <el-option label="下单时间" value="create" />
                        <el-option label="支付时间" value="pay" />
                    </el-select>
                    <daterange-picker
                        value-format="x"
                        :second="true"
                        v-model:startTime="queryParams.startTime"
                        v-model:endTime="queryParams.endTime"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="resetPage">查询</el-button>
                    <el-button @click="resetParams">重置</el-button>
                    <el-button @click="exportExcel">导出</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- Main -->
        <el-card class="!border-none mt-4" :body-style="{ 'padding-top': '10px' }" shadow="never">
            <!-- 选项卡 -->
            <el-tabs v-model="queryParams.verificationStatus" class="demo-tabs" @tab-change="changeTabs">
                <template v-for="(tabsItem, tabsIndex) in tabsParams.TabsEnumMap" :key="tabsIndex">
                    <el-tab-pane
                        :label="`${tabsItem.label}(${pager.extend[tabsItem.type] || 0})`"
                        :name="tabsItem.name"
                    />
                </template>
            </el-tabs>

            <!-- 表格 -->
            <el-table
                size="large"
                v-loading="pager.loading"
                :data="pager.lists"
                @selection-change="selectDataChange"
            >
                <el-table-column type="selection" width="55" />
                <el-table-column prop="orderSn" label="订单编号" width="200" />
                <el-table-column prop="goods" label="商品信息" min-width="220">
                    <template #default="{ row }">
                        <router-link
                            v-for="(item, index) in row.orderGoodsList"
                            :key="item"
                            :to="`/goods/lists`"
                            v-show="index <= 2"
                        >
                            <div class="flex mb-[5px]">
                                <image-contain
                                    class="flex-none"
                                    :src="item.goodsImage"
                                    :width="70"
                                    :height="70"
                                    :preview-src-list="[item.goodsImage]"
                                    :preview-teleported="true"
                                    fit="contain"
                                />

                                <div class="ml-[6px] text-xs overflow-hidden">
                                    <el-tooltip
                                        effect="dark"
                                        :content="item.goodsName"
                                        placement="top"
                                    >
                                        <div class="text-base truncate goods-name">
                                            {{ item.goodsName }}
                                        </div>
                                    </el-tooltip>
                                    <div class="text-tx-placeholder">
                                        {{ item.goodsSkuValue }}
                                    </div>
                                    <div class="black">
                                        ¥{{ item.goodsPrice }} &nbsp;&nbsp;x {{ item.goodsNum }}
                                    </div>
                                </div>
                            </div>
                        </router-link>
                        <el-button
                            v-if="row?.orderGoodsList?.length > 2"
                            type="primary"
                            :link="true"
                            @click="handleMore('all_goods', row)"
                        >
                            <span>查看更多</span>
                            <el-icon>
                                <ArrowRight />
                            </el-icon>
                        </el-button>
                    </template>
                </el-table-column>
                <el-table-column prop="payMoney" label="实付金额" min-width="100" />
                <!-- <el-table-column prop="nickname" label="买家昵称" width="130">
                    <template #default="{ row }">
                        <el-popover placement="top" width="300px" trigger="hover">
                            <div class="flex items-center">
                                <span class="mr-4">头像: </span>
                                <el-avatar :size="50" :src="row?.avatar" />
                            </div>
                            <div class="mt-[20px]">
                                <span class="mr-4"> 昵称: </span>
                                <span>{{ row.nickname }}</span>
                            </div>
                            <div class="mt-[20px]">
                                <span class="mr-4">编号: </span>
                                <span>{{ row.userSn }}</span>
                            </div>
                            <template #reference>
                                {{ row.nickname }}
                            </template>
                        </el-popover>
                    </template>
                </el-table-column> -->
                
                <el-table-column prop="date" label="提货人" min-width="120">
                    <template #default="{ row }">
                        <el-popover placement="top" width="300px" trigger="hover">
                            <div>姓名: {{ row.addressContact }}</div>
                            <div class="mt-[20px]">联系号码: {{ row.addressMobile }}</div>
                            <div class="mt-[20px]">
                                自提门店: {{ row.selffetchShopName }}
                            </div>
                            <template #reference>
                                <el-tag>{{ row.addressContact }}</el-tag>
                            </template>
                        </el-popover>
                    </template>
                </el-table-column>
                <!-- <el-table-column label="支付状态" min-width="100">
                    <template #default="{ row }">
                        <div v-if="row.payIs">已支付</div>
                        <div v-else class="text-error">未支付</div>
                    </template>
                </el-table-column> -->
                <el-table-column prop="orderStatusMsg" label="订单状态" min-width="100" />
                <el-table-column prop="verificationStatusStr" label="核销状态" min-width="100" />
                <el-table-column prop="verificationTimeStr" label="核销时间" min-width="100" />
                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button v-perms="['order:selffetch:detail']" type="primary" :link="true">
                            <router-link
                                :to="{
                                    path: getRoutePath('order:selffetch:detail'),
                                    query: {
                                        id: row.id
                                    }
                                }"
                            >
                                订单详情
                            </router-link>
                        </el-button>
                        <el-button @click="verifytion(row)" type="primary" :link="true" v-if="row.logisticsBtn"  v-perms="['order:selffetch:verify']">
                            核销
                        </el-button>
                        <el-button
                            v-if="row.orderStatus == 4"
                            @click="verifytionLog(row)"
                            :link="true"
                            v-perms="['order:selffetch:detail']"
                        >
                            核销记录
                        </el-button>

                        <!-- <el-dropdown @command="handleMore($event, row)">
                            <el-button type="primary" link class="mt-[2px] mx-2">
                                <span>更多</span>
                                <el-icon class="el-icon--right">
                                    <arrow-down />
                                </el-icon>
                            </el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item
                                        v-perms="['order:order:cancel']"
                                        command="cancel"
                                        v-if="row.cancelBtn && row.orderStatus <= 3"
                                    >
                                        取消订单
                                    </el-dropdown-item> -->
                                    <!-- <el-dropdown-item
                                        v-perms="['order:order:takeDelivery']"
                                        command="confirm"
                                        v-if="row.confirmBtn"
                                    >
                                        确认订单
                                    </el-dropdown-item> -->
                                    <!-- <el-dropdown-item
                                        v-perms="['order:order:sendDelivery']"
                                        command="send"
                                        v-if="row.deliverBtn"
                                    >
                                        去发货
                                    </el-dropdown-item> 
                                    <el-dropdown-item
                                        v-perms="['order:order:logistics']"
                                        command="logistcs_info"
                                        v-if="row.logisticsBtn"
                                    >
                                        查看物流
                                    </el-dropdown-item> 
                                    <el-dropdown-item
                                        v-perms="['order:order:remarks']"
                                        command="remark"
                                    >
                                        订单备注
                                    </el-dropdown-item>-->
                                <!-- </el-dropdown-menu>
                            </template>
                        </el-dropdown> -->
                    </template>
                </el-table-column>
            </el-table>
            <div class="flex justify-end mt-4">
                <pagination v-model="pager" @change="getLists" />
            </div>
        </el-card>

        <!-- Component -->
        <!-- 订单核销 -->
        <verification
            v-if="verificationPopup"
            ref="verificationRef"
            @success="verificationSucc"
            @close="verificationPopup = false"
        ></verification>

        <!-- 订单备注 -->
        <remark
            v-if="showRemarkPopup"
            ref="remarkRef"
            @success="getLists"
            @close="showRemarkPopup = false"
        ></remark>

        <!-- 发货 ｜｜ 查看物流 -->
        <logistics
            v-if="showLogisticsPopup"
            ref="logisticsRef"
            @success="getLists"
            @close="showLogisticsPopup = false"
        ></logistics>

        <!-- 查看全部商品 -->
        <all-goods
            v-if="showAllGoodsPopup"
            ref="allGoodsRef"
            @close="showAllGoodsPopup = false"
        ></all-goods>
        <!-- 订单核销记录 -->
        <verification-log
            v-if="verificationLogPopup"
            ref="verificationLogRef"
            @close="verificationLogPopup = false"
            @success="verificationLogPopup = false"
        ></verification-log>
    </div>
</template>
<script lang="ts" setup name="selffetchOrderLists">
import { usePaging } from '@/hooks/usePaging'
import { getRoutePath } from '@/router'
import type { OrderQueryParamsType } from '@/api/selffetchorder/selffetchorder.d'
import { orderLists, orderCancel, orderTakeDelivery, orderExportExcel } from '@/api/selffetchorder/selffetchorder'
import { ClientMap } from '@/enums/appEnums'
import feedback from '@/utils/feedback'
import { httpBuildQuery, isEmpty } from "@/utils/util"

import Remark from './component/remark.vue'
import Logistics from './component/logistics/index.vue'
import AllGoods from './component/all-goods.vue'
import Verification from "./component/verification.vue"
import VerificationLog from "./component/verification-log.vue"
import configs from '@/config'

// Tab类型
enum TabsEnum {
    ALL_ORDER = 'allNum',
    PAY_COUNT = 'waitPayNum',
    DELIVER_COUNT = 'waitDeliverNum',
    RECEIVE_COUNT = 'takeDeliverNum',
    FINISH_COUNT = 'completedNum',
    CANCEL_COUNT = 'cancelNum',
    UNPICKUP = 'unPickupNum',
    PICKUPED = 'pickupNum'
}

const remarkRef = shallowRef()
const logisticsRef = shallowRef()
const allGoodsRef = shallowRef()
const verificationRef = shallowRef()
const verificationLogRef = shallowRef()

const showRemarkPopup = ref<boolean>(false)
const showLogisticsPopup = ref<boolean>(false)
const showAllGoodsPopup = ref<boolean>(false)
const verificationPopup = ref<boolean>(false)
const verificationLogPopup = ref<boolean>(false)

const tabsParams = reactive({
    TabsEnumMap: [
        {
            label: '全部',
            name: '',
            type: TabsEnum.ALL_ORDER
        },

        {
            label: '待核销',
            name: 0,
            type: TabsEnum.UNPICKUP
        },
        {
            label: '已核销',
            name: 1,
            type: TabsEnum.PICKUPED
        },
        // {
        //     label: '待支付',
        //     name: 1,
        //     type: TabsEnum.PAY_COUNT
        // },
        // {
        //     label: '待发货',
        //     name: 2,
        //     type: TabsEnum.DELIVER_COUNT
        // },
        // {
        //     label: '待收货',
        //     name: 3,
        //     type: TabsEnum.RECEIVE_COUNT
        // },
        // {
        //     label: '已完成',
        //     name: 4,
        //     type: TabsEnum.FINISH_COUNT
        // },
        // {
        //     label: '已取消',
        //     name: 5,
        //     type: TabsEnum.CANCEL_COUNT
        // }
    ]
})

const specParams = reactive<any>({
    selectData: []
})

const queryParams = reactive<OrderQueryParamsType>({
    //type: 0,
    searchKeyword: '',
    searchType: '1',
    searchUserKeyword: '',
    userSearchType: '1',
    orderSource: '',
    payStatus: '',
    payWay: '',
    searchTimeType: '',
    startTime: '',
    endTime: '',
    pickupCode: '',
    selffetchShopId: '',
    orderStatus: '',
    verificationStatus: ''
})

const { pager, getLists, resetPage, resetParams } = usePaging({
    fetchFun: orderLists,
    params: queryParams
})

const selectDataChange = (value: any[]) => {
    specParams.selectData = value.map((item) => item.ids)
}

const changeTabs = (name: string) => {
    queryParams.verificationStatus = name
    getLists()
}

const handleMore = async (event: string, row: any) => {
    switch (event) {
        case 'cancel':
            handleCancel(row.id)
            break
        case 'confirm':
            handleConfirm(row.id)
            break
        case 'remark':
            showRemarkPopup.value = true
            await nextTick()
            remarkRef.value.open(row.id, row.shop_remark)
            break
        case 'send':
            showLogisticsPopup.value = true
            await nextTick()
            logisticsRef.value.open('send', row.id)
            break
        case 'logistcs_info':
            showLogisticsPopup.value = true
            await nextTick()
            logisticsRef.value.open('logistcs_info', row.id)
            break
        case 'all_goods':
            showAllGoodsPopup.value = true
            await nextTick()
            allGoodsRef.value.open(row.orderGoodsList)
    }
}

const handleCancel = async (id: number) => {
    try {
        await feedback.confirm('确定要取消订单吗？')
        await orderCancel({ id })
        await getLists()
    } catch (error) {
        console.log('取消失败=>', error)
    }
}

const handleConfirm = async (id: number) => {
    try {
        await feedback.confirm('确认用户已收到货？')
        await orderTakeDelivery({ id })
        await getLists()
    } catch (error) {
        console.log('取消失败=>', error)
    }
}

const verifytion = async (row: any) => {
    verificationPopup.value = true
    await nextTick()
    verificationRef.value.open(row.id, row)
}

const verificationSucc = async function () {
    getLists()
    verificationPopup.value = false
}

const changeSelffetchShop = (e) => {
    queryParams.selffetchShopId = e.id
}

const verifytionLog = async(row) => {
    verificationLogPopup.value = true
    await nextTick()
    verificationLogRef.value.open(row.id, row)
}

const exportExcel = async() => {
    let domain = isEmpty(configs.baseUrl) ? window.location.protocol + '//' + window.location.host : configs.baseUrl
    window.location.href = domain + '/api/order/selffetch/exportExcel?' + httpBuildQuery(queryParams);
}




onActivated(() => {
    getLists()
})

getLists()
</script>
