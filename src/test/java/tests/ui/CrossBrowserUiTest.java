package tests.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CrossBrowserUiTest {

    @Test(priority = 1, description = "Cross-Browser: Verifikasi akses katalog event di Google Chrome")
    public void testEventCatalogOnChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get("https://tixsplit.dibimbing.id/events");
            Assert.assertTrue(driver.getCurrentUrl().contains("events"),
                    "Katalog event seharusnya dapat dimuat di Google Chrome!");
            System.out.println(">>> [CHROME] Pengujian Sukses di Google Chrome!");
        } finally {
            driver.quit();
        }
    }

    @Test(priority = 2, description = "Cross-Browser: Verifikasi akses katalog event di Microsoft Edge")
    public void testEventCatalogOnEdge() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");

        WebDriver driver = new EdgeDriver(options);
        try {
            driver.get("https://tixsplit.dibimbing.id/events");
            Assert.assertTrue(driver.getCurrentUrl().contains("events"),
                    "Katalog event seharusnya dapat dimuat di Microsoft Edge!");
            System.out.println(">>> [EDGE] Pengujian Sukses di Microsoft Edge!");
        } finally {
            driver.quit();
        }
    }
}