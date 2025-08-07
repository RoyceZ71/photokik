import React, { createContext, useContext, useState, useEffect } from 'react';

// Translation definitions
const translations = {
  en: {
    // Header
    swipe: 'Swipe',
    gallery: 'Gallery',
    trash: 'Trash',
    storage: 'Storage',
    
    // Settings Page
    settings: 'Settings',
    language: 'Language',
    english: 'English',
    spanish: 'Spanish',
    portuguese: 'Portuguese',
    swipeSensitivity: 'Swipe Sensitivity',
    low: 'Low',
    medium: 'Medium',
    high: 'High',
    photoManagement: 'Photo Management',
    autoDeleteBlurry: 'Auto-delete blurry photos',
    autoDeleteBlurryDesc: 'Automatically move blurry photos to trash',
    autoCategorize: 'Auto-categorize photos',
    autoCategorizeDesc: 'Use AI to automatically sort photos into categories',
    storageAndPerformance: 'Storage & Performance',
    optimizeStorage: 'Optimize storage',
    optimizeStorageDesc: 'Automatically compress photos to save space',
    systemOptimization: 'System optimization',
    systemOptimizationDesc: 'Optimize app performance and memory usage',
    about: 'About',
    version: 'Version',
    support: 'Support & Feedback',
    
    // Swipe Interface
    keep: 'KEEP',
    kik: 'KIK',
    allDone: 'All done!',
    allDoneDesc: "You've reviewed all your photos.",
    reviewedPhotos: 'photos reviewed',
    
    // Coming Soon
    comingSoon: 'Coming soon in the next phase!',
    
    // Gallery
    memories: 'Memories',
    documents: 'Documents',
    duplicates: 'Duplicates',
    blurry: 'Blurry Photos',
    memoriesDesc: 'Your precious moments and experiences',
    documentsDesc: 'Important papers and receipts',
    duplicatesDesc: 'Photos that appear multiple times',
    blurryDesc: 'Photos that need attention or deletion',
    noPhotos: 'No photos yet',
    noPhotosDesc: 'Upload some photos to get started with PhotoKik',
    photosOrganized: 'photos organized by AI',
    searchPhotos: 'Search photos...',
    viewAll: 'View all',
    collapse: 'Collapse',
    
    // Trash
    trashBin: 'Trash Bin',
    deletedPhotos: 'deleted photos',
    searchTrash: 'Search trash...',
    emptyTrash: 'Empty Trash',
    restore: 'Restore',
    deleteForever: 'Delete Forever',
    trashEmpty: 'Trash is empty',
    trashEmptyDesc: 'Photos you delete will appear here',
    selected: 'selected',
    selectAll: 'Select all',
    clear: 'Clear',
    emptyTrashConfirm: 'Empty Trash?',
    emptyTrashWarning: 'This will permanently delete all photos in your trash. This action cannot be undone.',
    cancel: 'Cancel',
    
    // Additional
    of: 'of',
  },
  es: {
    // Header
    swipe: 'Deslizar',
    gallery: 'Galería',
    trash: 'Papelera',
    storage: 'Almacenamiento',
    
    // Settings Page
    settings: 'Configuración',
    language: 'Idioma',
    english: 'Inglés',
    spanish: 'Español',
    portuguese: 'Portugués',
    swipeSensitivity: 'Sensibilidad de deslizamiento',
    low: 'Baja',
    medium: 'Media',
    high: 'Alta',
    photoManagement: 'Gestión de fotos',
    autoDeleteBlurry: 'Auto-eliminar fotos borrosas',
    autoDeleteBlurryDesc: 'Mover automáticamente las fotos borrosas a la papelera',
    autoCategorize: 'Auto-categorizar fotos',
    autoCategorizeDesc: 'Usar IA para clasificar automáticamente las fotos en categorías',
    storageAndPerformance: 'Almacenamiento y rendimiento',
    optimizeStorage: 'Optimizar almacenamiento',
    optimizeStorageDesc: 'Comprimir automáticamente las fotos para ahorrar espacio',
    systemOptimization: 'Optimización del sistema',
    systemOptimizationDesc: 'Optimizar el rendimiento y uso de memoria de la app',
    about: 'Acerca de',
    version: 'Versión',
    support: 'Soporte y comentarios',
    
    // Swipe Interface
    keep: 'GUARDAR',
    kik: 'ELIMINAR',
    allDone: '¡Todo listo!',
    allDoneDesc: 'Has revisado todas tus fotos.',
    reviewedPhotos: 'fotos revisadas',
    
    // Coming Soon
    comingSoon: '¡Próximamente en la siguiente fase!',
    
    // Gallery
    memories: 'Recuerdos',
    documents: 'Documentos',
    duplicates: 'Duplicados',
    blurry: 'Fotos borrosas',
    memoriesDesc: 'Tus momentos preciosos y experiencias',
    documentsDesc: 'Papeles importantes y recibos',
    duplicatesDesc: 'Fotos que aparecen múltiples veces',
    blurryDesc: 'Fotos que necesitan atención o eliminación',
    noPhotos: 'Aún no hay fotos',
    noPhotosDesc: 'Sube algunas fotos para comenzar con PhotoKik',
    photosOrganized: 'fotos organizadas por IA',
    searchPhotos: 'Buscar fotos...',
    viewAll: 'Ver todas',
    collapse: 'Colapsar',
    
    // Trash
    trashBin: 'Papelera',
    deletedPhotos: 'fotos eliminadas',
    searchTrash: 'Buscar en papelera...',
    emptyTrash: 'Vaciar papelera',
    restore: 'Restaurar',
    deleteForever: 'Eliminar para siempre',
    trashEmpty: 'La papelera está vacía',
    trashEmptyDesc: 'Las fotos que elimines aparecerán aquí',
    selected: 'seleccionadas',
    selectAll: 'Seleccionar todas',
    clear: 'Limpiar',
    emptyTrashConfirm: '¿Vaciar papelera?',
    emptyTrashWarning: 'Esto eliminará permanentemente todas las fotos en tu papelera. Esta acción no se puede deshacer.',
    cancel: 'Cancelar',
    
    // Additional
    of: 'de',
  },
  pt: {
    // Header
    swipe: 'Deslizar',
    gallery: 'Galeria',
    trash: 'Lixeira',
    storage: 'Armazenamento',
    
    // Settings Page
    settings: 'Configurações',
    language: 'Idioma',
    english: 'Inglês',
    spanish: 'Espanhol',
    portuguese: 'Português',
    swipeSensitivity: 'Sensibilidade de deslizamento',
    low: 'Baixa',
    medium: 'Média',
    high: 'Alta',
    photoManagement: 'Gerenciamento de fotos',
    autoDeleteBlurry: 'Auto-deletar fotos desfocadas',
    autoDeleteBlurryDesc: 'Mover automaticamente fotos desfocadas para o lixo',
    autoCategorize: 'Auto-categorizar fotos',
    autoCategorizeDesc: 'Usar IA para classificar automaticamente fotos em categorias',
    storageAndPerformance: 'Armazenamento e performance',
    optimizeStorage: 'Otimizar armazenamento',
    optimizeStorageDesc: 'Comprimir automaticamente fotos para economizar espaço',
    systemOptimization: 'Otimização do sistema',
    systemOptimizationDesc: 'Otimizar performance e uso de memória do app',
    about: 'Sobre',
    version: 'Versão',
    support: 'Suporte e feedback',
    
    // Swipe Interface
    keep: 'MANTER',
    kik: 'EXCLUIR',
    allDone: 'Tudo pronto!',
    allDoneDesc: 'Você revisou todas as suas fotos.',
    reviewedPhotos: 'fotos revisadas',
    
    // Coming Soon
    comingSoon: 'Em breve na próxima fase!',
    
    // Gallery
    memories: 'Memórias',
    documents: 'Documentos',
    duplicates: 'Duplicatas',
    blurry: 'Fotos desfocadas',
    memoriesDesc: 'Seus momentos preciosos e experiências',
    documentsDesc: 'Papéis importantes e recibos',
    duplicatesDesc: 'Fotos que aparecem múltiplas vezes',
    blurryDesc: 'Fotos que precisam de atenção ou exclusão',
    noPhotos: 'Ainda não há fotos',
    noPhotosDesc: 'Carregue algumas fotos para começar com PhotoKik',
    photosOrganized: 'fotos organizadas por IA',
    searchPhotos: 'Pesquisar fotos...',
    viewAll: 'Ver todas',
    collapse: 'Recolher',
    
    // Trash
    trashBin: 'Lixeira',
    deletedPhotos: 'fotos excluídas',
    searchTrash: 'Pesquisar na lixeira...',
    emptyTrash: 'Esvaziar lixeira',
    restore: 'Restaurar',
    deleteForever: 'Excluir para sempre',
    trashEmpty: 'A lixeira está vazia',
    trashEmptyDesc: 'Fotos que você excluir aparecerão aqui',
    selected: 'selecionadas',
    selectAll: 'Selecionar todas',
    clear: 'Limpar',
    emptyTrashConfirm: 'Esvaziar lixeira?',
    emptyTrashWarning: 'Isso excluirá permanentemente todas as fotos na sua lixeira. Esta ação não pode ser desfeita.',
    cancel: 'Cancelar',
    
    // Additional
    of: 'de',
  },
};

type Language = 'en' | 'es' | 'pt';
type TranslationKey = keyof typeof translations.en;

interface LanguageContextType {
  language: Language;
  setLanguage: (lang: Language) => void;
  t: (key: TranslationKey) => string;
}

const LanguageContext = createContext<LanguageContextType | undefined>(undefined);

export function LanguageProvider({ children }: { children: React.ReactNode }) {
  const [language, setLanguage] = useState<Language>('en');

  // Load language from localStorage on mount
  useEffect(() => {
    const saved = localStorage.getItem('photoKikLanguage') as Language;
    if (saved && ['en', 'es', 'pt'].includes(saved)) {
      setLanguage(saved);
    }
  }, []);

  // Save language to localStorage when changed
  useEffect(() => {
    localStorage.setItem('photoKikLanguage', language);
  }, [language]);

  const t = (key: TranslationKey): string => {
    return translations[language][key] || translations.en[key] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (context === undefined) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
}
