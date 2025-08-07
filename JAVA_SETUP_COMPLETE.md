# Java JDK 17 Setup Complete Guide

## Current Status
✅ JDK 17 installed  
❌ JAVA_HOME environment variable not set  
❌ Java not in system PATH  

## Required Steps to Complete Setup

### Windows:
1. **Find your JDK installation path** (usually one of):
   - `C:\Program Files\Java\jdk-17`
   - `C:\Program Files\Eclipse Adoptium\jdk-17.*`
   - `C:\Program Files\OpenJDK\jdk-17.*`

2. **Set JAVA_HOME environment variable:**
   - Press `Win + R`, type `sysdm.cpl`, press Enter
   - Click "Environment Variables" button
   - Click "New" under System Variables
   - Variable name: `JAVA_HOME`
   - Variable value: Your JDK path (e.g., `C:\Program Files\Java\jdk-17`)

3. **Add Java to PATH:**
   - In Environment Variables, find "Path" in System Variables
   - Click "Edit" → "New"
   - Add: `%JAVA_HOME%\bin`

### macOS:
1. **Check JDK location:**
   ```bash
   /usr/libexec/java_home -V
   ```

2. **Set JAVA_HOME in your shell profile:**
   ```bash
   echo 'export JAVA_HOME=$(/usr/libexec/java_home -v17)' >> ~/.zshrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
   source ~/.zshrc
   ```

### Linux:
1. **Install OpenJDK 17:**
   ```bash
   # Ubuntu/Debian
   sudo apt update && sudo apt install openjdk-17-jdk
   
   # CentOS/RHEL
   sudo yum install java-17-openjdk-devel
   ```

2. **Set JAVA_HOME:**
   ```bash
   echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >> ~/.bashrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
   source ~/.bashrc
   ```

## Verification Commands

After setting up, **restart your terminal/command prompt** and run:

```bash
java -version
echo $JAVA_HOME    # Linux/macOS
echo %JAVA_HOME%   # Windows
```

You should see:
```
openjdk version "17.0.x" 2023-xx-xx
OpenJDK Runtime Environment...
OpenJDK 64-Bit Server VM...
```

## Android Studio Configuration

1. Open Android Studio
2. Go to File → Settings (or Preferences on Mac)
3. Navigate to Build, Execution, Deployment → Build Tools → Gradle
4. Set "Gradle JVM" to your JDK 17 installation
5. Click Apply and OK

## Next Steps

Once Java is properly configured:
1. **Restart your computer** (important for environment variables)
2. Open Android Studio
3. Open the PhotoKik project folder: `your-project/android`
4. Let Gradle sync automatically
5. Build the project

## Troubleshooting

If you still get "JAVA_HOME is not set" error:
- Make sure you **restarted your terminal/IDE**
- Verify JAVA_HOME path exists and contains `bin/java`
- On Windows, use forward slashes: `C:/Program Files/Java/jdk-17`

## Alternative: Set in gradle.properties

If environment variables don't work, you can set Java path directly in the project:

Edit `android/gradle.properties` and add:
```properties
# Windows example
org.gradle.java.home=C:/Program Files/Java/jdk-17

# macOS example  
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

# Linux example
org.gradle.java.home=/usr/lib/jvm/java-17-openjdk
```

## Updated Android Project

I've updated your Android project to use:
- ✅ JDK 17 (latest LTS version)
- ✅ Android Gradle Plugin 8.7.2
- ✅ Kotlin 1.9.24
- ✅ Latest Compose BOM 2024.06.00
- ✅ Hilt 2.51.1
- ✅ Modern Android APIs (API 34)
- ✅ Edge-to-edge display support
- ✅ Splash screen API
- ✅ Proper permissions for Android 13+

The native Kotlin code is fully updated and ready to build once Java is properly configured!
