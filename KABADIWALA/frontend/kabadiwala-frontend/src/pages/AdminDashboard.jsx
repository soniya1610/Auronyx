import React, { useState, useEffect } from 'react';
import { Shield, AlertTriangle, CheckCircle, TrendingUp, Package, Wallet, Scale } from 'lucide-react';
import api from '../services/api';
import Loader from '../components/common/Loader';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);

  useEffect(() => {
    fetchAdminData();
  }, []);

  const fetchAdminData = async () => {
    try {
      const [statsRes, alertsRes] = await Promise.all([
        api.get('/api/admin/dashboard'),
        api.get('/api/admin/fraud/alerts'),
      ]);
      setStats(statsRes.data?.data || {});
      setAlerts(alertsRes.data?.data || []);
    } catch (err) {
      console.error('Failed to load admin data', err);
    } finally {
      setLoading(false);
    }
  };

  const resolveAlert = async (id) => {
    setActionLoading(id);
    try {
      await api.put(`/api/admin/fraud/alerts/${id}/resolve?notes=Resolved+by+admin`);
      await fetchAdminData();
    } catch (err) {
      console.error('Failed to resolve alert', err);
    } finally {
      setActionLoading(null);
    }
  };

  if (loading) return <Loader text="Loading Admin Console..." />;

  return (
    <div className="page-wrapper" style={{ padding: 'calc(var(--nav-height) + 2rem) 0 3rem' }}>
      <div className="container">
        {/* Header */}
        <div style={{ marginBottom: '2rem', animation: 'fadeIn 0.4s ease forwards' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', padding: '0.25rem 0.75rem', background: 'rgba(59,130,246,0.1)', border: '1px solid rgba(59,130,246,0.3)', borderRadius: 'var(--radius-full)', marginBottom: '0.75rem' }}>
            <Shield size={14} color="var(--blue-400)" />
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--blue-400)' }}>System Administration</span>
          </div>
          <h1 style={{ fontSize: '1.75rem' }}>
            Operations & <span className="gradient-text">Fraud Console</span>
          </h1>
          <p className="text-muted text-sm">Platform monitoring, system health, and anomaly detection</p>
        </div>

        {/* Stats Grid */}
        <div className="grid-4" style={{ marginBottom: '2.5rem' }}>
          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <span className="text-muted text-xs">TOTAL PICKUPS</span>
              <Package size={18} color="var(--green-400)" />
            </div>
            <div style={{ fontSize: '1.75rem', fontWeight: 700 }}>{stats?.totalPickups || 0}</div>
            <div className="text-muted text-xs" style={{ marginTop: '0.25rem' }}>
              {stats?.completedPickups || 0} completed · {stats?.pendingPickups || 0} pending
            </div>
          </div>

          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <span className="text-muted text-xs">PLATFORM REVENUE</span>
              <Wallet size={18} color="var(--teal-400)" />
            </div>
            <div style={{ fontSize: '1.75rem', fontWeight: 700 }}>₹{parseFloat(stats?.totalRevenueINR || 0).toFixed(2)}</div>
            <div className="text-muted text-xs" style={{ marginTop: '0.25rem' }}>Disbursed to sellers & collectors</div>
          </div>

          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <span className="text-muted text-xs">WASTE RECYCLED</span>
              <Scale size={18} color="var(--amber-400)" />
            </div>
            <div style={{ fontSize: '1.75rem', fontWeight: 700 }}>{parseFloat(stats?.totalWeightRecycledKg || 0).toFixed(1)} kg</div>
            <div className="text-muted text-xs" style={{ marginTop: '0.25rem' }}>Diverted from landfills</div>
          </div>

          <div className="card" style={{ padding: '1.25rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <span className="text-muted text-xs">FRAUD ALERTS</span>
              <AlertTriangle size={18} color="var(--red-400)" />
            </div>
            <div style={{ fontSize: '1.75rem', fontWeight: 700, color: alerts.filter(a => !a.resolved).length > 0 ? 'var(--red-400)' : 'var(--text-primary)' }}>
              {alerts.filter(a => !a.resolved).length}
            </div>
            <div className="text-muted text-xs" style={{ marginTop: '0.25rem' }}>Requires administrative review</div>
          </div>
        </div>

        {/* Fraud Detection Table */}
        <div className="card" style={{ padding: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
            <h2 style={{ fontSize: '1.15rem' }}>Security & Fraud Audits</h2>
            <span className="text-xs text-muted">Auto-detected anomalies</span>
          </div>

          {alerts.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
              <CheckCircle size={32} color="var(--green-400)" style={{ margin: '0 auto 0.75rem' }} />
              <p>No fraud alerts or suspicious discrepancies detected.</p>
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.875rem' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid var(--border)', textAlign: 'left', color: 'var(--text-muted)' }}>
                    <th style={{ padding: '0.75rem 0.5rem' }}>ID</th>
                    <th style={{ padding: '0.75rem 0.5rem' }}>SEVERITY</th>
                    <th style={{ padding: '0.75rem 0.5rem' }}>TRIGGER</th>
                    <th style={{ padding: '0.75rem 0.5rem' }}>DETAILS</th>
                    <th style={{ padding: '0.75rem 0.5rem' }}>STATUS</th>
                    <th style={{ padding: '0.75rem 0.5rem' }}>ACTION</th>
                  </tr>
                </thead>
                <tbody>
                  {alerts.map((alert) => (
                    <tr key={alert.id} style={{ borderBottom: '1px solid var(--border-light)' }}>
                      <td style={{ padding: '0.75rem 0.5rem' }}>#{alert.id}</td>
                      <td style={{ padding: '0.75rem 0.5rem' }}>
                        <span className={`badge ${alert.severity === 'HIGH' ? 'badge-red' : alert.severity === 'MEDIUM' ? 'badge-amber' : 'badge-blue'}`}>
                          {alert.severity || 'LOW'}
                        </span>
                      </td>
                      <td style={{ padding: '0.75rem 0.5rem', fontWeight: 600 }}>{alert.ruleTriggered}</td>
                      <td style={{ padding: '0.75rem 0.5rem', maxWidth: '300px' }}>{alert.details}</td>
                      <td style={{ padding: '0.75rem 0.5rem' }}>
                        {alert.resolved ? (
                          <span style={{ color: 'var(--green-400)', display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}>
                            <CheckCircle size={14} /> Resolved
                          </span>
                        ) : (
                          <span style={{ color: 'var(--red-400)', display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}>
                            <AlertTriangle size={14} /> Open
                          </span>
                        )}
                      </td>
                      <td style={{ padding: '0.75rem 0.5rem' }}>
                        {!alert.resolved && (
                          <button
                            className="btn btn-sm btn-outline"
                            onClick={() => resolveAlert(alert.id)}
                            disabled={actionLoading === alert.id}
                          >
                            {actionLoading === alert.id ? 'Resolving...' : 'Resolve'}
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
