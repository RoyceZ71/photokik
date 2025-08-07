# PhotoKik Android Studio Troubleshooting Guide

## Common Issues and Solutions

### ❌ Issue: "App won't build or run in Android Studio"

**Most common causes:**

1. **Missing Java Development Kit (JDK)**
   - Android Studio requires JDK 8 or higher
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or install OpenJDK: https://openjdk.org/install/

2. **Gradle sync failures**
   - Go to: File → Sync Project with Gradle Files
   - If that fails: Build → Clean Project → Build → Rebuild Project

3. **Missing Android SDK**
   - Open Android Studio → SDK Manager
   - Install Android SDK Platform 34 and Build-Tools 34.0.0

4. **Capacitor assets not found**
   - Run: `npm run build && npx cap sync android`
   - Or copy dist/client/* to android/app/src/main/assets/public/

### ✅ Step-by-Step Setup Process

1. **Install Prerequisites:**
   ```bash
   # Install Node.js dependencies
   npm install
   
   # Build the web app
   npm run build
   
   # Sync with Capacitor (if working)
   npx cap sync android
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Click "Open an Existing Project"
   - Navigate to your PhotoKik folder
   - Select the `android` folder (NOT the root folder)
   - Click "OK"

3. **Wait for Gradle Sync:**
   - Android Studio will automatically sync Gradle (this can take 5-10 minutes)
   - You'll see progress in the status bar at the bottom
   - If sync fails, check the "Build" tab for error messages

4. **Set Up Device/Emulator:**
   - **For Physical Device:**
     - Enable Developer Options on your phone
     - Enable USB Debugging
     - Connect via USB and allow debugging
   
   - **For Emulator:**
     - Tools → AVD Manager
     - Create Virtual Device
     - Choose Pixel 6 or similar (API 34)
     - Download system image if needed

5. **Run the App:**
   - Click the green "Play" button in the toolbar
   - Or press Shift+F10
   - Select your device/emulator
   - Wait for app to install and launch

### 🔧 Common Error Solutions

**Error: "Module not found" or "Gradle sync failed"**
```bash
# Clean and rebuild
cd android
./gradlew clean
./gradlew build
```

**Error: "dist folder not found"**
```bash
# Rebuild web app
npm run build
npx cap copy android
```

**Error: "JAVA_HOME not set"**
- Install JDK 8 or higher
- Set JAVA_HOME environment variable
- Restart Android Studio

**Error: "SDK not found"**
- Open SDK Manager in Android Studio
- Install Android 14 (API 34) platform
- Install Build-Tools 34.0.0

**Error: "Device offline" or "No devices found"**
- Check USB connection
- Enable USB Debugging on phone
- Try different USB cable
- Restart ADB: `adb kill-server && adb start-server`

### 📱 Testing Your Setup

Once the app launches successfully, you should see:
- PhotoKik splash screen with purple gradient
- Main app interface with navigation tabs
- Camera FAB button (floating action button)
- Swipe interface for photo management

### 🆘 Still Having Issues?

1. **Check Android Studio logs:**
   - View → Tool Windows → Build
   - Look for red error messages

2. **Verify file structure:**
   ```
   android/
   ├── app/
   │   ├── src/main/
   │   │   ├── assets/public/
   │   │   │   ├── index.html
   │   │   │   └── assets/
   │   │   ├── java/com/mocha/photokik/
   │   │   └── res/
   │   └── build.gradle
   └── build.gradle
   ```

3. **Try manual build:**
   ```bash
   cd android
   ./gradlew assembleDebug
   ```

4. **Reset everything:**
   ```bash
   # Clean everything
   npm run build
   npx cap sync android
   cd android
   ./gradlew clean
   ```

The app should work once these steps are followed correctly. The main requirements are having Java/JDK installed and the web assets properly copied to the Android assets folder.
