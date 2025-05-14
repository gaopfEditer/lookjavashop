import request from '@/utils/request'

export type PayParamFormType = {
    from: string
    order_id: number | string
    pay_way: number
    redirect: string
    [index: string | number]: string | number
}

// 预支付
export function prepay(data: any) {
    return request.post({ url: '/pay/prepay', data: data }, { isAuth: true })
}

// 获取支付方式
export function getPayWay(data: any) {
    return request.get({ url: '/pay/payWay', data: data }, { isAuth: true })
}

// 获取支付状态
export function getPayStatus(data: any) {
    return request.get({ url: '/pay/payStatus', data: data }, { isAuth: true })
}
