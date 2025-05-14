<template>
    <view class="category">
        <template v-if="page.type === 1">
            <one class="container"></one>
        </template>
        <template v-if="page.type === 2">
            <two class="container"></two>
        </template>
        <template v-if="page.type === 3">
            <three class="container"></three>
        </template>
        <template v-if="page.type === 4">
            <four class="container"></four>
        </template>
    </view>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import One from './one.vue'
import Two from './two.vue'
import Three from './three.vue'
import Four from './four.vue'
import { useDataEffect } from './useDataEffect'

const { page } = useDataEffect()
</script>

<style>
.container {
    height: calc(100vh - 300rpx);
}
</style>
