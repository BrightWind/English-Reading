import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import reference from '@/components/reference'
import document from '@/components/document'
import whitelist from '@/components/whitelist'


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
      path: '/whitelist/:id',
      name: whitelist.name,
      props: true,
      component: whitelist
    },
    {
      path: '/HelloWorld',
      name: 'HelloWorld',
      component: HelloWorld
    }
  ]
})
