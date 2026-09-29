import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { LogOut, User, LayoutDashboard, Bell } from 'lucide-react'

export default function Navbar() {
  const { user, logout, getRole } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  const getDashboardPath = () => {
    const role = getRole()
    if (role === 'ADMIN') return '/admin'
    if (role === 'COLLECTOR') return '/collector'
    if (role === 'RECYCLER') return '/recycler'
    return '/user'
  }

  return (
    <nav className="navbar">
      <Link to={user ? getDashboardPath() : '/'} className="navbar-logo">
        ♻️ Kabadiwala
      </Link>
      <div className="navbar-links">
        {user ? (
          <>
            <Link to={getDashboardPath()} className="nav-link">
              <LayoutDashboard size={16} style={{ marginRight: 4 }} />
              Dashboard
            </Link>
            <span className="nav-link" style={{ cursor: 'default', color: 'var(--primary)' }}>
              <User size={14} style={{ marginRight: 4 }} />
              {user.name || user.email}
            </span>
            <button className="btn btn-ghost btn-sm" onClick={handleLogout}>
              <LogOut size={14} /> Logout
            </button>
          </>
        ) : (
          <>
            <Link to="/login" className="nav-link">Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
          </>
        )}
      </div>
    </nav>
  )
}
