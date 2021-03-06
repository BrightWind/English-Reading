import Vue from 'vue'
import VueX from 'vuex'
import App from './App'
import router from './router'
import store from './store'
import axios from 'axios'
import VueSessionStorage from 'vue-sessionstorage'
import { ObserveVisibility } from 'vue-observe-visibility'
import scroll from 'vue-scroll'

//axios.defaults.baseURL = 'http://127.0.0.1:8090/'
axios.defaults.baseURL = 'http://97.64.18.99:8090/'
Vue.config.productionTip = false

Vue.use(VueX)
Vue.use(VueSessionStorage)
Vue.directive('observe-visibility', ObserveVisibility)
Vue.use(scroll)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
