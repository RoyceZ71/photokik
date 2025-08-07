# PhotoKik - Android Studio Setup Guide

## Prerequisites
Before opening in Android Studio, ensure you have:
1. **Android Studio** installed (latest version recommended)
2. **Android SDK** (API level 34 minimum)
3. **Java JDK 8 or higher**
4. **Node.js** and **npm** installed

## Required Steps to Run in Android Studio

### 1. Install Dependencies
```bash
cd your-photokik-project-folder
npm install
```

### 2. Build the Web App
```bash
npm run build
```
This creates the `dist` folder that Android needs.

### 3. Sync Capacitor
```bash
npx cap sync android
```
This copies the web app to the Android project and updates native dependencies.

### 4. Open in Android Studio
```bash
npx cap open android
```
OR manually open Android Studio and select the `android` folder in your project.

## Common Issues & Solutions

### Issue: "dist folder not found"
**Solution:** Run `npm run build` first

### Issue: "Capacitor plugins not found"
**Solution:** Run `npx cap sync android`

### Issue: Gradle sync fails
**Solution:** 
1. In Android Studio: File → Sync Project with Gradle Files
2. Clean project: Build → Clean Project
3. Rebuild: Build → Rebuild Project

### Issue: Android SDK errors
**Solution:** 
1. Open SDK Manager in Android Studio
2. Install Android SDK Platform 34
3. Install Android SDK Build-Tools 34.0.0

### Issue: App won't start on device/emulator
**Solution:**
1. Make sure you've run `npm run build && npx cap sync android`
2. Check that your Android device has USB debugging enabled
3. Try running on a different API level emulator

## Verify Setup
After opening in Android Studio, you should see:
- `app` module with PhotoKik source code
- Multiple Capacitor plugin modules
- Gradle sync should complete successfully
- You can run the app on an emulator or device

## Development Workflow
When making changes to the web app:
1. Make your changes in `src/react-app/`
2. Run `npm run build`
3. Run `npx cap sync android`
4. Rebuild in Android Studio

The app should now launch successfully!
