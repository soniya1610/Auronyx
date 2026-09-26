import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Package, CheckCircle, Clock, MapPin, ArrowRight, Navigation, TrendingUp, Wallet
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { pickupService } from '../services/pickupService';
import Loader from '../components/common/Loader';

const STATUS_CONFIG = {
  REQUESTED:  { label: 'Requested',  badgeClass: 'badge-amber',  color: 'var(--amber-400)' },
  ACCEPTED:   { label: 'Accepted',   badgeClass: 'badge-blue',   color: 'var(--blue-400)' },
  ON_THE_WAY: { label: 'On the Way', badgeClass: 'badge-purple', color: 'var(--purple-400)' },
  COLLECTED:  { label: 'Collected',  badgeClass: 'badge-teal',   color: 'var(--teal-400)' },
  COMPLETED:  { label: 'Completed',  badgeClass: 'badge-green',  color: 'var(--green-400)' },
  CANCELLED:  { label: 'Cancelled',  badgeClass: 'badge-red',    color: 'var(--red-400)' },
};

export default function CollectorDashboard() {
  const { user } = useAuth();
  const [nearbyPickups, setNearbyPickups] = useState([]);
  const [myPickups, setMyPickups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [accepting, setAccepting] = useState(null);
  const [activeTab, setActiveTab] = useState('nearby');

  useEffect(() => {
    const load = async () => {
      try {
        const [nearby, mine] = await Promise.all([
          pickupService.getNearbyPickups(),
          pickupService.getCollectorPickups(),
        ]);
        setNearbyPickups(nearby || []);
        setMyPickups(mine || []);
      } catch { /* ignore */ }
      finally { setLoading(false); }
    };
    load();
  }, []);

  const handleAccept = async (id) => {
    setAccepting(id);
    try {
      await pickupService.acceptPickup(id);
      setNearbyPickups(prev => prev.filter(p => p.id !== id));
      const updated = await pickupService.getCollectorPickups();
      setMyPickups(updated || []);
    } catch { /* ignore */ }
    finally { setAccepting(null); }
  };

  const handleStatusUpdate = async (id, status) => {
    try {
      await pickupService.updatePickupStatus(id, status);
      const updated = await pickupService.getCollectorPickups();
      setMyPickups(updated || []);
    } catch { /* ignore */ }
  };

  if (loading) return <Loader text="Loading collector dashboard..." />;

  const active = myPickups.filter(p => !['COMPLETED', 'CANCELLED'].includes(p.status));
  const completed = myPickups.filter(p => p.status === 'COMPLETED');

  return (
    <div className="page-wrapper" style={{ padding: 'calc(var(--nav-height) + 2rem) 0 3rem' }}>
      <div className="container">
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '1.75rem' }}>
            Collector Hub, <span className="gradient-text">{user?.name?.split(' ')[0]}</span> 🚛
          </h1>
          <p className="text-muted text-sm" style={{ marginTop: '0.25rem' }}>Manage your pickups and earnings</p>
        </div>

        {/* Stats */}
        <div className="grid-4" style={{ marginBottom: '2rem' }}>
          {[
            { label: 'Active Pickups', value: active.length, color: '#f59e0b', icon: Clock },
            { label: 'Nearby Requests', value: nearbyPickups.length, color: '#3b82f6', icon: MapPin },
            { label: 'Completed Today', value: completed.length, color: '#22c55e', icon: CheckCircle },
            { label: 'Total Pickups', value: myPickups.length, color: '#a855f7', icon: Package },
          ].map(({ label, value, color, icon: Icon }) => (
            <div key={label} className="stat-card">
              <div className="stat-icon" style={{ background: `${color}20` }}>
                <Icon size={20} style={{ color }} />
              </div>
              <div className="stat-number" style={{ color }}>{value}</div>
              <div className="stat-label">{label}</div>
            </div>
          ))}
        </div>

        {/* Tabs */}
        <div className="tab-nav" style={{ marginBottom: '1.5rem' }}>
          {[
            { key: 'nearby', label: `Nearby Requests (${nearbyPickups.length})` },
            { key: 'active', label: `My Active (${active.length})` },
            { key: 'history', label: 'History' },
          ].map(t => (
            <button key={t.key} className={`tab-btn ${activeTab === t.key ? 'active' : ''}`}
              onClick={() => setActiveTab(t.key)}>{t.label}</button>
          ))}
        </div>

        {/* Nearby Pickups */}
        {activeTab === 'nearby' && (
          <div style={{ animation: 'fadeIn 0.3s ease' }}>
            {nearbyPickups.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon"><MapPin size={24} /></div>
                <h4>No nearby requests</h4>
                <p className="text-sm text-muted">Check back soon for new pickup requests in your area</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {nearbyPickups.map(pickup => (
                  <div key={pickup.id} className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                      <div style={{ flex: 1 }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '0.5rem' }}>
                          <h4 style={{ fontSize: '1rem', margin: 0 }}>{pickup.wasteCategoryName || 'Waste Pickup'}</h4>
                          <span className="badge badge-amber">New Request</span>
                        </div>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
                          {[
                            { icon: MapPin, text: `${pickup.city || 'Nearby'}, ${pickup.pincode || ''}` },
                            { icon: Package, text: `Est. ${pickup.estimatedWeight || '—'} kg` },
                            { icon: Clock, text: pickup.scheduledDate || 'Flexible' },
                          ].map(({ icon: Icon, text }) => (
                            <div key={text} style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', color: 'var(--text-muted)', fontSize: '0.825rem' }}>
                              <Icon size={13} /> {text}
                            </div>
                          ))}
                        </div>
                        {pickup.notes && <p style={{ marginTop: '0.5rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>{pickup.notes}</p>}
                      </div>
                      <div style={{ display: 'flex', gap: '0.5rem' }}>
                        <button className="btn btn-danger btn-sm" onClick={() => pickupService.rejectPickup(pickup.id).then(() => setNearbyPickups(p => p.filter(x => x.id !== pickup.id)))}>
                          Reject
                        </button>
                        <button className="btn btn-primary btn-sm" disabled={accepting === pickup.id} onClick={() => handleAccept(pickup.id)}>
                          {accepting === pickup.id ? <><div className="spinner" style={{ width: '0.8rem', height: '0.8rem', borderWidth: '2px' }} /> Accepting...</> : 'Accept'}
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Active Pickups */}
        {activeTab === 'active' && (
          <div style={{ animation: 'fadeIn 0.3s ease' }}>
            {active.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon"><Package size={24} /></div>
                <h4>No active pickups</h4>
                <p className="text-sm text-muted">Accept nearby requests to start collecting</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {active.map(pickup => {
                  const cfg = STATUS_CONFIG[pickup.status];
                  const nextStatus = {
                    ACCEPTED: 'ON_THE_WAY',
                    ON_THE_WAY: 'COLLECTED',
                    COLLECTED: null,
                  }[pickup.status];

                  return (
                    <div key={pickup.id} className="card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                        <div style={{ flex: 1 }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '0.5rem' }}>
                            <h4 style={{ fontSize: '1rem', margin: 0 }}>{pickup.wasteCategoryName || 'Waste Pickup'}</h4>
                            <span className={`badge ${cfg?.badgeClass}`}>{cfg?.label}</span>
                          </div>
                          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
                            {[
                              { icon: MapPin, text: pickup.address || 'Address not set' },
                              { icon: Package, text: `Est. ${pickup.estimatedWeight || '—'} kg` },
                              { icon: Clock, text: pickup.scheduledDate || 'Flexible' },
                            ].map(({ icon: Icon, text }) => (
                              <div key={text} style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', color: 'var(--text-muted)', fontSize: '0.825rem' }}>
                                <Icon size={13} /> {text}
                              </div>
                            ))}
                          </div>
                        </div>
                        <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
                          {nextStatus && (
                            <button className="btn btn-primary btn-sm" onClick={() => handleStatusUpdate(pickup.id, nextStatus)}>
                              <Navigation size={14} /> {nextStatus === 'ON_THE_WAY' ? 'Start Journey' : 'Mark Collected'}
                            </button>
                          )}
                          {pickup.status === 'COLLECTED' && (
                            <Link to={`/collector/verify/${pickup.id}`} className="btn btn-secondary btn-sm">
                              Verify Weight
                            </Link>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        {/* History */}
        {activeTab === 'history' && (
          <div style={{ animation: 'fadeIn 0.3s ease' }}>
            {completed.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon"><CheckCircle size={24} /></div>
                <h4>No completed pickups yet</h4>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {completed.map(pickup => (
                  <div key={pickup.id} style={{
                    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                    padding: '1rem', background: 'var(--bg-surface)',
                    border: '1px solid var(--border)', borderRadius: 'var(--radius-md)',
                    flexWrap: 'wrap', gap: '0.75rem',
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <div style={{ width: '2.25rem', height: '2.25rem', background: 'rgba(34,197,94,0.1)', borderRadius: 'var(--radius-sm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <CheckCircle size={14} color="var(--green-400)" />
                      </div>
                      <div>
                        <p style={{ fontWeight: 600, fontSize: '0.875rem' }}>{pickup.wasteCategoryName || 'Waste'}</p>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>{pickup.scheduledDate} · {pickup.city}</p>
                      </div>
                    </div>
                    <span className="badge badge-green">Completed</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
