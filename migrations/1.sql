
CREATE TABLE photos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  filename TEXT NOT NULL,
  file_path TEXT NOT NULL,
  file_size INTEGER,
  width INTEGER,
  height INTEGER,
  category TEXT DEFAULT 'uncategorized',
  is_favorite BOOLEAN DEFAULT 0,
  is_deleted BOOLEAN DEFAULT 0,
  blur_score REAL,
  ai_tags TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
