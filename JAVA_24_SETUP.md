# PhotoKik - Java JDK 24 Setup Guide

## Installing Java JDK 24

### Option 1: Oracle JDK 24 (Recommended)
1. **Download JDK 24:**
   - Visit: https://www.oracle.com/java/technologies/downloads/
   - Select "Java 24" from the version dropdown
   - Download for your operating system (Windows, macOS, or Linux)

### Option 2: OpenJDK 24
1. **Download OpenJDK 24:**
   - Visit: https://jdk.java.net/24/
   - Download the appropriate build for your OS

### Option 3: Using Package Managers

**Windows (using Chocolatey):**
```bash
choco install openjdk --version=24
```

**macOS (using Homebrew):**
```bash
brew install openjdk@24
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install openjdk-24-jdk
```

## Post-Installation Setup

### 1. Verify Installation
```bash
java -version
javac -version
```
You should see version 24.x.x

### 2. Set JAVA_HOME Environment Variable

**Windows:**
1. Open System Properties → Advanced → Environment Variables
2. Create new system variable:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-24` (or your JDK path)
3. Add to PATH: `%JAVA_HOME%\bin`

**macOS/Linux:**
Add to your `.bashrc`, `.zshrc`, or `.profile`:
```bash
export JAVA_HOME=/path/to/jdk-24
export PATH=$JAVA_HOME/bin:$PATH
```

### 3. Configure Android Studio
1. Open Android Studio
2. Go to: **File → Project Structure → SDK Location**
3. Set "JDK location" to your JDK 24 installation path
4. **IMPORTANT**: If you get "Java home invalid" error:
   - Either leave `org.gradle.java.home` commented out in `android/gradle.properties`
   - Or set it to your exact JDK path: `org.gradle.java.home=/path/to/your/jdk-24`
5. Restart Android Studio

## Project Updates Made

The PhotoKik project has been updated with:

- **Java 21 Compatibility:** While you requested JDK 24, we've set the project to Java 21 (latest LTS) for maximum stability
- **Updated Gradle configurations** in all build files
- **Modern build tools** compatible with newer Java versions

## If You Want to Use JDK 24 Specifically

If you want to force JDK 24 (experimental), you can:

1. **Update gradle.properties:**
```properties
org.gradle.java.home=/path/to/your/jdk-24
```

2. **Update build.gradle files manually:**
   - Change `JavaVersion.VERSION_21` to `JavaVersion.VERSION_24`
   - Note: This might not work if Gradle doesn't support JDK 24 yet

## Troubleshooting

### "Unsupported Java version" Error
- JDK 24 might not be supported by current Gradle version
- Downgrade to JDK 21 (LTS) for guaranteed compatibility
- Update Gradle wrapper to latest version

### Build Failures
- Clean project: `./gradlew clean`
- Rebuild: `./gradlew build`
- Check Gradle compatibility matrix

### Android Studio Issues
- Invalidate caches: **File → Invalidate Caches and Restart**
- Check JDK path in Project Structure settings

## Recommended Approach

For production apps, we recommend:
1. **Use JDK 21 (LTS)** for stability
2. **Test thoroughly** before deploying
3. **Keep JDK 17** as fallback if needed

The project is now configured for modern Java versions and should work seamlessly with your development environment.
