import request from '@/utils/request'
import type { AfterSaleQueryParamsType } from "./after_sale.d"

/**
 * @return { Promise }
 * @param { AfterSaleQueryParamsType } params
 * @description 获取售后列表
 */
export function afterSaleLists(params: AfterSaleQueryParamsType) {
    return request.get({ url: '/order/after/list', params })
}

/**
 * @return { Promise }
 * @description 获取售后详情
 * @param params
 */
export function afterSaleDetail(params: { id: number }) {
    return request.get({ url: '/order/after/detail', params })
}

/**
 * @return { Promise }
 * @description 同意售后
 * @param data
 */
export function afterSaleAgree(data: { id: number }) {
    return request.post({ url: '/order/after/agree', data })
}

/**
 * @return { Promise }
 * @description 拒绝售后
 * @param data
 */
export function afterSaleRefuse(data: { id: number; remarks: string }) {
    return request.post({ url: '/order/after/refuse', data })
}

/**
 * @return { Promise }
 * @description 确认收货
 * @param data
 */
export function afterSaleConfirmGoods(data: { id: number }) {
    return request.post({ url: '/order/after/confirmGoods', data })
}

/**
 * @return { Promise }
 * @description 拒绝收货
 * @param data
 */
export function afterSaleRefuseGoods(data: { id: number }) {
    return request.post({ url: '/order/after/refuseGoods', data })
}

/**
 * @return { Promise }
 * @description 同意退款
 * @param data
 */
export function afterSaleAgreeRefund(data: { id: number }) {
    return request.post({ url: '/order/after/agreeRefund', data })
}

/**
 * @return { Promise }
 * @description 拒绝退款
 * @param data
 */
export function afterSaleRefuseRefund(data: { id: number }) {
    return request.post({ url: '/order/after/refuseRefund', data })
}

/**
 * @return { Promise }
 * @description 确认退款
 * @param data
 */
export function afterSaleConfirmRefund(data: { id: number }) {
    return request.post({ url: '/order/after/confirmRefund', data })
}