import React from 'react';
import { Link } from 'react-router-dom';
import { Leaf, Github, Twitter, Mail, Phone } from 'lucide-react';

export default function Footer() {
  return (
    <footer style={{
      background: 'var(--bg-elevated)',
      borderTop: '1px solid var(--border)',
      padding: '3rem 0 1.5rem',
      marginTop: 'auto',
    }}>
      <div className="container">
        <div className="grid-4" style={{ gap: '2.5rem', marginBottom: '2.5rem' }}>
          {/* Brand */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem' }}>
              <div style={{
                width: '2rem', height: '2rem',
                background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
                borderRadius: '8px', display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <Leaf size={14} color="#fff" />
              </div>
              <span style={{
                fontFamily: "'Poppins', sans-serif", fontWeight: 800, fontSize: '1.1rem',
                background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
                WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
              }}>Kabadiwala</span>
            </div>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', lineHeight: 1.7, marginBottom: '1rem' }}>
              India's smart waste management platform connecting households, collectors, and recyclers for a cleaner future.
            </p>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              {[Github, Twitter, Mail].map((Icon, i) => (
                <button key={i} style={{
                  width: '2rem', height: '2rem', borderRadius: '6px',
                  background: 'var(--bg-surface)', border: '1px solid var(--border)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  color: 'var(--text-muted)', cursor: 'pointer', transition: 'all var(--transition-fast)',
                }}
                  onMouseEnter={e => { e.currentTarget.style.borderColor = 'var(--green-500)'; e.currentTarget.style.color = 'var(--green-400)'; }}
                  onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.color = 'var(--text-muted)'; }}
                >
                  <Icon size={14} />
                </button>
              ))}
            </div>
          </div>

          {/* Platform */}
          <div>
            <h5 style={{ color: 'var(--text-primary)', marginBottom: '1rem', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Platform</h5>
            {['For Households', 'For Collectors', 'For Recyclers', 'AI Analysis', 'QR Traceability'].map(link => (
              <Link key={link} to="/" style={{ display: 'block', color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem', textDecoration: 'none', transition: 'color var(--transition-fast)' }}
                onMouseEnter={e => e.target.style.color = 'var(--green-400)'}
                onMouseLeave={e => e.target.style.color = 'var(--text-muted)'}
              >{link}</Link>
            ))}
          </div>

          {/* Company */}
          <div>
            <h5 style={{ color: 'var(--text-primary)', marginBottom: '1rem', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Company</h5>
            {['About Us', 'How It Works', 'Impact Report', 'Blog', 'Careers'].map(link => (
              <Link key={link} to="/" style={{ display: 'block', color: 'var(--text-muted)', fontSize: '0.875rem', marginBottom: '0.5rem', textDecoration: 'none', transition: 'color var(--transition-fast)' }}
                onMouseEnter={e => e.target.style.color = 'var(--green-400)'}
                onMouseLeave={e => e.target.style.color = 'var(--text-muted)'}
              >{link}</Link>
            ))}
          </div>

          {/* Contact */}
          <div>
            <h5 style={{ color: 'var(--text-primary)', marginBottom: '1rem', fontSize: '0.875rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Contact</h5>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {[
                { icon: Mail, text: 'support@kabadiwala.in' },
                { icon: Phone, text: '+91 1800-KABADI' },
              ].map(({ icon: Icon, text }) => (
                <div key={text} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                  <Icon size={14} style={{ color: 'var(--green-400)', flexShrink: 0 }} />
                  {text}
                </div>
              ))}
            </div>
          </div>
        </div>

        <hr className="divider" />

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '0.75rem' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>
            © {new Date().getFullYear()} Kabadiwala. All rights reserved.
          </p>
          <div style={{ display: 'flex', gap: '1.5rem' }}>
            {['Privacy Policy', 'Terms of Service', 'Cookie Policy'].map(t => (
              <Link key={t} to="/" style={{ color: 'var(--text-muted)', fontSize: '0.8rem', textDecoration: 'none', transition: 'color var(--transition-fast)' }}
                onMouseEnter={e => e.target.style.color = 'var(--green-400)'}
                onMouseLeave={e => e.target.style.color = 'var(--text-muted)'}
              >{t}</Link>
            ))}
          </div>
        </div>
      </div>
    </footer>
  );
}
