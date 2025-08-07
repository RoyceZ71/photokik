import { Settings, Trash2, FolderOpen, Camera, Maximize2 } from 'lucide-react';
import { useLanguage } from '../contexts/LanguageContext';

interface HeaderProps {
  currentView: string;
  onViewChange: (view: string) => void;
  onToggleFullScreen?: () => void;
}

export default function Header({ currentView, onViewChange, onToggleFullScreen }: HeaderProps) {
  const { t } = useLanguage();

  return (
    <header className="w-full bg-black/90 backdrop-blur-md border-b border-white/10">
      {/* Mobile Header */}
      <div className="sm:hidden px-3 py-2">
        {/* Centered Logo */}
        <div className="flex justify-center items-center mb-2">
          <div className="flex items-center space-x-2">
            <div className="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center shadow-lg photokik-logo-glow">
              <Camera className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent photokik-text-glow">
                PhotoKik
              </h1>
              <p className="text-sm text-white/60">Smart photo management</p>
            </div>
          </div>
        </div>

        

        
      </div>

      {/* Desktop Header */}
      <div className="hidden sm:block p-6">
        <div className="grid grid-cols-3 items-center max-w-8xl mx-auto">
          {/* Navigation - Left */}
          <nav className="flex items-center space-x-4 justify-start">
            <button
              onClick={() => onViewChange('swipe')}
              className={`px-4 py-2 rounded-lg transition-all duration-200 flex items-center space-x-2 ${
                currentView === 'swipe'
                  ? 'bg-purple-500 text-white shadow-lg'
                  : 'text-white/70 hover:text-white hover:bg-white/10'
              }`}
            >
              <Camera size={20} className={`translate-y-0.5 nav-icon-glow ${currentView === 'swipe' ? 'nav-icon-selected' : ''}`} />
              <span>{t('swipe')}</span>
            </button>
            <button
              onClick={() => onViewChange('gallery')}
              className={`px-4 py-2 rounded-lg transition-all duration-200 flex items-center space-x-2 ${
                currentView === 'gallery'
                  ? 'bg-purple-500 text-white shadow-lg'
                  : 'text-white/70 hover:text-white hover:bg-white/10'
              }`}
            >
              <FolderOpen size={16} className={`nav-icon-gallery ${currentView === 'gallery' ? 'nav-icon-selected' : ''}`} />
              <span>{t('gallery')}</span>
            </button>
            <button
              onClick={() => onViewChange('trash')}
              className={`px-4 py-2 rounded-lg transition-all duration-200 flex items-center space-x-2 ${
                currentView === 'trash'
                  ? 'bg-purple-500 text-white shadow-lg'
                  : 'text-white/70 hover:text-white hover:bg-white/10'
              }`}
            >
              <Trash2 size={16} className={`nav-icon-trash ${currentView === 'trash' ? 'nav-icon-selected' : ''}`} />
              <span>{t('trash')}</span>
            </button>
          </nav>

          {/* Centered Logo */}
          <div className="flex items-center justify-center space-x-3">
            <div className="w-10 h-10 bg-gradient-to-r from-purple-500 to-pink-500 rounded-lg flex items-center justify-center shadow-lg photokik-logo-glow">
              <Camera className="w-5 h-5 text-white" />
            </div>
            <h1 className="text-xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent photokik-text-glow">
              PhotoKik
            </h1>
          </div>

          {/* Settings - Right */}
          <div className="flex items-center space-x-4 justify-end">

            {onToggleFullScreen && (
              <button
                onClick={onToggleFullScreen}
                className="w-10 h-10 rounded-lg transition-all duration-200 flex items-center justify-center text-white/70 hover:text-white hover:bg-white/10"
                title="Full Screen Mode"
              >
                <Maximize2 size={20} />
              </button>
            )}
            
            <button
              onClick={() => onViewChange('settings')}
              className={`w-10 h-10 rounded-lg transition-all duration-200 flex items-center justify-center ${
                currentView === 'settings'
                  ? 'bg-purple-500 text-white'
                  : 'text-white/70 hover:text-white hover:bg-white/10'
              }`}
            >
              <Settings size={20} className={`nav-icon-settings ${currentView === 'settings' ? 'nav-icon-selected' : ''}`} />
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Bottom Navigation */}
      <div className="sm:hidden fixed bottom-0 left-0 right-0 bg-black/95 backdrop-blur-md border-t border-white/10 p-2 z-50">
        <div className="flex justify-between items-center">
          <div className="flex justify-center space-x-4 flex-1">
            <button
              onClick={() => onViewChange('swipe')}
              className={`flex flex-col items-center space-y-1 p-2 rounded-lg transition-all duration-200 ${
                currentView === 'swipe'
                  ? 'text-purple-400'
                  : 'text-white/60'
              }`}
            >
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                currentView === 'swipe' ? 'bg-purple-500/20' : 'bg-transparent'
              }`}>
                <Camera size={20} className={`nav-icon-glow ${currentView === 'swipe' ? 'nav-icon-selected' : ''}`} />
              </div>
              <span className="text-xs font-medium">{t('swipe')}</span>
            </button>
            
            <button
              onClick={() => onViewChange('gallery')}
              className={`flex flex-col items-center space-y-1 p-2 rounded-lg transition-all duration-200 ${
                currentView === 'gallery'
                  ? 'text-purple-400'
                  : 'text-white/60'
              }`}
            >
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                currentView === 'gallery' ? 'bg-purple-500/20' : 'bg-transparent'
              }`}>
                <FolderOpen size={20} className={`nav-icon-gallery ${currentView === 'gallery' ? 'nav-icon-selected' : ''}`} />
              </div>
              <span className="text-xs font-medium">{t('gallery')}</span>
            </button>

            <button
              onClick={() => onViewChange('trash')}
              className={`flex flex-col items-center space-y-1 p-2 rounded-lg transition-all duration-200 ${
                currentView === 'trash'
                  ? 'text-purple-400'
                  : 'text-white/60'
              }`}
            >
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                currentView === 'trash' ? 'bg-purple-500/20' : 'bg-transparent'
              }`}>
                <Trash2 size={20} className={`nav-icon-trash ${currentView === 'trash' ? 'nav-icon-selected' : ''}`} />
              </div>
              <span className="text-xs font-medium">{t('trash')}</span>
            </button>

            <button
              onClick={() => onViewChange('settings')}
              className={`flex flex-col items-center space-y-1 p-2 rounded-lg transition-all duration-200 ${
                currentView === 'settings'
                  ? 'text-purple-400'
                  : 'text-white/60'
              }`}
            >
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                currentView === 'settings' ? 'bg-purple-500/20' : 'bg-transparent'
              }`}>
                <Settings size={20} className={`nav-icon-settings ${currentView === 'settings' ? 'nav-icon-selected' : ''}`} />
              </div>
              <span className="text-xs font-medium">{t('settings')}</span>
            </button>
          </div>

          
        </div>
      </div>
    </header>
  );
}
