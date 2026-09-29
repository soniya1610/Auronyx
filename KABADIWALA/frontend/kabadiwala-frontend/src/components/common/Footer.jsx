import React from 'react'

export default function Footer() {
  return (
    <footer style={{
      borderTop: '1px solid var(--border)',
      padding: '2rem 1.5rem',
      textAlign: 'center',
      color: 'var(--text-dim)',
      fontSize: '0.8rem',
      background: 'rgba(15,23,42,0.5)',
    }}>
      <p>© {new Date().getFullYear()} Kabadiwala Connect — Smart Waste Management & Recycling Platform</p>
      <p style={{ marginTop: '0.25rem' }}>Built with ♻️ for a greener future</p>
    </footer>
  )
}
