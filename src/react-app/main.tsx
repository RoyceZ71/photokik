import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import './index.css';

// Initialize Capacitor when app loads
import { Capacitor } from '@capacitor/core';
import { SplashScreen } from '@capacitor/splash-screen';
import { StatusBar, Style } from '@capacitor/status-bar';

const initializeApp = async () => {
  if (Capacitor.isNativePlatform()) {
    try {
      // Configure status bar for dark theme
      await StatusBar.setStyle({ style: Style.Dark });
      await StatusBar.setBackgroundColor({ color: '#1e1b4b' });
      
      // Hide splash screen after a short delay
      setTimeout(async () => {
        await SplashScreen.hide();
      }, 1000);
    } catch (error) {
      console.error('Error initializing native features:', error);
    }
  }
};

// Initialize app
initializeApp();

const root = createRoot(document.getElementById('root')!);

root.render(
  <StrictMode>
    <App />
  </StrictMode>
);
