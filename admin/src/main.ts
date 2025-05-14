import { createApp } from 'vue'
import App from './App.vue'
import install from './install'
import './permission'
import './styles/index.scss'
import 'virtual:svg-icons-register'
import Tmap from '@map-component/vue-tmap';
import elSelectLoadmore from '@/utils/elSelectLoadMore'

const app = createApp(App)
console.log(app)
app.use(Tmap)
app.directive('selectLoadmore', elSelectLoadmore);
app.use(install)
app.mount('#app')
