import api from './api';

export const authService = {
  register: async (data) => {
    const res = await api.post('/api/auth/register', data);
    return res.data.data;
  },

  login: async (credentials) => {
    const res = await api.post('/api/auth/login', credentials);
    const { token, user } = res.data.data;
    localStorage.setItem('kabadiwala_token', token);
    localStorage.setItem('kabadiwala_user', JSON.stringify(user));
    return res.data.data;
  },

  logout: () => {
    localStorage.removeItem('kabadiwala_token');
    localStorage.removeItem('kabadiwala_user');
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('kabadiwala_user');
    return user ? JSON.parse(user) : null;
  },

  getToken: () => localStorage.getItem('kabadiwala_token'),

  isAuthenticated: () => !!localStorage.getItem('kabadiwala_token'),
};
