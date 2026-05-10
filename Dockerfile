# 1. 使用與 GitHub Actions 相同的 JDK 版本 (Temurin JDK 21)
FROM eclipse-temurin:21-jdk-jammy

# 2. 設定環境變數
ENV ANDROID_HOME /opt/android-sdk
ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# 3. 安裝必要系統套件
RUN apt-get update && apt-get install -y wget unzip git && rm -rf /var/lib/apt/lists/*

# 4. 下載並安裝 Android SDK Command-line tools
# 參考版本: 11076708 (與目前的官方穩定版一致)
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O sdk.zip && \
    unzip sdk.zip -d ${ANDROID_HOME}/cmdline-tools && \
    mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm sdk.zip

# 5. 同意 SDK 授權並安裝編譯必備組件 (例如 API 34, Build-tools 34.0.0)
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" \
               "platforms;android-35" \
               "build-tools;35.0.0"

# 6. 設定工作目錄
WORKDIR /app