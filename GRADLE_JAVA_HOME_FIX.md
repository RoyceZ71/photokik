# Fix "Java home supplied is invalid" Error

## Problem
Getting error: `Value '' given for org.gradle.java.home Gradle property is invalid`

## Quick Fix

### Option 1: Use System JAVA_HOME (Recommended)
1. Open `android/gradle.properties`
2. Find this line: `org.gradle.java.home=`
3. **Comment it out** by adding `#` at the beginning:
   ```properties
   # org.gradle.java.home=
   ```
4. Make sure your JAVA_HOME environment variable is set correctly
5. Restart Android Studio

### Option 2: Set Explicit Path
1. Find your JDK installation path:
   - **Windows**: Usually `C:\Program Files\Java\jdk-24` or `C:\Program Files\Eclipse Adoptium\jdk-24.0.0.0-hotspot`
   - **macOS**: Usually `/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home`
   - **Linux**: Usually `/usr/lib/jvm/java-24-openjdk` or `/opt/jdk-24`

2. Open `android/gradle.properties`
3. Set the correct path:
   ```properties
   org.gradle.java.home=/your/actual/jdk/path/here
   ```

### Verify Your JDK Installation
```bash
# Check if Java is installed and get the path
java -version
javac -version

# On Windows, find Java installation
where java

# On macOS/Linux, find Java installation
which java
/usr/libexec/java_home -V  # macOS only
```

### Common Installation Paths

**Windows:**
```properties
# Oracle JDK
org.gradle.java.home=C:\\Program Files\\Java\\jdk-24

# Eclipse Adoptium
org.gradle.java.home=C:\\Program Files\\Eclipse Adoptium\\jdk-24.0.0.0-hotspot

# OpenJDK
org.gradle.java.home=C:\\openjdk\\jdk-24
```

**macOS:**
```properties
# Oracle JDK
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home

# Homebrew OpenJDK
org.gradle.java.home=/opt/homebrew/Cellar/openjdk@24/24.0.0/libexec/openjdk.jdk/Contents/Home
```

**Linux:**
```properties
# System OpenJDK
org.gradle.java.home=/usr/lib/jvm/java-24-openjdk

# Manual installation
org.gradle.java.home=/opt/jdk-24
```

## Test the Fix
1. Close Android Studio completely
2. Open Terminal/Command Prompt
3. Navigate to the `android` folder
4. Run: `./gradlew --version`
5. You should see Gradle version info without errors
6. Open Android Studio and try Gradle sync again

## If Still Having Issues
1. **Reinstall JDK**: Download fresh JDK 24 from https://jdk.java.net/24/
2. **Set Environment Variables**:
   - JAVA_HOME pointing to your JDK installation
   - Add %JAVA_HOME%\bin (Windows) or $JAVA_HOME/bin (macOS/Linux) to PATH
3. **Restart your computer** after setting environment variables
4. **Use JDK 21 instead**: If JDK 24 causes issues, use JDK 21 (LTS) for better compatibility

---

✅ After fixing this, your Android project should sync successfully!
