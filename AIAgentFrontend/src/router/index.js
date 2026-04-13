import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import PageAiAssistant from '../pages/PageAiAssistant.vue'
import PageAiAgent from '../pages/PageAiAgent.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'home', component: Home },
    { path: '/assistant', name: 'assistant', component: PageAiAssistant },
    { path: '/agent', name: 'agent', component: PageAiAgent },
  ],
})

export default router
