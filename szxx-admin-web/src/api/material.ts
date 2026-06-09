import request from '@/utils/request'

// 管理员素材项
export interface AdminMaterialItem {
  id: string
  title: string
  author: string
  dynasty: string
  category: string
  educationLevel: string
  tags: string
  coverImage: string
  summary: string
  content: string
  videoUrl: string
  status: string
  reviewComment: string
  viewCount: number
  uploaderId: string
  uploaderName: string
  createdAt: string
  updatedAt: string
}

// 附件项
export interface AttachmentItem {
  id: string
  materialId: string
  filename: string
  filePath: string
  fileType: string
  fileSize: number
  createdAt: string
}

// 管理员素材详情
export interface AdminMaterialDetail {
  material: AdminMaterialItem
  attachments: AttachmentItem[]
}

// 素材列表查询参数
export interface MaterialQuery {
  page: number
  size: number
  keyword?: string
  status?: string
  category?: string
  uploaderId?: number
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
 * 管理员素材详情 GET /admin/materials/{id}
 */
export function getAdminMaterialDetail(id: string) {
  return request<AdminMaterialDetail>({
    url: `/admin/materials/${id}`,
    method: 'get'
  })
}

/**
 * 删除素材 DELETE /materials/{id}
 */
export function deleteMaterial(id: string) {
  return request({
    url: `/admin/materials/${id}`,
    method: 'delete'
  })
}

/**
 * 素材审核 PUT /admin/materials/{id}/review
 */
export function reviewMaterial(id: string, data: { status: string; reviewComment: string }) {
  return request({
    url: `/admin/materials/${id}/review`,
    method: 'put',
    data
  })
}