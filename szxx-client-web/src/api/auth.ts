import http from './request'

export function login(data: { username: string; password: string }) {
  return http.post('/auth/login', data)
}

export function register(data: any) {
  return http.post('/auth/register', data)
}

export function getProfile() {
  return http.get('/user/profile')
}
