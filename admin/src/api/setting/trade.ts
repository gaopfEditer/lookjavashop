import request from '@/utils/request'

/**
 * @return { Promise }
 * @description 获取交易设置
 */
export function getTradeConfig() {
    return request.get({ url: '/setting/trade/detail' })
}

export type TradeFormType = {
    [index: string]: string | number
    cancelUnpaidOrderTime: number	    // integer 是	系统自动取消订单: -1=关闭,否则开启(天)
    cancelUnshippedOrderTime: number|string    // integer 是	订单允许取消时长: -1=关闭,否则开启(分钟)
    autoConfirmReceiptDay: number       // integer 否	订单自动完成时长: -1=关闭,否则开启(天)
    afterSalesDay: number               // integer 是   买家售后维权时效: -1=关闭,否则开启(天)
    inventoryOccupancy: number          // string  是   库存占用时机: 1=订单提交占用
    returnInventory: number             // string  是	取消订单退回库存: 0=否,1=是
}
/**
 * @return { Promise }
 * @param { TradeFormType } params
 * @description 设置交易设置
 */
export function setTradeConfig(params: TradeFormType) {
    return request.post({ url: '/setting/trade/save', params })
}