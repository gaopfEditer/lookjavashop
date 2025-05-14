<template>
    <view class="flex justify-between text-base px-[30rpx] py-[26rpx]" @click="handleOpen">
        <view class="flex items-start flex-1">
            <text class="pr-4 text-muted">物流</text>
            <view class="flex-1 pr-4 truncate">
                <view class="text-black">
                    {{ defaultFreight == 0 ? '免配送费' : '快递: ¥' + defaultFreight }}
                </view>
                <view class="text-muted mt-[14rpx]">
                    配送至:
                    {{ defaultAddress.provinceName }}
                    {{ defaultAddress.cityName }}
                    {{ defaultAddress.districtName }}
                </view>
            </view>
        </view>
        <u-icon name="arrow-right" color="#707070"></u-icon>
    </view>

    <u-popup
        v-model="showRegion"
        mode="bottom"
        :duration="200"
        :closeable="false"
        :safeAreaInsetBottom="true"
    >
        <view class="addresss">
            <view class="addresss-header p-[30rpx] flex justify-center">
                <text class="text-[#101010] text-lg">配送至</text>
            </view>
            <view class="addresss-main">
                <scroll-view
                    :scroll-y="true"
                    style="height: 700rpx; touch-action: none"
                >
                    <template v-if="addressList.length">
                        <view
                            class="flex py-[20rpx] px-[30rpx]"
                            style="border-bottom: 1px solid #f0f0f0"
                            v-for="item in addressList"
                            :key="item.id"
                            @click.stop="onSelectAddress(item)"
                        >
                            <view class="flex-1 pr-[40rpx]">
                                <view class="text-lg text-[#222] mb-[14rpx]">
                                    <text class="font-medium">{{ item?.contact }}</text>
                                    <text class="ml-[10rpx]">{{ item?.mobile }}</text>
                                    <text class="default" v-if="item.isDefault">默认</text>
                                </view>
                                <view class="text-base text-content">
                                    {{ item.addressDetail }}
                                </view>
                            </view>
                            <view class="w-[100rpx] flex items-center justify-center">
                                <l-checkbox
                                    :checked="regionId == item.id"
                                    label=""
                                    :true-label="1"
                                    :false-label="0"
                                    :active-color="$theme.color"
                                />
                            </view>
                        </view>
                    </template>

                    <template v-else>
                        <u-empty text="暂无地址～" mode="address"></u-empty>
                    </template>
                </scroll-view>
                <!--  操作按钮  -->
                <button
                    class="full-primary mx-[30rpx] h-[82rpx] rounded-[60px] leading-[82rpx]"
                    @click.stop="toAddressAdd"
                >
                    选择其他地址
                </button>
            </view>
        </view>
    </u-popup>
</template>

<script lang="ts" setup>
import {addressLists} from '@/api/address'
import {ref, watchEffect} from "vue";
import {onShow} from '@dcloudio/uni-app'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const emit = defineEmits(['refresh'])
const props = withDefaults(
    defineProps<{
        defaultFreight: number
        defaultAddress?: any
    }>(),
    {
        defaultFreight: 0,
        defaultAddress: {
            addressId: 0,
            provinceName: '北京',
            cityName: '北京市',
            districtName: '朝阳区'
        }
    }
)

const regionId = ref<number>(0)
const addressList = ref<any[]>([])
const showRegion = ref<boolean>(false)

watchEffect(() => {
    regionId.value = props?.defaultAddress?.addressId ?? ''
})

const onSelectAddress = (row: any) => {
    // emit('refresh')
    regionId.value = row.id
    showRegion.value = false
    props.defaultAddress.provinceName = row.provinceName
    props.defaultAddress.cityName = row.cityName
    props.defaultAddress.districtName = row.districtName
}

watchEffect(() => {
    const show = showRegion.value
    if (show) getAddressList()
})

const handleOpen = () => {
    const userStore = useUserStore()
    if (!userStore.isLogin) {
        router.navigateTo('/pages/login/login')
        return
    }
    showRegion.value = true
}

const getAddressList = async () => {
    if (!showRegion.value) return
    try {
        const data: any[] = await addressLists()
        addressList.value = data
    } catch (error) {
        console.log('请求收货地址失败', error)
    }
}

// 去新增收货地址
const toAddressAdd = () => {
    router.navigateTo('/packageA/pages/user_address_detail/user_address_detail')
}

onShow(async () => {
    console.log('Show')
    await getAddressList()
})
</script>

<style lang="scss" scoped>
.addresss {
    &-main {
        //background: $u-bg-color;
        padding-bottom: 10rpx;
        .default {
            padding: 0 4rpx;
            margin-left: 20rpx;
            color: $u-type-primary;
            font-size: 22rpx;
            background: rgba($u-type-primary, 0.1);
        }
    }
}
</style>