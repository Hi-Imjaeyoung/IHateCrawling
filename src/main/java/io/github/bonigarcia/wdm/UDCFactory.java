package io.github.bonigarcia.wdm;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Random;

@Component
@Slf4j
public class UDCFactory {

    public UndetectedChromeDriver createDriver() {
        try {
            log.info("드라이버 생성 시작 v 2.2");
            WebDriverManager.chromedriver().setup();
//            String driverPath = System.getProperty("webdriver.chrome.driver");
            ChromeOptions options = new ChromeOptions();
            // --- 필수 수정 및 권장 사항 적용 ---
            options.addArguments("--incognito"); // 한 번만 추가
            options.addArguments("--headless=new"); // "new" 헤드리스 모드 사용 (빌더의 .headless(true)는 제거)
            // options.addArguments("--no-sandbox"); // UDC 빌더의 .noSandbox(true)를 사용하므로 제거
            options.addArguments("--disable-dev-shm-usage"); // 더블 하이픈으로 수정, 컨테이너 환경에 권장
            options.addArguments("--window-size=1920,1080"); // 더블 하이픈으로 수정, 헤드리스 시 중요
            // 주석 처리된 옵션들은 필요 시 사용 고려
            // options.addArguments("--user-data-dir=/tmp/chrome_user_data_" + System.currentTimeMillis());
            // options.addArguments("--remote-debugging-port=" + (20000 + new Random().nextInt(10000)));
            // TODO: 실제 설치된 Chrome 버전에 맞춰 최신 User-Agent로 업데이트하세요.
            // 예시: Chrome 124 버전 기준 (실제로는 더 높을 수 있음)
            String latestUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
            return UndetectedChromeDriver.builder()
//                    .driverExecutable(new File(driverPath))
                    .options(options)                 // 수정된 옵션 적용
                    .noSandbox(true)                  // UDC에게 샌드박스 처리 위임 (이 옵션 권장)
                    .userAgent(latestUserAgent)       // 최신 버전으로 업데이트된 User-Agent 적용
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
