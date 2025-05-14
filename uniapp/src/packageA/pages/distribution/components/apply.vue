<template>
    <view>
        <view class="bg-white py-[40rpx] px-[35rpx] w-full rounded-lg">
            <view class="text-center text-base font-medium">申请成为分销会员</view>
            <view class="mt-[40rpx]">
                <view class="flex py-[24rpx] px-[20rpx] rounded" style="border: 1px solid #e5e5e5">
                    <view class="text-info">真实姓名：</view>
                    <input
                        v-model="formData.realName"
                        placeholder-class="text-[#BBBBBB] text-[28rpx]"
                        placeholder="请输入您的真实姓名"
                    />
                </view>
                <view
                    class="flex py-[24rpx] px-[20rpx] rounded mt-[30rpx]"
                    style="border: 1px solid #e5e5e5"
                >
                    <view class="text-info">手机号码：</view>
                    <input
                        v-model="formData.mobile"
                        placeholder-class="text-[#BBBBBB] text-[28rpx]"
                        placeholder="请输入您的手机号码"
                    />
                </view>
                <view
                    class="flex py-[24rpx] px-[20rpx] rounded mt-[30rpx]"
                    style="border: 1px solid #e5e5e5"
                >
                    <view class="text-info">现住省份：</view>
                    <view class="flex-1" @click="showRegionPop">
                        <view class="flex justify-between" v-if="!formData.formatRegion">
                            <view class="text-[#BBBBBB]">请输入省市区</view>
                            <u-icon name="arrow-right" color="#BBBBBB" size="28"></u-icon>
                        </view>
                        <view v-else>{{ formData.formatRegion }}</view>
                    </view>
                </view>
                <view
                    class="flex py-[24rpx] px-[20rpx] rounded mt-[30rpx]"
                    style="border: 1px solid #e5e5e5"
                >
                    <view class="text-info flex-none">申请原因：</view>
                    <input
                        v-model="formData.reason"
                        placeholder-class="text-[#BBBBBB] text-[28rpx]"
                        auto-height
                        placeholder="请输入内容"
                    />
                </view>
            </view>
        </view>
        <view class="mt-[20rpx]">
            <button class="bg-primary rounded-full text-white text-lg" @click="submit">确定</button>
            <view class="mt-[20rpx] text-xs text-center" v-if="isShowProtocol">
                <u-checkbox v-model="isCheckAgreement" shape="circle">
                    <view class="flex">
                        本人已阅读并同意
                        <view @click.stop>
                            <router-navigate
                                class="text-primary"
                                hover-class="none"
                                to="/packageA/pages/agreement/agreement?type=distribution"
                            >
                                <span class="text-primary"> 《分销协议》 </span>
                            </router-navigate>
                        </view>
                    </view>
                </u-checkbox>
            </view>
            <div class="mt-[20rpx] text-[#999999] text-xs text-center">
                提交成功，我们将会在1-2个工作日内给您回复
            </div>
        </view>
        <u-picker v-model="regionPickShow" mode="region" @confirm="getRegion"></u-picker>
    </view>
</template>

<script setup lang="ts">
import { number } from 'mathjs'
import { ref } from 'vue'
const emit = defineEmits(['submitApply'])
const props = defineProps({
    isShowProtocol: {
        type: Number
    }
})
//地区弹框显示/隐藏
const regionPickShow = ref(false)

const formData = ref({
    realName: '', //真实名称
    mobile: '', //手机号码
    province: '',
    city: '',
    district: '',
    reason: '', //申请原因
    formatRegion: ''
})

//打开选择地区弹框
const showRegionPop = () => {
    regionPickShow.value = true
}
//获取地区
const getRegion = (region: any) => {
    console.log(region)
    formData.value.province = region.province.code
    formData.value.city = region.city.code
    formData.value.district = region.area.code
    formData.value.formatRegion = `${region.province.name}/${region.city.name}/${region.area.name}`
}
//提交
const submit = () => {
    console.log(formData.value)
    // emit('submitApply', formData.value)
    emit('submitApply', { ...formData.value, agree: isCheckAgreement.value ? 1 : 0 })
}
const isCheckAgreement = ref<boolean>(false)
</script>

<style scoped lang="scss"></style>
