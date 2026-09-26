import React from 'react';
import { Link } from 'react-router-dom';
import { Leaf, ArrowRight } from 'lucide-react';

export default function Splash() {
  return (
    <div style={{
      minHeight: '100vh',
      background: 'var(--bg-base)',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      position: 'relative',
      overflow: 'hidden',
      padding: '2rem',
    }}>
      {/* Animated background orbs */}
      <div className="glow-orb glow-green" style={{ width: '600px', height: '600px', top: '-200px', right: '-200px' }} />
      <div className="glow-orb glow-teal" style={{ width: '500px', height: '500px', bottom: '-200px', left: '-200px' }} />
      <div className="glow-orb glow-emerald" style={{ width: '300px', height: '300px', top: '50%', left: '30%' }} />

      <div style={{ position: 'relative', zIndex: 1, textAlign: 'center', animation: 'scaleIn 0.5s cubic-bezier(0.34,1.56,0.64,1) forwards' }}>
        {/* Logo */}
        <div style={{
          width: '6rem', height: '6rem',
          background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
          borderRadius: '28px',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          margin: '0 auto 1.5rem',
          boxShadow: '0 0 48px rgba(34,197,94,0.4)',
          animation: 'pulse-glow 3s ease-in-out infinite',
        }}>
          <Leaf size={36} color="#fff" />
        </div>

        <h1 style={{
          fontFamily: "'Poppins', sans-serif",
          fontWeight: 900, fontSize: '3rem',
          background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
          WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
          marginBottom: '0.5rem',
        }}>
          Kabadiwala
        </h1>

        <p style={{ fontSize: '1.1rem', color: 'var(--text-secondary)', marginBottom: '3rem', fontWeight: 500 }}>
          Smart Waste. Real Rewards.
        </p>

        <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap', marginBottom: '2rem' }}>
          <Link to="/register" className="btn btn-primary btn-xl">
            Get Started <ArrowRight size={20} />
          </Link>
          <Link to="/login" className="btn btn-secondary btn-xl">
            Sign In
          </Link>
        </div>

        <Link to="/" style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
          Learn more about Kabadiwala →
        </Link>
      </div>

      <style>{`
        @keyframes pulse-glow {
          0%, 100% { box-shadow: 0 0 48px rgba(34,197,94,0.4); }
          50% { box-shadow: 0 0 72px rgba(34,197,94,0.6); }
        }
      `}</style>
    </div>
  );
}
