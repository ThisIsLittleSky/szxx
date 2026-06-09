import request from '@/utils/request'

/**
 * 获取所有标签 GET /tags
 */
export function getTagList() {
  return request({
    url: '/tags',
    method: 'get'
  })
}

/**
 * 获取分类树(朝代/品类/学段) GET /categories
 */
export function getCategoryTree() {
  return request({
    url: '/categories',
    method: 'get'
  })
}