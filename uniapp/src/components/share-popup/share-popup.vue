<template>
    <u-popup
        class="share-popup"
        v-model="showPopup"
        mode="bottom"
        border-radius="14"
        :closeable="true"
        :safe-area-inset-bottom="true"
        :mask-close-able="true"
    >
        <view class="flex items-center justify-center font-medium mt-[30rpx] mb-[30rpx]"
            >分享至</view
        >
        <view class="flex justify-around share-tab">
            <view class="flex-col items-center" @tap="getPoster">
                <image
                    mode="widthFix"
                    class="share-icon"
                    src="/static/images/icon/icon_generate_poster.png"
                ></image>
                <view class="mt-[40rpx]">生成海报</view>
            </view>
            <!-- #ifdef MP-WEIXIN-->
            <button
                open-type="share"
                class="flex-col items-center"
                style="background: none; border: none"
                hover-class="none"
                @click="handleshare"
            >
                <image class="share-icon" src="/static/images/icon/icon_wx.png"></image>
                <view class="mt-[10rpx]">微信好友</view>
            </button>
            <!-- #endif -->
            <!-- #ifdef H5 || APP-PLUS -->
            <view oclass="flex-col items-center" @tap="shareWx">
                <image class="share-icon" src="/static/images/icon/icon_wx.png"></image>
                <view class="mt-[40rpx]">微信好友</view>
            </view>
            <!-- #endif -->
        </view>
        <view class="leading-[98rpx] text-center text-xl bg-page h-[98rpx]" @tap="showPopup = false"
            >取消</view
        >
    </u-popup>
    <view class="share-poster plain-popup">
        <u-popup
            v-model="poster.showPoster"
            mode="center"
            :closeable="true"
            :safe-area-inset-bottom="true"
        >
            <!-- #ifndef H5 -->
            <image style="width: 640rpx" mode="widthFix" :src="poster.poster"></image>
            <!-- #endif -->
            <!-- #ifdef H5 -->
            <img style="width: 640rpx" :src="poster.poster" />
            <!-- #endif -->

            <button class="flex justify-center save-btn" size="lg" @tap="savePoster">
                <!-- #ifndef H5 -->
                保存图片到相册
                <!-- #endif -->
                <!-- #ifdef H5 -->
                长按保存图片到相册
                <!-- #endif -->
            </button>
        </u-popup>
    </view>
    <!-- #ifdef H5 -->
    <u-popup
        class="share-tips"
        v-model="poster.showTips"
        mode="top"
        :customStyle="{ background: 'none' }"
    >
        <view style="overflow: hidden">
            <image src="/static/images/icon/share_arrow.png" class="share-arrow" />
            <view class="text-white" style="text-align: center; margin-top: 280rpx">
                <view class="text-lg font-medium">立即分享给好友吧</view>
                <view class="mt-2 text-sm">点击屏幕右上角将本页面分享给好友</view>
            </view>
        </view>
    </u-popup>
    <!-- #endif -->

    <!-- 海报 -->
    <poster-vue
        v-if="showPopup"
        ref="posterRef"
        :qrcode="poster.mnpQrcode"
        :options="poster.options"
        :link="getLink"
        @success="handleSuccess"
        @fail="handleFail"
    />
</template>

<script lang="ts" setup>
import { miniAppCode, posterGoodsBase64 } from '@/api/app'
import { reactive, computed, shallowRef, unref, nextTick, watch } from 'vue'
import posterVue from './poster.vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { useRouter } from 'uniapp-router-next'

const router = useRouter()
const userStore = useUserStore()
const { isLogin } = storeToRefs(userStore)

const emit = defineEmits(['update:modelValue'])

const props = defineProps({
    modelValue: {
        type: Boolean
    },
    options: {
        type: Object,
        default: () => ({})
    },
    shareId: {
        type: [String, Number],
        default: ''
    },
    payload: {
        type: Object,
        default: () => ({})
    },
    pagePath: {
        type: String,
        default: ''
    }
})
// 海报实例
const posterRef = shallowRef()

// 海报参数设置等等
const poster = reactive({
    poster: '',
    options: props.options,
    showPoster: false,
    showTips: false,
    mnpQrcode: ''
})

// H5中分享的链接
const getLink = computed(() => {
    //#ifdef H5
    return `${window.location.origin}/mobile/${props.pagePath}?id=${
        props.shareId
    }&payload=${JSON.stringify(props.payload)}&share_code=${userStore?.userInfo?.code || ''}`
    //#endif
    //#ifdef MP
    return ''
    //#endif
})

const showPopup = computed({
    get: () => {
        console.log('??')
        return props.modelValue
    },
    set: (val) => {
        emit('update:modelValue', val)
    }
})

watch(
    () => props.options,
    (val) => {
        poster.options = val
    },
    { deep: true }
)

const getPoster = async () => {
    if (!isLogin.value) {
        router.navigateTo('/pages/login/login')
        return
    }
    if (poster.poster) {
        poster.showPoster = true
        showPopup.value = false
        return
    }
    uni.showLoading({
        title: '正在生成中'
    })
    try {
        // #ifdef MP-WEIXIN
        await getMiniAppCodeFunc()
        // #endif

        // #ifdef H5
        await getPosterGoodsFunc()
        // #endif

        setTimeout(() => {
            drawCanvas()
        }, 100)
    } catch (error) {
        console.log(error)
        uni.hideLoading()
    }
}

const getMiniAppCodeFunc = () => {
    return new Promise((resolve, reject) => {
        miniAppCode({
            // 承载商品拓展类型(t = goodsType, aid = activityId
            scene: `id=${props.shareId}&t=${props.payload.type}${
                props.payload.activityId ? '&aid=' + props.payload.activityId : ''
            }&share_code=${userStore?.userInfo?.code || ''}`,
            page: props.pagePath
        })
            .then((data: any) => {
                console.log(data)
                poster.mnpQrcode = data.image
                resolve(data)
            })
            .catch((error) => {
                console.log(error)
                reject()
            })
    })
}

const getPosterGoodsFunc = () => {
    return new Promise((resolve, reject) => {
        posterGoodsBase64({
            id: props.shareId
        })
            .then((data: any) => {
                poster.options.image = data.goodsImage
                poster.options.avatar = data.userAvatar
                resolve(data)
            })
            .catch(() => {
                reject()
            })
    })
}

const drawCanvas = async () => {
    await nextTick()
    posterRef.value.drawCanvas()
}

const handleSuccess = (val: any) => {
    if (!val) return
    poster.poster = val
    uni.hideLoading()
    poster.showPoster = true
    showPopup.value = false
}

const handleFail = () => {
    uni.hideLoading()
    uni.$u.toast('生成失败')
}

const shareWx = () => {
    // #ifdef H5
    poster.showTips = true
    showPopup.value = false
    // #endif
    // #ifdef APP-PLUS
    uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        href: unref(getLink),
        title: props.options.name,
        summary: '',
        imageUrl: props.options.image,
        success: (res) => {
            console.log('分享成功')
        },
        fail: (err) => {
            uni.$u.toast(err.errMsg)
        }
    })
    // #endif
}

const savePoster = async () => {
    // #ifndef H5
    uni.saveImageToPhotosAlbum({
        filePath: poster.poster,
        success: (res) => {
            poster.showPoster = false
            uni.$u.toast('保存成功')
        },
        fail: (err) => {
            uni.$u.toast('保存失败')
            console.log(err)
        }
    })
    // #endif
    // #ifdef H5
    uni.$u.toast('请长按图片保存')
    // #endif
}
const handleshare = () => {
    showPopup.value = false
}
</script>

<style lang="scss">
// 弹出选择分享方式样式
.share-popup {
    .share-tab {
        margin: 40rpx 0 140rpx;

        .share-icon {
            width: 100rpx;
            height: 100rpx;
        }
    }
}

// 分享海报样式
.share-poster {
    .share-img {
        width: 640rpx;
        border-radius: 12rpx;
    }
    .save-btn {
        color: #fff;
        margin-top: 20rpx;
        height: 84rpx;
        border-radius: 20rpx;
        line-height: 84rpx;
        background-color: $u-type-primary !important;
    }
}

// 提示分享样式
.share-tips .share-arrow {
    width: 140rpx;
    height: 250rpx;
    float: right;
    margin: 15rpx 31rpx 0 0;
}
</style>
