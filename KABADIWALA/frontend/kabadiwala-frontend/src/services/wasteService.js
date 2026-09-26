import api from './api';

export const wasteService = {
  getCategories: async () => {
    const res = await api.get('/api/waste/categories');
    return res.data.data;
  },

  getCategoryById: async (id) => {
    const res = await api.get(`/api/waste/categories/${id}`);
    return res.data.data;
  },

  getItems: async () => {
    const res = await api.get('/api/waste/items');
    return res.data.data;
  },

  getItemById: async (id) => {
    const res = await api.get(`/api/waste/items/${id}`);
    return res.data.data;
  },

  getPrices: async () => {
    const res = await api.get('/api/waste/prices');
    return res.data.data;
  },

  getPriceByCategory: async (categoryId) => {
    const res = await api.get(`/api/waste/prices/${categoryId}`);
    return res.data.data;
  },
};
