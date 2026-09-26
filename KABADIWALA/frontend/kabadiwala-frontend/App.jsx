import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './src/context/AuthContext';
import { LanguageProvider } from './src/context/LanguageContext';
import { LocationProvider } from './src/context/LocationContext';
import { UserProvider } from './src/context/UserContext';
import Navbar from './src/components/common/Navbar';
import AppRoutes from './src/routes/AppRoutes';
import Footer from './src/components/common/Footer';

export default function App() {
  return (
    <BrowserRouter>
      <LanguageProvider>
        <AuthProvider>
          <LocationProvider>
            <UserProvider>
              <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
                <Navbar />
                <main style={{ flex: 1 }}>
                  <AppRoutes />
                </main>
                <Footer />
              </div>
            </UserProvider>
          </LocationProvider>
        </AuthProvider>
      </LanguageProvider>
    </BrowserRouter>
  );
}
