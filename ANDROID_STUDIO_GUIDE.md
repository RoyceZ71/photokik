# PhotoKik Android Studio Setup Guide

## Prerequisites (Critical - Do These First!)

1. **Install Java Development Kit (JDK)**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or OpenJDK: https://openjdk.org/install/
   - **RESTART YOUR COMPUTER** after installation
   - Verify installation: Open Command Prompt/Terminal and run: `java -version`

2. **Install Android Studio**
   - Download from: https://developer.android.com/studio
   - Follow the installation wizard
   - Let it install the Android SDK when prompted

## Opening PhotoKik in Android Studio

### Step 1: Locate Your Project
- Find your PhotoKik folder on your computer
- You should see folders like: `android/`, `src/`, `dist/`, etc.

### Step 2: Open in Android Studio
1. Launch Android Studio
2. Choose **"Open an Existing Project"** (NOT "Import Project")
3. Navigate to your PhotoKik folder
4. **Important**: Select the `android` folder (inside PhotoKik, not the root PhotoKik folder)
5. Click "OK"

### Step 3: Wait for Gradle Sync
- Android Studio will automatically start "Gradle Sync"
- This can take 5-15 minutes on first setup
- You'll see progress at the bottom of the screen
- Don't interrupt this process

### Step 4: If Gradle Sync Fails
Try these in order:
1. **File → Sync Project with Gradle Files**
2. **Build → Clean Project** → then **Build → Rebuild Project**
3. **File → Invalidate Caches and Restart → Invalidate and Restart**

### Step 5: Set Up a Device

**Option A: Use Your Phone**
1. Enable Developer Options on your Android phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging (ON)
3. Connect phone to computer via USB
4. Accept the debugging permission popup

**Option B: Create Virtual Device**
1. In Android Studio: **Tools → AVD Manager**
2. Click **"Create Virtual Device"**
3. Select a phone model (Pixel 6 recommended)
4. Download and select Android API 34
5. Click "Finish"

### Step 6: Run the App
1. Make sure your device appears in the device dropdown (top toolbar)
2. Click the green "Run" button (play icon)
3. Or use **Run → Run 'app'**

## Project Structure
```
PhotoKik/
├── android/          ← Open THIS folder in Android Studio
│   ├── app/
│   │   ├── src/main/
│   │   └── build.gradle
│   ├── build.gradle
│   └── settings.gradle
├── src/
├── dist/
└── capacitor.config.ts
```

## Common Issues & Solutions

### "JDK Not Found"
- Install JDK (see Prerequisites)
- Restart computer
- In Android Studio: **File → Project Structure → SDK Location**
- Set JDK location if needed

### "Gradle Sync Failed"
- Check internet connection
- Try: **File → Sync Project with Gradle Files**
- Clear cache: **File → Invalidate Caches and Restart**

### "Device Not Detected"
- Enable USB Debugging on phone
- Try different USB cable
- Install device drivers if needed

### "Build Failed"
- Check Android SDK is installed
- **Tools → SDK Manager** → Install Android 14 (API 34)
- **Build → Clean Project** → **Rebuild Project**

## App Details
- **Package Name**: com.mocha.photokik
- **App Name**: PhotoKik
- **Min SDK**: Android 5.1 (API 22)
- **Target SDK**: Android 14 (API 34)

## Need Help?
If you encounter issues:
1. Check the **Build** tab at bottom of Android Studio for error details
2. Look at **Logcat** tab for runtime errors
3. Try the troubleshooting steps above
4. Make sure you opened the `android` folder, not the root project folder

---

✅ Your PhotoKik project is ready for Android Studio!
The web assets have been automatically copied to the Android project.
