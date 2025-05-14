<!-- 会员中心 -->
<template>
    <view class="member-center" :style="$theme.pageStyle">
        <view class="member-center__header">
            <!-- 导航条 -->
            <u-sticky offset-top="0" h5-nav-height="0" bg-color="transparent">
                <u-navbar
                    :is-back="true"
                    title="等级中心"
                    :title-bold="true"
                    :is-fixed="false"
                    :border-bottom="false"
                    :background="{ background: 'transparent' }"
                    title-color="#fff"
                ></u-navbar>
            </u-sticky>

            <!-- 会员轮播卡片 -->
            <view class="swiper-container pt-[30rpx]">
                <swiper
                    class="swiper"
                    style="height: 320rpx"
                    previous-margin="60rpx"
                    next-margin="25rpx"
                    display-multiple-items="1"
                    :current="current"
                    @change="bindchange"
                >
                    <swiper-item
                        v-for="(item, index) in userLevel"
                        :key="index"
                        class="box-border pr-[35rpx]"
                    >
                        <view
                            class="level_card text-white"
                            :style="'background-image: url(' + item.image + ');'"
                        >
                            <view class="level_tag" v-if="item.isNowLevel"> 当前等级 </view>
                            <!-- 等级名称 -->
                            <view class="mt-[30rpx] text-[46rpx] font-medium">
                                {{ item.levelName }}
                            </view>
                            <view class="mt-[20rpx] text-[26rpx] font-medium">
                                自购佣金：{{ item.selfRatio }}%
                            </view>
                            <view class="mt-[20rpx] text-[26rpx] font-medium">
                                一级佣金：{{ item.firstRatio }}%
                            </view>
                            <view class="mt-[20rpx] text-[26rpx] font-medium">
                                二级佣金：{{ item.secondRatio }}%
                            </view>
                        </view>
                    </swiper-item>
                </swiper>
            </view>
        </view>

        <!-- 升级条件 -->
        <view class="member-center__content">
            <view class="title flex items-center">
                <text class="text-lg font-medium">分销任务</text>
                <text class="text-sm text-muted ml-[14rpx]">{{
                    userLevel[current].conditionMsg
                }}</text>
            </view>

            <block v-for="(item, index) in userLevel[current].condition" :key="index">
                <view class="condition_item">
                    <view class="flex items-center">
                        <image class="w-[84rpx] h-[84rpx]" :src="imglists[index]"></image>
                        <view class="ml-[16rpx] text-[#222222]">
                            {{ item?.conditionMsg }}
                            <view class="mt-[6rpx] text-muted text-xs">
                                {{ item?.progressMsg }}
                            </view>
                        </view>
                    </view>
                    <view v-if="item?.isFinish == 1" class="finish">已完成</view>
                    <navigator class="btn" hover-class="none" url="/pages/index/index" v-else>
                        去完成
                    </navigator>
                </view>
            </block>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { userLevelInfo } from '@/api/user'
import { computed, reactive, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

import condition1 from '@/packageA/static/vip/condition1.png'
import condition2 from '@/packageA/static/vip/condition2.png'
import condition3 from '@/packageA/static/vip/condition3.png'
import condition0 from '@/packageA/static/vip/condition0.png'
import { watch } from 'vue'

const userLevel = ref<any>([
    {
        conditon: []
    }
])
const imglists = [condition0, condition1, condition2, condition3]

const current = ref(0)
// 等级卡片切换时等级升级条件改变
const bindchange = (e: any) => {
    current.value = e.detail.current
}

// 获取会员等级信息
const getUserLevel = async () => {
    try {
        const result = await userLevelInfo()
        userLevel.value = result

        // 首次进入，根据用户等级 匹配对应 等级卡片块
        userLevel.value.forEach((i: any, index: number) => {
            if (i.isNowLevel == 1) {
                current.value = index
            }
        })
    } catch (error) {
        console.log('error', error)
    }
}
onLoad(() => {
    getUserLevel()
})
</script>

<style lang="scss">
.member-center {
    &__header {
        padding-bottom: 60rpx;
        background-image: url('../../static/vip/vip_grade_bg.png');
        background-size: 100% 100%;

        .swiper-container {
            .level_card {
                padding: 24rpx 30rpx;
                border-radius: 20rpx;
                height: 320rpx;
                position: relative;
                background-size: 100% 100%;

                .level_tag {
                    position: absolute;
                    top: 0;
                    right: 0;
                    line-height: 50rpx;
                    background-color: rgba(0, 0, 0, 0.3);
                    border-top-right-radius: 20rpx;
                    border-bottom-left-radius: 20rpx;
                    height: 50rpx;
                    padding: 0 28rpx;
                }
            }
        }
    }

    &__content {
        padding: 44rpx 20rpx 0 20rpx;

        .condition_item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-top: 24rpx;
            padding: 24rpx 30rpx;
            border-radius: 14rpx;
            background-color: #ffffff;

            .btn {
                width: 138rpx;
                height: 52rpx;
                text-align: center;
                line-height: 52rpx;
                border: 1px solid #ff2c3c;
                border-radius: 30px;
                color: #ff2c3c;
            }

            .finish {
                width: 138rpx;
                height: 52rpx;
                text-align: center;
                line-height: 52rpx;
                border: 1px solid #a4adb3;
                border-radius: 30px;
                color: #a4adb3;
            }
        }
    }
}
</style>
