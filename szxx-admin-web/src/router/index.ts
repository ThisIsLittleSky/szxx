import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入组件（用相对路径，避免别名解析问题）
const Login = () => import('../views/Login.vue')
const AdminLayout = () => import('../layout/AdminLayout.vue')
const Dashboard = () => import('../views/admin/Dashboard.vue')
const MaterialList = () => import('../views/admin/MaterialList.vue')
const UserManage = () => import('../views/admin/UserManage.vue')
const TagManage = () => import('../views/admin/TagManage.vue')

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'Login', component: Login },
  // 后台嵌套路由，共用AdminLayout布局
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: Dashboard },
      { path: 'material/list', name: 'MaterialList', component: MaterialList },
      { path: 'user', name: 'UserManage', component: UserManage },
      { path: 'tag', name: 'TagManage', component: TagManage }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function isAdmin(): boolean {
  const userInfo = localStorage.getItem('userInfo')
  if (!userInfo) return false
  try {
    return JSON.parse(userInfo).role === 'admin'
  } catch {
    return false
  }
}

// 路由守卫：未登录拦截后台页面，非管理员禁止访问
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')

  // 访问登录页：已登录管理员则跳转到后台首页，否则清空登录态并放行
  if (to.path === '/login') {
    if (token && isAdmin()) {
      next('/admin/dashboard')
    } else {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      next()
    }
    return
  }

  // 访问后台页面：必须有token且为管理员，否则拦截到登录页
  if (to.path.startsWith('/admin')) {
    if (!token || !isAdmin()) {
      if (token && !isAdmin()) {
        ElMessage.warning('仅管理员可访问后台')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      } else {
        ElMessage.warning('请先登录后再访问')
      }
      next('/login')
    } else {
      next()
    }
    return
  }

  // 其他页面直接放行
  next()
})

export default router