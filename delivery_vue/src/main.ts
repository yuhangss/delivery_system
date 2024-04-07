import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import request from './utils/request'
// import installElementPlus from './plugins/element'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'


const  app = createApp(App)
app.use(
    ElementPlus ,{
        size:"small",
        zIndex:3000,
        locale:zhCn
    }
)

// installElementPlus(app)

app.use(store).use(router)

app.config.globalProperties.request=request

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')