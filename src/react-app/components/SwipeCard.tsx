import { useState, useRef, useEffect, useCallback } from 'react';
import { Heart, X } from 'lucide-react';
import { useLanguage } from '../contexts/LanguageContext';

interface SwipeCardProps {
  photo: {
    id: number;
    filename: string;
    file_path: string;
    category?: string;
  };
  onSwipe: (direction: 'keep' | 'kik') => void;
  swipeSensitivity?: number;
}

export default function SwipeCard({ photo, onSwipe, swipeSensitivity = 0.5 }: SwipeCardProps) {
  const { t } = useLanguage();
  const [isDragging, setIsDragging] = useState(false);
  const [swipeDirection, setSwipeDirection] = useState<'keep' | 'kik' | null>(null);
  const cardRef = useRef<HTMLDivElement>(null);
  const startPos = useRef({ x: 0, y: 0 });
  const currentPos = useRef({ x: 0, y: 0 });
  const animationId = useRef<number | undefined>(undefined);

  const updateCardTransform = useCallback((deltaX: number, deltaY: number) => {
    if (!cardRef.current) return;
    
    const rotationValue = deltaX * 0.08; // Reduced rotation for smoother feel
    const scale = isDragging ? 1.02 : 1;
    
    // Use transform3d for hardware acceleration
    cardRef.current.style.transform = `translate3d(${deltaX}px, ${deltaY}px, 0) rotate(${rotationValue}deg) scale(${scale})`;
    
    // Update swipe direction indicator
    const absX = Math.abs(deltaX);
    if (absX > 60) {
      setSwipeDirection(deltaX > 0 ? 'keep' : 'kik');
    } else {
      setSwipeDirection(null);
    }
  }, [isDragging]);

  const handleStart = useCallback((clientX: number, clientY: number) => {
    setIsDragging(true);
    startPos.current = { x: clientX, y: clientY };
    currentPos.current = { x: 0, y: 0 };
    
    if (cardRef.current) {
      cardRef.current.style.transition = 'none';
    }
  }, []);

  const handleMove = useCallback((clientX: number, clientY: number) => {
    if (!isDragging) return;
    
    const deltaX = clientX - startPos.current.x;
    const deltaY = clientY - startPos.current.y;
    
    currentPos.current = { x: deltaX, y: deltaY };
    
    // Use requestAnimationFrame for smooth 60fps updates
    if (animationId.current) {
      cancelAnimationFrame(animationId.current);
    }
    
    animationId.current = requestAnimationFrame(() => {
      updateCardTransform(deltaX, deltaY);
    });
  }, [isDragging, updateCardTransform]);

  const handleEnd = useCallback(() => {
    if (!isDragging) return;
    
    setIsDragging(false);
    setSwipeDirection(null);
    
    const threshold = 120 * swipeSensitivity;
    const deltaX = currentPos.current.x;
    
    if (cardRef.current) {
      cardRef.current.style.transition = 'transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
    }
    
    if (Math.abs(deltaX) > threshold) {
      const direction = deltaX > 0 ? 'keep' : 'kik';
      
      // Animate card off screen
      if (cardRef.current) {
        const exitX = deltaX > 0 ? window.innerWidth + 200 : -(window.innerWidth + 200);
        cardRef.current.style.transform = `translate3d(${exitX}px, ${currentPos.current.y}px, 0) rotate(${deltaX * 0.2}deg) scale(0.8)`;
        cardRef.current.style.opacity = '0';
      }
      
      // Trigger onSwipe after animation starts
      setTimeout(() => onSwipe(direction), 100);
    } else {
      // Snap back to center
      updateCardTransform(0, 0);
      if (cardRef.current) {
        cardRef.current.style.transition = 'transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
      }
    }
  }, [isDragging, swipeSensitivity, onSwipe, updateCardTransform]);

  const handleMouseDown = (e: React.MouseEvent) => {
    e.preventDefault();
    handleStart(e.clientX, e.clientY);
  };

  const handleTouchStart = (e: React.TouchEvent) => {
    e.preventDefault();
    const touch = e.touches[0];
    handleStart(touch.clientX, touch.clientY);
  };

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      handleMove(e.clientX, e.clientY);
    };

    const handleTouchMove = (e: TouchEvent) => {
      e.preventDefault(); // Prevent scroll
      const touch = e.touches[0];
      handleMove(touch.clientX, touch.clientY);
    };

    if (isDragging) {
      document.addEventListener('mousemove', handleMouseMove, { passive: false });
      document.addEventListener('mouseup', handleEnd);
      document.addEventListener('touchmove', handleTouchMove, { passive: false });
      document.addEventListener('touchend', handleEnd);
    }

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleEnd);
      document.removeEventListener('touchmove', handleTouchMove);
      document.removeEventListener('touchend', handleEnd);
      
      if (animationId.current) {
        cancelAnimationFrame(animationId.current);
      }
    };
  }, [isDragging, handleMove, handleEnd]);

  const getSwipeIndicator = () => {
    if (!swipeDirection) return null;
    
    return swipeDirection === 'keep' ? (
      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-green-500 text-white px-4 py-2 rounded-2xl font-black text-lg sm:text-xl flex items-center gap-2 shadow-xl border-2 border-white animate-pulse z-20">
        <Heart size={20} className="fill-current" />
        {t('keep')}
      </div>
    ) : (
      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-red-500 text-white px-4 py-2 rounded-2xl font-black text-lg sm:text-xl flex items-center gap-2 shadow-xl border-2 border-white animate-pulse z-20">
        <X size={20} />
        {t('kik')}
      </div>
    );
  };

  return (
    <div
      ref={cardRef}
      className={`
        relative bg-white rounded-3xl shadow-xl cursor-grab select-none
        w-[85vw] h-[55vh] max-w-lg max-h-[600px] min-h-[450px]
        sm:w-80 sm:h-[500px] lg:w-96 lg:h-[580px] xl:w-[420px] xl:h-[620px]
        border-2 border-transparent
        bg-gradient-to-br from-purple-400/30 via-pink-400/30 to-blue-400/30
        photokik-card-glow
        will-change-transform
        ${isDragging ? 'cursor-grabbing photokik-card-drag-glow' : 'cursor-grab'}
      `}
      style={{
        transformOrigin: 'center center',
        backfaceVisibility: 'hidden',
        perspective: '1000px',
      }}
      onMouseDown={handleMouseDown}
      onTouchStart={handleTouchStart}
    >
      {/* Glowing border effect */}
      <div className="absolute inset-0 rounded-2xl bg-gradient-to-r from-purple-500 via-pink-500 to-blue-500 opacity-60 blur-sm -z-10" />
      
      <div className="absolute inset-1 bg-gray-900 rounded-2xl overflow-hidden">
        <img
          src={photo.file_path || `https://picsum.photos/400/600?random=${photo.id}`}
          alt={photo.filename}
          className="w-full h-full object-cover will-change-transform"
          draggable={false}
          loading="lazy"
        />
        
        {/* Enhanced gradient overlay */}
        <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/90 via-black/50 to-transparent p-4 sm:p-6">
          <h3 className="text-white font-bold text-xl sm:text-2xl mb-2 truncate drop-shadow-lg">
            {photo.filename}
          </h3>
          {photo.category && (
            <p className="text-white/80 text-base sm:text-lg capitalize drop-shadow-md">
              {photo.category}
            </p>
          )}
        </div>

        {getSwipeIndicator()}
        
        {/* Drag indicator */}
        {isDragging && (
          <div className="absolute inset-0 bg-white/10 backdrop-blur-sm rounded-3xl pointer-events-none" />
        )}
      </div>
    </div>
  );
}
