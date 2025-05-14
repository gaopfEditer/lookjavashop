import request from '@/utils/request'
import type { ApplyWithdrawFormType } from "./withdraw.d"

/**
 * @return { Promise }
 * @description 提现列表
 * @param data
 */
export function userWithdrawList(data: { page_no: number; page_size: number }) {
    return request.get({ url: '/withdraw/list', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 提现配置
 */
export function userWithdrawConfig() {
    return request.get({ url: '/withdraw/config' }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 申请提现
 * @param data
 */
export function userWithdrawApply(data: ApplyWithdrawFormType) {
    return request.post({ url: '/withdraw/apply', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 提现详情
 * @param data
 */
export function userWithdrawDetail(data: { id: number }) {
    return request.get({ url: '/withdraw/detail', data }, { isAuth: true })
}
