import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Package, Wallet, Recycle, Trophy, ArrowRight, Plus, Camera,
  TrendingUp, Clock, CheckCircle, AlertCircle, Leaf, Star
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useUser } from '../context/UserContext';
import { pickupService } from '../services/pickupService';
import { paymentService } from '../services/paymentService';
import Loader from '../components/common/Loader';

const STATUS_CONFIG = {
  REQUESTED:    { label: 'Requested', color: 'var(--amber-400)', bg: 'rgba(251,191,36,0.1)', badgeClass: 'badge-amber' },
  ACCEPTED:     { label: 'Accepted', color: 'var(--blue-400)',  bg: 'rgba(59,130,246,0.1)',  badgeClass: 'badge-blue' },
  ON_THE_WAY:   { label: 'On the Way', color: 'var(--purple-400)', bg: 'rgba(168,85,247,0.1)', badgeClass: 'badge-purple' },
  COLLECTED:    { label: 'Collected', color: 'var(--teal-400)', bg: 'rgba(20,184,166,0.1)',  badgeClass: 'badge-teal' },
  COMPLETED:    { label: 'Completed', color: 'var(--green-400)', bg: 'rgba(34,197,94,0.1)',  badgeClass: 'badge-green' },
  CANCELLED:    { label: 'Cancelled', color: 'var(--red-400)',  bg: 'rgba(239,68,68,0.1)',   badgeClass: 'badge-red' },
};

export default function UserDashboard() {
  const { user } = useAuth();
  const { wallet, fetchWallet } = useUser();
  const [pickups, setPickups] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        await fetchWallet();
        const [p, t] = await Promise.all([
          pickupService.getMyPickups(),
          paymentService.getMyTransactions(),
        ]);
        setPickups(p || []);
        setTransactions(t || []);
      } catch { /* ignore */ }
      finally { setLoading(false); }
    };
    load();
  }, [fetchWallet]);

  if (loading) return <Loader text="Loading your dashboard..." />;

  const activePickups = pickups.filter(p => !['COMPLETED', 'CANCELLED'].includes(p.status));
  const completedPickups = pickups.filter(p => p.status === 'COMPLETED');

  return (
    <div className="page-wrapper" style={{ padding: 'calc(var(--nav-height) + 2rem) 0 3rem' }}>
      <div className="container">

        {/* Welcome */}
        <div style={{ marginBottom: '2rem', animation: 'fadeIn 0.4s ease forwards' }}>
          <h1 style={{ fontSize: '1.75rem' }}>
            Good day, <span className="gradient-text">{user?.name?.split(' ')[0]}!</span> 👋
          </h1>
          <p className="text-muted text-sm" style={{ marginTop: '0.25rem' }}>Here's your waste management overview</p>
        </div>

        {/* Stats Row */}
        <div className="grid-4" style={{ marginBottom: '2rem', animation: 'slideUp 0.5s ease forwards' }}>
          {[
            {
              label: 'Wallet Balance', value: `₹${parseFloat(wallet?.balance || 0).toFixed(2)}`,
              icon: Wallet, color: '#22c55e', bg: 'rgba(34,197,94,0.1)', to: '/wallet'
            },
            {
              label: 'Active Pickups', value: activePickups.length,
              icon: Package, color: '#f59e0b', bg: 'rgba(251,191,36,0.1)', to: '/pickups'
            },
            {
              label: 'Total Completed', value: completedPickups.length,
              icon: CheckCircle, color: '#14b8a6', bg: 'rgba(20,184,166,0.1)', to: '/pickups'
            },
            {
              label: 'Total Earned', value: `₹${transactions.filter(t => t.status === 'COMPLETED').reduce((sum, t) => sum + parseFloat(t.finalAmount || 0), 0).toFixed(0)}`,
              icon: TrendingUp, color: '#a855f7', bg: 'rgba(168,85,247,0.1)', to: '/wallet'
            },
          ].map(({ label, value, icon: Icon, color, bg, to }) => (
            <Link key={label} to={to} style={{ textDecoration: 'none' }}>
              <div className="stat-card" style={{ cursor: 'pointer' }}>
                <div className="stat-icon" style={{ background: bg }}>
                  <Icon size={20} style={{ color }} />
                </div>
                <div className="stat-number" style={{ color }}>{value}</div>
                <div className="stat-label">{label}</div>
              </div>
            </Link>
          ))}
        </div>

        {/* Quick Actions */}
        <div className="card" style={{ marginBottom: '2rem', animation: 'slideUp 0.6s ease forwards' }}>
          <h3 style={{ marginBottom: '1.25rem', fontSize: '1rem', fontWeight: 700 }}>Quick Actions</h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))', gap: '0.75rem' }}>
            {[
              { to: '/waste/analyze', icon: Camera, label: 'Analyze Waste', color: '#22c55e', bg: 'rgba(34,197,94,0.1)' },
              { to: '/pickups/new', icon: Plus, label: 'Schedule Pickup', color: '#3b82f6', bg: 'rgba(59,130,246,0.1)' },
              { to: '/wallet', icon: Wallet, label: 'View Wallet', color: '#f59e0b', bg: 'rgba(251,191,36,0.1)' },
              { to: '/recycling', icon: Recycle, label: 'Track Recycling', color: '#14b8a6', bg: 'rgba(20,184,166,0.1)' },
              { to: '/rewards', icon: Trophy, label: 'Earn Rewards', color: '#a855f7', bg: 'rgba(168,85,247,0.1)' },
              { to: '/qr', icon: Star, label: 'Scan QR', color: '#f97316', bg: 'rgba(249,115,22,0.1)' },
            ].map(({ to, icon: Icon, label, color, bg }) => (
              <Link key={label} to={to} style={{ textDecoration: 'none' }}>
                <div style={{
                  display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
                  gap: '0.625rem', padding: '1.25rem 0.75rem',
                  background: 'var(--bg-surface)', border: '1px solid var(--border)',
                  borderRadius: 'var(--radius-md)', cursor: 'pointer',
                  transition: 'all var(--transition-base)',
                  textAlign: 'center',
                }}
                  onMouseEnter={e => { e.currentTarget.style.borderColor = color; e.currentTarget.style.background = bg; e.currentTarget.style.transform = 'translateY(-2px)'; }}
                  onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.background = 'var(--bg-surface)'; e.currentTarget.style.transform = ''; }}
                >
                  <div style={{ width: '2.5rem', height: '2.5rem', background: bg, borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Icon size={18} style={{ color }} />
                  </div>
                  <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-secondary)' }}>{label}</span>
                </div>
              </Link>
            ))}
          </div>
        </div>

        {/* Recent Pickups */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', animation: 'slideUp 0.7s ease forwards' }}>
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>Recent Pickups</h3>
              <Link to="/pickups" className="btn btn-ghost btn-sm" style={{ fontSize: '0.8rem' }}>
                View All <ArrowRight size={13} />
              </Link>
            </div>

            {pickups.length === 0 ? (
              <div className="empty-state" style={{ padding: '2rem' }}>
                <div className="empty-state-icon" style={{ width: '3rem', height: '3rem' }}>
                  <Package size={20} style={{ color: 'var(--text-muted)' }} />
                </div>
                <p className="text-sm text-muted">No pickups yet</p>
                <Link to="/pickups/new" className="btn btn-primary btn-sm">Schedule First Pickup</Link>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {pickups.slice(0, 4).map(pickup => {
                  const cfg = STATUS_CONFIG[pickup.status] || STATUS_CONFIG.REQUESTED;
                  return (
                    <Link key={pickup.id} to={`/pickups/${pickup.id}`} style={{ textDecoration: 'none' }}>
                      <div style={{
                        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                        padding: '0.75rem', background: 'var(--bg-surface)',
                        border: '1px solid var(--border)', borderRadius: 'var(--radius-md)',
                        transition: 'all var(--transition-fast)',
                      }}
                        onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--border-hover)'; }}
                        onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border)'; }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                          <div style={{ width: '2rem', height: '2rem', background: cfg.bg, borderRadius: 'var(--radius-sm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                            <Package size={14} style={{ color: cfg.color }} />
                          </div>
                          <div>
                            <p style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '0.875rem' }}>
                              {pickup.wasteCategoryName || 'Waste'}
                            </p>
                            <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                              {pickup.scheduledDate || 'Scheduled'}
                            </p>
                          </div>
                        </div>
                        <span className={`badge ${cfg.badgeClass}`}>{cfg.label}</span>
                      </div>
                    </Link>
                  );
                })}
              </div>
            )}
          </div>

          {/* Recent Transactions */}
          <div className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>Transactions</h3>
              <Link to="/wallet" className="btn btn-ghost btn-sm" style={{ fontSize: '0.8rem' }}>
                View All <ArrowRight size={13} />
              </Link>
            </div>

            {transactions.length === 0 ? (
              <div className="empty-state" style={{ padding: '2rem' }}>
                <div className="empty-state-icon" style={{ width: '3rem', height: '3rem' }}>
                  <Wallet size={20} style={{ color: 'var(--text-muted)' }} />
                </div>
                <p className="text-sm text-muted">No transactions yet</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {transactions.slice(0, 4).map(tx => (
                  <div key={tx.id} style={{
                    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                    padding: '0.75rem', background: 'var(--bg-surface)',
                    border: '1px solid var(--border)', borderRadius: 'var(--radius-md)',
                  }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                      <div style={{ width: '2rem', height: '2rem', background: 'rgba(34,197,94,0.1)', borderRadius: 'var(--radius-sm)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        <TrendingUp size={14} color="var(--green-400)" />
                      </div>
                      <div>
                        <p style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '0.875rem' }}>
                          {tx.wasteCategoryName || 'Waste Sold'}
                        </p>
                        <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>
                          {tx.actualWeight ? `${tx.actualWeight} kg` : ''} · {tx.status}
                        </p>
                      </div>
                    </div>
                    <span style={{ fontWeight: 800, color: 'var(--green-400)', fontFamily: "'Poppins',sans-serif" }}>
                      +₹{parseFloat(tx.finalAmount || 0).toFixed(2)}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Eco Impact */}
        <div className="card" style={{ marginTop: '1.5rem', animation: 'slideUp 0.8s ease forwards' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
            <div style={{ width: '2.5rem', height: '2.5rem', background: 'rgba(34,197,94,0.1)', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Leaf size={18} color="var(--green-400)" />
            </div>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, margin: 0 }}>Your Eco Impact</h3>
          </div>
          <div className="grid-3">
            {[
              { label: 'CO₂ Saved', value: `${(completedPickups.length * 2.3).toFixed(1)} kg` },
              { label: 'Trees Equivalent', value: `${(completedPickups.length * 0.1).toFixed(1)}` },
              { label: 'Pickups Completed', value: completedPickups.length },
            ].map(({ label, value }) => (
              <div key={label} style={{ textAlign: 'center', padding: '1rem', background: 'var(--bg-surface)', borderRadius: 'var(--radius-md)' }}>
                <div style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--green-400)', fontFamily: "'Poppins',sans-serif", marginBottom: '0.25rem' }}>{value}</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>{label}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
