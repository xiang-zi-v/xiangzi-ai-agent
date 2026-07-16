import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import LoveChat from '../views/LoveChat.vue'
import ManusChat from '../views/ManusChat.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: 'Xiangzi AI Agent | 智能AI助手平台',
    },
  },
  {
    path: '/love-chat',
    name: 'LoveChat',
    component: LoveChat,
    meta: {
      title: 'AI 恋爱大师 | Xiangzi AI',
    },
  },
  {
    path: '/manus-chat',
    name: 'ManusChat',
    component: ManusChat,
    meta: {
      title: 'AI 超级智能体 | Xiangzi AI',
    },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

export default router
