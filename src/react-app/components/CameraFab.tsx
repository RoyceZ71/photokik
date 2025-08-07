import { useState } from 'react';
import { Camera, Plus, X } from 'lucide-react';
import { useNativeCamera } from '../hooks/useNativeCamera';

interface CameraFabProps {
  onPhotoTaken?: (photo: any) => void;
}

export default function CameraFab({ onPhotoTaken }: CameraFabProps) {
  const { isNative, takePhoto, pickPhoto } = useNativeCamera();
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleTakePhoto = async () => {
    try {
      setIsLoading(true);
      const photo = await takePhoto();
      if (photo && onPhotoTaken) {
        onPhotoTaken(photo);
      }
      setIsOpen(false);
    } catch (error) {
      console.error('Error taking photo:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handlePickPhoto = async () => {
    try {
      setIsLoading(true);
      const photo = await pickPhoto();
      if (photo && onPhotoTaken) {
        onPhotoTaken(photo);
      }
      setIsOpen(false);
    } catch (error) {
      console.error('Error picking photo:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleWebFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      const file = files[0];
      const photo = {
        id: Date.now(),
        filename: file.name,
        file_path: URL.createObjectURL(file),
        webPath: URL.createObjectURL(file)
      };
      
      if (onPhotoTaken) {
        onPhotoTaken(photo);
      }
      setIsOpen(false);
      e.target.value = ''; // Reset input
    }
  };

  if (!isNative) {
    // Web version with file input
    return (
      <>
        <input
          type="file"
          accept="image/*"
          onChange={handleWebFileInput}
          className="hidden"
          id="camera-fab-input"
        />
        <label
          htmlFor="camera-fab-input"
          className="fixed bottom-16 right-4 sm:bottom-8 sm:right-8 w-12 h-12 bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600 text-white rounded-full flex items-center justify-center shadow-xl hover:shadow-2xl transition-all duration-200 cursor-pointer z-40 hover:scale-110 active:scale-95"
        >
          <Plus className="w-6 h-6" />
        </label>
      </>
    );
  }

  return (
    <div className="fixed bottom-16 right-4 sm:bottom-8 sm:right-8 z-40">
      {/* Action buttons */}
      <div className={`flex flex-col items-end space-y-2 mb-3 transition-all duration-300 ${
        isOpen ? 'opacity-100 transform translate-y-0' : 'opacity-0 transform translate-y-4 pointer-events-none'
      }`}>
        <button
          onClick={handleTakePhoto}
          disabled={isLoading}
          className="w-10 h-10 bg-green-500 hover:bg-green-600 text-white rounded-full flex items-center justify-center shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-110 active:scale-95 disabled:opacity-50"
        >
          <Camera className="w-5 h-5" />
        </button>
        
        <button
          onClick={handlePickPhoto}
          disabled={isLoading}
          className="w-10 h-10 bg-blue-500 hover:bg-blue-600 text-white rounded-full flex items-center justify-center shadow-lg hover:shadow-xl transition-all duration-200 hover:scale-110 active:scale-95 disabled:opacity-50"
        >
          <Plus className="w-5 h-5" />
        </button>
      </div>

      {/* Main FAB */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        disabled={isLoading}
        className={`w-12 h-12 text-white rounded-full flex items-center justify-center shadow-xl hover:shadow-2xl transition-all duration-200 hover:scale-110 active:scale-95 disabled:opacity-50 ${
          isOpen 
            ? 'bg-gradient-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700' 
            : 'bg-gradient-to-r from-purple-500 to-pink-500 hover:from-purple-600 hover:to-pink-600'
        }`}
      >
        {isLoading ? (
          <div className="w-6 h-6 border-2 border-white border-t-transparent rounded-full animate-spin" />
        ) : isOpen ? (
          <X className="w-6 h-6" />
        ) : (
          <Plus className="w-6 h-6" />
        )}
      </button>
    </div>
  );
}
