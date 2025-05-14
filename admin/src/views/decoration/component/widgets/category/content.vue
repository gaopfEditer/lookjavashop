<template>
    <div class="category h-[640px]">
        <img
            :src="getImagesUrl"
            class="absolute top-0 left-0 z-10 w-full"
            alt="排名"
        />
    </div>
</template>

<script lang="ts" setup>
import No1 from './images/No.1.png'
import No2 from './images/No.2.png'
import No3 from './images/No.3.png'
import No4 from './images/No.4.png'
import type { PropType } from 'vue'
import type options from './options'
type OptionsType = ReturnType<typeof options>
const props = defineProps({
    content: {
        type: Object as PropType<OptionsType['content']>,
        default: () => ({})
    },
    styles: {
        type: Object as PropType<OptionsType['styles']>,
        default: () => ({})
    }
})

const imageArr = [No1, No2, No3, No4]
const getImagesUrl = computed(() => imageArr[props.content.type - 1])
</script>