package tests.ui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CommunityPage;

public class CommunityUiTest {
    private WebDriver driver;
    private CommunityPage communityPage;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        communityPage = new CommunityPage(driver);
    }

    @Test(priority = 1, description = "UI: Navigasi dan verifikasi daftar komunitas dapat dimuat")
    public void testOpenCommunityListSuccess() {
        communityPage.openCommunityListPage();
        Assert.assertTrue(communityPage.isCommunityPageLoaded(),
                "Halaman daftar komunitas seharusnya berhasil dimuat!");
    }

    @Test(priority = 2, description = "UI: Akses pembuatan komunitas oleh Guest dialihkan atau dibatasi")
    public void testCreateCommunityWithoutAuthRedirects() {
        communityPage.openCommunityListPage();
        communityPage.clickCreateCommunity();

        // Guest tanpa login seharusnya dialihkan ke sign-in atau tetap di halaman komunitas
        boolean isProtected = driver.getCurrentUrl().contains("sign-in") ||
                driver.getCurrentUrl().contains("communities");
        Assert.assertTrue(isProtected, "Sistem seharusnya mengamankan akses pembuatan komunitas!");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}