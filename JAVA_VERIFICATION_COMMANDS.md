# Java JDK 17 Verification Commands

## After Setting Environment Variables

Run these commands in a **new** terminal/command prompt window:

### Check Java Installation:
```bash
java -version
```
Expected output:
```
openjdk version "17.0.13" 2024-10-15
OpenJDK Runtime Environment Temurin-17.0.13+11 (build 17.0.13+11)
OpenJDK 64-Bit Server VM Temurin-17.0.13+11 (build 17.0.13+11, mixed mode, sharing)
```

### Check Java Compiler:
```bash
javac -version
```
Expected output:
```
javac 17.0.13
```

### Check JAVA_HOME (Windows):
```cmd
echo %JAVA_HOME%
```

### Check JAVA_HOME (macOS/Linux):
```bash
echo $JAVA_HOME
```

### Check PATH includes Java (Windows):
```cmd
where java
```

### Check PATH includes Java (macOS/Linux):
```bash
which java
```

## Test Android Project Build

Navigate to your PhotoKik android folder and test:

```bash
cd android
./gradlew --version
```

You should see Gradle version info with JVM 17.

## If Commands Don't Work:

1. **Restart your computer** (especially on Windows)
2. **Open a completely new terminal/command prompt**
3. **Check your JDK installation path** - make sure the folder actually exists
4. **Try setting the path directly** in android/gradle.properties file

## Success Indicators:

✅ `java -version` shows version 17.x.x  
✅ `echo $JAVA_HOME` (or `%JAVA_HOME%`) shows your JDK path  
✅ `./gradlew --version` works without errors  
✅ Android Studio can sync the project  
✅ You can build the PhotoKik app  

Once all these work, your Java environment is properly configured!
