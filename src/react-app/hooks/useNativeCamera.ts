import { useState, useCallback } from 'react';
import { Camera, CameraResultType, CameraSource } from '@capacitor/camera';
import { Filesystem, Directory } from '@capacitor/filesystem';
import { Capacitor } from '@capacitor/core';

interface Photo {
  id: number;
  filename: string;
  file_path: string;
  webPath?: string;
}

export function useNativeCamera() {
  const [isNative] = useState(() => Capacitor.isNativePlatform());
  
  const takePhoto = useCallback(async (): Promise<Photo | null> => {
    if (!isNative) {
      // Fallback for web - trigger file input
      return null;
    }

    try {
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

      // Generate filename
      const fileName = `photo_${Date.now()}.jpg`;
      
      // Read the image data
      const response = await fetch(image.webPath);
      const blob = await response.blob();
      const base64Data = await blobToBase64(blob);

      // Save to app's document directory
      await Filesystem.writeFile({
        path: fileName,
        data: base64Data,
        directory: Directory.Documents
      });

      // Get the file URI for display
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
  }, [isNative]);

  const pickPhoto = useCallback(async (): Promise<Photo | null> => {
    if (!isNative) {
      return null;
    }

    try {
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
  }, [isNative]);

  return {
    isNative,
    takePhoto,
    pickPhoto
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
