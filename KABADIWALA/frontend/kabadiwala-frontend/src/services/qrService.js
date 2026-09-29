import api from './api'

export const qrService = {
  async generateQR(data) {
    const res = await api.post('/qr/generate', data)
    return res.data
  },
  async verifyQR(code) {
    const res = await api.post('/qr/verify', { code })
    return res.data
  },
}
