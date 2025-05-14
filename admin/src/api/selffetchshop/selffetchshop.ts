import request from '@/utils/request'

// 自提门店列表
export function selffetchshopLists(params?: Record<string, any>) {
    return request.get({ url: '/selffetchshop/list', params })
}

// 自提门店详情
export function selffetchshopDetail(params: Record<string, any>) {
    return request.get({ url: '/selffetchshop/detail', params })
}

// 自提门店新增
export function selffetchshopAdd(params: Record<string, any>) {
    return request.post({ url: '/selffetchshop/add', params })
}

// 自提门店编辑
export function selffetchshopEdit(params: Record<string, any>) {
    return request.post({ url: '/selffetchshop/edit', params })
}

// 自提门店删除
export function selffetchshopDelete(params: Record<string, any>) {
    return request.post({ url: '/selffetchshop/del', params })
}

// 搜索地址
export function apiMapRegionSearch(params: Record<string, any>) {
    return request.get({ url: '/selffetchshop/regionSearch', params })
}

// 自提门店状态修改
export function apiSelffetchShopStatus(params: Record<string, any>) {
    return request.post({ url: '/selffetchshop/status', params })
}


