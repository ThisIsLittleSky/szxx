import request from '@/utils/request'

// 单条用户数据
export interface AdminUserItem {
  id: number
  username: string
  nickname: string
  role: string
  status: string
  avatar: string
  email: string
  phone: string
  createdAt: string
}

// 用户列表查询参数
export interface UserQuery {
  page: number
  size: number
  keyword?: string
  role?: string
  status?: string
}

/**
 * 管理员获取用户列表 GET /admin/users
 */
export function getAdminUserList(params: UserQuery) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 修改用户角色/状态 PUT /admin/users/{userId}
 */
export function updateUserStatusRole(userId: number, data: { role: string; status: string }) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'put',
    data
  })
}