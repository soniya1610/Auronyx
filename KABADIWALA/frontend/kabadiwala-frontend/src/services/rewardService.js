import api from './api'

export const rewardService = {
  async getCatalog() {
    const res = await api.get('/rewards/catalog')
    return res.data
  },
  async redeemReward(rewardId) {
    const res = await api.post('/rewards/redeem', { rewardId })
    return res.data
  },
  async getMyRewards() {
    const res = await api.get('/rewards/my-rewards')
    return res.data
  },
}
