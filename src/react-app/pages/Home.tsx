import { useState, useEffect } from 'react';
import Header from '../components/Header';
import SwipeInterface from '../components/SwipeInterface';
import Gallery from '../components/Gallery';
import Settings from '../components/Settings';
import Trash from '../components/Trash';
import SeamlessPhotoAccess from '../components/SeamlessPhotoAccess';
import AutoGalleryPrompt from '../components/AutoGalleryPrompt';
import { StatusBar, Style } from '@capacitor/status-bar';
import { SplashScreen } from '@capacitor/splash-screen';
import { Capacitor } from '@capacitor/core';

// Mock data for development
const mockPhotos = [
  { id: 1, filename: 'sunset-beach.jpg', file_path: '', category: 'memories' },
  { id: 2, filename: 'family-dinner.jpg', file_path: '', category: 'memories' },
  { id: 3, filename: 'document-scan.jpg', file_path: '', category: 'documents' },
  { id: 4, filename: 'blurry-photo.jpg', file_path: '', category: 'blurry' },
  { id: 5, filename: 'duplicate-1.jpg', file_path: '', category: 'duplicates' },
  { id: 6, filename: 'vacation-mountain.jpg', file_path: '', category: 'memories' },
  { id: 7, filename: 'receipt-grocery.jpg', file_path: '', category: 'documents' },
  { id: 8, filename: 'concert-night.jpg', file_path: '', category: 'memories' },
];

export default function Home() {
  const [currentView, setCurrentView] = useState('swipe');
  const [photos, setPhotos] = useState(mockPhotos);
  const [swipeSensitivity] = useState(0.5);
  const [isFullScreen, setIsFullScreen] = useState(false);

  // Initialize native app
  useEffect(() => {
    const initializeApp = async () => {
      if (Capacitor.isNativePlatform()) {
        try {
          // Hide splash screen
          await SplashScreen.hide();
          
          // Set status bar style
          await StatusBar.setStyle({ style: Style.Dark });
          await StatusBar.setBackgroundColor({ color: '#1e1b4b' });
        } catch (error) {
          console.error('Error initializing app:', error);
        }
      }
    };

    initializeApp();
    loadPhotos();
  }, []);

  const loadPhotos = async () => {
    try {
      const response = await fetch('/api/photos');
      const data = await response.json();
      if (data.photos && data.photos.length > 0) {
        setPhotos(data.photos);
      }
    } catch (error) {
      console.error('Error loading photos:', error);
      // Keep using mock data if API fails
    }
  };

  const handlePhotoAction = async (photoId: number, action: 'keep' | 'kik') => {
    console.log(`Photo ${photoId}: ${action}`);
    
    if (action === 'kik') {
      try {
        // Move photo to trash via API
        const response = await fetch('/api/photos/trash', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ photoId })
        });
        
        if (response.ok) {
          // Remove from current photos list
          setPhotos(prev => prev.filter(photo => photo.id !== photoId));
        }
      } catch (error) {
        console.error('Error moving photo to trash:', error);
        // Fallback to local removal if API fails
        setPhotos(prev => prev.filter(photo => photo.id !== photoId));
      }
    }
  };

  const handlePhotoAdded = async (photo: any) => {
    try {
      // Add photo to backend
      const response = await fetch('/api/photos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          filename: photo.filename,
          file_path: photo.file_path,
          category: 'memories'
        })
      });

      if (response.ok) {
        const result = await response.json();
        // Add to local state
        setPhotos(prev => [{
          id: result.photoId,
          filename: photo.filename,
          file_path: photo.file_path,
          category: 'memories'
        }, ...prev]);
      } else {
        // Fallback to local state only
        setPhotos(prev => [photo, ...prev]);
      }
    } catch (error) {
      console.error('Error saving photo:', error);
      // Fallback to local state only
      setPhotos(prev => [photo, ...prev]);
    }
  };

  const renderView = () => {
    switch (currentView) {
      case 'swipe':
        return (
          <SwipeInterface
            photos={photos}
            onPhotoAction={handlePhotoAction}
            swipeSensitivity={swipeSensitivity}
            storageUsed={45}
            totalStorage={128}
          />
        );
      case 'gallery':
        return <Gallery photos={photos} />;
      case 'trash':
        return (
          <Trash
            onPhotoRestore={(photoId) => {
              console.log(`Photo ${photoId} restored`);
              // Reload photos to include restored photo
              loadPhotos();
            }}
            onPhotoDelete={(photoId) => {
              console.log(`Photo ${photoId} permanently deleted`);
            }}
          />
        );
      case 'settings':
        return <Settings />;
      default:
        return null;
    }
  };

  return (
    <div className={`min-h-screen bg-gradient-to-br from-purple-900 via-blue-900 to-indigo-900 ${isFullScreen ? 'fixed inset-0 z-50' : ''}`}>
      {!isFullScreen && (
        <Header
          currentView={currentView}
          onViewChange={setCurrentView}
          onToggleFullScreen={() => setIsFullScreen(!isFullScreen)}
        />
      )}
      
      <main className={`${isFullScreen ? 'h-screen' : 'h-[calc(100vh-100px)] sm:h-[calc(100vh-80px)]'} ${currentView === 'swipe' ? 'p-1 sm:p-4 overflow-hidden' : 'overflow-y-auto scroll-container'}`}>
        {isFullScreen && (
          <button
            onClick={() => setIsFullScreen(false)}
            className="fixed top-4 right-4 z-50 w-10 h-10 bg-black/50 hover:bg-black/70 backdrop-blur-sm rounded-full flex items-center justify-center text-white transition-all duration-200"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        )}
        {renderView()}
      </main>

      {/* Seamless Photo Access - hidden but handles gallery permissions automatically */}
      <SeamlessPhotoAccess 
        onPhotoAdded={handlePhotoAdded}
        enabled={currentView === 'gallery' || currentView === 'swipe'}
      />
      
      {/* Auto Gallery Permission Prompt - Always show to auto-detect permissions */}
      <AutoGalleryPrompt 
        onPermissionGranted={() => {
          console.log('Gallery permissions granted');
          loadPhotos();
        }}
      />
    </div>
  );
}
