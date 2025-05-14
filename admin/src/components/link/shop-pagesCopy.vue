<template>
    <div class="shop-pages">
        <div class="flex flex-wrap link-list">
            <div
                class="link-item border border-br px-5 py-[5px] rounded-[3px] cursor-pointer mr-[10px] mb-[10px]"
                v-for="(item, index) in linkList"
                :class="{
                    'border-primary text-primary':
                        modelValue.path == item.path && modelValue.name == item.name
                }"
                :key="index"
                @click="handleSelect(item)"
            >
                {{ item.name }}
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import type { PropType } from 'vue'
import { LinkTypeEnum, type Link } from '.'

defineProps({
    modelValue: {
        type: Object as PropType<Link>,
        default: () => ({})
    }
})
const emit = defineEmits<{
    (event: 'update:modelValue', value: Link): void
}>()

const linkList = ref([
    {
        path: '/pages/index/index',
        name: '商城首页',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/store_street/store_street',
        name: '店铺街',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/store_settled/store_settled',
        name: '店铺入驻',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/news/news',
        name: '文章资讯',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/pages/user/user',
        name: '个人中心',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/collection/collection',
        name: '我的收藏',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/customer_service/customer_service',
        name: '联系客服',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/user_set/user_set',
        name: '个人设置',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/as_us/as_us',
        name: '关于我们',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/user_data/user_data',
        name: '个人资料',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/agreement/agreement',
        name: '隐私政策',
        query: {
            type: 'privacy'
        },
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/agreement/agreement',
        name: '服务协议',
        query: {
            type: 'service'
        },
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/pages/search/search',
        name: '搜索页面',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/order_list/order_list',
        name: '我的订单',
        query: {
            type: ''
        },
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/user_address/user_address',
        name: '收货地址',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/user_wallet/user_wallet',
        name: '我的钱包',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/after_sales/after_sales',
        name: '售后退款',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/pages/category/category',
        name: '分类页面',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/pages/shop_cart/shop_cart',
        name: '购物车',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/goods_seckill/goods_seckill',
        name: '限时秒杀',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/goods_team/goods_team',
        name: '拼团活动',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/distribution/distribution',
        name: '分销推广'
    },
    {
        path: '/packageA/pages/coupon_center/coupon_center',
        name: '领券中心',
        type: LinkTypeEnum.SHOP_PAGES
    },
    {
        path: '/packageA/pages/coupon_list/coupon_list',
        name: '我的优惠券',
        type: LinkTypeEnum.SHOP_PAGES
    }
])

const handleSelect = (value: Link) => {
    emit('update:modelValue', value)
}
</script>
