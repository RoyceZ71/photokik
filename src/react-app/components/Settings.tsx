import { useState, useEffect } from 'react';
import { useLanguage } from '../contexts/LanguageContext';
import { useNativeFeatures } from '../hooks/useNativeFeatures';
import { 
  Globe, 
  Sliders, 
  ImageIcon, 
  Trash2, 
  HardDrive, 
  Zap, 
  Info, 
  MessageCircle,
  ChevronRight,
  Check
} from 'lucide-react';

interface UserSettings {
  language: 'en' | 'es' | 'pt';
  swipe_sensitivity: number;
  auto_delete_blurry: boolean;
  auto_categorize: boolean;
  optimize_storage: boolean;
  system_optimization: boolean;
}

export default function Settings() {
  const { language, setLanguage, t } = useLanguage();
  const { isNative, appInfo, triggerHaptic } = useNativeFeatures();
  const [settings, setSettings] = useState<UserSettings>({
    language: 'en',
    swipe_sensitivity: 0.5,
    auto_delete_blurry: false,
    auto_categorize: true,
    optimize_storage: false,
    system_optimization: true,
  });

  // Load settings from localStorage on mount
  useEffect(() => {
    const saved = localStorage.getItem('photoKikSettings');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        setSettings(prev => ({ ...prev, ...parsed }));
      } catch (error) {
        console.error('Failed to parse saved settings:', error);
      }
    }
  }, []);

  // Save settings to localStorage when changed
  useEffect(() => {
    localStorage.setItem('photoKikSettings', JSON.stringify(settings));
  }, [settings]);

  const updateSetting = <K extends keyof UserSettings>(key: K, value: UserSettings[K]) => {
    setSettings(prev => ({ ...prev, [key]: value }));
    
    // Update language context when language changes
    if (key === 'language') {
      setLanguage(value as 'en' | 'es' | 'pt');
    }
  };

  const getSensitivityLabel = (value: number) => {
    if (value <= 0.3) return t('low');
    if (value <= 0.7) return t('medium');
    return t('high');
  };

  return (
    <div className="max-w-6xl mx-auto p-6 sm:p-8 space-y-8 sm:space-y-10 pb-24 sm:pb-8">
      {/* Header */}
      <div className="text-center space-y-2">
        <h1 className="text-2xl sm:text-3xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
          {t('settings')}
        </h1>
        <p className="text-white/70 text-sm sm:text-base">
          Customize your PhotoKik experience
        </p>
      </div>

      {/* Language Settings */}
      <div className="bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 p-4 sm:p-6">
        <div className="flex items-center space-x-3 mb-4">
          <Globe className="w-5 h-5 sm:w-6 sm:h-6 text-purple-400" />
          <h2 className="text-lg sm:text-xl font-semibold text-white">{t('language')}</h2>
        </div>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[
            { code: 'en' as const, name: t('english'), flag: '🇺🇸' },
            { code: 'es' as const, name: t('spanish'), flag: '🇪🇸' },
            { code: 'pt' as const, name: t('portuguese'), flag: '🇧🇷' },
          ].map((lang) => (
            <button
              key={lang.code}
              onClick={() => {
                triggerHaptic();
                updateSetting('language', lang.code);
              }}
              className={`p-4 rounded-xl transition-all duration-200 flex items-center justify-between active:scale-95 ${
                language === lang.code
                  ? 'bg-purple-500 text-white shadow-lg'
                  : 'bg-white/10 text-white/80 hover:bg-white/20 active:bg-white/15'
              }`}
            >
              <div className="flex items-center space-x-3">
                <span className="text-xl sm:text-2xl">{lang.flag}</span>
                <span className="font-medium text-sm sm:text-base">{lang.name}</span>
              </div>
              {language === lang.code && (
                <Check className="w-4 h-4 sm:w-5 sm:h-5" />
              )}
            </button>
          ))}
        </div>
      </div>

      {/* Swipe Sensitivity */}
      <div className="bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 p-4 sm:p-6">
        <div className="flex items-center space-x-3 mb-4">
          <Sliders className="w-5 h-5 sm:w-6 sm:h-6 text-purple-400" />
          <h2 className="text-lg sm:text-xl font-semibold text-white">{t('swipeSensitivity')}</h2>
        </div>
        
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-white/80 text-sm sm:text-base">{getSensitivityLabel(settings.swipe_sensitivity)}</span>
            <span className="text-purple-400 font-medium text-sm sm:text-base">
              {Math.round(settings.swipe_sensitivity * 100)}%
            </span>
          </div>
          
          <input
            type="range"
            min="0.1"
            max="1.0"
            step="0.1"
            value={settings.swipe_sensitivity}
            onChange={(e) => updateSetting('swipe_sensitivity', parseFloat(e.target.value))}
            className="w-full h-4 sm:h-3 bg-white/20 rounded-lg appearance-none cursor-pointer slider touch-manipulation"
            style={{
              background: `linear-gradient(to right, rgb(168 85 247) 0%, rgb(168 85 247) ${settings.swipe_sensitivity * 100}%, rgba(255,255,255,0.2) ${settings.swipe_sensitivity * 100}%, rgba(255,255,255,0.2) 100%)`
            }}
          />
          
          <div className="flex justify-between text-xs sm:text-sm text-white/60">
            <span>{t('low')}</span>
            <span>{t('medium')}</span>
            <span>{t('high')}</span>
          </div>
        </div>
      </div>

      {/* Photo Management */}
      <div className="bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 p-4 sm:p-6">
        <div className="flex items-center space-x-3 mb-4 sm:mb-6">
          <ImageIcon className="w-5 h-5 sm:w-6 sm:h-6 text-purple-400" />
          <h2 className="text-lg sm:text-xl font-semibold text-white">{t('photoManagement')}</h2>
        </div>
        
        <div className="space-y-3 sm:space-y-4">
          {/* Auto-delete blurry photos */}
          <div className="flex items-center justify-between p-3 sm:p-4 rounded-xl bg-white/5">
            <div className="flex items-center space-x-3 sm:space-x-4 flex-1 min-w-0">
              <div className="w-8 h-8 sm:w-10 sm:h-10 bg-red-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <Trash2 className="w-4 h-4 sm:w-5 sm:h-5 text-red-400" />
              </div>
              <div className="min-w-0 flex-1">
                <h3 className="font-medium text-white text-sm sm:text-base">{t('autoDeleteBlurry')}</h3>
                <p className="text-xs sm:text-sm text-white/60 leading-tight">{t('autoDeleteBlurryDesc')}</p>
              </div>
            </div>
            
            <button
              onClick={() => updateSetting('auto_delete_blurry', !settings.auto_delete_blurry)}
              className={`relative w-12 h-6 rounded-full transition-colors duration-200 flex-shrink-0 ${
                settings.auto_delete_blurry ? 'bg-purple-500' : 'bg-white/20'
              }`}
            >
              <div
                className={`absolute w-5 h-5 bg-white rounded-full top-0.5 transition-transform duration-200 ${
                  settings.auto_delete_blurry ? 'translate-x-6' : 'translate-x-0.5'
                }`}
              />
            </button>
          </div>

          {/* Auto-categorize photos */}
          <div className="flex items-center justify-between p-3 sm:p-4 rounded-xl bg-white/5">
            <div className="flex items-center space-x-3 sm:space-x-4 flex-1 min-w-0">
              <div className="w-8 h-8 sm:w-10 sm:h-10 bg-blue-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <ImageIcon className="w-4 h-4 sm:w-5 sm:h-5 text-blue-400" />
              </div>
              <div className="min-w-0 flex-1">
                <h3 className="font-medium text-white text-sm sm:text-base">{t('autoCategorize')}</h3>
                <p className="text-xs sm:text-sm text-white/60 leading-tight">{t('autoCategorizeDesc')}</p>
              </div>
            </div>
            
            <button
              onClick={() => updateSetting('auto_categorize', !settings.auto_categorize)}
              className={`relative w-12 h-6 rounded-full transition-colors duration-200 flex-shrink-0 ${
                settings.auto_categorize ? 'bg-purple-500' : 'bg-white/20'
              }`}
            >
              <div
                className={`absolute w-5 h-5 bg-white rounded-full top-0.5 transition-transform duration-200 ${
                  settings.auto_categorize ? 'translate-x-6' : 'translate-x-0.5'
                }`}
              />
            </button>
          </div>
        </div>
      </div>

      {/* Storage & Performance */}
      <div className="bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 p-4 sm:p-6">
        <div className="flex items-center space-x-3 mb-4 sm:mb-6">
          <HardDrive className="w-5 h-5 sm:w-6 sm:h-6 text-purple-400" />
          <h2 className="text-lg sm:text-xl font-semibold text-white">Storage & Performance</h2>
        </div>
        
        <div className="space-y-3 sm:space-y-4">
          {/* Optimize storage */}
          <div className="flex items-center justify-between p-3 sm:p-4 rounded-xl bg-white/5">
            <div className="flex items-center space-x-3 sm:space-x-4 flex-1 min-w-0">
              <div className="w-8 h-8 sm:w-10 sm:h-10 bg-green-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <HardDrive className="w-4 h-4 sm:w-5 sm:h-5 text-green-400" />
              </div>
              <div className="min-w-0 flex-1">
                <h3 className="font-medium text-white text-sm sm:text-base">Optimize storage</h3>
                <p className="text-xs sm:text-sm text-white/60 leading-tight">Automatically compress photos to save space</p>
              </div>
            </div>
            
            <button
              onClick={() => updateSetting('optimize_storage', !settings.optimize_storage)}
              className={`relative w-12 h-6 rounded-full transition-colors duration-200 flex-shrink-0 ${
                settings.optimize_storage ? 'bg-purple-500' : 'bg-white/20'
              }`}
            >
              <div
                className={`absolute w-5 h-5 bg-white rounded-full top-0.5 transition-transform duration-200 ${
                  settings.optimize_storage ? 'translate-x-6' : 'translate-x-0.5'
                }`}
              />
            </button>
          </div>

          {/* System optimization */}
          <div className="flex items-center justify-between p-3 sm:p-4 rounded-xl bg-white/5">
            <div className="flex items-center space-x-3 sm:space-x-4 flex-1 min-w-0">
              <div className="w-8 h-8 sm:w-10 sm:h-10 bg-yellow-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <Zap className="w-4 h-4 sm:w-5 sm:h-5 text-yellow-400" />
              </div>
              <div className="min-w-0 flex-1">
                <h3 className="font-medium text-white text-sm sm:text-base">System optimization</h3>
                <p className="text-xs sm:text-sm text-white/60 leading-tight">Optimize app performance and memory usage</p>
              </div>
            </div>
            
            <button
              onClick={() => updateSetting('system_optimization', !settings.system_optimization)}
              className={`relative w-12 h-6 rounded-full transition-colors duration-200 flex-shrink-0 ${
                settings.system_optimization ? 'bg-purple-500' : 'bg-white/20'
              }`}
            >
              <div
                className={`absolute w-5 h-5 bg-white rounded-full top-0.5 transition-transform duration-200 ${
                  settings.system_optimization ? 'translate-x-6' : 'translate-x-0.5'
                }`}
              />
            </button>
          </div>
        </div>
      </div>

      {/* About & Support */}
      <div className="bg-white/10 backdrop-blur-md rounded-2xl border border-white/20 p-4 sm:p-6">
        <div className="flex items-center space-x-3 mb-4 sm:mb-6">
          <Info className="w-5 h-5 sm:w-6 sm:h-6 text-purple-400" />
          <h2 className="text-lg sm:text-xl font-semibold text-white">{t('about')}</h2>
        </div>
        
        <div className="space-y-3">
          <div className="flex items-center justify-between p-3 sm:p-4 rounded-xl bg-white/5 hover:bg-white/10 transition-colors cursor-pointer active:scale-95">
            <div className="flex items-center space-x-3 sm:space-x-4 flex-1 min-w-0">
              <div className="w-8 h-8 sm:w-10 sm:h-10 bg-purple-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <Info className="w-4 h-4 sm:w-5 sm:h-5 text-purple-400" />
              </div>
              <div className="min-w-0 flex-1">
                <h3 className="font-medium text-white text-sm sm:text-base">{t('version')}</h3>
                <p className="text-xs sm:text-sm text-white/60">
                  PhotoKik {appInfo?.version || 'v1.0.0'} {isNative ? '(Native)' : '(Web)'}
                </p>
              </div>
            </div>
            <ChevronRight className="w-4 h-4 sm:w-5 sm:h-5 text-white/40 flex-shrink-0" />
          </div>

          <div className="flex items-center justify-between p-3 sm:p-4 rounded-xl bg-white/5 hover:bg-white/10 transition-colors cursor-pointer active:scale-95">
            <div className="flex items-center space-x-3 sm:space-x-4 flex-1 min-w-0">
              <div className="w-8 h-8 sm:w-10 sm:h-10 bg-blue-500/20 rounded-lg flex items-center justify-center flex-shrink-0">
                <MessageCircle className="w-4 h-4 sm:w-5 sm:h-5 text-blue-400" />
              </div>
              <div className="min-w-0 flex-1">
                <h3 className="font-medium text-white text-sm sm:text-base">{t('support')}</h3>
                <p className="text-xs sm:text-sm text-white/60">Get help and send feedback</p>
              </div>
            </div>
            <ChevronRight className="w-4 h-4 sm:w-5 sm:h-5 text-white/40 flex-shrink-0" />
          </div>
        </div>
      </div>
    </div>
  );
}
