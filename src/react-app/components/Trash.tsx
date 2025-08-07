import { useState, useEffect } from 'react';
import { 
  Trash2, 
  RotateCcw, 
  AlertTriangle, 
  Check,
  X,
  Search,
  Grid3X3,
  List
} from 'lucide-react';

interface Photo {
  id: number;
  filename: string;
  file_path: string;
  category?: string;
  updated_at: string;
}

interface TrashProps {
  onPhotoRestore?: (photoId: number) => void;
  onPhotoDelete?: (photoId: number) => void;
}

export default function Trash({ onPhotoRestore, onPhotoDelete }: TrashProps) {
  const [photos, setPhotos] = useState<Photo[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedPhotos, setSelectedPhotos] = useState<Set<number>>(new Set());
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [searchTerm, setSearchTerm] = useState('');
  const [showEmptyConfirm, setShowEmptyConfirm] = useState(false);

  // Load trashed photos
  useEffect(() => {
    loadTrashPhotos();
  }, []);

  const loadTrashPhotos = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/trash');
      const data = await response.json();
      setPhotos(data.photos || []);
    } catch (error) {
      console.error('Error loading trash:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleRestore = async (photoId: number) => {
    try {
      const response = await fetch('/api/photos/restore', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ photoId })
      });

      if (response.ok) {
        setPhotos(prev => prev.filter(photo => photo.id !== photoId));
        setSelectedPhotos(prev => {
          const newSet = new Set(prev);
          newSet.delete(photoId);
          return newSet;
        });
        onPhotoRestore?.(photoId);
      }
    } catch (error) {
      console.error('Error restoring photo:', error);
    }
  };

  const handlePermanentDelete = async (photoId: number) => {
    try {
      const response = await fetch(`/api/photos/${photoId}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        setPhotos(prev => prev.filter(photo => photo.id !== photoId));
        setSelectedPhotos(prev => {
          const newSet = new Set(prev);
          newSet.delete(photoId);
          return newSet;
        });
        onPhotoDelete?.(photoId);
      }
    } catch (error) {
      console.error('Error permanently deleting photo:', error);
    }
  };

  const handleEmptyTrash = async () => {
    try {
      const response = await fetch('/api/trash/empty', {
        method: 'POST'
      });

      if (response.ok) {
        setPhotos([]);
        setSelectedPhotos(new Set());
        setShowEmptyConfirm(false);
      }
    } catch (error) {
      console.error('Error emptying trash:', error);
    }
  };

  const handleBulkRestore = async () => {
    for (const photoId of selectedPhotos) {
      await handleRestore(photoId);
    }
  };

  const handleBulkDelete = async () => {
    for (const photoId of selectedPhotos) {
      await handlePermanentDelete(photoId);
    }
  };

  const togglePhotoSelection = (photoId: number) => {
    setSelectedPhotos(prev => {
      const newSet = new Set(prev);
      if (newSet.has(photoId)) {
        newSet.delete(photoId);
      } else {
        newSet.add(photoId);
      }
      return newSet;
    });
  };

  const selectAll = () => {
    setSelectedPhotos(new Set(filteredPhotos.map(photo => photo.id)));
  };

  const clearSelection = () => {
    setSelectedPhotos(new Set());
  };

  // Filter photos based on search term
  const filteredPhotos = photos.filter(photo =>
    photo.filename.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (photo.category && photo.category.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center space-y-4">
          <div className="w-16 h-16 border-4 border-purple-500 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-white/70">Loading trash...</p>
        </div>
      </div>
    );
  }

  if (photos.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-full">
        <div className="text-center space-y-6">
          <div className="w-32 h-32 bg-gradient-to-r from-gray-500 to-gray-600 rounded-full flex items-center justify-center mx-auto shadow-2xl">
            <Trash2 className="w-16 h-16 text-white gallery-icon-glow-red-large" />
          </div>
          <h2 className="text-3xl font-bold text-white">Trash is empty</h2>
          <p className="text-white/70 text-lg">Photos you delete will appear here</p>
        </div>
      </div>
    );
  }

  return (
    <div className="h-full overflow-y-auto pb-24 sm:pb-8 scroll-container">
      <div className="max-w-7xl mx-auto p-4 sm:p-6 space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between space-y-4 sm:space-y-0">
          <div>
            <h1 className="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-red-400 to-orange-400 bg-clip-text text-transparent">
              Trash Bin
            </h1>
            <p className="text-white/70 text-sm sm:text-base mt-1">
              {photos.length} deleted photos
            </p>
          </div>

          {/* Controls */}
          <div className="flex items-center space-x-3">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-white/50" />
              <input
                type="text"
                placeholder="Search trash..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 pr-4 py-2 bg-white/10 border border-white/20 rounded-xl text-white placeholder:text-white/50 focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-transparent w-48"
              />
            </div>

            {/* View Mode Toggle */}
            <div className="flex items-center bg-white/10 border border-white/20 rounded-xl p-1">
              <button
                onClick={() => setViewMode('grid')}
                className={`p-2 rounded-lg transition-all duration-200 ${
                  viewMode === 'grid'
                    ? 'bg-red-500 text-white'
                    : 'text-white/60 hover:text-white hover:bg-white/10'
                }`}
              >
                <Grid3X3 className="w-4 h-4" />
              </button>
              <button
                onClick={() => setViewMode('list')}
                className={`p-2 rounded-lg transition-all duration-200 ${
                  viewMode === 'list'
                    ? 'bg-red-500 text-white'
                    : 'text-white/60 hover:text-white hover:bg-white/10'
                }`}
              >
                <List className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Action Bar */}
        {selectedPhotos.size > 0 && (
          <div className="bg-white/10 backdrop-blur-md rounded-xl border border-white/20 p-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <span className="text-white font-medium">
                  {selectedPhotos.size} selected
                </span>
                <button
                  onClick={selectAll}
                  className="text-blue-400 hover:text-blue-300 text-sm"
                >
                  Select all
                </button>
                <button
                  onClick={clearSelection}
                  className="text-white/60 hover:text-white text-sm"
                >
                  Clear
                </button>
              </div>
              
              <div className="flex items-center space-x-3">
                <button
                  onClick={handleBulkRestore}
                  className="flex items-center space-x-2 px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded-lg transition-colors"
                >
                  <RotateCcw className="w-4 h-4" />
                  <span>Restore</span>
                </button>
                <button
                  onClick={handleBulkDelete}
                  className="flex items-center space-x-2 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors"
                >
                  <Trash2 className="w-4 h-4" />
                  <span>Delete Forever</span>
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Empty Trash Button */}
        <div className="flex justify-center">
          <button
            onClick={() => setShowEmptyConfirm(true)}
            className="flex items-center space-x-2 px-6 py-3 bg-red-500/20 hover:bg-red-500/30 border border-red-500/50 text-red-300 rounded-xl transition-colors"
          >
            <AlertTriangle className="w-5 h-5" />
            <span>Empty Trash</span>
          </button>
        </div>

        {/* Photo Grid */}
        <div className={`grid gap-4 ${
          viewMode === 'grid' 
            ? 'grid-cols-4 sm:grid-cols-5 md:grid-cols-6 lg:grid-cols-7 xl:grid-cols-8' 
            : 'grid-cols-1 sm:grid-cols-2 gap-6'
        }`}>
          {filteredPhotos.map((photo) => (
            <div
              key={photo.id}
              className={`group relative overflow-hidden rounded-xl transition-all duration-200 hover:scale-105 cursor-pointer ${
                viewMode === 'grid' 
                  ? 'aspect-square' 
                  : 'aspect-video sm:aspect-[3/2]'
              } ${
                selectedPhotos.has(photo.id) ? 'ring-2 ring-red-500' : ''
              }`}
              onClick={() => togglePhotoSelection(photo.id)}
            >
              <img
                src={photo.file_path || `https://picsum.photos/400/400?random=${photo.id}`}
                alt={photo.filename}
                className="w-full h-full object-cover"
                loading="lazy"
              />
              
              {/* Overlay */}
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                <div className="absolute bottom-2 left-2 right-2">
                  <p className="text-white text-sm font-medium truncate drop-shadow-lg">
                    {photo.filename}
                  </p>
                  <p className="text-white/70 text-xs truncate">
                    Deleted {formatDate(photo.updated_at)}
                  </p>
                </div>
              </div>

              {/* Selection indicator */}
              <div className={`absolute top-2 right-2 w-6 h-6 rounded-full flex items-center justify-center transition-all duration-200 ${
                selectedPhotos.has(photo.id)
                  ? 'bg-red-500 text-white'
                  : 'bg-white/20 backdrop-blur-sm opacity-0 group-hover:opacity-100'
              }`}>
                {selectedPhotos.has(photo.id) ? (
                  <Check className="w-4 h-4" />
                ) : (
                  <div className="w-3 h-3 border-2 border-white rounded-full" />
                )}
              </div>

              {/* Action buttons */}
              <div className="absolute top-2 left-2 flex space-x-2 opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRestore(photo.id);
                  }}
                  className="w-8 h-8 bg-green-500 hover:bg-green-600 text-white rounded-full flex items-center justify-center transition-colors"
                >
                  <RotateCcw className="w-4 h-4" />
                </button>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handlePermanentDelete(photo.id);
                  }}
                  className="w-8 h-8 bg-red-500 hover:bg-red-600 text-white rounded-full flex items-center justify-center transition-colors"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>

        {/* Empty Trash Confirmation Modal */}
        {showEmptyConfirm && (
          <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="bg-gray-900 border border-white/20 rounded-2xl p-6 max-w-md w-full">
              <div className="text-center space-y-4">
                <div className="w-16 h-16 bg-red-500/20 rounded-full flex items-center justify-center mx-auto">
                  <AlertTriangle className="w-8 h-8 text-red-400" />
                </div>
                <h3 className="text-xl font-bold text-white">Empty Trash?</h3>
                <p className="text-white/70">
                  This will permanently delete all {photos.length} photos in your trash. This action cannot be undone.
                </p>
                <div className="flex space-x-3">
                  <button
                    onClick={() => setShowEmptyConfirm(false)}
                    className="flex-1 px-4 py-2 bg-white/10 hover:bg-white/20 text-white rounded-lg transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleEmptyTrash}
                    className="flex-1 px-4 py-2 bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors"
                  >
                    Empty Trash
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
