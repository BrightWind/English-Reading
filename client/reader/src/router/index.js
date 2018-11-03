import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import reference from '@/components/reference'
import document from '@/components/document'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: reference.name,
      component: reference
    },
    {
      path: '/document/:id',
      name: document.name,
      props: true,
      component: document
    },
    {
      path: '/HelloWorld',
      name: 'HelloWorld',
      component: HelloWorld
    }
  ]
})
