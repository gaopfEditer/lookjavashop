import { PayStatusEnum } from '@/enums/appEnums'

export class Balance {
    init(name: string, pay: any) {
        pay[name] = this
    }

    async run(options: any) {
        try {
            //@ts-ignore
            return Promise.resolve(PayStatusEnum.SUCCESS);
        } catch (error) {
            return Promise.reject(error)
        }
    }
}
