import request from '@/utils/request'

/**
 * 获取标签列表 GET /admin/tags
 */
export function getTagList() {
  return request({
    url: '/admin/tags',
    method: 'get'
  })
}

/**
 * 新增标签 POST /admin/tags
 */
export function addTag(data: { name: string }) {
  return request({
    url: '/admin/tags',
    method: 'post',
    data
  })
}

/**
 * 编辑标签 PUT /admin/tags/{id}
 */
export function editTag(id: number, data: { name: string }) {
  return request({
    url: `/admin/tags/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除标签 DELETE /admin/tags/{id}
 */
export function delTag(id: number) {
  return request({
    url: `/admin/tags/${id}`,
    method: 'delete'
  })
}
