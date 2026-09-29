import api from './api'

export const pickupService = {
  async bookPickup(data) {
    const res = await api.post('/pickups/book', data)
    return res.data
  },
  async getMyPickups() {
    const res = await api.get('/pickups/my-pickups')
    return res.data
  },
  async getPickup(id) {
    const res = await api.get(`/pickups/${id}`)
    return res.data
  },
  // Collector
  async getCollectorPickups() {
    const res = await api.get('/collector/pickups')
    return res.data
  },
  async acceptPickup(id) {
    const res = await api.put(`/collector/pickups/${id}/accept`)
    return res.data
  },
  async verifyWeight(id, actualWeightKg) {
    const res = await api.put(`/collector/pickups/${id}/verify-weight`, { actualWeightKg })
    return res.data
  },
  async completePickup(id, paymentMethod = 'WALLET') {
    const res = await api.put(`/collector/pickups/${id}/complete`, { paymentMethod })
    return res.data
  },
}
