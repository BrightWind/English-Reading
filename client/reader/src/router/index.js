import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Reference from '@/components/Reference'
import Document from '@/components/Document'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Reference',
      component: Reference
    },
    {
      path: '/Document',
      name: 'Document',
      component: Document
    },
    {
      path: '/HelloWorld',
      name: 'HelloWorld',
      component: HelloWorld
    }
  ]
})
