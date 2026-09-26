import React from 'react';

export default function Loader({ fullScreen = false, text = 'Loading...' }) {
  if (fullScreen) {
    return (
      <div style={{
        position: 'fixed', inset: 0, zIndex: 9999,
        background: 'var(--bg-base)',
        display: 'flex', flexDirection: 'column',
        alignItems: 'center', justifyContent: 'center', gap: '1.5rem'
      }}>
        <div style={{ position: 'relative', width: '4rem', height: '4rem' }}>
          {/* Outer ring */}
          <div style={{
            position: 'absolute', inset: 0,
            border: '3px solid rgba(34,197,94,0.15)',
            borderTopColor: 'var(--green-500)',
            borderRadius: '50%',
            animation: 'spin 0.9s linear infinite'
          }} />
          {/* Inner ring */}
          <div style={{
            position: 'absolute', inset: '8px',
            border: '2px solid rgba(34,197,94,0.1)',
            borderTopColor: 'var(--teal-400)',
            borderRadius: '50%',
            animation: 'spin 0.6s linear infinite reverse'
          }} />
        </div>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            fontFamily: "'Poppins', sans-serif",
            fontSize: '1.25rem', fontWeight: 700,
            background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
            WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent'
          }}>
            Kabadiwala
          </div>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '0.25rem' }}>{text}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-center" style={{ padding: '3rem', flexDirection: 'column', gap: '1rem' }}>
      <div className="spinner spinner-lg" />
      {text && <p className="text-muted text-sm">{text}</p>}
    </div>
  );
}
