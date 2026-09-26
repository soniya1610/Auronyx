import api from './api';

export const pickupService = {
  createPickup: async (data) => {
    const res = await api.post('/api/pickups', data);
    return res.data.data;
  },

  getMyPickups: async () => {
    const res = await api.get('/api/pickups');
    return res.data.data;
  },

  getPickupById: async (id) => {
    const res = await api.get(`/api/pickups/${id}`);
    return res.data.data;
  },

  updatePickup: async (id, data) => {
    const res = await api.put(`/api/pickups/${id}`, data);
    return res.data.data;
  },

  cancelPickup: async (id) => {
    const res = await api.put(`/api/pickups/${id}/cancel`);
    return res.data.data;
  },

  getPickupStatus: async (id) => {
    const res = await api.get(`/api/pickups/${id}/status`);
    return res.data.data;
  },

  // Collector endpoints
  getNearbyPickups: async () => {
    const res = await api.get('/api/collector/pickups/nearby');
    return res.data.data;
  },

  getCollectorPickups: async () => {
    const res = await api.get('/api/collector/pickups');
    return res.data.data;
  },

  acceptPickup: async (id) => {
    const res = await api.post(`/api/collector/pickups/${id}/accept`);
    return res.data.data;
  },

  rejectPickup: async (id) => {
    const res = await api.post(`/api/collector/pickups/${id}/reject`);
    return res.data.data;
  },

  updatePickupStatus: async (id, status) => {
    const res = await api.put(`/api/collector/pickups/${id}/status`, { status });
    return res.data.data;
  },

  // Verification
  getVerification: async (pickupId) => {
    const res = await api.get(`/api/collector/pickups/${pickupId}/verification`);
    return res.data.data;
  },

  submitVerification: async (pickupId, data) => {
    const res = await api.post(`/api/collector/pickups/${pickupId}/verification`, data);
    return res.data.data;
  },

  updateVerification: async (pickupId, data) => {
    const res = await api.put(`/api/collector/pickups/${pickupId}/verification`, data);
    return res.data.data;
  },
};
