<template>
    <div>
        <el-card class="!border-none" shadow="never">
            <el-page-header content="用户详情" @back="$router.back()" />
        </el-card>
        <el-card class="mt-4 !border-none" header="基本资料" shadow="never">
            <el-form ref="formRef" class="ls-form" :model="formData" label-width="120px">
                <div class="bg-page flex py-5 mb-10 items-center">
                    <div class="basis-40 flex flex-col justify-center items-center">
                        <div class="mb-2 text-tx-regular">用户头像</div>
                        <el-avatar :src="formData.avatar" :size="58" />
                    </div>
                    <div class="basis-40 flex flex-col justify-center items-center">
                        <div class="text-tx-regular">账户余额</div>
                        <div class="mt-2 flex items-center">
                            <span class="text-xl font-medium"> ¥{{ formData.totalMoney }} </span>
                            <!-- <el-button
                v-perms="['user:adjustWallet']"
                type="primary"
                link
                @click="handleAdjust(formData.totalMoney)"
              >
                调整
              </el-button> -->
                        </div>
                    </div>
                    <div class="basis-40 flex flex-col justify-center items-center">
                        <div class="text-tx-regular">可用余额</div>
                        <div class="mt-2 flex items-center">
                            <span class="text-xl font-medium"> ¥{{ formData.money }} </span>
                            <el-button
                                v-perms="['user:adjustEarnings']"
                                type="primary"
                                link
                                :disabled="formData.isClose"
                                @click="handleAdjust(formData.money)"
                            >
                                调整
                            </el-button>
                        </div>
                    </div>
                    <div class="basis-40 flex flex-col justify-center items-center">
                        <div class="text-tx-regular">可提现余额</div>
                        <div class="mt-2 flex items-center">
                            <span class="text-xl font-medium"> ¥{{ formData.earnings }} </span>
                            <el-button
                                v-perms="['user:adjustEarnings']"
                                type="primary"
                                link
                                :disabled="formData.isClose"
                                @click="handleEarnings(formData.earnings)"
                            >
                                调整
                            </el-button>
                        </div>
                    </div>
                    <div class="basis-40 flex flex-col justify-center items-center">
                        <div class="text-tx-regular">可用优惠券</div>
                        <div class="mt-2 flex items-center">
                            <span class="text-xl font-medium">
                                {{ formData.unUseCouponNum }}
                            </span>
                            <el-button
                                v-perms="['user:sendCoupon']"
                                type="primary"
                                link
                                :disabled="formData.isClose"
                                @click="handleCoupon"
                            >
                                发放优惠券
                            </el-button>
                        </div>
                    </div>
                </div>
                <el-form-item label="用户编号："> {{ formData.sn }} </el-form-item>
                <el-form-item label="用户昵称：">
                    {{ formData.nickname }}
                </el-form-item>
                <el-form-item label="账号：">
                    {{ formData.username }}
                    <popover-input
                        class="ml-[10px]"
                        :limit="32"
                        @confirm="handleEdit($event, 'username')"
                    >
                        <el-button
                            :disabled="formData.isClose"
                            type="primary"
                            link
                            v-perms="['user:edit']"
                        >
                            <icon name="el-icon-EditPen" />
                        </el-button>
                    </popover-input>
                </el-form-item>
                <el-form-item label="真实姓名：">
                    {{ formData.realName || '-' }}
                    <popover-input
                        class="ml-[10px]"
                        :limit="32"
                        @confirm="handleEdit($event, 'realName')"
                    >
                        <el-button
                            :disabled="formData.isClose"
                            type="primary"
                            link
                            v-perms="['user:edit']"
                        >
                            <icon name="el-icon-EditPen" />
                        </el-button>
                    </popover-input>
                </el-form-item>
                <el-form-item label="性别：">
                    {{ formData.sex }}
                    <popover-input
                        class="ml-[10px]"
                        type="select"
                        :options="[
                            {
                                label: '未知',
                                value: 0
                            },
                            {
                                label: '男',
                                value: 1
                            },
                            {
                                label: '女',
                                value: 2
                            }
                        ]"
                        @confirm="handleEdit($event, 'sex')"
                    >
                        <el-button
                            :disabled="formData.isClose"
                            type="primary"
                            link
                            v-perms="['user:edit']"
                        >
                            <icon name="el-icon-EditPen" />
                        </el-button>
                    </popover-input>
                </el-form-item>
                <el-form-item label="联系电话：">
                    {{ formData.mobile || '-' }}
                    <popover-input
                        class="ml-[10px]"
                        type="number"
                        @confirm="handleEdit($event, 'mobile')"
                    >
                        <el-button
                            :disabled="formData.isClose"
                            type="primary"
                            link
                            v-perms="['user:edit']"
                        >
                            <icon name="el-icon-EditPen" />
                        </el-button>
                    </popover-input>
                </el-form-item>
                <el-form-item label="邀请人">
                    {{ formData.inviterName || '-' }}
                </el-form-item>
                <el-form-item label="我邀请的人数">
                    {{ formData.inviteNum }}
                    <router-link
                        :to="{
                            path: getRoutePath('user/invite'),
                            query: {
                                id: route.query.id,
                                num: formData.inviteNum,
                                name: formData.nickname
                            }
                        }"
                    >
                        <el-button :disabled="formData.isClose" link type="primary"
                            >邀请列表</el-button
                        >
                    </router-link>
                </el-form-item>
                <el-form-item label="上级分销商" @click="adjustSuperior">
                    {{ formData.firstLeaderName }}

                    <el-button
                        :disabled="formData.isClose"
                        type="primary"
                        link
                        v-perms="['user:edit']"
                    >
                        <icon name="el-icon-EditPen" />
                    </el-button>
                </el-form-item>
                <el-form-item label="注册来源："> {{ formData.channel }} </el-form-item>
                <el-form-item label="注册时间：">
                    {{ formData.createTime }}
                </el-form-item>
                <el-form-item label="最近登录时间：">
                    {{ formData.lastLoginTime }}
                </el-form-item>
            </el-form>
        </el-card>

        <!--    余额调整    -->
        <account-adjust
            v-model:show="adjustState.show"
            :value="adjustState.value"
            @confirm="handleConfirmAdjust"
        />
        <!--    优惠券发放    -->
        <send-coupon
            v-model:show="couponState.show"
            :value="couponState.value"
            @confirm="getDetails"
        ></send-coupon>
        <SuperiorAdjustPop ref="superiorAdjustPopRef" @close="getDetails"></SuperiorAdjustPop>
        <EarningAdjust
            ref="EarningAdjustRef"
            v-model:show="earningState.show"
            :value="earningState.value"
            @confirm="handleConfirmEarning"
        ></EarningAdjust>
    </div>
</template>

<script lang="ts" setup name="consumerDetail">
import SuperiorAdjustPop from '@/views/app/distribute/distributor/components/superiorAdjustPop.vue'
import EarningAdjust from '../components/earning-adjust.vue'
import type { FormInstance } from 'element-plus'
import { getUserDetail, userEdit, adjustMoney, adjustEarning } from '@/api/consumer'
import feedback from '@/utils/feedback'
import { isEmpty } from '@/utils/util'
import { getRoutePath } from '@/router'

import AccountAdjust from '../components/account-adjust.vue'
import SendCoupon from '@/views/consumer/components/send-coupon.vue'
const superiorAdjustPopRef = shallowRef()
const EarningAdjustRef = shallowRef()
const route = useRoute()
const formData = reactive({
    id: '',
    avatar: '',
    channel: '',
    createTime: '',
    lastLoginIp: '',
    lastLoginTime: '',
    money: 0,
    mobile: '',
    nickname: '',
    realName: 0,
    sex: 0,
    sn: '',
    unUseCouponNum: 0,
    username: '',
    firstLeaderName: '',
    inviterName: '',
    inviteNum: '',
    totalMoney: '',
    earnings: '',
    isClose: 0
})

const formRef = shallowRef<FormInstance>()
const earningState = reactive({
    show: false,
    value: ''
})
const adjustState = reactive({
    show: false,
    value: ''
})
const couponState = reactive({
    show: false,
    value: ''
})
const getDetails = async () => {
    const data = await getUserDetail({
        id: route.query.id
    })
    Object.keys(formData).forEach((key) => {
        //@ts-ignore
        formData[key] = data[key]
    })
}

const handleEdit = async (value: string, field: string) => {
    if (isEmpty(value)) return
    await userEdit({
        id: route.query.id,
        field,
        value
    })
    feedback.msgSuccess('编辑成功')
    await getDetails()
}
const handleAdjust = (value: any) => {
    adjustState.show = true
    adjustState.value = value
}
const handleCoupon = () => {
    couponState.show = true
    couponState.value = route.query.id as any
}
const handleConfirmAdjust = async (value: any) => {
    await adjustMoney({ userId: route.query.id, ...value })
    feedback.msgSuccess('调整成功')
    adjustState.show = false
    await getDetails()
}
// //调整上级分销商
const adjustSuperior = () => {
    superiorAdjustPopRef.value.open(formData)
}
//调整佣金
const handleEarnings = (value: any) => {
    earningState.show = true
    earningState.value = value
}
const handleConfirmEarning = async (value: any) => {
    await adjustEarning({ userId: route.query.id, ...value })
    feedback.msgSuccess('调整成功')
    earningState.show = false
    await getDetails()
}
getDetails()
</script>
