import type { Theme } from 'vitepress'
import DefaultTheme from 'vitepress/theme'
import MyLayout from './MyLayout.vue'
import './style.css'

// https://vitepress.dev/guide/custom-theme
export default {
    extends: DefaultTheme,
    Layout: MyLayout,
} satisfies Theme
