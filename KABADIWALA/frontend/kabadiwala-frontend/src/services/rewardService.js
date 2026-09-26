import api from './api';

export const rewardService = {
  getRewards: async () => {
    const res = await api.get('/api/rewards');
    return res.data.data;
  },

  getMyPoints: async () => {
    const res = await api.get('/api/points');
    return res.data.data;
  },

  getBadges: async () => {
    const res = await api.get('/api/badges');
    return res.data.data;
  },

  getChallenges: async () => {
    const res = await api.get('/api/challenges');
    return res.data.data;
  },

  redeemReward: async (data) => {
    const res = await api.post('/api/redemptions', data);
    return res.data.data;
  },

  getRedemptionHistory: async () => {
    const res = await api.get('/api/redemptions');
    return res.data.data;
  },
};
