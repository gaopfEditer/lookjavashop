import { reactive } from "vue"
import type { AddressFromType } from "@/api/address.d"

const address = reactive<AddressFromType>({
    id: '',
    contact: '',
    mobile: '',
    provinceId: '',
    provinceName: '',
    cityId: '',
    cityName: '',
    districtId: '',
    districtName: '',
    info: '',
    isDefault: 0,
    addressDetail: ''
})

export function useAddress() {
    const onSelect = (data: AddressFromType, type: boolean) => {
        if (!type) return
        Reflect.ownKeys(address).map((item: any) => {
            address[item] = data[item]
        })
        uni.navigateBack()
    }

    return {
        onSelect,
        address
    }
}
