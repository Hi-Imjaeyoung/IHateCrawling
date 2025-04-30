FROM openjdk:17-jdk-slim

# 필요한 패키지 업데이트 및 설치 (unzip 추가!) + 가상 X 서버(X Virtual FrameBuffer, xvfb)를 활용
RUN apt-get update && apt-get install -y wget gnupg ca-certificates unzip xvfb xfonts-base xfonts-100dpi xfonts-75dpi xfonts-cyrillic

# Google Chrome 저장소 키 추가
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -

# Google Chrome 저장소 추가
RUN sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list'

# Chrome 설치
RUN apt-get update && apt-get install -y google-chrome-stable

# ChromeDriver 다운로드 및 압축 해제
# 사용할 ChromeDriver 버전 명시 (현재 네 크롬 버전에 맞춰!)
ARG CHROME_DRIVER_VERSION=136.0.7103.49
RUN wget "https://storage.googleapis.com/chrome-for-testing-public/${CHROME_DRIVER_VERSION}/linux64/chromedriver-linux64.zip"
RUN unzip chromedriver-linux64.zip -d /tmp/
RUN mv /tmp/chromedriver-linux64/chromedriver /usr/local/bin/chromedriver
RUN rm chromedriver-linux64.zip
RUN rm -rf /tmp/chromedriver-linux64
RUN chmod +x /usr/local/bin/chromedriver # 실행 파일 이름은 보통 chromedriver
RUN ls -l /usr/local/bin/chromedriver # 정확한 파일 확인

WORKDIR /app
COPY target/*.jar app.jar

COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh
ENTRYPOINT ["/app/start.sh"]