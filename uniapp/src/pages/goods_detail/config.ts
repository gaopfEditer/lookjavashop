import { GoodsTypeEnum } from '@/enums/goodsEnums'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

export enum SubmitEventEnum {
    CONFIRM = 'confirm',
    CART = 'cart',
    BUY = 'buy',
    BUY_NOW = 'buyNow',
    // 处理函数
    CART_HANDLE = 'cartHandle',
    BUY_HANDLE = 'buyHandle',
    BUY_NOW_HANDLE = 'buyNowHandle'
}

// 定义修改规格确认按钮
export const confirmSpecButtons = {
    event: SubmitEventEnum.CONFIRM,
    style: {
        'background-color': themeStore.primaryColor
    },
    text: '确认'
}

// 定义按钮组
export const changeSpecButtons = {
    [GoodsTypeEnum.ORDINARY]: {
        [SubmitEventEnum.CART]: {
            event: SubmitEventEnum.CART,
            style: {
                'background-color': 'var(--theme-light-color)',
                color: 'var(--theme-color)'
            },
            text: '加入购物车'
        },
        [SubmitEventEnum.BUY]: {
            event: SubmitEventEnum.BUY,
            style: {
                'background-color': themeStore.primaryColor
            },
            text: '立即购买'
        }
    },
    [GoodsTypeEnum.SECKILL]: {
        [SubmitEventEnum.BUY]: {
            event: SubmitEventEnum.BUY,
            style: {
                'background-color': 'var(--theme-light-color)',
                color: 'var(--theme-color)'
            },
            text: '原价购买'
        },
        [SubmitEventEnum.BUY_NOW]: {
            event: SubmitEventEnum.BUY_NOW,
            style: {
                'background-color': themeStore.primaryColor
            },
            text: '立即抢购'
        }
    }
}
