import { ref } from "vue"

const list = ref<any>([]);
const page = ref<any>([]);
const current = ref(0)

export const useDataEffect = () => {


    const changeActive = (index: number) => {
        current.value = index;
    }

    return {
        list,
        page,
        current,
        changeActive
    }
}