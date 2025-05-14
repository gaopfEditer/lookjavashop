import request from '@/utils/request'
// @ts-ignore
import type { GoodsCategoryFormType } from "/category.d"

/**
 * @return { Promise }
 * @description 商品分类列表
 */
export function goodsCategoryLists() {
    return request.get({ url: '/goods/goodsCategory/list' }, { ignoreCancelToken: true })
}
/**
 * @return { Promise }
 * @description 商品分类列表
 */
export function goodsCategoryListsNew() {
    return request.get({ url: '/goodsNew/category/list' }, { ignoreCancelToken: true })
}

/**
 * @return { Promise }
 * @description 商品分类详情
 * @param params
 */
export function goodsCategoryDetail(params: { id: number }) {
    return request.get({ url: '/goods/goodsCategory/detail', params })
}

/**
 * @return { Promise }
 * @param { GoodsCategoryFormType } params
 * @description 商品分类添加
 */
export function goodsCategoryAdd(params: GoodsCategoryFormType) {
    return request.post({ url: '/goods/goodsCategory/add', params })
}

/**
 * @return { Promise }
 * @param { GoodsCategoryFormType } params
 * @description 商品分类编辑
 */
export function goodsCategoryEdit(params: GoodsCategoryFormType) {
    return request.post({ url: '/goods/goodsCategory/edit', params })
}

/**
 * @return { Promise }
 * @param params
 * @description 商品分类删除
 */
export function goodsCategoryDel(params: { id: number }) {
    return request.post({ url: '/goods/goodsCategory/del', params })
}

/**
 * @return { Promise }
 * @param params
 * @description 商品分类状态
 */
export function goodsCategoryChange(params: { id: number }) {
    return request.post({ url: '/goods/goodsCategory/change', params })
}