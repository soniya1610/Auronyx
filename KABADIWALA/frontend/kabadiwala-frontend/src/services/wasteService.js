import api from './api'

export const wasteService = {
  async getCategories() {
    const res = await api.get('/waste/categories')
    return res.data
  },
  async estimatePrice(category, weightKg) {
    const res = await api.post('/waste/estimate-price', { category, weightKg })
    return res.data
  },
}
