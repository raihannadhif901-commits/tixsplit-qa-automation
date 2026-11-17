package tests.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.SplitBillPage;

public class SplitBillUiTest {
    private WebDriver driver;
    private SplitBillPage splitBillPage;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        splitBillPage = new SplitBillPage(driver);
    }

    @Test(description = "UI: Navigasi dan verifikasi form pembuatan Split Bill dapat diakses")
    public void testOpenSplitBillPageSuccess() {
        splitBillPage.openSplitBillPage();
        Assert.assertTrue(splitBillPage.isPageLoaded(),
                "Halaman tools Split Bill seharusnya berhasil dimuat!");
    }

    @Test(description = "UI: Input data dasar tagihan Split Bill")
    public void testCreateSplitBillSuccess() {
        splitBillPage.openSplitBillPage();
        splitBillPage.fillBillDetails("Makan Bareng Final Project", "100000");
        Assert.assertTrue(driver.getCurrentUrl().contains("split-bill"),
                "User tetap berada di alur proses Split Bill");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}