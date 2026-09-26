import React, { createContext, useContext, useState, useCallback } from 'react';
import api from '../services/api';

const UserContext = createContext(null);

export function UserProvider({ children }) {
  const [wallet, setWallet] = useState(null);
  const [notifications, setNotifications] = useState([]);

  const fetchWallet = useCallback(async () => {
    try {
      const res = await api.get('/api/wallet');
      setWallet(res.data.data);
    } catch {
      // not authenticated yet
    }
  }, []);

  const fetchNotifications = useCallback(async () => {
    try {
      const res = await api.get('/api/notifications');
      setNotifications(res.data.data || []);
    } catch {
      // not authenticated yet
    }
  }, []);

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <UserContext.Provider value={{ wallet, setWallet, fetchWallet, notifications, fetchNotifications, unreadCount }}>
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  const ctx = useContext(UserContext);
  if (!ctx) throw new Error('useUser must be used within UserProvider');
  return ctx;
}
