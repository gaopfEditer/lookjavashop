<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view class="w-[full] relative top" :style="$theme.pageStyle">
        <view
            class="top absolute top-0 px-[30rpx] w-full"
            :style="{ backgroundImage: `url(${myData.applyImage})` }"
        >
            <view class="user flex py-[40rpx]">
                <image
                    class="w-[110rpx] h-[110rpx] rounded-full bg-white"
                    :src="userInfo.avatar"
                ></image>
                <view class="flex flex-col justify-between ml-[30rpx]">
                    <view class="">
                        <span class="text-[36rpx] text-white">{{ userInfo.nickname }}</span>
                    </view>
                    <view class="text-white flex" v-if="myData?.isDistribution">
                        <view>上级分销商：{{ myData?.firstLeader || '无' }}</view>
                        <view
                            @click="showTopPop"
                            class="ml-2"
                            style="border-bottom: 1px solid white"
                            v-if="!myData?.firstLeader"
                            >填写</view
                        >
                    </view>
                </view>
                <view
                    class="text-[22rpx] text-white absolute bottom-0 left-[20rpx] right-[20rpx] h-[100rpx] level text-[#C75629] rounded-t-lg px-[30rpx]"
                    v-if="myData?.isDistribution"
                >
                    <view class="flex items-center h-full justify-between">
                        <view class="flex items-center">
                            <image class="w-[50rpx] h-[50rpx]" :src="myData.levelIcon"></image>
                            <span class="text-[32rpx] ml-[15rpx]">
                                {{ myData.levelName }}
                            </span>
                        </view>
                        <span @click="handleLeval"
                            >查看 <u-icon name="arrow-right" size="22rpx"
                        /></span>
                    </view>
                </view>
            </view>
            <block v-if="myData?.isDistribution == 0">
                <apply
                    @submit-apply="submitApply"
                    v-if="!myData?.isDistributio && isApply"
                    :isShowProtocol="myData.isShowProtocol"
                ></apply>
                <result
                    @re-apply="isApply = true"
                    :data="detailData"
                    v-if="!myData?.isDistribution && !isApply"
                ></result>
            </block>
        </view>
        <!--输入邀请邀请码-->
        <u-popup v-model="PopShow" mode="center" border-radius="14" width="600rpx">
            <view class="p-[70rpx]">
                <view class="flex items-center">
                    <view>邀请码：</view>
                    <input
                        class="flex-1 p-[10rpx] rounded-lg"
                        v-model="inviteCode"
                        style="border: 1px solid #e5e5e5"
                    />
                </view>

                <button @click="confirmVisit" class="rounded-full bg-primary mt-[30rpx] text-white">
                    确定
                </button>
            </view>
        </u-popup>
    </view>
    <isDistribution :data="myData" v-if="myData?.isDistribution"></isDistribution>
</template>

<script lang="ts" setup>
import isDistribution from '@/packageA/pages/distribution/components/isDistribution.vue'
import apply from '@/packageA/pages/distribution/components/apply.vue'
import {
    applyDistribution,
    Mydistribution,
    applyDetial,
    bindDistributionCode,
    distributionIndex
} from '@/api/distribution'
import { onMounted, ref } from 'vue'
import result from '@/packageA/pages/distribution/components/result.vue'
import { isObjEmpty, isempty } from '@/utils/util'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()
const userStore = useUserStore()

const { userInfo } = storeToRefs(userStore)

//我的数据
const myData: any = ref({})
//详情数据
const detailData: any = ref()
//判断是否已经申请过
const isApply = ref<boolean>(true)
// const reApply = ref<boolean>(false)

//邀请码
const inviteCode = ref('')

//弹框 显示/隐藏
const PopShow = ref(false)

//提交申请
const submitApply = async (submitData: any) => {
    await applyDistribution(submitData)
    await getMyDistribution()
    isApply.value = false
}

//获取我的推广信息
const getMyDistribution = async () => {
    myData.value = await distributionIndex()
    // if (myData.value.user.is_distribution == 0) {
    detailData.value = await applyDetial()
    console.log(detailData.value)
    console.log(!isempty(detailData.value))

    isApply.value = isempty(detailData.value)
    // }
}

//弹出填写上级弹框
const showTopPop = () => {
    PopShow.value = true
    inviteCode.value = ''
}

//邀请码确定
const confirmVisit = async () => {
    uni.showLoading({
        title: '加载中'
    })
    try {
        await bindDistributionCode({ code: inviteCode.value })
        PopShow.value = false
        getMyDistribution()
    } catch (error) {
        uni.$u.toast(error)
        inviteCode.value = ''
    }
    uni.hideLoading()
}
const handleLeval = () => {
    router.navigateTo('/packageA/pages/member_center/member_center')
}
onMounted(() => {
    getMyDistribution()
})
</script>

<style lang="scss" scoped>
.top {
    height: 270rpx;
    background-position: top;
    background: url(@/static/images/user/my_topbg.png) no-repeat,
        linear-gradient(90deg, var(--theme-color) 0, var(--theme-dark-color) 100%);
    background-size: 100% auto;
}

.level {
    background: linear-gradient(
        134.09deg,
        #f9e3c2 0%,
        #fff1dd 8.68%,
        #f4cba8 35.74%,
        #ffc9ad 53.53%,
        #ffb683 100%
    );
}
</style>
