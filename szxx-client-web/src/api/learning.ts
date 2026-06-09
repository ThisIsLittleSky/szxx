import request from './request'

export function getLearningStats() {
  return request.get('/learning/stats')
}

export function getLearningRecords(params: { page?: number; size?: number }) {
  return request.get('/learning/records', { params })
}

export function reportLearning(data: {
  materialId: number
  duration: number
  completed: boolean
}) {
  return request.post('/learning/report', data)
}

export function getLearningRanking(limit?: number) {
  return request.get('/learning/ranking', { params: { limit: limit || 20 } })
}
