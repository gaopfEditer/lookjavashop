import request from '@/utils/request'

// 分销等级列表
export function levelList(params?: any) {
    return request.get({ url: '/distribution/level/list', params })
}

// 分销等级详情
export function levelDetail(params?: any) {
    return request.get({ url: '/distribution/level/detail', params })
}

// 分销等级删除
export function levelDel(params?: any) {
    return request.post({ url: '/distribution/level/del', params })
}
// 分销等级新增
export function levelAdd(params?: any) {
    return request.post({ url: '/distribution/level/add', params })
}
// 分销等级编辑
export function levelEdit(params?: any) {
    return request.post({ url: '/distribution/level/edit', params })
}
