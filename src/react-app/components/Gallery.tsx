import { useState } from 'react';
import { 
  ImageIcon, 
  FileText, 
  Copy, 
  EyeOff, 
  Search,
  Grid3X3,
  List,
  ChevronDown,
  ChevronUp
} from 'lucide-react';
import { useLanguage } from '../contexts/LanguageContext';

interface Photo {
  id: number;
  filename: string;
  file_path: string;
  category?: string;
}

interface GalleryProps {
  photos: Photo[];
}

export default function Gallery({ photos }: GalleryProps) {
  const { t } = useLanguage();
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [expandedCategory, setExpandedCategory] = useState<string | null>('memories');

  // Categorize photos
  const categorizedPhotos = {
    memories: photos.filter(photo => photo.category === 'memories'),
    documents: photos.filter(photo => photo.category === 'documents'),
    duplicates: photos.filter(photo => photo.category === 'duplicates'),
    blurry: photos.filter(photo => photo.category === 'blurry')
  };

  // Filter photos based on search term
  const filteredPhotos = photos.filter(photo =>
    photo.filename.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (photo.category && photo.category.toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'memories':
        return <ImageIcon className="w-5 h-5 text-purple-400 gallery-icon-glow-purple" />;
      case 'documents':
        return <FileText className="w-5 h-5 text-blue-400 gallery-icon-glow-blue" />;
      case 'duplicates':
        return <Copy className="w-5 h-5 text-yellow-400 gallery-icon-glow-yellow" />;
      case 'blurry':
        return <EyeOff className="w-5 h-5 text-red-400 gallery-icon-glow-red" />;
      default:
        return <ImageIcon className="w-5 h-5 text-gray-400 gallery-icon-glow-gray" />;
    }
  };

  const getCategoryColor = (category: string) => {
    switch (category) {
      case 'memories':
        return 'from-purple-500 to-pink-500';
      case 'documents':
        return 'from-blue-500 to-cyan-500';
      case 'duplicates':
        return 'from-yellow-500 to-orange-500';
      case 'blurry':
        return 'from-red-500 to-rose-500';
      default:
        return 'from-gray-500 to-gray-600';
    }
  };

  const renderCategorySection = (category: string, categoryPhotos: Photo[], title: string, description: string) => {
    const isExpanded = expandedCategory === category;
    const displayPhotos = isExpanded ? categoryPhotos : categoryPhotos.slice(0, 6);

    if (categoryPhotos.length === 0) return null;

    return (
      <div key={category} className="bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 overflow-hidden">
        <div className="p-4 sm:p-6">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center space-x-3">
              {getCategoryIcon(category)}
              <div>
                <h3 className="text-lg sm:text-xl font-semibold text-white">{title}</h3>
                <p className="text-white/60 text-sm">{description}</p>
              </div>
            </div>
            <div className="flex items-center space-x-2">
              <span className="text-white/70 text-sm font-medium">
                {categoryPhotos.length} {t('photosOrganized')}
              </span>
              {categoryPhotos.length > 6 && (
                <button
                  onClick={() => setExpandedCategory(isExpanded ? null : category)}
                  className="p-2 rounded-lg text-white/60 hover:text-white hover:bg-white/10 transition-all"
                >
                  {isExpanded ? <ChevronUp className="w-4 h-4 gallery-icon-glow-white" /> : <ChevronDown className="w-4 h-4 gallery-icon-glow-white" />}
                </button>
              )}
            </div>
          </div>

          <div className={`grid gap-4 ${
            viewMode === 'grid' 
              ? 'grid-cols-4 sm:grid-cols-5 md:grid-cols-6 lg:grid-cols-7 xl:grid-cols-8' 
              : 'grid-cols-1 sm:grid-cols-2 gap-6'
          }`}>
            {displayPhotos.map((photo) => (
              <div
                key={photo.id}
                className={`group relative overflow-hidden rounded-xl transition-all duration-200 hover:scale-105 cursor-pointer ${
                  viewMode === 'grid' ? 'aspect-square' : 'aspect-video sm:aspect-[3/2]'
                }`}
              >
                <img
                  src={photo.file_path || `https://picsum.photos/400/400?random=${photo.id}`}
                  alt={photo.filename}
                  className="w-full h-full object-cover"
                  loading="lazy"
                />
                
                {/* Gradient overlay */}
                <div className={`absolute inset-0 bg-gradient-to-t ${getCategoryColor(category)} opacity-0 group-hover:opacity-20 transition-opacity duration-200`} />
                
                {/* Info overlay */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                  <div className="absolute bottom-2 left-2 right-2">
                    <p className="text-white text-sm font-medium truncate drop-shadow-lg">
                      {photo.filename}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {categoryPhotos.length > 6 && !isExpanded && (
            <div className="mt-4 text-center">
              <button
                onClick={() => setExpandedCategory(category)}
                className="px-6 py-2 bg-white/10 hover:bg-white/20 text-white/80 hover:text-white rounded-lg transition-all duration-200 text-sm font-medium"
              >
                {t('viewAll')} ({categoryPhotos.length - 6} more)
              </button>
            </div>
          )}

          {isExpanded && categoryPhotos.length > 6 && (
            <div className="mt-4 text-center">
              <button
                onClick={() => setExpandedCategory(null)}
                className="px-6 py-2 bg-white/10 hover:bg-white/20 text-white/80 hover:text-white rounded-lg transition-all duration-200 text-sm font-medium"
              >
                {t('collapse')}
              </button>
            </div>
          )}
        </div>
      </div>
    );
  };

  if (photos.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-full">
        <div className="text-center space-y-6">
          <div className="w-32 h-32 bg-gradient-to-r from-purple-500 to-pink-500 rounded-full flex items-center justify-center mx-auto shadow-2xl">
            <ImageIcon className="w-16 h-16 text-white gallery-icon-glow-large" />
          </div>
          <h2 className="text-3xl font-bold text-white">{t('noPhotos')}</h2>
          <p className="text-white/70 text-lg">{t('noPhotosDesc')}</p>
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
            <h1 className="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
              {t('gallery')}
            </h1>
            <p className="text-white/70 text-sm sm:text-base mt-1">
              {photos.length} photos organized by AI
            </p>
          </div>

          {/* Controls */}
          <div className="flex items-center space-x-3">
            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-white/50 gallery-icon-glow-white" />
              <input
                type="text"
                placeholder={t('searchPhotos')}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10 pr-4 py-2 bg-white/10 border border-white/20 rounded-xl text-white placeholder:text-white/50 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent w-48"
              />
            </div>

            {/* View Mode Toggle */}
            <div className="flex items-center bg-white/10 border border-white/20 rounded-xl p-1">
              <button
                onClick={() => setViewMode('grid')}
                className={`p-2 rounded-lg transition-all duration-200 ${
                  viewMode === 'grid'
                    ? 'bg-purple-500 text-white'
                    : 'text-white/60 hover:text-white hover:bg-white/10'
                }`}
              >
                <Grid3X3 className="w-4 h-4 gallery-icon-glow-grid" />
              </button>
              <button
                onClick={() => setViewMode('list')}
                className={`p-2 rounded-lg transition-all duration-200 ${
                  viewMode === 'list'
                    ? 'bg-purple-500 text-white'
                    : 'text-white/60 hover:text-white hover:bg-white/10'
                }`}
              >
                <List className="w-4 h-4 gallery-icon-glow-list" />
              </button>
            </div>
          </div>
        </div>

        {/* Search Results or Categories */}
        {searchTerm ? (
          <div className="space-y-4">
            <h2 className="text-xl font-semibold text-white">
              Search Results ({filteredPhotos.length})
            </h2>
            <div className={`grid gap-4 ${
              viewMode === 'grid' 
                ? 'grid-cols-4 sm:grid-cols-5 md:grid-cols-6 lg:grid-cols-7 xl:grid-cols-8' 
                : 'grid-cols-1 sm:grid-cols-2 gap-6'
            }`}>
              {filteredPhotos.map((photo) => (
                <div
                  key={photo.id}
                  className={`group relative overflow-hidden rounded-xl transition-all duration-200 hover:scale-105 cursor-pointer ${
                    viewMode === 'grid' ? 'aspect-square' : 'aspect-video sm:aspect-[3/2]'
                  }`}
                >
                  <img
                    src={photo.file_path || `https://picsum.photos/400/400?random=${photo.id}`}
                    alt={photo.filename}
                    className="w-full h-full object-cover"
                    loading="lazy"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-200">
                    <div className="absolute bottom-2 left-2 right-2">
                      <p className="text-white text-sm font-medium truncate drop-shadow-lg">
                        {photo.filename}
                      </p>
                      <p className="text-white/70 text-xs capitalize">
                        {photo.category}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            {renderCategorySection('memories', categorizedPhotos.memories, t('memories'), t('memoriesDesc'))}
            {renderCategorySection('documents', categorizedPhotos.documents, t('documents'), t('documentsDesc'))}
            {renderCategorySection('duplicates', categorizedPhotos.duplicates, t('duplicates'), t('duplicatesDesc'))}
            {renderCategorySection('blurry', categorizedPhotos.blurry, t('blurry'), t('blurryDesc'))}
          </div>
        )}
      </div>
    </div>
  );
}
