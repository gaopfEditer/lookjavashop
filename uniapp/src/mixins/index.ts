import { App } from 'vue'
import share, { initShareEvent } from './share'
import theme from './theme'
export function setupMixin(app: App) {
    app.mixin(share).mixin(theme)
    initShareEvent()
}
