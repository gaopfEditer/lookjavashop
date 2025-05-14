import request from '@/utils/request'

// 财务中心
export function financeCenterCount() {
    return request.get({ url: '/finance/center/data ' })
}

// 余额明细
export function balanceLists(params: any) {
    return request.get({ url: '/finance/wallet/list', params })
}

// 充值记录
export function depositLists(params: any) {
    return request.get({ url: '/finance/recharger/list', params })
}

//退款
export function refund(params?: any) {
    return request.post({ url: '/finance/recharger/refund', params })
}

//重新退款
export function refundAgain(params?: any) {
    return request.post({ url: '/finance/recharger/refundAgain', params })
}

//退款记录
export function refundRecord(params?: any) {
    return request.get({ url: '/finance/refund/list', params })
}

//退款日志
export function refundLog(params?: any) {
    return request.get({ url: '/finance/refund/log', params })
}

//佣金明细
export function commissionDetail(params?: any) {
    return request.get({ url: '/finance/earnings/list', params })
}
//提现记录
export function getWithdrawLists(params?: any) {
    return request.get({ url: '/finance/withdraw/list', params })
}
//审核
export function apiWithdrawAudit(params?: any) {
    return request.post({ url: '/finance/withdraw/audit', params })
}
//转账WithDrawTransferType
export function WithDrawTransfer(params?: any) {
    return request.post({ url: '/finance/withdraw/transfer', params })
}
//提现详情getWithdrawDetail
export function getWithdrawDetail(params?: any) {
    return request.get({ url: '/finance/withdraw/detail', params })
}
//查询结果
export function withdrawquery(params?: any) {
    return request.get({ url: '/finance/withdraw/query', params })
}
