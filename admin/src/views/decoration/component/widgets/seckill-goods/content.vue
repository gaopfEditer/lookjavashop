<template>
    <div class="seckill-goods mx-[10px] my-[10px]">

        <div
            class="goods-list flex mb-[10px] p-[10px] bg-white rounded-lg overflow-hidden"
            v-for="(item, index) in seckillList"
        >
            <image-contain
                :src="item.image"
                :width="90"
                :height="90"
                :preview-teleported="true"
                fit="contain"
            />
            <div class="ml-[10px]">
                <div class="line-2 w-[210px] h-[40px]">{{ item.name }}</div>
                <div class="mt-[5px]">
                    <div class="seckill-sales-num pr-2 rounded-full inline-block text-xs">
                        <span class="text-[#FF2C3C] ml-2">
                            已抢 {{ item.salesVolume }} 件
                        </span>
                    </div>
                    <div class="flex items-center justify-between mt-[5px]">
                        <div class="text-xs ">
                            <span class="text-[#FF2C3C]">
                                ¥<span class="font-medium text-lg">
                                    {{ item.minSeckillPrice }}
                                </span>
                            </span>
                        </div>
                        <div class="buy-btn text-sm text-white px-[15px] py-[5px] rounded-full h-[28px]">立即抢购</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { decorateSeckillLists } from '@/api/decoration'

const seckillList = ref([])

const getData = async () => {
    const data = await decorateSeckillLists()
    seckillList.value = data
}
getData()
</script>

<style lang="scss" scoped>
.seckill-goods {
    .seckill-sales-num {
        background-color: rgba($color: #ed5349, $alpha: 0.1);
    }
    .buy-btn {
        background: linear-gradient(90.00deg, #f95f2f 0%, #ff2c3c 100%);
    }
}
</style>