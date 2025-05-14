import request from '@/utils/request'

/**
 * @return { Promise }
 * @description 获取支付方式
 */
export function getPayWay() {
    return request.get({ url: '/setting/payment/method' })
}

/**
 * @return { Promise }
 * @param { any } params
 * @description 设置支付方式
 */
export function setPayWay(params: any) {
    return request.post({ url: '/setting/payment/editMethod', params })
}

/**
 * @return { Promise }
 * @description 获取支付方式
 */
export function getPayConfigLists() {
    return request.get({ url: '/setting/payment/list' })
}

/**
 * @return { Promise }
 * @param { any } params
 * @description 设置支付方式
 */
export function setPayConfig(params: any) {
    return request.post({ url: '/setting/payment/editConfig', params })
}

/**
 * @return { Promise }
 * @param { any } params
 * @description 设置支付方式
 */
export function getPayConfig(params: any) {
    return request.get({ url: '/setting/payment/detail', params })
}
