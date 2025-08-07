import { useState, useEffect } from 'react';
import { useLanguage } from '../contexts/LanguageContext';
import { useNativeFeatures } from '../hooks/useNativeFeatures';
import SwipeCard from './SwipeCard';

import { 
  Heart, 
  X, 
  RotateCcw,
  ImageIcon,
  Camera 
} from 'lucide-react';

interface Photo {
  id: number;
  filename: string;
  file_path: string;
  category?: string;
}

interface SwipeInterfaceProps {
  photos: Photo[];
  onPhotoAction: (photoId: number, action: 'keep' | 'kik') => void;
  swipeSensitivity?: number;
  storageUsed?: number;
  totalStorage?: number;
}

export default function SwipeInterface({ 
  photos, 
  onPhotoAction, 
  swipeSensitivity = 0.5,
  storageUsed = 0,
  totalStorage = 100
}: SwipeInterfaceProps) {
  const { t } = useLanguage();
  const { triggerHaptic } = useNativeFeatures();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [reviewedCount, setReviewedCount] = useState(0);
  const [isProcessing, setIsProcessing] = useState(false);
  const [showUndoOption, setShowUndoOption] = useState(false);
  const [lastAction, setLastAction] = useState<{ photoId: number; action: 'keep' | 'kik' } | null>(null);
  

  // Get current photo
  const currentPhoto = photos[currentIndex];
  const hasPhotos = photos.length > 0;
  const isComplete = currentIndex >= photos.length;

  useEffect(() => {
    if (showUndoOption) {
      const timer = setTimeout(() => {
        setShowUndoOption(false);
        setLastAction(null);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [showUndoOption]);

  const handlePhotoAction = async (action: 'keep' | 'kik') => {
    if (!currentPhoto || isProcessing) return;

    setIsProcessing(true);
    await triggerHaptic();

    try {
      await onPhotoAction(currentPhoto.id, action);
      
      setLastAction({ photoId: currentPhoto.id, action });
      setShowUndoOption(true);
      setReviewedCount(prev => prev + 1);
      setCurrentIndex(prev => prev + 1);
    } catch (error) {
      console.error('Error processing photo action:', error);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleUndo = async () => {
    if (!lastAction) return;

    await triggerHaptic();
    
    // Restore the photo (if it was kik'd, restore it)
    if (lastAction.action === 'kik') {
      try {
        await fetch('/api/photos/restore', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ photoId: lastAction.photoId })
        });
      } catch (error) {
        console.error('Error undoing action:', error);
      }
    }

    setCurrentIndex(prev => Math.max(0, prev - 1));
    setReviewedCount(prev => Math.max(0, prev - 1));
    setShowUndoOption(false);
    setLastAction(null);
  };

  const handleSwipeGesture = (direction: 'keep' | 'kik') => {
    handlePhotoAction(direction);
  };

  

  

  if (!hasPhotos) {
    return (
      <div className="h-full flex flex-col items-center justify-center p-6 text-center">
        <div className="w-40 h-40 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center mx-auto shadow-2xl mb-8">
          <ImageIcon className="w-20 h-20 text-white" />
        </div>
        <h2 className="text-3xl sm:text-4xl font-bold text-white mb-6">
          Ready to organize!
        </h2>
        <p className="text-white/70 text-xl mb-10 max-w-2xl">
          Add some photos to start the smart organization process with AI-powered categorization.
        </p>
        <div className="bg-white/10 backdrop-blur-md rounded-xl p-8 border border-white/20 max-w-2xl">
          <h3 className="font-semibold text-white mb-4 text-xl">How it works:</h3>
          <div className="space-y-3 text-white/80 text-base">
            <div className="flex items-center space-x-4">
              <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-white text-sm font-bold">1</span>
              </div>
              <span>Swipe right to keep photos</span>
            </div>
            <div className="flex items-center space-x-4">
              <div className="w-8 h-8 bg-red-500 rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-white text-sm font-bold">2</span>
              </div>
              <span>Swipe left to remove photos</span>
            </div>
            <div className="flex items-center space-x-4">
              <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center flex-shrink-0">
                <span className="text-white text-sm font-bold">3</span>
              </div>
              <span>AI organizes everything automatically</span>
            </div>
          </div>
        </div>
        
        
      </div>
    );
  }

  if (isComplete) {
    return (
      <div className="h-full flex flex-col items-center justify-center p-6 text-center">
        <div className="w-32 h-32 bg-gradient-to-r from-green-500 to-emerald-500 rounded-full flex items-center justify-center mx-auto shadow-2xl mb-6 animate-pulse">
          <Heart className="w-16 h-16 text-white" />
        </div>
        <h2 className="text-2xl sm:text-3xl font-bold text-white mb-4">
          {t('allDone')}
        </h2>
        <p className="text-white/70 text-lg mb-6">
          {t('allDoneDesc')}
        </p>
        <div className="bg-white/10 backdrop-blur-md rounded-xl p-6 border border-white/20">
          <div className="text-center">
            <div className="text-3xl font-bold text-white mb-2">{reviewedCount}</div>
            <div className="text-white/70">{t('reviewedPhotos')}</div>
          </div>
        </div>
        
        
      </div>
    );
  }

  const storagePercentage = Math.min((storageUsed / totalStorage) * 100, 100);
  const freeSpace = totalStorage - storageUsed;
  
  const getStorageStatus = () => {
    if (storagePercentage > 90) return { text: 'Critical', color: 'text-red-400' };
    if (storagePercentage > 80) return { text: 'Almost Full', color: 'text-red-300' };
    if (storagePercentage > 60) return { text: 'Getting Full', color: 'text-yellow-300' };
    if (storagePercentage > 30) return { text: 'Half Full', color: 'text-blue-300' };
    return { text: 'Plenty of Space', color: 'text-green-300' };
  };

  const storageStatus = getStorageStatus();

  return (
    <div className="h-full relative overflow-hidden">
      {/* PhotoKik Logo and Storage Meter */}
      <div className="absolute top-4 left-0 right-0 z-20 px-4">
        {/* PhotoKik Logo */}
        <div className="flex items-center justify-center mb-3">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center shadow-lg photokik-logo-glow">
              <Camera className="w-5 h-5 text-white" />
            </div>
            <h1 className="text-xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent photokik-text-glow">
              PhotoKik
            </h1>
          </div>
        </div>

        {/* Storage Meter */}
        <div className="flex items-center justify-center mb-4">
          <div className="bg-white/10 backdrop-blur-sm rounded-xl px-4 py-2 border border-white/20">
            <div className="flex items-center space-x-3">
              <div className="flex items-center space-x-2">
                <div className="w-20 bg-white/20 rounded-full h-2">
                  <div
                    className={`h-2 rounded-full transition-all duration-300 ${
                      storagePercentage > 80 ? 'bg-red-500' : 
                      storagePercentage > 60 ? 'bg-yellow-500' : 'bg-green-500'
                    }`}
                    style={{ width: `${storagePercentage}%` }}
                  />
                </div>
                <span className="text-white/70 text-xs font-medium">
                  {Math.round(storagePercentage)}%
                </span>
              </div>
              <div className="text-xs text-white/50">
                {freeSpace.toFixed(1)}GB free
              </div>
              <div className={`text-xs font-medium ${storageStatus.color}`}>
                {storageStatus.text}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Progress Section */}
      <div className="absolute top-32 left-0 right-0 z-20 px-4">
        {/* Progress Bar */}
        <div className="bg-white/20 backdrop-blur-sm rounded-full h-2 mb-2">
          <div 
            className="bg-gradient-to-r from-purple-500 to-pink-500 h-2 rounded-full transition-all duration-300"
            style={{ width: `${(currentIndex / photos.length) * 100}%` }}
          />
        </div>
        <div className="flex justify-between text-white/80 text-sm">
          <span>{currentIndex + 1} {t('of')} {photos.length}</span>
          <span>{reviewedCount} {t('reviewedPhotos')}</span>
        </div>
      </div>

      {/* Undo Button */}
      {showUndoOption && (
        <div className="absolute top-24 left-1/2 transform -translate-x-1/2 z-30">
          <button
            onClick={handleUndo}
            className="flex items-center space-x-2 bg-yellow-500 hover:bg-yellow-600 text-black px-4 py-2 rounded-full shadow-lg transition-all duration-200 animate-bounce"
          >
            <RotateCcw className="w-4 h-4" />
            <span className="font-medium">Undo</span>
          </button>
        </div>
      )}

      {/* Main Swipe Area */}
      <div className="h-full flex items-center justify-center p-4 pt-48 pb-32">
        <div className="flex items-center justify-center w-full">
          {/* Current Photo - Perfectly Centered */}
          <SwipeCard
            photo={currentPhoto}
            onSwipe={handleSwipeGesture}
            swipeSensitivity={swipeSensitivity}
          />

          {/* Next Photo Preview (behind current) */}
          {photos[currentIndex + 1] && (
            <div className="absolute inset-0 flex items-center justify-center -z-10 transform scale-95 opacity-50">
              <div className="w-[85vw] h-[55vh] max-w-lg max-h-[600px] min-h-[450px] sm:w-80 sm:h-[500px] lg:w-96 lg:h-[580px] xl:w-[420px] xl:h-[620px] bg-white/10 backdrop-blur-md rounded-3xl border border-white/20 overflow-hidden">
                <img
                  src={photos[currentIndex + 1].file_path || `https://picsum.photos/400/600?random=${photos[currentIndex + 1].id}`}
                  alt={photos[currentIndex + 1].filename}
                  className="w-full h-full object-cover"
                />
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Action Buttons - Positioned under the swipe photo */}
      <div className="absolute bottom-8 sm:bottom-6 left-0 right-0 z-20 px-4">
        <div className="flex items-center justify-center space-x-8 sm:space-x-12">
          {/* Kik Button with Glow */}
          <button
            onClick={() => handlePhotoAction('kik')}
            disabled={isProcessing}
            className="w-16 h-16 sm:w-20 sm:h-20 bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white rounded-full flex items-center justify-center shadow-2xl transition-all duration-200 hover:scale-110 active:scale-95 disabled:opacity-50 x-glow"
          >
            <X className="w-8 h-8 sm:w-10 sm:h-10" />
          </button>

          {/* Keep Button with Glow */}
          <button
            onClick={() => handlePhotoAction('keep')}
            disabled={isProcessing}
            className="w-16 h-16 sm:w-20 sm:h-20 bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700 text-white rounded-full flex items-center justify-center shadow-2xl transition-all duration-200 hover:scale-110 active:scale-95 disabled:opacity-50 heart-glow"
          >
            <Heart className="w-8 h-8 sm:w-10 sm:h-10" />
          </button>
        </div>
      </div>

      
    </div>
  );
}
