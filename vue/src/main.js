import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/gloable.css'
import request from './utils/request'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

// 使用 Element UI
Vue.use(ElementUI, { 
  size: "mini",
  message: {
    duration: 3000,
    showClose: true
  }
})

Vue.config.productionTip = false

// 挂载请求工具
Vue.prototype.$request = request

// 使用 mavonEditor
Vue.use(mavonEditor)

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
