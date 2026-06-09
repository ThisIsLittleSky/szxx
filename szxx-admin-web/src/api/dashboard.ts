import request from '@/utils/request'

/**
 * 仪表盘总览数据 GET /admin/dashboard
 */
export function getDashboardData() {
  return request({
    url: '/admin/dashboard',
    method: 'get'
  })
}

/**
 * 素材分类统计 GET /admin/stats/materials-by-category
 */
export function getMaterialCategoryStats() {
  return request({
    url: '/admin/stats/materials-by-category',
    method: 'get'
  })
}

/**
 * 热门素材排行 GET /admin/stats/top-materials
 */
export function getTopMaterials(params?: { type?: string; limit?: number }) {
  return request({
    url: '/admin/stats/top-materials',
    method: 'get',
    params
  })
}

/**
 * 用户活跃趋势 GET /admin/stats/user-activity
 */
export function getUserActivity(params?: { days?: number }) {
  return request({
    url: '/admin/stats/user-activity',
    method: 'get',
    params
  })
}