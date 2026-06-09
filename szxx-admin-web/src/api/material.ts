import request from '@/utils/request'

// 管理员素材项
export interface AdminMaterialItem {
  id: number
  title: string
  author: string
  dynasty: string
  category: string
  education_level: string
  status: string
  created_at: string
  uploader_id: number
}

// 素材列表查询参数
export interface MaterialQuery {
  page: number
  size: number
  keyword?: string
  status?: string
  category?: string
  uploader_id?: number
}

/**
 * 管理员素材列表 GET /admin/materials
 */
export function getAdminMaterialList(params: MaterialQuery) {
  return request({
    url: '/admin/materials',
    method: 'get',
    params
  })
}

/**
 * 删除素材 DELETE /materials/{id}
 */
export function deleteMaterial(id: number) {
  return request({
    url: `/materials/${id}`,
    method: 'delete'
  })
}

/**
 * 素材审核 PUT /admin/materials/{id}/review
 */
export function reviewMaterial(id: number, data: { status: string; review_comment: string }) {
  return request({
    url: `/admin/materials/${id}/review`,
    method: 'put',
    data
  })
}