import { createSSRApp } from 'vue'
import App from './App.vue'
import plugins from './plugins'
import router from './router'
import './styles/index.scss'
import { setupMixin } from './mixins'
import RouterNavigate from 'uniapp-router-next/components/router-navigate/router-navigate.vue'

export function createApp() {
    const app = createSSRApp(App)
    setupMixin(app)
    app.use(plugins)
    app.use(router)
    app.component('router-navigate', RouterNavigate)

    return {
        app
    }
}
