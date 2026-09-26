import api from './api';

export const qrService = {
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
