import { defineStore } from 'pinia'
import { getConfig } from '@/api/app'

interface AppSate {
    config: Record<string, any>
}
export const useAppStore = defineStore({
    id: 'appStore',
    state: (): AppSate => ({
        config: {}
    }),
    getters: {
        getWebsiteConfig: (state) => state.config.website || {},
        getLoginConfig: (state) => state.config.login || {},
        getTabbarConfig: (state) => state.config.tabbar || [],
        getStyleConfig: (state) => state.config.style || {},
        getH5Config: (state) => state.config.h5 || {},
        getCopyright: (state) => state.config.copyright || {}
    },
    actions: {
        getImageUrl(url: string) {
            return url ? `${this.config.domain}${url}` : ''
        },
        async getConfig() {
            const data = await getConfig()

            this.config = data
            console.log(this.config, 45678913)
        }
    }
})
