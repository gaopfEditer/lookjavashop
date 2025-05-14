export enum LinkTypeEnum {
    'SHOP_PAGES' = 'shop',
    'ACTIVITY_LIST' = 'activity_list',
    'GOODS_LISTS' = 'goods',
    'CATEGORY_LIST' = 'category_list',
    'CUSTOM_LINK' = 'custom'
}

export interface Link {
    path: string
    name?: string
    type: string
    query?: Record<string, any>
}
