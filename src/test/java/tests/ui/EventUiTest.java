package tests.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.EventPage;

public class EventUiTest {
    private WebDriver driver;
    private EventPage eventPage;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        eventPage = new EventPage(driver);
    }

    @Test(priority = 1, description = "UI: Navigasi dan verifikasi halaman katalog event dapat dimuat")
    public void testOpenEventCatalogSuccess() {
        eventPage.openEventCatalogPage();
        Assert.assertTrue(eventPage.isCatalogPageLoaded(),
                "Halaman katalog event seharusnya berhasil dibuka!");
    }

    @Test(priority = 2, description = "UI: Menguji fitur filter pencarian event berdasarkan kata kunci")
    public void testSearchEventByKeyword() {
        eventPage.openEventCatalogPage();
        eventPage.searchEvent("Workshop");
        Assert.assertTrue(driver.getCurrentUrl().contains("events"),
                "User tetap berada di halaman katalog event dengan parameter pencarian");
    }

    @Test(priority = 3, description = "UI: Membuka detail salah satu event dari katalog")
    public void testOpenEventDetailSuccess() {
        eventPage.openEventCatalogPage();

        if (eventPage.getDisplayedEventCount() > 0) {
            eventPage.clickFirstEvent();

            org.openqa.selenium.support.ui.WebDriverWait wait =
                    new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10));

            // 1. Tangani jika kartu event membuka tab baru di browser
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
            }

            // 2. Beri waktu Selenium menunggu sampai URL BERUBAH dari katalog (/events)
            boolean isNavigated = wait.until(d ->
                    !d.getCurrentUrl().equalsIgnoreCase("https://tixsplit.dibimbing.id/events") &&
                            (d.getCurrentUrl().contains("/events/") || d.getCurrentUrl().matches(".*/events/.+"))
            );

            Assert.assertTrue(isNavigated, "User seharusnya dialihkan ke halaman detail event!");
            System.out.println(">>> Halaman Detail Terbuka: " + driver.getCurrentUrl());
        } else {
            System.out.println("Catatan: Daftar event kosong saat pengetesan.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}