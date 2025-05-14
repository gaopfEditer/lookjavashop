import { isObject } from '@vue/shared'
import router from '@/router'

/**
 * 节流
 * @param { Function } callback
 * @param { Number } time
 * @param context
 * @return { Function }
 */
export const throttle = (callback: (...params: any) => any, time = 1000) => {
    let previous = new Date(0).getTime()
    return (...args: []) => {
        const now = new Date().getTime()
        if (now - previous > time) {
            previous = now
            callback(args)
        }
    }
}

/**
 * @description 获取元素节点信息（在组件中的元素必须要传ctx）
 * @param  { String } selector 选择器 '.app' | '#app'
 * @param  { Boolean } all 是否多选
 * @param  { ctx } context 当前组件实例
 */
export const getRect = (selector: string, all = false, context?: any) => {
    return new Promise((resolve, reject) => {
        let qurey = uni.createSelectorQuery()
        if (context) {
            qurey = uni.createSelectorQuery().in(context)
        }
        qurey[all ? 'selectAll' : 'select'](selector)
            .boundingClientRect(function (rect) {
                if (all && Array.isArray(rect) && rect.length) {
                    return resolve(rect)
                }
                if (!all && rect) {
                    return resolve(rect)
                }
                reject('找不到元素')
            })
            .exec()
    })
}

/**
 * @description 获取当前页面实例
 */
export function currentPage() {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    return currentPage || {}
}

/**
 * @description 后台选择链接专用跳转
 */
interface Link {
    path: string
    name?: string
    type: string
    isTab: boolean
    query?: Record<string, any>
}

export enum LinkTypeEnum {
    'SHOP_PAGES' = 'shop',
    'CUSTOM_LINK' = 'custom'
}

export function navigateTo(link: Link, navigateType: 'navigateTo' | 'reLaunch' = 'navigateTo') {
    const url = link.query ? `${link.path}?${objectToQuery(link.query)}` : link.path
    navigateType == 'navigateTo' && uni.navigateTo({ url })
    navigateType == 'reLaunch' && uni.reLaunch({ url })
}

/**
 * @description 是否为空
 * @param {unknown} value
 * @return {Boolean}
 */
export const isEmpty = (value: unknown) => {
    return value == null && typeof value == 'undefined'
}
export const isempty = (value: unknown) => {
    return value == null
}

/**
 * @description 判断对象是否为空
 * @param {unknown} obj
 * @return {Boolean}
 */
export const isObjEmpty = (obj: any) => {
    return Object.keys(obj).length == 0 ? true : false
}

/**
 * @description 对象格式化为Query语法
 * @param { Object } params
 * @return {string} Query语法
 */
export function objectToQuery(params: Record<string, any>): string {
    let query = ''
    for (const props of Object.keys(params)) {
        const value = params[props]
        const part = encodeURIComponent(props) + '='
        if (!isEmpty(value)) {
            console.log(encodeURIComponent(props), isObject(value))
            if (isObject(value)) {
                for (const key of Object.keys(value)) {
                    if (!isEmpty(value[key])) {
                        const params = props + '[' + key + ']'
                        const subPart = encodeURIComponent(params) + '='
                        query += subPart + encodeURIComponent(value[key]) + '&'
                    }
                }
            } else {
                query += part + encodeURIComponent(value) + '&'
            }
        }
    }
    return query.slice(0, -1)
}

/**
 * @description Query语法格式化为数组对象
 * @param { String } queryParams
 * @return {Object} 对象
 */
export const queryToArrObj = (queryParams: string): { [key: string]: any }[] => {
    const arr: { [key: string]: any }[] = []
    const params = decodeURIComponent(queryParams).split('&')
    for (let i = 0; i < params.length; i++) {
        const [key, val] = params[i].split('=')
        // 对key进行解析，获取索引和属性名
        const match = key.match(/^(\d+)\[(.+)\]$/)
        if (match && match.length === 3) {
            const idx = parseInt(match[1])
            const prop = match[2]
            if (!arr[idx]) {
                arr[idx] = {}
            }
            arr[idx][prop] = val
        } else {
            arr.push({
                [key]: val
            })
        }
    }
    return arr
}

/**
 * @description 对象参数转为以？&拼接的字符
 * @param params
 * @returns
 */
export function paramsToStr(params: Record<string, string>) {
    let p = ''
    if (isObject(params)) {
        p = '?'
        for (const props in params) {
            p += `${props}=${params[props]}&`
        }
        p = p.slice(0, -1)
    }
    return p
}

/**
 * @description 添加单位
 * @param {String | Number} value 值 100
 * @param {String} unit 单位 px em rem
 */
export const addUnit = (value: string | number, unit = 'rpx') => {
    return !Object.is(Number(value), NaN) ? `${value}${unit}` : value
}

/**
 * @description 字符串格式化为Query语法
 * @param { string } str
 * @return { object } Query语法
 */
export const strToParams = (str: string) => {
    const newparams: any = {}
    for (const item of str.split('&')) {
        newparams[item.split('=')[0]] = item.split('=')[1]
    }
    return newparams
}

/**
 * @description 将一个数组分成几个同等长度的数组
 * @param  { Array } array[分割的原数组]
 * @param  { Number } size[每个子数组的长度]
 */
export const sliceArray = (array: Record<string, any>, size: number) => {
    const result = []
    for (let x = 0; x < Math.ceil(array.length / size); x++) {
        const start = x * size
        const end = start + size
        result.push(array.slice(start, end))
    }
    return result
}

/**
 * @description 组合异步任务
 * @param  { string } task 异步任务
 */
export function series(...task: Array<(_arg: any) => any>) {
    return function (): Promise<any> {
        return new Promise((resolve, reject) => {
            const iteratorTask = task.values()
            const next = (res?: any) => {
                const nextTask = iteratorTask.next()
                if (nextTask.done) {
                    resolve(res)
                } else {
                    Promise.resolve(nextTask.value(res)).then(next).catch(reject)
                }
            }
            next()
        })
    }
}

/**
 * 复制内容
 * @param { String } str
 * @return void
 */
export function copy(str: string) {
    // #ifdef H5
    const aux = document.createElement('input')
    aux.setAttribute('value', str)
    document.body.appendChild(aux)
    aux.select()
    document.execCommand('copy')
    document.body.removeChild(aux)
    uni.showToast({ title: '复制成功' })
    // #endif

    // #ifndef H5
    uni.setClipboardData({
        data: str.toString(),
        success() {
            // #ifdef MP-TOUTIAO
            uni.showToast({ title: '复制成功' })
            // #endif
        }
    })
    // #endif
}

/**
 * 换算为千米
 */
export function kilometers(meters : number) {
	if (meters < 1000) {
		return meters.toFixed(2) + "米";
	} else {
		return (meters/1000).toFixed(2) + "公里";
	}
}

/**
 * 把时间改为时分
 * @param {any} timeString 
 * @return 
 */ 
export function formatTime(timeString) {  
  // 使用字符串的split方法按冒号分割时间字符串  
  const parts = timeString.split(':');  
    
  // 检查是否至少有两个部分（小时和分钟）  
  if (parts.length >= 2) {  
    // 返回由小时和分钟组成的字符串，中间用冒号分隔  
    return `${parts[0]}:${parts[1]}`;  
  } else {  
    // 如果时间字符串格式不正确，返回原始字符串或适当的错误消息  
    return 'Invalid time format';  
  }  
}  

/**
 * 验证手机号码
 * @return 
 */ 
export function isValidPhone (phoneNumber) {
  const regex = /^1[3-9]\d{9}$/; // 中国大陆手机号码正则，以1开头，第二位是3-9，后面是9位数字
  return regex.test(phoneNumber.trim());
};