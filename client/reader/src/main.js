import Vue from 'vue'
import VueX from 'vuex'
import App from './App'
import router from './router'
import store from './store'
Vue.config.productionTip = false
Vue.use(VueX)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
