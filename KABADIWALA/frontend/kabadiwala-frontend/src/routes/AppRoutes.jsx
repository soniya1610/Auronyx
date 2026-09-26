import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Home from '../pages/Home';
import Splash from '../pages/Splash';
import Login from '../components/auth/Login';
import Register from '../components/auth/Register';
import UserDashboard from '../pages/UserDashboard';
import CollectorDashboard from '../pages/CollectorDashboard';
import RecyclerDashboard from '../pages/RecyclerDashboard';
import AdminDashboard from '../pages/AdminDashboard';
import Loader from '../components/common/Loader';

function ProtectedRoute({ children, allowedRoles }) {
  const { user, loading, isAuthenticated } = useAuth();

  if (loading) return <Loader text="Authenticating..." />;
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/splash" element={<Splash />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* User Routes */}
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute allowedRoles={['USER', 'ADMIN']}>
            <UserDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/pickups"
        element={
          <ProtectedRoute allowedRoles={['USER', 'ADMIN']}>
            <UserDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/wallet"
        element={
          <ProtectedRoute allowedRoles={['USER', 'ADMIN']}>
            <UserDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/recycling"
        element={
          <ProtectedRoute allowedRoles={['USER', 'ADMIN']}>
            <UserDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/rewards"
        element={
          <ProtectedRoute allowedRoles={['USER', 'ADMIN']}>
            <UserDashboard />
          </ProtectedRoute>
        }
      />

      {/* Collector Routes */}
      <Route
        path="/collector"
        element={
          <ProtectedRoute allowedRoles={['COLLECTOR', 'ADMIN']}>
            <CollectorDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/collector/pickups"
        element={
          <ProtectedRoute allowedRoles={['COLLECTOR', 'ADMIN']}>
            <CollectorDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/collector/earnings"
        element={
          <ProtectedRoute allowedRoles={['COLLECTOR', 'ADMIN']}>
            <CollectorDashboard />
          </ProtectedRoute>
        }
      />

      {/* Recycler Routes */}
      <Route
        path="/recycler"
        element={
          <ProtectedRoute allowedRoles={['RECYCLER', 'ADMIN']}>
            <RecyclerDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/recycler/incoming"
        element={
          <ProtectedRoute allowedRoles={['RECYCLER', 'ADMIN']}>
            <RecyclerDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/recycler/records"
        element={
          <ProtectedRoute allowedRoles={['RECYCLER', 'ADMIN']}>
            <RecyclerDashboard />
          </ProtectedRoute>
        }
      />

      {/* Admin Route */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
