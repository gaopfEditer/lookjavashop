import request from '@/utils/request'

// 获取分销商品列表
export function getDistributionGoods(params: any) {
    return request.get({ url: '/distribution/goods/list', params })
}

// 获取分销商品详情
export function getGoodsDetail(params: any) {
    return request.get({ url: '/distribution/goods/detail', params })
}

//商品参与/不参与分销
export function postChangStatus(params: any) {
    return request.post({ url: '/distribution/goods/join', params })
}

//设置分销佣金
export function distributionSave(params: any) {
    return request.post({ url: '/distribution/goods/set', params })
}
//分销设置
export function getconfig(params?: any) {
    return request.get({ url: '/distribution/config/detail', params })
}

export function Saveconfig(data?: any) {
    return request.post({ url: '/distribution/config/save', data })
}
//分销概览
export function getsurveydata(params?: any) {
    return request.get({ url: '/distribution/survey/data', params })
}
//收入排行
export function surveytopEarnings(params?: any) {
    return request.get({ url: '/distribution/survey/topEarnings', params })
}
//粉丝排行
export function surveytopFans(params?: any) {
    return request.get({ url: '/distribution/survey/topFans', params })
}
//分销订单
export function getorder(params?: any) {
    return request.get({ url: '/distribution/order/list', params })
}
