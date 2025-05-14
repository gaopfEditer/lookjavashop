<template>
    <view class="page-status" :style="pageContainer" v-if="page.status !== PageStatusEnum['NORMAL']">
        <!-- Loading -->
        <template v-if="page.status === PageStatusEnum['LOADING']">
            <slot name="loading">
                <u-loading :size="60" mode="flower" />
            </slot>
        </template>
        <!-- Tips -->
        <template v-if="page.status === PageStatusEnum['ABNORMAL']">
            <slot name="abnormal">
                <u-empty :text="page.text" :mode="page.mode" :src="page.src" :icon-size="page.iconSize"
                    :icon-color="page.iconColor">
                </u-empty>
            </slot>
        </template>
    </view>
</template>


<script lang="ts" setup>

/**
 * @description 页面状态显示组件
 * @property {String} text 提示文字颜色
 * @property {String} mode 内置的图标
 * @property {String} src  图标名称或者图片路径(绝对路径)，如定义，mode参数会失效
 * @property {String | Number} fontSize 提示文字的大小，单位rpx
 * @property {String | Number} iconSize icon的大小，单位rpx，如果src为图片路径，此参数可以设置图片的尺寸
 * @property {String | Number} iconColor icon的颜色，字体图标时有效
 * @event {Function} [event] 点击按钮 (事件名由按钮组的event提供) 
 * @example <page-status ref="pageRef" />
 */

import { PageStatusEnum } from '@/enums/appEnums'
import { reactive } from 'vue'

interface PageType {
    status: string
    text: string
    mode: string
    src: string
    fontSize: string | number
    iconSize: string | number
    [index: number | string]: number | string
}

defineProps({
    pageContainer: {
        type: Object,
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        default: () => {}
    }
})

const page = reactive<PageType>({
    // 默认为加载页面
    status: PageStatusEnum['LOADING'],
    // 提示信息文字
    text: '',
    // 提示图片状态
    mode: 'list',
    // 图标名称或者图片路径(绝对路径)，如定义，mode参数会失效
    src: '',
    // 提示文字的大小，单位rpx
    fontSize: 28,
    // icon的大小，单位rpx，如果src为图片路径，此参数可以设置图片的尺寸
    iconSize: 360,
})

const show = (options: PageType) => {
    page.status = PageStatusEnum['ABNORMAL'];
    Reflect.ownKeys(options)
        .map((item: any) => {
            if (!Reflect.has(page, item)) {
                return
            }
            page[item] = options[item]
        });
    console.log(page)
}

const close = () => {
    page.status = PageStatusEnum['NORMAL'];
}

defineExpose({
    show,
    close
})
</script>


<style>
.page-status {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 900;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #FFFFFF;
}
</style>
 