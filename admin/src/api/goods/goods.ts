import request from "@/utils/request";
import type { GoodsFormParamsType, GoodsListQueryParamsType } from "./goods.d";

/**
 * @return { Promise }
 * @param { GoodsListQueryParamsType } params
 * @description 商品列表
 */
export function goodsLists(params: GoodsListQueryParamsType) {
  return request.get(
    { url: "/goods/product/list", params },
    { ignoreCancelToken: true }
  );
}
/**
 * @return { Promise }
 * @param { GoodsListQueryParamsType } params
 * @description 商品列表
 */
export function goodsNewLists(params: GoodsListQueryParamsType) {
  return request.get(
    { url: "/goodsNew/product/list", params },
    { ignoreCancelToken: true }
  );
}

/**
 * @return { Promise }
 * @param  params
 * @description 商品排序
 */
export function goodsSort(params: { id: number; sort: number }) {
  return request.post({ url: "/goods/product/sort", params });
}

/**
 * @return { Promise }
 * @param { GoodsListQueryParamsType } params
 * @description 商品公共列表
 */
export function goodsCommonLists(params: GoodsListQueryParamsType) {
  return request.get({ url: "/goods/product/common", params });
}

/**
 * @return { Promise }
 * @description 商品详情
 * @param params
 */
export function getGoodsDetail(params: { id: number }) {
  return request.get({ url: "/goods/product/detail", params });
}

/**
 * @return { Promise }
 * @param { GoodsFormParamsType } params
 * @description 商品添加
 */
export function goodsAdd(params: GoodsFormParamsType) {
  return request.post({ url: "/goods/product/add", params });
}

/**
 * @return { Promise }
 * @param { GoodsFormParamsType } params
 * @description 商品编辑
 */
export function goodsEdit(params: GoodsFormParamsType) {
  return request.post({ url: "/goods/product/edit", params });
}

/**
 * @return { Promise }
 * @param params
 * @description 商品状态
 */
export function goodsChange(params: { id: number }) {
  return request.post({ url: "/goods/product/change", params });
}

/**
 * @return { Promise }
 * @param params
 * @description 商品删除
 */
export function goodsDel(params: { id: number }) {
  return request.post({ url: "/goods/product/del", params });
}

/**
 * @return { Promise }
 * @param params
 * @description 商品批量删除
 */
export function goodsBatchDelete(params: { ids: number[] }) {
  return request.post({ url: "/goods/product/batchDelete", params });
}

/**
 * @return { Promise }
 * @param params
 * @description 商品批量上架
 */
export function goodsBatchUpper(params: { ids: number[] }) {
  return request.post({ url: "/goods/product/batchUpper", params });
}

/**
 * @return { Promise }
 * @param params
 * @description 商品批量下架
 */
export function goodsBatchLower(params: { ids: number[] }) {
  return request.post({ url: "/goods/product/batchLower", params });
}
