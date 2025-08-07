import { useState, useEffect, useCallback } from 'react';
import { Camera, CameraResultType, CameraSource } from '@capacitor/camera';
import { Filesystem, Directory } from '@capacitor/filesystem';
import { Capacitor } from '@capacitor/core';

interface Photo {
  id: number;
  filename: string;
  file_path: string;
  webPath?: string;
}

export function useGalleryAccess() {
  const [isNative] = useState(() => Capacitor.isNativePlatform());
  const [hasPermissions, setHasPermissions] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [galleryPhotos] = useState<Photo[]>([]);

  // Request permissions automatically on mount
  useEffect(() => {
    if (isNative) {
      requestPermissions();
    }
  }, [isNative]);

  const requestPermissions = useCallback(async () => {
    if (!isNative) return true;

    try {
      setIsLoading(true);
      
      // Request camera and photo library permissions
      const permission = await Camera.requestPermissions({
        permissions: ['camera', 'photos']
      });

      const hasAccess = permission.camera === 'granted' && permission.photos === 'granted';
      setHasPermissions(hasAccess);
      
      if (hasAccess) {
        await loadGalleryPhotos();
      }
      
      return hasAccess;
    } catch (error) {
      console.error('Error requesting permissions:', error);
      setHasPermissions(false);
      return false;
    } finally {
      setIsLoading(false);
    }
  }, [isNative]);

  const loadGalleryPhotos = useCallback(async () => {
    if (!isNative || !hasPermissions) return;

    try {
      setIsLoading(true);
      
      // Note: Capacitor doesn't have direct gallery enumeration
      // We'll rely on the existing photo database and auto-import flow
      // This is a placeholder for future gallery integration
      
      console.log('Gallery access granted - ready for seamless photo access');
    } catch (error) {
      console.error('Error loading gallery photos:', error);
    } finally {
      setIsLoading(false);
    }
  }, [isNative, hasPermissions]);

  const takePhotoSeamlessly = useCallback(async (): Promise<Photo | null> => {
    if (!isNative) return null;

    try {
      if (!hasPermissions) {
        const granted = await requestPermissions();
        if (!granted) return null;
      }

      const image = await Camera.getPhoto({
        quality: 90,
        allowEditing: false,
        resultType: CameraResultType.Uri,
        source: CameraSource.Camera,
        saveToGallery: true
      });

      if (!image.webPath) {
        throw new Error('Failed to capture image');
      }

      const fileName = `photo_${Date.now()}.jpg`;
      
      const response = await fetch(image.webPath);
      const blob = await response.blob();
      const base64Data = await blobToBase64(blob);

      await Filesystem.writeFile({
        path: fileName,
        data: base64Data,
        directory: Directory.Documents
      });

      const fileUri = await Filesystem.getUri({
        directory: Directory.Documents,
        path: fileName
      });

      return {
        id: Date.now(),
        filename: fileName,
        file_path: fileUri.uri,
        webPath: image.webPath
      };
    } catch (error) {
      console.error('Error taking photo:', error);
      return null;
    }
  }, [isNative, hasPermissions, requestPermissions]);

  const pickFromGallerySeamlessly = useCallback(async (): Promise<Photo | null> => {
    if (!isNative) return null;

    try {
      if (!hasPermissions) {
        const granted = await requestPermissions();
        if (!granted) return null;
      }

      const image = await Camera.getPhoto({
        quality: 90,
        allowEditing: false,
        resultType: CameraResultType.Uri,
        source: CameraSource.Photos
      });

      if (!image.webPath) {
        throw new Error('Failed to pick image');
      }

      const fileName = `imported_${Date.now()}.jpg`;
      
      const response = await fetch(image.webPath);
      const blob = await response.blob();
      const base64Data = await blobToBase64(blob);

      await Filesystem.writeFile({
        path: fileName,
        data: base64Data,
        directory: Directory.Documents
      });

      const fileUri = await Filesystem.getUri({
        directory: Directory.Documents,
        path: fileName
      });

      return {
        id: Date.now(),
        filename: fileName,
        file_path: fileUri.uri,
        webPath: image.webPath
      };
    } catch (error) {
      console.error('Error picking photo:', error);
      return null;
    }
  }, [isNative, hasPermissions, requestPermissions]);

  // Auto-import from web file input (for web fallback)
  const handleWebFileInput = useCallback((files: FileList | null) => {
    if (!files || files.length === 0) return null;
    
    const file = files[0];
    const photo = {
      id: Date.now(),
      filename: file.name,
      file_path: URL.createObjectURL(file),
      webPath: URL.createObjectURL(file)
    };
    
    return photo;
  }, []);

  return {
    isNative,
    hasPermissions,
    isLoading,
    galleryPhotos,
    requestPermissions,
    takePhotoSeamlessly,
    pickFromGallerySeamlessly,
    handleWebFileInput
  };
}

// Helper function to convert blob to base64
function blobToBase64(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const base64 = (reader.result as string).split(',')[1];
      resolve(base64);
    };
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
}
