import { useEffect, useRef } from 'react';
import { useGalleryAccess } from '../hooks/useGalleryAccess';
import { Haptics, ImpactStyle } from '@capacitor/haptics';
import { Capacitor } from '@capacitor/core';

interface SeamlessPhotoAccessProps {
  onPhotoAdded?: (photo: any) => void;
  enabled?: boolean;
}

export default function SeamlessPhotoAccess({ onPhotoAdded, enabled = true }: SeamlessPhotoAccessProps) {
  const { 
    isNative, 
    hasPermissions, 
    isLoading, 
    requestPermissions,
    pickFromGallerySeamlessly,
    handleWebFileInput 
  } = useGalleryAccess();
  
  const fileInputRef = useRef<HTMLInputElement>(null);

  // Auto-request permissions when component mounts
  useEffect(() => {
    if (enabled && isNative && !hasPermissions && !isLoading) {
      requestPermissions();
    }
  }, [enabled, isNative, hasPermissions, isLoading, requestPermissions]);

  const handleHaptic = async () => {
    if (Capacitor.isNativePlatform()) {
      try {
        await Haptics.impact({ style: ImpactStyle.Light });
      } catch (error) {
        // Haptics not available, ignore
      }
    }
  };

  const triggerPhotoAccess = async () => {
    await handleHaptic();
    
    if (isNative) {
      // Use native gallery picker
      const photo = await pickFromGallerySeamlessly();
      if (photo && onPhotoAdded) {
        onPhotoAdded(photo);
      }
    } else {
      // Trigger web file input
      fileInputRef.current?.click();
    }
  };

  const handleWebFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const photo = handleWebFileInput(e.target.files);
    if (photo && onPhotoAdded) {
      onPhotoAdded(photo);
    }
    // Reset input for future selections
    if (e.target) {
      e.target.value = '';
    }
  };

  // For web, provide invisible file input
  if (!isNative) {
    return (
      <>
        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          multiple
          onChange={handleWebFileChange}
          className="hidden"
        />
        {/* Hidden trigger that can be activated programmatically */}
        <button
          onClick={triggerPhotoAccess}
          className="hidden"
          aria-label="Add photos from gallery"
        />
      </>
    );
  }

  // For native, provide invisible permission handler
  return (
    <div className="hidden">
      {/* Permission status indicator for debugging */}
      {process.env.NODE_ENV === 'development' && (
        <div className="fixed top-0 right-0 bg-black/80 text-white p-2 text-xs z-50">
          Permissions: {hasPermissions ? '✅' : '❌'}
          {isLoading && ' (Loading...)'}
        </div>
      )}
    </div>
  );
}
