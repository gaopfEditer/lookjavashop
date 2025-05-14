export default () => ({
    title: '我的订单',
    name: 'my-order',
    content: {
        title: '我的订单',
        data: [
            {
                image: '',
                name: '待付款',
                display: true,
                link: '/pages/order/order?type=1'
            },
            {
                image: '',
                name: '待发货',
                display: true,
                link: '/pages/order/order?type=2'
            },
            {
                image: '',
                name: '待收货',
                display: true,
                link: '/pages/order/order?type=3'
            },
            {
                image: '',
                name: '商品评价',
                display: true,
                link: '/pages/order/order?type=4'
            },
            {
                image: '',
                name: '售后退款',
                display: true,
                link: '/pages/order/order?type=5'
            }
        ]
    },
    styles: {}
})
