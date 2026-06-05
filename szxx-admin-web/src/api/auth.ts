import http from './request'

export function login(data: { username: string; password: string }) {
  return http.post('/auth/login', data)
}

export function getProfile() {
  return http.get('/user/profile')
}
