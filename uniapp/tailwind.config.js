/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ['./index.html', './src/**/*.{html,js,ts,jsx,tsx,vue}'],
    theme: {
        colors: {
            white: '#ffffff',
            black: '#000000',
            main: '#333333',
            content: '#666666',
            muted: '#999999',
            light: '#e5e5e5',
            primary: {
                DEFAULT: 'var(--theme-color, #FFC244)',
                'light-3': 'var(--color-primary-light-3, rgb(255, 217, 130))',
                'light-5': 'var(--color-primary-light-5, rgb(255, 228, 166))',
                'light-7': 'var(--color-primary-light-7, rgb(255, 239, 202))',
                'light-9': 'var(--color-primary-light-9, rgb(255, 250, 237))',
                'dark-2': 'var(--color-primary-dark-2, rgb(204, 161, 62))'
            },
            success: '#5ac725',
            warning: '#f9ae3d',
            error: '#f56c6c',
            info: '#909399',
            page: '#f6f6f6'
        },
        fontSize: {
            xs: '24rpx',
            sm: '26rpx',
            base: '28rpx',
            lg: '30rpx',
            xl: '32rpx',
            '2xl': '34rpx',
            '3xl': '38rpx',
            '4xl': '40rpx',
            '5xl': '44rpx'
        }
    },
    plugins: [],
    corePlugins: {
        preflight: false
    }
}
