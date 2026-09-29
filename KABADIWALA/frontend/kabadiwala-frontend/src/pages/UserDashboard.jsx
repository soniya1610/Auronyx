import React, { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import Navbar from '../components/common/Navbar'
import Footer from '../components/common/Footer'
import Modal from '../components/common/Modal'
import { pickupService } from '../services/pickupService'
import { paymentService } from '../services/paymentService'
import { rewardService } from '../services/rewardService'
import {
  Package, Truck, Wallet, Award, Plus, MapPin, Clock,
  CheckCircle, XCircle, RefreshCw, Calendar, Weight, IndianRupee
} from 'lucide-react'

function StatCard({ icon, label, value, color = 'var(--primary)' }) {
  return (
    <div className="stat-card">
      <div className="stat-icon" style={{ background: `${color}20`, color }}>{icon}</div>
      <div className="stat-value">{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  )
}

function statusBadge(status) {
  const map = {
    REQUESTED: 'badge-yellow', ASSIGNED: 'badge-blue', IN_TRANSIT: 'badge-blue',
    WEIGHED: 'badge-blue', COMPLETED: 'badge-green', PAID: 'badge-green', CANCELLED: 'badge-red',
  }
  return <span className={`badge ${map[status] || 'badge-gray'}`}>{status}</span>
}

export default function UserDashboard() {
  const { user } = useAuth()
  const [pickups, setPickups] = useState([])
  const [balance, setBalance] = useState(0)
  const [rewards, setRewards] = useState([])
  const [loading, setLoading] = useState(true)
  const [showBooking, setShowBooking] = useState(false)
  const [bookingForm, setBookingForm] = useState({
    wasteType: 'Paper', estimatedWeightKg: '', address: '', scheduledDate: '', notes: ''
  })
  const [bookingLoading, setBookingLoading] = useState(false)
  const [bookingMsg, setBookingMsg] = useState({ type: '', text: '' })
  const [activeTab, setActiveTab] = useState('overview')

  const loadData = async () => {
    setLoading(true)
    try {
      const [pRes, wRes] = await Promise.allSettled([
        pickupService.getMyPickups(),
        paymentService.getBalance(),
      ])
      if (pRes.status === 'fulfilled') setPickups(pRes.value.data || [])
      if (wRes.status === 'fulfilled') setBalance(wRes.value.data?.balance ?? 0)
    } catch {}
    setLoading(false)
  }

  useEffect(() => { loadData() }, [])

  const handleBookPickup = async (e) => {
    e.preventDefault()
    if (!bookingForm.address || !bookingForm.estimatedWeightKg) {
      setBookingMsg({ type: 'error', text: 'Please fill address and weight' }); return
    }
    setBookingLoading(true)
    setBookingMsg({ type: '', text: '' })
    try {
      await pickupService.bookPickup({
        wasteType: bookingForm.wasteType,
        estimatedWeightKg: parseFloat(bookingForm.estimatedWeightKg),
        address: bookingForm.address,
        scheduledDate: bookingForm.scheduledDate || null,
        notes: bookingForm.notes,
      })
      setBookingMsg({ type: 'success', text: 'Pickup booked successfully!' })
      setBookingForm({ wasteType: 'Paper', estimatedWeightKg: '', address: '', scheduledDate: '', notes: '' })
      setTimeout(() => { setShowBooking(false); loadData() }, 1200)
    } catch (err) {
      setBookingMsg({ type: 'error', text: err.response?.data?.message || 'Failed to book pickup' })
    } finally {
      setBookingLoading(false)
    }
  }

  const completedPickups = pickups.filter(p => p.status === 'COMPLETED' || p.status === 'PAID').length
  const pendingPickups = pickups.filter(p => ['REQUESTED', 'ASSIGNED', 'IN_TRANSIT', 'WEIGHED'].includes(p.status)).length

  return (
    <div className="page-wrapper">
      <Navbar />
      <div className="main-content">
        <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h1 className="page-title">Welcome, {user?.name || 'User'} 👋</h1>
            <p className="page-subtitle">Your waste management dashboard</p>
          </div>
          <button className="btn btn-primary" onClick={() => setShowBooking(true)}>
            <Plus size={18} /> Book Pickup
          </button>
        </div>

        {/* Stats */}
        <div className="grid-4 mb-4">
          <StatCard icon={<Package size={22} />} label="Total Pickups" value={pickups.length} />
          <StatCard icon={<CheckCircle size={22} />} label="Completed" value={completedPickups} color="#10b981" />
          <StatCard icon={<Clock size={22} />} label="Pending" value={pendingPickups} color="#f59e0b" />
          <StatCard icon={<Wallet size={22} />} label="Wallet Balance" value={`₹${balance}`} color="#3b82f6" />
        </div>

        {/* Tabs */}
        <div className="tabs">
          {['overview', 'pickups', 'rewards'].map(t => (
            <button key={t} className={`tab ${activeTab === t ? 'active' : ''}`} onClick={() => setActiveTab(t)}>
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>

        {activeTab === 'overview' && (
          <div className="fade-in">
            <h3 className="section-title">Recent Pickups</h3>
            {loading ? (
              <div className="loading-overlay"><div className="spinner"></div><span>Loading...</span></div>
            ) : pickups.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">📦</div>
                <div className="empty-state-title">No pickups yet</div>
                <p className="text-muted text-sm">Book your first pickup to start recycling!</p>
                <button className="btn btn-primary mt-2" onClick={() => setShowBooking(true)}>
                  <Plus size={16} /> Book First Pickup
                </button>
              </div>
            ) : (
              <div className="table-wrapper">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Waste Type</th>
                      <th>Weight (kg)</th>
                      <th>Address</th>
                      <th>Status</th>
                      <th>Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pickups.slice(0, 10).map(p => (
                      <tr key={p.id}>
                        <td className="text-muted">#{p.id}</td>
                        <td>{p.wasteType}</td>
                        <td>{p.estimatedWeightKg || '-'} kg</td>
                        <td className="text-muted" style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{p.address}</td>
                        <td>{statusBadge(p.status)}</td>
                        <td className="text-muted text-sm">{p.createdAt ? new Date(p.createdAt).toLocaleDateString() : '-'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === 'pickups' && (
          <div className="fade-in">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h3 className="section-title" style={{ margin: 0 }}>All Pickups</h3>
              <button className="btn btn-ghost btn-sm" onClick={loadData}><RefreshCw size={14} /> Refresh</button>
            </div>
            {pickups.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">🚛</div>
                <div className="empty-state-title">No pickups found</div>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {pickups.map(p => (
                  <div className="card" key={p.id}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.5rem' }}>
                      <div>
                        <span className="font-semibold">{p.wasteType}</span>
                        <span className="text-muted text-sm" style={{ marginLeft: 8 }}>#{p.id}</span>
                      </div>
                      {statusBadge(p.status)}
                    </div>
                    <div className="flex gap-2 mt-1 text-sm text-muted" style={{ flexWrap: 'wrap' }}>
                      <span><Weight size={13} style={{ verticalAlign: 'middle' }} /> {p.estimatedWeightKg || '?'} kg</span>
                      <span><MapPin size={13} style={{ verticalAlign: 'middle' }} /> {p.address || 'N/A'}</span>
                      {p.scheduledDate && <span><Calendar size={13} style={{ verticalAlign: 'middle' }} /> {new Date(p.scheduledDate).toLocaleDateString()}</span>}
                      {p.totalAmount > 0 && <span><IndianRupee size={13} style={{ verticalAlign: 'middle' }} /> ₹{p.totalAmount}</span>}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {activeTab === 'rewards' && (
          <div className="fade-in">
            <div className="empty-state">
              <div className="empty-state-icon">🎁</div>
              <div className="empty-state-title">Rewards Coming Soon</div>
              <p className="text-muted text-sm">Keep recycling to earn green points!</p>
            </div>
          </div>
        )}
      </div>

      {/* Book Pickup Modal */}
      <Modal isOpen={showBooking} onClose={() => setShowBooking(false)} title="Book a Pickup">
        {bookingMsg.text && <div className={`alert alert-${bookingMsg.type}`}>{bookingMsg.text}</div>}
        <form onSubmit={handleBookPickup}>
          <div className="form-group">
            <label className="form-label">Waste Type</label>
            <select className="form-input" value={bookingForm.wasteType}
              onChange={e => setBookingForm({ ...bookingForm, wasteType: e.target.value })}>
              {['Paper', 'Plastic', 'Metal', 'Glass', 'E-Waste', 'Fabric', 'Organic', 'Mixed'].map(t =>
                <option key={t} value={t}>{t}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Estimated Weight (kg) *</label>
            <input type="number" step="0.1" className="form-input" placeholder="e.g. 5"
              value={bookingForm.estimatedWeightKg}
              onChange={e => setBookingForm({ ...bookingForm, estimatedWeightKg: e.target.value })} />
          </div>
          <div className="form-group">
            <label className="form-label">Pickup Address *</label>
            <textarea className="form-input" placeholder="Full address for pickup"
              value={bookingForm.address}
              onChange={e => setBookingForm({ ...bookingForm, address: e.target.value })} />
          </div>
          <div className="form-group">
            <label className="form-label">Preferred Date</label>
            <input type="date" className="form-input"
              value={bookingForm.scheduledDate}
              onChange={e => setBookingForm({ ...bookingForm, scheduledDate: e.target.value })} />
          </div>
          <div className="form-group">
            <label className="form-label">Notes (optional)</label>
            <input type="text" className="form-input" placeholder="Any special instructions"
              value={bookingForm.notes}
              onChange={e => setBookingForm({ ...bookingForm, notes: e.target.value })} />
          </div>
          <button type="submit" className="btn btn-primary btn-full" disabled={bookingLoading}>
            {bookingLoading ? 'Booking...' : 'Confirm Pickup'}
          </button>
        </form>
      </Modal>

      <Footer />
    </div>
  )
}
