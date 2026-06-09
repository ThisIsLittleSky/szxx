import axios from 'axios'
import { ElMessage } from 'element-plus'

// 后端基准地址
const service = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
})

// 请求拦截器：自动携带 JWT Token
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理文档定义的状态码
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 业务码非200，直接抛出错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求异常')
      return Promise.reject(res)
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    switch (status) {
      case 401:
        ElMessage.error('登录已失效，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
        break
      case 403:
        ElMessage.error('暂无操作权限')
        break
      case 404:
        ElMessage.error('请求资源不存在')
        break
      case 500:
        ElMessage.error('服务器内部错误')
        break
      default:
        ElMessage.error('网络请求失败')
    }
    return Promise.reject(error)
  }
)

export default service