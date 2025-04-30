package io.github.bonigarcia.wdm;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class service {
    private final UDCFactory UDCFactory;

    @Autowired
    public service(UDCFactory UDCFactory) {
        this.UDCFactory = UDCFactory;
    }
    public String test1() {
        UndetectedChromeDriver UDCdriver = null;
        try {
            UDCdriver = UDCFactory.createDriver();
            System.out.println("WebDriver 생성 완료!");

            WebDriverWait wait = new WebDriverWait(UDCdriver, Duration.ofSeconds(60)); // 최대 20초 기다림
            Random random = new Random(); // 랜덤 딜레이용

            String keyword = "전등"; // 검색할 키워드

            try {
                // 1. 쿠팡 메인 페이지 접속
                String mainUrl = "https://www.coupang.com/";
                UDCdriver.get(mainUrl);
                log.info("쿠팡 메인 페이지 로딩 중: " + mainUrl);

                // 페이지 로딩 기다리기 (메인 페이지의 특정 요소가 나타날 때까지)
                By searchInputLocator = By.cssSelector("input[placeholder='찾고 싶은 상품을 검색해보세요!']"); // 이 셀렉터는 쿠팡 메인 페이지 보고 맞춰야 함!
                wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputLocator));
               log.info("쿠팡 메인 페이지 로딩 완료!");

                // 인간적인 행동 딜레이 추가
                try {
                    Thread.sleep(random.nextInt(2001) + 1000); // 1초에서 3초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 2. 검색어 입력
                WebElement searchInput = UDCdriver.findElement(searchInputLocator);
                searchInput.sendKeys(keyword); // 검색어 입력!
                log.info("검색어 입력 완료: " + keyword);

                try {
                    Thread.sleep(random.nextInt(2001) + 1000); // 1초에서 3초 사이 랜덤 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                By searchButtonLocator = By.id("headerSearchBtn");
                // WebDriverWait으로 요소가 나타날 때까지 기다리기 (ID로 찾아도 기다리는 건 필수!)
                wait.until(ExpectedConditions.visibilityOfElementLocated(searchButtonLocator));
                log.info("검색 버튼 로딩 완료! (ID로 찾음)");
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
                    String htmlSource = UDCdriver.getPageSource();
//                    System.out.println("HTML Source:\n" + htmlSource);                    // 상품 아이템 로딩 대기 전 딜레이
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
                    log.info("data-products 값: {}", dataProductsValue);

                    // paginationDiv 로딩 대기 전 딜레이
                    try {
                        Thread.sleep(random.nextInt(1001) + 500); // 0.5초 ~ 1.5초 사이 랜덤 대기
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    By paginationDivLocator = By.cssSelector("div.search-pagination");
                    wait.until(ExpectedConditions.presenceOfElementLocated(paginationDivLocator));
                    wait.until(ExpectedConditions.visibilityOfElementLocated(paginationDivLocator));

                    List<String> AdproductIds = new ArrayList<>(); // 여기에 추출한 ID를 담을 리스트 생성
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
                                AdproductIds.add(id);
                            }
                        }
                    }

                    log.info("광고 상품 id 리스트: {}", AdproductIds);

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
                     page2Link.click();

                    log.info("{}페이지 링크 로딩 및 클릭 가능 상태 확인!", nextPage);
                }
                return "success";
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("크롤링 중 에러 발생", e);
            return "error";
        } finally {
            if (UDCdriver != null) {
                UDCdriver.quit();
            }
        }
        return "fail";
    }

}
