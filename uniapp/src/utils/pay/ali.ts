import { PayStatusEnum } from '@/enums/appEnums'
import { handleClientEvent } from '../client'
import cache from '@/utils/cache'
//#ifdef H5
import wechatOa from '../wechat'
//#endif
export class ALI {
    init(name: string, pay: any) {
        pay[name] = this
    }

    async run(options: any) {
        try {
            // @ts-ignore
            const res = await handleClientEvent({
                H5: () => {
                    return new Promise((resolve) => {
                        document.write(options.form)
                    })
                }
            })
            return res
        } catch (error) {
            return Promise.reject(error)
        }
    }
}
