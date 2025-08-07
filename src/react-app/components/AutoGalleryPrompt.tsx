import { useState, useEffect } from 'react';
import { useGalleryAccess } from '../hooks/useGalleryAccess';
import { Camera, FolderOpen, Settings } from 'lucide-react';

interface AutoGalleryPromptProps {
  onPermissionGranted?: () => void;
}

export default function AutoGalleryPrompt({ onPermissionGranted }: AutoGalleryPromptProps) {
  const { isNative, hasPermissions, isLoading, requestPermissions } = useGalleryAccess();
  const [showPrompt, setShowPrompt] = useState(false);
  const [hasTriedPermissions, setHasTriedPermissions] = useState(false);

  useEffect(() => {
    // Auto-request permissions immediately when component mounts (for native apps)
    if (isNative && !hasPermissions && !hasTriedPermissions && !isLoading) {
      setHasTriedPermissions(true);
      // Auto-prompt for permissions immediately
      setShowPrompt(true);
      requestPermissions().then((granted) => {
        if (granted) {
          setShowPrompt(false);
          onPermissionGranted?.();
        }
      });
    } else if (isNative && hasPermissions) {
      onPermissionGranted?.();
    } else if (isNative && !hasPermissions) {
      // Show prompt if permissions are not granted
      setShowPrompt(true);
    }
  }, [isNative, hasPermissions, hasTriedPermissions, isLoading, requestPermissions, onPermissionGranted]);

  const handleRequestPermissions = async () => {
    const granted = await requestPermissions();
    if (granted) {
      setShowPrompt(false);
      onPermissionGranted?.();
    }
  };

  // Don't show prompt for web or if permissions already granted
  if (!isNative || hasPermissions || !showPrompt) {
    return null;
  }

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="bg-gray-900 border border-white/20 rounded-2xl p-6 max-w-md w-full">
        <div className="text-center space-y-6">
          <div className="w-20 h-20 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center mx-auto">
            <FolderOpen className="w-10 h-10 text-white" />
          </div>
          
          <div className="space-y-3">
            <h3 className="text-xl font-bold text-white">Access Your Photos</h3>
            <p className="text-white/70 text-sm leading-relaxed">
              PhotoKik needs access to your photos to help you organize them with AI-powered swipe sorting.
            </p>
          </div>

          <div className="space-y-4">
            <div className="flex items-center space-x-3 text-left">
              <div className="w-8 h-8 bg-green-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                <Camera className="w-4 h-4 text-green-400" />
              </div>
              <p className="text-white/80 text-sm">Smart photo categorization and duplicate detection</p>
            </div>
            
            <div className="flex items-center space-x-3 text-left">
              <div className="w-8 h-8 bg-blue-500/20 rounded-full flex items-center justify-center flex-shrink-0">
                <Settings className="w-4 h-4 text-blue-400" />
              </div>
              <p className="text-white/80 text-sm">All processing happens locally on your device</p>
            </div>
          </div>

          <div className="space-y-3">
            <button
              onClick={handleRequestPermissions}
              disabled={isLoading}
              className="w-full px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white rounded-xl font-medium transition-all duration-200 disabled:opacity-50"
            >
              {isLoading ? 'Requesting Access...' : 'Grant Photo Access'}
            </button>
            
            <button
              onClick={() => setShowPrompt(false)}
              className="w-full px-6 py-2 text-white/60 hover:text-white text-sm transition-colors"
            >
              Skip for now
            </button>
          </div>

          <p className="text-white/50 text-xs">
            You can change this permission anytime in your device settings
          </p>
        </div>
      </div>
    </div>
  );
}
