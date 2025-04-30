/*
 * (C) Copyright 2021 Boni Garcia (https://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.wdm.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.bonigarcia.wdm.SeleniumStealthOptions;
import io.github.bonigarcia.wdm.UndetectedChromeDriver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.github.bonigarcia.wdm.SpringBootDemoApp;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test using a local web application based on spring-boot.
 *
 * @author Boni Garcia (boni.gg@gmail.com)
 * @since 1.0.0
 */

//@SpringBootTest(classes = SpringBootDemoApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringChromeTest {

    UndetectedChromeDriver UDCdriver;

    // @BeforeEach에서 드라이버 객체 생성
    @BeforeEach
    void setupTest() {
        try {
            ChromeOptions options = new ChromeOptions();
            // --- 필수 수정 및 권장 사항 적용 ---
//            options.addArguments("--incognito"); // 한 번만 추가
//            options.addArguments("--headless=new"); // "new" 헤드리스 모드 사용 (빌더의 .headless(true)는 제거)
            // options.addArguments("--no-sandbox"); // UDC 빌더의 .noSandbox(true)를 사용하므로 제거
//            options.addArguments("--disable-dev-shm-usage"); // 더블 하이픈으로 수정, 컨테이너 환경에 권장
//            options.addArguments("--window-size=1920,1080"); // 더블 하이픈으로 수정, 헤드리스 시 중요
            // 주석 처리된 옵션들은 필요 시 사용 고려
            // options.addArguments("--user-data-dir=/tmp/chrome_user_data_" + System.currentTimeMillis());
            // options.addArguments("--remote-debugging-port=" + (20000 + new Random().nextInt(10000)));

            // TODO: 실제 설치된 Chrome 버전에 맞춰 최신 User-Agent로 업데이트하세요.
            // 예시: Chrome 124 버전 기준 (실제로는 더 높을 수 있음)
            String latestUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36";
            UDCdriver = UndetectedChromeDriver.builder()
//                    .options(options)                 // 수정된 옵션 적용
//                    .noSandbox(true)                  // UDC에게 샌드박스 처리 위임 (이 옵션 권장)
//                     .headless(true)                // options에서 --headless=new를 사용하므로 제거
//                    .userAgent(latestUserAgent)       // 최신 버전으로 업데이트된 User-Agent 적용
                    // .driverExecutable(new File("/usr/local/bin/chromedriver")) // 필요 시 경로 지정
                    .build();
            System.out.println("UndetectedChromeDriver 객체 생성 및 navigator.webdriver 속성 변경 성공!");
            System.out.println("ChromeDriver 객체 생성 성공!");
        } catch (Exception e) {
            System.err.println("🚫 ChromeDriver 객체 생성 중 오류 발생!");
            e.printStackTrace();
            UDCdriver = null;
        }
    }

    // @AfterEach에서 드라이버 종료
//    @AfterEach
//    void teardown() {
//        if (UDCdriver != null) {
//            UDCdriver.quit();
//            System.out.println("WebDriver 종료 완료!");
//        } else {
//            System.out.println("WebDriver가 null이어서 종료 스킵.");
//        }
//    }

    @Test
    @DisplayName("쿠팡 검색 및 결과 크롤링 테스트")
    void coupangSearchAndCrawlTest() {
        if (UDCdriver == null) {
            System.out.println("@BeforeEach에서 드라이버 생성 실패하여 테스트 스킵.");
            return;
        }

        WebDriverWait wait = new WebDriverWait(UDCdriver, Duration.ofSeconds(60)); // 최대 20초 기다림
        Random random = new Random(); // 랜덤 딜레이용

        String keyword = "칼갈이"; // 검색할 키워드

        try {
            // 1. 쿠팡 메인 페이지 접속
            String mainUrl = "https://www.coupang.com/";
            UDCdriver.get(mainUrl);
            System.out.println("쿠팡 메인 페이지 로딩 중: " + mainUrl);

            // 페이지 로딩 기다리기 (메인 페이지의 특정 요소가 나타날 때까지)
            // 예: 검색창이 나타날 때까지 기다림 (개발자 도구로 검색창 요소의 CSS Selector 찾아야 함!)
            By searchInputLocator = By.cssSelector("input[placeholder='찾고 싶은 상품을 검색해보세요!']"); // 이 셀렉터는 쿠팡 메인 페이지 보고 맞춰야 함!

            wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputLocator));
            System.out.println("쿠팡 메인 페이지 로딩 완료!");

            // 인간적인 행동 딜레이 추가
            try {
                Thread.sleep(random.nextInt(2001) + 1000); // 1초에서 3초 사이 랜덤 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }


            // 2. 검색어 입력
            WebElement searchInput = UDCdriver.findElement(searchInputLocator);
            searchInput.sendKeys(keyword); // 검색어 입력!
            System.out.println("검색어 입력 완료: " + keyword);

            try {
                Thread.sleep(random.nextInt(2001) + 1000); // 1초에서 3초 사이 랜덤 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }


            By searchButtonLocator = By.id("headerSearchBtn");

            // WebDriverWait으로 요소가 나타날 때까지 기다리기 (ID로 찾아도 기다리는 건 필수!)
            wait.until(ExpectedConditions.visibilityOfElementLocated(searchButtonLocator));
            System.out.println("검색 버튼 로딩 완료! (ID로 찾음)");

            // 요소 찾기
            WebElement searchButton = UDCdriver.findElement(searchButtonLocator);


            // 검색 버튼 클릭 전 잠시 대기
            try {
                Thread.sleep(random.nextInt(501) + 500); // 0.5초에서 1초 사이 랜덤 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            searchButton.click(); // 검색 버튼 클릭!
            System.out.println("검색 버튼 클릭 완료!");
            int nextPage = 1;
            int maxPage = 4;
            while(nextPage < maxPage){
                // 다음 페이지 번호 증가
                nextPage++;

                // 상품 아이템 로딩 대기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(2001) + 1000); // 1초 ~ 3초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 상품 아이템이 하나 이상 로딩될 때까지 명시적으로 기다림
                By productItemLocator = By.cssSelector("li.search-product.search-product__ad-badge");
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productItemLocator));

                // productList 로딩 대기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(1001) + 500); // 0.5초 ~ 1.5초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // ID가 "productList"인 ul 요소가 DOM에 나타날 때까지 기다리기
                By productListLocator = By.id("productList");
                WebElement productListElement = wait.until(ExpectedConditions.presenceOfElementLocated(productListLocator));

                // data-products 값 가져오기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(501) + 300); // 0.3초 ~ 0.8초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 요소가 나타날 때까지 기다린 후, 해당 요소에서 "data-products" 속성 값 가져오기
                String dataProductsValue = productListElement.getAttribute("data-products");
                // 가져온 값 출력
                System.out.println("data-products 값: " + dataProductsValue);

                // paginationDiv 로딩 대기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(1001) + 500); // 0.5초 ~ 1.5초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                By paginationDivLocator = By.cssSelector("div.search-pagination");
                wait.until(ExpectedConditions.presenceOfElementLocated(paginationDivLocator));
                wait.until(ExpectedConditions.visibilityOfElementLocated(paginationDivLocator));

                List<String> productIds = new ArrayList<>(); // 여기에 추출한 ID를 담을 리스트 생성
                // CSS Selector를 사용해서 "search-product"와 "search-product__ad-badge" 클래스를 모두 가진 li 요소를 찾기 위한 로케이터
                By liElementsLocator = By.cssSelector("li.search-product.search-product__ad-badge");

                // 상품 리스트 요소 로딩 대기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(1501) + 700); // 0.7초 ~ 2.2초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 해당 요소들이 하나 이상 나타날 때까지 기다리기
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(liElementsLocator));

                // 찾은 모든 li 요소들을 List 형태로 가져오기
                List<WebElement> productLiElements = UDCdriver.findElements(liElementsLocator);

                // 상품 ID 추출 전 딜레이
                try {
                    Thread.sleep(random.nextInt(801) + 400); // 0.4초 ~ 1.2초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 가져온 List를 순회하면서 각 요소의 id 속성 값을 추출하여 productIds 리스트에 담기
                if (productLiElements.isEmpty()) {
                    System.out.println("조건에 맞는 li 요소를 찾지 못했습니다.");
                } else {
                    System.out.println("찾은 li 요소 개수: " + productLiElements.size());
                    for (WebElement liElement : productLiElements) {
                        String id = liElement.getAttribute("id"); // 'id' 속성 값 가져오기
                        if (id != null && !id.isEmpty()) { // id 값이 null이거나 비어있지 않은 경우에만 추가
                            productIds.add(id);
                        }
                    }
                }

                System.out.println(productIds);

                // 페이지 링크 로딩 대기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(1201) + 600); // 0.6초 ~ 1.8초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // XPath를 사용해서 다음 페이지 링크 찾기
                By page2LinkLocator = By.xpath("//div[@class='search-pagination']//a[text()='"+nextPage+"']");

                // 페이지 링크 클릭 가능성 대기 전 딜레이
                try {
                    Thread.sleep(random.nextInt(601) + 300); // 0.3초 ~ 0.9초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 다음 페이지 링크 요소가 클릭 가능할 때까지 기다리기
                WebElement page2Link = wait.until(ExpectedConditions.elementToBeClickable(page2LinkLocator));

                // 페이지 링크 클릭! (일단 주석 처리)
                // page2Link.click();

                System.out.println(nextPage+"페이지 링크 로딩 및 클릭 가능 상태 확인!");
            }

            // 결과 페이지 로딩 후 잠시 대기
            try {
                Thread.sleep(random.nextInt(2001) + 1000); // 1초에서 3초 사이 랜덤 대기
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }


        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 출력
        }
        // 드라이버 종료는 @AfterEach에서 자동으로 처리됨
    }
}
