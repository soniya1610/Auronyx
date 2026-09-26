import api from './api';

export const paymentService = {
  // Transactions
  getMyTransactions: async () => {
    const res = await api.get('/api/transactions');
    return res.data.data;
  },

  getTransactionById: async (id) => {
    const res = await api.get(`/api/transactions/${id}`);
    return res.data.data;
  },

  completeTransaction: async (id) => {
    const res = await api.post(`/api/transactions/${id}/complete`);
    return res.data.data;
  },

  getReceipt: async (id) => {
    const res = await api.get(`/api/transactions/${id}/receipt`);
    return res.data.data;
  },

  // Payments
  createPayment: async (data) => {
    const res = await api.post('/api/payments', data);
    return res.data.data;
  },

  getPaymentById: async (id) => {
    const res = await api.get(`/api/payments/${id}`);
    return res.data.data;
  },

  getPaymentStatus: async (id) => {
    const res = await api.get(`/api/payments/${id}/status`);
    return res.data.data;
  },

  // Wallet
  getWallet: async () => {
    const res = await api.get('/api/wallet');
    return res.data.data;
  },

  getWalletBalance: async () => {
    const res = await api.get('/api/wallet/balance');
    return res.data.data;
  },

  getWalletTransactions: async () => {
    const res = await api.get('/api/wallet/transactions');
    return res.data.data;
  },

  getWalletTransactionById: async (id) => {
    const res = await api.get(`/api/wallet/transactions/${id}`);
    return res.data.data;
  },
};
