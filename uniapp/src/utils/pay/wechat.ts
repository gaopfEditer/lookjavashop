import { PayStatusEnum } from '@/enums/appEnums'
import { handleClientEvent } from '../client'
//#ifdef H5
import wechatOa from '../wechat'
//#endif
export class Wechat {
    init(name: string, pay: any) {
        pay[name] = this
    }

    async run(options: any) {
        try {
            // @ts-ignore
            const res = await handleClientEvent({
                MP_WEIXIN: () => {
                    return new Promise((resolve) => {
                        console.log(options)
                        uni.requestPayment({
                            orderInfo: '',
                            provider: 'wxpay',
                            timeStamp: options.timeStamp,
                            nonceStr: options.nonceStr,
                            package: options.packageValue,
                            paySign: options.paySign,
                            signType: options.signType,
                            success() {
                                resolve(PayStatusEnum.SUCCESS)
                            },
                            fail() {
                                resolve(PayStatusEnum.FAIL)
                            }
                        })
                    })
                },
                OA_WEIXIN: () => {
                    return new Promise((resolve) => {
                        wechatOa
                            .pay(options)
                            .then(() => {
                                resolve(PayStatusEnum.SUCCESS)
                            })
                            .catch(() => {
                                resolve(PayStatusEnum.FAIL)
                            })
                    })
                },
                H5: () => {
                    return new Promise((resolve) => {
                        window.open(options.url, '_self')
                        resolve(PayStatusEnum.PENDING)
                    })
                }
            })
            return res
        } catch (error) {
            return Promise.reject(error)
        }
    }
}
