<script setup lang="ts">
import { onLaunch, onShow, onLoad } from '@dcloudio/uni-app'
import { addVisitor } from '@/api/app'
import { useAppStore } from './stores/app'
import { useUserStore } from './stores/user'
import { useThemeStore } from './stores/theme'
import { strToParams } from './utils/util'
const { bindDistribution } = useUserStore()
import { SHARE_CODE } from './enums/cacheEnums'
import cache from './utils/cache'
import { useRouter } from 'uniapp-router-next'

const router = useRouter()
const appStore = useAppStore()
const { getUser } = useUserStore()
const { getTheme } = useThemeStore()

const getConfig = async () => {
    await appStore.getConfig()
    // #ifdef H5
    const { status, close, url } = appStore.getH5Config
    if (status == 0) {
        if (close == 1) return (location.href = url)
        router.reLaunch('/pages/empty/empty')
    }
    // #endif
}

const cacheInvite = (query: any = {}) => {
    const code = query[SHARE_CODE] || strToParams(decodeURIComponent(query['scene']))[SHARE_CODE]
    //console.log('appapp', code)
    if (code) {
        cache.set(SHARE_CODE, code)
    }
}

onLaunch(async (opinion) => {
    await getConfig()
    await getTheme()
    getUser()
    addVisitor()

    // #ifdef MP
    const updateManager = wx.getUpdateManager()
    updateManager.onUpdateReady(function () {
        wx.showModal({
            title: '更新提示',
            content: '新版本已经准备好，是否重启应用？',
            success(res: any) {
                if (res.confirm) {
                    // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
                    updateManager.applyUpdate()
                }
            }
        })
    })
    updateManager.onUpdateFailed(function () {
        uni.$u.toast('新版本下载失败，请检查网络！')
    })
    // #endif
})
onShow((opinion) => {
    console.log('获取opinion', opinion)
    cacheInvite(opinion?.query)
    bindDistribution()
	// #ifndef MP-WEIXIN
		uni.hideTabBar()
	// #endif
})
</script>
<style lang="scss">
page {
    min-height: 100%;
}
</style>
