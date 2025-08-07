
CREATE TABLE user_settings (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id TEXT,
  language TEXT DEFAULT 'en',
  swipe_sensitivity REAL DEFAULT 0.5,
  auto_delete_blurry BOOLEAN DEFAULT 0,
  auto_categorize BOOLEAN DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
