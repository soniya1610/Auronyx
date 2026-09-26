import React, { useState, useRef, useEffect } from 'react';
import { Globe, ChevronDown } from 'lucide-react';
import { useLanguage } from '../../context/LanguageContext';

export default function LanguageSelector() {
  const { lang, setLang, SUPPORTED_LANGS } = useLanguage();
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    const handler = (e) => { if (!ref.current?.contains(e.target)) setOpen(false); };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const current = SUPPORTED_LANGS.find(l => l.code === lang);

  return (
    <div ref={ref} style={{ position: 'relative' }}>
      <button
        onClick={() => setOpen(!open)}
        style={{
          display: 'flex', alignItems: 'center', gap: '0.375rem',
          padding: '0.375rem 0.625rem',
          background: 'transparent', border: 'none', cursor: 'pointer',
          color: 'var(--text-muted)', fontSize: '0.8rem', fontWeight: 500,
          borderRadius: 'var(--radius-md)', transition: 'all var(--transition-fast)',
          fontFamily: "'Inter', sans-serif",
        }}
        onMouseEnter={e => { e.currentTarget.style.background = 'var(--bg-hover)'; e.currentTarget.style.color = 'var(--text-primary)'; }}
        onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'var(--text-muted)'; }}
      >
        <Globe size={14} />
        <span className="hidden-mobile">{current?.native || lang}</span>
        <ChevronDown size={12} style={{ transform: open ? 'rotate(180deg)' : '', transition: 'transform var(--transition-fast)' }} />
      </button>

      {open && (
        <div style={{
          position: 'absolute', top: 'calc(100% + 6px)', right: 0,
          background: 'var(--bg-card)', border: '1px solid var(--border)',
          borderRadius: 'var(--radius-md)', boxShadow: 'var(--shadow-md)',
          minWidth: '140px', zIndex: 300, overflow: 'hidden',
          animation: 'scaleIn 0.15s ease forwards', transformOrigin: 'top right',
        }}>
          {SUPPORTED_LANGS.map(l => (
            <button
              key={l.code}
              onClick={() => { setLang(l.code); setOpen(false); }}
              style={{
                display: 'flex', alignItems: 'center', gap: '0.5rem',
                padding: '0.5rem 0.875rem', width: '100%', textAlign: 'left',
                background: lang === l.code ? 'rgba(34,197,94,0.1)' : 'transparent',
                border: 'none', cursor: 'pointer', color: lang === l.code ? 'var(--green-400)' : 'var(--text-secondary)',
                fontSize: '0.85rem', fontFamily: "'Inter', sans-serif",
                transition: 'all var(--transition-fast)',
              }}
              onMouseEnter={e => { if (lang !== l.code) { e.currentTarget.style.background = 'var(--bg-hover)'; e.currentTarget.style.color = 'var(--text-primary)'; } }}
              onMouseLeave={e => { if (lang !== l.code) { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'var(--text-secondary)'; } }}
            >
              <span style={{ fontWeight: lang === l.code ? 600 : 400 }}>{l.native}</span>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
