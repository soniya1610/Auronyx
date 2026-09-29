import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import Navbar from '../components/common/Navbar'
import Footer from '../components/common/Footer'
import { pickupService } from '../services/pickupService'
import {
  Truck, Package, CheckCircle, MapPin, Weight, Clock,
  RefreshCw, ChevronRight
} from 'lucide-react'

function statusBadge(status) {
  const map = {
    REQUESTED: 'badge-yellow', ASSIGNED: 'badge-blue', IN_TRANSIT: 'badge-blue',
    WEIGHED: 'badge-blue', COMPLETED: 'badge-green', PAID: 'badge-green', CANCELLED: 'badge-red',
  }
  return <span className={`badge ${map[status] || 'badge-gray'}`}>{status}</span>
}

export default function CollectorDashboard() {
  const { user } = useAuth()
  const [pickups, setPickups] = useState([])
  const [loading, setLoading] = useState(true)
  const [actionLoading, setActionLoading] = useState(null)
  const [msg, setMsg] = useState('')
  const [activeTab, setActiveTab] = useState('available')

  const loadPickups = async () => {
    setLoading(true)
    try {
      const res = await pickupService.getCollectorPickups()
      setPickups(res.data || [])
    } catch {
      setPickups([])
    }
    setLoading(false)
  }

  useEffect(() => { loadPickups() }, [])

  const handleAccept = async (id) => {
    setActionLoading(id)
    try {
      await pickupService.acceptPickup(id)
      setMsg('Pickup accepted!')
      loadPickups()
    } catch (err) {
      setMsg(err.response?.data?.message || 'Failed to accept')
    }
    setActionLoading(null)
  }

  const handleComplete = async (id) => {
    setActionLoading(id)
    try {
      await pickupService.completePickup(id)
      setMsg('Pickup completed!')
      loadPickups()
    } catch (err) {
      setMsg(err.response?.data?.message || 'Failed to complete')
    }
    setActionLoading(null)
  }

  const available = pickups.filter(p => p.status === 'REQUESTED')
  const myActive = pickups.filter(p => ['ASSIGNED', 'IN_TRANSIT', 'WEIGHED'].includes(p.status))
  const completed = pickups.filter(p => ['COMPLETED', 'PAID'].includes(p.status))

  const displayed = activeTab === 'available' ? available : activeTab === 'active' ? myActive : completed

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="main-content">
        <div className="page-header">
          <h1 className="page-title">Collector Dashboard 🚛</h1>
          <p className="page-subtitle">Welcome back, {user?.name || 'Collector'}</p>
        </div>

        {msg && <div className="alert alert-info">{msg}</div>}

        {/* Stats */}
        <div className="grid-3 mb-4">
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(245,158,11,0.12)', color: 'var(--accent)' }}><Package size={22} /></div>
            <div className="stat-value">{available.length}</div>
            <div className="stat-label">Available Pickups</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(59,130,246,0.12)', color: 'var(--info)' }}><Truck size={22} /></div>
            <div className="stat-value">{myActive.length}</div>
            <div className="stat-label">In Progress</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(16,185,129,0.12)', color: 'var(--primary)' }}><CheckCircle size={22} /></div>
            <div className="stat-value">{completed.length}</div>
            <div className="stat-label">Completed</div>
          </div>
        </div>

        {/* Tabs */}
        <div className="tabs">
          {[
            { key: 'available', label: `Available (${available.length})` },
            { key: 'active', label: `Active (${myActive.length})` },
            { key: 'completed', label: `Completed (${completed.length})` },
          ].map(t => (
            <button key={t.key} className={`tab ${activeTab === t.key ? 'active' : ''}`} onClick={() => setActiveTab(t.key)}>
              {t.label}
            </button>
          ))}
          <button className="btn btn-ghost btn-sm" onClick={loadPickups} style={{ marginLeft: 'auto' }}>
            <RefreshCw size={14} /> Refresh
          </button>
        </div>

        {loading ? (
          <div className="loading-overlay"><div className="spinner"></div><span>Loading pickups...</span></div>
        ) : displayed.length === 0 ? (
          <div className="empty-state fade-in">
            <div className="empty-state-icon">{activeTab === 'available' ? '📭' : activeTab === 'active' ? '🚛' : '✅'}</div>
            <div className="empty-state-title">No {activeTab} pickups</div>
            <p className="text-muted text-sm">{activeTab === 'available' ? 'No new pickups available right now.' : 'Nothing here yet.'}</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }} className="fade-in">
            {displayed.map(p => (
              <div className="card" key={p.id}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.75rem' }}>
                  <div>
                    <span className="font-semibold">{p.wasteType}</span>
                    <span className="text-muted text-sm" style={{ marginLeft: 8 }}>#{p.id}</span>
                    <div style={{ marginLeft: 4, display: 'inline-block' }}>{statusBadge(p.status)}</div>
                  </div>
                  <div className="flex gap-1">
                    {p.status === 'REQUESTED' && (
                      <button className="btn btn-primary btn-sm" disabled={actionLoading === p.id} onClick={() => handleAccept(p.id)}>
                        {actionLoading === p.id ? '...' : 'Accept'} <ChevronRight size={14} />
                      </button>
                    )}
                    {['ASSIGNED', 'IN_TRANSIT', 'WEIGHED'].includes(p.status) && (
                      <button className="btn btn-primary btn-sm" disabled={actionLoading === p.id} onClick={() => handleComplete(p.id)}>
                        {actionLoading === p.id ? '...' : 'Complete'} <CheckCircle size={14} />
                      </button>
                    )}
                  </div>
                </div>
                <div className="flex gap-2 mt-1 text-sm text-muted" style={{ flexWrap: 'wrap' }}>
                  <span><Weight size={13} style={{ verticalAlign: 'middle' }} /> {p.estimatedWeightKg || '?'} kg</span>
                  <span><MapPin size={13} style={{ verticalAlign: 'middle' }} /> {p.address || 'N/A'}</span>
                  <span><Clock size={13} style={{ verticalAlign: 'middle' }} /> {p.createdAt ? new Date(p.createdAt).toLocaleDateString() : '-'}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      <Footer />
    </div>
  )
}
