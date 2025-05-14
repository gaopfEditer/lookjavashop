<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>
    <view :style="$theme.pageStyle">
        <z-paging ref="paging" @query="queryList" height="100%" :fixed="true" :loading-more-enabled="false"
            empty-view-text="暂无可用地址，请添加~" :empty-view-img="EmptyAddressImg" :empty-view-style="{
                'margin-top': '100rpx'
            }" :empty-view-center="false" :empty-view-img-style="{
    width: '360rpx',
    height: '360rpx'
}" :auto-clean-list-when-reload="false" :auto-scroll-to-top-when-reload="false">
            <view class="p-[20rpx] pb-[100px]">
                <view class="mb-[20rpx]" v-for="(item, index) in address.lists" :key="item.id">
                    <u-swipe-action :show="item.show" :index="index" :options="options" btn-width="120" bg-color="none"
                        @click="handleDel">
                        <view class="flex justify-between item w-[710rpx]" :data-id="item.id"
                            @click.stop="onSelect(item, address.type)">
                            <view class="flex-1">
                                <view>
                                    <text class="text-xl font-medium">
                                        {{ item.contact }}
                                        {{ item.mobile }}
                                    </text>
                                    <text class="default" v-if="item.isDefault">默认</text>
                                </view>
                                <view class="mt-[20rpx] sm text-muted">
                                    {{ item.addressDetail }}
                                </view>
                            </view>
                            <view class="setting" @click.stop="goEditAddress(`?id=${item.id}`)">
                                编辑
                            </view>
                        </view>
                    </u-swipe-action>
                </view>
            </view>
        </z-paging>

        <view class="flex footer">
            <!-- #ifdef H5 || MP-WEIXIN -->
            <button v-if="address.isWxClient" class="bg-white btn mr-[20rpx]" @click="getWxAddress">
                <image class="icon-md" src="/static/images/icon/icon_wx.png"></image>
                <text>微信导入</text>
            </button>
            <!-- #endif -->
            <button class="text-white btn bg-primary" @click="goEditAddress('')">添加地址</button>
        </view>
    </view>
</template>

<script lang="ts" setup>
// #ifdef H5
import wechatOa from '@/utils/wechat'
// #endif
import { reactive, shallowRef } from 'vue'
import { isWeixinClient } from '@/utils/client'
import { useAddress } from '@/hooks/useAddress'
import { addressLists, addressDel } from '@/api/address'
import type { AddressFromType } from '@/api/address.d'
import { onLoad, onShow } from '@dcloudio/uni-app'
import EmptyAddressImg from '../../static/empty/address.png'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const { onSelect } = useAddress()
const paging = shallowRef()
const address = reactive<any>({
    type: false,
    lists: [] as AddressFromType[],
    isWxClient: true
})
const options = reactive([
    {
        text: '删除',
        style: {
            color: '#FFFFFF',
            backgroundColor: '#FF2C3C'
        }
    }
])

const queryList = async () => {
    const lists = await addressLists()
    lists.forEach((item: any) => {
        item.show = false
    })
    address.lists = lists
    paging.value.complete(lists)
}

const goEditAddress = (params: any = '') => {
    router.navigateTo('/packageA/pages/user_address_detail/user_address_detail' + params)
}

const handleDel = async (index: number): Promise<void> => {
    try {
        const id: number = address.lists[index].id
        await addressDel({ id: id })
        paging.value.reload()
    } catch (err) {
        //TODO handle the exception
        console.log('删除报错=>', err)
    }
}

const getWxAddress = async () => {
    // #ifdef H5
    const res: any = await wechatOa.getAddress()
    // #endif
    // #ifdef MP-WEIXIN
    const { errMsg }: any = await uni.authorize({ scope: 'scope.address' })
    if (errMsg !== 'authorize:ok') {
        const data: any = await uni.showModal({
            title: '您已拒绝导入微信地址权限',
            content: '是否进入权限管理，调整授权？'
        })
        if (data.confirm) {
            const [error, data]: any = await uni.openSetting()
        }
    }
    const res: any = await uni.chooseAddress({})
	console.log('res', res)
    // #endif
    uni.setStorageSync('wxAddress', JSON.stringify(res))
    goEditAddress()
}

onShow(() => {
    paging.value?.reload()
})

onLoad(({ type }) => {
    if (type) address.type = type
    //#ifdef H5
    address.isWxClient = isWeixinClient()
    //#endif
})
</script>

<style lang="scss" scoped>
.item {
    border-radius: 14rpx;
    background-color: #ffffff;
    padding: 30rpx 30rpx 36rpx 30rpx;

    .default {
        padding: 0 4rpx;
        margin-left: 20rpx;
        color: $u-type-primary;
        font-size: 22rpx;
        background: rgba($u-type-primary, 0.1);
    }

    .setting {
        height: 90rpx;
        width: 100rpx;
        line-height: 90rpx;
        text-align: right;
        color: $u-type-primary;
        font-size: 26rpx;
    }
}

.footer {
    left: 0;
    right: 0;
    bottom: 0;
    height: 118rpx;
    position: fixed;
    padding: 0 30rpx;
    z-index: 999;
    box-sizing: content-box;
    padding-bottom: env(safe-area-inset-bottom);

    .btn {
        flex: 1;
        display: flex;
        height: 80rpx;
        font-size: 30rpx;
        justify-content: center;
        align-items: center;
        border-radius: 60rpx;
    }

    .icon-md {
        width: 48rpx;
        height: 48rpx;
        margin-right: 20rpx;
    }
}
</style>
