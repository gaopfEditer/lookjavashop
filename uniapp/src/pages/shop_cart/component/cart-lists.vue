<template>
    <u-swipe-action
        :show="item.show"
        :index="index"
        v-for="(item, index) in lists"
        :key="item.id"
        @click="handleDel"
        :options="options"
        btn-width="140"
        bg-color="none"
        class="mt-[20rpx]"
    >
        <view
            class="bg-white w-[710rpx] rounded-[14rpx] px-[20rpx] pt-[30rpx] pb-[20rpx] flex items-center"
        >
            <view
                class="mr-[20rpx]"
                @click.stop="handleSelect(item.id, item.cartStatus, item.selected)"
            >
                <l-checkbox
                    :checked="item.selected"
                    v-show="!item.cartStatus"
                    label=""
                    :true-label="1"
                    :false-label="0"
                    :active-color="$theme.primaryColor"
                />
                <image
                    v-show="item.cartStatus"
                    class="w-[34rpx] h-[34rpx]"
                    src="@/static/images/icon/icon_no_select.png"
                />
            </view>
            <!-- 商品图片 -->
            <u-image
                width="160"
                height="160"
                :src="item.image"
                @click="gotoGoodsDetail(item.goodsId)"
            ></u-image>

            <!-- 商品信息 -->
            <view class="ml-[20rpx] flex-1">
                <view
                    class="line-2 text-base text-main"
                    @click.stop="gotoGoodsDetail(item.goodsId)"
                    >{{ item.name }}</view
                >
                <view class="mt-[10rpx]">
                    <view class="flex items-center text-xs">
                        <view
                            v-if="item.cartStatus == 3 || item?.specType == 2"
                            class="text-muted bg-[#F4F4F4] rounded-[26px] px-[16rpx] py-[6rpx] inline-block"
                            @click.stop="
                                handleShowSku(item.goodsId, item.cartStatus, item.id, item.num)
                            "
                        >
                            <text v-if="item.cartStatus != 3">
                                {{ item?.skuValueArr?.replace(/,/g, '&nbsp;') }}
                            </text>
                            <text v-else class="text-main"> 请重新选择规格 </text>
                            <u-icon name="arrow-down" size="22rpx" class="ml-[10rpx]"></u-icon>
                        </view>
                        <text v-if="!item?.stock" class="text-error ml-2">库存不足</text>
                    </view>
                    <view
                        v-if="item.cartStatus != 3"
                        class="flex flex-1 justify-between mt-[10rpx] number-box"
                    >
                        <price
                            :content="item?.price"
                            main-size="32rpx"
                            minor-size="24rpx"
                            fontWeight="500"
                        />
                        <u-number-box
                            v-model="item.num"
                            :min="1"
                            :max="item?.stock"
                            :disabled="item.cartStatus != 0"
                            @blur="handleCountChange($event, item.id, item.stock)"
                            @minus="handleCountChange($event, item.id, item.stock)"
                            @plus="handleCountChange($event, item.id, item.stock)"
                            bg-color="#F2F3F5"
                            color="#333333"
                            size="18"
                        />
                    </view>
                </view>
            </view>
        </view>
    </u-swipe-action>

    <!-- 商品规格 -->
    <goods-spec
        ref="goodsSpecRef"
        :defaultInfo="{
            image: goods.goodsImage[0],
            price: goods.sellPrice,
            stock: goods.totalStock,
            unit: goods.unit_name
        }"
        :buttons="[goods.buttons]"
        :showGoodsNum="false"
        :spec-list="goods.specValue"
        :spec-map="goods.specValueList"
        @buttonsHandle="handleConfirm"
    />
</template>

<script lang="ts" setup>
import {
    getGoodsDetail,
    shoppingCartChange,
    shoppingCartSelect,
    shoppingCartDel,
    shoppingCartChangeSku
} from '@/api/goods'
import { reactive, shallowRef, PropType, ref, unref } from 'vue'
import { useRouter } from 'uniapp-router-next'
const router = useRouter()

const emit = defineEmits<{
    (event: 'change', value: string): void
    (event: 'update', value: void): void
}>()

const props = defineProps({
    lists: {
        type: Array as PropType<any[]>,
        // eslint-disable-next-line vue/require-valid-default-prop
        default: []
    }
})

const goodsSpecRef = shallowRef()
const current_cart_id = ref<number>()
const current_cart_num = ref<number>()
const options = [
    {
        text: '删除',
        style: {
            color: '#FFFFFF',
            backgroundColor: 'var(--theme-color)'
        }
    }
]
const goods = reactive<any>({
    goodsImage: [''],
    sellPrice: '',
    totalStock: '',
    unit_name: '',
    buttons: {
        event: 'confirm',
        text: '确认'
    },
    specValue: [],
    specValueList: []
})

const handleShowSku = async (goods_id: number, status: number, cart_id: number, num: number) => {
    try {
        if (status === 1 || status == 2) return
        current_cart_id.value = cart_id
        current_cart_num.value = num
        const data = await getGoodsDetail({ id: goods_id })
        Reflect.ownKeys(goods).map((item: any) => {
            if (data[item] === undefined) return
            goods[item] = data[item]
        })
        goodsSpecRef.value?.handleOpen({ priceKey: 'price' })
    } catch (error) {
        console.log('获取商品详情接口', error)
    }
}

// 改变规格
const handleConfirm = async (event: any) => {
    try {
        await shoppingCartChangeSku({
            id: unref(current_cart_id),
            num: unref(current_cart_num),
            goodsSkuId: event.spec.id
        })
        // 改变后重新获取购物车
        emit('update')
    } catch (error) {
        console.log('改变规格', error)
    }
}

const handleSelect = async (cart_id: number, status: number, selected: number) => {
    try {
        if (status) return
        await shoppingCartSelect({ ids: [cart_id], isSelected: selected ? 0 : 1 })
        // 改变后重新获取购物车
        emit('update')
    } catch (error) {
        console.log('选中购物车', error)
    }
}

const handleCountChange = async (event: any, cart_id: number, stock: number) => {
    try {
        if (event.value >= stock) return
        await shoppingCartChange({ id: cart_id, num: event.value })
        // 改变后重新获取购物车
        emit('update')
    } catch (error) {
        console.log('改变购物车数量', error)
    }
}

const gotoGoodsDetail = (id: number) => {
    router.navigateTo({
        path: `/pages/goods_detail/goods_detail`,
        query: {
            id: id
        }
    })
}

const handleDel = async (index: number) => {
    try {
        const ids: number = props.lists[index].id
        await shoppingCartDel({ ids: [ids] })
        // 改变后重新获取购物车
        emit('update')
    } catch (error) {
        console.log('全选', error)
    }
}
</script>
