interface CowboyBootIconProps {
  size?: number;
  className?: string;
}

export default function CowboyBootIcon({ size = 24, className = "" }: CowboyBootIconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="currentColor"
      className={className}
      xmlns="http://www.w3.org/2000/svg"
    >
      {/* Cowboy boot shape */}
      <path d="M8 2C8 1.45 8.45 1 9 1h6c0.55 0 1 0.45 1 1v8c0 1.1-0.9 2-2 2h-1v2c0 0.55 0.45 1 1 1h5c1.1 0 2 0.9 2 2v4c0 1.1-0.9 2-2 2H4c-1.1 0-2-0.9-2-2v-1c0-1.1 0.9-2 2-2h2v-3c0-2.21 1.79-4 4-4V2z" />
      {/* Boot heel */}
      <path d="M2 18h2v3c0 0.55-0.45 1-1 1s-1-0.45-1-1v-3z" />
      {/* Boot spur */}
      <circle cx="4" cy="19.5" r="1" />
      <path d="M5 19.5l2-1v2l-2-1z" />
      {/* Boot decorative stitching */}
      <path d="M9 3h4m-4 2h4m-4 2h4" stroke="currentColor" strokeWidth="0.5" fill="none" opacity="0.7" />
    </svg>
  );
}
