import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User, Mail, Lock, Phone, Eye, EyeOff, Leaf, ArrowRight, AlertCircle, CheckCircle } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

const ROLES = [
  { value: 'USER', label: 'Household', desc: 'Schedule waste pickups & earn rewards', emoji: '🏠' },
  { value: 'COLLECTOR', label: 'Collector', desc: 'Collect waste & earn money', emoji: '🚛' },
  { value: 'RECYCLER', label: 'Recycler', desc: 'Receive & process waste materials', emoji: '♻️' },
];

export default function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({
    name: '', email: '', phone: '', password: '', role: 'USER',
  });
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--bg-base)' }}>
        <div style={{ textAlign: 'center', animation: 'scaleIn 0.4s ease forwards' }}>
          <div style={{ width: '5rem', height: '5rem', borderRadius: '50%', background: 'rgba(34,197,94,0.15)', border: '2px solid var(--green-500)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.5rem' }}>
            <CheckCircle size={32} color="var(--green-400)" />
          </div>
          <h2>Account Created!</h2>
          <p style={{ color: 'var(--text-muted)', marginTop: '0.5rem' }}>Redirecting to login...</p>
        </div>
      </div>
    );
  }

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'var(--bg-base)', padding: '1.5rem', position: 'relative', overflow: 'hidden',
    }}>
      <div className="glow-orb glow-green" style={{ width: '500px', height: '500px', top: '-200px', left: '-100px' }} />
      <div className="glow-orb glow-teal" style={{ width: '400px', height: '400px', bottom: '-200px', right: '-200px' }} />

      <div style={{ width: '100%', maxWidth: '480px', animation: 'slideUp 0.4s ease forwards' }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div style={{
            width: '3.5rem', height: '3.5rem',
            background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
            borderRadius: '16px', margin: '0 auto 1rem',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 0 24px rgba(34,197,94,0.3)',
          }}>
            <Leaf size={22} color="#fff" />
          </div>
          <h1 style={{ fontSize: '1.75rem', marginBottom: '0.25rem' }}>Join Kabadiwala</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Start your eco-friendly journey today</p>
        </div>

        {/* Step Progress */}
        <div className="step-progress" style={{ marginBottom: '1.75rem' }}>
          {[1, 2].map((s, i) => (
            <React.Fragment key={s}>
              <div className={`step ${step === s ? 'active' : step > s ? 'done' : ''}`}>
                <div className="step-circle">{step > s ? '✓' : s}</div>
                <span className="step-label">{s === 1 ? 'Role' : 'Details'}</span>
              </div>
              {i < 1 && <div className={`step-connector ${step > s ? 'done' : ''}`} />}
            </React.Fragment>
          ))}
        </div>

        <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: 'var(--radius-xl)', padding: '2rem', boxShadow: 'var(--shadow-lg)' }}>
          {error && (
            <div className="alert alert-error" style={{ marginBottom: '1.25rem' }}>
              <AlertCircle size={16} style={{ flexShrink: 0 }} />
              <span>{error}</span>
            </div>
          )}

          {/* Step 1: Role Selection */}
          {step === 1 && (
            <div style={{ animation: 'fadeIn 0.3s ease forwards' }}>
              <h3 style={{ marginBottom: '0.25rem', fontSize: '1.1rem' }}>Who are you?</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '1.25rem' }}>Select your role on the platform</p>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1.5rem' }}>
                {ROLES.map(r => (
                  <button
                    key={r.value}
                    type="button"
                    onClick={() => setForm({ ...form, role: r.value })}
                    style={{
                      display: 'flex', alignItems: 'center', gap: '1rem',
                      padding: '1rem 1.25rem', border: '1.5px solid',
                      borderColor: form.role === r.value ? 'var(--green-500)' : 'var(--border)',
                      background: form.role === r.value ? 'rgba(34,197,94,0.08)' : 'transparent',
                      borderRadius: 'var(--radius-md)', cursor: 'pointer', textAlign: 'left',
                      transition: 'all var(--transition-base)', width: '100%',
                    }}
                  >
                    <span style={{ fontSize: '2rem' }}>{r.emoji}</span>
                    <div>
                      <p style={{ fontWeight: 600, color: form.role === r.value ? 'var(--green-400)' : 'var(--text-primary)', fontSize: '0.95rem', marginBottom: '0.125rem' }}>{r.label}</p>
                      <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>{r.desc}</p>
                    </div>
                    {form.role === r.value && (
                      <div style={{ marginLeft: 'auto', color: 'var(--green-400)' }}><CheckCircle size={18} /></div>
                    )}
                  </button>
                ))}
              </div>
              <button className="btn btn-primary btn-lg w-full" type="button" onClick={() => setStep(2)}>
                Continue <ArrowRight size={17} />
              </button>
            </div>
          )}

          {/* Step 2: Details */}
          {step === 2 && (
            <form onSubmit={handleSubmit} style={{ animation: 'fadeIn 0.3s ease forwards', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              <div className="form-group">
                <label className="form-label" htmlFor="reg-name">
                  <User size={13} style={{ display: 'inline', marginRight: '0.375rem' }} />Full Name
                </label>
                <input id="reg-name" type="text" className="form-input" placeholder="Rahul Sharma" value={form.name}
                  onChange={e => setForm({ ...form, name: e.target.value })} required autoFocus />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="reg-email">
                  <Mail size={13} style={{ display: 'inline', marginRight: '0.375rem' }} />Email
                </label>
                <input id="reg-email" type="email" className="form-input" placeholder="you@example.com" value={form.email}
                  onChange={e => setForm({ ...form, email: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="reg-phone">
                  <Phone size={13} style={{ display: 'inline', marginRight: '0.375rem' }} />Phone
                </label>
                <input id="reg-phone" type="tel" className="form-input" placeholder="+91 9876543210" value={form.phone}
                  onChange={e => setForm({ ...form, phone: e.target.value })} required />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="reg-password">
                  <Lock size={13} style={{ display: 'inline', marginRight: '0.375rem' }} />Password
                </label>
                <div style={{ position: 'relative' }}>
                  <input id="reg-password" type={showPw ? 'text' : 'password'} className="form-input"
                    placeholder="••••••••" value={form.password}
                    onChange={e => setForm({ ...form, password: e.target.value })}
                    required minLength={6} style={{ paddingRight: '2.75rem' }} />
                  <button type="button" onClick={() => setShowPw(!showPw)} style={{
                    position: 'absolute', right: '0.75rem', top: '50%', transform: 'translateY(-50%)',
                    background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)',
                    display: 'flex', alignItems: 'center',
                  }}>
                    {showPw ? <EyeOff size={16} /> : <Eye size={16} />}
                  </button>
                </div>
                <span className="form-hint">Minimum 6 characters</span>
              </div>
              <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
                <button type="button" className="btn btn-secondary btn-lg" style={{ flex: 1 }} onClick={() => setStep(1)}>
                  ← Back
                </button>
                <button type="submit" className="btn btn-primary btn-lg" style={{ flex: 2 }} disabled={loading}>
                  {loading ? (
                    <><div className="spinner" style={{ width: '1rem', height: '1rem', borderWidth: '2px' }} /> Creating...</>
                  ) : 'Create Account'}
                </button>
              </div>
            </form>
          )}

          <div style={{ textAlign: 'center', marginTop: '1.25rem', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'var(--green-400)', fontWeight: 600 }}>Sign in</Link>
          </div>
        </div>
      </div>
    </div>
  );
}
