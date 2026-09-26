import React, { useState, useEffect } from 'react';
import { Package, Recycle, CheckCircle, Clock, ArrowRight, Leaf } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { recyclingService } from '../../services/recyclingService';
import Loader from '../common/Loader';

const RECYCLING_STATUSES = [
  'COLLECTED', 'SORTED', 'AGGREGATED', 'TRANSPORT', 'RECEIVED', 'PROCESSING', 'RECYCLED'
];

const STATUS_COLOR = {
  COLLECTED:   '#f59e0b',
  SORTED:      '#3b82f6',
  AGGREGATED:  '#a855f7',
  TRANSPORT:   '#f97316',
  RECEIVED:    '#14b8a6',
  PROCESSING:  '#22c55e',
  RECYCLED:    '#22c55e',
};

export default function RecyclerDashboard() {
  const { user } = useAuth();
  const [incoming, setIncoming] = useState([]);
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('incoming');

  useEffect(() => {
    const load = async () => {
      try {
        const [inc, rec] = await Promise.all([
          recyclingService.getIncomingWaste(),
          recyclingService.getRecyclerRecords(),
        ]);
        setIncoming(inc || []);
        setRecords(rec || []);
      } catch { /* ignore */ }
      finally { setLoading(false); }
    };
    load();
  }, []);

  const handleReceive = async (id) => {
    try {
      await recyclingService.receiveWaste(id);
      const updated = await recyclingService.getRecyclerRecords();
      setRecords(updated || []);
      setIncoming(prev => prev.filter(r => r.id !== id));
    } catch { /* ignore */ }
  };

  const handleStatusUpdate = async (id, status) => {
    try {
      await recyclingService.updateRecyclingStatus(id, { status });
      const updated = await recyclingService.getRecyclerRecords();
      setRecords(updated || []);
    } catch { /* ignore */ }
  };

  if (loading) return <Loader text="Loading recycler dashboard..." />;

  const active = records.filter(r => r.status !== 'RECYCLED');
  const completed = records.filter(r => r.status === 'RECYCLED');

  return (
    <div className="page-wrapper" style={{ padding: 'calc(var(--nav-height) + 2rem) 0 3rem' }}>
      <div className="container">
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '1.75rem' }}>
            Recycler Hub, <span className="gradient-text">{user?.name?.split(' ')[0]}</span> ♻️
          </h1>
          <p className="text-muted text-sm" style={{ marginTop: '0.25rem' }}>Process waste and update recycling lifecycle</p>
        </div>

        {/* Stats */}
        <div className="grid-4" style={{ marginBottom: '2rem' }}>
          {[
            { label: 'Incoming', value: incoming.length, color: '#f59e0b', icon: Package },
            { label: 'In Progress', value: active.length, color: '#3b82f6', icon: Recycle },
            { label: 'Recycled', value: completed.length, color: '#22c55e', icon: CheckCircle },
            { label: 'Total Processed', value: records.length, color: '#a855f7', icon: Leaf },
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
            { key: 'incoming', label: `Incoming (${incoming.length})` },
            { key: 'active', label: `In Progress (${active.length})` },
            { key: 'completed', label: `Recycled (${completed.length})` },
          ].map(t => (
            <button key={t.key} className={`tab-btn ${activeTab === t.key ? 'active' : ''}`}
              onClick={() => setActiveTab(t.key)}>{t.label}</button>
          ))}
        </div>

        {/* Incoming */}
        {activeTab === 'incoming' && (
          <div style={{ animation: 'fadeIn 0.3s ease' }}>
            {incoming.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon"><Package size={24} /></div>
                <h4>No incoming waste</h4>
                <p className="text-sm text-muted">Completed pickups will appear here</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {incoming.map(record => (
                  <div key={record.id} className="card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                      <div>
                        <h4 style={{ fontSize: '1rem', marginBottom: '0.5rem' }}>
                          {record.wasteCategoryName || 'Waste'} — {record.weight} kg
                        </h4>
                        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                          <p style={{ color: 'var(--text-muted)', fontSize: '0.825rem' }}>
                            Transaction #{record.transactionId}
                          </p>
                          <span className="badge badge-amber">COLLECTED</span>
                        </div>
                      </div>
                      <button className="btn btn-primary btn-sm" onClick={() => handleReceive(record.id)}>
                        Mark Received
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* In Progress */}
        {activeTab === 'active' && (
          <div style={{ animation: 'fadeIn 0.3s ease' }}>
            {active.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon"><Recycle size={24} /></div>
                <h4>No records in progress</h4>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {active.map(record => {
                  const currentIndex = RECYCLING_STATUSES.indexOf(record.status);
                  const nextStatus = currentIndex < RECYCLING_STATUSES.length - 1 ? RECYCLING_STATUSES[currentIndex + 1] : null;
                  const color = STATUS_COLOR[record.status] || '#22c55e';

                  return (
                    <div key={record.id} className="card">
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1rem' }}>
                        <div>
                          <h4 style={{ fontSize: '1rem', marginBottom: '0.375rem' }}>
                            {record.wasteCategoryName || 'Waste'} — {record.weight} kg
                          </h4>
                          <span className="badge" style={{ background: `${color}20`, color, border: `1px solid ${color}40` }}>
                            {record.status}
                          </span>
                        </div>
                        {nextStatus && (
                          <button className="btn btn-primary btn-sm" onClick={() => handleStatusUpdate(record.id, nextStatus)}>
                            → {nextStatus}
                          </button>
                        )}
                      </div>

                      {/* Progress Bar */}
                      <div style={{ display: 'flex', gap: '2px' }}>
                        {RECYCLING_STATUSES.map((s, i) => (
                          <div key={s} style={{
                            flex: 1, height: '4px', borderRadius: '2px',
                            background: i <= currentIndex ? 'var(--green-500)' : 'var(--bg-surface)',
                            transition: 'background var(--transition-base)',
                          }} />
                        ))}
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '0.375rem' }}>
                        <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>Collected</span>
                        <span style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>Recycled</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        {/* Completed */}
        {activeTab === 'completed' && (
          <div style={{ animation: 'fadeIn 0.3s ease' }}>
            {completed.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon"><CheckCircle size={24} /></div>
                <h4>No recycled records yet</h4>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {completed.map(record => (
                  <div key={record.id} style={{
                    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                    padding: '1rem', background: 'var(--bg-surface)',
                    border: '1px solid var(--border)', borderRadius: 'var(--radius-md)',
                    flexWrap: 'wrap', gap: '0.75rem',
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <div style={{ width: '2.25rem', height: '2.25rem', background: 'rgba(34,197,94,0.1)', borderRadius: 'var(--radius-sm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <Recycle size={14} color="var(--green-400)" />
                      </div>
                      <div>
                        <p style={{ fontWeight: 600, fontSize: '0.875rem' }}>{record.wasteCategoryName || 'Waste'}</p>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>{record.weight} kg recycled</p>
                      </div>
                    </div>
                    <span className="badge badge-green">♻️ Recycled</span>
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
