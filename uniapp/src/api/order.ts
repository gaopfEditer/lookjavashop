import request from '@/utils/request'

/** S 订单 **/
/**
 * @return { Promise }
 * @description 初始化订单
 * @param data
 */
export function orderSettle(data: any) {
    return request.post({ url: '/order/settlement', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 提交订单
 * @param data
 */
export function orderSubmit(data: any) {
    return request.post({ url: '/order/submit', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 订单列表
 * @param data
 */
export function orderLists(data: any) {
    return request.get({ url: '/order/list', data: data }, { isAuth: false })
}

/**
 * @return { Promise }
 * @description 订单详情
 * @param data
 */
export function orderDetail(data: { id: number }) {
    return request.get({ url: '/order/detail', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 取消订单
 * @param data
 */
export function apiOrderCancel(data: { id: number }) {
    return request.post({ url: '/order/cancel', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 确认收货
 * @param data
 */
export function apiOrderConfirm(data: { id: number }) {
    return request.post({ url: '/order/confirm', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 删除订单
 * @param data
 */
export function apiOrderDel(data: { id: number }) {
    return request.post({ url: '/order/del', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 获取订单商品详情
 * @param data
 */
export function orderGoodsDetail(data: { id: number }) {
    return request.get({ url: '/order/goods/detail', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 查看物流
 * @param data
 */
export function orderLogistics(data: { id: number }) {
    return request.get({ url: '/order/logistics', data: data }, { isAuth: true })
}
/** E 订单 **/

/** S 评价 **/
/**
 * @return { Promise }
 * @description 获取商品评价列表
 * @param data
 */
export function commentLists(data: any) {
    return request.get({ url: '/goodsComment/list', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 添加商品评论
 * @param data
 */
export function commentAdd(data: any) {
    return request.post({ url: '/goodsComment/add', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 获取商品评价列表
 * @param data
 */
export function commentGoodsLists(data: any) {
    return request.get({ url: '/goodsComment/goodsCommentList', data: data })
}
/** E 评价 **/

/** S 售后 **/
/**
 * @return { Promise }
 * @description 获取商品售后列表
 * @param data
 */
export function afterSalesLists(data: { pageNo: number; pageSize: number; type: string }) {
    return request.get({ url: '/order/after/list', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 获取订单商品详情
 * @param data
 */
export function afterSaleGoodsDetail(data: { orderGoodsId: number }) {
    return request.get({ url: '/order/after/orderInfo', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description
 * @param data
 */
export function afterSalesDetail(data: { id: number }) {
    return request.get({ url: '/order/after/detail', data: data }, { isAuth: true })
}

/**
 * @return { Promise }
 * @description 购物车选择
 * @param 撤销申请
 */
export function afterSalesCancel(data: { id: number }) {
    return request.post({ url: '/order/after/cancel', data: data }, { isAuth: true })
}

export type ApplyRefundType = {
    /** 订单商品id **/
    orderGoodsId: number
    /** 退款说明 **/
    refundRemark: string
    /** 退款图片 最多3张 **/
    refundImage: string[]
    /** 退款金额 **/
    refundMoney: number
    /** 退款原因 **/
    refundReason: string
    /** 0 仅退款 **/
    refundType: number
}

/**
 * @return { Promise }
 * @description 售后申请
 * @param { ApplyRefundType } data
 */
export function afterSalesAdd(data: ApplyRefundType) {
    return request.post({ url: '/order/after/add', data: data }, { isAuth: true })
}

export type SalesDeliveryFormType = {
    /** 售后id **/
    afterId: string | number
    /** 物流公司ID **/
    expressId: string | number
    /** 物流单号 **/
    invoiceNo: string
    /** 发货说明 **/
    expressRemark: string
    /** 联系方式 **/
    expressContact: string
}

/**
 * @return { Promise }
 * @description 填写快递单号
 * @param { SalesDeliveryFormType } data
 */
export function afterSalesDelivery(data: SalesDeliveryFormType) {
    return request.post({ url: '/order/after/delivery', data: data }, { isAuth: true })
}
/** E 售后 **/

export function apiGetMchId(data: any) {
    return request.get({ url: '/pay/getMchId', data: data }, { isAuth: true })
}


/**
 * @return { Promise }
 * @description 到店自提订单列表
 * @param data
 */
export function selffetchOrderLists(data: any) {
    return request.get({ url: '/order/selffetchList', data: data }, { isAuth: false })
}


/**
 * @return { Promise }
 * @description 自提订单详情
 * @param data
 */
export function selffetchDetail(data: { id: number }) {
    return request.get({ url: '/order/selffetchDetail', data: data }, { isAuth: true })
}

/**
 * 自提核销
 */
export function selffetchVerify(data) {
	return request.post({ url: '/order/selffetchVerify', data: data }, { isAuth: true })
}

/**
 * 自提核销
 */
export function pickupCodeVerify(data) {
	return request.post({ url: '/order/pickCodeVerify', data: data }, { isAuth: true })
}