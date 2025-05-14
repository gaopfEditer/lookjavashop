import request from '@/utils/request'

/**
 * @return { Promise }
 * @description 领券中心
 * @param data
 */
export function seckillLists(data?: { pageNo: number; pageSize: number }) {
    return request.get({ url: '/seckill/list', data })
}

/**
 * @return { Promise }
 * @description 获取秒杀商品
 * @param data
 */
export function seckillDetaill(data: { id: number | string }) {
    return request.get({ url: '/seckill/detail', data })
}
