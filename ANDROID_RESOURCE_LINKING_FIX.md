# PhotoKik - Android Resource Linking Error Fix

## Problem
Getting "android resource linking failed" error when building in Android Studio.

## Root Cause
The error occurs because **Java JDK is not installed** on your system. Android Gradle Plugin requires Java to compile and link resources.

## Quick Fix

### Step 1: Install Java JDK 17 (Recommended)

**Windows:**
1. Download from: https://adoptium.net/temurin/releases/
2. Select "JDK 17 (LTS)" 
3. Run installer and restart computer
4. Or use Chocolatey: `choco install openjdk17`

**macOS:**
```bash
# Using Homebrew (recommended)
brew install openjdk@17

# Or download from Adoptium
```

**Linux/Ubuntu:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk

# Verify installation
java -version
javac -version
```

### Step 2: Set JAVA_HOME Environment Variable

**Windows:**
1. System Properties → Advanced → Environment Variables
2. Create new system variable:
   - Name: `JAVA_HOME`
   - Value: `C:\Program Files\Eclipse Adoptium\jdk-17.0.x-hotspot`
3. Add to PATH: `%JAVA_HOME%\bin`
4. **Restart computer**

**macOS:**
```bash
# Add to ~/.zshrc or ~/.bash_profile
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.x.x/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

# Reload shell
source ~/.zshrc
```

**Linux:**
```bash
# Add to ~/.bashrc or ~/.profile  
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Reload shell
source ~/.bashrc
```

### Step 3: Verify Java Installation
```bash
java -version
javac -version
echo $JAVA_HOME  # Should show your JDK path
```

### Step 4: Configure Android Studio
1. Open Android Studio
2. File → Project Structure → SDK Location
3. Set "JDK location" to your JDK installation path
4. Apply and restart Android Studio

### Step 5: Test Build
1. Open PhotoKik `android` folder in Android Studio
2. Wait for Gradle sync (should complete successfully now)
3. Build → Clean Project
4. Build → Rebuild Project
5. Run app on device/emulator

## Alternative: Set JDK Path Directly in gradle.properties

If you still have issues, uncomment and set the path in `android/gradle.properties`:

```properties
# Windows example:
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.9-hotspot

# macOS example:  
org.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

# Linux example:
org.gradle.java.home=/usr/lib/jvm/java-17-openjdk-amd64
```

## Why This Happens
- Android Gradle Plugin needs Java to compile resources
- "Resource linking failed" is misleading - it's actually "Java not found"
- Without Java, no Android build operations can run

## Verification Steps
Once Java is installed correctly:

1. **Command line test:**
   ```bash
   cd android
   ./gradlew --version
   # Should show Gradle version and JVM info
   ```

2. **Android Studio test:**
   - Project should sync without errors
   - Build output should show successful compilation
   - App should install and run on device

---

✅ **After following these steps, your PhotoKik Android app should build successfully!**

The resource linking error will be resolved once Java JDK is properly installed and configured.
