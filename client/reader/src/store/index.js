import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    doc_list: {}
  },
  actions: {
    init ({commit}) {
      axios.get('/document/list/get')
        .then(function (response) {
          console.log(response.data);
          commit('set_document_list_mu', response.data)
        })
        .catch(function (error) {
          console.log(error)
        })
    }
  },
  mutations: {
    set_document_list_mu (state, docList) {
      state.doc_list = docList
    }
  }
})
