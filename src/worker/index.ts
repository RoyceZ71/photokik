import { Hono } from 'hono';
import { cors } from 'hono/cors';

type Bindings = {
  DB: D1Database;
};

const app = new Hono<{ Bindings: Bindings }>();

// Enable CORS for all routes
app.use('*', cors({
  origin: '*',
  allowMethods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowHeaders: ['Content-Type', 'Authorization'],
}));

// Health check
app.get('/api/health', (c) => {
  return c.json({ status: 'healthy', timestamp: new Date().toISOString() });
});

// Get all photos
app.get('/api/photos', async (c) => {
  try {
    const db = c.env.DB;
    const { results } = await db.prepare(`
      SELECT id, filename, file_path, category, is_favorite, blur_score, 
             ai_tags, created_at, updated_at 
      FROM photos 
      WHERE is_deleted = 0 
      ORDER BY created_at DESC
    `).all();
    
    return c.json({ photos: results });
  } catch (error) {
    console.error('Error fetching photos:', error);
    return c.json({ error: 'Failed to fetch photos' }, 500);
  }
});

// Add a new photo
app.post('/api/photos', async (c) => {
  try {
    const db = c.env.DB;
    const { filename, file_path, category = 'uncategorized', file_size, width, height } = await c.req.json();
    
    if (!filename || !file_path) {
      return c.json({ error: 'Missing required fields' }, 400);
    }

    const { success, meta } = await db.prepare(`
      INSERT INTO photos (filename, file_path, category, file_size, width, height)
      VALUES (?, ?, ?, ?, ?, ?)
    `).bind(filename, file_path, category, file_size || null, width || null, height || null).run();

    if (success) {
      return c.json({ photoId: meta.last_row_id });
    } else {
      return c.json({ error: 'Failed to save photo' }, 500);
    }
  } catch (error) {
    console.error('Error adding photo:', error);
    return c.json({ error: 'Failed to add photo' }, 500);
  }
});

// Move photo to trash
app.post('/api/photos/trash', async (c) => {
  try {
    const db = c.env.DB;
    const { photoId } = await c.req.json();
    
    if (!photoId) {
      return c.json({ error: 'Missing photoId' }, 400);
    }

    const { success } = await db.prepare(`
      UPDATE photos 
      SET is_deleted = 1, updated_at = CURRENT_TIMESTAMP 
      WHERE id = ?
    `).bind(photoId).run();

    if (success) {
      return c.json({ success: true });
    } else {
      return c.json({ error: 'Photo not found' }, 404);
    }
  } catch (error) {
    console.error('Error moving photo to trash:', error);
    return c.json({ error: 'Failed to move photo to trash' }, 500);
  }
});

// Get trashed photos
app.get('/api/trash', async (c) => {
  try {
    const db = c.env.DB;
    const { results } = await db.prepare(`
      SELECT id, filename, file_path, category, updated_at 
      FROM photos 
      WHERE is_deleted = 1 
      ORDER BY updated_at DESC
    `).all();
    
    return c.json({ photos: results });
  } catch (error) {
    console.error('Error fetching trash:', error);
    return c.json({ error: 'Failed to fetch trash' }, 500);
  }
});

// Restore photo from trash
app.post('/api/photos/restore', async (c) => {
  try {
    const db = c.env.DB;
    const { photoId } = await c.req.json();
    
    if (!photoId) {
      return c.json({ error: 'Missing photoId' }, 400);
    }

    const { success } = await db.prepare(`
      UPDATE photos 
      SET is_deleted = 0, updated_at = CURRENT_TIMESTAMP 
      WHERE id = ?
    `).bind(photoId).run();

    if (success) {
      return c.json({ success: true });
    } else {
      return c.json({ error: 'Photo not found' }, 404);
    }
  } catch (error) {
    console.error('Error restoring photo:', error);
    return c.json({ error: 'Failed to restore photo' }, 500);
  }
});

// Permanently delete photo
app.delete('/api/photos/:id', async (c) => {
  try {
    const db = c.env.DB;
    const photoId = c.req.param('id');
    
    const { success } = await db.prepare(`
      DELETE FROM photos WHERE id = ?
    `).bind(photoId).run();

    if (success) {
      return c.json({ success: true });
    } else {
      return c.json({ error: 'Photo not found' }, 404);
    }
  } catch (error) {
    console.error('Error deleting photo:', error);
    return c.json({ error: 'Failed to delete photo' }, 500);
  }
});

// Empty trash
app.post('/api/trash/empty', async (c) => {
  try {
    const db = c.env.DB;
    
    const { success } = await db.prepare(`
      DELETE FROM photos WHERE is_deleted = 1
    `).run();

    if (success) {
      return c.json({ success: true });
    } else {
      return c.json({ error: 'Failed to empty trash' }, 500);
    }
  } catch (error) {
    console.error('Error emptying trash:', error);
    return c.json({ error: 'Failed to empty trash' }, 500);
  }
});

// Get user settings
app.get('/api/settings', async (c) => {
  try {
    const db = c.env.DB;
    const { results } = await db.prepare(`
      SELECT * FROM user_settings ORDER BY created_at DESC LIMIT 1
    `).all();
    
    const settings = results[0] || {
      language: 'en',
      swipe_sensitivity: 0.5,
      auto_delete_blurry: false,
      auto_categorize: true
    };
    
    return c.json({ settings });
  } catch (error) {
    console.error('Error fetching settings:', error);
    return c.json({ error: 'Failed to fetch settings' }, 500);
  }
});

// Update user settings
app.post('/api/settings', async (c) => {
  try {
    const db = c.env.DB;
    const settings = await c.req.json();
    
    const { success } = await db.prepare(`
      INSERT OR REPLACE INTO user_settings 
      (id, language, swipe_sensitivity, auto_delete_blurry, auto_categorize, updated_at)
      VALUES (1, ?, ?, ?, ?, CURRENT_TIMESTAMP)
    `).bind(
      settings.language || 'en',
      settings.swipe_sensitivity || 0.5,
      settings.auto_delete_blurry || false,
      settings.auto_categorize !== false
    ).run();

    if (success) {
      return c.json({ success: true });
    } else {
      return c.json({ error: 'Failed to update settings' }, 500);
    }
  } catch (error) {
    console.error('Error updating settings:', error);
    return c.json({ error: 'Failed to update settings' }, 500);
  }
});

// Serve React app for all other routes
app.get('*', async (c) => {
  // This will be handled by Vite's build process
  return c.text('PhotoKik App', 200);
});

export default app;
