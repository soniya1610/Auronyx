import api from './api'

export const notificationService = {
  async getAll() {
    const res = await api.get('/notifications')
    return res.data
  },
  async getUnread() {
    const res = await api.get('/notifications/unread')
    return res.data
  },
  async markRead(id) {
    const res = await api.put(`/notifications/${id}/read`)
    return res.data
  },
  async markAllRead() {
    const res = await api.put('/notifications/read-all')
    return res.data
  },
  async deleteNotification(id) {
    const res = await api.delete(`/notifications/${id}`)
    return res.data
  },
}
