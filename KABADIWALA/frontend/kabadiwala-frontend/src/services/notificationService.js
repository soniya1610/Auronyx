import api from './api';

export const notificationService = {
  getNotifications: async () => {
    const res = await api.get('/api/notifications');
    return res.data.data;
  },

  markAsRead: async (id) => {
    const res = await api.put(`/api/notifications/${id}/read`);
    return res.data.data;
  },

  markAllAsRead: async () => {
    const res = await api.put('/api/notifications/read-all');
    return res.data.data;
  },
};
