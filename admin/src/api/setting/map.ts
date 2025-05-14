import request from '@/utils/request'


// 获取地图配置
export function apiMapGet(params?: any) {
    return request.get({ url: '/setting/map/detail', params })
}

export function apiMapSet(params?: any) {
    return request.post({ url: '/setting/map/save', params })
}