<template>
    <view class="sort-icon">
        <!-- Up -->
        <u-icon name="arrow-up-fill" :size="size" :color="status === 'desc' ? $theme.primaryColor : color"
            :customStyle="{ transform: 'scale(0.55) translateY(5px)' }" />
        <!-- Down -->
        <u-icon name="arrow-down-fill" :size="size" :color="status === 'asc' ? $theme.primaryColor : color"
            :customStyle="{ transform: 'scale(0.55)  translateY(-5px)' }" />
    </view>
</template>

<script lang="ts" setup>
/**
 * @description 排序升降序图标
 * @property {String} status 升降序状态 [desc | asc]
 * @property {Number|String} size 图标大小 (默认: 28)
 * @property {String} color 图标颜色 (默认: #999999)
 * @property {String} active-color 激活状态图标颜色 (默认: #101010)
 * @example <sort-icon :status="search.price" :active-color="themeColor" />
 */

withDefaults(
    defineProps<{
        status?: string // 升降序状态 [desc | asc]
        size?: string // 图标大小
        color?: string // 图标颜色
        activeColor?: string // 选中颜色
    }>(),
    {
        status: '',
        size: '24',
        color: '',
        activeColor: '#FF2C3C'
    }
)
</script>

<style lang="scss" scoped>
.sort-icon {
    display: flex;
    flex-direction: column;
    align-items: center;
}
</style>
