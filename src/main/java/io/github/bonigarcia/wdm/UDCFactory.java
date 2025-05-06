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
            WebDriverManager.chromedriver().setup(); // <<<--- 제거!
            log.info("드라이버 생성 시작 v 2.2");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--incognito");
            options.addArguments("--headless=new"); // <<<--- 복구 (주석 해제)
// options.addArguments("--no-sandbox"); // UDC 빌더 옵션을 사용하므로 주석 처리 유지
            options.addArguments("--disable-dev-shm-usage"); // <<<--- 복구 (주석 해제 및 '--' 확인)
            options.addArguments("--disable-gpu"); // <<<--- 추가 권장
            options.addArguments("--window-size=1920,1080"); // <<<--- 복구 (주석 해제 및 '--' 확인), 헤드리스에 필요

// TODO: 실제 설치된 Chrome 버전에 맞춰 최신 User-Agent로 업데이트
            String latestUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36"; // 예시 버전

            return UndetectedChromeDriver.builder()
                    .options(options)
                    .noSandbox(true) // <<<--- 복구 (주석 해제)
                    .userAgent(latestUserAgent) // <<<--- 복구 (주석 해제)
                    // .driverExecutable(new File("/usr/local/bin/chromedriver")) // Dockerfile에서 설치한 드라이버 명시적 지정 (선택 사항)
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
