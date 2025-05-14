<template>
    <el-select
       v-model="valueStr"
       filterable
       remote
       reserve-keyword
       placeholder="请输入自提点名称搜索"
       :remote-method="remoteMethod"
       :loading="loading"
       @focus="remoteMethod('')"
       @change="change"
       value-key="id"
       v-select-loadmore="onLoadmore"
       popper-class="single-select-loadmore"
       remote-show-suffix
       clearable
     >
       <el-option
         v-for="item in options"
         :key="item.id"
         :label="item.name"
         :value="item"
       />
     </el-select>
</template>
<script lang="ts" setup name="selectorselffetchOne">
import { ref, defineEmits, watch } from 'vue'
import { selffetchshopLists } from "@/api/selffetchshop/selffetchshop"
import { isEmpty } from "@/utils/util"

//define datas
const emit = defineEmits(["change"])
const props = defineProps(["title", "isPublic"])
interface ListItem {
 id: string
 title: string
}
const options = ref<ListItem[]>([])
const valueStr = ref<Object>([])
const loading = ref(false)
const pageNum  = ref(1)
const pageSize = ref(10)
const queryStr = ref('')
const canload = ref(true)

//define
watch(
   () => props.title,
   (value, old) => {
     if (!isEmpty(value)) {
       valueStr.value = value
     }
     //console.log(value, old)
   },
   {
       immediate: true
   }
)


//methods
const remoteMethod = (query: string) => {
   loading.value = true
   queryStr.value = query
   pageNum.value = 1
   canload.value = true
   selffetchshopLists({
     name: query,
     isPublic: props.isPublic,
     pageNum: pageNum.value,
     pageSize: pageSize.value
   }).then((rep:any)=> {
     options.value = rep.lists
   }).finally((rep) => {
     loading.value = false
   })
}

const loadMoreList = () => {
   if (canload.value == false) {
     return 
   }
   selffetchshopLists({
     name: queryStr.value,
     isPublic: 1,
     pageNum: pageNum.value,
     pageSize: pageSize.value
   }).then((rep:any)=> {
     if (rep.lists.length > 0) {
       rep.lists.map((item: Object) => {
         options.value.push(item)
       })
     } else {
       canload.value = false
     }
     //options.value = rep.data.list
   }).finally((rep) => {
   })
}

const change = ((e: any) => {
 emit("change", valueStr.value)
})

const onLoadmore = () => {
 pageNum.value++
 loadMoreList()
};
</script>