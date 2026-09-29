import api from './api'

export const authService = {
  async register(data) {
    const res = await api.post('/auth/register', data)
    return res.data
  },

  async login(data) {
    const res = await api.post('/auth/login', data)
    const { token, user } = res.data.data
    localStorage.setItem('kabadiwala_token', token)
    localStorage.setItem('kabadiwala_user', JSON.stringify(user))
    return res.data
  },

  async logout() {
    await api.post('/auth/logout').catch(() => {})
    localStorage.removeItem('kabadiwala_token')
    localStorage.removeItem('kabadiwala_user')
  },

  async sendOtp(target, purpose) {
    const res = await api.post('/auth/send-otp', { target, purpose })
    return res.data
  },

  async verifyOtp(target, otp, purpose) {
    const res = await api.post('/auth/verify-otp', { target, otp, purpose })
    return res.data
  },

  async forgotPassword(email) {
    const res = await api.post('/auth/forgot-password', { email })
    return res.data
  },

  async resetPassword(data) {
    const res = await api.post('/auth/reset-password', data)
    return res.data
  },

  getToken() {
    return localStorage.getItem('kabadiwala_token')
  },

  getUser() {
    const raw = localStorage.getItem('kabadiwala_user')
    return raw ? JSON.parse(raw) : null
  },

  isAuthenticated() {
    return !!localStorage.getItem('kabadiwala_token')
  },

  getUserRole() {
    const user = this.getUser()
    if (!user || !user.roles) return 'USER'
    if (user.roles.includes('ROLE_ADMIN')) return 'ADMIN'
    if (user.roles.includes('ROLE_COLLECTOR')) return 'COLLECTOR'
    if (user.roles.includes('ROLE_RECYCLER')) return 'RECYCLER'
    return 'USER'
  }
}
