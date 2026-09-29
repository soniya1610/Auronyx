import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Recycle, Truck, Award, Leaf, ArrowRight, BarChart3, Shield } from 'lucide-react'
import Navbar from '../components/common/Navbar'
import Footer from '../components/common/Footer'

export default function Splash() {
  const { user } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    if (user) navigate('/dashboard', { replace: true })
  }, [user, navigate])

  const features = [
    { icon: <Recycle size={28} />, title: 'Smart Waste Classification', desc: 'AI-powered waste identification and categorization for accurate pricing and recycling.' },
    { icon: <Truck size={28} />, title: 'Easy Pickup Scheduling', desc: 'Book pickups from your doorstep. Track collectors in real-time.' },
    { icon: <Award size={28} />, title: 'Earn Rewards', desc: 'Get green points for every recyclable item. Redeem for exciting rewards.' },
    { icon: <BarChart3 size={28} />, title: 'Impact Dashboard', desc: 'Track your environmental impact — CO₂ saved, waste diverted, trees planted.' },
    { icon: <Shield size={28} />, title: 'Fair Pricing', desc: 'Transparent, AI-driven pricing ensures you get the best value for your waste.' },
    { icon: <Leaf size={28} />, title: 'Eco-Friendly', desc: 'Every pickup contributes to a cleaner environment and circular economy.' },
  ]

  return (
    <div className="page-wrapper">
      <Navbar />
      {/* Hero */}
      <section className="hero">
        <div className="hero-badge">🌍 Sustainable Waste Management Platform</div>
        <h1 className="hero-title">
          Turn Your Waste Into <span>Green Value</span>
        </h1>
        <p className="hero-desc">
          Kabadiwala Connect bridges households, collectors, and recyclers with smart technology —
          making recycling effortless, rewarding, and impactful.
        </p>
        <div className="hero-actions">
          <button className="btn btn-primary btn-lg" onClick={() => navigate('/register')}>
            Get Started <ArrowRight size={18} />
          </button>
          <button className="btn btn-outline btn-lg" onClick={() => navigate('/login')}>
            Sign In
          </button>
        </div>
      </section>

      {/* Features */}
      <section style={{ padding: '4rem 1.5rem', maxWidth: 1200, margin: '0 auto' }}>
        <h2 className="section-title text-center" style={{ fontSize: '1.75rem', marginBottom: '0.5rem' }}>How It Works</h2>
        <p className="section-sub text-center">A complete ecosystem for smart waste management</p>
        <div className="grid-3" style={{ marginTop: '2rem' }}>
          {features.map((f, i) => (
            <div className="card fade-in" key={i} style={{ animationDelay: `${i * 0.08}s` }}>
              <div className="stat-icon" style={{ background: 'rgba(16,185,129,0.12)', color: 'var(--primary)' }}>
                {f.icon}
              </div>
              <h3 style={{ fontSize: '1.05rem', fontWeight: 700 }}>{f.title}</h3>
              <p className="text-muted text-sm" style={{ lineHeight: 1.6 }}>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Stats */}
      <section style={{ padding: '3rem 1.5rem', maxWidth: 1200, margin: '0 auto' }}>
        <div className="grid-4">
          {[
            { val: '10K+', label: 'Active Users' },
            { val: '500+', label: 'Collectors' },
            { val: '50T', label: 'Waste Recycled' },
            { val: '25T', label: 'CO₂ Saved' },
          ].map((s, i) => (
            <div className="stat-card text-center" key={i}>
              <div className="stat-value text-primary">{s.val}</div>
              <div className="stat-label">{s.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section style={{ padding: '4rem 1.5rem', textAlign: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '0.75rem' }}>Ready to Make a Difference?</h2>
        <p className="text-muted" style={{ maxWidth: 480, margin: '0 auto 1.5rem' }}>
          Join thousands of users already contributing to a cleaner, greener planet.
        </p>
        <button className="btn btn-primary btn-lg" onClick={() => navigate('/register')}>
          Join Kabadiwala Connect <ArrowRight size={18} />
        </button>
      </section>

      <Footer />
    </div>
  )
}
