import Vue from 'vue'
import Vuex from 'vuex'
import user from './user'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    doc_list: {}
  },
  actions: {
    get_document_list () {
      axios.get('/user?ID=12345')
        .then(function (response) {
          // handle success
          console.log(response)
        })
        .catch(function (error) {
          // handle error
          console.log(error)
        })
        .then(function () {
          // always executed
        })
    }
  },
  strict: process.env.NODE_ENV !== 'production', //在非生产环境下，使用严格模式
  modules: {
    user
  }
})
