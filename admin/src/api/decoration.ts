import request from '@/utils/request'

// 页面装修详情
export function getDecoratePages(params: any) {
    return request.get({ url: '/decorate/pages/detail', params }, { ignoreCancelToken: true })
}

// 页面装修保存
export function setDecoratePages(params: any) {
    return request.post({ url: '/decorate/pages/save', params })
}

// 获取首页文章数据
export function getDecorateArticle(params?: any) {
    return request.get({ url: '/decorate/data/article', params })
}

// 获取装修商品
export function decorateGoodsLists(params?: { type: string }) {
    return request.get({ url: '/decorate/data/goods', params }, { ignoreCancelToken: true })
}

// 获取装修优惠券列表
export function decorateCouponLists() {
    return request.get({ url: '/decorate/data/coupon' }, { ignoreCancelToken: true })
}

// 获取装修秒杀列表
export function decorateSeckillLists() {
    return request.get({ url: '/decorate/data/seckill' }, { ignoreCancelToken: true })
}

// 底部导航详情
export function getDecorateTabbar(params?: any) {
    return request.get({ url: '/decorate/tabbar/detail', params })
}

// 底部导航保存
export function setDecorateTabbar(params: any) {
    return request.post({ url: '/decorate/tabbar/save', params })
}
