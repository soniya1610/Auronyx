import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import Navbar from '../components/common/Navbar'
import Footer from '../components/common/Footer'
import { recyclingService } from '../services/recyclingService'
import { Recycle, Package, FileCheck, RefreshCw } from 'lucide-react'

export default function RecyclerDashboard() {
  const { user } = useAuth()
  const [incoming, setIncoming] = useState([])
  const [loading, setLoading] = useState(true)

  const loadData = async () => {
    setLoading(true)
    try {
      const res = await recyclingService.getRecyclerIncoming()
      setIncoming(res.data || [])
    } catch {
      setIncoming([])
    }
    setLoading(false)
  }

  useEffect(() => { loadData() }, [])

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="main-content">
        <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h1 className="page-title">Recycler Dashboard ♻️</h1>
            <p className="page-subtitle">Welcome, {user?.name || 'Recycler'}</p>
          </div>
          <button className="btn btn-ghost btn-sm" onClick={loadData}><RefreshCw size={14} /> Refresh</button>
        </div>

        <div className="grid-3 mb-4">
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(16,185,129,0.12)', color: 'var(--primary)' }}><Recycle size={22} /></div>
            <div className="stat-value">{incoming.length}</div>
            <div className="stat-label">Incoming Batches</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(59,130,246,0.12)', color: 'var(--info)' }}><Package size={22} /></div>
            <div className="stat-value">0</div>
            <div className="stat-label">Processing</div>
          </div>
          <div className="stat-card">
            <div className="stat-icon" style={{ background: 'rgba(245,158,11,0.12)', color: 'var(--accent)' }}><FileCheck size={22} /></div>
            <div className="stat-value">0</div>
            <div className="stat-label">EPR Certificates</div>
          </div>
        </div>

        <h3 className="section-title">Incoming Materials</h3>
        {loading ? (
          <div className="loading-overlay"><div className="spinner"></div><span>Loading...</span></div>
        ) : incoming.length === 0 ? (
          <div className="empty-state fade-in">
            <div className="empty-state-icon">♻️</div>
            <div className="empty-state-title">No incoming materials yet</div>
            <p className="text-muted text-sm">Materials from collectors will appear here.</p>
          </div>
        ) : (
          <div className="table-wrapper fade-in">
            <table>
              <thead><tr><th>ID</th><th>Type</th><th>Weight</th><th>Status</th><th>Date</th></tr></thead>
              <tbody>
                {incoming.map(item => (
                  <tr key={item.id}>
                    <td className="text-muted">#{item.id}</td>
                    <td>{item.wasteType || item.type}</td>
                    <td>{item.weightKg || '?'} kg</td>
                    <td><span className="badge badge-blue">{item.status || 'RECEIVED'}</span></td>
                    <td className="text-muted text-sm">{item.createdAt ? new Date(item.createdAt).toLocaleDateString() : '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
      <Footer />
    </div>
  )
}
