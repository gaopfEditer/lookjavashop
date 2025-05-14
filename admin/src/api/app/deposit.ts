import request from '@/utils/request'
import type { DepositFormType } from "./deposit.d"

/**
 * @return { Promise }
 * @description 获取充值信息
 */
export function getDeposit() {
    return request.get({ url: '/marketing/recharge/detail' })
}

/**
 * @return { Promise }
 * @param { DepositFormType } params
 * @description 设置充值信息
 */
export function setDeposit(params: DepositFormType) {
    return request.post({ url: '/marketing/recharge/save', params })
}
