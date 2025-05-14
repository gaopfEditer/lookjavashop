<template>
    <view :class="$theme.pageStyle">
        <u-popup v-model="spec.show" mode="bottom" border-radius="14" :closeable="true" :safe-area-inset-bottom="true">
            <!-- Header -->
            <view class="header">
                <!-- Goods Image -->
                <u-image width="180" height="180" mode="aspectFill" border-radius="10"
                    :src="spec.goodsOptionsSpec.image || defaultInfo.image" @click="previewGoodsImage" />
                <!-- Goods Checked Specifcation -->
                <view class="header-content u-skeleton-rect">
                    <view class="flex">
                        <!-- 售价 -->
                        <price class="header-content-price" :content="getSpecPrice(spec.priceKey)" main-size="46rpx"
                            minor-size="32rpx" />
                    </view>

                    <!-- 库存 -->
                    <view class="header-content-label">
                        库存：
                        <text v-if="spec.goodsOptionsSpec.stock == 0">缺货</text>
                        <text v-else>
                            {{ spec.goodsOptionsSpec.stock || defaultInfo.stock }}
                            {{ defaultInfo.unit || '' }}
                        </text>
                    </view>

                    <view class="text-base mt-[10rpx]">
                        <text>{{ skuCheckedText }}</text>
                    </view>
                </view>
            </view>
            <!-- Main -->
            <view class="main">
                <!-- Specifcation -->
                <scroll-view scroll-y="true" class="scroll-spec" v-show="specList.length">
                    <view class="spec" v-for="(specItem, specIndex) in specList" :key="specIndex">
                        <!-- Title -->
                        <view class="spec-title">{{ specItem.name }}</view>
                        <!-- Content -->
                        <view class="spec-content">
                            <view v-for="(optionItem, optionIndex) in specItem.specList" :key="optionIndex" :class="[
                                'spec-item',
                                {
                                    'spec-item--active':
                                        spec.checkedList[specIndex]?.id == optionItem?.id
                                },
                                {
                                    'spec-item--disabled': specDisabeld(optionItem?.id)
                                }
                            ]" @click="changeSpecOption(specIndex, optionItem?.id)">
                                {{ optionItem.value }}
                            </view>
                        </view>
                    </view>
                </scroll-view>

                <!-- Goods Number -->
                <view class="total" v-if="showGoodsNum">
                    <view class="total-label">数量</view>
                    <u-number-box v-model="spec.goodsTotal" :min="1"
                        @change="handleGoodsNumber($event, spec.goodsOptionsSpec.stock)" />
                </view>

                <!--  -->
                <view class="scroll-spec" v-show="!specList.length"></view>
            </view>

            <!-- operation -->
            <view class="operation">
                <view class="operation-button disabled" v-if="spec.goodsOptionsSpec.stock == 0">
                    缺货
                </view>
                <template v-else>
                    <view class="operation-button" :class="{
                        disabled: !spec.goodsOptionsSpec.stock
                    }" v-for="(item, index) in buttons" :key="index" :style="[item.style]"
                        @click="onButtonHandle(item.event)">
                        {{ item.text }}
                    </view>
                </template>
            </view>
        </u-popup>
    </view>
</template>

<script lang="ts" setup>
import { reactive, computed, watch, unref, PropType, nextTick } from 'vue'

/**
 * @description 商品规格选择Popup弹窗
 * @property {String} mode 商品规格模式 (默认值: normal)
 * @property {Array} specList 规格列表 (必填项)
 * @property {Array} specMap 规格映射表 (必填项)
 * @property {Array} buttons 按钮组 (默认: [{ text: '立即购买', style: {}, event: 'buy' }])
 * @event {Function} [event] 点击按钮 (事件名由按钮组的event提供)
 * @example <goods-spec v-model="show" @buy="handleSubmit" />
 */

const emit = defineEmits(['confirm', 'buttonsHandle'])

const props = defineProps({
    // 显示状态
    value: {
        type: Boolean,
        default: false
    },
    // 按钮组
    buttons: {
        type: Array as PropType<any[]>,
        default: () => [
            {
                text: '立即购买',
                style: {},
                event: 'buy'
            }
        ]
    },
    // 规格列表
    specList: {
        type: Array as PropType<any[]>,
        default: () => []
    },
    // 规格映射表
    specMap: {
        type: Array as PropType<any[]>,
        default: () => []
    },
    // 活动的商品规格
    activitySpec: {
        type: Array as PropType<any[]>,
        default: () => []
    },
    // 默认商品信息
    defaultInfo: {
        type: Object,
        default: () => ({})
    },
    // 显示商品数量
    showGoodsNum: {
        type: Boolean,
        default: true
    }
})

const spec = reactive<any>({
    show: false, //
    mapTable: new Map(), // 规格值映射表
    checkedList: [], // 已选择的规格列表
    goodsTotal: 1, // 商品数量
    priceKey: 'price', // 显示价格字段
    goodsOptionsSpec: {}, // 商品已选择的规格
    nullSpec: [], // 当前选择中缺货的规格

    activitySpec: [] // 活动商品sku
})

const specCheckedIds = computed(() =>
    spec.checkedList
        .map((item: any) => item.id)
        .filter((item: any[]) => item)
        .join(',')
)

const specDisabeld = computed(() => (id: number | string) => {
    return (
        spec.nullSpec.includes(id.toString()) ||
        (spec.goodsOptionsSpec.stock === 0 &&
            spec.goodsOptionsSpec.skuValueIds.split(',').includes(id.toString()))
    )
})

// 活动的商品规格当前选择
const getActivityGoodsSpec: any = computed(() => {
    const checkedSpec = spec.goodsOptionsSpec
    const activitySpec = props.activitySpec
    const temp = activitySpec.find((item: any) => item.skuId == checkedSpec.id)
    return temp || (activitySpec.length >= 1 ? activitySpec[0] : {})
})

// 获取当前规格价格
const getSpecPrice = computed(() => {
    return (priceKey: string) => {
        return (
            getActivityGoodsSpec.value[priceKey] ||
            spec.goodsOptionsSpec[priceKey] ||
            props.defaultInfo.price
        )
    }
})

const skuCheckedText = computed(() => {
    const sku_text = spec.checkedList
        .map(({ id, name }: any) => (!id ? name : ''))
        .filter(Boolean)
        .join(', ')
    if (spec.goodsOptionsSpec?.stock || sku_text) {
        const prefix = sku_text ? '请选择 ' : '已选择 '
        return `${prefix}${sku_text}${spec.goodsOptionsSpec.skuValueArr ?? ''} ${spec.goodsTotal
            } 件`
    } else {
        return '当前商品规格库存不足'
    }
})

const handleOpen = ({ priceKey = 'price' }: any) => {
    spec.show = true
    spec.priceKey = priceKey
}

const handleClose = () => {
    spec.show = false
}

// 选择规格
const changeSpecOption = (index: number, value: number | string) => {
    const { id } = spec.checkedList[index]
    const curSku = props.specList[index].specList.find((item: any) => item.id == value)
    if (!curSku) {
        return uni.$u.toast('规格数据有误～')
    }
    if (id == value) {
        value = ''
    }
    spec.checkedList[index].id = value
    spec.checkedList[index].value = curSku.value
}

const handleGoodsNumber = async (event: any, num: number) => {
    if (event.value > num) {
        await nextTick()
        spec.goodsTotal = num
    }
}

// 点击按钮
const onButtonHandle = (event: string) => {
    if (spec.goodsTotal > spec.goodsOptionsSpec.stock) {
        return uni.$u.toast('超出库存数量')
    }
    const nullSelect = spec.checkedList.find((item: any) => !item.id)
    if (nullSelect) return uni.$u.toast(`请选择${nullSelect.name}`)
    handleClose()
    throwSpecEmit('buttonsHandle', event)
}

// 查看商品图片
const previewGoodsImage = () => {
    const image = spec.goodsOptionsSpec.image ?? props.defaultInfo.image
    uni.previewImage({ urls: [image] })
}

// 抛出事件
const throwSpecEmit = (name: string, event: string | 'buttons' | 'change') => {
    const checkedSpec = spec.goodsOptionsSpec
    emit(name, {
        event: event + 'Handle',
        spec: { ...checkedSpec, number: spec.goodsTotal },
        activitySpec: getActivityGoodsSpec.value
    })
}

const checkStock = () => {
    const nullStock = props.specMap.filter((item: any) => item.stock == 0)
    spec.nullSpec = nullStock.reduce((prev: any, item: any) => {
        const idsArr = item.skuValueIds.split(',')
        const checkIdsArr = unref(specCheckedIds).split(',')
        const includesIds = idsArr.filter((item: any) => unref(specCheckedIds).includes(item))
        if (!unref(specCheckedIds)) return prev
        // 全选
        if (item.skuValueIds == unref(specCheckedIds)) {
            return prev.concat(...checkIdsArr)
        }
        // 剩下一个未选
        if (includesIds.length && includesIds.length == spec.checkedList.length - 1) {
            return prev.concat(idsArr.filter((item: any) => !unref(specCheckedIds).includes(item)))
        }
        return prev
    }, [])
}

// 监听规格
watch(
    () => spec.checkedList,
    (value) => {
        const selectSepc = spec.mapTable.get(unref(specCheckedIds))
        checkStock()
        if (selectSepc) {
            spec.goodsOptionsSpec = selectSepc
            throwSpecEmit('change', 'change')
        } else {
            spec.goodsOptionsSpec = {}
        }
    },
    { deep: true }
)

// 初始化规格映射表
watch(
    () => props.specMap,
    (value) => {
        if (!value?.length) return
        // 配置隐射关系spec_value_ids => self
        value.map((item: any) => spec.mapTable.set(item.skuValueIds, item))
        // 拿到最小价格
        const minPrice: any = Math.min.apply(
            Math,
            value.map((item: any) => {
                return item.price
            })
        )
        const checkItem: any = value.find((item: any) => item.stock && minPrice == item.price)
        const checkItemIds = checkItem ? checkItem.skuValueIds.split(',') : ''
        const checkItemArr = checkItem ? checkItem.skuValueArr.split(',') : ''

        //初始化选中数据
        spec.checkedList = props.specList.map((item: any, index: number) => ({
            name: item.name,
            id: checkItemIds[index] || item.specList[0].id,
            value: checkItemArr[index] || item.specList[0].value
        }))
        console.log(spec.checkedList)
    },
    { immediate: true }
)

defineExpose({
    handleOpen,
    handleClose
})
</script>

<style lang="scss" scoped>
.header {
    display: flex;
    padding: 20rpx;

    &-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        margin-left: 20rpx;

        &-label {
            padding-top: 10rpx;
            font-size: 26rpx;
        }

        &-price {
            margin-bottom: 20rpx;
        }
    }
}

.main {
    padding: 20rpx;
    // border-top: $-solid-border;

    .scroll-spec {
        min-height: 520rpx;
        max-height: 800rpx;
    }

    .spec {
        padding: 10rpx 0;

        &-title {
            font-size: 28rpx;
        }

        &-content {
            display: flex;
            flex-wrap: wrap;
            padding-top: 20rpx;
        }

        &-item {
            display: flex;
            align-items: center;
            justify-content: center;
            min-width: 100rpx;
            box-sizing: border-box;
            padding: 8rpx 25rpx;
            margin: 0 20rpx 20rpx 0;
            border-radius: 60px;
            font-size: 26rpx;
            background-color: #f4f4f4;

            &--active {
                color: $u-type-primary;
                background-color: rgba($color: $u-type-primary, $alpha: 0.1);
                border: 2rpx $u-type-primary solid;
            }

            &--disabled {
                position: relative;

                &::after {
                    content: '缺货';
                    position: absolute;
                    right: -20rpx;
                    top: -24rpx;
                    color: #ffffff;
                    width: 70rpx;
                    height: 36rpx;
                    text-align: center;
                    line-height: 36rpx;
                    font-size: 22rpx;
                    border-radius: 20rpx 20rpx 20rpx 0;
                    background-color: $u-type-primary;
                }
            }
        }
    }

    .total {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20rpx 0;

        &-label {
            font-size: 28rpx;
        }
    }
}

.operation {
    display: flex;
    justify-content: space-between;
    padding: 10rpx 20rpx;

    &-button {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        height: 82rpx;
        border-radius: 60px;
        font-size: 30rpx;
        color: #ffffff;
        background-color: $u-type-primary;

        &:nth-child(n + 2) {
            margin-left: 20rpx;
        }

        &.disabled {
            background-color: #d7d7d7;
        }
    }
}
</style>
