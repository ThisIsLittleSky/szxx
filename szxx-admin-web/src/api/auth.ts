import request from '@/utils/request'

// 登录参数
export interface LoginParams {
  username: string
  password: string
}

// 登录返回结构
export interface LoginRes {
  code: number
  message: string
  data: {
    token: string
    token_type: string
    expires_in: number
    user: {
      id: number
      username: string
      nickname: string
      role: string
      avatar: string
    }
  }
}

/**
 * 登录 POST /auth/login
 */
export function login(data: LoginParams): Promise<LoginRes> {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 退出登录 POST /auth/logout
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}