import api from './api';

export const aiService = {
  analyzeWaste: async (formData) => {
    const res = await api.post('/api/ai/analyze', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return res.data.data;
  },

  getAnalysis: async (id) => {
    const res = await api.get(`/api/ai/analysis/${id}`);
    return res.data.data;
  },

  getHealth: async () => {
    const res = await api.get('/api/ai/health');
    return res.data.data;
  },
};
