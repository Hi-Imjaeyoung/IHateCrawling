# 1. Base Image: Java 17
FROM openjdk:17-jdk-slim

# Define ChromeDriver version (update as needed!)
ARG CHROME_DRIVER_VERSION=136.0.7103.49

# 2. Install Base Dependencies (wget, gnupg, ca-certs, unzip)
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    gnupg \
    ca-certificates \
    unzip \
 && rm -rf /var/lib/apt/lists/*

# 3. Add Google Chrome Repository
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
 && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list

# 4. Install Google Chrome Stable AND additional libraries/fonts from the first script
RUN apt-get update && apt-get install -y --no-install-recommends \
    # Install Google Chrome
    google-chrome-stable \
    # Add Libraries explicitly (excluding chromium/driver)
    libnss3 libxss1 libatk1.0-0 libatk-bridge2.0-0 libcups2 libx11-xcb1 \
    libxcomposite1 libxdamage1 libxrandr2 libxshmfence1 libgbm1 libgtk-3-0 \
    # Add Fonts explicitly
    fonts-nanum \
    fonts-nanum-coding \
    fonts-unfonts-core \
    fonts-noto-cjk \
 # Clean up apt lists
 && rm -rf /var/lib/apt/lists/*

# 5. Download and Install specific ChromeDriver version manually
RUN wget -O /tmp/chromedriver.zip "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_DRIVER_VERSION}/linux64/chromedriver-linux64.zip" \
 && unzip /tmp/chromedriver.zip -d /tmp/ \
 && mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver \
 && chmod +x /usr/local/bin/chromedriver \
 && rm /tmp/chromedriver.zip \
 && rm -rf /tmp/chromedriver-linux64

# 6. Application Deployment Settings
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]