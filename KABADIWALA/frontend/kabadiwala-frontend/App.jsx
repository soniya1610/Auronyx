import React from 'react'
import { BrowserRouter } from 'react-router-dom'
import { AuthProvider } from './src/context/AuthContext'
import AppRoutes from './src/routes/AppRoutes'

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
