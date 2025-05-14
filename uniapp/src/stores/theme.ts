import { getDecorate } from '@/api/shop'
import { generateVars } from '@/utils/theme'
import { defineStore } from 'pinia'

interface ThemeStore {
    primaryColor: string
    deepColor: string
    undeepColor: string
    navColor: string
    navBgColor: string
    enabledColor: string
    vars: string
}
export const useThemeStore = defineStore({
    id: 'themeStore',
    state: (): ThemeStore => ({
        primaryColor: '',
        deepColor: '',
        undeepColor: '',
        navColor: '#000000',
        navBgColor: '#ffffff',
        enabledColor: '',
        vars: ''
    }),
    actions: {
        async getTheme() {
            const { pages } = await getDecorate({
                id: 9
            })
            console.log(JSON.parse(pages), 2222)
            const {
                main_color,
                navigationBarColor,
                topTextColor,
                deep_color,
                undeep_color,
                enabled_color
            } = JSON.parse(pages)
            this.primaryColor = main_color
            this.deepColor = deep_color
            this.undeepColor = undeep_color
            this.navColor = topTextColor === 'white' ? '#ffffff' : '#000000'
            this.navBgColor = navigationBarColor || main_color
            this.vars = generateVars({
                'theme-color': main_color,
                'theme-light-color': undeep_color,
                'theme-dark-color': deep_color,
                'theme-dosabled-color': enabled_color
            })
            console.log('vars', this.vars)
        },
        setTheme(color: string) {
            this.primaryColor = color
        }
    }
})
