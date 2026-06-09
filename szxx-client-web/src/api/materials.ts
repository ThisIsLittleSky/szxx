import request from './request'

export function createMaterial(data: FormData) {
  return request.post('/materials', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
