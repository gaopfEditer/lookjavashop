<template>
    <view class="navbar" @tap.stop="navbar.showFloat = false">
        <u-navbar
            :background="background"
            :title="title"
            :title-color="titleColor"
            :border-bottom="borderBottom"
            :immersive="immersive"
            :title-bold="true"
            :is-back="false"
        >
            <view class="navbar-left flex py-[12rpx] px-[25rpx] rounded-[30rpx] ml-[20rpx]">
                <u-icon :name="backIcon" :size="36" @click="goBack"></u-icon>
                <view class="line"></view>
                <view class="navbar-lists" @tap.stop="navbar.showFloat = !navbar.showFloat">
                    <u-icon :name="IconList" :size="32"></u-icon>
                    <view class="navbar-float" v-show="navbar.showFloat">
                        <view
                            v-for="(item, index) in navbar.list"
                            :key="index"
                            :to="item.url"
                            :open-type="item.type"
                            class="float-item"
                            hover-class="none"
							@click="gotoPage(item)"
                        >
                            <view class="flex items-center justify-center">
                                <u-icon :name="item.icon" :size="44"></u-icon>
                                <text class="ml-[20rpx]">{{ item.name }}</text>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
        </u-navbar>
        <view class="mask" v-show="navbar.showFloat" @touchstart="navbar.showFloat = false"> </view>
    </view>
</template>

<script lang="ts" setup>
import { reactive, computed } from 'vue'
import IconBack from '@/static/images/icon/icon_back.png'
import IconList from '@/static/images/icon/icon_list.png'
import IconHome from '@/static/images/icon/icon_home.png'
import IconSearch from '@/static/images/icon/icon_search.png'
import IconUser from '@/static/images/icon/icon_nav_user.png'
import IconCart from '@/static/images/icon/icon_cart.png'
import { useRouter } from 'uniapp-router-next'
import { navigateTo } from '@/utils/util'

const router = useRouter()
const props = defineProps({
    // 导航标题
    title: {
        type: String
    },
    // 导航标题颜色
    titleColor: {
        type: String,
        default: '#000000'
    },
    // 导航的背景颜色
    background: {
        type: Object,
        default: () => ({
            background: '#ffffff'
        })
    },
    // 是否显示底部边框
    borderBottom: {
        type: Boolean,
        default: false
    },
    immersive: {
        type: Boolean,
        default: false
    }
})

const navbar = reactive({
    isIndex: false,
    showFloat: false,
    list: [
        {
            url: '/pages/index/index',
            name: '首页',
            icon: IconHome,
            type: 'switchTab'
        },
        {
            url: '/pages/search/search',
            name: '搜索',
            icon: IconSearch,
            type: 'navigate'
        },
        {
            url: '/pages/shop_cart/shop_cart',
            name: '购物车',
            icon: IconCart,
            type: 'switchTab'
        },
        {
            url: '/pages/user/user',
            name: '个人中心',
            icon: IconUser,
            type: 'switchTab'
        }
    ]
})

const backIcon = computed(() => (navbar.isIndex ? IconHome : IconBack))

const goBack = () => {
    if (!navbar.isIndex) {
        uni.navigateBack()
        return
    }
    router.reLaunch('/pages/index/index')
}

const gotoPage = (item: any) => {
	if (item.type == 'switchTab') {
	    uni.switchTab({
	        url: item.url
	    })
	} else {
		uni.navigateTo({
			url: item.url
		})
	    //navigateTo(item.url, 'navigateTo')
	}
}

setTimeout(() => {
    const pages = getCurrentPages()
    if (pages.length == 1) {
        navbar.isIndex = true
    }
})
</script>

<style lang="scss" scoped>
.navbar {
    .navbar-left {
        background: rgba(255, 255, 255, 0.3);
        border: 1rpx solid rgba(0, 0, 0, 0.1);

        .line {
            width: 1px;
            height: 36rpx;
            background: rgba(0, 0, 0, 0.2);
            margin: 0 25rpx;
        }

        .navbar-lists {
            display: flex;
            justify-content: center;
            position: relative;

            .navbar-float {
                position: absolute;
                top: 40px;
                width: 258rpx;
                padding: 0 24rpx;
                background: #fff;
                border-radius: 14rpx;
                box-shadow: 0px 3px 6px rgba(0, 0, 0, 0.06);

                &::before {
                    content: '';
                    display: block;
                    position: absolute;
                    left: 50%;
                    width: 0;
                    height: 0;
                    border: 14rpx solid transparent;
                    border-bottom-color: #fff;
                    transform: translate(-50%, -100%);
                }

                .float-item {
                    padding: 20rpx 0;
                    display: flex;
                    align-items: center;

                    &:not(:last-of-type) {
                        border-bottom: 1px solid #e5e5e5;
                    }
                }
            }
        }
    }

    .mask {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1;
    }
}
</style>
