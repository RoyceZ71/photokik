import { useRef } from 'react';
import { useNativeCamera } from '../hooks/useNativeCamera';
import { Camera, Plus } from 'lucide-react';

interface PhotoUploaderProps {
  onPhotoTaken?: (photo: any) => void;
  className?: string;
}

export default function PhotoUploader({ onPhotoTaken, className = '' }: PhotoUploaderProps) {
  const { isNative, takePhoto, pickPhoto } = useNativeCamera();
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleTakePhoto = async () => {
    try {
      const photo = await takePhoto();
      if (photo && onPhotoTaken) {
        onPhotoTaken(photo);
      }
    } catch (error) {
      console.error('Error taking photo:', error);
    }
  };

  const handlePickPhoto = async () => {
    try {
      const photo = await pickPhoto();
      if (photo && onPhotoTaken) {
        onPhotoTaken(photo);
      }
    } catch (error) {
      console.error('Error picking photo:', error);
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
      e.target.value = ''; // Reset input
    }
  };

  if (!isNative) {
    // Web version with file input
    return (
      <div className={className}>
        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          onChange={handleWebFileInput}
          className="hidden"
          id="photo-uploader-input"
        />
        <label
          htmlFor="photo-uploader-input"
          className="inline-flex items-center px-4 py-2 bg-purple-500 hover:bg-purple-600 text-white rounded-lg cursor-pointer transition-colors"
        >
          <Camera className="w-4 h-4 mr-2" />
          Add Photo
        </label>
      </div>
    );
  }

  // Native version with camera/gallery options
  return (
    <div className={`flex space-x-3 ${className}`}>
      <button
        onClick={handleTakePhoto}
        className="flex items-center px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded-lg transition-colors"
      >
        <Camera className="w-4 h-4 mr-2" />
        Take Photo
      </button>
      
      <button
        onClick={handlePickPhoto}
        className="flex items-center px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors"
      >
        <Plus className="w-4 h-4 mr-2" />
        From Gallery
      </button>
    </div>
  );
}
