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

          response.data.sort((a, b) => {
            var a1 = a.fileName.match(/\.[S|s]\d+[E|e]\d+\./i);
            var b1 = b.fileName.match(/\.[S|s]\d+[E|e]\d+\./i);
            if (a1.length > 0 && b1.length > 0) {
              return a1[0] > b1[0] ? 1 : -1
            }

            if (a1.length > 0) {
              return 1;
            }

            if (b1.length > 0) {
              return -1;
            }

            return 0;
          });
          response.data.forEach(item => {
              item.fileName = item.fileName.substr(0,25);
          });

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
