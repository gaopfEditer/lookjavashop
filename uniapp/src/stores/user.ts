import { getUserCenter } from '@/api/user'
import { shoppingCartCount } from '@/api/goods'
import { bindDistributionCode } from '@/api/distribution'
import { TOKEN_KEY, SHARE_CODE } from '@/enums/cacheEnums'
import cache from '@/utils/cache'
import { defineStore } from 'pinia'

interface UserSate {
    userInfo: Record<string, any>
    token: string | null
    temToken: string | null
    shopCartCount: number
}
export const useUserStore = defineStore({
    id: 'userStore',
    state: (): UserSate => ({
        userInfo: {},
        token: cache.get(TOKEN_KEY) || null,
        temToken: null,
        shopCartCount: 0
    }),
    getters: {
        isLogin: (state) => !!state.token
    },
    actions: {
        async getUser() {
            const data = await getUserCenter({
                token: this.token || this.temToken
            })
            this.userInfo = data
        },
        login(token: string) {
            this.token = token
            cache.set(TOKEN_KEY, token)
            this.bindDistribution()
        },
        logout() {
            this.token = ''
            this.userInfo = {}
            cache.remove(TOKEN_KEY)
        },
        async getShopCartCount() {
            try {
                const { num } = await shoppingCartCount()
                this.shopCartCount = num
            } catch (error) {
                console.log('获取购物车接口出错', error)
                this.shopCartCount = 0
            }
        },
        //分销下级绑定
        async bindDistribution() {
            const code = cache.get(SHARE_CODE) || ''
            try {
                if (this.isLogin && code) {
                    await bindDistributionCode({
                        code: code
                    })
                    cache.remove(SHARE_CODE)
                }
            } catch (error) {
                console.log('绑定失败', error)
            }
        }
    }
})
