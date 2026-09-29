import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import Navbar from '../components/common/Navbar'
import Footer from '../components/common/Footer'
import api from '../services/api'
import {
  Users, Truck, Package, Activity, RefreshCw, Shield, TrendingUp
} from 'lucide-react'

export default function AdminDashboard() {
  const { user } = useAuth()
  const [stats, setStats] = useState({ totalUsers: 0, totalCollectors: 0, totalPickups: 0, totalRecycled: 0 })
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState('overview')

  const loadData = async () => {
    setLoading(true)
    try {
      const [sRes, uRes] = await Promise.allSettled([
        api.get('/admin/stats'),
        api.get('/admin/users'),
      ])
      if (sRes.status === 'fulfilled') setStats(sRes.value.data?.data || stats)
      if (uRes.status === 'fulfilled') setUsers(uRes.value.data?.data || [])
    } catch {}
    setLoading(false)
  }

  useEffect(() => { loadData() }, [])

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="main-content">
        <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h1 className="page-title">Admin Dashboard 🔧</h1>
            <p className="page-subtitle">System overview &amp; management</p>
          </div>
          <button className="btn btn-ghost btn-sm" onClick={loadData}><RefreshCw size={14} /> Refresh</button>
        </div>

        {/* Stats */}
        <div className="grid-4 mb-4">
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(59,130,246,0.12)', color: 'var(--info)' }}><Users size={22} /></div>
            <div className="stat-value">{stats.totalUsers || users.length}</div>
            <div className="stat-label">Total Users</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(16,185,129,0.12)', color: 'var(--primary)' }}><Truck size={22} /></div>
            <div className="stat-value">{stats.totalCollectors || 0}</div>
            <div className="stat-label">Collectors</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(245,158,11,0.12)', color: 'var(--accent)' }}><Package size={22} /></div>
            <div className="stat-value">{stats.totalPickups || 0}</div>
            <div className="stat-label">Total Pickups</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(139,92,246,0.12)', color: '#a78bfa' }}><TrendingUp size={22} /></div>
            <div className="stat-value">{stats.totalRecycled || 0} kg</div>
            <div className="stat-label">Waste Recycled</div>
          </div>
        </div>

        {/* Tabs */}
        <div className="tabs">
          {['overview', 'users', 'system'].map(t => (
            <button key={t} className={`tab ${activeTab === t ? 'active' : ''}`} onClick={() => setActiveTab(t)}>
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>

        {activeTab === 'overview' && (
          <div className="fade-in">
            <h3 className="section-title">System Status</h3>
            <div className="card" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <Activity size={22} color="var(--primary)" />
              <div>
                <p className="font-semibold">All Systems Operational</p>
                <p className="text-muted text-sm">Backend, Database, and AI services are running</p>
              </div>
            </div>
          </div>
        )}

        {activeTab === 'users' && (
          <div className="fade-in">
            <h3 className="section-title">User Management</h3>
            {loading ? (
              <div className="loading-overlay"><div className="spinner"></div><span>Loading users...</span></div>
            ) : users.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">👥</div>
                <div className="empty-state-title">No users loaded</div>
              </div>
            ) : (
              <div className="table-wrapper">
                <table>
                  <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Role</th><th>Status</th></tr></thead>
                  <tbody>
                    {users.map(u => (
                      <tr key={u.id}>
                        <td className="text-muted">#{u.id}</td>
                        <td>{u.name}</td>
                        <td className="text-muted">{u.email}</td>
                        <td><span className="badge badge-blue">{u.role || 'USER'}</span></td>
                        <td><span className="badge badge-green">{u.enabled !== false ? 'Active' : 'Disabled'}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === 'system' && (
          <div className="fade-in">
            <h3 className="section-title">System Configuration</h3>
            <div className="card">
              <div className="flex items-center gap-2 mb-2">
                <Shield size={18} color="var(--primary)" />
                <span className="font-semibold">Security</span>
              </div>
              <p className="text-muted text-sm">JWT authentication enabled. Spring Security active.</p>
            </div>
          </div>
        )}
      </div>
      <Footer />
    </div>
  )
}
