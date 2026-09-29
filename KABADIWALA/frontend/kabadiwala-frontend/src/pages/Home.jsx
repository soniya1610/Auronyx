import React from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Home() {
  const { user } = useAuth()
  if (user) return <Navigate to="/dashboard" replace />
  return <Navigate to="/" replace />
}
