import { Pay } from './pay'
import { Wechat } from './wechat'
import { Balance } from './balance'
import { ALI } from './ali'

// 支付方式
enum PayWayEnum {
    BALANCE = 1,
    WECHAT = 2,
    ALIPAY = 3
}

const balance = new Balance()
const wechat = new Wechat()
const ali = new ALI()

// 注入余额支付
Pay.inject(PayWayEnum[1], balance)
// 注入微信支付
Pay.inject(PayWayEnum[2], wechat)
Pay.inject(PayWayEnum[3], ali)

// 创建支付
const pay = new Pay()
export { pay, PayWayEnum }
