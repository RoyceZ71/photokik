# Java JDK 17 Environment Setup Guide

## Setting JAVA_HOME and PATH Environment Variables

### Windows Instructions:

1. **Find your JDK 17 installation path:**
   - Common locations:
     - `C:\Program Files\Java\jdk-17`
     - `C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot`
     - `C:\Program Files\OpenJDK\jdk-17.0.2`

2. **Set JAVA_HOME Environment Variable:**
   - Press `Windows Key + R`
   - Type `sysdm.cpl` and press Enter
   - Click "Environment Variables" button at the bottom
   - In "System Variables" section, click "New"
   - Variable name: `JAVA_HOME`
   - Variable value: Your JDK path (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot`)
   - Click OK

3. **Add Java to PATH:**
   - In the same Environment Variables window
   - Find "Path" in System Variables and select it
   - Click "Edit"
   - Click "New"
   - Add: `%JAVA_HOME%\bin`
   - Click OK on all windows

4. **Verify Installation:**
   - Open a NEW Command Prompt or PowerShell window
   - Run: `java -version`
   - Run: `echo %JAVA_HOME%`
   - You should see Java 17 version info

### macOS Instructions:

1. **Find your JDK installation:**
   ```bash
   /usr/libexec/java_home -V
   ```

2. **Add to shell profile (.zshrc or .bash_profile):**
   ```bash
   echo 'export JAVA_HOME=$(/usr/libexec/java_home -v17)' >> ~/.zshrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
   source ~/.zshrc
   ```

3. **Verify:**
   ```bash
   java -version
   echo $JAVA_HOME
   ```

### Linux (Ubuntu/Debian) Instructions:

1. **Install OpenJDK 17 (if not already installed):**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```

2. **Set environment variables:**
   ```bash
   echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
   echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
   source ~/.bashrc
   ```

3. **Verify:**
   ```bash
   java -version
   echo $JAVA_HOME
   ```

## Important Notes:

- **RESTART YOUR COMPUTER** after setting environment variables on Windows
- **Open a new terminal/command prompt** to see the changes
- The exact JDK path may vary based on your installation method

## Troubleshooting:

If environment variables don't work:
1. Double-check the JDK installation path exists
2. Make sure you opened a new terminal after setting variables
3. On Windows, try using forward slashes: `C:/Program Files/Java/jdk-17`
4. The project is also configured with a direct JDK path fallback in gradle.properties

## Next Steps:

After setting up environment variables:
1. Restart your computer (Windows) or terminal (macOS/Linux)
2. Open Android Studio
3. Open the PhotoKik project's `android` folder
4. Let Gradle sync complete
5. Build the project

Your PhotoKik Android project is now configured for JDK 17!
