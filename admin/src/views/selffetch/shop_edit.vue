<template>
    <div class="edit-popup">
        <popup
            ref="popupRef"
            :title="popupTitle"
            :async="true"
            width="50%"
            :clickModalClose="true"
            @confirm="handleSubmit"
            @close="handleClose"
        >
            <el-form ref="formRef" :model="formData" label-width="120px"  :rules="formRules">
                <!-- 门店名称 -->
                <el-form-item label="门店名称" prop="name">
                    <el-input
                        class="ls-input"
                        v-model="formData.name"
                        show-word-limit
                        placeholder="请输入门店名称"
                    ></el-input>
                </el-form-item>
                <el-form-item label="门店LOGO" prop="image">
                    <material-picker v-model="formData.image" />
                </el-form-item>
                <el-form-item label="联系人" prop="contact">
                    <el-input v-model="formData.contact" placeholder="请输入联系人" />
                </el-form-item>
                <el-form-item label="联系电话" prop="mobile">
                    <el-input v-model="formData.mobile" placeholder="请输入联系电话" />
                </el-form-item>
                <!-- 门店地址 -->
                <el-form-item label="门店地址" prop="province">
                    <area-select v-model:province="formData.province" v-model:city="formData.city" v-model:district="formData.district" :checkStrictly="true" :width="'320px'" />
                </el-form-item>

                <el-form-item label="详细地址" prop="address">
                    <el-input class="ls-input" v-model="formData.address" show-word-limit placeholder="请输入详情地址" >
                        <template #append>
                            <el-button  @click="onMapSearch">搜索地图</el-button>
                        </template>
                    </el-input>
                </el-form-item>
                <!-- 地图定位 -->
                <el-form-item label="地图定位" prop="password_confirm">
                    <qq-map :tencentMap="tencentMap" v-if="tencentMap" :extendData="extendData" @click="clickQqMap" :centerLocation="initCenter"></qq-map>
                </el-form-item>

                <!-- 营业周天 -->
                <el-form-item label="营业周天" prop="weekdays">
                    <el-checkbox-group v-model="weekdayChecked">
                        <el-checkbox v-for="weekday in weekdayList" :label="weekday.key" :key="weekday.key">{{
                            weekday.label
                        }}</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>

                 <!-- 营业时段 -->
                 <el-form-item label="营业时段" prop="businessStartTime">
                    <el-time-picker
                        class="ls-input"
                        is-range
                        v-model="businessTime"
                        range-separator="至"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间"
                        placeholder="选择时间范围"
                    >
                    </el-time-picker>
                </el-form-item>

                <!-- 门店详情 -->
                <el-form-item label="门店详情" prop="remark">
                    <el-input
                        class="ls-input"
                        v-model="formData.remark"
                        type="textarea"
                        :autosize="{ minRows: 3, maxRows: 6 }"
                        show-word-limit
                    />
                </el-form-item>

                <!-- 门店状态 -->
                <el-form-item label="门店状态" prop="status">
                    <el-radio-group v-model="formData.status">
                        <el-radio :label="0">停用</el-radio>
                        <el-radio :label="1">启用</el-radio>
                    </el-radio-group>
                </el-form-item>

            </el-form>
        </popup>
    </div>
</template>
<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import {  selffetchshopEdit, selffetchshopAdd, selffetchshopDetail, apiMapRegionSearch } from '@/api/selffetchshop/selffetchshop'
import Popup from '@/components/popup/index.vue'
import feedback from '@/utils/feedback'
import type { PropType } from 'vue'
import AreaSelect from '@/components/area-select/index.vue'
import QqMap from "@/components/qqmap/index.vue"
import { apiMapGet } from "@/api/setting/map"
import { timeFormat } from "@/utils/util"

defineProps({
    dictData: {
        type: Object as PropType<Record<string, any[]>>,
        default: () => ({})
    }
})
const emit = defineEmits(['success', 'close'])
const formRef = shallowRef<FormInstance>()
const popupRef = shallowRef<InstanceType<typeof Popup>>()
const mode = ref('add')
const popupTitle = computed(() => {
    return mode.value == 'edit' ? '编辑自提门店' : '新增自提门店'
})
const weekdayChecked = ref([])
const businessTime: any[] = ref<[Date, Date]>([new Date().setHours(6, 0, 0), new Date().setHours(18, 0, 0)])
const tencentMap = ref<any>(undefined)
const markerLayer = ref<any>(undefined)
const extendData = ref([])
const initCenter = ref(undefined)

const formData = reactive({
    id: undefined,
    name: '',
    image: '',
    contact: '',
    mobile: '',
    province: '',
    city: '',
    district: '',
    address: '',
    longitude: 116.30571126937866,
    latitude: 39.98481500648338,
    businessStartTime: '06:00:00',
    businessEndTime: '18:00:00',
    weekdays: '',
    status: 1,
    remark: ''
})

const weekdayList = ref([
        {
            key: 1,
            label: '周一'
        },
        {
            key: 2,
            label: '周二'
        },
        {
            key: 3,
            label: '周三'
        },
        {
            key: 4,
            label: '周四'
        },
        {
            key: 5,
            label: '周五'
        },
        {
            key: 6,
            label: '周六'
        },
        {
            key: 7,
            label: '周天'
        }
    ])

const formRules = {
        name: [
            {
                required: true,
                message: '请输入门店名称',
                trigger: ['blur', 'change']
            }
        ],
        image: [
            {
                required: true,
                message: '请选择门店LOGO',
                trigger: ['blur', 'change']
            }
        ],
        contact: [
            {
                required: true,
                message: '请输入联系人',
                trigger: ['blur', 'change']
            }
        ],
        mobile: [
            {
                required: true,
                message: '请输入联系电话',
                trigger: ['blur', 'change']
            }
        ],
        province: [
            {
                required: true,
                message: '请选择门店地址',
                trigger: ['blur', 'change']
            }
        ],
        address: [
            {
                required: true,
                message: '请输入详细地址',
                trigger: ['blur', 'change']
            }
        ],
        weekdays: [
            {
                required: true,
                message: '请选择营业周天',
                trigger: ['blur', 'change']
            }
        ],
        status: [
            {
                required: true,
                message: '请选择门店状态',
                trigger: ['blur', 'change']
            }
        ],
        businessStartTime: [
            {
                required: true,
                message: '请选择营业时段',
                trigger: ['blur', 'change']
            }
        ]
}

/**
 * watch
 */
watch(weekdayChecked, (value, oldValue) => {
    formData.weekdays = value.slice().sort((a, b) => a - b).join(',')
}, { deep: true });

watch(businessTime, (value, oldValue) => {
    formData.businessStartTime =  timeFormat(Date.parse(value[0]), 'hh:MM:ss')
    formData.businessEndTime =  timeFormat(Date.parse(value[1]), 'hh:MM:ss')
    //console.log(formData, "formDataformData", value)
})

//function start

const handleSubmit = async () => {
    await formRef.value?.validate()
    const data: any = { ...formData }
    mode.value == 'edit' ? await selffetchshopEdit(data) : await selffetchshopAdd(data)
    popupRef.value?.close()
    feedback.msgSuccess('操作成功')
    emit('success')
}

const open = (type = 'add') => {
    mode.value = type
    popupRef.value?.open()
}

const setFormData = async (data: Record<string, any>) => {
    for (const key in formData) {
        if (data[key] != null && data[key] != undefined) {
            //@ts-ignore
            formData[key] = data[key]
        }
    }

    weekdayChecked.value = data.weekdays.split(',').map((item: string) => Number(item))
    const date = new Date().toDateString()
    const dateFormat = timeFormat(Date.parse(date), 'yyyy-mm-dd')
    businessTime.value = [
        new Date(dateFormat + ' ' + data.businessStartTime),
        new Date(dateFormat + ' ' + data.businessEndTime)
    ]

    initCenter.value = {lat: formData.latitude, lng: formData.longitude, content: formData.address}
}

const getDetail = async (row: Record<string, any>) => {
    const data = await selffetchshopDetail({
        id: row.id
    })
    setFormData(data)
}

const handleClose = () => {
    emit('close')
}

const getApiMapGet = async () => {
    let map = await apiMapGet()
    tencentMap.value = map.tencentMapKey
}

    // 地图搜索
const onMapSearch = () => {
        if (!tencentMap.value) {
            return feedback.msgError('地图未加载完成')
        }
        apiMapRegionSearch({
            keyword: encodeURI(formData.address),
            boundary: `region(${formData.district ?? formData.city}, 0)`,
            key: tencentMap.value!
        }).then(({ data }) => {
                if (!data.length) {
                    return feedback.msgError(`没有找到“${formData.address}”的相关地点`)
                } else {
                    extendData.value = []
                    formData.longitude = data[0].location.lng
                    formData.latitude = data[0].location.lat
                    console.log(formData)
                    data.map(item => {
                        extendData.value.push({
                            id: item.id,
                            styleId: 'marker',
                            position:  { lat: item.location.lat, lng: item.location.lng },
                            properties: {
                                title: item.title
                            },
                            content: item.title
                        })
                    })
                }
        }).catch(err => {
            feedback.msgError('地图搜索出现了点问题，请重新尝试。')
        })
}

const clickQqMap = (e) => {
    formData.longitude = e.latLng.lng
    formData.latitude = e.latLng.lat
    console.log(formData, "formDataformData")
}

defineExpose({
    open,
    setFormData,
    getDetail
})
getApiMapGet()
</script>

<style lang="scss" scoped>
.ls-add-admin {
    padding-bottom: 80px;

    .ls-input {
        width: 380px;
    }

    #tencent-map {
        width: 580px;
        height: 380px;
    }
}
</style>