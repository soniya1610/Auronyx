import React from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight, Leaf, Recycle, Truck, Factory, Shield, Zap, Star,
  CheckCircle, TrendingUp, Globe, Award, Package, Wallet
} from 'lucide-react';

const STATS = [
  { number: '2.4M+', label: 'KG Recycled', icon: Recycle, color: '#22c55e' },
  { number: '50K+', label: 'Happy Users', icon: Star, color: '#14b8a6' },
  { number: '8,000+', label: 'Collectors', icon: Truck, color: '#f59e0b' },
  { number: '₹12Cr+', label: 'Paid to Users', icon: Wallet, color: '#a855f7' },
];

const HOW_IT_WORKS = [
  { step: '01', title: 'Photograph Your Waste', desc: 'Use our AI camera to instantly identify waste type and get an estimated price', icon: Zap },
  { step: '02', title: 'Schedule a Pickup', desc: 'Choose a convenient time and our verified collector will come to your door', icon: Truck },
  { step: '03', title: 'Get Paid Instantly', desc: 'Collector verifies weight, finalizes price, and credits your wallet on the spot', icon: Wallet },
  { step: '04', title: 'Track Recycling', desc: 'Follow your waste journey through QR code from collection to recycling', icon: Globe },
];

const WASTE_TYPES = [
  { emoji: '📱', name: 'E-Waste', rate: '₹250/kg', color: '#3b82f6' },
  { emoji: '🔩', name: 'Metal', rate: '₹35/kg', color: '#f59e0b' },
  { emoji: '🧴', name: 'Plastic', rate: '₹15/kg', color: '#a855f7' },
  { emoji: '📰', name: 'Paper', rate: '₹12/kg', color: '#22c55e' },
  { emoji: '🍶', name: 'Glass', rate: '₹5/kg', color: '#14b8a6' },
  { emoji: '📦', name: 'Cardboard', rate: '₹10/kg', color: '#f97316' },
];

export default function Home() {
  return (
    <div style={{ background: 'var(--bg-base)' }}>
      {/* Hero */}
      <section style={{
        position: 'relative', minHeight: '100vh',
        display: 'flex', alignItems: 'center',
        overflow: 'hidden', paddingTop: 'var(--nav-height)',
      }}>
        {/* Animated background orbs */}
        <div className="glow-orb glow-green" style={{ width: '700px', height: '700px', top: '-100px', right: '-200px', animation: 'float 8s ease-in-out infinite' }} />
        <div className="glow-orb glow-teal" style={{ width: '500px', height: '500px', bottom: '-100px', left: '-150px', animation: 'float 6s ease-in-out infinite reverse' }} />
        <div className="glow-orb glow-emerald" style={{ width: '300px', height: '300px', top: '50%', left: '40%', animation: 'float 10s ease-in-out infinite' }} />

        <div className="container" style={{ position: 'relative', zIndex: 1, padding: '5rem 1.5rem' }}>
          {/* Badge */}
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', padding: '0.375rem 0.875rem', background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.25)', borderRadius: 'var(--radius-full)', marginBottom: '1.75rem', animation: 'fadeIn 0.6s ease forwards' }}>
            <Leaf size={14} color="var(--green-400)" />
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--green-400)' }}>India's #1 Smart Waste Platform</span>
          </div>

          <h1 style={{ maxWidth: '700px', marginBottom: '1.5rem', animation: 'slideUp 0.7s ease forwards' }}>
            Turn Your{' '}
            <span className="gradient-text">Waste</span>
            {' '}Into{' '}
            <span className="gradient-text">Wealth</span>
          </h1>

          <p style={{ fontSize: '1.2rem', color: 'var(--text-secondary)', maxWidth: '560px', marginBottom: '2.5rem', lineHeight: 1.7, animation: 'slideUp 0.8s ease forwards' }}>
            AI-powered waste identification, doorstep pickup by verified collectors, instant wallet payments, and full recycling traceability — all in one app.
          </p>

          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', animation: 'slideUp 0.9s ease forwards' }}>
            <Link to="/register" className="btn btn-primary btn-xl">
              Start Selling Waste <ArrowRight size={20} />
            </Link>
            <Link to="/register?role=COLLECTOR" className="btn btn-secondary btn-xl">
              Become a Collector
            </Link>
          </div>

          {/* Trust badges */}
          <div style={{ display: 'flex', gap: '1.5rem', marginTop: '2.5rem', flexWrap: 'wrap', animation: 'fadeIn 1.1s ease forwards' }}>
            {['AI-Powered Pricing', 'Instant Payments', 'QR Traceability', 'Verified Collectors'].map((badge) => (
              <div key={badge} style={{ display: 'flex', alignItems: 'center', gap: '0.375rem', color: 'var(--text-muted)', fontSize: '0.825rem' }}>
                <CheckCircle size={14} color="var(--green-400)" />
                {badge}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats */}
      <section style={{ padding: '5rem 0', background: 'var(--bg-elevated)', borderTop: '1px solid var(--border)', borderBottom: '1px solid var(--border)' }}>
        <div className="container">
          <div className="grid-4">
            {STATS.map(({ number, label, icon: Icon, color }) => (
              <div key={label} className="stat-card" style={{ textAlign: 'center' }}>
                <div className="stat-icon" style={{ background: `${color}20`, margin: '0 auto 0.75rem' }}>
                  <Icon size={22} style={{ color }} />
                </div>
                <div className="stat-number" style={{ color }}>{number}</div>
                <div className="stat-label">{label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* How It Works */}
      <section style={{ padding: '6rem 0' }}>
        <div className="container">
          <div className="section-header text-center" style={{ marginBottom: '4rem' }}>
            <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', padding: '0.375rem 0.875rem', background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.2)', borderRadius: 'var(--radius-full)', marginBottom: '1rem' }}>
              <Zap size={14} color="var(--green-400)" />
              <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--green-400)' }}>Simple Process</span>
            </div>
            <h2>How It Works</h2>
            <p style={{ maxWidth: '500px', margin: '0 auto', fontSize: '1rem' }}>From your doorstep to the recycling plant — transparent and fully tracked</p>
          </div>

          <div className="grid-4">
            {HOW_IT_WORKS.map(({ step, title, desc, icon: Icon }) => (
              <div key={step} className="card" style={{ textAlign: 'center', padding: '2rem 1.5rem' }}>
                <div style={{ fontSize: '0.75rem', fontWeight: 800, color: 'var(--green-400)', letterSpacing: '0.1em', marginBottom: '1rem' }}>{step}</div>
                <div style={{ width: '3rem', height: '3rem', background: 'rgba(34,197,94,0.1)', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
                  <Icon size={20} style={{ color: 'var(--green-400)' }} />
                </div>
                <h4 style={{ marginBottom: '0.5rem' }}>{title}</h4>
                <p style={{ fontSize: '0.875rem' }}>{desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Waste Types & Pricing */}
      <section style={{ padding: '6rem 0', background: 'var(--bg-elevated)' }}>
        <div className="container">
          <div className="section-header text-center" style={{ marginBottom: '3rem' }}>
            <h2>Current Pricing</h2>
            <p>Transparent, market-rate pricing. Always calculated server-side for fairness.</p>
          </div>

          <div className="grid-3" style={{ gap: '1rem' }}>
            {WASTE_TYPES.map(({ emoji, name, rate, color }) => (
              <div key={name} className="card" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.875rem' }}>
                  <span style={{ fontSize: '2rem' }}>{emoji}</span>
                  <span style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{name}</span>
                </div>
                <span style={{ fontWeight: 800, color, fontFamily: "'Poppins', sans-serif", fontSize: '1.1rem' }}>{rate}</span>
              </div>
            ))}
          </div>

          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
              Actual rates may vary. Final price is always calculated transparently at pickup based on verified weight.
            </p>
          </div>
        </div>
      </section>

      {/* Features */}
      <section style={{ padding: '6rem 0' }}>
        <div className="container">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '4rem', alignItems: 'center' }}>
            <div>
              <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', padding: '0.375rem 0.875rem', background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.2)', borderRadius: 'var(--radius-full)', marginBottom: '1rem' }}>
                <Shield size={14} color="var(--green-400)" />
                <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--green-400)' }}>100% Transparent</span>
              </div>
              <h2 style={{ marginBottom: '1rem' }}>AI-Powered Waste Intelligence</h2>
              <p style={{ marginBottom: '2rem', lineHeight: 1.8 }}>
                Simply photograph your waste and our AI instantly identifies the category, estimates weight, and gives you a fair price — all before you even schedule a pickup.
              </p>
              {[
                'Multi-waste category recognition',
                'Real-time price estimation',
                'Condition assessment',
                'Fallback for offline areas',
              ].map(f => (
                <div key={f} style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '0.75rem' }}>
                  <CheckCircle size={16} color="var(--green-400)" />
                  <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{f}</span>
                </div>
              ))}
              <Link to="/register" className="btn btn-primary" style={{ marginTop: '1.5rem' }}>
                Try AI Analysis <ArrowRight size={16} />
              </Link>
            </div>

            <div style={{ position: 'relative' }}>
              <div className="card" style={{ padding: '2rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1.5rem' }}>
                  <div style={{ width: '2.5rem', height: '2.5rem', background: 'rgba(34,197,94,0.15)', borderRadius: 'var(--radius-md)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Zap size={18} color="var(--green-400)" />
                  </div>
                  <div>
                    <p style={{ fontWeight: 700, color: 'var(--text-primary)', fontSize: '0.9rem' }}>AI Analysis Result</p>
                    <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Detected • 97% confidence</p>
                  </div>
                </div>
                {[
                  { label: 'Category', value: 'E-Waste (Laptop)', color: 'var(--blue-400)' },
                  { label: 'Est. Weight', value: '2.3 kg' },
                  { label: 'Price Range', value: '₹460 – ₹600', color: 'var(--green-400)' },
                  { label: 'Condition', value: 'Good' },
                ].map(({ label, value, color }) => (
                  <div key={label} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.625rem 0', borderBottom: '1px solid var(--border)' }}>
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>{label}</span>
                    <span style={{ fontWeight: 600, color: color || 'var(--text-primary)', fontSize: '0.875rem' }}>{value}</span>
                  </div>
                ))}
                <button className="btn btn-primary w-full" style={{ marginTop: '1.25rem' }}>
                  Schedule Pickup <ArrowRight size={16} />
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA */}
      <section style={{ padding: '6rem 0', background: 'var(--bg-elevated)', textAlign: 'center', position: 'relative', overflow: 'hidden' }}>
        <div className="glow-orb glow-green" style={{ width: '600px', height: '600px', top: '-300px', left: '50%', transform: 'translateX(-50%)' }} />
        <div className="container" style={{ position: 'relative', zIndex: 1 }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', padding: '0.375rem 0.875rem', background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.2)', borderRadius: 'var(--radius-full)', marginBottom: '1.5rem' }}>
            <TrendingUp size={14} color="var(--green-400)" />
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--green-400)' }}>Join 50,000+ users</span>
          </div>
          <h2 style={{ maxWidth: '600px', margin: '0 auto 1rem' }}>Ready to start earning from your waste?</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '1rem', marginBottom: '2.5rem', maxWidth: '500px', margin: '0 auto 2.5rem' }}>
            Create a free account in 2 minutes and schedule your first pickup today.
          </p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
            <Link to="/register" className="btn btn-primary btn-xl">
              Get Started Free <ArrowRight size={20} />
            </Link>
            <Link to="/login" className="btn btn-secondary btn-xl">
              Sign In
            </Link>
          </div>
        </div>
      </section>

      <style>{`
        @keyframes float {
          0%, 100% { transform: translateY(0); }
          50% { transform: translateY(-30px); }
        }
        @media (max-width: 768px) {
          section > div > div[style*="grid-template-columns: 1fr 1fr"] {
            grid-template-columns: 1fr !important;
          }
        }
      `}</style>
    </div>
  );
}
