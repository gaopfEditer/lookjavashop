import request from '@/utils/request'
import type { OrderQueryParamsType, DeliveryGoodsFormType } from "./selffetchorder.d"

/**
 * @return { Promise }
 * @description 获取订单列表
 * @param { DeliveryGoodsFormType } params
 */
export function orderLists(params: OrderQueryParamsType) {
    return request.get({ url: '/order/selffetch/list', params })
}

/**
 * @return { Promise }
 * @description 获取订单x详情
 * @param params
 */
export function orderDetail(params: { id: number }) {
    return request.get({ url: '/order/selffetch/detail', params })
}

/**
 * @return { Promise }
 * @param { DeliveryGoodsFormType } data
 * @description 发货
 */
export function orderDelivery(data: DeliveryGoodsFormType) {
    return request.post({ url: '/order/selffetch/sendDelivery', data })
}

/**
 * @return { Promise }
 * @description 查看物流
 * @param params
 */
export function orderLogistics(params: { id: number }) {
    return request.get({ url: '/order/selffetch/logistics', params })
}

/**
 * @return { Promise }
 * @description 取消订单
 * @param data
 */
export function orderCancel(data: { id: number }) {
    return request.post({ url: '/order/selffetch/cancel', data })
}

/**
 * @return { Promise }
 * @description 确认订单
 * @param data
 */
export function orderTakeDelivery(data: { id: number }) {
    return request.post({ url: '/order/selffetch/takeDelivery', data })
}

/**
 * @return { Promise }
 * @description 设置商家备注
 * @param data
 */
export function orderRemark(data: { id: number; remarks: string }) {
    return request.post({ url: '/order/selffetch/remarks', data })
}

/**
 * @return { Promise }
 * @description 设置商家备注
 * @param data
 */
export function orderSelffetchVerify(data: { id: number; items: string }) {
    return request.post({ url: '/order/selffetch/verify', data })
}


/**
 * @return { Promise }
 * @description 导出订单列表
 * @param { DeliveryGoodsFormType } params
 */
export function orderExportExcel(params: OrderQueryParamsType) {
    return request.get({
        url: '/order/selffetch/exportExcel',
        params,
        responseType: 'blob'
    })
}


