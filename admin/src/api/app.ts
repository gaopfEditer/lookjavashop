import request from '@/utils/request'

// 配置
export function getConfig() {
    return request.get({ url: '/index/config' })
}

// 工作台主页
export function getWorkbench() {
    return request.get({ url: '/index/console' })
}
// 正版检测
export function apiCheckLegal(params:any) {
    return request.get({ url: '/index/check',params })
}
