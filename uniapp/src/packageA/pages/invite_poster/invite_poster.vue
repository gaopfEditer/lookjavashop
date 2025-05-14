<template>
    <page-meta :page-style="$theme.pageStyle">
        <!-- #ifndef H5 -->
        <navigation-bar :front-color="$theme.navColor" :background-color="$theme.navBgColor" />
        <!-- #endif -->
    </page-meta>

    <view class="flex flex-col items-center">
        <image :src="imgPath" mode="widthFix" class="mt-[20rpx]"></image>
        <invite-poster ref="poster" :config="config" @success="handleSuccess" />
        <view class="mt-[40rpx] px-[60rpx] w-full">
            <!-- <view class="m-b-40">
                <view class="m-b-10 sm lighter">我的邀请码</view>
                <view class="flex row-between">
                    <view class="font-size-44">{{ '123' }}</view>
                    <view class="sm m-r-30 copy-btn">点击复制</view>
                </view>
            </view> -->
            <!-- #ifndef H5  -->
            <u-button type="primary" shape="circle" @click="saveImg">保存到相册</u-button>
            <!-- #endif -->
            <!-- #ifdef H5 -->
            <u-button type="primary" shape="circle" @click="saveImg">长按保存到相册</u-button>
            <!-- #endif -->
        </view>
    </view>

    <!-- 页面状态 -->
    <PageStatus ref="pageRef"></PageStatus>
</template>

<script setup lang="ts">
import {nextTick, onMounted, reactive, ref, shallowRef, unref} from 'vue'
import invitePoster from './components/invite-poster.vue'
import { miniAppCode } from '@/api/app'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import PageStatus from '@/components/page-status/page-status.vue'
import EmptyErrorImage from '@/packageA/static/empty/error.png'

const userStore = useUserStore()

const pageRef = shallowRef()
//海报生成器ref
const poster = shallowRef()
//海报路径
const imgPath: any = ref('')
//海报配置
const config = reactive({
    url: '',
    userData: userStore.userInfo,
    slogan: '邀请你一起来赚大钱！！'
})

const handleSuccess = (val: any) => {
    imgPath.value = val
    pageRef?.value?.close()
}
//获取海报路径
const getPostUrl = async () => {
    try {
        uni.showLoading({
            title: '正在生成中'
        })
        //#ifndef H5
        const { image } = await miniAppCode({
            // 承载商品拓展类型(t = goodsType, aid = activityId
            scene: `share_code=${userStore.userInfo.code}`,
            page: 'pages/index/index'
        })
        config.url = image
        //#endif
        //#ifdef H5
        const domain = useAppStore().config.domain
        config.url = `${domain}mobile/pages/index/index?share_code=${userStore.userInfo.code}`
        //#endif
        await poster.value.drawCanvas()
        uni.hideLoading()
    } catch (error) {
        unref(pageRef)?.show({
            text: error,
            src: EmptyErrorImage
        })
        console.log('海报生产失败', error)
    }
}
//保存图片
const saveImg = () => {
    // #ifndef H5
    uni.saveImageToPhotosAlbum({
        filePath: imgPath.value,
        success: (res) => {
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

onMounted(async () => {
    await nextTick()
    await getPostUrl()
})
</script>

<style scoped lang="scss"></style>
