import api from './api';

export const recyclingService = {
  // User endpoints
  getMyRecyclingRecords: async () => {
    const res = await api.get('/api/recycling');
    return res.data.data;
  },

  getRecyclingRecordById: async (id) => {
    const res = await api.get(`/api/recycling/${id}`);
    return res.data.data;
  },

  getTimeline: async (id) => {
    const res = await api.get(`/api/recycling/${id}/timeline`);
    return res.data.data;
  },

  // Recycler endpoints
  getIncomingWaste: async () => {
    const res = await api.get('/api/recycler/incoming');
    return res.data.data;
  },

  getRecyclerRecords: async () => {
    const res = await api.get('/api/recycler/records');
    return res.data.data;
  },

  receiveWaste: async (id) => {
    const res = await api.post(`/api/recycler/records/${id}/receive`);
    return res.data.data;
  },

  updateRecyclingStatus: async (id, data) => {
    const res = await api.put(`/api/recycler/records/${id}/status`, data);
    return res.data.data;
  },

  submitProcessing: async (id, data) => {
    const res = await api.post(`/api/recycler/records/${id}/processing`, data);
    return res.data.data;
  },

  completeRecycling: async (id) => {
    const res = await api.post(`/api/recycler/records/${id}/complete`);
    return res.data.data;
  },

  // QR
  generateQR: async (transactionId) => {
    const res = await api.post(`/api/qr/generate/${transactionId}`);
    return res.data.data;
  },

  getQRByTransaction: async (transactionId) => {
    const res = await api.get(`/api/qr/transaction/${transactionId}`);
    return res.data.data;
  },

  lookupQR: async (code) => {
    const res = await api.get(`/api/qr/${code}`);
    return res.data.data;
  },
};
