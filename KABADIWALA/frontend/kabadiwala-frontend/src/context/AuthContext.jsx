import React, { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { authService } from '../services/authService'
import api from '../services/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const loadUser = useCallback(async () => {
    const token = authService.getToken()
    if (!token) {
      setLoading(false)
      return
    }
    try {
      const stored = authService.getUser()
      if (stored) setUser(stored)
      // Re-fetch fresh profile
      const res = await api.get('/users/me')
      const freshUser = res.data.data
      localStorage.setItem('kabadiwala_user', JSON.stringify(freshUser))
      setUser(freshUser)
    } catch {
      authService.logout()
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    loadUser()
  }, [loadUser])

  const login = async (email, password) => {
    setError(null)
    const data = await authService.login({ email, password })
    const freshUser = data.data?.user || authService.getUser()
    setUser(freshUser)
    return data
  }

  const register = async (formData) => {
    setError(null)
    const data = await authService.register(formData)
    return data
  }

  const logout = async () => {
    await authService.logout()
    setUser(null)
  }

  const getRole = () => authService.getUserRole()

  const isAdmin = () => getRole() === 'ADMIN'
  const isCollector = () => getRole() === 'COLLECTOR'
  const isRecycler = () => getRole() === 'RECYCLER'
  const isUser = () => getRole() === 'USER' || !getRole()

  return (
    <AuthContext.Provider value={{ user, loading, error, login, register, logout, getRole, isAdmin, isCollector, isRecycler, isUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
