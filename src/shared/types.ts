// Shared type definitions for PhotoKik

export interface Photo {
  id: number;
  filename: string;
  file_path: string;
  file_size?: number;
  width?: number;
  height?: number;
  category?: 'memories' | 'documents' | 'duplicates' | 'blurry' | 'uncategorized';
  is_favorite?: boolean;
  is_deleted?: boolean;
  blur_score?: number;
  ai_tags?: string;
  created_at?: string;
  updated_at?: string;
}

export interface UserSettings {
  id?: number;
  user_id?: string;
  language: 'en' | 'es' | 'pt';
  swipe_sensitivity: number;
  auto_delete_blurry: boolean;
  auto_categorize: boolean;
  created_at?: string;
  updated_at?: string;
}

export interface SwipeAction {
  photoId: number;
  action: 'keep' | 'kik';
  timestamp: string;
}

export interface GalleryStats {
  total_photos: number;
  memories: number;
  documents: number;
  duplicates: number;
  blurry: number;
  favorites: number;
  storage_used: number;
}

export interface AppInfo {
  name: string;
  version: string;
  platform: 'web' | 'android' | 'ios';
  build: string;
}

export interface CameraPhoto {
  id: number;
  filename: string;
  file_path: string;
  webPath?: string;
  format?: string;
  saved?: boolean;
}

export interface PermissionStatus {
  camera: 'granted' | 'denied' | 'prompt';
  photos: 'granted' | 'denied' | 'prompt';
}

// Android-specific types
export interface AndroidConfig {
  allowMixedContent: boolean;
  captureInput: boolean;
  webContentsDebuggingEnabled: boolean;
  loggingBehavior: 'none' | 'debug' | 'production';
  buildOptions: {
    keystorePath?: string;
    keystorePassword?: string;
    keystoreAlias?: string;
    keystoreAliasPassword?: string;
    releaseType: 'APK' | 'AAB';
    signingType: 'apksigner' | 'jarsigner';
  };
}

export interface AndroidBuildInfo {
  versionCode: number;
  versionName: string;
  buildType: 'debug' | 'release';
  flavor?: string;
  applicationId: string;
  minSdk: number;
  targetSdk: number;
  compileSdk: number;
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

export interface PhotosApiResponse extends ApiResponse<{ photos: Photo[] }> {}
export interface SettingsApiResponse extends ApiResponse<{ settings: UserSettings }> {}
export interface StatsApiResponse extends ApiResponse<GalleryStats> {}

// Event types for Android callbacks
export interface AndroidEvent {
  type: 'back' | 'menu' | 'orientation' | 'memory' | 'battery';
  data?: any;
}

export interface HapticFeedback {
  type: 'light' | 'medium' | 'heavy' | 'selection' | 'success' | 'warning' | 'error';
  intensity?: number;
}

// Image processing types
export interface ImageMetadata {
  width: number;
  height: number;
  format: string;
  file_size: number;
  orientation?: number;
  location?: {
    latitude: number;
    longitude: number;
  };
  date_taken?: string;
}

export interface AIProcessingResult {
  category: Photo['category'];
  tags: string[];
  blur_score: number;
  confidence: number;
  is_duplicate?: boolean;
  similar_photos?: number[];
}

// Performance monitoring
export interface PerformanceMetrics {
  app_start_time: number;
  memory_usage: number;
  battery_level: number;
  network_type: string;
  device_info: {
    model: string;
    os_version: string;
    app_version: string;
  };
}
