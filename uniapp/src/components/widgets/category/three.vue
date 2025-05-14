<template>
    <view class="h-full">
        <tabs
            :current="current"
            @change="handleChange"
            height="80"
            bar-width="60"
            :barStyle="{ bottom: '0' }"
        >
            
            <tab v-for="(item, i) in list" :key="i" :name="item.name">
                <view class="three-list">
                    <three-list :cid="item.id" :i="i" :index="current" v-model:cardType="cardType"></three-list>
                </view>
            </tab>
        </tabs>
    </view>
</template>

<script lang="ts" setup>
import { ref, watch, shallowRef } from 'vue';
import threeList from './three-list.vue';

import { useDataEffect } from "./useDataEffect";
const { list, current } = useDataEffect()

const cardType = ref<string>('square')

const handleChange = (index: number) => {
    console.log(index)
    current.value = Number(index)
}
</script>

<style>
    .three-list {
        height: calc(100vh - 136px - env(safe-area-inset-bottom));
    }
</style>