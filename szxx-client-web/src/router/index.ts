import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import { pinia } from '../stores/pinia'
import AppLayout from '../components/AppLayout.vue'
import MainLayout from '../components/MainLayout.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // ---- 公开页面（AppLayout：顶部导航 + 首页单页） ----
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

    // 登录/注册（无布局）
    {
      path: '/login',
      name: 'Login',
      component: Login,
      meta: { guest: true }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: { guest: true }
    },

    // ---- 认证页面（MainLayout：侧边栏 + 顶栏） ----
    {
      path: '/home',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('../views/HomeDashboard.vue')
        }
      ]
    },
    {
      path: '/search',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Search',
          component: () => import('../views/Search.vue')
        }
      ]
    },
    {
      path: '/category',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Category',
          component: () => import('../views/Category.vue')
        }
      ]
    },
    {
      path: '/course',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Course',
          component: () => import('../views/Course.vue')
        }
      ]
    },
    {
      path: '/course/:level',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'CourseLevel',
          component: () => import('../views/Course.vue')
        }
      ]
    },
    {
      path: '/material/:id',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'MaterialDetail',
          component: () => import('../views/MaterialDetail.vue')
        }
      ]
    },
    {
      path: '/learning',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Learning',
          component: () => import('../views/Learning.vue')
        }
      ]
    },
    {
      path: '/favorites',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Favorites',
          component: () => import('../views/Favorites.vue')
        }
      ]
    },
    {
      path: '/profile',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Profile',
          component: () => import('../views/Profile.vue')
        }
      ]
    },
    {
      path: '/upload',
      component: MainLayout,
      meta: { requiresAuth: true, roles: ['teacher', 'admin'] },
      children: [
        {
          path: '',
          name: 'Upload',
          component: () => import('../views/Upload.vue')
        }
      ]
    },
    {
      path: '/my-materials',
      component: MainLayout,
      meta: { requiresAuth: true, roles: ['teacher', 'admin'] },
      children: [
        {
          path: '',
          name: 'MyMaterials',
          component: () => import('../views/MyMaterials.vue')
        }
      ]
    },
    {
      path: '/help',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Help',
          component: () => import('../views/Help.vue')
        }
      ]
    },
    {
      path: '/about',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'About',
          component: () => import('../views/About.vue')
        }
      ]
    },
    {
      path: '/forgot-password',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'ForgotPassword',
          component: () => import('../views/ForgotPassword.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const userStore = useUserStore(pinia)
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return `/login?redirect=${to.path}`
  }
  if (to.meta.guest && userStore.isLoggedIn) {
    return '/home'
  }
  const roles = to.meta.roles as string[] | undefined
  if (roles && !roles.includes(userStore.user?.role || '')) {
    return '/home'
  }
})

export default router
