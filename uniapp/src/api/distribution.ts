import request from '@/utils/request'

//我的分销信息
export function Mydistribution(data?: any) {
    //     return request.get({ url: '/distribution/applyDetail' })
}

//申请分销
export function applyDistribution(data?: any) {
    return request.post({ url: '/distribution/apply', data })
}

//绑定关系
export function bindDistributionCode(data?: any) {
    return request.post({ url: '/distribution/bind', data })
}

//申请详情
export function applyDetial(data?: any) {
    return request.get({ url: '/distribution/applyDetail', data })
}
//分销首页/
export function distributionIndex(data?: any) {
    return request.get({ url: '/distribution/index', data })
}
//粉丝列表
export function getMyFansList(data?: any) {
    return request.get({ url: '/distribution/fans', data })
}
//订单列表
export function getOrderList(data?: any) {
    return request.get({ url: '/distribution/order', data })
}
