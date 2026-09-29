import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { Mail, Lock, User, Phone, Eye, EyeOff, UserPlus } from 'lucide-react'

export default function Register() {
  const { register: doRegister } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    name: '', email: '', phone: '', password: '', confirmPassword: '', role: 'USER'
  })
  const [showPwd, setShowPwd] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.name || !form.email || !form.password) { setError('Please fill all required fields'); return }
    if (form.password !== form.confirmPassword) { setError('Passwords do not match'); return }
    if (form.password.length < 6) { setError('Password must be at least 6 characters'); return }
    setLoading(true)
    setError('')
    try {
      await doRegister({
        name: form.name,
        email: form.email,
        phone: form.phone,
        password: form.password,
        role: form.role,
      })
      setSuccess('Registration successful! Redirecting to login...')
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <div className="auth-card fade-in">
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <span style={{ fontSize: '2.5rem' }}>♻️</span>
        </div>
        <h1 className="auth-title">Create Account</h1>
        <p className="auth-subtitle">Join Kabadiwala Connect today</p>

        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">{success}</div>}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name *</label>
            <div style={{ position: 'relative' }}>
              <User size={16} style={{ position: 'absolute', left: 12, top: 13, color: 'var(--text-dim)' }} />
              <input type="text" name="name" className="form-input" placeholder="Your name"
                value={form.name} onChange={handleChange} style={{ paddingLeft: '2.5rem' }} />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Email Address *</label>
            <div style={{ position: 'relative' }}>
              <Mail size={16} style={{ position: 'absolute', left: 12, top: 13, color: 'var(--text-dim)' }} />
              <input type="email" name="email" className="form-input" placeholder="you@example.com"
                value={form.email} onChange={handleChange} style={{ paddingLeft: '2.5rem' }} autoComplete="email" />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Phone Number</label>
            <div style={{ position: 'relative' }}>
              <Phone size={16} style={{ position: 'absolute', left: 12, top: 13, color: 'var(--text-dim)' }} />
              <input type="tel" name="phone" className="form-input" placeholder="+91 9876543210"
                value={form.phone} onChange={handleChange} style={{ paddingLeft: '2.5rem' }} />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">I want to join as</label>
            <select name="role" className="form-input" value={form.role} onChange={handleChange}>
              <option value="USER">🏠 Household User</option>
              <option value="COLLECTOR">🚛 Waste Collector</option>
              <option value="RECYCLER">♻️ Recycler</option>
            </select>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Password *</label>
              <div style={{ position: 'relative' }}>
                <Lock size={16} style={{ position: 'absolute', left: 12, top: 13, color: 'var(--text-dim)' }} />
                <input type={showPwd ? 'text' : 'password'} name="password" className="form-input"
                  placeholder="••••••" value={form.password} onChange={handleChange} style={{ paddingLeft: '2.5rem' }} />
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Confirm Password *</label>
              <div style={{ position: 'relative' }}>
                <Lock size={16} style={{ position: 'absolute', left: 12, top: 13, color: 'var(--text-dim)' }} />
                <input type={showPwd ? 'text' : 'password'} name="confirmPassword" className="form-input"
                  placeholder="••••••" value={form.confirmPassword} onChange={handleChange} style={{ paddingLeft: '2.5rem' }} />
              </div>
            </div>
          </div>

          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              <input type="checkbox" checked={showPwd} onChange={() => setShowPwd(!showPwd)} /> Show passwords
            </label>
          </div>

          <button type="submit" className="btn btn-primary btn-full btn-lg" disabled={loading}>
            {loading ? <><div className="spinner" style={{ width: 18, height: 18 }}></div> Creating Account...</> : <><UserPlus size={18} /> Create Account</>}
          </button>
        </form>

        <div style={{ marginTop: '1.5rem', textAlign: 'center' }}>
          <p className="text-muted text-sm">
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>Sign In</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
