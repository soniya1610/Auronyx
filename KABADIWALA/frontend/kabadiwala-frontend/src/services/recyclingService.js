import api from './api'

export const recyclingService = {
  async getRecyclerIncoming() {
    const res = await api.get('/recycler/incoming')
    return res.data
  },
  async createBatch(data) {
    const res = await api.post('/recycler/batches', data)
    return res.data
  },
  async getEPRCertificates() {
    const res = await api.get('/recycler/epr-certificates')
    return res.data
  },
}
