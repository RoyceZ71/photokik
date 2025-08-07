import { useState, useEffect } from 'react';
import { Capacitor } from '@capacitor/core';
import { App } from '@capacitor/app';
import { Haptics, ImpactStyle } from '@capacitor/haptics';

export function useNativeFeatures() {
  const [isNative] = useState(() => Capacitor.isNativePlatform());
  const [appInfo, setAppInfo] = useState<{ name: string; version: string } | null>(null);

  useEffect(() => {
    if (isNative) {
      let appStateListener: any;
      let urlOpenListener: any;

      const setupListeners = async () => {
        try {
          // Get app info
          const info = await App.getInfo();
          setAppInfo({
            name: info.name,
            version: info.version
          });

          // Handle app state changes
          appStateListener = await App.addListener('appStateChange', ({ isActive }) => {
            console.log('App state changed. Is active?', isActive);
          });

          // Handle deep links
          urlOpenListener = await App.addListener('appUrlOpen', (event) => {
            console.log('App opened with URL:', event.url);
          });
        } catch (error) {
          console.warn('Error setting up app listeners:', error);
        }
      };

      setupListeners();

      return () => {
        if (appStateListener) appStateListener.remove();
        if (urlOpenListener) urlOpenListener.remove();
      };
    }
  }, [isNative]);

  const triggerHaptic = async (style: ImpactStyle = ImpactStyle.Light) => {
    if (isNative) {
      try {
        await Haptics.impact({ style });
      } catch (error) {
        console.warn('Haptics not available:', error);
      }
    }
  };

  const exitApp = async () => {
    if (isNative) {
      try {
        await App.exitApp();
      } catch (error) {
        console.warn('Exit app not available:', error);
      }
    }
  };

  const minimizeApp = async () => {
    if (isNative) {
      try {
        await App.minimizeApp();
      } catch (error) {
        console.warn('Minimize app not available:', error);
      }
    }
  };

  return {
    isNative,
    appInfo,
    triggerHaptic,
    exitApp,
    minimizeApp
  };
}
