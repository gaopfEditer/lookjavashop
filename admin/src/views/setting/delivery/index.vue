<template>
    <div class="delivery-lists">
        <div v-for="(item, index) in delivery.lists" :key="index" class="delivery-lists-div">
            <el-card class="!border-none mt-4" shadow="never">
                <el-row class="title">
                    <el-col :span="24" v-if="index == 0">
                        <popover-input
                                class="flex cursor-pointer"
                                :value="item.expressName"
                                @confirm="deliveryName($event, index)"
                            >
                            <strong>{{ item.expressName }}</strong>
                                <el-button type="primary" link>
                                    <el-icon>
                                        <EditPen />
                                    </el-icon>
                                </el-button>
                            </popover-input>
                            <el-divider class="divider" />
                    </el-col>
                    
           
                    <el-col :span="24" v-if="index == 1">
                        <popover-input
                                class="flex cursor-pointer"
                                :value="item.pickupName"
                                @confirm="deliveryName($event, index)"
                            >
                            <strong>{{ item.pickupName }}</strong>
                                <el-button type="primary" link>
                                    <el-icon>
                                        <EditPen />
                                    </el-icon>
                                </el-button>
                            </popover-input>
                        <el-divider class="divider" />
                    </el-col>
                </el-row>
            <el-row>
                <el-col :span="18">
                    {{item.tips}}
                </el-col>
                <el-col :span="6">
                    <div v-if="index==0">
                        <el-button type="primary" :link="true">
                            <router-link
                                :to="{
                                    path: getRoutePath(
                                        'deliver:tpl:list'
                                    )
                                }"
                            >
                                运费模版
                            </router-link>
                        </el-button>
                        <el-button type="primary" :link="true">
                            <router-link
                                :to="{
                                    path: getRoutePath(
                                        'deliver:express:list'
                                    )
                                }"
                            >
                                快递公司
                            </router-link>
                        </el-button>
                        <el-button
                            type="primary"
                            :link="true"
                            @click="handleAdd"
                        >
                            物流接口
                        </el-button>
                    </div>
                    <div v-if="index==1">
                        <el-button type="primary" :link="true">
                            <router-link
                                :to="{
                                    path: getRoutePath(
                                        'selffetch:shop:list'
                                    )
                                }"
                            >
                                查看自提点
                            </router-link>
                        </el-button>
                    </div>
                </el-col>
            </el-row>
            
            </el-card>
        </div>

        <edit-popup v-if="showEdit" ref="editRef" @close="showEdit = false" />
    </div>
</template>
<script lang="ts" setup name="delivery">
import { getDelivery, deliverySet, deliverySetPickup } from '@/api/setting/delivery'
import type { DeliveryFormType } from '@/api/setting/delivery'
import { getRoutePath } from '@/router'

import EditPopup from './edit.vue'

const editRef = shallowRef<InstanceType<typeof EditPopup>>()
const showEdit = ref<boolean>(false)

const delivery = reactive({
    lists: [{ expressIs: 1, expressAlias: '配送方式', expressName: '配送方式' }, { pickupAlias: "到店自提", pickupName: "到店自提", pickupIs: 1}] as DeliveryFormType[]
})

const getData = async () => {
    try {
        const data = await getDelivery()
        delivery.lists[0] = {
            expressName: data.expressName,
            expressIs: data.expressIs,
            expressAlias: data.expressAlias,
            tips: "启用快递发货后，买家下单可以选择快递发货，由卖家安排快递送货上门"
        }
        delivery.lists[1] = {
            pickupName: data.pickupName,
            pickupIs: data.pickupIs,
            pickupAlias: data.pickupAlias,
            tips: "启用到店自提后，买家下单可以选择就近门店自提点，卖家需要确保只订购的自提点商品库存充足"
        }
        //console.log(delivery.lists)
    } catch (error) {
        console.log('获取配送方式失败', error)
    }
}

const deliveryName = (value: string, index) => {
    if (index == 0) {
        delivery.lists[0].expressName = value
        handleSetKey()
    }
    if (index == 1) {
        delivery.lists[1].pickupName = value
        handleSetPickupKey()
    }
}

const handleSetKey = async () => {
    try {
        await deliverySet({ ...delivery.lists[0] })
        await getData()
    } catch (error) {
        await getData()
    }
}

const handleSetPickupKey = async () => {
    try {
        await deliverySetPickup({ ...delivery.lists[1] })
        await getData()
    } catch (error) {
        await getData()
    }
}

const handleAdd = async () => {
    showEdit.value = true
    await nextTick()
    editRef.value?.open()
}

getData()
</script>

<style scoped>
.delivery-lists {
    margin-top: 1rem;
}
.delivery-lists-div {
    margin-bottom: 2rem;
}
.title {
    margin-bottom: 1rem;
}
.divider {
    margin: 1rem 0;
}
</style>