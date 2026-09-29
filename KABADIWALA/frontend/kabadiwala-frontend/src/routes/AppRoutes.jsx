import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Splash from '../pages/Splash'
import Home from '../pages/Home'
import Login from '../components/auth/Login'
import Register from '../components/auth/Register'
import UserDashboard from '../pages/UserDashboard'
import CollectorDashboard from '../pages/CollectorDashboard'
import AdminDashboard from '../pages/AdminDashboard'
import RecyclerDashboard from '../pages/RecyclerDashboard'

function ProtectedRoute({ children, allowedRoles }) {
  const { user, loading, getRole } = useAuth()
  if (loading) return <div className="loading-overlay"><div className="spinner"></div><span>Loading...</span></div>
  if (!user) return <Navigate to="/login" replace />
  if (allowedRoles && !allowedRoles.includes(getRole())) return <Navigate to="/dashboard" replace />
  return children
}

function GuestRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="loading-overlay"><div className="spinner"></div><span>Loading...</span></div>
  if (user) return <Navigate to="/dashboard" replace />
  return children
}

function DashboardRedirect() {
  const { getRole } = useAuth()
  const role = getRole()
  if (role === 'ADMIN') return <Navigate to="/admin" replace />
  if (role === 'COLLECTOR') return <Navigate to="/collector" replace />
  if (role === 'RECYCLER') return <Navigate to="/recycler" replace />
  return <Navigate to="/user" replace />
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Splash />} />
      <Route path="/home" element={<Home />} />
      <Route path="/login" element={<GuestRoute><Login /></GuestRoute>} />
      <Route path="/register" element={<GuestRoute><Register /></GuestRoute>} />
      <Route path="/dashboard" element={<ProtectedRoute><DashboardRedirect /></ProtectedRoute>} />
      <Route path="/user/*" element={<ProtectedRoute allowedRoles={['USER']}><UserDashboard /></ProtectedRoute>} />
      <Route path="/collector/*" element={<ProtectedRoute allowedRoles={['COLLECTOR']}><CollectorDashboard /></ProtectedRoute>} />
      <Route path="/recycler/*" element={<ProtectedRoute allowedRoles={['RECYCLER']}><RecyclerDashboard /></ProtectedRoute>} />
      <Route path="/admin/*" element={<ProtectedRoute allowedRoles={['ADMIN']}><AdminDashboard /></ProtectedRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
