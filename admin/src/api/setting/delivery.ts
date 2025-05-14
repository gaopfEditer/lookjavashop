import request from '@/utils/request'

// 配送方式 获取配置
/**
 * @return { Promise }
 * @description 设置支付方式
 */
export function getDelivery() {
    return request.get({ url: '/deliver/config/getDeliverConfig' })
}

export type DeliveryFormType = {
    id: number | string
    expressName: string // 是	快递显示名称
    expressIs: number // 是	快递开启 1开启 0不开启
    [index: string]: string | number
}
/**
 * @return { Promise }
 * @param { DeliveryFormType } params
 * @description 配送方式 设置配置
 */
export function deliverySet(params: DeliveryFormType) {
    return request.post({ url: '/deliver/config/setDeliverConfig', params })
}

/**
 * @return { Promise }
 * @param { DeliveryFormType } params
 * @description 到店自提 设置配置
 */
export function deliverySetPickup(params: DeliveryFormType) {
    return request.post({ url: '/deliver/config/setPickupConfig', params })
}

/**
 * @return { Promise }
 * @description 获取物流接口
 */
export function getLogistics() {
    return request.get({ url: '/deliver/config/getLogisticsConfig' })
}


export type LogisticsFormType = {
    id: number | string
    kd100: {
        interface_type?: string
        key: string
        customer: string
    }
    kdniao: {
        customer: string
        key: string
        requestType: string
    }
    engine: string
}

/**
 * @return { Promise }
 * @param { LogisticsFormType } params
 * @description  配送方式 设置配置
 */
export function logisticsSet(params: LogisticsFormType) {
    return request.post({ url: '/deliver/config/setLogisticsConfig', params })
}



/**
 * @return { Promise }
 * @description 运费模版列表
 */
export function freightLists() {
    return request.get({ url: '/deliver/tpl/list' })
}

/**
 * @return { Promise }
 * @description 运费模版详情
 * @param params
 */
export function freightDetail(params: { id: number }) {
    return request.get({ url: '/deliver/tpl/detail', params })
}

/**
 * @return { Promise }
 * @description 运费模版删除
 * @param params
 */
export function freightDel(params: { id: number }) {
    return request.post({ url: '/deliver/tpl/del', params })
}


export type FreightFormType = {
    id: number | string
    name: string            // 是	示例值：运费模板1
    type: number	        // 是	示例值：0说明：计费方式 0按件 1按重量 2按 体积
    firstNum: number | string       // 是	示例值：2说明：首 件/重/体积
    firstPrice: number | string     // 是	示例值：3说明：首 件/重/体积 运费
    continueNum: number | string    // 是	示例值：4说明：续 件/重/体积
    continuePrice: number | string  // 是	示例值：5说明：续 件/重/体积 运费
    remark: string          // 否	示例值：这是备注 说明：运费模板备注
    [index: string]: string | number
}

/**
 * @return { Promise }
 * @param { FreightFormType } params
 * @description 运费模版新增
 */
export function freightAdd(params: FreightFormType) {
    return request.post({ url: '/deliver/tpl/add', params })
}

/**
 * @return { Promise }
 * @param { FreightFormType } params
 * @description 运费模版编辑
 */
export function freightEdit(params: FreightFormType) {
    return request.post({ url: '/deliver/tpl/edit', params })
}



/**
 * @return { Promise }
 * @description 快递公司列表
 * @param params
 */
export function expressLists(params: { name?: string }) {
    return request.get({ url: '/deliver/express/list', params })
}

/**
 * @return { Promise }
 * @description 快递公司详情
 * @param params
 */
export function expressDetail(params: { id: number }) {
    return request.get({ url: '/deliver/express/detail', params })
}

/**
 * @return { Promise }
 * @description 快递公司删除
 * @param params
 */
export function expressDel(params: { id: number }) {
    return request.post({ url: '/deliver/express/del', params })
}


export type ExpressFormType = {
    id: number | string
    name: string            // 是	示例值：顺丰
    image: string	        // 是	说明：图标
    codeKd: string          // 是	说明：快递编码
    codeKd100: string       // 是	说明：快递100编码
    codeKdniao: string      // 是	说明：快递鸟编码
    sort: number            // 是	说明：排序
    [index: string]: string | number
}

/**
 * @return { Promise }
 * @param { ExpressFormType } params
 * @description 快递公司新增
 */
export function expressAdd(params: ExpressFormType) {
    return request.post({ url: '/deliver/express/add', params }, { isFilterParams: false })
}

/**
 * @return { Promise }
 * @param { ExpressFormType } params
 * @description 快递公司编辑
 */
export function expressEdit(params: ExpressFormType) {
    return request.post({ url: '/deliver/express/edit', params }, { isFilterParams: false })
}
