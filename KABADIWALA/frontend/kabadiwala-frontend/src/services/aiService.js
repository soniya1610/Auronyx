import api from './api'

export const aiService = {
  async analyzeWaste(data) {
    const res = await api.post('/ai/analyze', data)
    return res.data
  },
  async classifyWaste(data) {
    const res = await api.post('/ai/classify', data)
    return res.data
  },
  async predictPrice(data) {
    const res = await api.post('/ai/predict-price', data)
    return res.data
  },
}
