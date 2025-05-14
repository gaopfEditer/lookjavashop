import request from '@/utils/request'

/**
 * @return { Promise }
 * @description 商品分类
 */
export function goodsCategory() {
    return request.get({ url: '/goods/category' })
}

/**
 * @return { Promise }
 * @description 商品列表
 * @param data
 */
export function getGoodsLists(data: any) {
    return request.get({ url: '/goods/list', data })
}

/**
 * @return { Promise }
 * @description 商品列表(限定，非分页)
 * @param data
 */
export function getGoodsLimitLists(data: any) {
    return request.get({ url: '/goods/limit', data })
}

/*** Start 商品详情相关 ***/

/**
 * @return { Promise }
 * @description 商品详情
 * @param data
 */
export function getGoodsDetail(data: { id: number }) {
    return request.get({ url: '/goods/detail', data })
}

/**
 * @return { Promise }
 * @description 商品收藏
 * @param data
 */
export function goodsCollect(data: { id: number }) {
    return request.post({ url: '/goods/collect', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 商品收藏列表
 * @param data
 */
export function getGoodsCollectLists(data: { pageNo: number, pageSize: number }) {
    return request.get({ url: '/goods/collectList', data })
}

/*** End 商品详情相关 ***/


/*** Start 购物车相关 ***/

/**
 * @return { Promise }
 * @description 获取购物车列表
 */
export function shoppingCartLists() {
    return request.get({ url: '/cart/list' })
}

/**
 * @return { Promise }
 * @description 获取购物车数量
 */
export function shoppingCartCount() {
    return request.get({ url: '/cart/count' })
}

/**
 * @return { Promise }
 * @description 添加购物车
 * @param data
 */
export function shoppingCartAdd(data: { goodsSkuId: number, num: number }) {
    return request.post({ url: '/cart/add', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 购物车改变数量
 * @param data
 */
export function shoppingCartChange(data: { id: number, num: number }) {
    return request.post({ url: '/cart/changeNum', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 购物车选择
 * @param data
 */
export function shoppingCartSelect(data: { ids: number[], isSelected: number }) {
    return request.post({ url: '/cart/select', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 商品规格修改
 * @param data
 */
export function shoppingCartChangeSku(data: { id?: number, goodsSkuId: number, num?: number }) {
    return request.post({ url: '/cart/changeSku', data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 购物车删除
 * @param data
 */
export function shoppingCartDel(data: { ids: number[] }) {
    return request.post({ url: '/cart/del', data }, { isAuth: true })
}

/*** End 购物车相关 ***/