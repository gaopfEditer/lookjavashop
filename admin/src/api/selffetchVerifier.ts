import request from '@/utils/request'

// 核销员列表
export function selffetchVerifierLists(params?: Record<string, any>) {
    return request.get({ url: '/selffetchVerifier/list', params })
}

// 核销员详情
export function selffetchVerifierDetail(params: Record<string, any>) {
    return request.get({ url: '/selffetchVerifier/detail', params })
}

// 核销员新增
export function selffetchVerifierAdd(params: Record<string, any>) {
    return request.post({ url: '/selffetchVerifier/add', params })
}

// 核销员编辑
export function selffetchVerifierEdit(params: Record<string, any>) {
    return request.post({ url: '/selffetchVerifier/edit', params })
}

// 核销员删除
export function selffetchVerifierDelete(params: Record<string, any>) {
    return request.post({ url: '/selffetchVerifier/del', params })
}

// 核销员状态修改
export function apiSelffetchVerifierStatus(params: Record<string, any>) {
    return request.post({ url: '/selffetchVerifier/status', params })
}
