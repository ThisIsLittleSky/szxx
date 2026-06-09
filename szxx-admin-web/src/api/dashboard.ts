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