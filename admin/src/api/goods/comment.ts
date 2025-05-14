import request from '@/utils/request'

/**
 * @return { Promise }
 * @description 获取评价列表
 */
export function commentLists(params: Record<any, string>) {
    return request.get({ url: '/goods/comment/list', params })
}

/**
 * @return { Promise }
 * @description 评价显示
 * @param params
 */
export function commentChange(params: { id: number }) {
    return request.post({ url: '/goods/comment/change', params })
}

/**
 * @return { Promise }
 * @description 审核售后
 * @param data
 */
export function commentReply(data: { id: number, reply_content?: string }) {
    return request.post({ url: '/goods/comment/reply', data })
}