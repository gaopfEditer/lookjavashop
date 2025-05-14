import routes from 'uni-router-routes'
import { createRouter } from 'uniapp-router-next'

import { ClientEnum } from '@/enums/appEnums'
import { useUserStore } from '@/stores/user'
import { client } from '@/utils/client'
import wechatOa from '@/utils/wechat'
import cache from '@/utils/cache'
import { BACK_URL } from '@/enums/cacheEnums'
const router = createRouter({
    routes,
    //@ts-ignore
    platform: process.env.UNI_PLATFORM,
    h5: {}
})

// 登录拦截
router.beforeEach(async (to, from, next) => {
    //console.log(to)
    const userStore = useUserStore()

    if (to.meta && !to.meta.white && !userStore.isLogin) {
        //保存登录前的路径
        cache.set(BACK_URL, to.fullPath)
    }

    if (to.meta && !userStore.isLogin && to.meta.auth) {
        return '/pages/login/login'
    }
})

// #ifdef H5
router.afterEach((to, from) => {
    setTimeout(async () => {
        if (client == ClientEnum.OA_WEIXIN) {
            // jssdk配置
            await wechatOa.config()
        }
    })
})
// #endif

export default router
