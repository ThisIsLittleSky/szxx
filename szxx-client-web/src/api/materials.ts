import request from './request'

export function parseVideoUrl(url: string) {
  const params = new URLSearchParams()
  params.append('url', url)
  return request.post('/materials/parse-video', params, {
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  })
}

export function createMaterial(data: FormData) {
  return request.post('/materials', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getMyMaterials(params: {
  page?: number
  size?: number
  keyword?: string
  dynasty?: string
  category?: string
  educationLevel?: string
  sort?: string
}) {
  return request.get('/materials/my', { params })
}

export function getMaterialDetail(id: number | string) {
  return request.get(`/materials/${id}`)
}

export function deleteMaterial(id: number) {
  return request.delete(`/materials/${id}`)
}

export function getMaterials(params: {
  page?: number
  size?: number
  keyword?: string
  dynasty?: string
  category?: string
  educationLevel?: string
  sort?: string
}) {
  return request.get('/materials', { params })
}

export function getFavorites(params: {
  page?: number
  size?: number
  category?: string
  dynasty?: string
}) {
  return request.get('/favorites', { params })
}

export function toggleFavorite(materialId: number) {
  return request.post(`/favorites/toggle/${materialId}`)
}

// ---- 搜索相关 ----

export interface HotSearchItem {
  keyword: string
  count: number
}

export function searchMaterials(params: {
  keyword: string
  page?: number
  size?: number
}) {
  return request.get('/search', { params })
}

export function getSearchSuggest(keyword: string) {
  return request.get('/search/suggest', { params: { keyword } })
}

export function getHotSearch() {
  return request.get('/search/hot')
}
