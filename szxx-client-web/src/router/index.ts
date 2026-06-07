import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import AppLayout from '../components/AppLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: AppLayout,
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('../views/Home.vue')
        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue'),
      meta: { guest: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue'),
      meta: { guest: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (!userStore.isLoggedIn && !to.meta.guest) {
    next('/login')
  } else if (userStore.isLoggedIn && to.meta.guest) {
    next('/')
  } else {
    next()
  }
})

export default router
