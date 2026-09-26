import React, { createContext, useContext, useState, useEffect } from 'react';

const SUPPORTED_LANGS = [
  { code: 'en', label: 'English', native: 'English' },
  { code: 'hi', label: 'Hindi', native: 'हिंदी' },
  { code: 'regional', label: 'Regional', native: 'Regional' },
];

const LanguageContext = createContext(null);

export function LanguageProvider({ children }) {
  const [lang, setLang] = useState(() => {
    return localStorage.getItem('kabadiwala_lang') || 'en';
  });
  const [translations, setTranslations] = useState({});

  useEffect(() => {
    import(`../i18n/${lang}.json`)
      .then((mod) => setTranslations(mod.default || mod))
      .catch(() => setTranslations({}));
    localStorage.setItem('kabadiwala_lang', lang);
  }, [lang]);

  const t = (key, fallback = key) => {
    return translations[key] || fallback;
  };

  return (
    <LanguageContext.Provider value={{ lang, setLang, t, SUPPORTED_LANGS }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const ctx = useContext(LanguageContext);
  if (!ctx) throw new Error('useLanguage must be used within LanguageProvider');
  return ctx;
}
