<template>
    <view class="address-container" :style="$theme.pageStyle">
        <!-- Mian -->
        <u-form :model="address" ref="formRef" :error-type="['message', 'toast']">
            <!-- 联系人 -->
            <view class="card">
                <u-form-item prop="contact">
                    <view class="inline-block label"> 联系人 </view>
                    <u-input
                        v-model="address.contact"
                        class="w-[540rpx]"
                        placeholder="请输入联系人"
                    />
                </u-form-item>
            </view>
            <!-- 手机号 -->
            <view class="card">
                <u-form-item prop="mobile">
                    <view class="inline-block label"> 手机号 </view>
                    <u-input
                        v-model="address.mobile"
                        class="w-[540rpx]"
                        placeholder="请输入手机号码"
                    />
                </u-form-item>
            </view>
            <!-- 省市区 -->
            <view class="card">
                <u-form-item prop="region_content">
                    <view class="flex items-center" style="width: 100%" @click="selectPupop = true">
                        <view class="label"> 省市区 </view>
                        <view
                            class="flex-1 text-lg"
                            :class="{
                                'text-[#c0c0c0]': !regionContent,
                                'text-main': regionContent
                            }"
                        >
                            {{ regionContent || '请选择地址' }}
                        </view>
                        <u-icon name="arrow-right" size="22" color="#888888"></u-icon>
                    </view>
                </u-form-item>
            </view>
            <!-- 门牌号 -->
            <view class="card">
                <u-form-item prop="info">
                    <view class="flex w-full col-start">
                        <view class="label"> 门牌号 </view>
                        <view class="flex-1 pt-[6rpx]">
                            <u-input
                                v-model="address.info"
                                type="textarea"
                                placeholder="请选择"
                                height="124"
                            />
                        </view>
                    </view>
                </u-form-item>
            </view>
            <!-- 是否默认地址 -->
            <view
                class="is-default flex m-[20rpx]"
                @click="address.isDefault = address.isDefault ? 0 : 1"
            >
                <l-checkbox
                    v-model="address.isDefault"
                    label="设为默认"
                    :true-label="1"
                    :false-label="0"
                    :active-color="$theme.primaryColor"
                />
            </view>
        </u-form>

        <!-- Footer -->
        <view class="flex flex-1 m-[20rpx] pt-[30rpx]">
            <!-- #ifdef H5 || MP-WEIXIN -->
            <button
                v-if="address.id"
                class="btn mr-[20rpx] bg-white text-main"
                @click="delPupop = true"
            >
                删除地址
            </button>
            <!-- #endif -->
            <button class="text-white btn bg-primary" @click="onSubmit">保存地址</button>
        </view>

        <!-- 删除地址 -->
        <u-modal
            id="delete-dialog"
            v-model="delPupop"
            :showCancelButton="true"
            confirm-text="狠心删除"
            title="温馨提示"
            :confirm-color="$theme.primaryColor"
            @confirm="handleDel"
        >
            <view class="text-center p-[50rpx]">
                <text>确认删除该地址吗？</text>
            </view>
        </u-modal>

        <!-- 地址组件 -->
        <u-picker
            v-model="selectPupop"
            mode="region"
            :confirm-color="$theme.primaryColor"
            @confirm="regionChange"
        ></u-picker>
    </view>
</template>

<script lang="ts" setup>
import { onLoad, onReady, onUnload } from '@dcloudio/uni-app'
import { ref, reactive, computed } from 'vue'
import {
    addressDetail,
    addressEdit,
    addressAdd,
    addressDel,
    addressAreaChange
} from '@/api/address'
import type { AddressFromType } from '@/api/address.d'

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
    isDefault: 0
})
const formRef = ref()
const delPupop = ref<boolean | null>(false)
const selectPupop = ref<boolean | null>(false)
const rules = reactive<object>({
    contact: [
        { required: true, message: '请输入联系人', trigger: ['change', 'blur'] },
        { min: 1, max: 20, message: '输入长度不得超过20位', trigger: ['blur', 'change'] }
    ],
    mobile: [
        { required: true, message: '请输入手机号码', trigger: ['change', 'blur'] },
        {
            pattern: /^1[3-9]\d{9}$/,
            transform(value: any) {
                return String(value)
            },
            message: '请输入正确的手机号'
        }
    ],
    region: [{ required: true, message: '请选择省市区', trigger: ['change', 'blur'] }],
    info: [{ required: true, message: '请输入门牌号', trigger: ['change', 'blur'] }]
})

const regionContent = computed(() => {
    if (!address.provinceName) return false
    else return `${address.provinceName} ${address.cityName} ${address.districtName}`
})

const getDetail = async (): Promise<void> => {
    try {
        const data = await addressDetail({ id: address.id })
        Reflect.ownKeys(address).map((item: any) => {
            address[item] = data[item]
        })
    } catch (error) {
        console.log('获取详情', error)
    }
}

const onSubmit = () => {
    formRef.value.validate((valid: boolean) => {
        if (!valid) return false
        if (!address.id) handleAdd()
        else handleEdit()
    })
}

const handleAdd = async (): Promise<void> => {
	// return uni.$u.toast(address.cityId)
    await addressAdd({ ...address })
    setTimeout(() => {
        uni.navigateBack()
    }, 300)
}

const handleEdit = async (): Promise<void> => {
    await addressEdit({ ...address })
    setTimeout(() => {
        uni.navigateBack()
    }, 300)
}

const regionChange = (region: any) => {
    address.provinceId = region['province'].code
    address.cityId = region['city'].code
    address.districtId = region['area'].code
    address.provinceName = region['province'].name
    address.cityName = region['city'].name
    address.districtName = region['area'].name
}

// 逆解析地址
const getWechatAddress = async () => {
    try {
        let wxAddress = uni.getStorageSync('wxAddress')
        if (!wxAddress) return
        wxAddress = JSON.parse(wxAddress)
        console.log(wxAddress)

        // #ifdef H5
        const region = [wxAddress.provinceName, wxAddress.cityName, wxAddress.countryName]
        // #endif
        // #ifdef MP-WEIXIN
        const region = [wxAddress.provinceName, wxAddress.cityName, wxAddress.countyName]
        // #endif
		
		const landCity = ['香港特别行政区', '澳门特别行政区']
		const landCityFn = (city: string) => {
			const landCityMap: Record<string, string> = {
				'香港特别行政区': '香港岛',
				'澳门特别行政区': '澳门'
			}
			return landCityMap[city]
		}

        const data = await addressAreaChange({
            province: region[0],
            city: landCity.includes(region[1]) ? landCityFn(region[1]) : region[1],
            district: region[2]
        })
        Reflect.ownKeys(data).map((item: any) => {
            address[item] = data[item]
        })
        address.contact = wxAddress.userName
        address.mobile = wxAddress.telNumber
        address.info = wxAddress.detailInfo

        address.provinceName = region[0]
        address.cityName = region[1]
        address.districtName = region[2]
    } catch (error) {
        console.log('地址逆解析', error)
    }
}

// 删除地址
const handleDel = async (): Promise<void> => {
    try {
        await addressDel({ id: address.id })
        setTimeout(() => {
            uni.navigateBack()
        }, 300)
    } catch (error) {
        console.log(error)
    }
}

onLoad(({ id }: any) => {
    address.id = id
    if (address.id) {
        getDetail()
        uni.setNavigationBarTitle({ title: '编辑地址' })
    } else {
        getWechatAddress()
        uni.setNavigationBarTitle({ title: '添加地址' })
    }
})

onReady(() => {
    formRef.value?.setRules(rules)
})

onUnload(() => {
    uni.removeStorageSync('wxAddress')
})
</script>
<style lang="scss">
.address-container {
    padding: 20rpx;

    .card {
        margin-bottom: 20rpx;
        padding: 0 24rpx;
        border-radius: 14rpx;
        background-color: #ffffff;

        .label {
            color: #222222;
            font-size: 28rpx;
            margin-right: 30rpx;
            line-height: 70rpx;
        }
    }

    .btn {
        flex: 1;
        height: 80rpx;
        line-height: 80rpx;
        font-size: 30rpx;
        border-radius: 60rpx;
    }

    .is-default {
        image {
            width: 34rpx;
            height: 34rpx;
        }
    }
}
</style>
