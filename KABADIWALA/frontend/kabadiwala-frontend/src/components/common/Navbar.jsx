import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
  Recycle, LayoutDashboard, Package, Wallet, Trophy, Bell,
  Menu, X, LogOut, User, ChevronDown, Leaf
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useUser } from '../../context/UserContext';
import LanguageSelector from './LanguageSelector';

const NAV_LINKS = {
  USER: [
    { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/pickups', icon: Package, label: 'Pickups' },
    { to: '/wallet', icon: Wallet, label: 'Wallet' },
    { to: '/recycling', icon: Recycle, label: 'Recycling' },
    { to: '/rewards', icon: Trophy, label: 'Rewards' },
  ],
  COLLECTOR: [
    { to: '/collector', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/collector/pickups', icon: Package, label: 'Pickups' },
    { to: '/collector/earnings', icon: Wallet, label: 'Earnings' },
  ],
  RECYCLER: [
    { to: '/recycler', icon: LayoutDashboard, label: 'Dashboard' },
    { to: '/recycler/incoming', icon: Package, label: 'Incoming' },
    { to: '/recycler/records', icon: Recycle, label: 'Records' },
  ],
  ADMIN: [
    { to: '/admin', icon: LayoutDashboard, label: 'Dashboard' },
  ],
};

export default function Navbar() {
  const { user, logout, isAuthenticated } = useAuth();
  const { wallet, unreadCount } = useUser();
  const navigate = useNavigate();
  const location = useLocation();
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);

  useEffect(() => {
    const handler = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', handler);
    return () => window.removeEventListener('scroll', handler);
  }, []);

  useEffect(() => { setMenuOpen(false); setProfileOpen(false); }, [location.pathname]);

  const links = user ? (NAV_LINKS[user.role] || []) : [];

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => location.pathname === path || location.pathname.startsWith(path + '/');

  return (
    <nav style={{
      position: 'fixed', top: 0, left: 0, right: 0, zIndex: 100,
      height: 'var(--nav-height)',
      background: scrolled
        ? 'rgba(10,15,10,0.92)' : 'rgba(10,15,10,0.7)',
      backdropFilter: 'blur(20px)',
      WebkitBackdropFilter: 'blur(20px)',
      borderBottom: `1px solid ${scrolled ? 'var(--border)' : 'transparent'}`,
      transition: 'all var(--transition-base)',
    }}>
      <div className="container" style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>

        {/* Logo */}
        <Link to={isAuthenticated ? '/dashboard' : '/'} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', textDecoration: 'none' }}>
          <div style={{
            width: '2.25rem', height: '2.25rem',
            background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
            borderRadius: '10px',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 0 12px rgba(34,197,94,0.3)',
          }}>
            <Leaf size={16} color="#fff" />
          </div>
          <span style={{
            fontFamily: "'Poppins', sans-serif", fontWeight: 800,
            fontSize: '1.2rem',
            background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
            WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent',
          }}>
            Kabadiwala
          </span>
        </Link>

        {/* Desktop Nav Links */}
        {isAuthenticated && (
          <div className="flex flex-gap-1" style={{ display: 'flex' }}>
            {links.map(({ to, icon: Icon, label }) => (
              <Link
                key={to} to={to}
                style={{
                  display: 'flex', alignItems: 'center', gap: '0.375rem',
                  padding: '0.375rem 0.75rem', borderRadius: 'var(--radius-md)',
                  fontSize: '0.875rem', fontWeight: 500, textDecoration: 'none',
                  color: isActive(to) ? 'var(--green-400)' : 'var(--text-secondary)',
                  background: isActive(to) ? 'rgba(34,197,94,0.1)' : 'transparent',
                  transition: 'all var(--transition-fast)',
                }}
                onMouseEnter={e => { if (!isActive(to)) { e.currentTarget.style.color = 'var(--text-primary)'; e.currentTarget.style.background = 'var(--bg-hover)'; } }}
                onMouseLeave={e => { if (!isActive(to)) { e.currentTarget.style.color = 'var(--text-secondary)'; e.currentTarget.style.background = 'transparent'; } }}
              >
                <Icon size={15} />
                <span className="hidden-mobile">{label}</span>
              </Link>
            ))}
          </div>
        )}

        {/* Right Side */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <LanguageSelector />

          {isAuthenticated ? (
            <>
              {/* Wallet Balance */}
              {wallet && (
                <Link to="/wallet" style={{
                  display: 'flex', alignItems: 'center', gap: '0.375rem',
                  padding: '0.375rem 0.75rem',
                  background: 'rgba(34,197,94,0.1)',
                  border: '1px solid rgba(34,197,94,0.2)',
                  borderRadius: 'var(--radius-full)',
                  textDecoration: 'none',
                  fontSize: '0.825rem', fontWeight: 700,
                  color: 'var(--green-400)',
                }}>
                  <Wallet size={14} />
                  ₹{parseFloat(wallet.balance || 0).toFixed(2)}
                </Link>
              )}

              {/* Notifications */}
              <Link to="/notifications" style={{
                position: 'relative',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                width: '2.25rem', height: '2.25rem',
                borderRadius: 'var(--radius-md)',
                color: 'var(--text-muted)',
                textDecoration: 'none',
                transition: 'all var(--transition-fast)',
              }}
                onMouseEnter={e => { e.currentTarget.style.background = 'var(--bg-hover)'; e.currentTarget.style.color = 'var(--text-primary)'; }}
                onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'var(--text-muted)'; }}
              >
                <Bell size={18} />
                {unreadCount > 0 && <span className="notif-badge">{unreadCount > 9 ? '9+' : unreadCount}</span>}
              </Link>

              {/* Profile Dropdown */}
              <div style={{ position: 'relative' }}>
                <button
                  onClick={() => setProfileOpen(!profileOpen)}
                  style={{
                    display: 'flex', alignItems: 'center', gap: '0.5rem',
                    padding: '0.375rem 0.625rem',
                    background: 'var(--bg-surface)',
                    border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-md)',
                    cursor: 'pointer',
                    color: 'var(--text-primary)',
                    fontSize: '0.875rem', fontWeight: 500,
                    transition: 'all var(--transition-fast)',
                  }}
                >
                  <div style={{
                    width: '1.75rem', height: '1.75rem', borderRadius: '50%',
                    background: 'linear-gradient(135deg, #22c55e 0%, #14b8a6 100%)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: '0.75rem', fontWeight: 700, color: '#fff',
                  }}>
                    {user?.name?.[0]?.toUpperCase() || 'U'}
                  </div>
                  <ChevronDown size={14} style={{ color: 'var(--text-muted)', transform: profileOpen ? 'rotate(180deg)' : '', transition: 'transform var(--transition-fast)' }} />
                </button>

                {profileOpen && (
                  <div style={{
                    position: 'absolute', top: 'calc(100% + 8px)', right: 0,
                    background: 'var(--bg-card)', border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-lg)', boxShadow: 'var(--shadow-lg)',
                    minWidth: '200px', zIndex: 200,
                    animation: 'scaleIn 0.2s cubic-bezier(0.34,1.56,0.64,1) forwards',
                    transformOrigin: 'top right',
                  }}>
                    <div style={{ padding: '0.875rem 1rem', borderBottom: '1px solid var(--border)' }}>
                      <p style={{ fontWeight: 600, color: 'var(--text-primary)', fontSize: '0.875rem' }}>{user?.name}</p>
                      <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem' }}>{user?.email}</p>
                      <span className="badge badge-green" style={{ marginTop: '0.375rem' }}>{user?.role}</span>
                    </div>
                    <div style={{ padding: '0.5rem' }}>
                      <Link to="/profile" style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', padding: '0.5rem 0.75rem', borderRadius: 'var(--radius-md)', color: 'var(--text-secondary)', textDecoration: 'none', fontSize: '0.875rem', transition: 'all var(--transition-fast)' }}
                        onMouseEnter={e => { e.currentTarget.style.background = 'var(--bg-hover)'; e.currentTarget.style.color = 'var(--text-primary)'; }}
                        onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'var(--text-secondary)'; }}
                      >
                        <User size={15} /> Profile
                      </Link>
                      <button
                        onClick={handleLogout}
                        style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', padding: '0.5rem 0.75rem', borderRadius: 'var(--radius-md)', color: 'var(--red-400)', background: 'transparent', border: 'none', cursor: 'pointer', fontSize: '0.875rem', width: '100%', transition: 'all var(--transition-fast)', fontFamily: "'Inter', sans-serif" }}
                        onMouseEnter={e => { e.currentTarget.style.background = 'rgba(239,68,68,0.1)'; }}
                        onMouseLeave={e => { e.currentTarget.style.background = 'transparent'; }}
                      >
                        <LogOut size={15} /> Logout
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </>
          ) : (
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <Link to="/login" className="btn btn-ghost btn-sm">Login</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
            </div>
          )}

          {/* Mobile Menu Toggle */}
          {isAuthenticated && (
            <button
              className="btn btn-ghost btn-icon"
              onClick={() => setMenuOpen(!menuOpen)}
              style={{ display: 'none' }}
              id="mobile-menu-btn"
            >
              {menuOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          )}
        </div>
      </div>

      {/* Mobile Dropdown */}
      {menuOpen && isAuthenticated && (
        <div style={{
          position: 'absolute', top: '100%', left: 0, right: 0,
          background: 'rgba(10,15,10,0.97)',
          backdropFilter: 'blur(20px)',
          borderBottom: '1px solid var(--border)',
          padding: '0.75rem 1.5rem 1.25rem',
          animation: 'slideUp 0.25s ease forwards',
        }}>
          {links.map(({ to, icon: Icon, label }) => (
            <Link
              key={to} to={to}
              style={{
                display: 'flex', alignItems: 'center', gap: '0.75rem',
                padding: '0.625rem 0.75rem', borderRadius: 'var(--radius-md)',
                marginBottom: '0.25rem',
                color: isActive(to) ? 'var(--green-400)' : 'var(--text-secondary)',
                background: isActive(to) ? 'rgba(34,197,94,0.1)' : 'transparent',
                textDecoration: 'none', fontSize: '0.9rem', fontWeight: 500,
              }}
            >
              <Icon size={17} />
              {label}
            </Link>
          ))}
        </div>
      )}

      <style>{`
        @media (max-width: 768px) {
          #mobile-menu-btn { display: flex !important; }
          .hidden-mobile { display: none !important; }
        }
      `}</style>
    </nav>
  );
}
