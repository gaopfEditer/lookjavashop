<template>
    <l-painter
        ref="painterRef"
        css="width: 640rpx;"
        isCanvasToTempFilePath
        @success="handleSuccess"
        @fail="handleFail"
        custom-style="position: fixed; left: 200%"
    >
        <l-painter-view
            v-if="options.image"
            css="padding-left: 40rpx;border-radius: 20rpx;background-color: #ffffff; padding-bottom: 35rpx;"
        >
            <l-painter-view css="margin-top: 20rpx; display: block; ">
                <l-painter-image
                    :src="options.avatar"
                    css="width: 72rpx;  height: 72rpx; border-radius: 50%;display: inline-block;"
                />
                <l-painter-text
                    :text="`来自${userInfo?.nickname}的分享`"
                    css="margin-top: 15rpx; padding-left: 20rpx;padding-bottom: 10rpx; color: #333333; font-size: 28rpx;line-clamp:1;width: 400rpx;"
                />
            </l-painter-view>
            <l-painter-view css="margin-top: 20rpx;">
                <l-painter-image
                    :src="options?.image"
                    css="object-fit: cover; object-position: center; width: 560rpx; height: 560rpx;"
                />
            </l-painter-view>
            <l-painter-view css="margin-top: 30rpx;">
                <!-- 商品分享海报文字内容 -->
                <l-painter-view
                    v-if="type == 'goods'"
                    :css="`display: inline-block; width: 400rpx;`"
                >
                    <l-painter-view
                        :css="`vertical-align: bottom; color: red; font-size: 30rpx; line-height: 1em;`"
                    >
                        <l-painter-view css="display: inline-block">
                            <l-painter-text
                                text="￥"
                                css="vertical-align: bottom;font-size: 28rpx;"
                            />
                            <l-painter-text
                                v-if="options?.price"
                                :text="options?.price"
                                css="vertical-align: bottom; font-size: 38rpx;"
                            />
                        </l-painter-view>
                        <l-painter-view v-if="options?.linePrice" css="display: inline-block;margin-left: 20rpx">
                            <l-painter-text
                                text="￥"
                                css="vertical-align: bottom;font-size: 28rpx;color: #999999"
                            />
                            <l-painter-text
                                :text="options?.linePrice"
                                css="vertical-align: bottom;font-size: 28rpx; font-weight: normal; text-decoration: line-through; color: #999999"
                            />
                        </l-painter-view>
                    </l-painter-view>
                    <l-painter-view css="margin-top:30rpx;">
                        <l-painter-text
                            css="line-clamp: 2; color: #333333; line-height: 1.5em;font-size: 30rpx; width: 378rpx; padding-right:22rpx; box-sizing: border-box"
                            :text="options?.name"
                        ></l-painter-text>
                    </l-painter-view>
                </l-painter-view>

                <!-- 砍价分享海报文字内容 -->
                <l-painter-view
                    v-if="type == 'bargain'"
                    :css="`display: inline-block; width: 400rpx;`"
                >
                    <l-painter-view>
                        <l-painter-text
                            :css="`line-clamp: 2; color: red; line-height: 1.5em;font-size: 32rpx; width: 375rpx; padding-right:22rpx; box-sizing: border-box`"
                            text="我正在参与砍价 还差一步"
                        ></l-painter-text>
                    </l-painter-view>
                    <l-painter-view css="margin-top:8rpx;">
                        <l-painter-text
                            css="line-clamp: 2; color: #F95F2F; line-height: 1.5em;font-size: 24rpx; width: 378rpx; padding-right:22rpx; box-sizing: border-box"
                            text="帮忙砍一刀"
                        ></l-painter-text>
                    </l-painter-view>
                    <l-painter-view css="margin-top:8rpx;">
                        <l-painter-text
                            css="line-clamp: 2; color: #333333; line-height: 1.5em;font-size: 28rpx; width: 378rpx; padding-right:22rpx; box-sizing: border-box"
                            :text="options?.name"
                        ></l-painter-text>
                    </l-painter-view>
                </l-painter-view>

                <l-painter-view v-if="type" css="display: inline-block;">
                    <!-- #ifdef H5 || APP-PLUS -->
                    <l-painter-qrcode css="width: 168rpx; height: 168rpx;" :text="link">
                    </l-painter-qrcode>
                    <!--  #endif -->
                    <!-- #ifdef MP -->
                    <l-painter-image :src="qrcode" css="width: 168rpx; height: 168rpx;" />
                    <!--  #endif -->
                    <l-painter-text
                        text="长按识别二维码"
                        css="display: block; padding-top: 10rpx; color: #999999;font-size: 24rpx;"
                    />
                </l-painter-view>
            </l-painter-view>
        </l-painter-view>
    </l-painter>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
const userStore = useUserStore()
const { userInfo } = storeToRefs(userStore)

const emit = defineEmits(['fail', 'success'])

const props = defineProps({
    options: {
        type: Object,
        default: () => ({})
    },
    qrcode: {
        type: [String],
        default: ''
    },
    link: {
        type: String,
        default: ''
    },
    type: {
        type: String,
        default: 'goods'
    }
})

const painterRef = ref()

const handleSuccess = () => {
    emit('success')
}

const handleFail = () => {
    emit('fail')
}

const drawCanvas = () => {
    try {
        console.log('开始生产')
        // 生成图片
        painterRef.value?.canvasToTempFilePathSync({
            fileType: 'png',
            // 如果返回的是base64是无法使用 saveImageToPhotosAlbum，需要设置 pathType为url
            pathType: 'url',
            quality: 1,
            success: (res: any) => {
                console.log('绘制海报成功', res)
                emit('success', res.tempFilePath)
            }
        })
    } catch (error) {
        uni.$u.toast('调用海报错误')
    }
}

defineExpose({ drawCanvas })
</script>
