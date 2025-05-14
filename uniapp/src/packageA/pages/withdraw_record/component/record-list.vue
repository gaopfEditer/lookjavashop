<template>
    <view class="withdrawal-record">
        <view
            v-for="(item, index) in withdrawApplyLists"
            :key="index"
            class="withdrawal-record-item"
            @click="toDetail(item.id)"
        >
            <view class="flex">
                <view class="flex-1 withdrawal-record-item-text">提现至{{ item.typeMsg }}</view>
                <view class="withdrawal-record-item-amount">+{{ item.money }}</view>
            </view>

            <view class="flex justify-between">
                <view class="withdrawal-record-item-time"> {{ item.createTime }} </view>

                <template v-if="item.status == 1">
                    <view class="text-xs" style="color: #f95f2f">{{ item.statusMsg }}</view>
                </template>

                <template v-if="item.status == 2">
                    <view class="text-xs" style="color: #f95f2f">{{ item.statusMsg }}</view>
                </template>

                <template v-if="item.status == 3">
                    <view class="text-xs" style="color: #0cc267">{{ item.statusMsg }}</view>
                </template>

                <template v-if="item.status == 4">
                    <view class="text-xs" style="color: #ff2c3c">{{ item.statusMsg }}</view>
                </template>
            </view>

            <template v-if="item.aduotRemark != null">
                <template v-if="item.aduotRemark != ''">
                    <view class="review-tips">审核提示：{{ item.aduotRemark }}</view>
                </template>
            </template>
        </view>
    </view>
</template>

<script lang="ts" setup>
import { useRouter } from 'uniapp-router-next'
const props = withDefaults(
    defineProps<{
        withdrawApplyLists: any
    }>(),
    {
        withdrawApplyLists: []
    }
)
const router = useRouter()

const toDetail = (id: any) => {
    router.navigateTo(`/packageA/pages/withdraw_detail/withdraw_detail?id=${id}`)
}
</script>

<style lang="scss" scoped>
.withdrawal-record {
    margin: 20rpx;
    border-radius: 14rpx;
    background-color: #fff;

    .withdrawal-record-item:last-child {
        border: none;
    }
    .withdrawal-record-item {
        padding: 20rpx 30rpx;
        border-bottom: 1rpx solid #ebebeb;

        .withdrawal-record-item-text {
            font-size: 30rpx;
            font-weight: 400;
        }

        .withdrawal-record-item-amount {
            font-size: 34rpx;
            font-weight: 400;
        }

        .withdrawal-record-item-time {
            font-size: 24rpx;
            font-weight: 400;
            color: #999;
            margin-top: 10rpx;
        }

        .review-tips {
            font-size: 24rpx;
            color: #ff2c3c;
            margin-top: 10rpx;
        }

        .success {
            font-size: 24rpx;
            color: #0cc267;
        }

        .fail {
            font-size: 24rpx;
            color: #ff2c3c;
        }
    }
}
</style>
