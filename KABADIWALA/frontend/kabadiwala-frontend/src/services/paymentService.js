import api from './api'

export const paymentService = {
  async getBalance() {
    const res = await api.get('/wallet/balance')
    return res.data
  },
  async getTransactions() {
    const res = await api.get('/wallet/transactions')
    return res.data
  },
}
