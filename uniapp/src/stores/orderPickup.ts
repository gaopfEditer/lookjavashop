import { defineStore } from 'pinia'

interface AppSate {
    config: Record<string, any>
}
export const useOrderPickup = defineStore({
    id: 'orderPickup',
    state: (): AppSate => ({
        config: {}
    }),
    getters: {
        getPickup: (state) => state.config || {}
    },
    actions: {
    }
})
