import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// 先导入 CSS 变量，确保全局可用
import '@/assets/css/variables.css'
import '@/assets/css/global.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// 使用正确的导入方式
import EnumPlugin from './constant/index.js'

const app = createApp(App)

// 导入Element Plus图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(router)
app.use(ElementPlus, { locale: zhCn })
// 使用插件
app.use(EnumPlugin)

app.mount('#app')