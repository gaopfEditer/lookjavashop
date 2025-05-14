<template>
    <view>
        <l-painter
            ref="painterRef"
            :css="{
                width: '600rpx',
                height: '960rpx',
                'border-radius': '20rpx',
                overflow: 'hidden'
            }"
            custom-style="position: fixed; left: 200%"
        >
            <l-painter-image
                :src="bg"
                :css="{
                    position: 'absolute',
                    width: '600rpx'
                }"
            />
            <l-painter-view
                :css="{
                    position: 'absolute',
                    bottom: 0,
                    'background-color': `rgba(255, 255, 255, ${90 / 100})`,
                    width: '100%',
                    height: '290rpx',
                    'border-radius': '20rpx'
                }"
            >
                <l-painter-view
                    :css="{
                        position: 'absolute',
                        top: '30rpx',
                        left: '30rpx',
                        display: 'flex',
                        'align-items': 'center'
                    }"
                >
                    <l-painter-image
                        :src="config!.userData.avatar"
                        :css="{
                            width: '80rpx',
                            height: '80rpx',
                            'border-radius': '50%'
                        }"
                    />
                    <l-painter-text
                        :text="`${config!.userData.nickname}`"
                        :css="{
                            'margin-left': '20rpx',
                            fontSize: '34rpx',
                            'line-clamp': 1,
                            'font-weight': 'bold',
                            width: '220rpx'
                        }"
                    />
                </l-painter-view>
                <l-painter-text
                    :text="`${config!.slogan}`"
                    :css="{
                        position: 'absolute',
                        top: '140rpx',
                        left: '30rpx',
                        fontSize: '28rpx',
                        color: '#333333'
                    }"
                />
                <l-painter-text
                    :text="`邀请码：${config!.userData.code}`"
                    :css="{
                        position: 'absolute',
                        top: '200rpx',
                        left: '30rpx',
                        fontSize: '28rpx',
                        color: '#FF2C3C'
                    }"
                />
                <l-painter-view
                    :css="{
                        position: 'absolute',
                        top: '56rpx',
                        left: '360rpx'
                    }"
                >
                    <!-- #ifdef H5 -->
                    <l-painter-qrcode
                        css="width: 180rpx; height: 180rpx;"
                        :text="config!.url"
                    ></l-painter-qrcode>
                    <!-- #endif -->
                    <!-- #ifdef MP -->
                    <l-painter-image :src="config!.url" css="width: 168rpx; height: 168rpx;" />
                    <!--  #endif -->
                </l-painter-view>
            </l-painter-view>
        </l-painter>
    </view>
</template>

<script setup lang="ts">
import { shallowRef } from 'vue'
import bg from '@/packageA/static/distribution/invitePost.png'

const props = defineProps({
    config: {
        type: Object,
        default: {} as any
    }
})

const emit = defineEmits(['fail', 'success'])

const painterRef = shallowRef()

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
        console.log(error)
        uni.$u.toast('调用海报错误')
    }
}

defineExpose({ drawCanvas })
</script>

<style scoped lang="scss"></style>
